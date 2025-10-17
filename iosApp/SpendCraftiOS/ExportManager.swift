//
//  ExportManager.swift
//  SpendCraftiOS
//
//  Export/Import Yöneticisi
//

import Foundation
import CoreData
import PDFKit
import UIKit

// MARK: - Backup Data Models

struct BackupData: Codable {
    let version: Int
    let exportDate: Date
    let transactions: [BackupTransaction]
    let categories: [BackupCategory]
    let accounts: [BackupAccount]
    
    static let currentVersion = 1
}

struct BackupTransaction: Codable {
    let id: Int64
    let amountMinor: Int64
    let timestampUtcMillis: Int64
    let note: String?
    let categoryId: Int64
    let accountId: Int64
    let isIncome: Bool
}

struct BackupCategory: Codable {
    let id: Int64
    let name: String
    let icon: String?
    let color: String?
    let type: String?
}

struct BackupAccount: Codable {
    let id: Int64
    let name: String
    let type: String?
    let currency: String?
    let isDefault: Bool
    let archived: Bool
}

class ExportManager {
    
    // MARK: - JSON Backup Export/Import
    
    static func exportToJSON(transactions: [TransactionEntity], categories: [CategoryEntity], accounts: [AccountEntity]) -> URL? {
        // Convert entities to backup models
        let backupTransactions = transactions.map { transaction in
            BackupTransaction(
                id: transaction.id,
                amountMinor: transaction.amountMinor,
                timestampUtcMillis: transaction.timestampUtcMillis,
                note: transaction.note,
                categoryId: transaction.categoryId,
                accountId: transaction.accountId,
                isIncome: transaction.isIncome
            )
        }
        
        let backupCategories = categories.map { category in
            BackupCategory(
                id: category.id,
                name: category.name ?? "",
                icon: category.icon,
                color: category.color,
                type: category.type
            )
        }
        
        let backupAccounts = accounts.map { account in
            BackupAccount(
                id: account.id,
                name: account.name ?? "",
                type: account.type,
                currency: account.currency,
                isDefault: account.isDefault,
                archived: account.archived
            )
        }
        
        let backupData = BackupData(
            version: BackupData.currentVersion,
            exportDate: Date(),
            transactions: backupTransactions,
            categories: backupCategories,
            accounts: backupAccounts
        )
        
        do {
            let encoder = JSONEncoder()
            encoder.dateEncodingStrategy = .iso8601
            encoder.outputFormatting = [.prettyPrinted, .sortedKeys]
            let jsonData = try encoder.encode(backupData)
            
            let fileName = "spendcraft_backup_\(Date().timeIntervalSince1970).json"
            return saveToFile(data: jsonData, fileName: fileName)
        } catch {
            print("❌ JSON export failed: \(error)")
            return nil
        }
    }
    
    static func importFromJSON(url: URL, context: NSManagedObjectContext, replaceExisting: Bool = false) -> (success: Int, failed: Int, message: String) {
        do {
            let jsonData = try Data(contentsOf: url)
            let decoder = JSONDecoder()
            decoder.dateDecodingStrategy = .iso8601
            let backupData = try decoder.decode(BackupData.self, from: jsonData)
            
            var successCount = 0
            var failedCount = 0
            
            // If replace existing, clear all data first
            if replaceExisting {
                clearAllData(context: context)
            }
            
            // Import categories first
            var categoryMap: [Int64: CategoryEntity] = [:]
            for backupCategory in backupData.categories {
                // Check if category with same ID already exists
                let fetchRequest: NSFetchRequest<CategoryEntity> = CategoryEntity.fetchRequest() as! NSFetchRequest<CategoryEntity>
                fetchRequest.predicate = NSPredicate(format: "id == %lld", backupCategory.id)
                
                let existingCategories = try context.fetch(fetchRequest)
                let category: CategoryEntity
                
                if let existing = existingCategories.first, !replaceExisting {
                    category = existing
                } else {
                    category = CategoryEntity(context: context)
                    category.id = backupCategory.id
                }
                
                category.name = backupCategory.name
                category.icon = backupCategory.icon
                category.color = backupCategory.color!
                category.type = backupCategory.type
                
                categoryMap[backupCategory.id] = category
                successCount += 1
            }
            
            // Import accounts
            var accountMap: [Int64: AccountEntity] = [:]
            for backupAccount in backupData.accounts {
                let fetchRequest: NSFetchRequest<AccountEntity> = AccountEntity.fetchRequest() as! NSFetchRequest<AccountEntity>
                fetchRequest.predicate = NSPredicate(format: "id == %lld", backupAccount.id)
                
                let existingAccounts = try context.fetch(fetchRequest)
                let account: AccountEntity
                
                if let existing = existingAccounts.first, !replaceExisting {
                    account = existing
                } else {
                    account = AccountEntity(context: context)
                    account.id = backupAccount.id
                }
                
                account.name = backupAccount.name
                account.type = backupAccount.type!
                account.currency = backupAccount.currency!
                account.isDefault = backupAccount.isDefault
                account.archived = backupAccount.archived
                
                accountMap[backupAccount.id] = account
                successCount += 1
            }
            
            // Import transactions
            for backupTransaction in backupData.transactions {
                let transaction = TransactionEntity(context: context)
                transaction.id = backupTransaction.id
                transaction.amountMinor = backupTransaction.amountMinor
                transaction.timestampUtcMillis = backupTransaction.timestampUtcMillis
                transaction.note = backupTransaction.note
                transaction.categoryId = backupTransaction.categoryId
                transaction.accountId = backupTransaction.accountId
                transaction.isIncome = backupTransaction.isIncome
                transaction.category = categoryMap[backupTransaction.categoryId]
                transaction.account = accountMap[backupTransaction.accountId]
                
                successCount += 1
            }
            
            try context.save()
            
            let message = "✅ Yedekleme başarıyla içe aktarıldı!\n\n📊 \(backupData.transactions.count) işlem\n📁 \(backupData.categories.count) kategori\n💳 \(backupData.accounts.count) hesap"
            return (successCount, failedCount, message)
            
        } catch {
            print("❌ JSON import failed: \(error)")
            return (0, 0, "❌ İçe aktarım başarısız: \(error.localizedDescription)")
        }
    }
    
