import Foundation
import CoreData

@objc(CategoryEntity)
public class CategoryEntity: NSManagedObject {
    @NSManaged public var id: Int64
    @NSManaged public var name: String
    @NSManaged public var color: String
    @NSManaged public var icon: String?
    @NSManaged public var type: String? // "income" veya "expense"
    @NSManaged public var transactions: NSSet?
    @NSManaged public var budgets: NSSet?
}

extension CategoryEntity {
    var isIncomeCategory: Bool {
        return type == "income"
    }
    
    var isExpenseCategory: Bool {
        return type == "expense"
    }
}
