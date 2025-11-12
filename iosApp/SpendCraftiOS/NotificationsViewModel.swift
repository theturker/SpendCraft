//
//  NotificationsViewModel.swift
//  SpendCraftiOS
//
//  Bildirimler ViewModel
//

import Foundation
import Combine
import UserNotifications
import shared

class NotificationsViewModel: ObservableObject {
    @Published var notifications: [NotificationItem] = []
    @Published var unreadCount: Int = 0
    
    private let userDefaults = UserDefaults.standard
    private let notificationsKey = "saved_notifications"
    
    // Shared business logic - NO PLATFORM DEPENDENCIES
    private let sharedBusinessLogic = SharedNotificationBusinessLogic()
    
    init() {
        loadNotifications()
        requestNotificationPermission()
    }
    
    // MARK: - Load & Save
    
    func loadNotifications() {
        if let data = userDefaults.data(forKey: notificationsKey),
           let decoded = try? JSONDecoder().decode([NotificationItem].self, from: data) {
            notifications = decoded.sorted { $0.timestamp > $1.timestamp }
        } else {
            // Yeni kullanıcılar için boş liste ile başla
            notifications = []
        }
        updateUnreadCount()
    }
    
    private func saveNotifications() {
        if let encoded = try? JSONEncoder().encode(notifications) {
            userDefaults.set(encoded, forKey: notificationsKey)
        }
        updateUnreadCount()
    }
    
    private func updateUnreadCount() {
        unreadCount = notifications.filter { !$0.isRead }.count
    }
    
    // MARK: - Actions
    
    func markAsRead(_ id: Int64) {
        if let index = notifications.firstIndex(where: { $0.id == id }) {
            notifications[index].isRead = true
            saveNotifications()
        }
    }
    
    func markAllAsRead() {
        for index in notifications.indices {
            notifications[index].isRead = true
        }
        saveNotifications()
    }
    
    func deleteNotification(_ id: Int64) {
        notifications.removeAll { $0.id == id }
        saveNotifications()
    }
    
    func addNotification(title: String, message: String, type: NotificationType) {
        let newNotification = NotificationItem(
            id: Int64.random(in: 1000...999999),
            title: title,
            message: message,
            type: type,
            timestamp: Date().timeIntervalSince1970Millis,
            isRead: false
        )
        notifications.insert(newNotification, at: 0)
        saveNotifications()
        
        // Local notification gönder
        sendLocalNotification(title: title, message: message)
    }
    
    // MARK: - Budget Alert
    
    func checkBudgetAlert(category: String, percentage: Double) {
        if percentage >= 100 {
            addNotification(
                title: "🚨 Bütçe Aşıldı!",
                message: "\(category) kategorisinde bütçenizin tamamını kullandınız!",
                type: .budgetAlert
            )
        } else if percentage >= 80 {
            addNotification(
                title: "⚠️ Bütçe Uyarısı",
                message: "\(category) kategorisinde bütçenizin %80'ini kullandınız.",
                type: .budgetAlert
            )
        }
    }
    
    func checkAllBudgets(budgets: [BudgetEntity], spentAmounts: [String: Double]) {
        for budget in budgets {
            let categoryId = budget.categoryId // categoryId is String
            guard let spent = spentAmounts[categoryId] else { continue }
            
            let limit = Double(budget.monthlyLimitMinor) / 100.0
            guard limit > 0 else { continue }
            
            let percentage = (spent / limit) * 100.0
            
            let localizedCategory = budget.category?.localizedName ?? NSLocalizedString("common.other", comment: "Other")
            
            // Check if we already sent notification for this threshold
            let hasNotified80 = notifications.contains { notification in
                notification.type == .budgetAlert &&
                notification.message.contains(localizedCategory) &&
                notification.message.contains("80")
            }
            
            let hasNotified100 = notifications.contains { notification in
                notification.type == .budgetAlert &&
                notification.message.contains(localizedCategory) &&
                notification.message.contains("tamamını")
            }
            
            if percentage >= 100 && !hasNotified100 {
                checkBudgetAlert(category: localizedCategory, percentage: percentage)
            } else if percentage >= 80 && percentage < 100 && !hasNotified80 {
                checkBudgetAlert(category: localizedCategory, percentage: percentage)
            }
        }
    }
    
    // MARK: - Achievement
    
    func celebrateAchievement(title: String, description: String) {
        addNotification(
            title: "🎉 \(title)",
            message: description,
            type: .achievement
        )
    }
    
    // MARK: - Spending Reminder
    
    func sendSpendingReminder() {
        addNotification(
            title: "💰 Harcama Hatırlatıcısı",
            message: "Bugün henüz hiç işlem eklemediniz. Harcamalarınızı takip etmeyi unutmayın!",
            type: .spendingReminder
        )
    }
    
    // MARK: - Local Notifications
    
    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if granted {
                print("✅ Bildirim izni verildi")
            } else {
                print("❌ Bildirim izni reddedildi")
            }
        }
    }
    
    private func sendLocalNotification(title: String, message: String) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = message
        content.sound = .default
        content.badge = NSNumber(value: unreadCount)
        
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: trigger
        )
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("❌ Bildirim gönderilemedi: \(error)")
            }
        }
    }
    
    // MARK: - Schedule Recurring Reminders
    
    func scheduleDailyReminder() {
        let content = UNMutableNotificationContent()
        content.title = "Günlük Hatırlatma"
        content.body = "Bugünün harcamalarını eklemeyi unutmayın!"
        content.sound = .default
        
        var dateComponents = DateComponents()
        dateComponents.hour = 20 // 20:00'de
        dateComponents.minute = 0
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(
            identifier: "dailyReminder",
            content: content,
            trigger: trigger
        )
        
        UNUserNotificationCenter.current().add(request)
    }
}
