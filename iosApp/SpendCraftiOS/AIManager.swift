//
//  AIManager.swift
//  SpendCraftiOS
//
//  AI Yönetim Sistemi - Groq API
//

import Foundation

enum AdviceType: String, CaseIterable, Identifiable {
    case spendingAnalysis = "Harcama Analizi"
    case budgetOptimization = "Bütçe Optimizasyonu"
    case savingsAdvice = "Tasarruf Önerileri"
    
    var id: String { self.rawValue }
    
    var icon: String {
        switch self {
        case .spendingAnalysis: return "chart.pie.fill"
        case .budgetOptimization: return "chart.line.uptrend.xyaxis"
        case .savingsAdvice: return "banknote.fill"
        }
    }
    
    var description: String {
        switch self {
        case .spendingAnalysis:
            return "Kategori bazında harcama alışkanlıklarınızı detaylı analiz eder"
        case .budgetOptimization:
            return "Gelir-gider dengesini optimize etmek için öneriler verir"
        case .savingsAdvice:
            return "Daha fazla tasarruf yapmak için pratik öneriler sunar"
        }
    }
}

class AIManager: ObservableObject {
    @Published var isLoading = false
    @Published var currentAdvice: String?
    @Published var errorMessage: String?
    
    private let apiKey = "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" // Groq API key
    private let apiURL = "https://api.groq.com/openai/v1/chat/completions"
    
    // MARK: - Generate Advice
    
    func generateAdvice(
        type: AdviceType,
        income: Double,
        expenses: Double,
        categoryBreakdown: [(category: String, amount: Double)]
    ) async {
        await MainActor.run {
            isLoading = true
            errorMessage = nil
        }
        
        let prompt = buildPrompt(
            type: type,
            income: income,
            expenses: expenses,
            categoryBreakdown: categoryBreakdown
        )
        
        do {
            let advice = try await callGroqAPI(prompt: prompt)
            await MainActor.run {
                currentAdvice = advice
                isLoading = false
            }
        } catch {
            await MainActor.run {
                errorMessage = "AI önerisi oluşturulamadı: \(error.localizedDescription)"
                isLoading = false
            }
        }
    }
    
    // MARK: - Build Prompt
    
    private func buildPrompt(
        type: AdviceType,
        income: Double,
        expenses: Double,
        categoryBreakdown: [(category: String, amount: Double)]
    ) -> String {
        let savings = income - expenses
        let categoryText = categoryBreakdown.map { "\($0.category): ₺\($0.amount)" }.joined(separator: ", ")
        
        switch type {
        case .spendingAnalysis:
            return """
            Sen bir finansal danışmansın. Aşağıdaki harcama verilerini analiz et ve Türkçe olarak detaylı bir analiz sun:
            
            Gelir: ₺\(income)
            Gider: ₺\(expenses)
            Tasarruf: ₺\(savings)
            
            Kategori Bazlı Harcamalar:
            \(categoryText)
            
            Lütfen:
            1. En çok harcama yapılan kategorileri belirt
            2. Hangi kategorilerde dikkat edilmeli
            3. Harcama dengesi hakkında yorum yap
            4. Kısa ve öz, madde madde yaz (maksimum 200 kelime)
            """
            
        case .budgetOptimization:
            return """
            Sen bir bütçe uzmanısın. Aşağıdaki finansal durumu incele ve Türkçe olarak bütçe optimizasyon önerileri sun:
            
            Aylık Gelir: ₺\(income)
            Aylık Gider: ₺\(expenses)
            Net Bakiye: ₺\(savings)
            
            Kategori Dağılımı:
            \(categoryText)
            
            Lütfen:
            1. Gelir-gider dengesini değerlendir
            2. Hangi kategorilerde bütçe azaltılabilir
            3. Tasarruf hedefi öner
            4. Uygulanabilir 3-4 öneri sun (maksimum 200 kelime)
            """
            
        case .savingsAdvice:
            return """
            Sen bir tasarruf danışmanısın. Aşağıdaki finansal duruma göre Türkçe olarak pratik tasarruf önerileri sun:
            
            Gelir: ₺\(income)
            Gider: ₺\(expenses)
            Mevcut Tasarruf: ₺\(savings)
            
            Harcama Kategorileri:
            \(categoryText)
            
            Lütfen:
            1. Kısa vadeli tasarruf teknikleri
            2. Günlük hayatta uygulanabilir öneriler
            3. Hangi kategorilerde kesinti yapılabilir
            4. Madde madde, pratik öneriler (maksimum 200 kelime)
            """
        }
    }
    
    // MARK: - Groq API Call
    
    private func callGroqAPI(prompt: String) async throws -> String {
        guard let url = URL(string: apiURL) else {
            throw AIError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = [
            "model": "llama-3.1-8b-instant",
            "messages": [
                [
                    "role": "user",
                    "content": prompt
                ]
            ],
            "temperature": 0.7,
            "max_tokens": 500
        ]
        
        request.httpBody = try JSONSerialization.data(withJSONObject: body)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw AIError.invalidResponse
        }
        
        guard httpResponse.statusCode == 200 else {
            throw AIError.apiError(code: httpResponse.statusCode)
        }
        
        let json = try JSONSerialization.jsonObject(with: data) as? [String: Any]
        guard let choices = json?["choices"] as? [[String: Any]],
              let firstChoice = choices.first,
              let message = firstChoice["message"] as? [String: Any],
              let content = message["content"] as? String else {
            throw AIError.parseError
        }
        
        return content
    }
}

// MARK: - AI Error

enum AIError: LocalizedError {
    case invalidURL
    case invalidResponse
    case apiError(code: Int)
    case parseError
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Geçersiz API URL"
        case .invalidResponse:
            return "Geçersiz yanıt"
        case .apiError(let code):
            return "API hatası (Kod: \(code))"
        case .parseError:
            return "Yanıt ayrıştırılamadı"
        }
    }
}
