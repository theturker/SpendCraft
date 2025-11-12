//
//  AIManager.swift
//  SpendCraftiOS
//
//  AI Yönetim Sistemi - Groq API
//

import Foundation

enum AdviceType: String, CaseIterable, Identifiable {
    case spendingAnalysis = "ai.expense.analysis"
    case budgetOptimization = "ai.budget.optimization"
    case savingsAdvice = "ai.savings.suggestions"
    
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
            return NSLocalizedString("ai.expense.analysis.description", comment: "Analyzes your spending habits in detail based on categories")
        case .budgetOptimization:
            return NSLocalizedString("ai.budget.optimization.description", comment: "Provides suggestions to optimize your income-expense balance")
        case .savingsAdvice:
            return NSLocalizedString("ai.savings.suggestions.description", comment: "Offers practical suggestions to save more")
        }
    }
}

class AIManager: ObservableObject {
    @Published var isLoading = false
    @Published var currentAdvice: String?
    @Published var errorMessage: String?
    
    private let apiKey = "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" // Groq API key
    private let apiURL = "https://api.groq.com/openai/v1/chat/completions"
    
    // MARK: - User Profiling Data
    
    private func getUserProfilingData() -> [String: String]? {
        let isCompleted = UserDefaults.standard.bool(forKey: "userProfilingCompleted")
        guard isCompleted else { return nil }
        
        var profileData: [String: String] = [:]
        let keys = ["income_frequency", "spending_habit", "savings_goal", "biggest_expense", 
                    "budget_management", "financial_goal", "debt_status"]
        
        for key in keys {
            if let value = UserDefaults.standard.string(forKey: "profiling_\(key)") {
                profileData[key] = value
            }
        }
        
        return profileData.isEmpty ? nil : profileData
    }
    
    private func buildProfilingContext() -> String? {
        guard let profileData = getUserProfilingData() else { return nil }
        
        let isEnglish = LanguageHelper.shared.getCurrentLanguage() == "en"
        
        var context = isEnglish ? "\n\nUser Profile:" : "\n\nKullanıcı Profili:"
        
        if let incomeFreq = profileData["income_frequency"] {
            context += isEnglish ? "\n- Income Frequency: \(incomeFreq)" : "\n- Gelir Sıklığı: \(incomeFreq)"
        }
        if let spendingHabit = profileData["spending_habit"] {
            context += isEnglish ? "\n- Spending Habit: \(spendingHabit)" : "\n- Harcama Alışkanlığı: \(spendingHabit)"
        }
        if let savingsGoal = profileData["savings_goal"] {
            context += isEnglish ? "\n- Savings Goal: \(savingsGoal)" : "\n- Tasarruf Hedefi: \(savingsGoal)"
        }
        if let biggestExpense = profileData["biggest_expense"] {
            context += isEnglish ? "\n- Biggest Expense Area: \(biggestExpense)" : "\n- En Çok Harcama Yapılan Alan: \(biggestExpense)"
        }
        if let budgetMgmt = profileData["budget_management"] {
            context += isEnglish ? "\n- Budget Management Level: \(budgetMgmt)" : "\n- Bütçe Yönetimi Seviyesi: \(budgetMgmt)"
        }
        if let financialGoal = profileData["financial_goal"] {
            context += isEnglish ? "\n- Main Financial Goal: \(financialGoal)" : "\n- Ana Finansal Hedef: \(financialGoal)"
        }
        if let debtStatus = profileData["debt_status"] {
            context += isEnglish ? "\n- Debt Status: \(debtStatus)" : "\n- Borç Durumu: \(debtStatus)"
        }
        
        return context
    }
    
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
                let isEnglish = LanguageHelper.shared.getCurrentLanguage() == "en"
                errorMessage = isEnglish ? "Could not generate AI advice: \(error.localizedDescription)" : "AI önerisi oluşturulamadı: \(error.localizedDescription)"
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
        let isEnglish = LanguageHelper.shared.getCurrentLanguage() == "en"
        let currencySymbol = isEnglish ? "$" : "₺"
        let categoryText = categoryBreakdown.map { "\($0.category): \(currencySymbol)\($0.amount)" }.joined(separator: ", ")
        let profilingContext = buildProfilingContext() ?? ""
        
