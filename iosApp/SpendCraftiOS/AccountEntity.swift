import Foundation
import CoreData

@objc(AccountEntity)
public class AccountEntity: NSManagedObject {
    @NSManaged public var id: Int64
    @NSManaged public var name: String
    @NSManaged public var type: String
    @NSManaged public var currency: String
    @NSManaged public var isDefault: Bool
    @NSManaged public var archived: Bool
    @NSManaged public var transactions: NSSet?
}
