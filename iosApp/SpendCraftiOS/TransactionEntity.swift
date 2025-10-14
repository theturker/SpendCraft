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

// MARK: - Identifiable Conformance
extension TransactionEntity: Identifiable {
    // id property already exists (Int64), just need to conform to Identifiable
}

extension TransactionEntity {
    var amount: Double {
        return Double(amountMinor) / 100.0
    }
    
    var formattedAmount: String {
        let currencySymbol = getCurrentCurrencySymbol()
        let currencyCode = getCurrentCurrencyCode()
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        
        // Türk Lirası için özel format
        if currencyCode == "TRY" {
            formatter.locale = Locale(identifier: "tr_TR")
            formatter.groupingSeparator = "."
            formatter.decimalSeparator = ","
        } else {
            formatter.locale = Locale(identifier: "en_US")
            formatter.groupingSeparator = ","
            formatter.decimalSeparator = "."
        }
        
        let formattedNumber = formatter.string(from: NSNumber(value: amount)) ?? "0.00"
        return "\(isIncome ? "+" : "-")\(formattedNumber) \(currencySymbol)"
    }
    
    var formattedDate: String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestampUtcMillis / 1000))
        let formatter = DateFormatter()
        formatter.dateFormat = "d MMMM yyyy"
        formatter.locale = Locale(identifier: "tr_TR")
        return formatter.string(from: date)
    }
}
