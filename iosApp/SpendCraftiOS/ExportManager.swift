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

class ExportManager {
    
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
