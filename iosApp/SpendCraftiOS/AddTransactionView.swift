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
    @EnvironmentObject var notificationsViewModel: NotificationsViewModel
    
    let initialIsIncome: Bool
    
    @State private var amount: String = ""
    @State private var note: String = ""
    @State private var selectedCategory: CategoryEntity?
    @State private var selectedAccount: AccountEntity?
    @State private var isIncome: Bool
    @State private var date: Date = Date()
    @State private var showAddCategory = false
    
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
                        Picker("İşlem Tipi", selection: $isIncome) {
                            Text("Gider").tag(false)
                            Text("Gelir").tag(true)
                        }
                        .pickerStyle(.segmented)
                        .onChange(of: isIncome) { _ in
                            // İşlem tipi değiştiğinde kategori seçimini sıfırla
                            selectedCategory = nil
                        }
                    }
                    
                    // Amount
                    Section("Miktar") {
                        HStack {
                            TextField("0.00", text: $amount)
                                .keyboardType(.decimalPad)
                                .font(.title2)
                            Text(getCurrentCurrencySymbol())
                                .font(.title2)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    // Category
                    Section("Kategori") {
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
                                        
                                        Text("Yeni")
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
                        Text("Kaydet")
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
        .sheet(isPresented: $showAddCategory, onDismiss: {
            // Reload categories after adding new one
            transactionsViewModel.loadCategories()
        }) {
            AddCategoryView()
                .environmentObject(transactionsViewModel)
        }
        .onAppear {
            // Reload data to get fresh categories and accounts
            transactionsViewModel.loadCategories()
            transactionsViewModel.loadAccounts()
            
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
            isIncome: isIncome,
            achievementsViewModel: achievementsViewModel,
            notificationsViewModel: notificationsViewModel
        )
        
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
