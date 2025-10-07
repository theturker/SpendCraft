//
//  DailyEntryEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData

extension DailyEntryEntity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<DailyEntryEntity> {
        return NSFetchRequest<DailyEntryEntity>(entityName: "DailyEntryEntity")
    }

    @NSManaged public var id: Int64
    @NSManaged public var date: String
    @NSManaged public var dayOfWeek: Int64
    @NSManaged public var totalIncome: Int64
    @NSManaged public var totalExpense: Int64
    @NSManaged public var transactionCount: Int64

}

extension DailyEntryEntity : Identifiable {

}
