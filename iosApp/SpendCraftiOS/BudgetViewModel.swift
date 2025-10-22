//
//  BudgetViewModel.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData
import SwiftUI
import shared

class BudgetViewModel: ObservableObject {
    @Published var budgets: [BudgetEntity] = []
    
    private let context = CoreDataStack.shared.container.viewContext
    private let transactionsViewModel: TransactionsViewModel
    
    init(transactionsViewModel: TransactionsViewModel) {
        self.transactionsViewModel = transactionsViewModel
    }
    
    func loadBudgets() {
        let fetchRequest: NSFetchRequest<BudgetEntity> = BudgetEntity.fetchRequest() as! NSFetchRequest<BudgetEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \BudgetEntity.id, ascending: true)]
        
        do {
            budgets = try context.fetch(fetchRequest)
        } catch {
            print("Error fetching budgets: \(error)")
        }
    }
    
    func upsertBudget(category: CategoryEntity, monthlyLimit: Double) {
        // Check if budget already exists
        if let existingBudget = budgets.first(where: { $0.categoryId == String(category.id) }) {
            existingBudget.monthlyLimitMinor = Int64(monthlyLimit * 100)
        } else {
            let budget = BudgetEntity(context: context)
            budget.id = Int64.random(in: 1...1000000)
            budget.categoryId = String(category.id)
            budget.monthlyLimitMinor = Int64(monthlyLimit * 100)
            budget.category = category
        }
        
        CoreDataStack.shared.saveContext()
        loadBudgets()
    }
    
    func deleteBudget(_ budget: BudgetEntity) {
        context.delete(budget)
        CoreDataStack.shared.saveContext()
        loadBudgets()
    }
    
    func budgetForCategory(_ category: CategoryEntity) -> BudgetEntity? {
        return budgets.first(where: { $0.categoryId == String(category.id) })
    }
    
    func spentForCategory(_ category: CategoryEntity) -> Double {
        return transactionsViewModel.totalSpentForCategory(category)
    }
    
    func budgetProgress(_ budget: BudgetEntity) -> Double {
        guard let category = budget.category else { return 0 }
        let spent = spentForCategory(category)
        let limit = Double(budget.monthlyLimitMinor) / 100.0
        
        // NOW USING SHARED KMP CALCULATOR! 🎉
        let sharedBudget = shared.Budget(
            categoryId: budget.categoryId ?? "0",
            monthlyLimitMinor: budget.monthlyLimitMinor
        )
        
        // Delegate to shared calculator
        let analysis = shared.BudgetCalculator.shared.analyze(
            budget: sharedBudget,
            spent: Int64(spent * 100),
            timestampMillis: Int64(Date().timeIntervalSince1970 * 1000)
        )
        
        return analysis.percentage / 100.0
    }
}