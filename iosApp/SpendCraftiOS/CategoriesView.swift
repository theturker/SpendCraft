//
//  CategoriesView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI

struct CategoriesView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    
    @State private var showAddBudget = false
    @State private var showAddCategory = false
    @State private var selectedCategory: CategoryEntity?
    
    var body: some View {
        ZStack {
            if transactionsViewModel.categories.isEmpty {
                // Empty State
                VStack(spacing: 20) {
                    Image(systemName: "folder.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.gray)
                    
                    Text(NSLocalizedString("categories.no.categories", comment: "No categories"))
                        .font(.title2)
                        .fontWeight(.semibold)
                    
                    Text(NSLocalizedString("categories.no.categories.hint", comment: "No categories hint"))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 40)
                    
                    Button {
                        showAddCategory = true
                    } label: {
                        Text(NSLocalizedString("categories.add.first", comment: "Add First Category"))
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(12)
                    }
                    .padding(.top)
                }
            } else {
                List {
                    ForEach(transactionsViewModel.categories, id: \.id) { category in
                        CategoryRow(
                            category: category,
                            spent: transactionsViewModel.totalSpentForCategory(category),
                            budget: budgetViewModel.budgetForCategory(category)
                        )
                        .contentShape(Rectangle())
                        .onTapGesture {
                            selectedCategory = category
                            showAddBudget = true
                        }
                        .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                            Button(role: .destructive) {
                                deleteCategory(category)
                            } label: {
                                Label(NSLocalizedString("common.delete", comment: "Delete"), systemImage: "trash")
                            }
                        }
                    }
                }
            }
        }
        .navigationTitle(NSLocalizedString("categories.title", comment: "Categories"))
        .navigationBarTitleDisplayMode(.large)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    showAddCategory = true
                } label: {
                    Image(systemName: "plus")
                }
            }
        }
        .sheet(isPresented: $showAddBudget) {
            if let category = selectedCategory {
                AddBudgetView(category: category)
                    .environmentObject(budgetViewModel)
                    .environmentObject(transactionsViewModel)
            }
        }
        .sheet(isPresented: $showAddCategory) {
            AddCategoryView()
                .environmentObject(transactionsViewModel)
        }
    }
    
    func deleteCategory(_ category: CategoryEntity) {
        transactionsViewModel.deleteCategory(category)
    }
}

struct CategoryRow: View {
    let category: CategoryEntity
    let spent: Double
    let budget: BudgetEntity?
    
    var body: some View {
        VStack(spacing: 12) {
            HStack {
                // Category Icon and Name
                HStack(spacing: 12) {
                    Image(systemName: category.icon ?? "circle.fill")
                        .font(.title2)
                        .foregroundColor(category.uiColor)
                        .frame(width: 44, height: 44)
                        .background(category.uiColor.opacity(0.2))
                        .cornerRadius(12)
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text(NSLocalizedString(category.name ?? "", comment: category.name ?? ""))
                            .font(.subheadline)
                            .fontWeight(.medium)
                        
                        if let budget = budget {
                            Text(String(format: NSLocalizedString("categories.budget", comment: "Budget"), formatCurrency(Double(budget.monthlyLimitMinor) / 100.0)))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        } else {
                            Text(NSLocalizedString("categories.no.budget", comment: "No budget"))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                Spacer()
                
                // Spent Amount
                VStack(alignment: .trailing, spacing: 4) {
                    Text(formatCurrency(spent))
                        .font(.subheadline)
                        .fontWeight(.semibold)
                    
                    if let budget = budget {
                        let progress = spent / (Double(budget.monthlyLimitMinor) / 100.0)
                        Text(String(format: "%.0f%%", progress * 100))
                            .font(.caption)
                            .foregroundColor(progress > 0.9 ? .red : (progress > 0.7 ? .orange : .green))
                    }
                }
            }
            
            // Budget Progress Bar
            if let budget = budget {
                let limit = Double(budget.monthlyLimitMinor) / 100.0
                let progress = min(spent / limit, 1.0)
                
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Rectangle()
                            .fill(Color.gray.opacity(0.2))
                            .frame(height: 6)
                            .cornerRadius(3)
                        
                        Rectangle()
                            .fill(progress > 0.9 ? Color.red : (progress > 0.7 ? Color.orange : Color.green))
                            .frame(width: geometry.size.width * CGFloat(progress), height: 6)
                            .cornerRadius(3)
                    }
                }
                .frame(height: 6)
            }
        }
        .padding(.vertical, 8)
    }
}

