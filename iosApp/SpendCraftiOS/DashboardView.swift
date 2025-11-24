//
//  DashboardView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI
import CoreData
import AVFoundation
import UIKit

struct DashboardView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    @EnvironmentObject var notificationsViewModel: NotificationsViewModel
    @EnvironmentObject var recurringViewModel: RecurringViewModel
    
    @State private var transactionTypeToAdd: TransactionType? = nil
    @State private var showUserProfiling = false
    @AppStorage("userProfilingCompleted") private var profilingCompleted = false
    @State private var showReceiptCamera = false
    @State private var showReceiptImagePicker = false
    @State private var capturedReceiptImage: UIImage? = nil
    @State private var showReceiptAnalysis = false
    @State private var receiptAnalysisResult: ReceiptAnalysisResult? = nil
    @State private var isAnalyzing = false
    @State private var showReceiptSourceSelection = false
    @State private var showCameraPermissionAlert = false
    @State private var pendingReceiptAnalysis = false // Analiz tamamlandı ama sheet henüz açılmadı
    
    enum TransactionType: Identifiable {
        case income
        case expense
        
        var id: String {
            switch self {
            case .income: return "income"
            case .expense: return "expense"
            }
        }
        
        var isIncome: Bool {
            switch self {
            case .income: return true
            case .expense: return false
            }
        }
    }
    
    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(spacing: 12) {
                // Balance Card
                VStack(spacing: 12) {
                    Text(NSLocalizedString("dashboard.total.balance", comment: "Total Balance"))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    Text(formatCurrency(transactionsViewModel.currentBalance))
                        .font(.system(size: 42, weight: .bold))
                        .foregroundColor(transactionsViewModel.currentBalance >= 0 ? .green : .red)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                .background(
                    RoundedRectangle(cornerRadius: 20)
                        .fill(LinearGradient(
                            colors: [Color.blue.opacity(0.1), Color.purple.opacity(0.1)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ))
                )
                .padding(.horizontal, 16)
                .padding(.top, 8) // başlık ile kart arası makul boşluk
                
                // Income & Expense Summary Cards
                HStack(spacing: 16) {
                    // Income Summary
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "arrow.down.circle.fill")
                                .foregroundColor(.green)
                            Text(NSLocalizedString("dashboard.income", comment: "Income"))
                                .font(.subheadline)
                        }
                        Text(formatCurrency(transactionsViewModel.totalIncome))
                            .font(.title3)
                            .fontWeight(.semibold)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 15)
                            .fill(Color.green.opacity(0.1))
                    )
                    
                    // Expense Summary
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "arrow.up.circle.fill")
                                .foregroundColor(.red)
                            Text(NSLocalizedString("dashboard.expense", comment: "Expense"))
                                .font(.subheadline)
                        }
                        Text(formatCurrency(transactionsViewModel.totalExpense))
                            .font(.title3)
                            .fontWeight(.semibold)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 15)
                            .fill(Color.red.opacity(0.1))
                    )
                }
                .padding(.horizontal, 16)
                
                // User Profiling Card (if not completed)
                if !profilingCompleted {
                    Button {
                        showUserProfiling = true
                    } label: {
                        HStack(spacing: 16) {
                            Image(systemName: "person.text.rectangle.fill")
                                .font(.title)
                                .foregroundColor(.purple)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(NSLocalizedString("dashboard.ai.profiling", comment: "AI Profiling Survey"))
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                Text(NSLocalizedString("dashboard.ai.profiling.description", comment: "Answer 7 questions for better recommendations"))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .foregroundColor(.purple)
                        }
                        .padding()
                        .background(
                            RoundedRectangle(cornerRadius: 15)
                                .fill(Color.purple.opacity(0.1))
                        )
                    }
                    .padding(.horizontal, 16)
                }
                
                // Quick Action Buttons
                HStack(spacing: 16) {
                    Button {
                        transactionTypeToAdd = .income
                    } label: {
                        HStack {
                            Image(systemName: "arrow.down.circle.fill")
                                .foregroundColor(.white)
                            Text(NSLocalizedString("dashboard.add.income", comment: "Add Income"))
                                .foregroundColor(.white)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .cornerRadius(15)
                    }
                    
                    Button {
                        transactionTypeToAdd = .expense
                    } label: {
                        HStack {
                            Image(systemName: "arrow.up.circle.fill")
                                .foregroundColor(.white)
                            Text(NSLocalizedString("dashboard.add.expense", comment: "Add Expense"))
                                .foregroundColor(.white)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.red)
                        .cornerRadius(15)
                    }
                }
                .padding(.horizontal, 16)
                
                // Fiş/Fatura Ekle Butonu
                Button {
                    showReceiptSourceSelection = true
                } label: {
                    HStack {
                        Image(systemName: "camera.fill")
                            .foregroundColor(.white)
                        Text(NSLocalizedString("dashboard.add.receipt", comment: "Add Receipt/Invoice"))
                            .foregroundColor(.white)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(15)
                }
                .padding(.horizontal, 16)
                
                // Streak Card
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Image(systemName: "flame.fill")
                            .foregroundColor(.orange)
                            .font(.title2)
                        Text(NSLocalizedString("dashboard.streak.title", comment: "Daily Streak"))
                            .font(.headline)
                        Spacer()
                    }
                    
                    HStack(spacing: 20) {
                        VStack {
                            Text("\(achievementsViewModel.currentStreak)")
                                .font(.title)
                                .fontWeight(.bold)
                            Text(NSLocalizedString("dashboard.streak.current", comment: "Current"))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Divider()
                            .frame(height: 40)
                        
                        VStack {
                            Text("\(achievementsViewModel.longestStreak)")
                                .font(.title)
                                .fontWeight(.bold)
                            Text(NSLocalizedString("dashboard.streak.longest", comment: "Longest"))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                .padding()
                .background(
                    RoundedRectangle(cornerRadius: 15)
                        .fill(Color.orange.opacity(0.1))
                )
                .padding(.horizontal, 16)
                
                // Budget Overview
                if !budgetViewModel.budgets.isEmpty {
                    VStack(alignment: .leading, spacing: 12) {
                        Text(NSLocalizedString("dashboard.budget.status", comment: "Budget Status"))
                            .font(.headline)
                            .padding(.horizontal, 16)
                        
                        ForEach(budgetViewModel.budgets.prefix(3), id: \.id) { budget in
                            if let category = budget.category {
                                BudgetProgressRow(
                                    budget: budget,
                                    category: category,
                                    spent: budgetViewModel.spentForCategory(category),
                                    progress: budgetViewModel.budgetProgress(budget)
                                )
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                // Achievements
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Text(NSLocalizedString("dashboard.achievements", comment: "Achievements"))
                            .font(.headline)
                        Spacer()
                        Text(String(format: NSLocalizedString("achievements.total.points.format", comment: "%d Points"), achievementsViewModel.totalPoints))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .padding(.horizontal, 16)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            ForEach(achievementsViewModel.achievements.prefix(5), id: \.objectID) { achievement in
                                AchievementCard(achievement: achievement)
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
                .padding(.vertical, 8)
                .id(achievementsViewModel.achievements.map { "\($0.objectID)-\($0.progress)" }.joined())
                
                // Recent Transactions
                VStack(alignment: .leading, spacing: 12) {
                        Text(NSLocalizedString("dashboard.recent.transactions.title", comment: "Recent Transactions"))
                            .font(.headline)
                        .padding(.horizontal, 16)
                    
                    ForEach(transactionsViewModel.transactions.prefix(5), id: \.id) { transaction in
                        TransactionRow(transaction: transaction)
                    }
                }
                .padding(.vertical, 8)
                .id(transactionsViewModel.transactions.map { "\($0.id)-\($0.timestampUtcMillis)-\($0.amountMinor)" }.joined())
            }
        }
        
        // Banner Ad at bottom
        AdaptiveBannerAdView()
            .background(Color(uiColor: .systemBackground))
            .shadow(color: .black.opacity(0.1), radius: 4, y: -2)
        }
        .navigationTitle(NSLocalizedString("tab.dashboard", comment: "Dashboard"))
        .navigationBarTitleDisplayMode(.large)
        .sheet(item: $transactionTypeToAdd, onDismiss: {
            // Small delay to ensure CoreData changes are committed
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                // Reload data when sheet is dismissed to reflect new transaction
                transactionsViewModel.loadTransactions()
                achievementsViewModel.loadAchievements()
                achievementsViewModel.updateStreak()
                achievementsViewModel.loadStreak()
                
                // Check achievements with updated transaction count
                achievementsViewModel.checkAchievements(
                    transactionCount: transactionsViewModel.transactions.count,
                    totalSpent: transactionsViewModel.totalExpense,
                    categories: transactionsViewModel.categories.count,
                    notificationsViewModel: notificationsViewModel
                )
            }
        }) { transactionType in
            AddTransactionView(initialIsIncome: transactionType.isIncome)
                .id(transactionType.id) // Force new view instance for each type
                .environmentObject(transactionsViewModel)
                .environmentObject(achievementsViewModel)
                .environmentObject(notificationsViewModel)
                .environmentObject(recurringViewModel)
        }
        .sheet(isPresented: $showUserProfiling) {
            UserProfilingView()
        }
        .sheet(isPresented: $showReceiptCamera, onDismiss: {
            // Camera kapandığında, eğer analiz tamamlandıysa analiz ekranını aç
            // Sheet tamamen kapandıktan sonra analiz ekranını aç
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                // State'lerin hazır olduğundan emin ol
                if pendingReceiptAnalysis {
                    if receiptAnalysisResult != nil && capturedReceiptImage != nil {
                        showReceiptAnalysis = true
                        pendingReceiptAnalysis = false
                    } else {
                        // State'ler henüz hazır değil, biraz daha bekle
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                            if receiptAnalysisResult != nil && capturedReceiptImage != nil {
                                showReceiptAnalysis = true
                                pendingReceiptAnalysis = false
                            }
                        }
                    }
                }
            }
        }) {
            ReceiptCameraView(isPresented: $showReceiptCamera) { image in
                // Image çekildiğinde sheet'i kapat ve analizi başlat
                capturedReceiptImage = image
                isAnalyzing = true
                showReceiptCamera = false // Sheet'i kapat
                
                // Analiz işlemini başlat
                ReceiptAnalyzer.shared.analyzeReceipt(image: image) { result in
                    DispatchQueue.main.async {
                        isAnalyzing = false
                        receiptAnalysisResult = result
                        // Analiz tamamlandı, sheet kapandıktan sonra analiz ekranını açacağız
                        pendingReceiptAnalysis = true
                    }
                }
            }
        }
        .sheet(isPresented: $showReceiptImagePicker, onDismiss: {
            // Image picker kapandığında, eğer analiz tamamlandıysa analiz ekranını aç
            // Sheet tamamen kapandıktan sonra analiz ekranını aç
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                // State'lerin hazır olduğundan emin ol
                if pendingReceiptAnalysis {
                    if receiptAnalysisResult != nil && capturedReceiptImage != nil {
                        showReceiptAnalysis = true
                        pendingReceiptAnalysis = false
                    } else {
                        // State'ler henüz hazır değil, biraz daha bekle
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                            if receiptAnalysisResult != nil && capturedReceiptImage != nil {
                                showReceiptAnalysis = true
                                pendingReceiptAnalysis = false
                            }
                        }
                    }
                }
            }
        }) {
            ReceiptImagePickerView(isPresented: $showReceiptImagePicker) { image in
                // Image seçildiğinde sheet'i kapat ve analizi başlat
                capturedReceiptImage = image
                isAnalyzing = true
                showReceiptImagePicker = false // Sheet'i kapat
                
                // Analiz işlemini başlat
                ReceiptAnalyzer.shared.analyzeReceipt(image: image) { result in
                    DispatchQueue.main.async {
                        isAnalyzing = false
                        receiptAnalysisResult = result
                        // Analiz tamamlandı, sheet kapandıktan sonra analiz ekranını açacağız
                        pendingReceiptAnalysis = true
                    }
                }
            }
        }
        .sheet(isPresented: $showReceiptSourceSelection) {
            VStack(alignment: .leading, spacing: 0) {
                // Başlık
                VStack(alignment: .leading, spacing: 4) {
                    Text(NSLocalizedString("receipt.select.source", comment: "Select Source"))
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Text(NSLocalizedString("receipt.select.source.message", comment: "Choose how to add receipt"))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .padding(.bottom, 16)
                
                Divider()
                
                // Kamera butonu
                Button {
                    showReceiptSourceSelection = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        checkCameraPermission { granted in
                            if granted {
                                DispatchQueue.main.async {
                                    showReceiptCamera = true
                                }
                            } else {
                                DispatchQueue.main.async {
                                    showCameraPermissionAlert = true
                                }
                            }
                        }
                    }
                } label: {
                    HStack(spacing: 16) {
                        Image(systemName: "camera.fill")
                            .font(.title2)
                            .foregroundColor(.blue)
                            .frame(width: 30)
                        Text(NSLocalizedString("receipt.camera", comment: "Camera"))
                            .font(.body)
                            .foregroundColor(.primary)
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 16)
                }
                .buttonStyle(.plain)
                
                Divider()
                
                // Fotoğraf Galerisi butonu
                Button {
                    showReceiptSourceSelection = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        showReceiptImagePicker = true
                    }
                } label: {
                    HStack(spacing: 16) {
                        Image(systemName: "photo.fill")
                            .font(.title2)
                            .foregroundColor(.blue)
                            .frame(width: 30)
                        Text(NSLocalizedString("receipt.photo.library", comment: "Photo Library"))
                            .font(.body)
                            .foregroundColor(.primary)
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 16)
                }
                .buttonStyle(.plain)
                
                Spacer()
            }
            .presentationDetents([.height(220)])
            .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $showReceiptAnalysis) {
            ReceiptAnalysisSheetView(
                capturedReceiptImage: $capturedReceiptImage,
                receiptAnalysisResult: $receiptAnalysisResult,
                pendingReceiptAnalysis: $pendingReceiptAnalysis,
                onConfirm: { amount, note, date in
                    addReceiptTransaction(amount: amount, note: note, date: date)
                },
                onCancel: {
                    showReceiptAnalysis = false
                    capturedReceiptImage = nil
                    receiptAnalysisResult = nil
                    pendingReceiptAnalysis = false
                }
            )
        }
        .alert(
            NSLocalizedString("receipt.camera.permission.title", comment: "Camera Permission Required"),
            isPresented: $showCameraPermissionAlert
        ) {
            Button(NSLocalizedString("common.settings", comment: "Settings")) {
                if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(settingsURL)
                }
            }
            Button(NSLocalizedString("common.cancel", comment: "Cancel"), role: .cancel) {}
        } message: {
            Text(NSLocalizedString("receipt.camera.permission.message", comment: "Please enable camera access in Settings"))
        }
        .onAppear {
            // Load data
            reloadAllData()
            
            // Check budgets and send notifications if needed
            let spentAmounts = transactionsViewModel.categories.reduce(into: [String: Double]()) { result, category in
                result[String(category.id)] = transactionsViewModel.totalSpentForCategory(category)
            }
            notificationsViewModel.checkAllBudgets(budgets: budgetViewModel.budgets, spentAmounts: spentAmounts)
        }
        .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("TransactionUpdated"))) { _ in
            // Reload when transaction is updated from other screens
            print("🔄 Dashboard: Received transaction update notification")
            reloadAllData()
        }
    }
    
    private func reloadAllData() {
        // Refresh CoreData context first
        let context = CoreDataStack.shared.container.viewContext
        context.refreshAllObjects()
        
        // Reload all data
        transactionsViewModel.loadTransactions()
        transactionsViewModel.loadCategories()
        budgetViewModel.loadBudgets()
        achievementsViewModel.loadAchievements()
        achievementsViewModel.loadStreak()
        
        // Force UI update
        transactionsViewModel.objectWillChange.send()
        
        print("✅ Dashboard: All data reloaded")
    }
    
    private func checkCameraPermission(completion: @escaping (Bool) -> Void) {
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        switch status {
        case .authorized:
            completion(true)
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { granted in
                completion(granted)
            }
        default:
            completion(false)
        }
    }
    
    private func processReceiptImage(_ image: UIImage) {
        // Bu fonksiyon artık kullanılmıyor, doğrudan sheet callback'lerinde işlem yapılıyor
        // Ama geriye dönük uyumluluk için tutuyoruz
        capturedReceiptImage = image
        isAnalyzing = true
        
        ReceiptAnalyzer.shared.analyzeReceipt(image: image) { result in
            DispatchQueue.main.async {
                isAnalyzing = false
                receiptAnalysisResult = result
                // Sheet'lerin kapanmasını bekle
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                    if capturedReceiptImage != nil && receiptAnalysisResult != nil {
                        showReceiptAnalysis = true
                    }
                }
            }
        }
    }
    
    private func addReceiptTransaction(amount: Double, note: String?, date: Date) {
        // Varsayılan kategoriyi bul (gider kategorilerinden birini seç)
        let expenseCategories = transactionsViewModel.categoriesForType(false)
        let defaultCategory = expenseCategories.first { category in
            category.name.contains("category.other") || 
            category.name.contains("category.food") ||
            category.name.contains("category.shopping")
        } ?? expenseCategories.first
        
        // Varsayılan hesabı bul
        let defaultAccount = transactionsViewModel.accounts.first
        
        // Gider olarak ekle
        transactionsViewModel.addTransaction(
            amount: amount,
            note: note,
            category: defaultCategory,
            account: defaultAccount,
            isIncome: false,
            achievementsViewModel: achievementsViewModel,
            notificationsViewModel: notificationsViewModel
        )
        
        // Verileri yenile
        reloadAllData()
        
        // Sheet'i kapat
        showReceiptAnalysis = false
        capturedReceiptImage = nil
        receiptAnalysisResult = nil
    }
}

