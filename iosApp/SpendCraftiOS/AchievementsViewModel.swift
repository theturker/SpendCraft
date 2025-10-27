//
//  AchievementsViewModel.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData
import SwiftUI
import shared

class AchievementsViewModel: ObservableObject {
    @Published var achievements: [AchievementEntity] = []
    @Published var currentStreak: Int = 0
    @Published var longestStreak: Int = 0
    
    private let context = CoreDataStack.shared.container.viewContext
    
    // Shared business logic - NO PLATFORM DEPENDENCIES
    private let sharedBusinessLogic = SharedAchievementBusinessLogic()
    
    func loadAchievements() {
        let fetchRequest: NSFetchRequest<AchievementEntity> = AchievementEntity.fetchRequest() as! NSFetchRequest<AchievementEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \AchievementEntity.points, ascending: false)]
        
        do {
            achievements = try context.fetch(fetchRequest)
            
            // If no achievements exist, seed them
            if achievements.isEmpty {
                seedAchievements()
                achievements = try context.fetch(fetchRequest)
            } else {
                // Check if achievements need migration (if they contain localized strings instead of keys)
                let needsMigration = achievements.contains { achievement in
                    let name = achievement.name ?? ""
                    return name.contains("Uzman") || name.contains("Başlangıç") || name.contains("Düzenli")
                }
                
                if needsMigration {
                    print("🔄 Migrating achievements to use localization keys...")
                    CoreDataStack.shared.migrateLocalizationData()
                    seedAchievements()
                    achievements = try context.fetch(fetchRequest)
                }
            }
        } catch {
            print("Error fetching achievements: \(error)")
        }
    }
    
    private func seedAchievements() {
        let achievementsData: [(String, String, String, Int64, String, Int64)] = [
            ("achievement.beginner", "achievement.beginner.description", "checkmark.circle.fill", 10, "TRANSACTIONS", 1),
            ("achievement.beginner", "achievement.beginner.description", "flame.fill", 25, "TRANSACTIONS", 5),
            ("achievement.regular", "achievement.regular.description", "star.fill", 50, "TRANSACTIONS", 10),
            ("achievement.expert", "achievement.expert.description", "crown.fill", 100, "TRANSACTIONS", 50),
            ("achievement.category.master", "achievement.category.master.description", "folder.badge.plus", 30, "CATEGORIES", 5),
            ("achievement.budget.conscious", "achievement.budget.conscious.description", "chart.bar.fill", 20, "BUDGET", 1),
            ("achievement.thrifty", "achievement.thrifty.description", "shield.fill", 75, "BUDGET", 1),
            ("achievement.investor", "achievement.investor.description", "banknote.fill", 15, "INCOME", 1),
        ]
        
        for (nameKey, descriptionKey, icon, points, category, maxProgress) in achievementsData {
            let achievement = AchievementEntity(context: context)
            achievement.id = Int64.random(in: 1...1000000)
            achievement.name = nameKey // Store the key instead of localized string
            achievement.achievementDescription = descriptionKey // Store the key instead of localized string
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
        var hasChanges = false
        
        for achievement in achievements where !achievement.isUnlocked {
            var shouldUnlock = false
            let oldProgress = achievement.progress
            
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
            
            // Check if progress changed
            if oldProgress != achievement.progress {
                hasChanges = true
            }
            
            if shouldUnlock {
                unlockAchievement(achievement, notificationsViewModel: notificationsViewModel)
                hasChanges = true
            }
        }
        
        CoreDataStack.shared.saveContext()
        
        // Force UI update by reloading achievements from CoreData
        if hasChanges {
            // Reload to ensure SwiftUI picks up the changes
            loadAchievements()
        }
    }
    
    func unlockAchievement(_ achievement: AchievementEntity, notificationsViewModel: NotificationsViewModel? = nil) {
        achievement.isUnlocked = true
        achievement.unlockedAt = Int64(Date().timeIntervalSince1970 * 1000)
        achievement.progress = achievement.maxProgress // Set progress to max when unlocked
        CoreDataStack.shared.saveContext()
        
        // Reload to ensure SwiftUI picks up the changes
        loadAchievements()
        
        // Send notification
        notificationsViewModel?.celebrateAchievement(
            title: NSLocalizedString(achievement.name ?? "", comment: achievement.name ?? ""),
            description: NSLocalizedString(achievement.achievementDescription ?? "", comment: achievement.achievementDescription ?? "")
        )
    }
    
    func updateProgress(for category: String, progress: Int64) {
        var hasChanges = false
        
        for achievement in achievements where achievement.category == category && !achievement.isUnlocked {
            let oldProgress = achievement.progress
            achievement.progress = progress
            
            if oldProgress != progress {
                hasChanges = true
            }
            
            if progress >= achievement.maxProgress {
                unlockAchievement(achievement)
            }
        }
        
        CoreDataStack.shared.saveContext()
        
        // Force UI update by reloading achievements from CoreData
        if hasChanges {
            loadAchievements()
        }
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
        // NOW USING SHARED KMP CALCULATOR! 🎉
        let lastDate = UserDefaults.standard.object(forKey: "lastStreakDate") as? Date ?? Date.distantPast
        let today = Date()
        
        // Delegate to shared calculator
        let lastStreakDate = shared.DateTimeFormatter.shared.dateToInstant(date: lastDate)
        let todayInstant = shared.DateTimeFormatter.shared.dateToInstant(date: today)
        
        let streakResult = shared.StreakCalculator.shared.calculateCurrentStreak(
            dailyEntries: [lastStreakDate],
            today: todayInstant
        )
        
        currentStreak = Int(streakResult)
        
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