import SwiftUI
import UserNotifications

struct MainAppView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @State private var showSplash = true
    
    var body: some View {
        Group {
            if showSplash {
                SplashView()
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                            withAnimation(.easeInOut(duration: 0.5)) {
                                showSplash = false
                            }
                        }
                    }
                    .transition(.opacity)
            } else if !hasCompletedOnboarding {
                OnboardingView()
                    .transition(.asymmetric(
                        insertion: .move(edge: .bottom),
                        removal: .move(edge: .top)
                    ))
            } else if authViewModel.isAuthenticated {
                AuthenticatedAppView()
                    .environmentObject(authViewModel)
                    .transition(.asymmetric(
                        insertion: .move(edge: .trailing),
                        removal: .move(edge: .leading)
                    ))
            } else {
                AuthFlowView()
                    .transition(.asymmetric(
                        insertion: .move(edge: .leading),
                        removal: .move(edge: .trailing)
                    ))
            }
        }
        .animation(.easeInOut(duration: 0.5), value: authViewModel.isAuthenticated)
        .animation(.easeInOut(duration: 0.5), value: hasCompletedOnboarding)
        .animation(.easeInOut(duration: 0.5), value: showSplash)
    }
}

struct AuthenticatedAppView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @StateObject private var notificationManager = NotificationManager.shared
    @State private var selectedTab = 0
    @State private var hasRequestedNotifications = false
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Ana Dashboard
            ContentView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Ana Sayfa")
                }
                .tag(0)
            
            // İşlemler
            TransactionsTabView()
                .tabItem {
                    Image(systemName: "list.bullet")
                    Text("İşlemler")
                }
                .tag(1)
            
            // Raporlar
            ReportsView()
                .tabItem {
                    Image(systemName: "chart.bar.fill")
                    Text("Raporlar")
                }
                .tag(2)
            
            // Ayarlar
            SettingsView()
                .tabItem {
                    Image(systemName: "gearshape.fill")
                    Text("Ayarlar")
                }
                .tag(3)
        }
        .accentColor(.blue)
        .onAppear {
            // Tab bar görünümünü ayarla
            let appearance = UITabBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = UIColor.systemBackground
            
            UITabBar.appearance().standardAppearance = appearance
            UITabBar.appearance().scrollEdgeAppearance = appearance
            
            // Request notification permission and schedule notifications
            if !hasRequestedNotifications {
                hasRequestedNotifications = true
                Task {
                    let granted = await notificationManager.requestAuthorization()
                    if granted {
                        notificationManager.scheduleAllNotifications()
                    }
                }
            }
        }
    }
}

#Preview {
    MainAppView()
}