struct BudgetProgressRow: View {
    let budget: BudgetEntity
    let category: CategoryEntity
    let spent: Double
    let progress: Double
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(category.localizedName)
                    .font(.subheadline)
                Spacer()
                Text("\(formatCurrency(spent)) / \(formatCurrency(Double(budget.monthlyLimitMinor) / 100.0))")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(height: 8)
                        .cornerRadius(4)
                    
                    Rectangle()
                        .fill(progress > 0.8 ? Color.red : Color.blue)
                        .frame(width: geometry.size.width * CGFloat(progress), height: 8)
                        .cornerRadius(4)
                }
            }
            .frame(height: 8)
        }
        .padding(.horizontal)
    }
}

struct AchievementCard: View {
    @ObservedObject var achievement: AchievementEntity
    @State private var showDetailSheet = false
    
    var body: some View {
        Button {
            showDetailSheet = true
        } label: {
            VStack(spacing: 8) {
                Image(systemName: achievement.icon ?? "star.fill")
                    .font(.system(size: 32))
                    .foregroundColor(achievement.isUnlocked ? .yellow : .gray)
                
                Text(NSLocalizedString(achievement.name, comment: achievement.name))
                    .font(.caption)
                    .fontWeight(.semibold)
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
                
                if achievement.isUnlocked {
                    Text(String(format: NSLocalizedString("achievements.points", comment: "Points"), achievement.points))
                        .font(.caption2)
                        .foregroundColor(.secondary)
                } else {
                    Text("\(achievement.progress)/\(achievement.maxProgress)")
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }
            .frame(width: 100, height: 120)
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(achievement.isUnlocked ? Color.yellow.opacity(0.1) : Color.gray.opacity(0.1))
            )
        }
        .buttonStyle(.plain)
        .sheet(isPresented: $showDetailSheet) {
            AchievementDetailSheet(achievement: achievement)
                .presentationDetents([.height(500), .large])
                .presentationDragIndicator(.visible)
        }
        .id(achievement.objectID) // Force SwiftUI to track changes by CoreData objectID
    }
}

