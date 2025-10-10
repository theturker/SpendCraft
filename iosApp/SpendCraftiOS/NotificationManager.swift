//
//  NotificationManager.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import UserNotifications
import SwiftUI

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

