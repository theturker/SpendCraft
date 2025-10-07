import SwiftUI

struct ContentView: View {
    @StateObject private var transactionsViewModel = TransactionsViewModel()
    @StateObject private var achievementsViewModel = AchievementsViewModel()
    @StateObject private var budgetViewModel = BudgetViewModel(transactionsViewModel: TransactionsViewModel())
    @StateObject private var recurringViewModel = RecurringViewModel(transactionsViewModel: TransactionsViewModel())
    @StateObject private var accountsViewModel = AccountsViewModel()

    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack {
                DashboardView()
                    .environmentObject(transactionsViewModel)
                    .environmentObject(budgetViewModel)
                    .environmentObject(achievementsViewModel)
            }
            .tabItem {
                Label("Ana Sayfa", systemImage: "house.fill")
            }
            .tag(0)

            NavigationStack {
                TransactionsTabView()
                    .environmentObject(transactionsViewModel)
                    .environmentObject(achievementsViewModel)
            }
            .tabItem {
                Label("İşlemler", systemImage: "list.bullet")
            }
            .tag(1)

            NavigationStack {
                ReportsView()
                    .environmentObject(transactionsViewModel)
                    .environmentObject(budgetViewModel)
            }
            .tabItem {
                Label("Raporlar", systemImage: "chart.bar.fill")
            }
            .tag(2)

            NavigationStack {
                CategoriesView()
                    .environmentObject(transactionsViewModel)
                    .environmentObject(budgetViewModel)
            }
            .tabItem {
                Label("Kategoriler", systemImage: "folder.fill")
            }
            .tag(3)

            NavigationStack {
                SettingsView()
                    .environmentObject(transactionsViewModel)
                    .environmentObject(accountsViewModel)
                    .environmentObject(budgetViewModel)
                    .environmentObject(recurringViewModel)
                    .environmentObject(achievementsViewModel)
            }
            .tabItem {
                Label("Ayarlar", systemImage: "gearshape.fill")
            }
            .tag(4)
        }
        .ignoresSafeArea(.keyboard, edges: .bottom)
        .onAppear {
            CoreDataStack.shared.seedInitialData()
            transactionsViewModel.loadData()
            budgetViewModel.loadBudgets()
            recurringViewModel.loadRecurringTransactions()
            accountsViewModel.loadAccounts()
            achievementsViewModel.loadAchievements()

            // Check achievements on app launch
            achievementsViewModel.checkAchievements(
                transactionCount: transactionsViewModel.transactions.count,
                totalSpent: transactionsViewModel.totalExpense,
                categories: transactionsViewModel.categories.count
            )
            recurringViewModel.executeDueTransactions {
                transactionsViewModel.loadData() // Reload transactions after executing recurring ones
            }
        }
        .onChange(of: transactionsViewModel.transactions.count) { count in
            // Update achievements when transactions change
            achievementsViewModel.checkAchievements(
                transactionCount: count,
                totalSpent: transactionsViewModel.totalExpense,
                categories: transactionsViewModel.categories.count
            )
        }
    }
}
