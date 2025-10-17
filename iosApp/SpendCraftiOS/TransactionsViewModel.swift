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
        
        // Refresh all objects to get latest data
        context.refreshAllObjects()
        
        do {
            transactions = try context.fetch(fetchRequest)
            // Force UI update
            objectWillChange.send()
        } catch {
            print("Error fetching transactions: \(error)")
        }
    }
    
    func loadCategories() {
        let fetchRequest: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest() as! NSFetchRequest<CategoryEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \CategoryEntity.name, ascending: true)]
        
        // Refresh context to get latest data
        context.refreshAllObjects()
        
        do {
            categories = try context.fetch(fetchRequest)
            
            // Debug: Kategorileri ve type'larını yazdır
            print("📂 Loaded \(categories.count) categories:")
            for cat in categories {
                print("  - \(cat.name ?? "?"): type=\(cat.type ?? "nil")")
            }
        } catch {
            print("❌ Error fetching categories: \(error)")
        }
    }
    
    func addCategory(name: String, icon: String, color: Color, type: String) {
        let category = CategoryEntity(context: context)
        category.id = Int64.random(in: 1...1000000)
        category.name = name
        category.icon = icon
        category.type = type
        
        print("➕ Adding category: \(name), type: \(type)")
        
        // Convert SwiftUI Color to hex string for storage
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        let hexString = String(format: "#%02X%02X%02X",
                              Int(red * 255),
                              Int(green * 255),
                              Int(blue * 255))
        category.color = hexString
        
        // CRITICAL: Save to disk immediately
        do {
            // First save the main context
            try context.save()
            print("✅ Category saved to main context")
            
            // Then save the persistent container to ensure it's written to disk
            if context.hasChanges {
                try context.save()
            }
            
            // Force the persistent store coordinator to save
            CoreDataStack.shared.saveContext()
            print("✅ Category persisted to disk")
        } catch {
            print("❌ Error saving category: \(error.localizedDescription)")
            print("❌ Full error: \(error)")
        }
        
        // Reload categories and force UI update
        loadCategories()
        objectWillChange.send()
        
        print("✅ Category saved. Total categories: \(categories.count)")
        print("📂 Categories by type:")
        for cat in categories {
            print("  - \(cat.name ?? "?"): \(cat.type ?? "no-type")")
        }
    }
    
    func deleteCategory(_ category: CategoryEntity) {
        context.delete(category)
        CoreDataStack.shared.saveContext()
        loadCategories()
    }
    
    func categoriesForType(_ isIncome: Bool) -> [CategoryEntity] {
        let type = isIncome ? "income" : "expense"
        let filtered = categories.filter { category in
            if let categoryType = category.type, !categoryType.isEmpty {
                return categoryType == type
            } else {
                // Type yoksa hepsini göster (backward compatibility)
                return true
            }
        }
        
        print("🔍 Filtering categories for \(type): found \(filtered.count) out of \(categories.count)")
        print("🔍 Filtered categories:")
        for cat in filtered {
            print("  - \(cat.name ?? "?"): \(cat.type ?? "no-type")")
        }
        return filtered
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
    
    func addTransaction(amount: Double, note: String?, category: CategoryEntity?, account: AccountEntity?, isIncome: Bool, achievementsViewModel: AchievementsViewModel? = nil, notificationsViewModel: NotificationsViewModel? = nil) {
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
        
        // Update daily streak
        achievementsViewModel?.updateStreak()
        
        // Trigger budget check
        notificationsViewModel?.objectWillChange.send()
        
        // If income is added in the first 5 days of the month, cancel salary notification
        if isIncome {
            let calendar = Calendar.current
            let day = calendar.component(.day, from: Date())
            if (1...5).contains(day) {
                NotificationManager.shared.cancelSalaryNotificationForCurrentMonth()
            }
        }
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
    
    // MARK: - Chart Data Helpers
    
    /// Kategoriye göre harcama verilerini döndürür (Pie Chart için)
    func categorySpendingData() -> [(CategoryEntity, Double)] {
        let categorySpending: [(CategoryEntity, Double)] = categories.compactMap { category in
            let spent = totalSpentForCategory(category)
            return spent > 0 ? (category, spent) : nil
        }
        return categorySpending.sorted { $0.1 > $1.1 }
    }
    
    /// Günlük bazda gelir/gider trendini döndürür (Line Chart için)
    func dailyTrendData(days: Int = 30) -> [(Date, Double, Double)] {
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let startDate = calendar.date(byAdding: .day, value: -days, to: today)!
        
        var dailyData: [(Date, Double, Double)] = []
        
        for dayOffset in 0..<days {
            if let date = calendar.date(byAdding: .day, value: dayOffset, to: startDate) {
                let dayStart = calendar.startOfDay(for: date)
                let dayEnd = calendar.date(byAdding: .day, value: 1, to: dayStart)!
                
                let dayTransactions = transactions.filter { transaction in
                    let transactionDate = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis) / 1000)
                    return transactionDate >= dayStart && transactionDate < dayEnd
                }
                
                let income = dayTransactions.filter { $0.isIncome }.reduce(0.0) { $0 + $1.amount }
                let expense = dayTransactions.filter { !$0.isIncome }.reduce(0.0) { $0 + $1.amount }
                
                dailyData.append((dayStart, income, expense))
            }
        }
        
        return dailyData
    }
    
    /// Haftalık bazda gelir/gider trendini döndürür
    func weeklyTrendData(weeks: Int = 12) -> [(Date, Double, Double)] {
        let calendar = Calendar.current
        let today = Date()
        let startDate = calendar.date(byAdding: .weekOfYear, value: -weeks, to: today)!
        
        var weeklyData: [(Date, Double, Double)] = []
        
        for weekOffset in 0..<weeks {
            if let weekStart = calendar.date(byAdding: .weekOfYear, value: weekOffset, to: startDate) {
                let weekEnd = calendar.date(byAdding: .weekOfYear, value: 1, to: weekStart)!
                
                let weekTransactions = transactions.filter { transaction in
                    let transactionDate = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis) / 1000)
                    return transactionDate >= weekStart && transactionDate < weekEnd
                }
                
                let income = weekTransactions.filter { $0.isIncome }.reduce(0.0) { $0 + $1.amount }
                let expense = weekTransactions.filter { !$0.isIncome }.reduce(0.0) { $0 + $1.amount }
                
                weeklyData.append((weekStart, income, expense))
            }
        }
        
        return weeklyData
    }
    
    /// Aylık bazda gelir/gider trendini döndürür
    func monthlyTrendData(months: Int = 12) -> [(Date, Double, Double)] {
        let calendar = Calendar.current
        let today = Date()
        let startDate = calendar.date(byAdding: .month, value: -months, to: today)!
        
        var monthlyData: [(Date, Double, Double)] = []
        
        for monthOffset in 0..<months {
            if let monthStart = calendar.date(byAdding: .month, value: monthOffset, to: startDate) {
                let monthEnd = calendar.date(byAdding: .month, value: 1, to: monthStart)!
                
                let monthTransactions = transactions.filter { transaction in
                    let transactionDate = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis) / 1000)
                    return transactionDate >= monthStart && transactionDate < monthEnd
                }
                
                let income = monthTransactions.filter { $0.isIncome }.reduce(0.0) { $0 + $1.amount }
                let expense = monthTransactions.filter { !$0.isIncome }.reduce(0.0) { $0 + $1.amount }
                
                monthlyData.append((monthStart, income, expense))
            }
        }
        
        return monthlyData
    }
}