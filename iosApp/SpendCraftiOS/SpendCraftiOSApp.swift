import SwiftUI
import BackgroundTasks
import Firebase

@main
struct SpendCraftiOSApp: App {
    let persistenceController = CoreDataStack.shared
    
    init() {
        // Configure Firebase
        FirebaseApp.configure()
        
        // Seed initial data on first launch
        CoreDataStack.shared.seedInitialData()
        
        // Register background tasks for recurring transactions
        RecurringAutomationManager.shared.registerBackgroundTasks()
        
        // Request notification authorization on first launch
        Task {
            _ = await NotificationManager.shared.requestAuthorization()
        }
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
                .onAppear {
                    // Schedule background task on app launch
                    RecurringAutomationManager.shared.scheduleBackgroundTask()
                    
                    // Schedule all notifications
                    NotificationManager.shared.scheduleAllNotifications()
                    
                    // Clear app icon badge on app open
                    NotificationManager.shared.clearBadge()
                }
        }
    }
}
