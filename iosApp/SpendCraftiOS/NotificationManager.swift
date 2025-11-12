//
//  NotificationManager.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import UserNotifications
import SwiftUI

// MARK: - Notification Template

struct NotificationTemplate: Identifiable, Codable {
    let id: String
    let title: String // Artık localization key olarak saklanıyor
    let body: String // Artık localization key olarak saklanıyor
    let icon: String
    let category: String
    var hour: Int
    var minute: Int
    var isEnabled: Bool
    var daysOfWeek: [Int]? // nil = her gün, [1-7] = haftanın günleri (1=Pazar)
    var daysOfMonth: [Int]? // nil = her gün, [1-31] = ayın günleri
    
    var localizedTitle: String {
        // Eğer title bir localization key ise çevir, değilse direkt döndür (eski veriler için)
        if title.hasPrefix("notification.") {
            return NSLocalizedString(title, comment: title)
        }
        return title
    }
    
    var localizedBody: String {
        // Eğer body bir localization key ise çevir, değilse direkt döndür (eski veriler için)
        if body.hasPrefix("notification.") {
            return NSLocalizedString(body, comment: body)
        }
        return body
    }
    
    static let templates: [NotificationTemplate] = [
        // Morning Motivation
        NotificationTemplate(id: "morning_1", title: "notification.morning.title.1", body: "notification.morning.body.1", icon: "sun.max.fill", category: "notification.morning", hour: 8, minute: 0, isEnabled: true, daysOfWeek: nil, daysOfMonth: nil),
        NotificationTemplate(id: "morning_2", title: "notification.morning.title.2", body: "notification.morning.body.2", icon: "cup.and.saucer.fill", category: "notification.morning", hour: 9, minute: 30, isEnabled: false, daysOfWeek: nil, daysOfMonth: nil),
        
        // Noon Reminders
        NotificationTemplate(id: "noon_1", title: "notification.noon.title.1", body: "notification.noon.body.1", icon: "fork.knife", category: "notification.noon", hour: 12, minute: 30, isEnabled: true, daysOfWeek: nil, daysOfMonth: nil),
        NotificationTemplate(id: "noon_2", title: "notification.noon.title.2", body: "notification.noon.body.2", icon: "chart.bar.fill", category: "notification.noon", hour: 14, minute: 0, isEnabled: false, daysOfWeek: nil, daysOfMonth: nil),
        
        // Evening Summaries
        NotificationTemplate(id: "evening_1", title: "notification.evening.title.1", body: "notification.evening.body.1", icon: "moon.stars.fill", category: "notification.evening", hour: 20, minute: 0, isEnabled: true, daysOfWeek: nil, daysOfMonth: nil),
        NotificationTemplate(id: "evening_2", title: "notification.evening.title.2", body: "notification.evening.body.2", icon: "target", category: "notification.evening", hour: 21, minute: 0, isEnabled: false, daysOfWeek: nil, daysOfMonth: nil),
        
        // Weekly Summaries
        NotificationTemplate(id: "weekly_1", title: "notification.weekly.title.1", body: "notification.weekly.body.1", icon: "calendar", category: "notification.weekly", hour: 10, minute: 0, isEnabled: false, daysOfWeek: [1], daysOfMonth: nil), // Monday
        NotificationTemplate(id: "weekly_2", title: "notification.weekly.title.2", body: "notification.weekly.body.2", icon: "party.popper.fill", category: "notification.weekly", hour: 18, minute: 0, isEnabled: false, daysOfWeek: [6], daysOfMonth: nil), // Saturday
        
        // Monthly Reminders
        NotificationTemplate(id: "monthly_1", title: "notification.monthly.title.1", body: "notification.monthly.body.1", icon: "banknote.fill", category: "notification.monthly", hour: 10, minute: 0, isEnabled: true, daysOfWeek: nil, daysOfMonth: [1, 2, 3, 4, 5]),
        NotificationTemplate(id: "monthly_2", title: "notification.monthly.title.2", body: "notification.monthly.body.2", icon: "doc.text.fill", category: "notification.monthly", hour: 19, minute: 0, isEnabled: false, daysOfWeek: nil, daysOfMonth: [25, 26, 27, 28, 29, 30]),
        
        // Motivation
        NotificationTemplate(id: "motivation_1", title: "notification.motivation.title.1", body: "notification.motivation.body.1", icon: "star.fill", category: "notification.motivation", hour: 16, minute: 0, isEnabled: false, daysOfWeek: nil, daysOfMonth: nil),
        NotificationTemplate(id: "motivation_2", title: "notification.motivation.title.2", body: "notification.motivation.body.2", icon: "rocket.fill", category: "notification.motivation", hour: 15, minute: 30, isEnabled: false, daysOfWeek: nil, daysOfMonth: nil),
        
        // Special Days
        NotificationTemplate(id: "weekend_1", title: "notification.special.title.1", body: "notification.special.body.1", icon: "gift.fill", category: "notification.special", hour: 11, minute: 0, isEnabled: false, daysOfWeek: [6, 7], daysOfMonth: nil),
        NotificationTemplate(id: "weekend_2", title: "notification.special.title.2", body: "notification.special.body.2", icon: "house.fill", category: "notification.special", hour: 19, minute: 0, isEnabled: false, daysOfWeek: [7], daysOfMonth: nil)
    ]
}

