//
//  EditTransactionView.swift
//  SpendCraftiOS
//
//  İşlem düzenleme ekranı
//

import SwiftUI

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
                        if filteredCategories.isEmpty {
                            Text("Bu tip için kategori bulunamadı")
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
                        saveChanges()
                    } label: {
                        Text("Değişiklikleri Kaydet")
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
            .navigationTitle("İşlemi Düzenle")
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
        amount = String(format: "%.2f", transaction.amount)
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
        } catch {
            print("Error saving transaction: \(error)")
        }
        
        dismiss()
    }
}

