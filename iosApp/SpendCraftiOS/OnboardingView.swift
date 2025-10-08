//
//  OnboardingView.swift
//  SpendCraftiOS
//
//  iOS Native Onboarding - 6 Sayfa
//

import SwiftUI

// MARK: - AuthViewModel

@MainActor
class AuthViewModel: ObservableObject {
    @Published var isAuthenticated = false
    @Published var currentUser: UserModel?
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    // Basit kullanıcı modeli
    struct UserModel: Codable {
        let id: String
        let email: String
        let displayName: String
        let isEmailVerified: Bool
    }
    
    init() {
        // Uygulama başlangıcında auth durumunu kontrol et
        checkAuthState()
    }
    
    // MARK: - Auth State Management
    
    private func checkAuthState() {
        // Basit local storage kontrolü
        if let userData = UserDefaults.standard.data(forKey: "current_user"),
           let user = try? JSONDecoder().decode(UserModel.self, from: userData) {
            currentUser = user
            isAuthenticated = true
        }
    }
    
    // MARK: - Authentication Methods
    
    func signIn(email: String, password: String) async throws {
        isLoading = true
        errorMessage = nil
        
        // Simulated delay
        try await Task.sleep(nanoseconds: 1_000_000_000)
        
        // Basit validation
        guard !email.isEmpty, !password.isEmpty else {
            isLoading = false
            errorMessage = "E-posta ve şifre gerekli"
            throw AuthError.invalidInput
        }
        
        guard email.contains("@") else {
            isLoading = false
            errorMessage = "Geçerli bir e-posta adresi girin"
            throw AuthError.invalidEmail
        }
        
        guard password.count >= 6 else {
            isLoading = false
            errorMessage = "Şifre en az 6 karakter olmalı"
            throw AuthError.weakPassword
        }
        
        // Demo için basit kullanıcı oluştur
        let user = UserModel(
            id: UUID().uuidString,
            email: email,
            displayName: email.components(separatedBy: "@").first ?? "Kullanıcı",
            isEmailVerified: true
        )
        
        // Local storage'a kaydet
        if let userData = try? JSONEncoder().encode(user) {
            UserDefaults.standard.set(userData, forKey: "current_user")
        }
        
        currentUser = user
        isAuthenticated = true
        isLoading = false
    }
    
    func register(name: String, email: String, password: String) async throws {
        isLoading = true
        errorMessage = nil
        
        // Simulated delay
        try await Task.sleep(nanoseconds: 1_500_000_000)
        
        // Basit validation
        guard !name.isEmpty, !email.isEmpty, !password.isEmpty else {
            isLoading = false
            errorMessage = "Tüm alanlar gerekli"
            throw AuthError.invalidInput
        }
        
        guard email.contains("@") else {
            isLoading = false
            errorMessage = "Geçerli bir e-posta adresi girin"
            throw AuthError.invalidEmail
        }
        
        guard password.count >= 6 else {
            isLoading = false
            errorMessage = "Şifre en az 6 karakter olmalı"
            throw AuthError.weakPassword
        }
        
        // Demo için basit kullanıcı oluştur
        let user = UserModel(
            id: UUID().uuidString,
            email: email,
            displayName: name,
            isEmailVerified: false
        )
        
        // Local storage'a kaydet
        if let userData = try? JSONEncoder().encode(user) {
            UserDefaults.standard.set(userData, forKey: "current_user")
        }
        
        currentUser = user
        isAuthenticated = true
        isLoading = false
    }
    
    func sendPasswordReset(email: String) async throws {
        isLoading = true
        errorMessage = nil
        
        // Simulated delay
        try await Task.sleep(nanoseconds: 1_000_000_000)
        
        guard !email.isEmpty, email.contains("@") else {
            isLoading = false
            errorMessage = "Geçerli bir e-posta adresi girin"
            throw AuthError.invalidEmail
        }
        
        // Demo için başarılı mesaj
        isLoading = false
        print("Password reset email sent to: \(email)")
    }
    