// MARK: - Custom Notification

struct CustomNotification: Identifiable, Codable {
    let id: String
    var title: String
    var body: String
    var hour: Int
    var minute: Int
    var isEnabled: Bool
    var daysOfWeek: [Int]? // nil = her gün
    
    init(id: String = UUID().uuidString, title: String, body: String, hour: Int, minute: Int, isEnabled: Bool = true, daysOfWeek: [Int]? = nil) {
        self.id = id
        self.title = title
        self.body = body
        self.hour = hour
        self.minute = minute
        self.isEnabled = isEnabled
        self.daysOfWeek = daysOfWeek
    }
}

class NotificationManager: ObservableObject {
    static let shared = NotificationManager()
    
    @Published var isAuthorized = false
    @Published var templates: [NotificationTemplate] = []
    @Published var customNotifications: [CustomNotification] = []
    
    private let templatesKey = "notification_templates"
    private let customNotificationsKey = "custom_notifications"
    
    private init() {
        checkAuthorizationStatus()
        loadTemplates()
        loadCustomNotifications()
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
    
    // MARK: - Load & Save
    
    private func loadTemplates() {
        if let data = UserDefaults.standard.data(forKey: templatesKey),
           let loaded = try? JSONDecoder().decode([NotificationTemplate].self, from: data) {
            templates = loaded
        } else {
            templates = NotificationTemplate.templates
            saveTemplates()
        }
    }
    
    private func saveTemplates() {
        if let data = try? JSONEncoder().encode(templates) {
            UserDefaults.standard.set(data, forKey: templatesKey)
        }
    }
    
    private func loadCustomNotifications() {
        if let data = UserDefaults.standard.data(forKey: customNotificationsKey),
           let loaded = try? JSONDecoder().decode([CustomNotification].self, from: data) {
            customNotifications = loaded
        }
    }
    
    private func saveCustomNotifications() {
        if let data = try? JSONEncoder().encode(customNotifications) {
            UserDefaults.standard.set(data, forKey: customNotificationsKey)
        }
    }
    
    // MARK: - Custom Notifications Management
    
    func addCustomNotification(_ notification: CustomNotification) {
        customNotifications.append(notification)
        saveCustomNotifications()
        scheduleAllNotifications()
    }
    
    func updateCustomNotification(_ notification: CustomNotification) {
        if let index = customNotifications.firstIndex(where: { $0.id == notification.id }) {
            customNotifications[index] = notification
            saveCustomNotifications()
            scheduleAllNotifications()
        }
    }
    
    func deleteCustomNotification(_ notification: CustomNotification) {
        customNotifications.removeAll { $0.id == notification.id }
        saveCustomNotifications()
        scheduleAllNotifications()
    }
    
    func updateTemplate(_ template: NotificationTemplate) {
        if let index = templates.firstIndex(where: { $0.id == template.id }) {
            templates[index] = template
            saveTemplates()
            scheduleAllNotifications()
        }
    }
    
    // MARK: - Schedule Notifications
    
    func scheduleAllNotifications() {
        // Cancel all existing notifications first
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        
        // Schedule template notifications
        for template in templates where template.isEnabled {
            scheduleNotification(
                id: template.id,
                title: template.localizedTitle,
                body: template.localizedBody,
                hour: template.hour,
                minute: template.minute,
                daysOfWeek: template.daysOfWeek,
                daysOfMonth: template.daysOfMonth
            )
        }
        
        // Schedule custom notifications
        for notification in customNotifications where notification.isEnabled {
            scheduleNotification(
                id: notification.id,
                title: notification.title,
                body: notification.body,
                hour: notification.hour,
                minute: notification.minute,
                daysOfWeek: notification.daysOfWeek,
                daysOfMonth: nil
            )
        }
    }
    
    private func scheduleNotification(id: String, title: String, body: String, hour: Int, minute: Int, daysOfWeek: [Int]?, daysOfMonth: [Int]?) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default
        content.badge = 1
        
        if let monthDays = daysOfMonth {
            // Specific days of month (e.g., 1-5 for salary, 25-30 for bills)
            for day in monthDays {
                var dateComponents = DateComponents()
                dateComponents.day = day
                dateComponents.hour = hour
                dateComponents.minute = minute
                
                let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
                let request = UNNotificationRequest(
                    identifier: "\(id)_monthDay_\(day)",
                    content: content,
                    trigger: trigger
                )
                
                UNUserNotificationCenter.current().add(request) { error in
                    if let error = error {
                        print("Error scheduling notification \(id) for month day \(day): \(error)")
                    }
                }
            }
        } else if let weekDays = daysOfWeek {
            // Specific days of week
            for day in weekDays {
                var dateComponents = DateComponents()
                dateComponents.weekday = day
                dateComponents.hour = hour
                dateComponents.minute = minute
                
                let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
                let request = UNNotificationRequest(
                    identifier: "\(id)_weekDay_\(day)",
                    content: content,
                    trigger: trigger
                )
                
                UNUserNotificationCenter.current().add(request) { error in
                    if let error = error {
                        print("Error scheduling notification \(id) for week day \(day): \(error)")
                    }
                }
            }
        } else {
            // Every day
            var dateComponents = DateComponents()
            dateComponents.hour = hour
            dateComponents.minute = minute
            
            let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
            let request = UNNotificationRequest(
                identifier: id,
                content: content,
                trigger: trigger
            )
            
            UNUserNotificationCenter.current().add(request) { error in
                if let error = error {
                    print("Error scheduling notification \(id): \(error)")
                }
            }
        }
    }
    
    // MARK: - Salary Notification Management
    
    func cancelSalaryNotificationForCurrentMonth() {
        let calendar = Calendar.current
        let now = Date()
        let year = calendar.component(.year, from: now)
        let month = calendar.component(.month, from: now)
        
        // Cancel all salary notification variants for days 1-5 of current month
        for day in 1...5 {
            UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: ["monthly_1_monthDay_\(day)"])
        }
        
        // Save that salary notification was dismissed for this month
        let key = "salaryNotificationDismissed_\(year)_\(month)"
        UserDefaults.standard.set(true, forKey: key)
        
        print("✅ Salary notification cancelled for \(month)/\(year)")
    }
    
    func shouldShowSalaryNotificationForCurrentMonth() -> Bool {
        let calendar = Calendar.current
        let now = Date()
        let year = calendar.component(.year, from: now)
        let month = calendar.component(.month, from: now)
        let day = calendar.component(.day, from: now)
        
        // Only show between day 1-5
        guard (1...5).contains(day) else {
            return false
        }
        
        // Check if already dismissed for this month
        let key = "salaryNotificationDismissed_\(year)_\(month)"
        let dismissed = UserDefaults.standard.bool(forKey: key)
        
        return !dismissed
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

