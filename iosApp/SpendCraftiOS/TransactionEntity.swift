import Foundation
import CoreData

@objc(TransactionEntity)
public class TransactionEntity: NSManagedObject {
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