    func signOut() async throws {
        // Local storage'dan kullanıcıyı kaldır
        UserDefaults.standard.removeObject(forKey: "current_user")
        
        currentUser = nil
        isAuthenticated = false
    }
    
    // MARK: - User Info
    
    var userDisplayName: String {
        return currentUser?.displayName ?? "Kullanıcı"
    }
    
    var userEmail: String {
        return currentUser?.email ?? ""
    }
    
    var isEmailVerified: Bool {
        return currentUser?.isEmailVerified ?? false
    }
    
    // MARK: - Helper Methods
    
    func clearError() {
        errorMessage = nil
    }
}

// MARK: - Auth Errors

enum AuthError: Error, LocalizedError {
    case invalidInput
    case invalidEmail
    case weakPassword
    case passwordMismatch
    case userNotFound
    case networkError
    
    var errorDescription: String? {
        switch self {
        case .invalidInput:
            return "Geçersiz giriş bilgileri"
        case .invalidEmail:
            return "Geçersiz e-posta adresi"
        case .weakPassword:
            return "Şifre çok zayıf"
        case .passwordMismatch:
            return "Şifreler eşleşmiyor"
        case .userNotFound:
            return "Kullanıcı bulunamadı"
        case .networkError:
            return "Ağ hatası"
        }
    }
}

// MARK: - Auth State Enum

enum AuthState {
    case authenticated(AuthViewModel.UserModel)
    case unauthenticated
    case loading
}

struct OnboardingPage: Identifiable {
    let id = UUID()
    let title: String
    let description: String
    let systemImage: String
    let gradientColors: [Color]
}

struct OnboardingView: View {
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @State private var currentPage = 0
    
    let pages = [
        OnboardingPage(
            title: "Paratik'e Hoş Geldiniz",
            description: "Kişisel finans yönetiminizi kolaylaştıran akıllı asistanınız. Harcamalarınızı takip edin, bütçe oluşturun ve finansal hedeflerinize ulaşın.",
            systemImage: "chart.line.uptrend.xyaxis",
            gradientColors: [Color.blue, Color.purple]
        ),
        OnboardingPage(
            title: "Akıllı Kategorilendirme",
            description: "İşlemlerinizi otomatik olarak kategorilere ayırın. Harcama alışkanlıklarınızı analiz edin ve nerede tasarruf edebileceğinizi keşfedin.",
            systemImage: "square.grid.2x2",
            gradientColors: [Color.green, Color.mint]
        ),
        OnboardingPage(
            title: "Bütçe Yönetimi",
            description: "Kategoriler için bütçe belirleyin ve harcamalarınızı kontrol altında tutun. Bütçe aşımlarında anında bildirim alın.",
            systemImage: "chart.pie.fill",
            gradientColors: [Color.orange, Color.yellow]
        ),
        OnboardingPage(
            title: "AI Destekli Öneriler",
            description: "Yapay zeka teknolojisi ile kişiselleştirilmiş finansal öneriler alın. Harcama alışkanlıklarınızı optimize edin.",
            systemImage: "sparkles",
            gradientColors: [Color.purple, Color.pink]
        ),
        OnboardingPage(
            title: "Detaylı Raporlar",
            description: "Gelir-gider analizleri, kategori bazında harcama dağılımları ve trend analizleri ile finansal durumunuzu takip edin.",
            systemImage: "chart.bar.fill",
            gradientColors: [Color.red, Color.orange]
        ),
        OnboardingPage(
            title: "Hemen Başlayın",
            description: "Tüm özellikler sizin için hazır! Finansal özgürlük yolculuğunuza şimdi başlayın.",
            systemImage: "checkmark.circle.fill",
            gradientColors: [Color.cyan, Color.blue]
        )
    ]
    
