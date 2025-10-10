//
//  AddTransactionView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI

struct AddTransactionView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    
    let initialIsIncome: Bool?
    
    @State private var amount: String = ""
    @State private var note: String = ""
    @State private var selectedCategory: CategoryEntity?
    @State private var selectedAccount: AccountEntity?
    @State private var isIncome: Bool = false
    @State private var date: Date = Date()
    
    init(initialIsIncome: Bool? = nil) {
        self.initialIsIncome = initialIsIncome
    }
    
    var body: some View {
        NavigationView {
            Form {
                // Transaction Type
                Section {
                    Picker("İşlem Tipi", selection: $isIncome) {
                        Text("Gider").tag(false)
                        Text("Gelir").tag(true)
                    }
                    .pickerStyle(.segmented)
                }
                
                // Amount
                Section("Miktar") {
                    HStack {
                        TextField("0.00", text: $amount)
                            .keyboardType(.decimalPad)
                            .font(.title2)
                            .font(.subheadline)
                        Text("₺")
                            .font(.title2)
                            .foregroundColor(.secondary)
                    }
                }
                
                // Category
                Section("Kategori") {
                    if transactionsViewModel.categories.isEmpty {
                        Text("Kategori bulunamadı")
                            .foregroundColor(.secondary)
                    } else {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 12) {
                                ForEach(transactionsViewModel.categories, id: \.id) { category in
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
                Section("Hesap") {
                    Picker("Hesap Seç", selection: $selectedAccount) {
                        Text("Seçiniz").tag(nil as CategoryEntity?)
                        ForEach(transactionsViewModel.accounts, id: \.id) { account in
                            Text(account.name ?? "").tag(account as AccountEntity?)
                        }
                    }
                }
                
                // Date
                Section("Tarih") {
                    DatePicker("Tarih", selection: $date, displayedComponents: [.date, .hourAndMinute])
                }
                
                // Note
                Section("Not") {
                    TextField("İsteğe bağlı not", text: $note)
                }
                
                // Save Button
                Section {
                    Button {
                        saveTransaction()
                    } label: {
                        Text("Kaydet")
                            .frame(maxWidth: .infinity)
                            .font(.subheadline)
                    }
                    .disabled(!isValid)
                }
            }
            .navigationTitle("Yeni İşlem")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
                        dismiss()
                    }
                }
            }
        }
        .onAppear {
            // Reload data to get fresh categories and accounts
            transactionsViewModel.loadCategories()
            transactionsViewModel.loadAccounts()
            
            // Set initial transaction type
            if let initialType = initialIsIncome {
                isIncome = initialType
            }
            
            // Set default account
            if let defaultAccount = transactionsViewModel.accounts.first(where: { $0.isDefault }) {
                selectedAccount = defaultAccount
            } else if let firstAccount = transactionsViewModel.accounts.first {
                selectedAccount = firstAccount
            }
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
        
        transactionsViewModel.addTransaction(
            amount: amountValue,
            note: note.isEmpty ? nil : note,
            category: category,
            account: account,
            isIncome: isIncome
        )
        
        // Update achievements
        achievementsViewModel.checkAchievements(
            transactionCount: transactionsViewModel.transactions.count,
            totalSpent: transactionsViewModel.totalExpense,
            categories: transactionsViewModel.categories.count
        )
        
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
