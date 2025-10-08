import SwiftUI
import BackgroundTasks

@main
struct SpendCraftiOSApp: App {
    let persistenceController = CoreDataStack.shared
    
    init() {
        // Seed initial data on first launch
        CoreDataStack.shared.seedInitialData()
        
        // Register background tasks for recurring transactions
        RecurringAutomationManager.shared.registerBackgroundTasks()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
                .onAppear {
                    // Schedule background task on app launch
                    RecurringAutomationManager.shared.scheduleBackgroundTask()
                }
        }
    }
}