    private static func clearAllData(context: NSManagedObjectContext) {
        // Delete all transactions
        let transactionFetch: NSFetchRequest<NSFetchRequestResult> = TransactionEntity.fetchRequest()
        let transactionDelete = NSBatchDeleteRequest(fetchRequest: transactionFetch)
        
        // Delete all categories
        let categoryFetch: NSFetchRequest<NSFetchRequestResult> = CategoryEntity.fetchRequest()
        let categoryDelete = NSBatchDeleteRequest(fetchRequest: categoryFetch)
        
        // Delete all accounts
        let accountFetch: NSFetchRequest<NSFetchRequestResult> = AccountEntity.fetchRequest()
        let accountDelete = NSBatchDeleteRequest(fetchRequest: accountFetch)
        
        do {
            try context.execute(transactionDelete)
            try context.execute(categoryDelete)
            try context.execute(accountDelete)
            try context.save()
            print("✅ All data cleared successfully")
        } catch {
            print("❌ Failed to clear data: \(error)")
        }
    }
    
    // MARK: - CSV Export
    
    static func exportToCSV(transactions: [TransactionEntity]) -> URL? {
        var csvString = "Tarih,Kategori,Tutar,Not,Tür\n"
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy HH:mm"
        
        for transaction in transactions.sorted(by: { $0.timestampUtcMillis > $1.timestampUtcMillis }) {
            let date = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis / 1000))
            let dateString = dateFormatter.string(from: date)
            let category = transaction.category?.name ?? "Diğer"
            let amount = String(format: "%.2f", transaction.amount)
            let note = (transaction.note ?? "").replacingOccurrences(of: ",", with: ";")
            let type = transaction.isIncome ? "Gelir" : "Gider"
            