// Receipt Analysis Sheet için wrapper view
struct ReceiptAnalysisSheetView: View {
    @Binding var capturedReceiptImage: UIImage?
    @Binding var receiptAnalysisResult: ReceiptAnalysisResult?
    @Binding var pendingReceiptAnalysis: Bool
    let onConfirm: (Double, String?, Date) -> Void
    let onCancel: () -> Void
    
    @State private var retryCount = 0
    private let maxRetries = 10
    
    var body: some View {
        Group {
            if let image = capturedReceiptImage, let result = receiptAnalysisResult {
                ReceiptAnalysisView(
                    image: image,
                    analysisResult: result,
                    onConfirm: onConfirm,
                    onCancel: onCancel
                )
            } else {
                // Fallback view - state'ler henüz hazır değilse loading göster
                VStack(spacing: 20) {
                    ProgressView()
                        .scaleEffect(1.5)
                    Text(NSLocalizedString("common.loading", comment: "Loading..."))
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(uiColor: .systemBackground))
                .onAppear {
                    // State'ler hazır olana kadar bekle ve yeniden dene
                    checkAndRetry()
                }
            }
        }
    }
    
    private func checkAndRetry() {
        if capturedReceiptImage == nil || receiptAnalysisResult == nil {
            if retryCount < maxRetries {
                retryCount += 1
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    checkAndRetry()
                }
            }
        }
    }
}

struct TransactionRow: View {
    let transaction: TransactionEntity
    
    var body: some View {
        HStack {
            if let category = transaction.category {
                Image(systemName: category.icon ?? "circle.fill")
                    .font(.title2)
                    .foregroundColor(category.uiColor)
                    .frame(width: 40, height: 40)
                    .background(category.uiColor.opacity(0.2))
                    .cornerRadius(10)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(transaction.category?.name.isEmpty == false ? NSLocalizedString(transaction.category?.name ?? "", comment: transaction.category?.name ?? "") : NSLocalizedString("common.other", comment: "Other"))
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                if let note = transaction.note, !note.isEmpty {
                    Text(note)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Text(transaction.formattedDate)
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Text(transaction.formattedAmount)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(transaction.isIncome ? .green : .red)
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
    }
}
