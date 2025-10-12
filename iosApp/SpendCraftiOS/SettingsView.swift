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
    
    @State private var showAISettings = false
    @State private var showAISuggestions = false
    @State private var showExport = false
    @State private var showNotifications = false
    @State private var showNotificationSettings = false
    @State private var showSignOutConfirm = false
    @State private var signOutError: String?

    var body: some View {
        List {
            // User Profile Section
            Section {
                NavigationLink {
                    AccountInfoView()
                        .environmentObject(authViewModel)
                } label: {
                    HStack(spacing: 12) {
                        Circle()
                            .fill(LinearGradient(
                                colors: [.blue, .purple],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            ))
                            .frame(width: 50, height: 50)
                            .overlay(
                                Text(authViewModel.userDisplayName.prefix(1).uppercased())
                                    .font(.title3)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                            )
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text(authViewModel.userDisplayName)
                                .font(.headline)
                            Text(authViewModel.userEmail)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .padding(.vertical, 4)
                }
            } header: {
                Text("Kullanıcı Profili")
            }
            
            // Accounts Section
            Section {
                NavigationLink {
                    AccountsListView()
                        .environmentObject(accountsViewModel)
                } label: {
                    HStack {
                        Image(systemName: "creditcard.fill")
                            .foregroundColor(.blue)
                        Text("Hesaplar")
                    }
                }
            } header: {
                Text("Finans")
            }
            
            // AI Features Section
            Section {
                Button {
                    showAISuggestions = true
                } label: {
                    HStack {
                        Image(systemName: "sparkles")
                            .foregroundColor(.purple)
                        Text("AI Önerileri")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
                
                Button {
                    showAISettings = true
                } label: {
                    HStack {
                        Image(systemName: "gear")
                            .foregroundColor(.blue)
                        Text("AI Ayarları")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
            } header: {
                Text("🤖 Yapay Zeka")
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
                        Text("Tekrarlayan İşlemler")
                    }
                }
                
                NavigationLink {
                    AchievementsListView()
                        .environmentObject(achievementsViewModel)
                } label: {
                    HStack {
                        Image(systemName: "trophy.fill")
                            .foregroundColor(.yellow)
                        Text("Başarılar")
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
                        Text("Bildirimler")
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
                        Text("Bildirim Ayarları")
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
                Text("Özellikler")
            }
            
            // Data Management
            Section {
                Button {
                    showExport = true
                } label: {
                    HStack {
                        Image(systemName: "square.and.arrow.up")
                            .foregroundColor(.green)
                        Text("Dışa/İçe Aktar")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
            } header: {
                Text("Veri Yönetimi")
            }
            
            // Account Section - Sign Out
            Section {
                Button(role: .destructive) {
                    showSignOutConfirm = true
                } label: {
                    HStack {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                            .foregroundColor(.red)
                        Text("Çıkış Yap")
                            .foregroundColor(.red)
                    }
                }
            } header: {
                Text("Hesap")
            } footer: {
                if let error = signOutError {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            }
            
            // App Info
            Section {
                HStack {
                    Text("Versiyon")
                    Spacer()
                    Text("1.0.0")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text("Toplam İşlem")
                    Spacer()
                    Text("\(transactionsViewModel.transactions.count)")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text("Toplam Kategori")
                    Spacer()
                    Text("\(transactionsViewModel.categories.count)")
                        .foregroundColor(.secondary)
                }
            } header: {
                Text("Uygulama")
            }
        }
        .navigationTitle("Ayarlar")
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
        .alert("Çıkış yapılsın mı?", isPresented: $showSignOutConfirm) {
            Button("Çıkış Yap", role: .destructive) {
                Task {
                    await signOut()
                }
            }
            Button("İptal", role: .cancel) {}
        } message: {
            Text("Hesabınızdan çıkış yapacaksınız.")
        }
    }
    
    private func signOut() async {
        signOutError = nil
        do {
            try await authViewModel.signOut()
        } catch {
            signOutError = error.localizedDescription
        }
    }
}

struct AccountsListView: View {
    @EnvironmentObject var accountsViewModel: AccountsViewModel
    @State private var showAddAccount = false
    
    var body: some View {
        List {
            ForEach(accountsViewModel.accounts, id: \.id) { account in
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(account.name ?? "")
                            .font(.subheadline)
                            .fontWeight(.medium)
                        Text(account.type ?? "")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    
                    Spacer()
                    
                    if account.isDefault {
                        Text("Varsayılan")
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
                        Label("Sil", systemImage: "trash")
                    }
                    
                    Button {
                        accountsViewModel.setDefaultAccount(account)
                    } label: {
                        Label("Varsayılan", systemImage: "star")
                    }
                    .tint(.blue)
                }
            }
        }
        .navigationTitle("Hesaplar")
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
                Section("Hesap Bilgileri") {
                    TextField("Hesap Adı", text: $name)
                    
                    Picker("Hesap Tipi", selection: $type) {
                        Text("Nakit").tag("CASH")
                        Text("Banka").tag("BANK")
                        Text("Kredi Kartı").tag("CREDIT_CARD")
                        Text("Tasarruf").tag("SAVINGS")
                    }
                    
                    Picker("Para Birimi", selection: $currency) {
                        ForEach(currencies, id: \.self) { curr in
                            Text(curr).tag(curr)
                        }
                    }
                }
                
                Section {
                    Button {
                        saveAccount()
                    } label: {
                        Text("Kaydet")
                            .frame(maxWidth: .infinity)
                            .font(.subheadline)
                    }
                    .disabled(name.isEmpty)
                }
            }
            .navigationTitle("Yeni Hesap")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
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
                    Text("Henüz tekrarlayan işlem yok")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    Text("Sağ üst köşedeki + butonuna basarak ekleyin")
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
                                Label("Sil", systemImage: "trash")
                            }
                            
                            Button {
                                recurringViewModel.toggleRecurringTransaction(recurring)
                            } label: {
                                Label(recurring.isActive ? "Duraklat" : "Aktifleştir", 
                                      systemImage: recurring.isActive ? "pause" : "play")
                            }
                            .tint(.orange)
                        }
                }
            }
        }
        .navigationTitle("Tekrarlayan İşlemler")
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
                
                Text(recurring.frequency ?? "")
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
                    Text("Aktif")
                        .font(.caption)
                        .foregroundColor(.green)
                } else {
                    Text("Pasif")
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
                    
                    Text("\(achievementsViewModel.totalPoints) Puan")
                        .font(.title)
                        .fontWeight(.bold)
                    
                    Text("\(achievementsViewModel.achievements.filter { $0.isUnlocked }.count) / \(achievementsViewModel.achievements.count) Başarı")
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
        .navigationTitle("Başarılar")
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
            
            Text(achievement.name ?? "")
                .font(.subheadline)
                .fontWeight(.semibold)
                .multilineTextAlignment(.center)
                .lineLimit(2)
            
            Text(achievement.achievementDescription ?? "")
                .font(.caption)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .lineLimit(2)
            
            if achievement.isUnlocked {
                HStack(spacing: 4) {
                    Image(systemName: "star.fill")
                        .font(.caption2)
                        .foregroundColor(.yellow)
                    Text("\(achievement.points) Puan")
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

struct NotificationSettingsView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @StateObject private var notificationManager = NotificationManager.shared
    @State private var pendingNotifications: [UNNotificationRequest] = []
    @State private var showTestAlert = false
    @State private var testMessage = ""
    
    var body: some View {
        NavigationStack {
            List {
                Section {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Bildirim Durumu")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            Text(notificationManager.isAuthorized ? "Aktif" : "Kapalı")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        if notificationManager.isAuthorized {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                                .font(.title2)
                        } else {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.red)
                                .font(.title2)
                        }
                    }
                    .padding(.vertical, 4)
                    
                    if !notificationManager.isAuthorized {
                        Button {
                            openSettings()
                        } label: {
                            HStack {
                                Image(systemName: "gear")
                                Text("Ayarlarda Bildirimleri Aç")
                                Spacer()
                                Image(systemName: "arrow.right")
                            }
                        }
                    }
                } header: {
                    Text("Durum")
                } footer: {
                    Text("SpendCraft, harcamalarınızı takip etmenize yardımcı olmak için bildirimler gönderir.")
                }
                
                Section {
                    NotificationInfoRow(
                        icon: "sun.max.fill",
                        color: .orange,
                        title: "Sabah Hatırlatması",
                        time: "09:00",
                        description: "Günaydın! Harcamalarınızı kaydetmeyi unutmayın"
                    )
                    
                    NotificationInfoRow(
                        icon: "sun.min.fill",
                        color: .yellow,
                        title: "Öğlen Hatırlatması",
                        time: "13:00",
                        description: "Harcamalarınızı takip ediyor musunuz?"
                    )
                    
                    NotificationInfoRow(
                        icon: "moon.fill",
                        color: .indigo,
                        title: "Akşam Özeti",
                        time: "20:00",
                        description: "Bugünkü harcamalarınızı gözden geçirin"
                    )
                } header: {
                    Text("Günlük Hatırlatmalar (3 adet)")
                } footer: {
                    Text("Her gün 3 defa harcama girişi hatırlatması alırsınız.")
                }
                
                Section {
                    HStack {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Image(systemName: "calendar.badge.clock")
                                    .foregroundColor(.green)
                                Text("Aylık Gelir Hatırlatması")
                                    .font(.subheadline)
                                    .fontWeight(.medium)
                            }
                            
                            Text("Her ayın 1-5. günleri arasında, saat 10:00'da")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            
                            Text("\"Gelirlerinizi girmeyi unutmayın!\"")
                                .font(.caption)
                                .italic()
                                .foregroundColor(.green)
                        }
                        .padding(.vertical, 4)
                    }
                } header: {
                    Text("Gelir Hatırlatmaları")
                } footer: {
                    Text("Maaş ve düzenli gelirlerinizi takip etmeniz için ayın başında 5 gün boyunca hatırlatma alırsınız.")
                }
                
                Section {
                    Button {
                        sendTestNotification()
                    } label: {
                        HStack {
                            Image(systemName: "bell.badge.waveform.fill")
                                .foregroundColor(.purple)
                                .font(.title3)
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Test Bildirimi Gönder")
                                    .font(.subheadline)
                                    .fontWeight(.semibold)
                                Text("3 saniye sonra bildirim alacaksınız")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .padding(.vertical, 4)
                    }
                    .buttonStyle(.plain)
                    .disabled(!notificationManager.isAuthorized)
                } header: {
                    Text("Test")
                } footer: {
                    Text("Bildirimlerin nasıl göründüğünü görmek için test bildirimi gönderin. Uygulamayı arka plana alın.")
                }
                
                Section {
                    HStack {
                        Text("Zamanlanmış Bildirimler")
                            .font(.subheadline)
                        Spacer()
                        Text("\(pendingNotifications.count)")
                            .font(.headline)
                            .foregroundColor(.blue)
                    }
                    
                    Button {
                        Task {
                            pendingNotifications = await notificationManager.getPendingNotifications()
                        }
                    } label: {
                        HStack {
                            Image(systemName: "arrow.clockwise")
                            Text("Bildirimleri Yenile")
                        }
                    }
                    
                    Button(role: .destructive) {
                        notificationManager.cancelAllNotifications()
                        pendingNotifications = []
                    } label: {
                        HStack {
                            Image(systemName: "trash")
                            Text("Tüm Bildirimleri İptal Et")
                        }
                    }
                    
                    Button {
                        notificationManager.scheduleAllNotifications()
                        Task {
                            try? await Task.sleep(nanoseconds: 500_000_000)
                            pendingNotifications = await notificationManager.getPendingNotifications()
                        }
                    } label: {
                        HStack {
                            Image(systemName: "bell.badge.fill")
                            Text("Bildirimleri Yeniden Planla")
                        }
                    }
                    .disabled(!notificationManager.isAuthorized)
                } header: {
                    Text("Yönetim")
                }
            }
            .navigationTitle("Bildirim Ayarları")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Tamam") {
                        dismiss()
                    }
                }
            }
            .onAppear {
                Task {
                    pendingNotifications = await notificationManager.getPendingNotifications()
                }
            }
            .alert(testMessage, isPresented: $showTestAlert) {
                Button("Tamam", role: .cancel) {}
            }
        }
    }
    
    func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
    
    func sendTestNotification() {
        // Önce izin kontrolü yap
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            DispatchQueue.main.async {
                if settings.authorizationStatus != .authorized {
                    self.testMessage = "Bildirim izni verilmemiş. Lütfen ayarlardan bildirimleri açın."
                    self.showTestAlert = true
                    return
                }
                
                let content = UNMutableNotificationContent()
                content.title = "✅ Test Bildirimi"
                content.body = "SpendCraft bildirimleri başarıyla çalışıyor! 🎉"
                content.sound = .default
                content.badge = 1
                
                // 3 saniye sonra gönder
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 3, repeats: false)
                
                let request = UNNotificationRequest(
                    identifier: "test_notification_\(UUID().uuidString)",
                    content: content,
                    trigger: trigger
                )
                
                UNUserNotificationCenter.current().add(request) { error in
                    DispatchQueue.main.async {
                        if let error = error {
                            self.testMessage = "Bildirim gönderilemedi: \(error.localizedDescription)"
                            self.showTestAlert = true
                        } else {
                            self.testMessage = "✅ Test bildirimi gönderildi!\n\n3 saniye içinde bildirim alacaksınız.\n\nBildirimi görmek için uygulamayı arka plana alın (home tuşuna basın)."
                            self.showTestAlert = true
                        }
                    }
                }
            }
        }
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
        ("Günlük", "DAILY"),
        ("Haftalık", "WEEKLY"),
        ("Aylık", "MONTHLY"),
        ("Yıllık", "YEARLY")
    ]
    
    var body: some View {
        NavigationStack {
            Form {
                Section("İşlem Bilgileri") {
                    TextField("İşlem Adı", text: $name)
                    
                    HStack {
                        TextField("0.00", text: $amount)
                            .keyboardType(.decimalPad)
                        Text("₺")
                            .foregroundColor(.secondary)
                    }
                    
                    Picker("Tip", selection: $isIncome) {
                        Text("Gider").tag(false)
                        Text("Gelir").tag(true)
                    }
                    .pickerStyle(.segmented)
                }
                
                Section("Kategori") {
                    if let category = selectedCategory {
                        HStack {
                            Image(systemName: category.icon ?? "circle.fill")
                                .foregroundColor(category.uiColor)
                            Text(category.name ?? "")
                            
                            Spacer()
                            
                            Button("Değiştir") {
                                selectedCategory = nil
                            }
                            .font(.caption)
                        }
                    } else {
                        Picker("Kategori Seç", selection: $selectedCategory) {
                            Text("Seçiniz").tag(nil as CategoryEntity?)
                            ForEach(transactionsViewModel.categories, id: \.id) { category in
                                HStack {
                                    Image(systemName: category.icon ?? "circle.fill")
                                    Text(category.name ?? "")
                                }
                                .tag(category as CategoryEntity?)
                            }
                        }
                    }
                }
                
                Section("Tekrar Sıklığı") {
                    Picker("Sıklık", selection: $frequency) {
                        ForEach(frequencies, id: \.1) { freq in
                            Text(freq.0).tag(freq.1)
                        }
                    }
                    
                    DatePicker("Başlangıç Tarihi",
                              selection: $startDate,
                              displayedComponents: .date)
                }
                
                Section("Durum") {
                    Toggle("Aktif", isOn: $isActive)
                }
                
                Section {
                    Button {
                        saveRecurringTransaction()
                    } label: {
                        Text("Kaydet")
                            .frame(maxWidth: .infinity)
                            .font(.subheadline)
                            .fontWeight(.semibold)
                    }
                    .disabled(name.isEmpty || amount.isEmpty || Double(amount) == nil || selectedCategory == nil)
                }
            }
            .navigationTitle("Tekrarlayan İşlem Ekle")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
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
                        Text(achievement.name ?? "")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        // Description
                        Text(achievement.achievementDescription ?? "")
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
                                        Text("Puan")
                                            .foregroundColor(.secondary)
                                        Spacer()
                                        Text("\(achievement.points)")
                                            .fontWeight(.bold)
                                    }
                                    
                                    Divider()
                                    
                                    HStack {
                                        Image(systemName: "calendar")
                                            .foregroundColor(.blue)
                                        Text("Kazanıldı")
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
                                        Text("İlerleme")
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
                                        Text("Kalan")
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
                            Text("Devam et! Bu başarıya çok yakınsın! 💪")
                                .font(.subheadline)
                                .foregroundColor(.blue)
                                .padding()
                                .background(Color.blue.opacity(0.1))
                                .cornerRadius(12)
                                .padding(.horizontal)
                        } else {
                            Text("Tebrikler! Bu başarıyı kazandın! 🎉")
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
