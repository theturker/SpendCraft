//
//  NotificationItem.swift
//  SpendCraftiOS
//
//  Bildirim veri modeli
//

import Foundation

enum NotificationType: String, Codable {
    case budgetAlert = "BUDGET_ALERT"
    case spendingReminder = "SPENDING_REMINDER"
    case achievement = "ACHIEVEMENT"
    case system = "SYSTEM"
    
    var icon: String {
        switch self {
        case .budgetAlert: return "exclamationmark.triangle.fill"
        case .spendingReminder: return "clock.fill"
        case .achievement: return "trophy.fill"
        case .system: return "info.circle.fill"
        }
    }
    
    var color: String {
        switch self {
        case .budgetAlert: return "red"
        case .spendingReminder: return "blue"
        case .achievement: return "yellow"
        case .system: return "gray"
        }
    }
}

struct NotificationItem: Identifiable, Codable {
    let id: Int64
    let title: String
    let message: String
    let type: NotificationType
    let timestamp: Int64
    var isRead: Bool
    
    var date: Date {
        Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
    }
    
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        let currentLanguage = LanguageHelper.shared.getCurrentLanguage()
        formatter.locale = Locale(identifier: currentLanguage)
        return formatter.string(from: date)
    }
}

// Sample notifications for testing
extension NotificationItem {
    static let samples = [
        NotificationItem(
            id: 1,
            title: "Bütçe Uyarısı",
            message: "Gıda kategorisinde bütçenizin %90'ını kullandınız.",
            type: .budgetAlert,
            timestamp: Date().timeIntervalSince1970Millis - 3600000,
            isRead: false
        ),
        NotificationItem(
            id: 2,
            title: "Harcama Hatırlatıcısı",
            message: "Bu hafta henüz hiç işlem eklemediniz.",
            type: .spendingReminder,
            timestamp: Date().timeIntervalSince1970Millis - 7200000,
            isRead: false
        ),
        NotificationItem(
            id: 3,
            title: "Başarı Kazanıldı! 🎉",
            message: "7 gün üst üste işlem eklediniz!",
            type: .achievement,
            timestamp: Date().timeIntervalSince1970Millis - 86400000,
            isRead: true
        ),
        NotificationItem(
            id: 4,
            title: "Sistem Bildirimi",
            message: "Yeni özellikler eklendi. Keşfetmek için ayarlara gidin.",
            type: .system,
            timestamp: Date().timeIntervalSince1970Millis - 172800000,
            isRead: true
        )
    ]
}

extension Date {
    var timeIntervalSince1970Millis: Int64 {
        Int64(self.timeIntervalSince1970 * 1000)
    }
}

