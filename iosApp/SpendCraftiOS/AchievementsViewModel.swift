//
//  AchievementsViewModel.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData
import SwiftUI

class AchievementsViewModel: ObservableObject {
    @Published var achievements: [AchievementEntity] = []
    @Published var currentStreak: Int = 0
    @Published var longestStreak: Int = 0
    
    private let context = CoreDataStack.shared.container.viewContext
    
    func loadAchievements() {
        let fetchRequest: NSFetchRequest<AchievementEntity> = AchievementEntity.fetchRequest() as! NSFetchRequest<AchievementEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \AchievementEntity.points, ascending: false)]
        
        do {
            achievements = try context.fetch(fetchRequest)
            
            // If no achievements exist, seed them
            if achievements.isEmpty {
                seedAchievements()
                achievements = try context.fetch(fetchRequest)
            }
        } catch {
            print("Error fetching achievements: \(error)")
        }
    }
    
    private func seedAchievements() {
        let achievementsData: [(String, String, String, Int64, String, Int64)] = [
            ("İlk Adım", "İlk işleminizi kaydedin", "checkmark.circle.fill", 10, "TRANSACTIONS", 1),
            ("Başlangıç", "5 işlem kaydedin", "flame.fill", 25, "TRANSACTIONS", 5),
            ("Düzenli", "10 işlem kaydedin", "star.fill", 50, "TRANSACTIONS", 10),
            ("Uzman", "50 işlem kaydedin", "crown.fill", 100, "TRANSACTIONS", 50),
            ("Kategori Ustası", "5 farklı kategori kullanın", "folder.badge.plus", 30, "CATEGORIES", 5),
            ("Bütçe Bilinci", "İlk bütçenizi oluşturun", "chart.bar.fill", 20, "BUDGET", 1),
            ("Tutumlu", "Aylık bütçenize uyun", "shield.fill", 75, "BUDGET", 1),
            ("Yatırımcı", "İlk gelirinizi kaydedin", "banknote.fill", 15, "INCOME", 1),
        ]
        
        for (name, description, icon, points, category, maxProgress) in achievementsData {
            let achievement = AchievementEntity(context: context)
            achievement.id = Int64.random(in: 1...1000000)
            achievement.name = name
            achievement.achievementDescription = description
            achievement.icon = icon
            achievement.points = points
            achievement.category = category
            achievement.isUnlocked = false
            achievement.unlockedAt = 0
            achievement.progress = 0
            achievement.maxProgress = maxProgress
        }
        
        CoreDataStack.shared.saveContext()
    }
    
    func checkAchievements(transactionCount: Int, totalSpent: Double, categories: Int, notificationsViewModel: NotificationsViewModel? = nil) {
        for achievement in achievements where !achievement.isUnlocked {
            var shouldUnlock = false
            
            switch achievement.category {
            case "TRANSACTIONS":
                achievement.progress = Int64(transactionCount)
                shouldUnlock = transactionCount >= achievement.maxProgress
            case "CATEGORIES":
                achievement.progress = Int64(categories)
                shouldUnlock = categories >= achievement.maxProgress
            case "BUDGET":
                // Budget achievements are handled separately
                break
            case "INCOME":
                // Income achievements are handled separately
                break
            default:
                break
            }
            
            if shouldUnlock {
                unlockAchievement(achievement, notificationsViewModel: notificationsViewModel)
            }
        }
        
        CoreDataStack.shared.saveContext()
    }
    
    func unlockAchievement(_ achievement: AchievementEntity, notificationsViewModel: NotificationsViewModel? = nil) {
        achievement.isUnlocked = true
        achievement.unlockedAt = Int64(Date().timeIntervalSince1970 * 1000)
        CoreDataStack.shared.saveContext()
        
        // Send notification
        notificationsViewModel?.celebrateAchievement(
            title: achievement.name ?? "Başarım",
            description: achievement.achievementDescription ?? ""
        )
    }
    
    func updateProgress(for category: String, progress: Int64) {
        for achievement in achievements where achievement.category == category && !achievement.isUnlocked {
            achievement.progress = progress
            
            if progress >= achievement.maxProgress {
                unlockAchievement(achievement)
            }
        }
        
        CoreDataStack.shared.saveContext()
    }
    
    var totalPoints: Int64 {
        achievements.filter { $0.isUnlocked }.reduce(0) { $0 + $1.points }
    }
    
    // MARK: - Daily Streak
    
    func loadStreak() {
        currentStreak = UserDefaults.standard.integer(forKey: "currentStreak")
        longestStreak = UserDefaults.standard.integer(forKey: "longestStreak")
    }
    
    func updateStreak() {
        let lastDate = UserDefaults.standard.object(forKey: "lastStreakDate") as? Date ?? Date.distantPast
        let calendar = Calendar.current
        
        if calendar.isDateInToday(lastDate) {
            // Already updated today
            return
        } else if calendar.isDateInYesterday(lastDate) {
            // Continue streak
            currentStreak += 1
        } else {
            // Reset streak
            currentStreak = 1
        }
        
        // Update longest streak
        if currentStreak > longestStreak {
            longestStreak = currentStreak
            UserDefaults.standard.set(longestStreak, forKey: "longestStreak")
        }
        
        // Save
        UserDefaults.standard.set(currentStreak, forKey: "currentStreak")
        UserDefaults.standard.set(Date(), forKey: "lastStreakDate")
    }
}