    var body: some View {
        ZStack {
            // Background gradient
            LinearGradient(
                colors: pages[currentPage].gradientColors,
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
            .animation(.easeInOut(duration: 0.5), value: currentPage)
            
            VStack {
                // Skip button
                HStack {
                    Spacer()
                    if currentPage < pages.count - 1 {
                        Button {
                            completeOnboarding()
                        } label: {
                            Text("Atla")
                                .foregroundColor(.white.opacity(0.8))
                                .padding()
                        }
                    }
                }
                
                Spacer()
                
                // Page content
                TabView(selection: $currentPage) {
                    ForEach(0..<pages.count, id: \.self) { index in
                        OnboardingPageView(page: pages[index])
                            .tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))
                
                Spacer()
                
                // Page indicators
                HStack(spacing: 8) {
                    ForEach(0..<pages.count, id: \.self) { index in
                        Circle()
                            .fill(currentPage == index ? Color.white : Color.white.opacity(0.5))
                            .frame(width: currentPage == index ? 10 : 8, height: currentPage == index ? 10 : 8)
                            .animation(.spring(), value: currentPage)
                    }
                }
                .padding(.bottom, 20)
                
                // Navigation buttons
                HStack(spacing: 20) {
                    if currentPage > 0 {
                        Button {
                            withAnimation {
                                currentPage -= 1
                            }
                        } label: {
                            Image(systemName: "chevron.left")
                                .font(.title3)
                                .foregroundColor(.white)
                                .frame(width: 50, height: 50)
                                .background(Color.white.opacity(0.2))
                                .clipShape(Circle())
                        }
                    }
                    
                    Spacer()
                    
                    Button {
                        if currentPage < pages.count - 1 {
                            withAnimation {
                                currentPage += 1
                            }
                        } else {
                            completeOnboarding()
                        }
                    } label: {
                        HStack {
                            Text(currentPage == pages.count - 1 ? "Başla" : "İleri")
                                .fontWeight(.semibold)
                            Image(systemName: currentPage == pages.count - 1 ? "checkmark" : "chevron.right")
                        }
                        .foregroundColor(.white)
                        .frame(height: 50)
                        .frame(maxWidth: currentPage == pages.count - 1 ? .infinity : 120)
                        .background(
                            LinearGradient(
                                colors: [Color.white.opacity(0.3), Color.white.opacity(0.2)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(25)
                    }
                }
                .padding(.horizontal, 30)
                .padding(.bottom, 40)
            }
        }
    }
    
    private func completeOnboarding() {
        withAnimation {
            hasCompletedOnboarding = true
        }
    }
}

struct OnboardingPageView: View {
    let page: OnboardingPage
    
    @State private var isAnimating = false
    
    var body: some View {
        VStack(spacing: 30) {
            Spacer()
            
            // Icon with animation
            Image(systemName: page.systemImage)
                .font(.system(size: 80))
                .foregroundColor(.white)
                .scaleEffect(isAnimating ? 1.0 : 0.8)
                .opacity(isAnimating ? 1.0 : 0.5)
                .animation(
                    .easeInOut(duration: 1.5)
                    .repeatForever(autoreverses: true),
                    value: isAnimating
                )
                .onAppear {
                    isAnimating = true
                }
            
            // Title
            Text(page.title)
                .font(.system(size: 32, weight: .bold))
                .foregroundColor(.white)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
            
            // Description
            Text(page.description)
                .font(.system(size: 18))
                .foregroundColor(.white.opacity(0.9))
                .multilineTextAlignment(.center)
                .lineSpacing(8)
                .padding(.horizontal, 40)
            
            Spacer()
        }
    }
}

// Main app entry point düzenlemesi
struct RootView: View {
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @State private var showSplash = true
    @StateObject private var authViewModel = AuthViewModel()
    
    var body: some View {
        Group {
            if showSplash {
                SplashView()
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                            withAnimation {
                                showSplash = false
                            }
                        }
                    }
            } else if !hasCompletedOnboarding {
                OnboardingView()
            } else if authViewModel.isAuthenticated {
                ContentView()
                    .environmentObject(authViewModel)
            } else {
                AuthFlowView()
                    .environmentObject(authViewModel)
            }
        }
        .animation(.easeInOut(duration: 0.5), value: authViewModel.isAuthenticated)
        .animation(.easeInOut(duration: 0.5), value: hasCompletedOnboarding)
        .animation(.easeInOut(duration: 0.5), value: showSplash)
    }
}

struct SplashView: View {
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.blue, Color.purple],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
            
            VStack {
                Image(systemName: "chart.line.uptrend.xyaxis.circle.fill")
                    .font(.system(size: 100))
                    .foregroundColor(.white)
                
                Text("Paratik")
                    .font(.system(size: 42, weight: .bold))
                    .foregroundColor(.white)
            }
        }
    }
}

