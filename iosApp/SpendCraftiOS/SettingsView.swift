//
//  SettingsView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI
import UserNotifications

struct SettingsView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var accountsViewModel: AccountsViewModel
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    @EnvironmentObject var recurringViewModel: RecurringViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    @EnvironmentObject var notificationsViewModel: NotificationsViewModel
    @EnvironmentObject var authViewModel: AuthViewModel
    @StateObject private var notificationManager = NotificationManager.shared
    @StateObject private var biometricManager = BiometricManager.shared
    
    @AppStorage("selectedCurrency") private var selectedCurrency: String = "TRY"
    @AppStorage("biometric_enabled") private var biometricEnabled = false
    
    @State private var showAISettings = false
    @State private var showAISuggestions = false
    @State private var showExport = false
    @State private var showNotifications = false
    @State private var showNotificationSettings = false
    @State private var showSignOutConfirm = false
    @State private var signOutError: String?
    @State private var showCategoryDebug = false
    @State private var showBiometricError = false
    @State private var biometricError: String?
    
    var body: some View {
        List {
            // TODO: Kullanıcı Profili geçici olarak devre dışı
            // User Profile Section
            // Section {
            //     NavigationLink {
            //         AccountInfoView()
            //             .environmentObject(authViewModel)
            //     } label: {
            //         HStack(spacing: 12) {
            //             Circle()
            //                 .fill(LinearGradient(
            //                     colors: [.blue, .purple],
            //                     startPoint: .topLeading,
            //                     endPoint: .bottomTrailing
            //                 ))
            //                 .frame(width: 50, height: 50)
            //                 .overlay(
            //                     Text(authViewModel.userDisplayName.prefix(1).uppercased())
            //                         .font(.title3)
            //                         .fontWeight(.bold)
            //                         .foregroundColor(.white)
            //                 )
            //
            //             VStack(alignment: .leading, spacing: 4) {
            //                 Text(authViewModel.userDisplayName)
            //                     .font(.headline)
            //                 Text(authViewModel.userEmail)
            //                     .font(.caption)
            //                     .foregroundColor(.secondary)
            //             }
            //
            //             Spacer()
            //
            //             Image(systemName: "chevron.right")
            //                 .font(.caption)
            //                 .foregroundColor(.secondary)
            //         }
            //         .padding(.vertical, 4)
            //     }
            // } header: {
            //     Text("Kullanıcı Profili")
            // }
            
            // Accounts Section
            Section {
                NavigationLink {
                    AccountsListView()
                        .environmentObject(accountsViewModel)
                } label: {
                    HStack {
                        Image(systemName: "creditcard.fill")
                            .foregroundColor(.blue)
                        Text(NSLocalizedString("settings.accounts", comment: "Accounts"))
                    }
                }
                
                NavigationLink {
                    CurrencySettingsView()
                } label: {
                    HStack {
                        Image(systemName: "dollarsign.circle.fill")
                            .foregroundColor(.green)
                        Text(NSLocalizedString("settings.currency", comment: "Currency"))
                        Spacer()
                        Text(selectedCurrency)
                            .foregroundColor(.secondary)
                    }
                }
                
                NavigationLink {
                    LanguageSettingsView()
                } label: {
                    HStack {
                        Image(systemName: "globe")
                            .foregroundColor(.blue)
                        Text(NSLocalizedString("settings.language", comment: "Language"))
                        Spacer()
                        Text(getCurrentLanguageName())
                            .foregroundColor(.secondary)
                    }
                }
            } header: {
                Text(NSLocalizedString("finance.title", comment: "Finance"))
            }
            
            // AI Features Section
            Section {
                Button {
                    showAISuggestions = true
                } label: {
                    HStack {
                        Image(systemName: "sparkles")
                            .foregroundColor(.purple)
                        Text(NSLocalizedString("features.ai.suggestions", comment: "AI Suggestions"))
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
            } header: {
                Text(NSLocalizedString("ai.title", comment: "AI"))
            }
            
            // Features Section
            Section {
                NavigationLink {
                    RecurringTransactionsListView()
                        .environmentObject(recurringViewModel)
                        .environmentObject(transactionsViewModel)
                } label: {
                    HStack {
                        Image(systemName: "repeat.circle.fill")
                            .foregroundColor(.orange)
                        Text(NSLocalizedString("features.recurring.transactions", comment: "Recurring Transactions"))
                    }
                }
                
                NavigationLink {
                    AchievementsListView()
                        .environmentObject(achievementsViewModel)
                } label: {
                    HStack {
                        Image(systemName: "trophy.fill")
                            .foregroundColor(.yellow)
                        Text(NSLocalizedString("features.achievements", comment: "Achievements"))
                        Spacer()
                        Text("\(achievementsViewModel.totalPoints)")
                            .foregroundColor(.secondary)
                    }
                }
                
                Button {
                    showNotifications = true
                } label: {
                    HStack {
                        Image(systemName: "bell.fill")
                            .foregroundColor(.red)
                        Text(NSLocalizedString("features.notifications", comment: "Notifications"))
                        Spacer()
                        if notificationsViewModel.unreadCount > 0 {
                            Text("\(notificationsViewModel.unreadCount)")
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 2)
                                .background(Color.red)
                                .cornerRadius(10)
                        }
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
                
                Button {
                    showNotificationSettings = true
                } label: {
                    HStack {
                        Image(systemName: "bell.badge.fill")
                            .foregroundColor(.blue)
                        Text(NSLocalizedString("features.notification.settings", comment: "Notification Settings"))
                        Spacer()
                        if notificationManager.isAuthorized {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                                .font(.caption)
                        }
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
            } header: {
                Text(NSLocalizedString("settings.features", comment: "Features"))
            }
            
            // Security Section - Face ID / Touch ID
            if biometricManager.isAvailable {
                Section {
                    Toggle(isOn: $biometricEnabled) {
                        HStack {
                            Image(systemName: biometricManager.biometricType == .faceID ? "faceid" : "touchid")
                                .foregroundColor(.blue)
                            Text(biometricManager.getBiometricTypeName())
                        }
                    }
                    .onChange(of: biometricEnabled) { newValue in
                        if newValue {
                            // Biometric'i test et
                            Task {
                                do {
                                    let reason = NSLocalizedString("biometric.auth.enable.reason", comment: "Authenticate to enable Face ID / Touch ID")
                                    let success = try await biometricManager.authenticate(reason: reason)
                                    if !success {
                                        biometricEnabled = false
                                    }
                                } catch {
                                    biometricEnabled = false
                                    biometricError = NSLocalizedString("settings.biometric.error.message", comment: "Biometric authentication could not be enabled")
                                    showBiometricError = true
                                }
                            }
                        }
                    }
                } header: {
                    Text(NSLocalizedString("settings.security", comment: "Security"))
                } footer: {
                    if biometricEnabled {
                        Text(String(format: NSLocalizedString("settings.biometric.help.message", comment: "You will be required to authenticate with %@ every time you access the app."), biometricManager.getBiometricTypeName()))
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            
            // Data Management
            Section {
                Button {
                    showExport = true
                } label: {
                    HStack {
                        Image(systemName: "square.and.arrow.up")
                            .foregroundColor(.green)
                        Text(NSLocalizedString("settings.export.import", comment: "Export/Import"))
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
            } header: {
                Text(NSLocalizedString("settings.data.management", comment: "Data Management"))
            }
            
            // TODO: Çıkış Yap butonu geçici olarak devre dışı
            // Account Section - Sign Out
            // Section {
            //     Button(role: .destructive) {
            //         showSignOutConfirm = true
            //     } label: {
            //         HStack {
            //             Image(systemName: "rectangle.portrait.and.arrow.right")
            //                 .foregroundColor(.red)
            //             Text("Çıkış Yap")
            //                 .foregroundColor(.red)
            //         }
            //     }
            // } header: {
            //     Text("Hesap")
            // } footer: {
            //     if let error = signOutError {
            //         Text(error)
            //             .font(.caption)
            //             .foregroundColor(.red)
            //     }
            // }
            
            // App Info
            Section {
                HStack {
                    Text(NSLocalizedString("settings.version", comment: "Version"))
                    Spacer()
                    Text("1.0.0")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text(NSLocalizedString("settings.total.transactions", comment: "Total Transactions"))
                    Spacer()
                    Text("\(transactionsViewModel.transactions.count)")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text(NSLocalizedString("settings.total.categories", comment: "Total Categories"))
                    Spacer()
                    Text("\(transactionsViewModel.categories.count)")
                        .foregroundColor(.secondary)
                }
            } header: {
                Text(NSLocalizedString("settings.app", comment: "App"))
            }
        }
        .navigationTitle(NSLocalizedString("settings.title", comment: "Settings"))
        .navigationBarTitleDisplayMode(.large)
        .sheet(isPresented: $showAISuggestions) {
            AISuggestionsView()
                .environmentObject(transactionsViewModel)
        }
        .sheet(isPresented: $showAISettings) {
            AISettingsView()
        }
        .sheet(isPresented: $showExport) {
            ExportView()
                .environmentObject(transactionsViewModel)
        }
        .sheet(isPresented: $showNotifications) {
            NotificationsView()
                .environmentObject(notificationsViewModel)
        }
        .sheet(isPresented: $showNotificationSettings) {
            NotificationSettingsView()
                .environmentObject(transactionsViewModel)
        }
        .alert(NSLocalizedString("settings.biometric.error", comment: "Biometric Error"), isPresented: $showBiometricError) {
            Button(NSLocalizedString("common.ok", comment: "OK"), role: .cancel) {}
        } message: {
            if let error = biometricError {
                Text(error)
            }
        }
    }
    
    private func getCurrentLanguageName() -> String {
        let savedLanguage = UserDefaults.standard.string(forKey: "selectedLanguage")
        let systemLanguage = savedLanguage ?? String(Locale.preferredLanguages.first?.prefix(2) ?? "tr")
        
        switch systemLanguage {
        case "tr":
            return NSLocalizedString("language.turkish", comment: "Turkish")
        case "en":
            return NSLocalizedString("language.english", comment: "English")
        default:
            return NSLocalizedString("language.turkish", comment: "Turkish")
        }
    }
}

// MARK: - Supporting Views

struct AccountsListView: View {
    @EnvironmentObject var accountsViewModel: AccountsViewModel
    @State private var showAddAccount = false
    
    var body: some View {
            List {
                ForEach(accountsViewModel.accounts, id: \.id) { account in
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(account.name)
                                .font(.subheadline)
                                .fontWeight(.medium)
                            Text(Self.getAccountTypeLocalized(account.type))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        if account.isDefault {
                            Text(NSLocalizedString("accounts.default", comment: "Default"))
                                .font(.caption)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(Color.blue.opacity(0.2))
                                .foregroundColor(.blue)
                                .cornerRadius(8)
                        }
                    }
                    .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                        Button(role: .destructive) {
                            accountsViewModel.deleteAccount(account)
                        } label: {
                            Label(NSLocalizedString("accounts.delete", comment: "Delete"), systemImage: "trash")
                        }
                        
                        Button {
                            accountsViewModel.setDefaultAccount(account)
                        } label: {
                            Label(NSLocalizedString("accounts.set.default", comment: "Set Default"), systemImage: "star")
                        }
                        .tint(.blue)
                    }
                }
            }
            .navigationTitle(NSLocalizedString("accounts.title", comment: "Accounts"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        showAddAccount = true
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showAddAccount) {
                AddAccountView()
                    .environmentObject(accountsViewModel)
            }
        }
    }
    
    struct AddAccountView: View {
        @Environment(\.dismiss) var dismiss
        @EnvironmentObject var accountsViewModel: AccountsViewModel
        
        @State private var name: String = ""
        @State private var type: String = "CASH"
        @State private var currency: String = "TRY"
        
        let accountTypes = ["CASH", "BANK", "CREDIT_CARD", "SAVINGS"]
        let currencies = ["TRY", "USD", "EUR"]
        
        var body: some View {
            NavigationStack {
                Form {
                    Section(NSLocalizedString("accounts.account.info", comment: "Account Information")) {
                        TextField(NSLocalizedString("accounts.account.name", comment: "Account Name"), text: $name)
                        
                        Picker(NSLocalizedString("accounts.account.type", comment: "Account Type"), selection: $type) {
                            Text(NSLocalizedString("accounts.cash", comment: "Cash")).tag("CASH")
                            Text(NSLocalizedString("accounts.bank", comment: "Bank")).tag("BANK")
                            Text(NSLocalizedString("accounts.credit.card", comment: "Credit Card")).tag("CREDIT_CARD")
                            Text(NSLocalizedString("accounts.savings", comment: "Savings")).tag("SAVINGS")
                        }
                        
                        Picker(NSLocalizedString("accounts.currency", comment: "Currency"), selection: $currency) {
                            ForEach(currencies, id: \.self) { curr in
                                Text(curr).tag(curr)
                            }
                        }
                    }
                    
                    Section {
                        Button {
                            saveAccount()
                        } label: {
                            Text(NSLocalizedString("common.save", comment: "Save"))
                                .frame(maxWidth: .infinity)
                                .font(.subheadline)
                        }
                        .disabled(name.isEmpty)
                    }
                }
                .navigationTitle(NSLocalizedString("accounts.add.new", comment: "New Account"))
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                            dismiss()
                        }
                    }
                }
            }
        }
        
        func saveAccount() {
            accountsViewModel.addAccount(name: name, type: type, currency: currency)
            dismiss()
        }
    }
    
    struct RecurringTransactionsListView: View {
        @EnvironmentObject var recurringViewModel: RecurringViewModel
        @EnvironmentObject var transactionsViewModel: TransactionsViewModel
        @State private var showAddRecurring = false
        
    var body: some View {
        List {
                if recurringViewModel.recurringTransactions.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "repeat.circle")
                            .font(.system(size: 60))
                            .foregroundColor(.gray)
                        Text(NSLocalizedString("recurring.no.transactions", comment: "No recurring transactions yet"))
                            .font(.headline)
                            .foregroundColor(.secondary)
                        Text(NSLocalizedString("recurring.no.transactions.hint", comment: "Press + button in top right to add"))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                } else {
                    ForEach(recurringViewModel.recurringTransactions, id: \.id) { recurring in
                        RecurringRow(recurring: recurring)
                            .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                                Button(role: .destructive) {
                                    recurringViewModel.deleteRecurringTransaction(recurring)
                                } label: {
                                    Label(NSLocalizedString("accounts.delete", comment: "Delete"), systemImage: "trash")
                                }
                                
                                Button {
                                    recurringViewModel.toggleRecurringTransaction(recurring)
                                } label: {
                                    Label(recurring.isActive ? NSLocalizedString("recurring.pause", comment: "Pause") : NSLocalizedString("recurring.activate", comment: "Activate"),
                                          systemImage: recurring.isActive ? "pause" : "play")
                                }
                                .tint(.orange)
                            }
                    }
                }
            }
            .navigationTitle(NSLocalizedString("recurring.title", comment: "Recurring Transactions"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        showAddRecurring = true
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showAddRecurring) {
                AddRecurringTransactionView()
                    .environmentObject(recurringViewModel)
                    .environmentObject(transactionsViewModel)
            }
        }
    }
    
    struct RecurringRow: View {
        let recurring: RecurringTransactionEntity
        
        var body: some View {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(recurring.name ?? "")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text(Self.getFrequencyLocalized(recurring.frequency ?? ""))
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 4) {
                    Text(String(format: "%.2f ₺", Double(recurring.amount) / 100.0))
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundColor(recurring.isIncome ? .green : .red)
                    
                    if recurring.isActive {
                        Text(NSLocalizedString("recurring.active", comment: "Active"))
                            .font(.caption)
                            .foregroundColor(.green)
                    } else {
                        Text(NSLocalizedString("recurring.inactive", comment: "Inactive"))
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }
            }
        }
    }
    
    struct AchievementsListView: View {
        @EnvironmentObject var achievementsViewModel: AchievementsViewModel
        @State private var selectedAchievement: AchievementEntity?
        @State private var showAchievementDetail = false
        
        var body: some View {
            ScrollView {
                VStack(spacing: 16) {
                    // Total Points Card
                    VStack(spacing: 8) {
                        Image(systemName: "crown.fill")
                            .font(.system(size: 50))
                            .foregroundColor(.yellow)
                        
                        Text(String(format: NSLocalizedString("achievements.total.points.format", comment: "%d Points"), achievementsViewModel.totalPoints))
                            .font(.title)
                            .fontWeight(.bold)
                        
                        Text(String(format: NSLocalizedString("achievements.unlocked.count.format", comment: "%d / %d Achievement"), achievementsViewModel.achievements.filter { $0.isUnlocked }.count, achievementsViewModel.achievements.count))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 30)
                    .background(
                        RoundedRectangle(cornerRadius: 20)
                            .fill(Color.yellow.opacity(0.1))
                    )
                    .padding()
                    
                    // Achievements List
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 16) {
                        ForEach(achievementsViewModel.achievements, id: \.id) { achievement in
                            AchievementCardLarge(achievement: achievement)
                                .onTapGesture {
                                    selectedAchievement = achievement
                                    showAchievementDetail = true
                                }
                        }
                    }
                    .padding(.horizontal)
                }
                .padding(.vertical)
            }
            .navigationTitle(NSLocalizedString("achievements.title", comment: "Achievements"))
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $showAchievementDetail) {
                if let achievement = selectedAchievement {
                    AchievementDetailSheet(achievement: achievement)
                }
            }
        }
    }
    
    struct AchievementCardLarge: View {
        let achievement: AchievementEntity
        
        var body: some View {
            VStack(spacing: 12) {
                Image(systemName: achievement.icon ?? "star.fill")
                    .font(.system(size: 40))
                    .foregroundColor(achievement.isUnlocked ? .yellow : .gray)
                
                Text(NSLocalizedString(achievement.name ?? "", comment: achievement.name ?? ""))
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)
                    .lineLimit(2)
                
                Text(NSLocalizedString(achievement.achievementDescription ?? "", comment: achievement.achievementDescription ?? ""))
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .lineLimit(2)
                
                if achievement.isUnlocked {
                    HStack(spacing: 4) {
                        Image(systemName: "star.fill")
                            .font(.caption2)
                            .foregroundColor(.yellow)
                        Text(String(format: NSLocalizedString("achievements.points", comment: "Points"), achievement.points))
                            .font(.caption)
                            .fontWeight(.medium)
                    }
                } else {
                    ProgressView(value: Double(achievement.progress), total: Double(achievement.maxProgress))
                        .tint(.blue)
                    Text("\(achievement.progress)/\(achievement.maxProgress)")
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }
            .padding()
            .frame(maxWidth: .infinity, minHeight: 200)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(achievement.isUnlocked ? Color.yellow.opacity(0.1) : Color.gray.opacity(0.1))
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(achievement.isUnlocked ? Color.yellow : Color.clear, lineWidth: 2)
            )
        }
    }
    
    struct NotificationInfoRow: View {
        let icon: String
        let color: Color
        let title: String
        let time: String
        let description: String
        
        var body: some View {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.title2)
                    .foregroundColor(color)
                    .frame(width: 40, height: 40)
                    .background(color.opacity(0.2))
                    .cornerRadius(8)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.subheadline)
                        .fontWeight(.medium)
                    
                    Text(time)
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundColor(.blue)
                    
                    Text(description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
            }
            .padding(.vertical, 4)
        }
    }
    
    struct AddRecurringTransactionView: View {
        @Environment(\.dismiss) var dismiss
        @EnvironmentObject var recurringViewModel: RecurringViewModel
        @EnvironmentObject var transactionsViewModel: TransactionsViewModel
        
        @State private var name: String = ""
        @State private var amount: String = ""
        @State private var isIncome: Bool = false
        @State private var frequency: String = "MONTHLY"
        @State private var selectedCategory: CategoryEntity?
        @State private var startDate: Date = Date()
        @State private var isActive: Bool = true
        
        let frequencies = [
            (NSLocalizedString("recurring.daily", comment: "Daily"), "DAILY"),
            (NSLocalizedString("recurring.weekly", comment: "Weekly"), "WEEKLY"),
            (NSLocalizedString("recurring.monthly", comment: "Monthly"), "MONTHLY"),
            (NSLocalizedString("recurring.yearly", comment: "Yearly"), "YEARLY")
        ]
        
        var body: some View {
            NavigationStack {
                Form {
                    Section(NSLocalizedString("recurring.transaction.info", comment: "Transaction Information")) {
                        TextField(NSLocalizedString("recurring.transaction.name", comment: "Transaction Name"), text: $name)
                        
                        HStack {
                            TextField("0.00", text: $amount)
                                .keyboardType(.decimalPad)
                            Text("₺")
                                .foregroundColor(.secondary)
                        }
                        
                        Picker(NSLocalizedString("recurring.type", comment: "Type"), selection: $isIncome) {
                            Text(NSLocalizedString("recurring.expense", comment: "Expense")).tag(false)
                            Text(NSLocalizedString("recurring.income", comment: "Income")).tag(true)
                        }
                        .pickerStyle(.segmented)
                    }
                    
                    Section(NSLocalizedString("recurring.category", comment: "Category")) {
                        if let category = selectedCategory {
                            HStack {
                                Image(systemName: category.icon ?? "circle.fill")
                                    .foregroundColor(category.uiColor)
                                Text(NSLocalizedString(category.name ?? "", comment: category.name ?? ""))
                                
                                Spacer()
                                
                                Button(NSLocalizedString("recurring.change", comment: "Change")) {
                                    selectedCategory = nil
                                }
                                .font(.caption)
                            }
                        } else {
                            Picker(NSLocalizedString("recurring.select.category", comment: "Select Category"), selection: $selectedCategory) {
                                Text(NSLocalizedString("recurring.select", comment: "Select")).tag(nil as CategoryEntity?)
                                ForEach(transactionsViewModel.categories, id: \.id) { category in
                                    HStack {
                                        Image(systemName: category.icon ?? "circle.fill")
                                        Text(NSLocalizedString(category.name ?? "", comment: category.name ?? ""))
                                    }
                                    .tag(category as CategoryEntity?)
                                }
                            }
                        }
                    }
                    
                    Section(NSLocalizedString("recurring.frequency", comment: "Recurrence Frequency")) {
                        Picker(NSLocalizedString("recurring.frequency", comment: "Frequency"), selection: $frequency) {
                            ForEach(frequencies, id: \.1) { freq in
                                Text(freq.0).tag(freq.1)
                            }
                        }
                        
                        DatePicker(NSLocalizedString("recurring.start.date", comment: "Start Date"),
                                   selection: $startDate,
                                   displayedComponents: .date)
                    }
                    
                    Section(NSLocalizedString("recurring.status", comment: "Status")) {
                        Toggle(NSLocalizedString("recurring.active", comment: "Active"), isOn: $isActive)
                    }
                    
                    Section {
                        Button {
                            saveRecurringTransaction()
                        } label: {
                            Text(NSLocalizedString("common.save", comment: "Save"))
                                .frame(maxWidth: .infinity)
                                .font(.subheadline)
                                .fontWeight(.semibold)
                        }
                        .disabled(name.isEmpty || amount.isEmpty || Double(amount) == nil || selectedCategory == nil)
                    }
                }
                .navigationTitle(NSLocalizedString("recurring.add.transaction", comment: "Add Recurring Transaction"))
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                            dismiss()
                        }
                    }
                }
            }
        }
        
        func saveRecurringTransaction() {
            guard let amountValue = Double(amount),
                  let category = selectedCategory else { return }
            
            recurringViewModel.addRecurringTransaction(
                name: name,
                amount: amountValue,
                category: category,
                frequency: frequency,
                startDate: startDate,
                isIncome: isIncome,
                isActive: isActive
            )
            dismiss()
        }
    }
    // MARK: - Achievement Detail Sheet
    
    struct AchievementDetailSheet: View {
        @Environment(\.dismiss) var dismiss
        let achievement: AchievementEntity
        
        var body: some View {
            NavigationView {
                ZStack {
                    // Background gradient
                    LinearGradient(
                        colors: achievement.isUnlocked ? [Color.yellow.opacity(0.3), Color.orange.opacity(0.2)] : [Color.gray.opacity(0.2), Color.gray.opacity(0.1)],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                    .ignoresSafeArea()
                    
                    ScrollView {
                        VStack(spacing: 30) {
                            // Badge Icon
                            ZStack {
                                Circle()
                                    .fill(achievement.isUnlocked ? Color.yellow : Color.gray)
                                    .frame(width: 150, height: 150)
                                    .shadow(color: achievement.isUnlocked ? .yellow.opacity(0.5) : .clear, radius: 20)
                                
                                Image(systemName: achievement.icon ?? "star.fill")
                                    .font(.system(size: 70))
                                    .foregroundColor(.white)
                            }
                            .padding(.top, 40)
                            
                            // Achievement Name
                            Text(NSLocalizedString(achievement.name ?? "", comment: achievement.name ?? ""))
                                .font(.title)
                                .fontWeight(.bold)
                                .multilineTextAlignment(.center)
                            
                            // Description
                            Text(NSLocalizedString(achievement.achievementDescription ?? "", comment: achievement.achievementDescription ?? ""))
                                .font(.body)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal)
                            
                            // Status Card
                            VStack(spacing: 16) {
                                if achievement.isUnlocked {
                                    // Unlocked Info
                                    VStack(spacing: 12) {
                                        HStack {
                                            Image(systemName: "star.fill")
                                                .foregroundColor(.yellow)
                                            Text(NSLocalizedString("achievements.points", comment: "Points"))
                                                .foregroundColor(.secondary)
                                            Spacer()
                                            Text("\(achievement.points)")
                                                .fontWeight(.bold)
                                        }
                                        
                                        Divider()
                                        
                                        HStack {
                                            Image(systemName: "calendar")
                                                .foregroundColor(.blue)
                                            Text(NSLocalizedString("achievements.earned", comment: "Earned"))
                                                .foregroundColor(.secondary)
                                            Spacer()
                                            if achievement.unlockedAt > 0 {
                                                Text(Date(timeIntervalSince1970: Double(achievement.unlockedAt) / 1000).formatted(date: .abbreviated, time: .omitted))
                                                    .fontWeight(.medium)
                                            }
                                        }
                                    }
                                    .padding()
                                    .background(Color(uiColor: .systemBackground))
                                    .cornerRadius(16)
                                    .shadow(radius: 2)
                                } else {
                                    // Progress Info
                                    VStack(spacing: 12) {
                                        HStack {
                                            Text(NSLocalizedString("achievements.progress", comment: "Progress"))
                                                .font(.subheadline)
                                                .foregroundColor(.secondary)
                                            Spacer()
                                            Text("\(achievement.progress) / \(achievement.maxProgress)")
                                                .font(.subheadline)
                                                .fontWeight(.semibold)
                                        }
                                        
                                        ProgressView(value: Double(achievement.progress), total: Double(achievement.maxProgress))
                                            .tint(.blue)
                                            .scaleEffect(y: 2)
                                        
                                        HStack {
                                            Text(NSLocalizedString("achievements.remaining", comment: "Remaining"))
                                                .foregroundColor(.secondary)
                                            Spacer()
                                            Text("\(achievement.maxProgress - achievement.progress)")
                                                .fontWeight(.bold)
                                                .foregroundColor(.blue)
                                        }
                                    }
                                    .padding()
                                    .background(Color(uiColor: .systemBackground))
                                    .cornerRadius(16)
                                    .shadow(radius: 2)
                                }
                            }
                            .padding(.horizontal)
                            
                            // Motivational Text
                            if !achievement.isUnlocked {
                                Text(NSLocalizedString("achievements.motivational.locked", comment: "Keep going! You're very close to this achievement! 💪"))
                                    .font(.subheadline)
                                    .foregroundColor(.blue)
                                    .padding()
                                    .background(Color.blue.opacity(0.1))
                                    .cornerRadius(12)
                                    .padding(.horizontal)
                            } else {
                                Text(NSLocalizedString("achievements.motivational.unlocked", comment: "Congratulations! You earned this achievement! 🎉"))
                                    .font(.subheadline)
                                    .foregroundColor(.yellow)
                                    .padding()
                                    .background(Color.yellow.opacity(0.1))
                                    .cornerRadius(12)
                                    .padding(.horizontal)
                            }
                            
                            Spacer(minLength: 50)
                        }
                    }
                }
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button {
                            dismiss()
                        } label: {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
        }
    }


// MARK: - Helper Extensions

extension AccountsListView {
    static func getAccountTypeLocalized(_ type: String) -> String {
        switch type {
        case "CASH":
            return NSLocalizedString("accounts.cash", comment: "Cash")
        case "BANK":
            return NSLocalizedString("accounts.bank", comment: "Bank")
        case "CREDIT_CARD":
            return NSLocalizedString("accounts.credit.card", comment: "Credit Card")
        case "SAVINGS":
            return NSLocalizedString("accounts.savings", comment: "Savings")
        default:
            return type
        }
    }
}

extension RecurringRow {
    static func getFrequencyLocalized(_ frequency: String) -> String {
        switch frequency {
        case "DAILY":
            return NSLocalizedString("recurring.daily", comment: "Daily")
        case "WEEKLY":
            return NSLocalizedString("recurring.weekly", comment: "Weekly")
        case "MONTHLY":
            return NSLocalizedString("recurring.monthly", comment: "Monthly")
        case "YEARLY":
            return NSLocalizedString("recurring.yearly", comment: "Yearly")
        default:
            return frequency
        }
    }
}