            csvString += "\(dateString),\(category),\(amount),\(note),\(type)\n"
        }
        
        return saveToFile(content: csvString, fileName: "transactions_\(Date().timeIntervalSince1970).csv")
    }
    
    // MARK: - PDF Export
    
    static func exportToPDF(
        transactions: [TransactionEntity],
        totalIncome: Double,
        totalExpense: Double,
        balance: Double
    ) -> URL? {
        let pdfMetaData = [
            kCGPDFContextTitle: "Finansal Rapor",
            kCGPDFContextAuthor: "Paratik",
            kCGPDFContextCreator: "Paratik iOS"
        ]
        
        let format = UIGraphicsPDFRendererFormat()
        format.documentInfo = pdfMetaData as [String: Any]
        
        let pageWidth = 8.5 * 72.0
        let pageHeight = 11 * 72.0
        let pageRect = CGRect(x: 0, y: 0, width: pageWidth, height: pageHeight)
        
        let renderer = UIGraphicsPDFRenderer(bounds: pageRect, format: format)
        
        let data = renderer.pdfData { context in
            context.beginPage()
            
            var yOffset: CGFloat = 50
            
            // Title
            let titleFont = UIFont.boldSystemFont(ofSize: 24)
            let titleAttributes: [NSAttributedString.Key: Any] = [
                .font: titleFont,
                .foregroundColor: UIColor.black
            ]
            let title = "Finansal Rapor"
            title.draw(at: CGPoint(x: 50, y: yOffset), withAttributes: titleAttributes)
            yOffset += 40
            
            // Date
            let dateFormatter = DateFormatter()
            dateFormatter.dateStyle = .long
            let dateString = "Rapor Tarihi: \(dateFormatter.string(from: Date()))"
            let normalFont = UIFont.systemFont(ofSize: 12)
            let normalAttributes: [NSAttributedString.Key: Any] = [
                .font: normalFont,
                .foregroundColor: UIColor.gray
            ]
            dateString.draw(at: CGPoint(x: 50, y: yOffset), withAttributes: normalAttributes)
            yOffset += 30
            
            // Summary
            let summaryFont = UIFont.boldSystemFont(ofSize: 16)
            let summaryAttributes: [NSAttributedString.Key: Any] = [
                .font: summaryFont,
                .foregroundColor: UIColor.black
            ]
            
            "Özet".draw(at: CGPoint(x: 50, y: yOffset), withAttributes: summaryAttributes)
            yOffset += 25
            
            let bodyFont = UIFont.systemFont(ofSize: 14)
            let bodyAttributes: [NSAttributedString.Key: Any] = [
                .font: bodyFont,
                .foregroundColor: UIColor.darkGray
            ]
            
            "Toplam Gelir: ₺\(String(format: "%.2f", totalIncome))".draw(
                at: CGPoint(x: 70, y: yOffset),
                withAttributes: bodyAttributes
            )
            yOffset += 20
            
            "Toplam Gider: ₺\(String(format: "%.2f", totalExpense))".draw(
                at: CGPoint(x: 70, y: yOffset),
                withAttributes: bodyAttributes
            )
            yOffset += 20
            
            "Net Bakiye: ₺\(String(format: "%.2f", balance))".draw(
                at: CGPoint(x: 70, y: yOffset),
                withAttributes: bodyAttributes
            )
            yOffset += 40
            
            // Transactions Header
            "İşlemler (\(transactions.count))".draw(
                at: CGPoint(x: 50, y: yOffset),
                withAttributes: summaryAttributes
            )
            yOffset += 25
            
            // Transaction list
            let transactionDateFormatter = DateFormatter()
            transactionDateFormatter.dateFormat = "dd/MM/yyyy HH:mm"
            
            for (index, transaction) in transactions.sorted(by: { $0.timestampUtcMillis > $1.timestampUtcMillis }).enumerated() {
                if yOffset > pageHeight - 100 {
                    context.beginPage()
                    yOffset = 50
                }
                
                let date = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis / 1000))
                let dateStr = transactionDateFormatter.string(from: date)
                let category = transaction.category?.name ?? "Diğer"
                let amount = String(format: "%.2f", transaction.amount)
                let type = transaction.isIncome ? "Gelir" : "Gider"
                
                let transactionText = "\(index + 1). \(dateStr) - \(category) - ₺\(amount) (\(type))"
                transactionText.draw(
                    at: CGPoint(x: 70, y: yOffset),
                    withAttributes: bodyAttributes
                )
                yOffset += 18
            }
        }
        
        return saveToFile(data: data, fileName: "report_\(Date().timeIntervalSince1970).pdf")
    }
    
    // MARK: - CSV Import
    
    static func importFromCSV(url: URL, context: NSManagedObjectContext, categories: [CategoryEntity], accounts: [AccountEntity]) -> (success: Int, failed: Int) {
        var successCount = 0
        var failedCount = 0
        
        guard let csvString = try? String(contentsOf: url, encoding: .utf8) else {
            return (0, 0)
        }
        
        let rows = csvString.components(separatedBy: "\n")
        
        // Skip header
        for row in rows.dropFirst() {
            guard !row.isEmpty else { continue }
            
            let columns = row.components(separatedBy: ",")
            guard columns.count >= 5 else {
                failedCount += 1
                continue
            }
            
            // Parse date
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "dd/MM/yyyy HH:mm"
            guard let date = dateFormatter.date(from: columns[0]) else {
                failedCount += 1
                continue
            }
            
            // Find category
            let categoryName = columns[1]
            let category = categories.first(where: { $0.name == categoryName }) ?? categories.first
            
            // Parse amount
            guard let amount = Double(columns[2]) else {
                failedCount += 1
                continue
            }
            
            let note = columns[3]
            let isIncome = columns[4].trimmingCharacters(in: .whitespaces) == "Gelir"
            
            // Create transaction
            let transaction = TransactionEntity(context: context)
            transaction.id = Int64.random(in: 1...1000000)
            transaction.amountMinor = Int64(amount * 100)
            transaction.timestampUtcMillis = Int64(date.timeIntervalSince1970 * 1000)
            transaction.note = note.isEmpty ? nil : note
            transaction.categoryId = category?.id ?? 0
            transaction.accountId = accounts.first?.id ?? 0
            transaction.isIncome = isIncome
            transaction.category = category
            transaction.account = accounts.first
            
            successCount += 1
        }
        
        do {
            try context.save()
        } catch {
            print("❌ CSV import save failed: \(error)")
            return (0, failedCount + successCount)
        }
        
        return (successCount, failedCount)
    }
    
    // MARK: - Helper Methods
    
    private static func saveToFile(content: String, fileName: String) -> URL? {
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent(fileName)
        
        do {
            try content.write(to: fileURL, atomically: true, encoding: .utf8)
            return fileURL
        } catch {
            print("❌ Save to file failed: \(error)")
            return nil
        }
    }
    
    private static func saveToFile(data: Data, fileName: String) -> URL? {
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent(fileName)
        
        do {
            try data.write(to: fileURL)
            return fileURL
        } catch {
            print("❌ Save to file failed: \(error)")
            return nil
        }
    }
}
