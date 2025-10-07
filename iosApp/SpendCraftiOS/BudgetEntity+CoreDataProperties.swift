//
//  BudgetEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData

extension BudgetEntity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<BudgetEntity> {
        return NSFetchRequest<BudgetEntity>(entityName: "BudgetEntity")
    }

    @NSManaged public var id: Int64
    @NSManaged public var categoryId: String
    @NSManaged public var monthlyLimitMinor: Int64
    @NSManaged public var category: CategoryEntity?

}

extension BudgetEntity : Identifiable {

}