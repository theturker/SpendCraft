//
//  RecurringRuleEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Gelişmiş tekrarlayan işlem kuralları özellikleri
//

import Foundation
import CoreData

extension RecurringRuleEntity {
    
    @nonobjc public class func fetchRequest() -> NSFetchRequest<RecurringRuleEntity> {
        return NSFetchRequest<RecurringRuleEntity>(entityName: "RecurringRuleEntity")
    }
    
    @NSManaged public var id: Int64
    @NSManaged public var templateTransactionId: Int64 // FK to transactions
    @NSManaged public var frequency: String // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    @NSManaged public var interval: Int64 // Every N days/weeks/months/years
    @NSManaged public var nextRunEpoch: Int64
    @NSManaged public var lastRunEpoch: Int64
    @NSManaged public var endEpoch: Int64
    @NSManaged public var isActive: Bool
    
}

extension RecurringRuleEntity : Identifiable {
    
}
