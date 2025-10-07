//
//  ReportsView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI
import Charts

struct ReportsView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    
    @State private var selectedPeriod: Period = .month
    
    enum Period: String, CaseIterable {
        case week = "Hafta"
        case month = "Ay"
        case year = "Yıl"
    }
    
    var body: some View {
        GeometryReader { geometry in
            ScrollView {
                VStack(spacing: 12) {
                // Period Selector
                Picker("Dönem", selection: $selectedPeriod) {
                    ForEach(Period.allCases, id: \.self) { period in
                        Text(period.rawValue).tag(period)
                    }
                }
                .pickerStyle(.segmented)
                .padding(.horizontal, 16)
                .padding(.top, 8)
                
                // Summary Cards
                HStack(spacing: 16) {
                    SummaryCard(
                        title: "Toplam Gelir",
                        amount: transactionsViewModel.totalIncome,
                        color: .green,
                        icon: "arrow.down.circle.fill"
                    )
                    
                    SummaryCard(
                        title: "Toplam Gider",
                        amount: transactionsViewModel.totalExpense,
                        color: .red,
                        icon: "arrow.up.circle.fill"
                    )
                }
                .padding(.horizontal, 16)
                .padding(.top, 4)
                
                // Category Breakdown
                VStack(alignment: .leading, spacing: 12) {
                    Text("Kategoriye Göre Harcamalar")
                        .font(.headline)
                        .padding(.horizontal)
                    
                    ForEach(transactionsViewModel.categories, id: \.id) { category in
                        let spent = transactionsViewModel.totalSpentForCategory(category)
                        if spent > 0 {
                            CategorySpendingRow(
                                category: category,
                                amount: spent,
                                percentage: spent / transactionsViewModel.totalExpense
                            )
                        }
                    }
                }
                .padding(.vertical)
                
                // Budget Status
                if !budgetViewModel.budgets.isEmpty {
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Bütçe Durumu")
                            .font(.headline)
                            .padding(.horizontal)
                        
                        ForEach(budgetViewModel.budgets, id: \.id) { budget in
                            if let category = budget.category {
                                BudgetStatusRow(
                                    budget: budget,
                                    category: category,
                                    spent: budgetViewModel.spentForCategory(category),
                                    progress: budgetViewModel.budgetProgress(budget)
                                )
                            }
                        }
                    }
                    .padding(.vertical)
                }
                
                // Top Categories
                VStack(alignment: .leading, spacing: 16) {
                    Text("En Çok Harcanan Kategoriler")
                        .font(.headline)
                        .padding(.horizontal)
                    
                    ForEach(topCategories().prefix(5), id: \.0.id) { category, amount in
                        TopCategoryRow(category: category, amount: amount)
                    }
                }
                .padding(.vertical)
                }
                .padding(.top)
                .padding(.top, geometry.safeAreaInsets.top)
                .padding(.bottom, geometry.safeAreaInsets.bottom + 20)
            }
        }
        .navigationTitle("Raporlar")
        .navigationBarTitleDisplayMode(.large)
    }
    
    func topCategories() -> [(CategoryEntity, Double)] {
        let categorySpending: [(CategoryEntity, Double)] = transactionsViewModel.categories.map { category in
            (category, transactionsViewModel.totalSpentForCategory(category))
        }
        return categorySpending.sorted { $0.1 > $1.1 }
    }
}

struct SummaryCard: View {
    let title: String
    let amount: Double
    let color: Color
    let icon: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(color)
                Text(title)
                    .font(.subheadline)
            }
            
            Text(String(format: "%.2f ₺", amount))
                .font(.title2)
                .fontWeight(.bold)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(color.opacity(0.1))
        )
    }
}

struct CategorySpendingRow: View {
    let category: CategoryEntity
    let amount: Double
    let percentage: Double
    
    var body: some View {
        VStack(spacing: 8) {
            HStack {
                Image(systemName: category.icon ?? "circle.fill")
                    .foregroundColor(category.uiColor)
                
                Text(category.name ?? "")
                    .font(.subheadline)
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 2) {
                    Text(String(format: "%.2f ₺", amount))
                        .font(.subheadline)
                        .fontWeight(.semibold)
                    Text(String(format: "%.0f%%", percentage * 100))
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(height: 6)
                        .cornerRadius(3)
                    
                    Rectangle()
                        .fill(category.uiColor)
                        .frame(width: geometry.size.width * CGFloat(percentage), height: 6)
                        .cornerRadius(3)
                }
            }
            .frame(height: 6)
        }
        .padding(.horizontal)
    }
}

struct BudgetStatusRow: View {
    let budget: BudgetEntity
    let category: CategoryEntity
    let spent: Double
    let progress: Double
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: category.icon ?? "circle.fill")
                    .foregroundColor(category.uiColor)
                
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
                        .fill(progress > 0.9 ? Color.red : (progress > 0.7 ? Color.orange : Color.green))
                        .frame(width: geometry.size.width * CGFloat(min(progress, 1.0)), height: 8)
                        .cornerRadius(4)
                }
            }
            .frame(height: 8)
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(UIColor.secondarySystemBackground))
        )
        .padding(.horizontal)
    }
}

struct TopCategoryRow: View {
    let category: CategoryEntity
    let amount: Double
    
    var body: some View {
        HStack {
            Image(systemName: category.icon ?? "circle.fill")
                .font(.title3)
                .foregroundColor(category.uiColor)
                .frame(width: 40, height: 40)
                .background(category.uiColor.opacity(0.2))
                .cornerRadius(10)
            
            Text(category.name ?? "")
                .font(.subheadline)
            
            Spacer()
            
            Text(String(format: "%.2f ₺", amount))
                .font(.subheadline)
                .fontWeight(.semibold)
        }
        .padding(.horizontal)
    }
}