// MARK: - Auth Flow Views

struct AuthFlowView: View {
    @State private var currentAuthScreen: AuthScreen = .login
    @EnvironmentObject var authViewModel: AuthViewModel
    
    enum AuthScreen {
        case login, register, forgotPassword
    }
    
    var body: some View {
        Group {
            switch currentAuthScreen {
            case .login:
                LoginView(
                    onLoginSuccess: {
                        // Auth state will be handled by AuthViewModel
                    },
                    onNavigateToRegister: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            currentAuthScreen = .register
                        }
                    },
                    onNavigateToForgotPassword: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            currentAuthScreen = .forgotPassword
                        }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
                
            case .register:
                RegisterView(
                    onRegisterSuccess: {
                        // Auth state will be handled by AuthViewModel
                    },
                    onNavigateToLogin: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            currentAuthScreen = .login
                        }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
                
            case .forgotPassword:
                ForgotPasswordView(
                    onNavigateToLogin: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            currentAuthScreen = .login
                        }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
            }
        }
    }
}

struct LoginView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var email = ""
    @State private var password = ""
    @State private var showError = false
    @State private var errorMessage = ""
    
    let onLoginSuccess: () -> Void
    let onNavigateToRegister: () -> Void
    let onNavigateToForgotPassword: () -> Void
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Header
                VStack(spacing: 16) {
                    Image(systemName: "chart.line.uptrend.xyaxis.circle.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.blue)
                    
                    Text("Hoş Geldiniz")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    Text("Hesabınıza giriş yapın")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 60)
                .padding(.bottom, 40)
                
                // Form
                VStack(spacing: 20) {
                    // Email Field
                    VStack(alignment: .leading, spacing: 8) {
                        Text("E-posta")
                            .font(.subheadline)
                            .fontWeight(.medium)
                            .foregroundColor(.primary)
                        
                        TextField("ornek@email.com", text: $email)
                            .textFieldStyle(CustomTextFieldStyle())
                            .keyboardType(.emailAddress)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                    }
                    
                    // Password Field
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Şifre")
                            .font(.subheadline)
                            .fontWeight(.medium)
                            .foregroundColor(.primary)
                        
                        SecureField("Şifrenizi girin", text: $password)
                            .textFieldStyle(CustomTextFieldStyle())
                    }
                    
                    // Error Message
                    if showError {
                        Text(errorMessage)
                            .font(.caption)
                            .foregroundColor(.red)
                            .padding(.horizontal)
                    }
                    
                    // Login Button
                    Button(action: handleLogin) {
                        HStack {
                            if authViewModel.isLoading {
                                ProgressView()
                                    .scaleEffect(0.8)
                                    .foregroundColor(.white)
                            } else {
                                Text("Giriş Yap")
                                    .fontWeight(.semibold)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                    .disabled(authViewModel.isLoading || email.isEmpty || password.isEmpty)
                    .opacity((authViewModel.isLoading || email.isEmpty || password.isEmpty) ? 0.6 : 1.0)
                    
                    // Forgot Password
                    Button(action: onNavigateToForgotPassword) {
                        Text("Şifremi Unuttum")
                            .font(.subheadline)
                            .foregroundColor(.blue)
                    }
                    .padding(.top, 8)
                }
                .padding(.horizontal, 24)
                
                Spacer()
                
                // Register Link
                HStack {
                    Text("Hesabınız yok mu?")
                        .foregroundColor(.secondary)
                    
                    Button(action: onNavigateToRegister) {
                        Text("Kayıt Ol")
                            .fontWeight(.semibold)
                            .foregroundColor(.blue)
                    }
                }
                .padding(.bottom, 40)
            }
            .background(Color(.systemBackground))
            .navigationBarHidden(true)
        }
    }
    
    private func handleLogin() {
        showError = false
        
        Task {
            do {
                try await authViewModel.signIn(email: email, password: password)
                onLoginSuccess()
            } catch {
                showError = true
                errorMessage = error.localizedDescription
            }
        }
    }
}