struct AddBudgetView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var budgetViewModel: BudgetViewModel
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    
    let category: CategoryEntity
    
    @State private var budgetAmount: String = ""
    
    var currentSpent: Double {
        transactionsViewModel.totalSpentForCategory(category)
    }
    
    var body: some View {
        NavigationView {
            Form {
                Section {
                    HStack {
                        Image(systemName: category.icon ?? "circle.fill")
                            .foregroundColor(category.uiColor)
                        Text(NSLocalizedString(category.name ?? "", comment: category.name ?? ""))
                            .fontWeight(.medium)
                    }
                }
                
                // Current Spending
                Section(NSLocalizedString("categories.this.month.spent", comment: "Spent This Month")) {
                    HStack {
                        Text(formatCurrency(currentSpent))
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        Spacer()
                        
                        if let budget = budgetViewModel.budgetForCategory(category) {
                            let limit = Double(budget.monthlyLimitMinor) / 100.0
                            let percentage = (currentSpent / limit) * 100
                            Text(String(format: "%.0f%%", percentage))
                                .font(.subheadline)
                                .foregroundColor(percentage >= 100 ? .red : (percentage >= 80 ? .orange : .green))
                        }
                    }
                }
                
                Section(NSLocalizedString("add.budget.monthly", comment: "Monthly Budget")) {
                    HStack {
                        TextField("0.00", text: $budgetAmount)
                            .keyboardType(.decimalPad)
                            .font(.title3)
                        Text("₺")
                            .foregroundColor(.secondary)
                    }
                    
                    if let amount = Double(budgetAmount), amount > 0 {
                        let remaining = amount - currentSpent
                        HStack {
                            Text(NSLocalizedString("add.budget.remaining", comment: "Remaining") + ":")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(formatCurrency(remaining))
                                .fontWeight(.medium)
                                .foregroundColor(remaining < 0 ? .red : .green)
                        }
                    }
                }
                
                Section {
                    Button {
                        saveBudget()
                    } label: {
                        Text(NSLocalizedString("common.save", comment: "Save"))
                            .frame(maxWidth: .infinity)
                            .font(.subheadline)
                    }
                    .disabled(budgetAmount.isEmpty || Double(budgetAmount) == nil || Double(budgetAmount) ?? 0 <= 0)
                }
            }
            .navigationTitle(NSLocalizedString("add.budget.title", comment: "Manage Budget"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                        dismiss()
                    }
                }
            }
        }
        .onAppear {
            if let existingBudget = budgetViewModel.budgetForCategory(category) {
                budgetAmount = String(format: "%.2f", Double(existingBudget.monthlyLimitMinor) / 100.0)
            }
        }
    }
    
    func saveBudget() {
        guard let amount = Double(budgetAmount) else { return }
        budgetViewModel.upsertBudget(category: category, monthlyLimit: amount)
        dismiss()
    }
}

