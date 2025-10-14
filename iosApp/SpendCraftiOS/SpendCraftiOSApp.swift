import SwiftUI
import BackgroundTasks
import Firebase

@main
struct SpendCraftiOSApp: App {
    let persistenceController = CoreDataStack.shared
    
    init() {
        // Configure Firebase
        FirebaseApp.configure()
        
        // Initialize Google Mobile Ads
        AdsManager.shared.initializeAds()
        
        // Seed initial data on first launch
        CoreDataStack.shared.seedInitialData()
        
        // Register background tasks for recurring transactions
        RecurringAutomationManager.shared.registerBackgroundTasks()
        
        // Request notification authorization on first launch
        Task {
            _ = await NotificationManager.shared.requestAuthorization()
        }
        
        // Preload interstitial ads for better UX (hemen yükle)
        DispatchQueue.main.async {
            print("📱 App started - Preloading interstitial ads...")
            AdsManager.shared.loadInterstitialAd()
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
                    
                    // Preload interstitial ad if needed
                    AdsManager.shared.loadInterstitialAd()
                }
        }
    }
}