struct RegisterView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var name = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var showError = false
    @State private var errorMessage = ""
    
    let onRegisterSuccess: () -> Void
    let onNavigateToLogin: () -> Void
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Header
                VStack(spacing: 16) {
                    Image(systemName: "person.badge.plus")
                        .font(.system(size: 80))
                        .foregroundColor(.blue)
                    
                    Text("Hesap Oluştur")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    Text("Yeni hesabınızı oluşturun")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 40)
                .padding(.bottom, 30)
                
                ScrollView {
                    VStack(spacing: 20) {
                        // Name Field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Ad Soyad")
                                .font(.subheadline)
                                .fontWeight(.medium)
                                .foregroundColor(.primary)
                            
                            TextField("Adınızı ve soyadınızı girin", text: $name)
                                .textFieldStyle(CustomTextFieldStyle())
                                .autocapitalization(.words)
                        }
                        
                        // Email Field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("E-posta")
                                .font(.subheadline)
                                .fontWeight(.medium)
                                .foregroundColor(.primary)
                            
                            TextField("ornek@email.com", text: $email)
                                .textFieldStyle(CustomTextFieldStyle())
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                        }
                        
                        // Password Field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Şifre")
                                .font(.subheadline)
                                .fontWeight(.medium)
                                .foregroundColor(.primary)
                            
                            SecureField("Şifrenizi girin", text: $password)
                                .textFieldStyle(CustomTextFieldStyle())
                        }
                        
                        // Confirm Password Field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Şifre Tekrar")
                                .font(.subheadline)
                                .fontWeight(.medium)
                                .foregroundColor(.primary)
                            
                            SecureField("Şifrenizi tekrar girin", text: $confirmPassword)
                                .textFieldStyle(CustomTextFieldStyle())
                        }
                        
                        // Error Message
                        if showError {
                            Text(errorMessage)
                                .font(.caption)
                                .foregroundColor(.red)
                                .padding(.horizontal)
                        }
                        
                        // Register Button
                        Button(action: handleRegister) {
                            HStack {
                                if authViewModel.isLoading {
                                    ProgressView()
                                        .scaleEffect(0.8)
                                        .foregroundColor(.white)
                                } else {
                                    Text("Hesap Oluştur")
                                        .fontWeight(.semibold)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(isFormValid ? Color.blue : Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                        }
                        .disabled(authViewModel.isLoading || !isFormValid)
                        .opacity((authViewModel.isLoading || !isFormValid) ? 0.6 : 1.0)
                    }
                    .padding(.horizontal, 24)
                    .padding(.bottom, 40)
                }
                
                // Login Link
                HStack {
                    Text("Zaten hesabınız var mı?")
                        .foregroundColor(.secondary)
                    
                    Button(action: onNavigateToLogin) {
                        Text("Giriş Yap")
                            .fontWeight(.semibold)
                            .foregroundColor(.blue)
                    }
                }
                .padding(.bottom, 40)
            }
            .background(Color(.systemBackground))
            .navigationBarHidden(true)
        }
    }
    
    private var isFormValid: Bool {
        !name.isEmpty &&
        !email.isEmpty &&
        !password.isEmpty &&
        !confirmPassword.isEmpty &&
        password == confirmPassword &&
        password.count >= 6 &&
        email.contains("@")
    }
    
    private func handleRegister() {
        guard isFormValid else { return }
        
        showError = false
        
        Task {
            do {
                try await authViewModel.register(name: name, email: email, password: password)
                onRegisterSuccess()
            } catch {
                showError = true
                errorMessage = error.localizedDescription
            }
        }
    }
}

