//
//  AchievementEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData

extension AchievementEntity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<AchievementEntity> {
        return NSFetchRequest<AchievementEntity>(entityName: "AchievementEntity")
    }

    @NSManaged public var id: Int64
    @NSManaged public var name: String
    @NSManaged public var achievementDescription: String
    @NSManaged public var icon: String
    @NSManaged public var points: Int64
    @NSManaged public var category: String
    @NSManaged public var isUnlocked: Bool
    @NSManaged public var unlockedAt: Int64
    @NSManaged public var progress: Int64
    @NSManaged public var maxProgress: Int64

}

extension AchievementEntity : Identifiable {

}