//
//  TransactionEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData

extension TransactionEntity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<TransactionEntity> {
        return NSFetchRequest<TransactionEntity>(entityName: "TransactionEntity")
    }

    @NSManaged public var id: Int64
    @NSManaged public var amountMinor: Int64
    @NSManaged public var timestampUtcMillis: Int64
    @NSManaged public var note: String?
    @NSManaged public var categoryId: Int64
    @NSManaged public var accountId: Int64
    @NSManaged public var isIncome: Bool
    @NSManaged public var category: CategoryEntity?
    @NSManaged public var account: AccountEntity?

}

extension TransactionEntity : Identifiable {

}
