import CoreData
import Foundation
import UIKit
import SwiftUI

class CoreDataStack: ObservableObject {
    static let shared = CoreDataStack()
    
    init() {}

    lazy var container: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "SpendCraft")
        container.loadPersistentStores { storeDescription, error in
            if let error = error as NSError? {
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
            print("💾 CoreData store loaded: \(storeDescription.url?.path ?? "unknown")")
        }
        container.viewContext.automaticallyMergesChangesFromParent = true
        container.viewContext.mergePolicy = NSMergeByPropertyObjectTrumpMergePolicy
        return container
    }()

    func saveContext() {
        let context = container.viewContext
        if context.hasChanges {
            do {
                try context.save()
                print("💾 CoreDataStack: Context saved successfully")
                
                // Force synchronous save to disk
                try container.viewContext.parent?.save()
            } catch {
                let nserror = error as NSError
                print("❌ CoreDataStack: Failed to save context: \(nserror), \(nserror.userInfo)")
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        } else {
            print("💾 CoreDataStack: No changes to save")
        }
    }

    func migrateExistingCategories() {
        let context = container.viewContext
        let fetchRequest: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest() as! NSFetchRequest<CategoryEntity>
        
        do {
            let existingCategories = try context.fetch(fetchRequest)
            var migrated = false
            
            // SADECE type'ı olmayan kategorileri migrate et
            for category in existingCategories {
                // Eğer kategori zaten bir type'a sahipse, DOKUNMA!
                if let existingType = category.type, !existingType.isEmpty {
                    continue
                }
                
                // Sadece type'ı olmayan kategoriler için tahmin et
                let name = category.name.lowercased() ?? ""
                var newType: String?
                
                // Gelir kategorilerini tespit et
                if name.contains("maaş") || name.contains("gelir") || name.contains("yatırım") || 
                   name.contains("ikramiye") || name.contains("serbest") || name.contains("kira") || 
                   name.contains("prim") || name == "maaş" {
                    newType = "income"
                    print("🔵 Migrating '\(category.name ?? "")' -> income")
                } else {
                    newType = "expense"
                    print("🔴 Migrating '\(category.name ?? "")' -> expense")
                }
                
                category.type = newType
                migrated = true
            }
            
            if migrated {
                try context.save()
                print("✅ Categories migrated successfully")
            } else {
                print("ℹ️ No migration needed, all categories have correct types")
            }
        } catch {
            print("❌ Error migrating categories: \(error)")
        }
    }
    
    func clearAllUserData() {
        let context = container.viewContext
        
        // Delete all transactions
        let transactionFetch: NSFetchRequest<NSFetchRequestResult> = TransactionEntity.fetchRequest()
        let transactionDelete = NSBatchDeleteRequest(fetchRequest: transactionFetch)
        
        // Delete all categories
        let categoryFetch: NSFetchRequest<NSFetchRequestResult> = CategoryEntity.fetchRequest()
        let categoryDelete = NSBatchDeleteRequest(fetchRequest: categoryFetch)
        
        // Delete all accounts
        let accountFetch: NSFetchRequest<NSFetchRequestResult> = AccountEntity.fetchRequest()
        let accountDelete = NSBatchDeleteRequest(fetchRequest: accountFetch)
        
        // Delete all budgets
        let budgetFetch: NSFetchRequest<NSFetchRequestResult> = BudgetEntity.fetchRequest()
        let budgetDelete = NSBatchDeleteRequest(fetchRequest: budgetFetch)
        
        // Delete all achievements
        let achievementFetch: NSFetchRequest<NSFetchRequestResult> = AchievementEntity.fetchRequest()
        let achievementDelete = NSBatchDeleteRequest(fetchRequest: achievementFetch)
        
        // Delete all recurring transactions
        let recurringFetch: NSFetchRequest<NSFetchRequestResult> = RecurringTransactionEntity.fetchRequest()
        let recurringDelete = NSBatchDeleteRequest(fetchRequest: recurringFetch)
        
        do {
            try context.execute(transactionDelete)
            try context.execute(categoryDelete)
            try context.execute(accountDelete)
            try context.execute(budgetDelete)
            try context.execute(achievementDelete)
            try context.execute(recurringDelete)
            try context.save()
            
            // Refresh context to reflect changes
            context.refreshAllObjects()
            
            print("✅ All user data cleared successfully")
        } catch {
            print("❌ Failed to clear user data: \(error)")
        }
    }
    
    func seedInitialData() {
        let context = container.viewContext
        let fetchRequest: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest() as! NSFetchRequest<CategoryEntity>

        do {
            let count = try context.count(for: fetchRequest)
            
            // Önce mevcut kategorileri migrate et
            if count > 0 {
                migrateExistingCategories()
            }
            
            if count == 0 {
                // Seed initial categories
                let categories: [(name: String, color: String, icon: String, type: String)] = [
                    // Gider Kategorileri
                    ("Gıda", "#FF6347", "fork.knife", "expense"),
                    ("Ulaşım", "#4682B4", "car.fill", "expense"),
                    ("Fatura", "#DAA520", "doc.text.fill", "expense"),
                    ("Eğlence", "#9370DB", "gamecontroller.fill", "expense"),
                    ("Alışveriş", "#3CB371", "cart.fill", "expense"),
                    ("Sağlık", "#FF69B4", "heart.fill", "expense"),
                    ("Eğitim", "#8B4513", "book.closed.fill", "expense"),
                    ("Kredi", "#DC143C", "creditcard.fill", "expense"),
                    ("Diğer Gider", "#808080", "ellipsis.circle.fill", "expense"),
                    
                    // Gelir Kategorileri
                    ("Maaş", "#008000", "banknote.fill", "income"),
                    ("Kira", "#32CD32", "house.fill", "income"),
                    ("Prim", "#FFD700", "star.fill", "income"),
                    ("Yatırım", "#4169E1", "chart.line.uptrend.xyaxis", "income"),
                    ("İkramiye", "#FFA500", "gift.fill", "income"),
                    ("Serbest Çalışma", "#9370DB", "briefcase.fill", "income"),
                    ("Kira Geliri", "#20B2AA", "building.2.fill", "income"),
                    ("Diğer Gelir", "#808080", "ellipsis.circle.fill", "income")
                ]

                for category in categories {
                    let categoryEntity = CategoryEntity(context: context)
                    categoryEntity.id = Int64.random(in: 1...10000)
                    categoryEntity.name = category.name
                    categoryEntity.color = category.color
                    categoryEntity.icon = category.icon
                    categoryEntity.type = category.type
                }

                // Seed initial account
                let accountFetchRequest: NSFetchRequest<AccountEntity> = AccountEntity.fetchRequest() as! NSFetchRequest<AccountEntity>
                let accountCount = try context.count(for: accountFetchRequest)
                if accountCount == 0 {
                    let account = AccountEntity(context: context)
                    account.id = Int64.random(in: 1...10000)
                    account.name = "Nakit"
                    account.type = "CASH"
                    account.currency = "TRY"
                    account.isDefault = true
                    account.archived = false
                }

                try context.save()
                print("✅ Initial data seeded.")
            }
        } catch {
            print("❌ Error seeding initial data: \(error)")
        }
    }
}

