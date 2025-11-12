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
                        VStack(alignment: .leading) {
                            Text(category.localizedName)
                                .font(.headline)
                            Text("ID: \(category.id)")
                                .font(.caption)
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
                                Text(category.localizedName)
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
                                Text(category.localizedName)
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

