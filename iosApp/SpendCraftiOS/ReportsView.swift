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
    @State private var selectedChartType: ChartType = .trend
    @State private var showAISuggestions = false
    
    enum Period: String, CaseIterable {
        case week = "Hafta"
        case month = "Ay"
        case year = "Yıl"
    }
    
    enum ChartType: String, CaseIterable {
        case trend = "Trend"
        case category = "Kategori"
        case comparison = "Karşılaştırma"
    }
    
    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(spacing: 16) {
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
                
                // Chart Type Selector
                Picker("Grafik Tipi", selection: $selectedChartType) {
                    ForEach(ChartType.allCases, id: \.self) { type in
                        Text(type.rawValue).tag(type)
                    }
                }
                .pickerStyle(.segmented)
                .padding(.horizontal, 16)
                .padding(.top, 8)
                
                // Charts Section
                VStack(spacing: 16) {
                    switch selectedChartType {
                    case .trend:
                        TrendChartView(
                            viewModel: transactionsViewModel,
                            period: selectedPeriod
                        )
                    case .category:
                        CategoryPieChartView(
                            viewModel: transactionsViewModel
                        )
                    case .comparison:
                        ComparisonBarChartView(
                            viewModel: transactionsViewModel,
                            period: selectedPeriod
                        )
                    }
                }
                .padding(.horizontal, 16)
                
                // AI Suggestions Button
                Button {
                    showAISuggestions = true
                } label: {
                    HStack {
                        Image(systemName: "sparkles")
                            .font(.title3)
                        VStack(alignment: .leading, spacing: 4) {
                            Text("AI Önerileri")
                                .font(.headline)
                            Text("Harcama alışkanlıklarınız hakkında öneriler alın")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        Image(systemName: "chevron.right")
                            .foregroundColor(.secondary)
                    }
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(
                                LinearGradient(
                                    colors: [Color.purple.opacity(0.2), Color.blue.opacity(0.2)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                )
                            )
                    )
                }
                .buttonStyle(.plain)
                .padding(.horizontal, 16)
                .padding(.top, 8)
                
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
                                percentage: spent / max(transactionsViewModel.totalExpense, 0.01)
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
        }
        
        // Banner Ad at bottom
        AdaptiveBannerAdView()
            .background(Color(uiColor: .systemBackground))
            .shadow(color: .black.opacity(0.1), radius: 4, y: -2)
        }
        .navigationTitle("Raporlar")
        .navigationBarTitleDisplayMode(.large)
        .sheet(isPresented: $showAISuggestions) {
            AISuggestionsView()
                .environmentObject(transactionsViewModel)
        }
        .onAppear {
            transactionsViewModel.loadTransactions()
            transactionsViewModel.loadCategories()
            budgetViewModel.loadBudgets()
        }
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
            
            Text(formatCurrency(amount))
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
                    Text(formatCurrency(amount))
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
                        .frame(width: geometry.size.width * CGFloat(min(max(percentage, 0), 1)), height: 6)
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
            
            Text(formatCurrency(amount))
                .font(.subheadline)
                .fontWeight(.semibold)
        }
        .padding(.horizontal)
    }
}

// MARK: - Chart Views

/// Zaman bazlı trend grafiği (Line Chart)
struct TrendChartView: View {
    let viewModel: TransactionsViewModel
    let period: ReportsView.Period
    
