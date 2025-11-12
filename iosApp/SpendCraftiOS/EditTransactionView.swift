//
//  EditTransactionView.swift
//  SpendCraftiOS
//
//  İşlem düzenleme ekranı
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

struct EditTransactionView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    
    let transaction: TransactionEntity
    
    @State private var amount: String = ""
    @State private var note: String = ""
    @State private var selectedCategory: CategoryEntity?
    @State private var selectedAccount: AccountEntity?
    @State private var isIncome: Bool = false
    @State private var date: Date = Date()
    @FocusState private var isAmountFocused: Bool
    
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
                        .onChange(of: isIncome) { _ in
                            // İşlem tipi değiştiğinde kategori seçimini sıfırla
                            selectedCategory = nil
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
                        if filteredCategories.isEmpty {
                            Text(NSLocalizedString("categories.no.categories", comment: "No categories yet"))
                                .foregroundColor(.secondary)
                        } else {
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
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
                    }
                    
                    // Account
                    Section(NSLocalizedString("section.account", comment: "Account")) {
                        Picker(NSLocalizedString("select.account", comment: "Select Account"), selection: $selectedAccount) {
                            Text(NSLocalizedString("recurring.select", comment: "Select")).tag(nil as AccountEntity?)
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
                        saveChanges()
                    } label: {
                        Text(NSLocalizedString("common.save", comment: "Save"))
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
            .navigationTitle(NSLocalizedString("edit.transaction.title", comment: "Edit Transaction"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                        dismiss()
                    }
                }
            }
        }
        .onAppear {
            loadTransactionData()
        }
    }
    
    var isValid: Bool {
        guard let amountValue = Double(amount), amountValue > 0 else { return false }
        guard selectedCategory != nil else { return false }
        guard selectedAccount != nil else { return false }
        return true
    }
    
    func loadTransactionData() {
        // Load current transaction data
        let amountValue = transaction.amount
        // Remove trailing zeros for cleaner display
        if amountValue.truncatingRemainder(dividingBy: 1) == 0 {
            amount = String(format: "%.0f", amountValue)
        } else {
            amount = String(format: "%.2f", amountValue).replacingOccurrences(of: ".00", with: "")
        }
        
        note = transaction.note ?? ""
        isIncome = transaction.isIncome
        date = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis / 1000))
        
        // Load categories and accounts
        transactionsViewModel.loadCategories()
        transactionsViewModel.loadAccounts()
        
        // Set selected category and account
        selectedCategory = transaction.category
        selectedAccount = transaction.account
    }
    
    func saveChanges() {
        guard let amountValue = Double(amount),
              let category = selectedCategory,
              let account = selectedAccount else { return }
        
        // Update all fields directly
        transaction.amountMinor = Int64(amountValue * 100)
        transaction.note = note.isEmpty ? nil : note
        transaction.categoryId = category.id
        transaction.accountId = account.id
        transaction.isIncome = isIncome
        transaction.category = category
        transaction.account = account
        transaction.timestampUtcMillis = Int64(date.timeIntervalSince1970 * 1000)
        
        // Save to CoreData and wait for it to complete
        let context = CoreDataStack.shared.container.viewContext
        do {
            try context.save()
            // Refresh this specific object to get the latest state
            context.refresh(transaction, mergeChanges: false)
            
            print("✅ Transaction saved successfully: \(transaction.id)")
            
            // Post notification to update dashboard
            NotificationCenter.default.post(name: NSNotification.Name("TransactionUpdated"), object: nil)
        } catch {
            print("Error saving transaction: \(error)")
        }
        
        dismiss()
    }
}

