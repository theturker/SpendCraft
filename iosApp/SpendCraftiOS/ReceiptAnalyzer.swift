//
//  ReceiptAnalyzer.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import Vision
import UIKit

struct ReceiptAnalysisResult {
    let amount: Double?
    let merchant: String?
    let date: Date?
    let items: [String]
    let rawText: String
}

class ReceiptAnalyzer {
    static let shared = ReceiptAnalyzer()
    
    private init() {}
    
    func analyzeReceipt(image: UIImage, completion: @escaping (ReceiptAnalysisResult) -> Void) {
        guard let cgImage = image.cgImage else {
            completion(ReceiptAnalysisResult(amount: nil, merchant: nil, date: nil, items: [], rawText: ""))
            return
        }
        
        let requestHandler = VNImageRequestHandler(cgImage: cgImage, options: [:])
        let request = VNRecognizeTextRequest { request, error in
            guard let observations = request.results as? [VNRecognizedTextObservation] else {
                completion(ReceiptAnalysisResult(amount: nil, merchant: nil, date: nil, items: [], rawText: ""))
                return
            }
            
            var recognizedStrings: [String] = []
            for observation in observations {
                guard let topCandidate = observation.topCandidates(1).first else {
                    continue
                }
                recognizedStrings.append(topCandidate.string)
            }
            
            let fullText = recognizedStrings.joined(separator: "\n")
            let result = self.parseReceiptText(fullText)
            
            DispatchQueue.main.async {
                completion(result)
            }
        }
        
        // Türkçe ve İngilizce için optimize edilmiş OCR
        request.recognitionLanguages = ["tr-TR", "en-US"]
        request.recognitionLevel = .accurate
        request.usesLanguageCorrection = true
        
        do {
            try requestHandler.perform([request])
        } catch {
            print("OCR Error: \(error)")
            completion(ReceiptAnalysisResult(amount: nil, merchant: nil, date: nil, items: [], rawText: ""))
        }
    }
    
    private func parseReceiptText(_ text: String) -> ReceiptAnalysisResult {
        let lines = text.components(separatedBy: .newlines)
        var amount: Double? = nil
        var merchant: String? = nil
        var date: Date? = nil
        var items: [String] = []
        
        // Tutar bulma - Türkçe ve İngilizce formatlar
        let amountPatterns = [
            #"TOPLAM[:\s]*([\d,\.]+)"#,  // TOPLAM: 125.50
            #"TOTAL[:\s]*([\d,\.]+)"#,   // TOTAL: 125.50
            #"GENEL[:\s]*TOPLAM[:\s]*([\d,\.]+)"#,  // GENEL TOPLAM: 125.50
            #"KDV[:\s]*DAHİL[:\s]*([\d,\.]+)"#,     // KDV DAHİL: 125.50
            #"([\d,\.]+)\s*TL"#,         // 125.50 TL
            #"([\d,\.]+)\s*TRY"#,        // 125.50 TRY
            #"([\d,\.]+)\s*₺"#,          // 125.50 ₺
            #"([\d]{1,3}(?:\.[\d]{3})*(?:,[\d]{2})?)\s*(?:TL|TRY|₺)"#,  // 1.234,56 TL
        ]
        
        for pattern in amountPatterns {
            if let regex = try? NSRegularExpression(pattern: pattern, options: .caseInsensitive) {
                let range = NSRange(text.startIndex..., in: text)
                if let match = regex.firstMatch(in: text, options: [], range: range) {
                    if let amountRange = Range(match.range(at: 1), in: text) {
                        let amountString = String(text[amountRange])
                            .replacingOccurrences(of: ".", with: "")
                            .replacingOccurrences(of: ",", with: ".")
                        if let parsedAmount = Double(amountString) {
                            amount = parsedAmount
                            break
                        }
                    }
                }
            }
        }
        
        // Eğer direkt tutar bulunamazsa, en büyük sayıyı bul
        if amount == nil {
            let numberPattern = #"([\d]{1,3}(?:\.[\d]{3})*(?:,[\d]{2})?)"#
            if let regex = try? NSRegularExpression(pattern: numberPattern, options: []) {
                let range = NSRange(text.startIndex..., in: text)
                let matches = regex.matches(in: text, options: [], range: range)
                var maxAmount: Double? = nil
                for match in matches {
                    if let amountRange = Range(match.range, in: text) {
                        let amountString = String(text[amountRange])
                            .replacingOccurrences(of: ".", with: "")
                            .replacingOccurrences(of: ",", with: ".")
                        if let parsedAmount = Double(amountString), parsedAmount > 0 {
                            if maxAmount == nil || parsedAmount > maxAmount! {
                                maxAmount = parsedAmount
                            }
                        }
                    }
                }
                amount = maxAmount
            }
        }
        
        // İşyeri adı bulma - genellikle ilk satırlarda
        let merchantPatterns = [
            #"^([A-ZÇĞİÖŞÜ][A-ZÇĞİÖŞÜ\s]+)"#,  // Büyük harflerle başlayan satır
        ]
        
        for (index, line) in lines.prefix(5).enumerated() {
            let trimmedLine = line.trimmingCharacters(in: .whitespaces)
            if trimmedLine.count > 3 && trimmedLine.count < 50 {
                // İlk birkaç satırdan birini işyeri olarak al
                if index < 3 && merchant == nil {
                    merchant = trimmedLine
                }
            }
        }
        
        // Tarih bulma
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "tr_TR")
        let datePatterns = [
            "dd.MM.yyyy",
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "dd MMMM yyyy",
            "dd MMM yyyy"
        ]
        
        for pattern in datePatterns {
            dateFormatter.dateFormat = pattern
            for line in lines {
                if let parsedDate = dateFormatter.date(from: line.trimmingCharacters(in: .whitespaces)) {
                    date = parsedDate
                    break
                }
            }
            if date != nil { break }
        }
        
        // Ürün isimleri - genellikle tutar içermeyen satırlar
        for line in lines {
            let trimmedLine = line.trimmingCharacters(in: .whitespaces)
            if trimmedLine.count > 2 && trimmedLine.count < 100 {
                // Tutar içermeyen satırları ürün olarak ekle
                if !trimmedLine.matches(#"[\d,\.]+\s*(?:TL|TRY|₺)"#) && 
                   !trimmedLine.matches(#"^[\d,\.]+$"#) &&
                   !trimmedLine.lowercased().contains("toplam") &&
                   !trimmedLine.lowercased().contains("kdv") {
                    items.append(trimmedLine)
                }
            }
        }
        
        return ReceiptAnalysisResult(
            amount: amount,
            merchant: merchant,
            date: date ?? Date(),
            items: Array(items.prefix(10)), // En fazla 10 ürün
            rawText: text
        )
    }
}

extension String {
    func matches(_ pattern: String) -> Bool {
        guard let regex = try? NSRegularExpression(pattern: pattern, options: .caseInsensitive) else {
            return false
        }
        let range = NSRange(self.startIndex..., in: self)
        return regex.firstMatch(in: self, options: [], range: range) != nil
    }
}

