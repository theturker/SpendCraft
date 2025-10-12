//
//  NotificationsViewModel.swift
//  SpendCraftiOS
//
//  Bildirimler ViewModel
//

import Foundation
import Combine
import UserNotifications

class NotificationsViewModel: ObservableObject {
    @Published var notifications: [NotificationItem] = []
    @Published var unreadCount: Int = 0
    
    private let userDefaults = UserDefaults.standard
    private let notificationsKey = "saved_notifications"
    
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
            // İlk açılışta sample notifications ekle
            notifications = NotificationItem.samples
            saveNotifications()
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
            
            // Check if we already sent notification for this threshold
            let hasNotified80 = notifications.contains { notification in
                notification.type == .budgetAlert &&
                notification.message.contains(budget.category?.name ?? "") &&
                notification.message.contains("80")
            }
            
            let hasNotified100 = notifications.contains { notification in
                notification.type == .budgetAlert &&
                notification.message.contains(budget.category?.name ?? "") &&
                notification.message.contains("tamamını")
            }
            
            if percentage >= 100 && !hasNotified100 {
                checkBudgetAlert(category: budget.category?.name ?? "Kategori", percentage: percentage)
            } else if percentage >= 80 && percentage < 100 && !hasNotified80 {
                checkBudgetAlert(category: budget.category?.name ?? "Kategori", percentage: percentage)
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

// MARK: - NotificationManager for Scheduled Reminders

class NotificationManager: ObservableObject {
    static let shared = NotificationManager()
    
    @Published var isAuthorized = false
    
    private init() {
        checkAuthorizationStatus()
    }
    
    // MARK: - Authorization
    
    func requestAuthorization() async -> Bool {
        do {
            let granted = try await UNUserNotificationCenter.current().requestAuthorization(
                options: [.alert, .badge, .sound]
            )
            await MainActor.run {
                self.isAuthorized = granted
            }
            return granted
        } catch {
            print("Notification authorization error: \(error)")
            return false
        }
    }
    
    func checkAuthorizationStatus() {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            DispatchQueue.main.async {
                self.isAuthorized = settings.authorizationStatus == .authorized
            }
        }
    }
    
    // MARK: - Schedule Notifications
    
    func scheduleAllNotifications() {
        // Cancel all existing notifications first
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        
        // Schedule daily reminders
        scheduleDailyReminders()
        
        // Schedule monthly income reminders
        scheduleMonthlyIncomeReminders()
    }
    
    // MARK: - Daily Reminders (3 times a day)
    
    private func scheduleDailyReminders() {
        let reminderTimes = [
            (hour: 9, minute: 0, title: "Günaydın! ☀️", body: "Bugünkü harcamalarınızı kaydetmeyi unutmayın!"),
            (hour: 13, minute: 0, title: "Öğlen Hatırlatması 🌤️", body: "Harcamalarınızı takip ediyor musunuz?"),
            (hour: 20, minute: 0, title: "Günün Özeti 🌙", body: "Bugünkü harcamalarınızı gözden geçirin!")
        ]
        
        for (index, reminder) in reminderTimes.enumerated() {
            let content = UNMutableNotificationContent()
            content.title = reminder.title
            content.body = reminder.body
            content.sound = .default
            content.badge = 1
            
            // Create date components
            var dateComponents = DateComponents()
            dateComponents.hour = reminder.hour
            dateComponents.minute = reminder.minute
            
            // Create trigger that repeats daily
            let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
            
            // Create request
            let request = UNNotificationRequest(
                identifier: "daily_reminder_\(index)",
                content: content,
                trigger: trigger
            )
            
            // Schedule notification
            UNUserNotificationCenter.current().add(request) { error in
                if let error = error {
                    print("Error scheduling daily reminder \(index): \(error)")
                }
            }
        }
    }
    
    // MARK: - Monthly Income Reminders (Day 1-5)
    
    private func scheduleMonthlyIncomeReminders() {
        // Schedule for days 1-5 of each month at 10 AM
        for day in 1...5 {
            let content = UNMutableNotificationContent()
            content.title = "Gelir Hatırlatması 💰"
            content.body = "Ayın \(day). günü - Gelirlerinizi girmeyi unutmayın!"
            content.sound = .default
            content.badge = 1
            
            var dateComponents = DateComponents()
            dateComponents.day = day
            dateComponents.hour = 10
            dateComponents.minute = 0
            
            let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
            
            let request = UNNotificationRequest(
                identifier: "monthly_income_day_\(day)",
                content: content,
                trigger: trigger
            )
            
            UNUserNotificationCenter.current().add(request) { error in
                if let error = error {
                    print("Error scheduling monthly income reminder for day \(day): \(error)")
                }
            }
        }
    }
    
    // MARK: - Check Today's Income
    
    func checkAndScheduleIncomeReminderIfNeeded(hasIncomeToday: Bool) {
        let calendar = Calendar.current
        let today = Date()
        let day = calendar.component(.day, from: today)
        
        // Only check for days 1-5
        guard (1...5).contains(day) else { return }
        
        // If no income today, send additional reminder
        if !hasIncomeToday {
            let content = UNMutableNotificationContent()
            content.title = "Gelir Girişi Bekleniyor 📊"
            content.body = "Henüz bugün için gelir kaydı girmediniz. Gelirlerinizi kaydetmek ister misiniz?"
            content.sound = .default
            
            // Schedule for 2 hours from now
            let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 7200, repeats: false)
            
            let request = UNNotificationRequest(
                identifier: "income_reminder_today",
                content: content,
                trigger: trigger
            )
            
            UNUserNotificationCenter.current().add(request) { error in
                if let error = error {
                    print("Error scheduling today's income reminder: \(error)")
                }
            }
        }
    }
    
    // MARK: - Badge Management
    
    func clearBadge() {
        UNUserNotificationCenter.current().setBadgeCount(0)
    }
    
    // MARK: - Cancel Notifications
    
    func cancelAllNotifications() {
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
        clearBadge()
    }
    
    func getPendingNotifications() async -> [UNNotificationRequest] {
        return await UNUserNotificationCenter.current().pendingNotificationRequests()
    }
}
