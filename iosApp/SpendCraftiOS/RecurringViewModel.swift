//
//  RecurringViewModel.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData
import SwiftUI

class RecurringViewModel: ObservableObject {
    @Published var recurringTransactions: [RecurringTransactionEntity] = []
    
    private let context = CoreDataStack.shared.container.viewContext
    private let transactionsViewModel: TransactionsViewModel
    
    init(transactionsViewModel: TransactionsViewModel) {
        self.transactionsViewModel = transactionsViewModel
    }
    
    func loadRecurringTransactions() {
        let fetchRequest: NSFetchRequest<RecurringTransactionEntity> = RecurringTransactionEntity.fetchRequest() as! NSFetchRequest<RecurringTransactionEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \RecurringTransactionEntity.name, ascending: true)]
        
        do {
            recurringTransactions = try context.fetch(fetchRequest)
        } catch {
            print("Error fetching recurring transactions: \(error)")
        }
    }
    
    func addRecurringTransaction(name: String, amount: Double, categoryId: Int64, accountId: Int64, isIncome: Bool, frequency: String, startDate: Date, endDate: Date?, note: String?) {
        let recurring = RecurringTransactionEntity(context: context)
        recurring.id = Int64.random(in: 1...1000000)
        recurring.name = name
        recurring.amount = Int64(amount * 100)
        recurring.categoryId = categoryId
        recurring.accountId = accountId
        recurring.isIncome = isIncome
        recurring.frequency = frequency
        recurring.startDate = Int64(startDate.timeIntervalSince1970 * 1000)
        recurring.endDate = endDate != nil ? Int64(endDate!.timeIntervalSince1970 * 1000) : 0
        recurring.isActive = true
        recurring.lastExecuted = 0
        recurring.nextExecution = Int64(startDate.timeIntervalSince1970 * 1000)
        recurring.note = note
        
        CoreDataStack.shared.saveContext()
        loadRecurringTransactions()
    }
    
    func updateRecurringTransaction(_ recurring: RecurringTransactionEntity, name: String, amount: Double, categoryId: Int64, accountId: Int64, isIncome: Bool, frequency: String, startDate: Date, endDate: Date?, note: String?) {
        recurring.name = name
        recurring.amount = Int64(amount * 100)
        recurring.categoryId = categoryId
        recurring.accountId = accountId
        recurring.isIncome = isIncome
        recurring.frequency = frequency
        recurring.startDate = Int64(startDate.timeIntervalSince1970 * 1000)
        recurring.endDate = endDate != nil ? Int64(endDate!.timeIntervalSince1970 * 1000) : 0
        recurring.note = note
        
        CoreDataStack.shared.saveContext()
        loadRecurringTransactions()
    }
    
    func deactivateRecurringTransaction(_ recurring: RecurringTransactionEntity) {
        recurring.isActive.toggle()
        CoreDataStack.shared.saveContext()
        loadRecurringTransactions()
    }
    
    func deleteRecurringTransaction(_ recurring: RecurringTransactionEntity) {
        context.delete(recurring)
        CoreDataStack.shared.saveContext()
        loadRecurringTransactions()
    }
    
    func executeDueTransactions(completion: @escaping () -> Void) {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        
        for recurring in recurringTransactions where recurring.isActive && recurring.nextExecution <= now {
            // Create a transaction
            if let category = transactionsViewModel.categories.first(where: { $0.id == recurring.categoryId }),
               let account = transactionsViewModel.accounts.first(where: { $0.id == recurring.accountId }) {
                let amount = Double(recurring.amount) / 100.0
                transactionsViewModel.addTransaction(
                    amount: amount,
                    note: recurring.note,
                    category: category,
                    account: account,
                    isIncome: recurring.isIncome
                )
            }
            
            // Update next execution date
            recurring.lastExecuted = now
            recurring.nextExecution = calculateNextExecution(from: recurring.nextExecution, frequency: recurring.frequency)
        }
        
        CoreDataStack.shared.saveContext()
        completion()
    }
    
    private func calculateNextExecution(from current: Int64, frequency: String) -> Int64 {
        let currentDate = Date(timeIntervalSince1970: TimeInterval(current / 1000))
        var nextDate: Date
        
        switch frequency {
        case "DAILY":
            nextDate = Calendar.current.date(byAdding: .day, value: 1, to: currentDate) ?? currentDate
        case "WEEKLY":
            nextDate = Calendar.current.date(byAdding: .weekOfYear, value: 1, to: currentDate) ?? currentDate
        case "MONTHLY":
            nextDate = Calendar.current.date(byAdding: .month, value: 1, to: currentDate) ?? currentDate
        case "YEARLY":
            nextDate = Calendar.current.date(byAdding: .year, value: 1, to: currentDate) ?? currentDate
        default:
            nextDate = currentDate
        }
        
        return Int64(nextDate.timeIntervalSince1970 * 1000)
    }
}