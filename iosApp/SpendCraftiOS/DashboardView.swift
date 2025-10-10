//
//  DashboardView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI
import CoreData

struct DashboardView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    
    @State private var showAddTransaction = false
    @State private var initialTransactionType: Bool? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                // Balance Card
                VStack(spacing: 12) {
                    Text("Toplam Bakiye")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    Text(String(format: "%.2f ₺", transactionsViewModel.currentBalance))
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
                
                // Quick Action Buttons
                HStack(spacing: 16) {
                    Button {
                        initialTransactionType = true // true = income
                        showAddTransaction = true
                    } label: {
                        HStack {
                            Image(systemName: "arrow.down.circle.fill")
                                .foregroundColor(.white)
                            Text("Gelir")
                                .foregroundColor(.white)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .cornerRadius(15)
                    }
                    
                    Button {
                        initialTransactionType = false // false = expense
                        showAddTransaction = true
                    } label: {
                        HStack {
                            Image(systemName: "arrow.up.circle.fill")
                                .foregroundColor(.white)
                            Text("Gider")
                                .foregroundColor(.white)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.red)
                        .cornerRadius(15)
                    }
                }
                .padding(.horizontal, 16)
                
                // Income & Expense Summary Cards
                HStack(spacing: 16) {
                    // Income Summary
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "arrow.down.circle.fill")
                                .foregroundColor(.green)
                            Text("Gelir")
                                .font(.subheadline)
                        }
                        Text(String(format: "%.2f ₺", transactionsViewModel.totalIncome))
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
                            Text("Gider")
                                .font(.subheadline)
                        }
                        Text(String(format: "%.2f ₺", transactionsViewModel.totalExpense))
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
                
                // Streak Card
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Image(systemName: "flame.fill")
                            .foregroundColor(.orange)
                            .font(.title2)
                        Text("Günlük Seri")
                            .font(.headline)
                        Spacer()
                    }
                    
                    HStack(spacing: 20) {
                        VStack {
                            Text("\(achievementsViewModel.currentStreak)")
                                .font(.title)
                                .fontWeight(.bold)
                            Text("Güncel")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Divider()
                            .frame(height: 40)
                        
                        VStack {
                            Text("\(achievementsViewModel.longestStreak)")
                                .font(.title)
                                .fontWeight(.bold)
                            Text("En Uzun")
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
                        Text("Bütçe Durumu")
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
                        Text("Başarılar")
                            .font(.headline)
                        Spacer()
                        Text("\(achievementsViewModel.totalPoints) Puan")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .padding(.horizontal, 16)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            ForEach(achievementsViewModel.achievements.prefix(5), id: \.id) { achievement in
                                AchievementCard(achievement: achievement)
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
                .padding(.vertical, 8)
                
                // Recent Transactions
                VStack(alignment: .leading, spacing: 12) {
                    Text("Son İşlemler")
                        .font(.headline)
                        .padding(.horizontal, 16)
                    
                    ForEach(transactionsViewModel.transactions.prefix(5), id: \.id) { transaction in
                        TransactionRow(transaction: transaction)
                    }
                }
                .padding(.vertical, 8)
            }
        }
        .navigationTitle("Ana Sayfa")
        .navigationBarTitleDisplayMode(.large)
        .sheet(isPresented: $showAddTransaction) {
            AddTransactionView(initialIsIncome: initialTransactionType)
                .environmentObject(transactionsViewModel)
                .environmentObject(achievementsViewModel)
        }
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
                Text(category.name ?? "")
                    .font(.subheadline)
                Spacer()
                Text(String(format: "%.2f / %.2f ₺", spent, Double(budget.monthlyLimitMinor) / 100.0))
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
    let achievement: AchievementEntity
    @State private var showDetailDialog = false
    
    var body: some View {
        Button {
            showDetailDialog = true
        } label: {
            VStack(spacing: 8) {
                Image(systemName: achievement.icon ?? "star.fill")
                    .font(.system(size: 32))
                    .foregroundColor(achievement.isUnlocked ? .yellow : .gray)
                
                Text(achievement.name ?? "")
                    .font(.caption)
                    .fontWeight(.semibold)
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
                
                if achievement.isUnlocked {
                    Text("\(achievement.points) Puan")
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
        .sheet(isPresented: $showDetailDialog) {
            AchievementDetailSheet(achievement: achievement)
                .presentationDetents([.height(400)])
        }
    }
}

struct AchievementDetailSheet: View {
    @Environment(\.dismiss) var dismiss
    let achievement: AchievementEntity
    
    var progressPercentage: Double {
        guard achievement.maxProgress > 0 else { return 0 }
        return Double(achievement.progress) / Double(achievement.maxProgress)
    }
    
    var body: some View {
        VStack(spacing: 0) {
            // Header with close button
            HStack {
                Spacer()
                Button {
                    dismiss()
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .font(.title2)
                        .foregroundColor(.gray)
                }
            }
            .padding()
            
            ScrollView {
                VStack(spacing: 24) {
                    // Icon and Status
                    VStack(spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(achievement.isUnlocked ? 
                                     LinearGradient(colors: [.yellow.opacity(0.3), .orange.opacity(0.3)], 
                                                   startPoint: .topLeading, 
                                                   endPoint: .bottomTrailing) :
                                     LinearGradient(colors: [.gray.opacity(0.2), .gray.opacity(0.1)], 
                                                   startPoint: .topLeading, 
                                                   endPoint: .bottomTrailing))
                                .frame(width: 120, height: 120)
                            
                            Image(systemName: achievement.icon ?? "star.fill")
                                .font(.system(size: 60))
                                .foregroundColor(achievement.isUnlocked ? .yellow : .gray)
                        }
                        
                        if achievement.isUnlocked {
                            HStack(spacing: 8) {
                                Image(systemName: "checkmark.seal.fill")
                                    .foregroundColor(.green)
                                Text("Tamamlandı!")
                                    .fontWeight(.semibold)
                                    .foregroundColor(.green)
                            }
                            .font(.headline)
                        }
                    }
                    
                    // Title
                    Text(achievement.name ?? "Başarı")
                        .font(.title2)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                    
                    // Description
                    Text(achievement.achievementDescription ?? "")
                        .font(.body)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                    
                    // Progress or Points
                    if achievement.isUnlocked {
                        VStack(spacing: 12) {
                            HStack {
                                Image(systemName: "star.fill")
                                    .foregroundColor(.yellow)
                                Text("\(achievement.points) Puan Kazandınız!")
                                    .font(.headline)
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.yellow.opacity(0.15))
                            )
                        }
                        .padding(.horizontal)
                    } else {
                        VStack(spacing: 16) {
                            // Progress Bar
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text("İlerleme")
                                        .font(.subheadline)
                                        .fontWeight(.medium)
                                    Spacer()
                                    Text("\(achievement.progress) / \(achievement.maxProgress)")
                                        .font(.subheadline)
                                        .fontWeight(.bold)
                                        .foregroundColor(.blue)
                                }
                                
                                GeometryReader { geometry in
                                    ZStack(alignment: .leading) {
                                        RoundedRectangle(cornerRadius: 8)
                                            .fill(Color.gray.opacity(0.2))
                                            .frame(height: 12)
                                        
                                        RoundedRectangle(cornerRadius: 8)
                                            .fill(
                                                LinearGradient(
                                                    colors: [.blue, .purple],
                                                    startPoint: .leading,
                                                    endPoint: .trailing
                                                )
                                            )
                                            .frame(width: geometry.size.width * CGFloat(progressPercentage), height: 12)
                                    }
                                }
                                .frame(height: 12)
                                
                                HStack {
                                    Text("\(Int(progressPercentage * 100))% Tamamlandı")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                    Spacer()
                                    Text("Kalan: \(achievement.maxProgress - achievement.progress)")
                                        .font(.caption)
                                        .foregroundColor(.orange)
                                        .fontWeight(.medium)
                                }
                            }
                            .padding()
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.blue.opacity(0.05))
                            )
                            
                            // Reward info
                            HStack {
                                Image(systemName: "gift.fill")
                                    .foregroundColor(.purple)
                                Text("Kazanacağınız: \(achievement.points) Puan")
                                    .font(.subheadline)
                                    .fontWeight(.medium)
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.purple.opacity(0.1))
                            )
                        }
                        .padding(.horizontal)
                    }
                }
                .padding(.vertical)
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
                Text(transaction.category?.name ?? "Diğer")
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
