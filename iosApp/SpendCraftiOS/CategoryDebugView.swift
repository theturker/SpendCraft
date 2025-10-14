//
//  CategoryDebugView.swift
//  SpendCraftiOS
//
//  Debug view to check category types
//

import SwiftUI

struct CategoryDebugView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    
    var body: some View {
        NavigationView {
            List {
                Section("Tüm Kategoriler (\(transactionsViewModel.categories.count))") {
                    ForEach(transactionsViewModel.categories, id: \.id) { category in
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Image(systemName: category.icon ?? "circle.fill")
                                    .foregroundColor(category.uiColor)
                                Text(category.name ?? "?")
                                    .font(.headline)
                                Spacer()
                                Text(category.type ?? "NO TYPE")
                                    .font(.caption)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background(category.type == "income" ? Color.green.opacity(0.2) : Color.red.opacity(0.2))
                                    .cornerRadius(8)
                            }
                            
                            Text("ID: \(category.id)")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            
                            Text("Color: \(category.color ?? "no color")")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .padding(.vertical, 4)
                    }
                }
                
                Section("Gelir Kategorileri") {
                    let incomeCategories = transactionsViewModel.categoriesForType(true)
                    if incomeCategories.isEmpty {
                        Text("Gelir kategorisi yok")
                            .foregroundColor(.secondary)
                    } else {
                        ForEach(incomeCategories, id: \.id) { category in
                            HStack {
                                Image(systemName: category.icon ?? "circle.fill")
                                    .foregroundColor(category.uiColor)
                                Text(category.name ?? "?")
                            }
                        }
                    }
                }
                
                Section("Gider Kategorileri") {
                    let expenseCategories = transactionsViewModel.categoriesForType(false)
                    if expenseCategories.isEmpty {
                        Text("Gider kategorisi yok")
                            .foregroundColor(.secondary)
                    } else {
                        ForEach(expenseCategories, id: \.id) { category in
                            HStack {
                                Image(systemName: category.icon ?? "circle.fill")
                                    .foregroundColor(category.uiColor)
                                Text(category.name ?? "?")
                            }
                        }
                    }
                }
                
                Section("Eylemler") {
                    Button("Kategorileri Yeniden Yükle") {
                        transactionsViewModel.loadCategories()
                    }
                }
            }
            .navigationTitle("Kategori Debug")
            .onAppear {
                transactionsViewModel.loadCategories()
            }
        }
    }
}