    var trendData: [(Date, Double, Double)] {
        switch period {
        case .week:
            return viewModel.dailyTrendData(days: 7)
        case .month:
            return viewModel.dailyTrendData(days: 30)
        case .year:
            return viewModel.monthlyTrendData(months: 12)
        }
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Gelir & Gider Trendi")
                .font(.headline)
            
            if trendData.isEmpty {
                Text("Henüz veri yok")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 60)
            } else {
                Chart {
                    // Gelir çizgisi
                    ForEach(trendData, id: \.0) { item in
                        LineMark(
                            x: .value("Tarih", item.0, unit: period == .year ? .month : .day),
                            y: .value("Gelir", item.1)
                        )
                        .foregroundStyle(.green)
                        .interpolationMethod(.catmullRom)
                        
                        AreaMark(
                            x: .value("Tarih", item.0, unit: period == .year ? .month : .day),
                            y: .value("Gelir", item.1)
                        )
                        .foregroundStyle(
                            LinearGradient(
                                colors: [.green.opacity(0.3), .green.opacity(0.0)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )
                        .interpolationMethod(.catmullRom)
                    }
                    
                    // Gider çizgisi
                    ForEach(trendData, id: \.0) { item in
                        LineMark(
                            x: .value("Tarih", item.0, unit: period == .year ? .month : .day),
                            y: .value("Gider", item.2)
                        )
                        .foregroundStyle(.red)
                        .interpolationMethod(.catmullRom)
                        
                        AreaMark(
                            x: .value("Tarih", item.0, unit: period == .year ? .month : .day),
                            y: .value("Gider", item.2)
                        )
                        .foregroundStyle(
                            LinearGradient(
                                colors: [.red.opacity(0.3), .red.opacity(0.0)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )
                        .interpolationMethod(.catmullRom)
                    }
                }
                .frame(height: 220)
                .chartXAxis {
                    AxisMarks(values: .stride(by: period == .year ? .month : (period == .month ? .day : .day), count: period == .year ? 2 : (period == .month ? 5 : 1))) { _ in
                        AxisGridLine()
                        AxisTick()
                        AxisValueLabel(format: period == .year ? .dateTime.month(.abbreviated) : .dateTime.day())
                    }
                }
                .chartYAxis {
                    AxisMarks { value in
                        AxisGridLine()
                        AxisValueLabel {
                            if let doubleValue = value.as(Double.self) {
                                Text(formatCurrency(doubleValue))
                                    .font(.caption2)
                            }
                        }
                    }
                }
                .padding(.vertical, 8)
                
                // Legend
                HStack(spacing: 20) {
                    HStack(spacing: 6) {
                        Circle()
                            .fill(.green)
                            .frame(width: 10, height: 10)
                        Text("Gelir")
                            .font(.caption)
                    }
                    
                    HStack(spacing: 6) {
                        Circle()
                            .fill(.red)
                            .frame(width: 10, height: 10)
                        Text("Gider")
                            .font(.caption)
                    }
                }
                .padding(.top, 4)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(UIColor.secondarySystemBackground))
        )
    }
}

/// Kategorilere göre harcama dağılımı (Pie Chart)
struct CategoryPieChartView: View {
    let viewModel: TransactionsViewModel
    
    var categoryData: [(CategoryEntity, Double)] {
        viewModel.categorySpendingData()
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Kategori Dağılımı")
                .font(.headline)
            
            if categoryData.isEmpty {
                Text("Henüz harcama yok")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 60)
            } else {
                Chart(categoryData, id: \.0.id) { item in
                    if #available(iOS 17.0, *) {
                        SectorMark(
                            angle: .value("Tutar", item.1),
                            innerRadius: .ratio(0.5),
                            angularInset: 1.5
                        )
                        .cornerRadius(5)
                        .foregroundStyle(item.0.uiColor)
                        .opacity(0.9)
                    } else {
                        // iOS 16 fallback - SectorMark without cornerRadius
                        if #available(iOS 17.0, *) {
                            SectorMark(
                                angle: .value("Tutar", item.1),
                                innerRadius: .ratio(0.5),
                                angularInset: 1.5
                            )
                            .foregroundStyle(item.0.uiColor)
                            .opacity(0.9)
                        } else {
                            // Fallback on earlier versions
                        }
                    }
                }
                .frame(height: 280)
                .padding(.vertical, 8)
                
                // Category List
                VStack(spacing: 12) {
                    ForEach(categoryData.prefix(6), id: \.0.id) { item in
                        HStack(spacing: 12) {
                            Circle()
                                .fill(item.0.uiColor)
                                .frame(width: 12, height: 12)
                            
                            Image(systemName: item.0.icon ?? "circle.fill")
                                .foregroundColor(item.0.uiColor)
                                .frame(width: 24)
                            
                            Text(item.0.name ?? "")
                                .font(.subheadline)
                            
                            Spacer()
                            
                            VStack(alignment: .trailing, spacing: 2) {
                                Text(formatCurrency(item.1))
                                    .font(.subheadline)
                                    .fontWeight(.semibold)
                                
                                Text(String(format: "%.1f%%", (item.1 / viewModel.totalExpense) * 100))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }
                .padding(.top, 8)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(UIColor.secondarySystemBackground))
        )
    }
}

/// Kategorilere göre karşılaştırma (Bar Chart)
struct ComparisonBarChartView: View {
    let viewModel: TransactionsViewModel
    let period: ReportsView.Period
    
    var categoryData: [(CategoryEntity, Double)] {
        viewModel.categorySpendingData().prefix(8).map { $0 }
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Kategori Karşılaştırması")
                .font(.headline)
            
            if categoryData.isEmpty {
                Text("Henüz harcama yok")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 60)
            } else {
                Chart(categoryData, id: \.0.id) { item in
                    BarMark(
                        x: .value("Tutar", item.1),
                        y: .value("Kategori", item.0.name ?? "")
                    )
                    .foregroundStyle(item.0.uiColor.gradient)
                    .cornerRadius(6)
                    .annotation(position: .trailing, alignment: .leading) {
                        Text(formatCurrency(item.1))
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.leading, 4)
                    }
                }
                .frame(height: CGFloat(max(categoryData.count * 45, 200)))
                .chartXAxis {
                    AxisMarks { value in
                        AxisGridLine()
                        AxisValueLabel {
                            if let doubleValue = value.as(Double.self) {
                                Text(formatCurrency(doubleValue))
                                    .font(.caption2)
                            }
                        }
                    }
                }
                .chartYAxis {
                    AxisMarks { value in
                        AxisValueLabel {
                            if let stringValue = value.as(String.self) {
                                // Kategori için icon göster
                                if let category = categoryData.first(where: { $0.0.name == stringValue })?.0 {
                                    HStack(spacing: 6) {
                                        Image(systemName: category.icon ?? "circle.fill")
                                            .font(.caption)
                                            .foregroundColor(category.uiColor)
                                        Text(stringValue)
                                            .font(.caption)
                                    }
                                }
                            }
                        }
                    }
                }
                .padding(.vertical, 8)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(UIColor.secondarySystemBackground))
        )
    }
}
