//
//  RecurringTransactionEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData

extension RecurringTransactionEntity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<RecurringTransactionEntity> {
        return NSFetchRequest<RecurringTransactionEntity>(entityName: "RecurringTransactionEntity")
    }

    @NSManaged public var id: Int64
    @NSManaged public var name: String
    @NSManaged public var amount: Int64
    @NSManaged public var categoryId: Int64
    @NSManaged public var accountId: Int64
    @NSManaged public var isIncome: Bool
    @NSManaged public var frequency: String
    @NSManaged public var startDate: Int64
    @NSManaged public var endDate: Int64
    @NSManaged public var isActive: Bool
    @NSManaged public var lastExecuted: Int64
    @NSManaged public var nextExecution: Int64
    @NSManaged public var note: String?

}

extension RecurringTransactionEntity : Identifiable {

}