// Extensions for convenience
extension CategoryEntity {
    var uiColor: Color {
        return Color(UIColor(hex: color ?? "#000000") ?? UIColor.black)
    }
}

extension UIColor {
    convenience init?(hex: String) {
        let r, g, b: CGFloat
        
        if hex.hasPrefix("#") {
            let start = hex.index(hex.startIndex, offsetBy: 1)
            let hexColor = String(hex[start...])
            
            if hexColor.count == 6 {
                let scanner = Scanner(string: hexColor)
                var hexNumber: UInt64 = 0
                
                if scanner.scanHexInt64(&hexNumber) {
                    r = CGFloat((hexNumber & 0xff0000) >> 16) / 255
                    g = CGFloat((hexNumber & 0x00ff00) >> 8) / 255
                    b = CGFloat(hexNumber & 0x0000ff) / 255
                    
                    self.init(red: r, green: g, blue: b, alpha: 1)
                    return
                }
            }
        }
        
        return nil
    }
}

// MARK: - Currency Helper Functions

/// Para birimi sembolünü al
func getCurrentCurrencySymbol() -> String {
    return UserDefaults.standard.string(forKey: "selectedCurrencySymbol") ?? "₺"
}

/// Para birimi kodunu al
func getCurrentCurrencyCode() -> String {
    return UserDefaults.standard.string(forKey: "selectedCurrency") ?? "TRY"
}

/// Tutarı seçilen para birimine göre formatla
func formatCurrency(_ amount: Double) -> String {
    let selectedCurrency = getCurrentCurrencyCode()
    let selectedSymbol = getCurrentCurrencySymbol()
    
    let formatter = NumberFormatter()
    formatter.numberStyle = .decimal
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    
    // Türk Lirası için özel format
    if selectedCurrency == "TRY" {
        formatter.locale = Locale(identifier: "tr_TR")
        formatter.groupingSeparator = "."
        formatter.decimalSeparator = ","
    } else {
        // Diğer para birimleri için varsayılan format
        formatter.locale = Locale(identifier: "en_US")
        formatter.groupingSeparator = ","
        formatter.decimalSeparator = "."
    }
    
    let formattedNumber = formatter.string(from: NSNumber(value: abs(amount))) ?? "0.00"
    return "\(formattedNumber) \(selectedSymbol)"
}
