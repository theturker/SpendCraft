//
//  TransactionsViewModel.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData
import SwiftUI

class TransactionsViewModel: ObservableObject {
    @Published var transactions: [TransactionEntity] = []
    @Published var categories: [CategoryEntity] = []
    @Published var accounts: [AccountEntity] = []
    
    private let context = CoreDataStack.shared.container.viewContext
    
    // Computed properties
    var totalIncome: Double {
        transactions.filter { $0.isIncome }.reduce(0) { $0 + ($1.amount) }
    }
    
    var totalExpense: Double {
        transactions.filter { !$0.isIncome }.reduce(0) { $0 + ($1.amount) }
    }
    
    var currentBalance: Double {
        totalIncome - totalExpense
    }
    
    func loadData() {
        loadTransactions()
        loadCategories()
        loadAccounts()
    }
    
    func loadTransactions() {
        let fetchRequest: NSFetchRequest<TransactionEntity> = TransactionEntity.fetchRequest() as! NSFetchRequest<TransactionEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \TransactionEntity.timestampUtcMillis, ascending: false)]
        
        do {
            transactions = try context.fetch(fetchRequest)
        } catch {
            print("Error fetching transactions: \(error)")
        }
    }
    
    func loadCategories() {
        let fetchRequest: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest() as! NSFetchRequest<CategoryEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \CategoryEntity.name, ascending: true)]
        
        do {
            categories = try context.fetch(fetchRequest)
        } catch {
            print("Error fetching categories: \(error)")
        }
    }
    
    func loadAccounts() {
        let fetchRequest: NSFetchRequest<AccountEntity> = AccountEntity.fetchRequest() as! NSFetchRequest<AccountEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \AccountEntity.name, ascending: true)]
        
        do {
            accounts = try context.fetch(fetchRequest)
        } catch {
            print("Error fetching accounts: \(error)")
        }
    }
    
    func addTransaction(amount: Double, note: String?, category: CategoryEntity?, account: AccountEntity?, isIncome: Bool) {
        let transaction = TransactionEntity(context: context)
        transaction.id = Int64.random(in: 1...1000000)
        transaction.amountMinor = Int64(amount * 100)
        transaction.timestampUtcMillis = Int64(Date().timeIntervalSince1970 * 1000)
        transaction.note = note
        transaction.categoryId = category?.id ?? 0
        transaction.accountId = account?.id ?? 0
        transaction.isIncome = isIncome
        transaction.category = category
        transaction.account = account
        
        CoreDataStack.shared.saveContext()
        loadTransactions()
    }
    
    func deleteTransaction(_ transaction: TransactionEntity) {
        context.delete(transaction)
        CoreDataStack.shared.saveContext()
        loadTransactions()
    }
    
    func updateTransaction(_ transaction: TransactionEntity, amount: Double, note: String?, category: CategoryEntity?, account: AccountEntity?, isIncome: Bool) {
        transaction.amountMinor = Int64(amount * 100)
        transaction.note = note
        transaction.categoryId = category?.id ?? 0
        transaction.accountId = account?.id ?? 0
        transaction.isIncome = isIncome
        transaction.category = category
        transaction.account = account
        
        CoreDataStack.shared.saveContext()
        loadTransactions()
    }
    
    func transactionsForCategory(_ category: CategoryEntity) -> [TransactionEntity] {
        return transactions.filter { $0.categoryId == category.id }
    }
    
    func totalSpentForCategory(_ category: CategoryEntity) -> Double {
        return transactionsForCategory(category)
            .filter { !$0.isIncome }
            .reduce(0) { $0 + $1.amount }
    }
}