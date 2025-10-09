//
//  SettingsView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var accountsViewModel: AccountsViewModel
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    @EnvironmentObject var recurringViewModel: RecurringViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    @EnvironmentObject var notificationsViewModel: NotificationsViewModel
    @EnvironmentObject var authViewModel: AuthViewModel
    
    @State private var showAISettings = false
    @State private var showAISuggestions = false
    @State private var showExport = false
    @State private var showNotifications = false
    @State private var showSignOutConfirm = false
    @State private var signOutError: String?

    var body: some View {
        GeometryReader { geometry in
            List {
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
            .padding(.top, geometry.safeAreaInsets.top)
            .padding(.bottom, geometry.safeAreaInsets.bottom)
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
        .confirmationDialog(
            "Çıkış yapılsın mı?",
            isPresented: $showSignOutConfirm,
            titleVisibility: .visible
        ) {
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
            // İsteğe bağlı: Onboarding'i de sıfırlamak isterseniz:
            // UserDefaults.standard.set(false, forKey: "hasCompletedOnboarding")
            // RootView auth değişimini dinlediği için login ekranına düşecektir.
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
                                recurringViewModel.deactivateRecurringTransaction(recurring)
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
                    }
                }
                .padding(.horizontal)
            }
            .padding(.vertical)
        }
        .navigationTitle("Başarılar")
        .navigationBarTitleDisplayMode(.inline)
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
