import Foundation
import CoreData
import shared

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
    
    /// NOW USES SHARED KMP FORMATTER! 🎉
    var formattedAmount: String {
        let currencyCode = getCurrentCurrencyCode()
        
        // Delegate to shared KMP formatter
        return shared.CurrencyFormatter.shared.format(
            minorUnits: amountMinor,
            currencyCode: currencyCode,
            showSign: true,
            isIncome: isIncome
        )
    }
    
    /// NOW USES SHARED KMP FORMATTER! 🎉
    var formattedDate: String {
        // Delegate to shared KMP DateTimeFormatter
        let currentLanguage = LanguageHelper.shared.getCurrentLanguage()
        let locale = currentLanguage == "en" ? "en_US" : "tr_TR"
        return shared.DateTimeFormatter.shared.format(
            timestampMillis: timestampUtcMillis,
            format: .medium,
            locale: locale
        )
    }
    
    /// NEW: Relative time format ("5 dakika önce")
    var formattedDateRelative: String {
        return shared.DateTimeFormatter.shared.formatRelative(
            timestampMillis: timestampUtcMillis
        )
    }
}
