//
//  TransactionsTabView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI
import CoreData

struct TransactionsTabView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var achievementsViewModel: AchievementsViewModel
    @EnvironmentObject var notificationsViewModel: NotificationsViewModel
    
    @State private var showAddTransaction = false
    @State private var filterType: TransactionFilter = .all
    
    enum TransactionFilter: String, CaseIterable {
        case all = "Tümü"
        case income = "Gelir"
        case expense = "Gider"
    }
    
    var filteredTransactions: [TransactionEntity] {
        switch filterType {
        case .all:
            return transactionsViewModel.transactions
        case .income:
            return transactionsViewModel.transactions.filter { $0.isIncome }
        case .expense:
            return transactionsViewModel.transactions.filter { !$0.isIncome }
        }
    }
    
    var body: some View {
        VStack(spacing: 0) {
            // Filter Pills
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(TransactionFilter.allCases, id: \.self) { filter in
                        FilterPill(
                            title: filter.rawValue,
                            isSelected: filterType == filter
                        ) {
                            filterType = filter
                        }
                    }
                }
                .padding(.horizontal, 16)
            }
            .padding(.vertical, 8)
            .background(Color(UIColor.systemBackground))
            
            // Transactions List
            if filteredTransactions.isEmpty {
                VStack(spacing: 12) {
                    Spacer()
                    Image(systemName: "tray")
                        .font(.system(size: 60))
                        .foregroundColor(.gray)
                    Text("Henüz işlem yok")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    Text("+ butonuna basarak ilk işleminizi ekleyin")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                    Spacer()
                }
                .padding()
            } else {
                List {
                    ForEach(groupedTransactions().keys.sorted(by: >), id: \.self) { date in
                        Section(header: Text(date)) {
                            ForEach(groupedTransactions()[date] ?? [], id: \.id) { transaction in
                                TransactionListRow(transaction: transaction)
                                    .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                                        Button(role: .destructive) {
                                            transactionsViewModel.deleteTransaction(transaction)
                                        } label: {
                                            Label("Sil", systemImage: "trash")
                                        }
                                    }
                            }
                        }
                    }
                }
                .listStyle(.insetGrouped)
            }
        }
        .navigationTitle("İşlemler")
        .navigationBarTitleDisplayMode(.large)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    showAddTransaction = true
                } label: {
                    Image(systemName: "plus.circle.fill")
                        .font(.title2)
                }
            }
        }
        .sheet(isPresented: $showAddTransaction) {
            AddTransactionView()
                .environmentObject(transactionsViewModel)
                .environmentObject(achievementsViewModel)
                .environmentObject(notificationsViewModel)
        }
    }
    
    func groupedTransactions() -> [String: [TransactionEntity]] {
        let grouped = Dictionary(grouping: filteredTransactions) { transaction -> String in
            let date = Date(timeIntervalSince1970: TimeInterval(transaction.timestampUtcMillis / 1000))
            let formatter = DateFormatter()
            formatter.dateFormat = "d MMMM yyyy"
            formatter.locale = Locale(identifier: "tr_TR")
            return formatter.string(from: date)
        }
        return grouped
    }
}

struct FilterPill: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline)
                .fontWeight(isSelected ? .semibold : .regular)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(
                    Capsule()
                        .fill(isSelected ? Color.blue : Color.gray.opacity(0.2))
                )
                .foregroundColor(isSelected ? .white : .primary)
        }
    }
}

struct TransactionListRow: View {
    let transaction: TransactionEntity
    
    var body: some View {
        HStack(spacing: 12) {
            // Category Icon
            if let category = transaction.category {
                Image(systemName: category.icon ?? "circle.fill")
                    .font(.title2)
                    .foregroundColor(category.uiColor)
                    .frame(width: 44, height: 44)
                    .background(category.uiColor.opacity(0.2))
                    .cornerRadius(12)
            }
            
            // Transaction Info
            VStack(alignment: .leading, spacing: 4) {
                Text(transaction.category?.name ?? "Diğer")
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                if let note = transaction.note, !note.isEmpty {
                    Text(note)
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .lineLimit(1)
                }
                
                Text(formatTime(transaction.timestampUtcMillis))
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            // Amount
            VStack(alignment: .trailing, spacing: 4) {
                Text(String(format: "%.2f ₺", transaction.amount))
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(transaction.isIncome ? .green : .red)
                
                if let account = transaction.account {
                    Text(account.name ?? "")
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.vertical, 4)
    }
    
    func formatTime(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: date)
    }
}
