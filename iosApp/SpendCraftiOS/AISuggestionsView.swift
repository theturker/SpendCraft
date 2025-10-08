//
//  AISuggestionsView.swift
//  SpendCraftiOS
//
//  AI Önerileri Ekranı
//

import SwiftUI

struct AISuggestionsView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @StateObject private var aiManager = AIManager()
    @Environment(\.dismiss) private var dismiss
    
    @State private var selectedType: AdviceType?
    
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
                        
                        // Generate Button
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
                                    value: "₺\(String(format: "%.0f", transactionsViewModel.totalIncome))",
                                    icon: "arrow.down.circle.fill",
                                    color: .green
                                )
                                
                                FinancialStatCard(
                                    title: "Gider",
                                    value: "₺\(String(format: "%.0f", transactionsViewModel.totalExpense))",
                                    icon: "arrow.up.circle.fill",
                                    color: .red
                                )
                            }
                            .padding(.horizontal)
                            
                            HStack(spacing: 12) {
                                FinancialStatCard(
                                    title: "Bakiye",
                                    value: "₺\(String(format: "%.0f", transactionsViewModel.currentBalance))",
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
            .navigationTitle("AI Önerileri")
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
        }
    }
    
    // MARK: - Generate Advice
    
    private func generateAdvice() {
        guard let type = selectedType else { return }
        
        Task {
            await aiManager.generateAdvice(
                type: type,
                income: transactionsViewModel.totalIncome,
                expenses: transactionsViewModel.totalExpense,
                categoryBreakdown: categoryBreakdown
            )
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