        switch type {
        case .spendingAnalysis:
            if isEnglish {
                return """
                You are a financial advisor. Analyze the following spending data and provide a detailed analysis in English:
                
                Income: \(currencySymbol)\(income)
                Expenses: \(currencySymbol)\(expenses)
                Savings: \(currencySymbol)\(savings)
                
                Category-based Expenses:
                \(categoryText)\(profilingContext)
                
                Please:
                1. Identify the categories with highest spending
                2. Suggest which categories need attention
                3. Comment on spending balance
                4. Provide personalized recommendations based on user profile if available
                5. Write concisely, bullet points (maximum 200 words)
                """
            } else {
                return """
                Sen bir finansal danışmansın. Aşağıdaki harcama verilerini analiz et ve Türkçe olarak detaylı bir analiz sun:
                
                Gelir: \(currencySymbol)\(income)
                Gider: \(currencySymbol)\(expenses)
                Tasarruf: \(currencySymbol)\(savings)
                
                Kategori Bazlı Harcamalar:
                \(categoryText)\(profilingContext)
                
                Lütfen:
                1. En çok harcama yapılan kategorileri belirt
                2. Hangi kategorilerde dikkat edilmeli
                3. Harcama dengesi hakkında yorum yap
                4. Kullanıcı profili varsa ona göre özel öneriler ver
                5. Kısa ve öz, madde madde yaz (maksimum 200 kelime)
                """
            }
            
        case .budgetOptimization:
            if isEnglish {
                return """
                You are a budget expert. Analyze the following financial situation and provide budget optimization recommendations in English:
                
                Monthly Income: \(currencySymbol)\(income)
                Monthly Expenses: \(currencySymbol)\(expenses)
                Net Balance: \(currencySymbol)\(savings)
                
                Category Distribution:
                \(categoryText)\(profilingContext)
                
                Please:
                1. Evaluate income-expense balance
                2. Suggest which categories can reduce budget
                3. Recommend savings target
                4. Provide customized recommendations based on user's financial goals and situation
                5. Provide 3-4 actionable recommendations (maximum 200 words)
                """
            } else {
                return """
                Sen bir bütçe uzmanısın. Aşağıdaki finansal durumu incele ve Türkçe olarak bütçe optimizasyon önerileri sun:
                
                Aylık Gelir: \(currencySymbol)\(income)
                Aylık Gider: \(currencySymbol)\(expenses)
                Net Bakiye: \(currencySymbol)\(savings)
                
                Kategori Dağılımı:
                \(categoryText)\(profilingContext)
                
                Lütfen:
                1. Gelir-gider dengesini değerlendir
                2. Hangi kategorilerde bütçe azaltılabilir
                3. Tasarruf hedefi öner
                4. Kullanıcının finansal hedef ve durumuna göre özelleştirilmiş öneriler ver
                5. Uygulanabilir 3-4 öneri sun (maksimum 200 kelime)
                """
            }
            
        case .savingsAdvice:
            if isEnglish {
                return """
                You are a savings consultant. Based on the following financial situation, provide practical savings advice in English:
                
                Income: \(currencySymbol)\(income)
                Expenses: \(currencySymbol)\(expenses)
                Current Savings: \(currencySymbol)\(savings)
                
                Expense Categories:
                \(categoryText)\(profilingContext)
                
                Please:
                1. Short-term savings techniques
                2. Practical recommendations for daily life
                3. Which categories can be reduced
                4. Consider user's savings goals and debt status
                5. Bullet points, practical recommendations (maximum 200 words)
                """
            } else {
                return """
                Sen bir tasarruf danışmanısın. Aşağıdaki finansal duruma göre Türkçe olarak pratik tasarruf önerileri sun:
                
                Gelir: \(currencySymbol)\(income)
                Gider: \(currencySymbol)\(expenses)
                Mevcut Tasarruf: \(currencySymbol)\(savings)
                
                Harcama Kategorileri:
                \(categoryText)\(profilingContext)
                
                Lütfen:
                1. Kısa vadeli tasarruf teknikleri
                2. Günlük hayatta uygulanabilir öneriler
                3. Hangi kategorilerde kesinti yapılabilir
                4. Kullanıcının tasarruf hedefi ve borç durumunu göz önünde bulundur
                5. Madde madde, pratik öneriler (maksimum 200 kelime)
                """
            }
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
        let isEnglish = LanguageHelper.shared.getCurrentLanguage() == "en"
        
        switch self {
        case .invalidURL:
            return isEnglish ? "Invalid API URL" : "Geçersiz API URL"
        case .invalidResponse:
            return isEnglish ? "Invalid response" : "Geçersiz yanıt"
        case .apiError(let code):
            return isEnglish ? "API error (Code: \(code))" : "API hatası (Kod: \(code))"
        case .parseError:
            return isEnglish ? "Response could not be parsed" : "Yanıt ayrıştırılamadı"
        }
    }
}
