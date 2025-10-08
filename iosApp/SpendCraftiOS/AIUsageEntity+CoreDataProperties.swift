//
//  AIUsageEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  AI kullanım limitleri ve takibi özellikleri
//

import Foundation
import CoreData

extension AIUsageEntity {
    
    @nonobjc public class func fetchRequest() -> NSFetchRequest<AIUsageEntity> {
        return NSFetchRequest<AIUsageEntity>(entityName: "AIUsageEntity")
    }
    
    @NSManaged public var id: Int64
    @NSManaged public var userId: String // Default: "local"
    @NSManaged public var lastUsedEpoch: Int64
    @NSManaged public var weeklyQuota: Int64 // Free users: 2
    @NSManaged public var usedThisWeek: Int64
    
}

extension AIUsageEntity : Identifiable {
    
}
