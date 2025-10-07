//
//  CategoryEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData

extension CategoryEntity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<CategoryEntity> {
        return NSFetchRequest<CategoryEntity>(entityName: "CategoryEntity")
    }

    @NSManaged public var id: Int64
    @NSManaged public var name: String
    @NSManaged public var color: String
    @NSManaged public var icon: String?
    @NSManaged public var transactions: NSSet?
    @NSManaged public var budgets: NSSet?

}

// MARK: Generated accessors for transactions
extension CategoryEntity {

    @objc(addTransactionsObject:)
    @NSManaged public func addToTransactions(_ value: TransactionEntity)

    @objc(removeTransactionsObject:)
    @NSManaged public func removeFromTransactions(_ value: TransactionEntity)

    @objc(addTransactions:)
    @NSManaged public func addToTransactions(_ values: NSSet)

    @objc(removeTransactions:)
    @NSManaged public func removeFromTransactions(_ values: NSSet)

}

// MARK: Generated accessors for budgets
extension CategoryEntity {

    @objc(addBudgetsObject:)
    @NSManaged public func addToBudgets(_ value: BudgetEntity)

    @objc(removeBudgetsObject:)
    @NSManaged public func removeFromBudgets(_ value: BudgetEntity)

    @objc(addBudgets:)
    @NSManaged public func addToBudgets(_ values: NSSet)

    @objc(removeBudgets:)
    @NSManaged public func removeFromBudgets(_ values: NSSet)

}

extension CategoryEntity : Identifiable {

}
