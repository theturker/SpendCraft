//
//  AddTransactionView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI

// MARK: - Currency TextField Helper
fileprivate struct CurrencyTextField: View {
    let title: String
    @Binding var value: String
    @FocusState.Binding var isFocused: Bool
    
    @State private var displayValue: String = ""
    
    var body: some View {
        TextField(title, text: $displayValue)
            .keyboardType(.decimalPad)
            .font(.title2)
            .focused($isFocused)
            .onChange(of: displayValue) { newValue in
                formatInput(newValue)
            }
            .onAppear {
                if !value.isEmpty {
                    displayValue = formatNumber(value)
                }
            }
    }
    
    private func formatInput(_ input: String) {
        let cleaned = input.replacingOccurrences(of: "[^0-9,.]", with: "", options: .regularExpression)
        let currency = getCurrentCurrencyCode()
        let decimalSep = currency == "TRY" ? "," : "."
        let parts = cleaned.split(separator: Character(decimalSep), maxSplits: 1)
        
        var integerPart = String(parts.first ?? "")
        var decimalPart = parts.count > 1 ? String(parts[1]) : ""
        
        integerPart = integerPart.replacingOccurrences(of: ".", with: "")
        integerPart = integerPart.replacingOccurrences(of: ",", with: "")
        
        if decimalPart.count > 2 {
            decimalPart = String(decimalPart.prefix(2))
        }
        
        if decimalPart.isEmpty {
            value = integerPart
        } else {
            value = "\(integerPart).\(decimalPart)"
        }
        
        if !integerPart.isEmpty {
            displayValue = formatNumber(value)
        } else {
            displayValue = ""
        }
    }
    
    private func formatNumber(_ number: String) -> String {
        guard let doubleValue = Double(number) else { return number }
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        
        let currency = getCurrentCurrencyCode()
        if currency == "TRY" {
            formatter.locale = Locale(identifier: "tr_TR")
            formatter.groupingSeparator = "."
            formatter.decimalSeparator = ","
        } else {
            formatter.locale = Locale(identifier: "en_US")
            formatter.groupingSeparator = ","
            formatter.decimalSeparator = "."
        }
        
        return formatter.string(from: NSNumber(value: doubleValue)) ?? number
    }
}

