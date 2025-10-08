//
//  BudgetAlertEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Bütçe uyarı kayıtları özellikleri
//

import Foundation
import CoreData

extension BudgetAlertEntity {
    
    @nonobjc public class func fetchRequest() -> NSFetchRequest<BudgetAlertEntity> {
        return NSFetchRequest<BudgetAlertEntity>(entityName: "BudgetAlertEntity")
    }
    
    @NSManaged public var id: String?
    @NSManaged public var categoryId: String
    @NSManaged public var level: Int64 // 80 or 100 (percentage)
    @NSManaged public var monthKey: String // Format: "YYYY-MM"
    
}

extension BudgetAlertEntity : Identifiable {
    
}