struct ForgotPasswordView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var email = ""
    @State private var showSuccess = false
    @State private var showError = false
    @State private var errorMessage = ""
    
    let onNavigateToLogin: () -> Void
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Header
                VStack(spacing: 16) {
                    Image(systemName: "key.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.blue)
                    
                    Text("Şifremi Unuttum")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    Text("E-posta adresinizi girin, size şifre sıfırlama bağlantısı gönderelim")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                }
                .padding(.top, 60)
                .padding(.bottom, 40)
                
                if showSuccess {
                    // Success State
                    VStack(spacing: 24) {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.green)
                        
                        Text("E-posta Gönderildi")
                            .font(.title2)
                            .fontWeight(.semibold)
                            .foregroundColor(.primary)
                        
                        Text("E-posta adresinize şifre sıfırlama bağlantısı gönderildi. Lütfen e-postanızı kontrol edin.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                        
                        Button(action: onNavigateToLogin) {
                            Text("Giriş Sayfasına Dön")
                                .fontWeight(.semibold)
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 50)
                                .background(Color.blue)
                                .cornerRadius(12)
                        }
                        .padding(.horizontal, 24)
                    }
                } else {
                    // Form State
                    VStack(spacing: 20) {
                        // Email Field
                        VStack(alignment: .leading, spacing: 8) {
                            Text("E-posta")
                                .font(.subheadline)
                                .fontWeight(.medium)
                                .foregroundColor(.primary)
                            
                            TextField("ornek@email.com", text: $email)
                                .textFieldStyle(CustomTextFieldStyle())
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                        }
                        
                        // Error Message
                        if showError {
                            Text(errorMessage)
                                .font(.caption)
                                .foregroundColor(.red)
                                .padding(.horizontal)
                        }
                        
                        // Send Reset Button
                        Button(action: handleForgotPassword) {
                            HStack {
                                if authViewModel.isLoading {
                                    ProgressView()
                                        .scaleEffect(0.8)
                                        .foregroundColor(.white)
                                } else {
                                    Text("Sıfırlama Bağlantısı Gönder")
                                        .fontWeight(.semibold)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(email.isEmpty ? Color.gray : Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                        }
                        .disabled(authViewModel.isLoading || email.isEmpty)
                        .opacity((authViewModel.isLoading || email.isEmpty) ? 0.6 : 1.0)
                        .padding(.horizontal, 24)
                        
                        Spacer()
                        
                        // Back to Login
                        Button(action: onNavigateToLogin) {
                            HStack {
                                Image(systemName: "arrow.left")
                                Text("Giriş Sayfasına Dön")
                            }
                            .font(.subheadline)
                            .foregroundColor(.blue)
                        }
                        .padding(.bottom, 40)
                    }
                    .padding(.horizontal, 24)
                }
            }
            .background(Color(.systemBackground))
            .navigationBarHidden(true)
        }
    }
    
    private func handleForgotPassword() {
        guard !email.isEmpty else { return }
        
        showError = false
        
        Task {
            do {
                try await authViewModel.sendPasswordReset(email: email)
                showSuccess = true
            } catch {
                showError = true
                errorMessage = error.localizedDescription
            }
        }
    }
}

struct CustomTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color(.systemGray4), lineWidth: 1)
            )
    }
}

#Preview {
    OnboardingView()
}