struct AddTransactionView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    @EnvironmentObject var notificationsViewModel: NotificationsViewModel
    @EnvironmentObject var recurringViewModel: RecurringViewModel
    
    let initialIsIncome: Bool
    
    @State private var amount: String = ""
    @State private var note: String = ""
    @State private var selectedCategory: CategoryEntity?
    @State private var selectedAccount: AccountEntity?
    @State private var isIncome: Bool
    @State private var date: Date = Date()
    @State private var showAddCategory = false
    @State private var isRecurring: Bool = false
    @State private var recurringFrequency: String = "MONTHLY"
    @FocusState private var isAmountFocused: Bool
    
    init(initialIsIncome: Bool) {
        self.initialIsIncome = initialIsIncome
        // Set initial state immediately in init, don't wait for onAppear
        _isIncome = State(initialValue: initialIsIncome)
    }
    
    var filteredCategories: [CategoryEntity] {
        return transactionsViewModel.categoriesForType(isIncome)
    }
    
    var body: some View {
        NavigationView {
            ZStack(alignment: .bottom) {
                Form {
                    // Transaction Type
                    Section {
                        Picker(NSLocalizedString("add.transaction.type", comment: "Transaction Type"), selection: $isIncome) {
                            Text(NSLocalizedString("add.transaction.expense", comment: "Expense")).tag(false)
                            Text(NSLocalizedString("add.transaction.income", comment: "Income")).tag(true)
                        }
                        .pickerStyle(.segmented)
                        .onChange(of: isIncome) { newValue in
                            print("🔄 Transaction type changed to: \(newValue ? "income" : "expense")")
                            // İşlem tipi değiştiğinde kategori seçimini sıfırla
                            selectedCategory = nil
                            // Reload categories
                            transactionsViewModel.loadCategories()
                        }
                    }
                    
                    // Amount
                    Section(NSLocalizedString("section.amount", comment: "Amount")) {
                        HStack {
                            CurrencyTextField(title: "0.00", value: $amount, isFocused: $isAmountFocused)
                            Text(getCurrentCurrencySymbol())
                                .font(.title2)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    // Category
                    Section(NSLocalizedString("section.category", comment: "Category")) {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 12) {
                                // New Category Button
                                Button {
                                    showAddCategory = true
                                } label: {
                                    VStack(spacing: 8) {
                                        Image(systemName: "plus.circle.fill")
                                            .font(.title2)
                                            .foregroundColor(.blue)
                                            .frame(width: 50, height: 50)
                                            .background(
                                                Circle()
                                                    .fill(Color.blue.opacity(0.2))
                                            )
                                        
                                        Text(NSLocalizedString("new.category", comment: "New"))
                                            .font(.caption)
                                            .foregroundColor(.blue)
                                            .fontWeight(.medium)
                                    }
                                    .frame(width: 80)
                                }
                                
                                // Existing Categories - Filtered by type
                                ForEach(filteredCategories, id: \.id) { category in
                                    CategoryButton(
                                        category: category,
                                        isSelected: selectedCategory?.id == category.id
                                    ) {
                                        selectedCategory = category
                                    }
                                }
                            }
                            .padding(.vertical, 8)
                        }
                    }
                    
                    // Account
                    Section(NSLocalizedString("section.account", comment: "Account")) {
                        Picker(NSLocalizedString("select.account", comment: "Select Account"), selection: $selectedAccount) {
                            Text(NSLocalizedString("select.category", comment: "Select")).tag(nil as CategoryEntity?)
                            ForEach(transactionsViewModel.accounts, id: \.id) { account in
                                Text(account.name ?? "").tag(account as AccountEntity?)
                            }
                        }
                    }
                    
                    // Date
                    Section(NSLocalizedString("section.date", comment: "Date")) {
                        DatePicker(NSLocalizedString("section.date", comment: "Date"), selection: $date, displayedComponents: [.date, .hourAndMinute])
                    }
                    
                    // Note
                    Section(NSLocalizedString("section.note", comment: "Note")) {
                        TextField(NSLocalizedString("optional.note", comment: "Optional note"), text: $note)
                    }
                    
                    // Recurring Transaction
                    Section {
                        Toggle(NSLocalizedString("section.recurring", comment: "Recurring Transaction"), isOn: $isRecurring)
                        
                        if isRecurring {
                            Picker(NSLocalizedString("section.recurring.frequency", comment: "Recurrence Frequency"), selection: $recurringFrequency) {
                                Text(NSLocalizedString("frequency.daily", comment: "Daily")).tag("DAILY")
                                Text(NSLocalizedString("frequency.weekly", comment: "Weekly")).tag("WEEKLY")
                                Text(NSLocalizedString("frequency.monthly", comment: "Monthly")).tag("MONTHLY")
                                Text(NSLocalizedString("frequency.yearly", comment: "Yearly")).tag("YEARLY")
                            }
                        }
                    } header: {
                        Text(NSLocalizedString("section.recurring.settings", comment: "Recurrence Settings"))
                    } footer: {
                        if isRecurring {
                            Text(NSLocalizedString("recurring.info", comment: "Recurring info"))
                                .font(.caption)
                        }
                    }
                    
                    // Spacer for floating button
                    Section {
                        Color.clear
                            .frame(height: 60)
                            .listRowBackground(Color.clear)
                    }
                }
                
                // Floating Save Button
                VStack(spacing: 0) {
                    Divider()
                    
                    Button {
                        saveTransaction()
                    } label: {
                        Text(NSLocalizedString("save.transaction", comment: "Save"))
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(isValid ? Color.blue : Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                    .disabled(!isValid)
                    .padding(.horizontal)
                    .padding(.vertical, 12)
                    .background(Color(uiColor: .systemBackground))
                }
            }
            .navigationTitle(NSLocalizedString("add.transaction.title", comment: "New Transaction"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                        dismiss()
                    }
                }
            }
        }
        .sheet(isPresented: $showAddCategory, onDismiss: {
            // Reload categories after adding new one
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                print("🔄 Reloading categories after adding new one...")
                transactionsViewModel.loadCategories()
                // Force refresh the filtered categories
                transactionsViewModel.objectWillChange.send()
                print("🔄 Categories reloaded. Available for \(isIncome ? "income" : "expense"): \(transactionsViewModel.categoriesForType(isIncome).count)")
            }
        }) {
            AddCategoryView(initialType: isIncome ? "income" : "expense")
                .environmentObject(transactionsViewModel)
        }
        .onAppear {
            print("\n🔵 ============ AddTransactionView APPEARED ============")
            print("🔵 isIncome: \(isIncome)")
            print("🔵 Looking for type: \(isIncome ? "income" : "expense")")
            
            // Reload data to get fresh categories and accounts
            transactionsViewModel.loadCategories()
            transactionsViewModel.loadAccounts()
            
            // Set default account
            if let defaultAccount = transactionsViewModel.accounts.first(where: { $0.isDefault }) {
                selectedAccount = defaultAccount
            } else if let firstAccount = transactionsViewModel.accounts.first {
                selectedAccount = firstAccount
            }
            
            // Auto-focus on amount field with slight delay
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                isAmountFocused = true
            }
            
            print("🔵 Total categories loaded: \(transactionsViewModel.categories.count)")
            print("🔵 Filtered categories for \(isIncome ? "income" : "expense"): \(filteredCategories.count)")
            print("🔵 Filtered category names:")
            for cat in filteredCategories {
                print("   - \(cat.name ?? "?")")
            }
            print("🔵 ===============================================\n")
        }
    }
    
    var isValid: Bool {
        guard let amountValue = Double(amount), amountValue > 0 else { return false }
        guard selectedCategory != nil else { return false }
        guard selectedAccount != nil else { return false }
        return true
    }
    
    func saveTransaction() {
        guard let amountValue = Double(amount),
              let category = selectedCategory,
              let account = selectedAccount else { return }
        
        // İşlemi kaydet
        transactionsViewModel.addTransaction(
            amount: amountValue,
            note: note.isEmpty ? nil : note,
            category: category,
            account: account,
            isIncome: isIncome,
            achievementsViewModel: achievementsViewModel,
            notificationsViewModel: notificationsViewModel
        )
        
        // Eğer tekrarlayan işlem ise, recurring transaction olarak da kaydet
        if isRecurring {
            let transactionName = category.name ?? "Tekrarlayan İşlem"
            recurringViewModel.addRecurringTransaction(
                name: transactionName,
                amount: amountValue,
                categoryId: category.id,
                accountId: account.id,
                isIncome: isIncome,
                frequency: recurringFrequency,
                startDate: date,
                endDate: nil,
                note: note.isEmpty ? nil : note
            )
        }
        
        // Reload achievements to update UI
        achievementsViewModel.loadAchievements()
        
        // Update achievements
        achievementsViewModel.checkAchievements(
            transactionCount: transactionsViewModel.transactions.count,
            totalSpent: transactionsViewModel.totalExpense,
            categories: transactionsViewModel.categories.count,
            notificationsViewModel: notificationsViewModel
        )
        
        // Force UI refresh
        achievementsViewModel.objectWillChange.send()
        
        dismiss()
    }
}

struct CategoryButton: View {
    let category: CategoryEntity
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Image(systemName: category.icon ?? "circle.fill")
                    .font(.title2)
                    .foregroundColor(isSelected ? .white : category.uiColor)
                    .frame(width: 50, height: 50)
                    .background(
                        Circle()
                            .fill(isSelected ? category.uiColor : category.uiColor.opacity(0.2))
                    )
                
                Text(category.name ?? "")
                    .font(.caption)
                    .fontWeight(isSelected ? .semibold : .regular)
                    .foregroundColor(isSelected ? .primary : .secondary)
                    .lineLimit(1)
            }
            .frame(width: 80)
        }
    }
}
