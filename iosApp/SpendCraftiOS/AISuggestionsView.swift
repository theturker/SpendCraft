//
//  AISuggestionsView.swift
//  SpendCraftiOS
//
//  AI Önerileri Ekranı
//

import SwiftUI
import GoogleMobileAds

struct AISuggestionsView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @StateObject private var aiManager = AIManager()
    @StateObject private var adsManager = AdsManager.shared
    @Environment(\.dismiss) private var dismiss
    
    @State private var selectedType: AdviceType?
    @AppStorage("lastAISuggestionTime") private var lastSuggestionTime: Double = 0
    @State private var remainingTime: TimeInterval = 0
    @State private var timer: Timer?
    
    private var canUseAI: Bool {
        let elapsed = Date().timeIntervalSince1970 - lastSuggestionTime
        return elapsed >= 86400 // 24 saat
    }
    
    var categoryBreakdown: [(category: String, amount: Double)] {
        let categories = Dictionary(grouping: transactionsViewModel.transactions.filter { !$0.isIncome }) { transaction -> String in
            transaction.category?.name ?? "Diğer"
        }
        
        return categories.map { (category: $0.key, amount: $0.value.reduce(0) { $0 + $1.amount }) }
            .sorted { $0.amount > $1.amount }
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                // Background
                LinearGradient(
                    colors: [Color.purple.opacity(0.1), Color.blue.opacity(0.1)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 24) {
                        // Header
                        VStack(spacing: 12) {
                            Image(systemName: "sparkles")
                                .font(.system(size: 60))
                                .foregroundStyle(
                                    LinearGradient(
                                        colors: [.purple, .blue, .pink],
                                        startPoint: .leading,
                                        endPoint: .trailing
                                    )
                                )
                            
                            Text("AI Finansal Danışman")
                                .font(.title)
                                .fontWeight(.bold)
                            
                            Text("Yapay zeka destekli kişiselleştirilmiş finansal öneriler")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal)
                        }
                        .padding(.top)
                        
                        // Advice Type Selection
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Öneri Türü Seçin")
                                .font(.headline)
                                .padding(.horizontal)
                            
                            ForEach(AdviceType.allCases) { type in
                                Button {
                                    selectedType = type
                                } label: {
                                    HStack(spacing: 16) {
                                        ZStack {
                                            Circle()
                                                .fill(selectedType == type ? 
                                                      LinearGradient(colors: [.blue, .purple], startPoint: .topLeading, endPoint: .bottomTrailing) :
                                                        LinearGradient(colors: [Color.gray.opacity(0.2)], startPoint: .top, endPoint: .bottom))
                                                .frame(width: 50, height: 50)
                                            
                                            Image(systemName: type.icon)
                                                .font(.title3)
                                                .foregroundColor(selectedType == type ? .white : .primary)
                                        }
                                        
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text(type.rawValue)
                                                .font(.headline)
                                                .foregroundColor(.primary)
                                            
                                            Text(type.description)
                                                .font(.caption)
                                                .foregroundColor(.secondary)
                                                .lineLimit(2)
                                        }
                                        
                                        Spacer()
                                        
                                        if selectedType == type {
                                            Image(systemName: "checkmark.circle.fill")
                                                .font(.title3)
                                                .foregroundColor(.blue)
                                        }
                                    }
                                    .padding()
                                    .background(
                                        RoundedRectangle(cornerRadius: 16)
                                            .fill(Color(.systemBackground))
                                            .shadow(color: selectedType == type ? .blue.opacity(0.3) : .black.opacity(0.05), radius: selectedType == type ? 10 : 5)
                                    )
                                }
                                .padding(.horizontal)
                            }
                        }
                        
                        // Generate Button or Countdown
                        if canUseAI {
                            Button {
                                generateAdvice()
                            } label: {
                                HStack {
                                    if aiManager.isLoading {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    } else {
                                        Image(systemName: "sparkles")
                                        Text("AI Önerisi Al")
                                            .fontWeight(.semibold)
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(
                                    LinearGradient(
                                        colors: selectedType == nil ? [Color.gray] : [Color.blue, Color.purple],
                                        startPoint: .leading,
                                        endPoint: .trailing
                                    )
                                )
                                .foregroundColor(.white)
                                .cornerRadius(16)
                            }
                            .disabled(selectedType == nil || aiManager.isLoading)
                            .padding(.horizontal)
                        } else {
                            // Countdown Display
                            VStack(spacing: 12) {
                                HStack(spacing: 8) {
                                    Image(systemName: "hourglass")
                                        .font(.title2)
                                        .foregroundColor(.purple)
                                    Text("Sonraki Öneri İçin Bekleme Süresi")
                                        .font(.headline)
                                }
                                
                                Text(timeString(from: remainingTime))
                                    .font(.system(size: 36, weight: .bold))
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [.blue, .purple],
                                            startPoint: .leading,
                                            endPoint: .trailing
                                        )
                                    )
                                
                                Text("AI önerileri günde bir kez kullanılabilir. Bir sonraki öneriye erişmek için lütfen bekleyin.")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .multilineTextAlignment(.center)
                                    .padding(.horizontal)
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(
                                RoundedRectangle(cornerRadius: 16)
                                    .fill(Color(.systemBackground))
                            )
                            .padding(.horizontal)
                        }
                        
                        // AI Advice Display
                        if let advice = aiManager.currentAdvice {
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "lightbulb.fill")
                                        .foregroundColor(.yellow)
                                    Text("AI Önerisi")
                                        .font(.headline)
                                }
                                
                                Text(advice)
                                    .font(.body)
                                    .lineSpacing(6)
                                    .foregroundColor(.primary)
                            }
                            .padding()
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(
                                RoundedRectangle(cornerRadius: 16)
                                    .fill(Color(.systemBackground))
                            )
                            .padding(.horizontal)
                        }
                        
                        // Error Message
                        if let error = aiManager.errorMessage {
                            HStack {
                                Image(systemName: "exclamationmark.triangle.fill")
                                    .foregroundColor(.red)
                                Text(error)
                                    .font(.callout)
                                    .foregroundColor(.red)
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.red.opacity(0.1))
                            )
                            .padding(.horizontal)
                        }
                        
                        // Stats Card
                        VStack(alignment: .leading, spacing: 16) {
                            Text("Finansal Durum Özeti")
                                .font(.headline)
                                .padding(.horizontal)
                            
                            HStack(spacing: 12) {
                                FinancialStatCard(
                                    title: "Gelir",
                                    value: formatCurrency(transactionsViewModel.totalIncome),
                                    icon: "arrow.down.circle.fill",
                                    color: .green
                                )
                                
                                FinancialStatCard(
                                    title: "Gider",
                                    value: formatCurrency(transactionsViewModel.totalExpense),
                                    icon: "arrow.up.circle.fill",
                                    color: .red
                                )
                            }
                            .padding(.horizontal)
                            
                            HStack(spacing: 12) {
                                FinancialStatCard(
                                    title: "Bakiye",
                                    value: formatCurrency(transactionsViewModel.currentBalance),
                                    icon: "banknote.fill",
                                    color: .blue
                                )
                                
                                FinancialStatCard(
                                    title: "Kategori",
                                    value: "\(transactionsViewModel.categories.count)",
                                    icon: "folder.fill",
                                    color: .orange
                                )
                            }
                            .padding(.horizontal)
                        }
                        .padding(.vertical)
                    }
                    .padding(.vertical)
                }
            }
            .navigationTitle(NSLocalizedString("features.ai.suggestions", comment: "AI Suggestions"))
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.secondary)
                    }
                }
            }
            .onAppear {
                updateRemainingTime()
                startTimer()
                
                // Ekran geçişinin tamamen bitmesini bekle (daha uzun süre)
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    showInterstitialAd()
                }
            }
            .onDisappear {
                stopTimer()
            }
        }
    }
    
    // MARK: - Timer & Countdown
    
    private func updateRemainingTime() {
        let elapsed = Date().timeIntervalSince1970 - lastSuggestionTime
        remainingTime = max(0, 86400 - elapsed)
    }
    
    private func startTimer() {
        timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { _ in
            updateRemainingTime()
            
            // Süre dolduğunda bildirim gönder
            if remainingTime <= 0 && lastSuggestionTime > 0 {
                sendAIAvailableNotification()
                stopTimer()
            }
        }
    }
    
    private func stopTimer() {
        timer?.invalidate()
        timer = nil
    }
    
    private func timeString(from timeInterval: TimeInterval) -> String {
        let hours = Int(timeInterval) / 3600
        let minutes = Int(timeInterval) / 60 % 60
        let seconds = Int(timeInterval) % 60
        return String(format: "%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    private func sendAIAvailableNotification() {
        let content = UNMutableNotificationContent()
        content.title = "AI Danışman Hazır! 🤖✨"
        content.body = "Yeni bir AI önerisi almanın zamanı geldi! Finansal önerileriniz sizi bekliyor."
        content.sound = .default
        content.badge = 1
        
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: "ai_suggestion_available",
            content: content,
            trigger: trigger
        )
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error sending AI available notification: \(error)")
            }
        }
    }
    
    // MARK: - Generate Advice
    
    private func generateAdvice() {
        guard let type = selectedType, canUseAI else { return }
        
        // Son kullanım zamanını kaydet
        lastSuggestionTime = Date().timeIntervalSince1970
        
        Task {
            await aiManager.generateAdvice(
                type: type,
                income: transactionsViewModel.totalIncome,
                expenses: transactionsViewModel.totalExpense,
                categoryBreakdown: categoryBreakdown
            )
        }
        
        // Timer'ı başlat
        updateRemainingTime()
        startTimer()
    }
    
    // MARK: - Interstitial Ad
    
    private func showInterstitialAd() {
        print("🎯 AI Suggestions View - Attempting to show interstitial ad...")
        
        // Premium kullanıcılar için reklam gösterme
        guard adsManager.shouldShowAds() else {
            print("⚠️ AI Suggestions - User is premium, skipping ad")
            return
        }
        
        // Farklı view controller bulma yöntemleri dene
        var targetViewController: UIViewController?
        
        // Yöntem 1: Window scene'den root view controller
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {
            targetViewController = rootViewController
            print("✅ AI Suggestions - Found root view controller via window scene")
        }
        
        // Yöntem 2: Key window'dan root view controller
        if targetViewController == nil,
           let keyWindow = UIApplication.shared.windows.first(where: { $0.isKeyWindow }),
           let rootViewController = keyWindow.rootViewController {
            targetViewController = rootViewController
            print("✅ AI Suggestions - Found root view controller via key window")
        }
        
        guard let viewController = targetViewController else {
            print("❌ AI Suggestions - Could not find any root view controller")
            return
        }
        
        // UI transition'ın tamamlanmasını bekle
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            print("🚀 AI Suggestions - Showing interstitial ad now...")
            self.adsManager.showInterstitialAd(from: viewController) {
                // Reklam kapandıktan sonra yeni reklam yükle
                print("✅ AI Suggestions - Interstitial ad closed, loading new ad")
            }
        }
    }
}

// MARK: - Financial Stat Card

struct FinancialStatCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(color)
            
            Text(value)
                .font(.headline)
                .fontWeight(.bold)
            
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemBackground))
        )
    }
}

#Preview {
    AISuggestionsView()
        .environmentObject(TransactionsViewModel())
}

