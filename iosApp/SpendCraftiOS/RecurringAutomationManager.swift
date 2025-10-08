//
//  RecurringAutomationManager.swift
//  SpendCraftiOS
//
//  Otomatik Tekrarlayan İşlem Yöneticisi
//

import Foundation
import BackgroundTasks
import CoreData
import UserNotifications

class RecurringAutomationManager {
    static let shared = RecurringAutomationManager()
    
    private let backgroundTaskIdentifier = "com.alperen.spendcraft.recurring"
    
    private init() {}
    
    // MARK: - Register Background Task
    
    func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: backgroundTaskIdentifier,
            using: nil
        ) { task in
            self.handleRecurringTransactions(task: task as! BGAppRefreshTask)
        }
        
        print("✅ Background task registered")
    }
    
    // MARK: - Schedule Background Task
    
    func scheduleBackgroundTask() {
        let request = BGAppRefreshTaskRequest(identifier: backgroundTaskIdentifier)
        request.earliestBeginDate = Calendar.current.date(byAdding: .hour, value: 1, to: Date())
        
        do {
            try BGTaskScheduler.shared.submit(request)
            print("✅ Background task scheduled")
        } catch {
            print("❌ Could not schedule background task: \(error)")
        }
    }
    
    // MARK: - Handle Recurring Transactions
    
    private func handleRecurringTransactions(task: BGAppRefreshTask) {
        // Schedule next execution
        scheduleBackgroundTask()
        
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1
        
        let context = CoreDataStack.shared.container.newBackgroundContext()
        
        task.expirationHandler = {
            queue.cancelAllOperations()
        }
        
        let operation = BlockOperation {
            self.processDueRecurringTransactions(context: context)
        }
        
        operation.completionBlock = {
            task.setTaskCompleted(success: !operation.isCancelled)
        }
        
        queue.addOperation(operation)
    }
    
    // MARK: - Process Due Transactions
    
    func processDueRecurringTransactions(context: NSManagedObjectContext) {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        
        let fetchRequest: NSFetchRequest<RecurringTransactionEntity> = RecurringTransactionEntity.fetchRequest() as! NSFetchRequest<RecurringTransactionEntity>
        fetchRequest.predicate = NSPredicate(format: "isActive == YES AND nextExecution <= %lld", now)
        
        do {
            let dueTransactions = try context.fetch(fetchRequest)
            var executedCount = 0
            
            for recurring in dueTransactions {
                // Check if end date has passed
                if recurring.endDate > 0 && recurring.endDate < now {
                    recurring.isActive = false
                    continue
                }
                
                // Create transaction
                let transaction = TransactionEntity(context: context)
                transaction.id = Int64.random(in: 1...1000000)
                transaction.amountMinor = recurring.amount
                transaction.timestampUtcMillis = now
                transaction.note = "\(recurring.name ?? "") (Otomatik)"
                transaction.categoryId = recurring.categoryId
                transaction.accountId = recurring.accountId
                transaction.isIncome = recurring.isIncome
                
                // Update recurring transaction
                recurring.lastExecuted = now
                recurring.nextExecution = calculateNextExecution(
                    from: Date(timeIntervalSince1970: TimeInterval(now / 1000)),
                    frequency: recurring.frequency ?? "MONTHLY"
                )
                
                executedCount += 1
            }
            
            try context.save()
            
            if executedCount > 0 {
                sendNotification(count: executedCount)
                print("✅ \(executedCount) recurring transaction(s) executed")
            }
            
        } catch {
            print("❌ Error processing recurring transactions: \(error)")
        }
    }
    
    // MARK: - Calculate Next Execution
    
    private func calculateNextExecution(from date: Date, frequency: String) -> Int64 {
        let calendar = Calendar.current
        var nextDate = date
        
        switch frequency {
        case "DAILY":
            nextDate = calendar.date(byAdding: .day, value: 1, to: date) ?? date
        case "WEEKLY":
            nextDate = calendar.date(byAdding: .weekOfYear, value: 1, to: date) ?? date
        case "MONTHLY":
            nextDate = calendar.date(byAdding: .month, value: 1, to: date) ?? date
        case "YEARLY":
            nextDate = calendar.date(byAdding: .year, value: 1, to: date) ?? date
        default:
            nextDate = calendar.date(byAdding: .month, value: 1, to: date) ?? date
        }
        
        return Int64(nextDate.timeIntervalSince1970 * 1000)
    }
    
    // MARK: - Send Notification
    
    private func sendNotification(count: Int) {
        let content = UNMutableNotificationContent()
        content.title = "Otomatik İşlemler Eklendi"
        content.body = "\(count) adet tekrarlayan işlem otomatik olarak eklendi."
        content.sound = .default
        content.badge = 1
        
        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: nil
        )
        
        UNUserNotificationCenter.current().add(request)
    }
    
    // MARK: - Manual Execution (for testing)
    
    func executeNow() {
        let context = CoreDataStack.shared.container.viewContext
        processDueRecurringTransactions(context: context)
    }
}
