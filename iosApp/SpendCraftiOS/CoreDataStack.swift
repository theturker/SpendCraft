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
        }
        container.viewContext.automaticallyMergesChangesFromParent = true
        return container
    }()

    func saveContext() {
        let context = container.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }

    func seedInitialData() {
        let context = container.viewContext
        let fetchRequest: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest() as! NSFetchRequest<CategoryEntity>

        do {
            let count = try context.count(for: fetchRequest)
            if count == 0 {
                // Seed initial categories
                let categories = [
                    ("Gıda", "#FF6347", "fork.knife"),
                    ("Ulaşım", "#4682B4", "car.fill"),
                    ("Fatura", "#DAA520", "doc.text.fill"),
                    ("Eğlence", "#9370DB", "gamecontroller.fill"),
                    ("Alışveriş", "#3CB371", "cart.fill"),
                    ("Sağlık", "#FF69B4", "heart.fill"),
                    ("Eğitim", "#8B4513", "book.closed.fill"),
                    ("Kredi", "#DC143C", "creditcard.fill"),
                    ("Maaş", "#008000", "banknote.fill"),
                    ("Diğer", "#808080", "ellipsis.circle.fill")
                ]

                for (name, color, icon) in categories {
                    let category = CategoryEntity(context: context)
                    category.id = Int64.random(in: 1...10000)
                    category.name = name
                    category.color = color
                    category.icon = icon
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