struct AddCategoryView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    
    let initialType: String? // Hangi tip için kategori ekleneceği
    
    @State private var categoryName: String = ""
    @State private var selectedIcon: String = "circle.fill"
    @State private var selectedColor: Color = .blue
    @State private var categoryType: String // "income" veya "expense"
    
    init(initialType: String? = nil) {
        self.initialType = initialType
        // Init içinde doğru değeri ata
        _categoryType = State(initialValue: initialType ?? "expense")
    }
    
    let categoryIcons = [
        "cart.fill", "fork.knife", "house.fill", "car.fill", "tram.fill",
        "airplane", "bolt.fill", "bag.fill", "gift.fill", "book.fill",
        "gamecontroller.fill", "film.fill", "heart.fill", "creditcard.fill",
        "pills.fill", "briefcase.fill", "graduationcap.fill", "phone.fill"
    ]
    
    let categoryColors: [(String, Color)] = [
        ("colors.blue", .blue), ("colors.green", .green), ("colors.red", .red),
        ("colors.orange", .orange), ("colors.purple", .purple), ("colors.pink", .pink),
        ("colors.yellow", .yellow), ("colors.brown", .brown), ("colors.indigo", .indigo),
        ("colors.cyan", .cyan), ("colors.mint", .mint), ("colors.teal", .teal)
    ]
    
    var body: some View {
        NavigationStack {
            Form {
                Section(NSLocalizedString("add.category.info", comment: "Category Information")) {
                    TextField(NSLocalizedString("add.category.name", comment: "Category Name"), text: $categoryName)
                        .font(.body)
                    
                    Picker(NSLocalizedString("add.category.type", comment: "Category Type"), selection: $categoryType) {
                        Text(NSLocalizedString("categories.expense", comment: "Expense")).tag("expense")
                                                 Text(NSLocalizedString("categories.income", comment: "Income")).tag("income")
                    }
                    .pickerStyle(.segmented)
                }
                
                Section(NSLocalizedString("add.category.select.icon", comment: "Select Icon")) {
                    LazyVGrid(columns: [GridItem(.adaptive(minimum: 50))], spacing: 12) {
                        ForEach(categoryIcons, id: \.self) { icon in
                            Button {
                                selectedIcon = icon
                            } label: {
                                Image(systemName: icon)
                                    .font(.title2)
                                    .foregroundColor(selectedIcon == icon ? .white : selectedColor)
                                    .frame(width: 50, height: 50)
                                    .background(
                                        RoundedRectangle(cornerRadius: 10)
                                            .fill(selectedIcon == icon ? selectedColor : selectedColor.opacity(0.2))
                                    )
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Section(NSLocalizedString("add.category.select.color", comment: "Select Color")) {
                    LazyVGrid(columns: [GridItem(.adaptive(minimum: 60))], spacing: 12) {
                        ForEach(categoryColors, id: \.0) { colorItem in
                            Button {
                                selectedColor = colorItem.1
                            } label: {
                                VStack(spacing: 6) {
                                    Circle()
                                        .fill(colorItem.1)
                                        .frame(width: 40, height: 40)
                                        .overlay(
                                            Circle()
                                                .stroke(Color.white, lineWidth: selectedColor == colorItem.1 ? 3 : 0)
                                        )
                                        .shadow(color: selectedColor == colorItem.1 ? colorItem.1.opacity(0.5) : .clear, radius: 5)
                                    
                                    Text(NSLocalizedString(colorItem.0, comment: ""))
                                        .font(.caption2)
                                        .foregroundColor(.primary)
                                }
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Section(NSLocalizedString("add.category.preview", comment: "Preview")) {
                    HStack(spacing: 12) {
                        Image(systemName: selectedIcon)
                            .font(.title2)
                            .foregroundColor(selectedColor)
                            .frame(width: 50, height: 50)
                            .background(selectedColor.opacity(0.2))
                            .cornerRadius(12)
                        
                        Text(categoryName.isEmpty ? NSLocalizedString("add.category.name", comment: "Category Name") : categoryName)
                            .font(.headline)
                    }
                    .padding(.vertical, 8)
                }
                
                Section {
                    Button {
                        saveCategory()
                    } label: {
                        Text(NSLocalizedString("add.category.add", comment: "Add Category"))
                            .frame(maxWidth: .infinity)
                            .font(.subheadline)
                            .fontWeight(.semibold)
                    }
                    .disabled(categoryName.isEmpty)
                }
            }
            .navigationTitle(NSLocalizedString("add.category.title", comment: "New Category"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                        dismiss()
                    }
                }
            }
            .onAppear {
                print("📝 AddCategoryView appeared with categoryType: \(categoryType)")
            }
        }
    }
    
    func saveCategory() {
        print("💾 Saving category: \(categoryName), type: \(categoryType)")
        transactionsViewModel.addCategory(
            name: categoryName,
            icon: selectedIcon,
            color: selectedColor,
            type: categoryType
        )
        dismiss()
    }
}
