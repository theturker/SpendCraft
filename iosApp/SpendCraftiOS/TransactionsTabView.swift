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
    @EnvironmentObject var recurringViewModel: RecurringViewModel
    
    @State private var showAddTransaction = false
    @State private var filterType: TransactionFilter = .all
    @State private var transactionToEdit: TransactionEntity?
    
    enum TransactionFilter: String, CaseIterable {
        case all = "Tümü"
        case income = "Gelir"
        case expense = "Gider"
    }
    
    // Make the return type explicit and simplify each branch to help the compiler.
    var filteredTransactions: [TransactionEntity] {
        switch filterType {
        case .all:
            let all: [TransactionEntity] = transactionsViewModel.transactions
            return all
        case .income:
            let income: [TransactionEntity] = transactionsViewModel.transactions.filter { tx in
                tx.isIncome
            }
            return income
        case .expense:
            let expense: [TransactionEntity] = transactionsViewModel.transactions.filter { tx in
                !tx.isIncome
            }
            return expense
        }
    }
    
    // Precompute grouped sections as a simple array to avoid heavy generics in the View builder.
    private var sections: [(date: String, items: [TransactionEntity])] {
        let grouped: [String: [TransactionEntity]] = groupedTransactions()
        let sortedDates: [String] = grouped.keys.sorted(by: >)
        let result: [(String, [TransactionEntity])] = sortedDates.map { date in
            let items: [TransactionEntity] = grouped[date] ?? []
            return (date, items)
        }
        return result
    }
    
    var body: some View {
        VStack(spacing: 0) {
            filterPillsView
            contentView
            
            // Banner Ad at bottom
            AdaptiveBannerAdView()
                .background(Color(uiColor: .systemBackground))
                .shadow(color: .black.opacity(0.1), radius: 4, y: -2)
        }
        .navigationTitle("İşlemler")
        .navigationBarTitleDisplayMode(.large)
        .toolbar {
            toolbarContent
        }
        .sheet(isPresented: $showAddTransaction, onDismiss: {
            // Reload transactions after adding new transaction
            transactionsViewModel.loadTransactions()
        }) {
            AddTransactionView(initialIsIncome: false)
                .environmentObject(transactionsViewModel)
                .environmentObject(achievementsViewModel)
                .environmentObject(notificationsViewModel)
                .environmentObject(recurringViewModel)
        }
        .sheet(item: $transactionToEdit, onDismiss: {
            // Delay to ensure CoreData changes are fully committed
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                // Force a complete reload with context refresh
                let context = CoreDataStack.shared.container.viewContext
                context.refreshAllObjects()
                transactionsViewModel.loadTransactions()
                transactionsViewModel.objectWillChange.send()
            }
        }) { transaction in
            EditTransactionView(transaction: transaction)
                .environmentObject(transactionsViewModel)
        }
        .id(transactionsViewModel.transactions.map { "\($0.id)-\($0.timestampUtcMillis)" }.joined())
    }
    
    // MARK: - Subviews
    
    private var filterPillsView: some View {
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
    }
    
    @ViewBuilder
    private var contentView: some View {
        if filteredTransactions.isEmpty {
            emptyStateView
        } else {
            transactionsList
        }
    }
    
    private var emptyStateView: some View {
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
    }
    
    private var transactionsList: some View {
        List {
            ForEach(sections, id: \.date) { section in
                Section(header: Text(section.date)) {
                    ForEach(section.items, id: \.id) { transaction in
                        transactionRow(for: transaction)
                            .id("\(transaction.id)-\(transaction.timestampUtcMillis)-\(transaction.amountMinor)")
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
    }
    
    private func transactionRow(for transaction: TransactionEntity) -> some View {
        TransactionListRow(transaction: transaction)
            .contentShape(Rectangle())
            .onTapGesture {
                transactionToEdit = transaction
            }
            .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                deleteButton(for: transaction)
            }
            .swipeActions(edge: .leading, allowsFullSwipe: false) {
                editButton(for: transaction)
            }
    }
    
    private func deleteButton(for transaction: TransactionEntity) -> some View {
        Button(role: .destructive) {
            transactionsViewModel.deleteTransaction(transaction)
        } label: {
            Label("Sil", systemImage: "trash")
        }
    }
    
    private func editButton(for transaction: TransactionEntity) -> some View {
        Button {
            transactionToEdit = transaction
        } label: {
            Label("Düzenle", systemImage: "pencil")
        }
        .tint(.blue)
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .navigationBarTrailing) {
            Button {
                showAddTransaction = true
            } label: {
                Image(systemName: "plus.circle.fill")
                    .font(.title2)
            }
        }
    }
    
    func groupedTransactions() -> [String: [TransactionEntity]] {
        // Add explicit types for the grouping key and result to reduce inference work.
        let grouped: [String: [TransactionEntity]] = Dictionary(grouping: filteredTransactions) { transaction -> String in
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
                Text(transaction.formattedAmount)
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
