//
//  OnboardingView.swift
//  SpendCraftiOS
//
//  iOS Native Onboarding - 6 Sayfa
//

import SwiftUI
import FirebaseAuth
import shared

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
    
    private var authStateHandle: AuthStateDidChangeListenerHandle?
    
    init() {
        // Uygulama başlangıcında auth durumunu kontrol et
        checkAuthState()
    }
    
    deinit {
        if let handle = authStateHandle {
            Auth.auth().removeStateDidChangeListener(handle)
        }
    }
    
    // MARK: - Auth State Management
    
    private func checkAuthState() {
        // Firebase auth state listener
        authStateHandle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            guard let self = self else { return }
            if let user = user {
                let model = UserModel(
                    id: user.uid,
                    email: user.email ?? "",
                    displayName: user.displayName ?? (user.email?.components(separatedBy: "@").first ?? NSLocalizedString("user.default", comment: "User")),
                    isEmailVerified: user.isEmailVerified
                )
                self.currentUser = model
                self.isAuthenticated = true
            } else {
                self.currentUser = nil
                self.isAuthenticated = false
            }
        }
    }
    
    // MARK: - Authentication Methods
    
    func signIn(email: String, password: String) async throws {
        isLoading = true
        errorMessage = nil
        
        // NOW USING SHARED KMP VALIDATORS! 🎉
        let emailValidation = shared.AccountValidator.shared.validateEmail(email: email)
        let passwordValidation = shared.AccountValidator.shared.validatePassword(password: password)
        
        guard emailValidation.isValid else {
            isLoading = false
            errorMessage = emailValidation.errorMessage ?? NSLocalizedString("error.valid.email.required", comment: "Please enter a valid email address")
            throw AuthError.invalidEmail
        }
        
        guard passwordValidation.isValid else {
            isLoading = false
            errorMessage = passwordValidation.errorMessage ?? NSLocalizedString("error.password.min.6.chars", comment: "Password must be at least 6 characters")
            throw AuthError.weakPassword
        }
        
        do {
            try await Auth.auth().signIn(withEmail: email, password: password)
            // Listener isAuthenticated'i güncelleyecek
            isLoading = false
        } catch {
            isLoading = false
            let mapped = mapFirebaseError(error)
            errorMessage = mapped.localizedDescription
            throw mapped
        }
    }
    
    func register(name: String, email: String, password: String) async throws {
        isLoading = true
        errorMessage = nil
        
        // Basit validation
        guard !name.isEmpty, !email.isEmpty, !password.isEmpty else {
            isLoading = false
            errorMessage = NSLocalizedString("error.all.fields.required", comment: "All fields are required")
            throw AuthError.invalidInput
        }
        
        guard email.contains("@") else {
            isLoading = false
            errorMessage = NSLocalizedString("error.valid.email.required", comment: "Please enter a valid email address")
            throw AuthError.invalidEmail
        }
        
        guard password.count >= 6 else {
            isLoading = false
            errorMessage = NSLocalizedString("error.password.min.6.chars", comment: "Password must be at least 6 characters")
            throw AuthError.weakPassword
        }
        
        do {
            let result = try await Auth.auth().createUser(withEmail: email, password: password)
            // Display name güncelle
            let changeRequest = result.user.createProfileChangeRequest()
            changeRequest.displayName = name
            try await changeRequest.commitChanges()
            
            // İsteğe bağlı: e-posta doğrulama göndermek istersen aç
            // try await result.user.sendEmailVerification()
            
            // Listener güncelleyecek
            isLoading = false
        } catch {
            isLoading = false
            let mapped = mapFirebaseError(error)
            errorMessage = mapped.localizedDescription
            throw mapped
        }
    }
    
    func sendPasswordReset(email: String) async throws {
        isLoading = true
        errorMessage = nil
        
        guard !email.isEmpty, email.contains("@") else {
            isLoading = false
            errorMessage = NSLocalizedString("error.valid.email.required", comment: "Please enter a valid email address")
            throw AuthError.invalidEmail
        }
        
        do {
            try await Auth.auth().sendPasswordReset(withEmail: email)
            isLoading = false
        } catch {
            isLoading = false
            let mapped = mapFirebaseError(error)
            errorMessage = mapped.localizedDescription
            throw mapped
        }
    }
    
    func signOut() async throws {
        do {
            try Auth.auth().signOut()
            currentUser = nil
            isAuthenticated = false
        } catch {
            let mapped = mapFirebaseError(error)
            errorMessage = mapped.localizedDescription
            throw mapped
        }
    }
    
    // MARK: - User Info
    
    var userDisplayName: String {
        return currentUser?.displayName ?? NSLocalizedString("user.default", comment: "User")
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
    
    private func mapFirebaseError(_ error: Error) -> Error {
        if let err = error as NSError?, err.domain == AuthErrorDomain,
           let code = AuthErrorCode(rawValue: err.code) {
            switch code {
            case .invalidEmail:
                return AuthError.invalidEmail
            case .weakPassword:
                return AuthError.weakPassword
            case .emailAlreadyInUse:
                return LocalMappedError(NSLocalizedString("error.email.already.in.use", comment: "This email address is already in use"))
            case .userNotFound:
                return AuthError.userNotFound
            case .wrongPassword:
                return LocalMappedError(NSLocalizedString("error.wrong.password", comment: "Email or password is incorrect"))
            case .networkError:
                return AuthError.networkError
            case .tooManyRequests:
                return LocalMappedError(NSLocalizedString("error.too.many.requests", comment: "Too many attempts. Please try again later."))
            case .userDisabled:
                return LocalMappedError(NSLocalizedString("error.user.disabled", comment: "This user has been disabled."))
            default:
                return LocalMappedError(err.localizedDescription)
            }
        }
        return error
    }
    
    struct LocalMappedError: LocalizedError {
        let message: String
        init(_ message: String) { self.message = message }
        var errorDescription: String? { message }
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
            return NSLocalizedString("error.invalid.input", comment: "Invalid input information")
        case .invalidEmail:
            return NSLocalizedString("error.invalid.email", comment: "Invalid email address")
        case .weakPassword:
            return NSLocalizedString("error.weak.password", comment: "Password is too weak")
        case .passwordMismatch:
            return NSLocalizedString("error.password.mismatch", comment: "Passwords do not match")
        case .userNotFound:
            return NSLocalizedString("error.user.not.found", comment: "User not found")
        case .networkError:
            return NSLocalizedString("error.network.error", comment: "Network error")
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
            title: NSLocalizedString("onboarding.welcome.title", comment: "Welcome to Masraf Takip 2026: Gelir Gider"),
            description: NSLocalizedString("onboarding.welcome.description", comment: "Welcome description"),
            systemImage: "chart.line.uptrend.xyaxis",
            gradientColors: [Color.blue, Color.purple]
        ),
        OnboardingPage(
            title: NSLocalizedString("onboarding.smart.categorization.title", comment: "Smart Categorization"),
            description: NSLocalizedString("onboarding.smart.categorization.description", comment: "Smart categorization description"),
            systemImage: "square.grid.2x2",
            gradientColors: [Color.green, Color.mint]
        ),
        OnboardingPage(
            title: NSLocalizedString("onboarding.budget.management.title", comment: "Budget Management"),
            description: NSLocalizedString("onboarding.budget.management.description", comment: "Budget management description"),
            systemImage: "chart.pie.fill",
            gradientColors: [Color.orange, Color.yellow]
        ),
        OnboardingPage(
            title: NSLocalizedString("onboarding.ai.recommendations.title", comment: "AI-Powered Recommendations"),
            description: NSLocalizedString("onboarding.ai.recommendations.description", comment: "AI recommendations description"),
            systemImage: "sparkles",
            gradientColors: [Color.purple, Color.pink]
        ),
        OnboardingPage(
            title: NSLocalizedString("onboarding.detailed.reports.title", comment: "Detailed Reports"),
            description: NSLocalizedString("onboarding.detailed.reports.description", comment: "Detailed reports description"),
            systemImage: "chart.bar.fill",
            gradientColors: [Color.red, Color.orange]
        ),
        OnboardingPage(
            title: NSLocalizedString("onboarding.get.started.title", comment: "Get Started Now"),
            description: NSLocalizedString("onboarding.get.started.description", comment: "Get started description"),
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
                            Text(NSLocalizedString("onboarding.skip", comment: "Skip"))
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
                            Text(currentPage == pages.count - 1 ? NSLocalizedString("onboarding.start", comment: "Start") : NSLocalizedString("onboarding.next", comment: "Next"))
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
    @AppStorage("biometric_enabled") private var biometricEnabled = false
    @AppStorage("selectedLanguage") private var selectedLanguage: String = ""
    @State private var showBiometricAuth = false
    @State private var biometricAuthenticated = false
    @StateObject private var authViewModel = AuthViewModel()
    @StateObject private var biometricManager = BiometricManager.shared
    
    var body: some View {
        Group {
            if !hasCompletedOnboarding {
                OnboardingView()
            } else {
                // Biometric kontrolü - eğer etkinse
                if biometricEnabled && biometricManager.isAvailable && !biometricAuthenticated {
                    // Biometric authentication ekranı göster
                    BiometricAuthView(
                        onAuthenticated: {
                            biometricAuthenticated = true
                        }
                    )
                    .environmentObject(biometricManager)
                } else {
                    // Ana uygulama - Face ID zaten BiometricAuthView'da kontrol edildi
                    ContentView()
                        .environmentObject(authViewModel)
                }
            }
        }
        .onAppear {
            // Uygulama açıldığında seçili dili ayarla
            let currentLang = LanguageHelper.shared.getCurrentLanguage()
            LanguageHelper.shared.setLanguage(currentLang)
        }
        .animation(.easeInOut(duration: 0.5), value: hasCompletedOnboarding)
        .animation(.easeInOut(duration: 0.5), value: biometricAuthenticated)
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

            VStack(spacing: 20) {
                Image("splash_icon") // Assets.xcassets içindeki ad
                    .resizable()
                    .frame(width: 180, height: 180) 
                    .shadow(color: .black.opacity(0.25), radius: 12, x: 0, y: 6)

                Text(NSLocalizedString("onboarding.app.name", comment: "Masraf Takip 2026: Gelir Gider"))
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
    @State private var isEmailFocused = false
    @State private var isPasswordFocused = false
    @State private var animateContent = false
    
    let onLoginSuccess: () -> Void
    let onNavigateToRegister: () -> Void
    let onNavigateToForgotPassword: () -> Void
    
    var body: some View {
        NavigationView {
            ZStack {
                // Animated Background Gradient
                LinearGradient(
                    colors: [
                        Color(red: 0.4, green: 0.5, blue: 1.0),
                        Color(red: 0.6, green: 0.4, blue: 0.9),
                        Color(red: 0.8, green: 0.3, blue: 0.7)
                    ],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                .opacity(0.15)
                
                // Floating circles for depth
                Circle()
                    .fill(Color.blue.opacity(0.1))
                    .frame(width: 300, height: 300)
                    .offset(x: -150, y: -200)
                    .blur(radius: 50)
                
                Circle()
                    .fill(Color.purple.opacity(0.1))
                    .frame(width: 250, height: 250)
                    .offset(x: 150, y: 300)
                    .blur(radius: 50)
                
                ScrollView(showsIndicators: false) {
                    VStack(spacing: 0) {
                        // Modern Header with Icon
                        VStack(spacing: 24) {
                            // Logo Container with Glassmorphism
                            ZStack {
                                Circle()
                                    .fill(
                                        LinearGradient(
                                            colors: [
                                                Color.blue.opacity(0.3),
                                                Color.purple.opacity(0.3)
                                            ],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .frame(width: 120, height: 120)
                                    .blur(radius: 20)
                                
                                Circle()
                                    .fill(Color(.systemBackground).opacity(0.9))
                                    .frame(width: 100, height: 100)
                                    .shadow(color: Color.blue.opacity(0.3), radius: 20, x: 0, y: 10)
                                
                                Image(systemName: "chart.line.uptrend.xyaxis")
                                    .font(.system(size: 45, weight: .medium))
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [Color.blue, Color.purple],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                            }
                            .scaleEffect(animateContent ? 1.0 : 0.8)
                            .opacity(animateContent ? 1.0 : 0.0)
                            
                            VStack(spacing: 8) {
                                Text(NSLocalizedString("auth.welcome.back", comment: "Welcome Back"))
                                    .font(.system(size: 32, weight: .bold))
                                    .foregroundColor(.primary)
                                
                                Text(NSLocalizedString("auth.continue.journey", comment: "Continue your financial journey"))
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                        }
                        .padding(.top, 60)
                        .padding(.bottom, 40)
                        
                        // Modern Form Card
                        VStack(spacing: 24) {
                            // Email Field with Modern Design
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "envelope.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(isEmailFocused ? .blue : .secondary)
                                    Text(NSLocalizedString("auth.email", comment: "Email"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(isEmailFocused ? .blue : .primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "at")
                                        .foregroundColor(.secondary)
                                        .frame(width: 20)
                                    
                                    TextField("ornek@email.com", text: $email)
                                        .textFieldStyle(PlainTextFieldStyle())
                                        .keyboardType(.emailAddress)
                                        .autocapitalization(.none)
                                        .disableAutocorrection(true)
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color(.systemBackground))
                                        .shadow(color: isEmailFocused ? Color.blue.opacity(0.3) : Color.black.opacity(0.05), radius: isEmailFocused ? 12 : 8, x: 0, y: 4)
                                )
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(isEmailFocused ? Color.blue : Color.clear, lineWidth: 2)
                                )
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.1), value: animateContent)
                            
                            // Password Field with Modern Design
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "lock.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(isPasswordFocused ? .blue : .secondary)
                                    Text(NSLocalizedString("auth.password", comment: "Password"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(isPasswordFocused ? .blue : .primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "key.fill")
                                        .foregroundColor(.secondary)
                                        .frame(width: 20)
                                    
                                    SecureField("••••••••", text: $password)
                                        .textFieldStyle(PlainTextFieldStyle())
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color(.systemBackground))
                                        .shadow(color: isPasswordFocused ? Color.blue.opacity(0.3) : Color.black.opacity(0.05), radius: isPasswordFocused ? 12 : 8, x: 0, y: 4)
                                )
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(isPasswordFocused ? Color.blue : Color.clear, lineWidth: 2)
                                )
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.2), value: animateContent)
                            
                            // Error Message with better styling
                            if showError {
                                HStack(spacing: 12) {
                                    Image(systemName: "exclamationmark.triangle.fill")
                                        .foregroundColor(.red)
                                    Text(errorMessage)
                                        .font(.caption)
                                        .foregroundColor(.red)
                                    Spacer()
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 12)
                                        .fill(Color.red.opacity(0.1))
                                )
                                .transition(.scale.combined(with: .opacity))
                            }
                            
                            // Forgot Password Button
                            HStack {
                                Spacer()
                                Button(action: onNavigateToForgotPassword) {
                                    Text(NSLocalizedString("auth.forgot.password", comment: "Forgot Password"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundStyle(
                                            LinearGradient(
                                                colors: [Color.blue, Color.purple],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        )
                                }
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.3), value: animateContent)
                            
                            // Modern Login Button
                            Button(action: handleLogin) {
                                HStack(spacing: 12) {
                                    if authViewModel.isLoading {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    } else {
                                        Text(NSLocalizedString("auth.sign.in", comment: "Sign In"))
                                            .fontWeight(.bold)
                                            .font(.system(size: 17))
                                        Image(systemName: "arrow.right")
                                            .font(.system(size: 16, weight: .bold))
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 56)
                                .background(
                                    Group {
                                        if authViewModel.isLoading || email.isEmpty || password.isEmpty {
                                            LinearGradient(
                                                colors: [Color.gray, Color.gray.opacity(0.8)],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        } else {
                                            LinearGradient(
                                                colors: [Color.blue, Color.purple],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        }
                                    }
                                )
                                .foregroundColor(.white)
                                .cornerRadius(16)
                                .shadow(color: (authViewModel.isLoading || email.isEmpty || password.isEmpty) ? Color.clear : Color.blue.opacity(0.4), radius: 15, x: 0, y: 8)
                            }
                            .disabled(authViewModel.isLoading || email.isEmpty || password.isEmpty)
                            .scaleEffect(animateContent ? 1.0 : 0.9)
                            .opacity(animateContent ? 1.0 : 0.0)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.4), value: animateContent)
                        }
                        .padding(.horizontal, 32)
                        .padding(.vertical, 32)
                        .background(
                            RoundedRectangle(cornerRadius: 30)
                                .fill(Color(.systemBackground).opacity(0.7))
                                .shadow(color: Color.black.opacity(0.1), radius: 30, x: 0, y: 15)
                        )
                        .padding(.horizontal, 24)
                        
                        // Register Link with modern design
                        VStack(spacing: 16) {
                            HStack(spacing: 12) {
                                Rectangle()
                                    .fill(Color.secondary.opacity(0.3))
                                    .frame(height: 1)
                                Text(NSLocalizedString("onboarding.or", comment: "or"))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Rectangle()
                                    .fill(Color.secondary.opacity(0.3))
                                    .frame(height: 1)
                            }
                            .padding(.horizontal, 32)
                            .padding(.top, 32)
                            
                            Button(action: onNavigateToRegister) {
                                HStack {
                                    Text(NSLocalizedString("auth.no.account", comment: "Don't have an account?"))
                                        .foregroundColor(.secondary)
                                        .fontWeight(.medium)
                                    Text(NSLocalizedString("auth.register", comment: "Register"))
                                        .fontWeight(.bold)
                                        .foregroundStyle(
                                            LinearGradient(
                                                colors: [Color.blue, Color.purple],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        )
                                }
                                .font(.subheadline)
                                .padding(.vertical, 16)
                                .padding(.horizontal, 32)
                                .background(
                                    Capsule()
                                        .fill(Color(.systemBackground))
                                        .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                )
                            }
                        }
                        .opacity(animateContent ? 1.0 : 0.0)
                        .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.5), value: animateContent)
                        .padding(.bottom, 40)
                    }
                }
            }
            .navigationBarHidden(true)
            .onAppear {
                withAnimation {
                    animateContent = true
                }
            }
        }
    }
    
    private func handleLogin() {
        showError = false
        
        Task {
            do {
                try await authViewModel.signIn(email: email, password: password)
                onLoginSuccess()
            } catch {
                withAnimation {
                    showError = true
                    errorMessage = error.localizedDescription
                }
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
    @State private var animateContent = false
    
    let onRegisterSuccess: () -> Void
    let onNavigateToLogin: () -> Void
    
    var body: some View {
        NavigationView {
            ZStack {
                // Animated Background Gradient
                LinearGradient(
                    colors: [
                        Color(red: 0.3, green: 0.8, blue: 0.6),
                        Color(red: 0.4, green: 0.6, blue: 1.0),
                        Color(red: 0.6, green: 0.4, blue: 0.9)
                    ],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                .opacity(0.15)
                
                // Floating circles
                Circle()
                    .fill(Color.green.opacity(0.1))
                    .frame(width: 280, height: 280)
                    .offset(x: -140, y: -180)
                    .blur(radius: 50)
                
                Circle()
                    .fill(Color.blue.opacity(0.1))
                    .frame(width: 230, height: 230)
                    .offset(x: 140, y: 280)
                    .blur(radius: 50)
                
                ScrollView(showsIndicators: false) {
                    VStack(spacing: 0) {
                        // Modern Header
                        VStack(spacing: 24) {
                            ZStack {
                                Circle()
                                    .fill(
                                        LinearGradient(
                                            colors: [
                                                Color.green.opacity(0.3),
                                                Color.blue.opacity(0.3)
                                            ],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .frame(width: 120, height: 120)
                                    .blur(radius: 20)
                                
                                Circle()
                                    .fill(Color(.systemBackground).opacity(0.9))
                                    .frame(width: 100, height: 100)
                                    .shadow(color: Color.green.opacity(0.3), radius: 20, x: 0, y: 10)
                                
                                Image(systemName: "person.badge.plus.fill")
                                    .font(.system(size: 45, weight: .medium))
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [Color.green, Color.blue],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                            }
                            .scaleEffect(animateContent ? 1.0 : 0.8)
                            .opacity(animateContent ? 1.0 : 0.0)
                            
                            VStack(spacing: 8) {
                                Text(NSLocalizedString("auth.create.account", comment: "Create Account"))
                                    .font(.system(size: 32, weight: .bold))
                                    .foregroundColor(.primary)
                                
                                Text(NSLocalizedString("auth.start.journey", comment: "Start your journey to financial freedom"))
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                    .multilineTextAlignment(.center)
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                        }
                        .padding(.top, 50)
                        .padding(.bottom, 30)
                        
                        // Modern Form Card
                        VStack(spacing: 20) {
                            // Name Field
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "person.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(.secondary)
                                    Text(NSLocalizedString("auth.full.name", comment: "Full Name"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(.primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "person.text.rectangle")
                                        .foregroundColor(.secondary)
                                        .frame(width: 20)
                                    
                                    TextField(NSLocalizedString("auth.full.name", comment: "Full Name"), text: $name)
                                        .textFieldStyle(PlainTextFieldStyle())
                                        .autocapitalization(.words)
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color(.systemBackground))
                                        .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                )
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.1), value: animateContent)
                            
                            // Email Field
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "envelope.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(.secondary)
                                    Text(NSLocalizedString("auth.email", comment: "Email"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(.primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "at")
                                        .foregroundColor(.secondary)
                                        .frame(width: 20)
                                    
                                    TextField("ornek@email.com", text: $email)
                                        .textFieldStyle(PlainTextFieldStyle())
                                        .keyboardType(.emailAddress)
                                        .autocapitalization(.none)
                                        .disableAutocorrection(true)
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color(.systemBackground))
                                        .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                )
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.2), value: animateContent)
                            
                            // Password Field
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "lock.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(.secondary)
                                    Text(NSLocalizedString("auth.password", comment: "Password"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(.primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "key.fill")
                                        .foregroundColor(.secondary)
                                        .frame(width: 20)
                                    
                                    SecureField("En az 6 karakter", text: $password)
                                        .textFieldStyle(PlainTextFieldStyle())
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color(.systemBackground))
                                        .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                )
                                
                                // Password strength indicator
                                if !password.isEmpty {
                                    HStack(spacing: 4) {
                                        ForEach(0..<3) { index in
                                            Rectangle()
                                                .fill(passwordStrength > index ? strengthColor : Color.gray.opacity(0.3))
                                                .frame(height: 3)
                                                .cornerRadius(2)
                                        }
                                    }
                                    .padding(.top, 4)
                                    
                                    Text(strengthText)
                                        .font(.caption2)
                                        .foregroundColor(strengthColor)
                                }
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.3), value: animateContent)
                            
                            // Confirm Password Field
                            VStack(alignment: .leading, spacing: 12) {
                                HStack {
                                    Image(systemName: "lock.shield.fill")
                                        .font(.system(size: 14))
                                        .foregroundColor(.secondary)
                                    Text(NSLocalizedString("auth.password.repeat", comment: "Repeat Password"))
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(.primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "checkmark.shield.fill")
                                        .foregroundColor(passwordsMatch ? .green : .secondary)
                                        .frame(width: 20)
                                    
                                    SecureField(NSLocalizedString("auth.password.repeat.placeholder", comment: "Re-enter your password"), text: $confirmPassword)
                                        .textFieldStyle(PlainTextFieldStyle())
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color(.systemBackground))
                                        .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                )
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(passwordsMatch && !confirmPassword.isEmpty ? Color.green : Color.clear, lineWidth: 2)
                                )
                            }
                            .opacity(animateContent ? 1.0 : 0.0)
                            .offset(y: animateContent ? 0 : 20)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.4), value: animateContent)
                            
                            // Error Message
                            if showError {
                                HStack(spacing: 12) {
                                    Image(systemName: "exclamationmark.triangle.fill")
                                        .foregroundColor(.red)
                                    Text(errorMessage)
                                        .font(.caption)
                                        .foregroundColor(.red)
                                    Spacer()
                                }
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 12)
                                        .fill(Color.red.opacity(0.1))
                                )
                                .transition(.scale.combined(with: .opacity))
                            }
                            
                            // Register Button
                            Button(action: handleRegister) {
                                HStack(spacing: 12) {
                                    if authViewModel.isLoading {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    } else {
                                        Text(NSLocalizedString("auth.create.account", comment: "Create Account"))
                                            .fontWeight(.bold)
                                            .font(.system(size: 17))
                                        Image(systemName: "checkmark.circle.fill")
                                            .font(.system(size: 18, weight: .bold))
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 56)
                                .background(
                                    Group {
                                        if !isFormValid {
                                            LinearGradient(
                                                colors: [Color.gray, Color.gray.opacity(0.8)],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        } else {
                                            LinearGradient(
                                                colors: [Color.green, Color.blue],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        }
                                    }
                                )
                                .foregroundColor(.white)
                                .cornerRadius(16)
                                .shadow(color: isFormValid ? Color.green.opacity(0.4) : Color.clear, radius: 15, x: 0, y: 8)
                            }
                            .disabled(authViewModel.isLoading || !isFormValid)
                            .scaleEffect(animateContent ? 1.0 : 0.9)
                            .opacity(animateContent ? 1.0 : 0.0)
                            .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.5), value: animateContent)
                        }
                        .padding(.horizontal, 32)
                        .padding(.vertical, 32)
                        .background(
                            RoundedRectangle(cornerRadius: 30)
                                .fill(Color(.systemBackground).opacity(0.7))
                                .shadow(color: Color.black.opacity(0.1), radius: 30, x: 0, y: 15)
                        )
                        .padding(.horizontal, 24)
                        
                        // Login Link
                        VStack(spacing: 16) {
                            HStack(spacing: 12) {
                                Rectangle()
                                    .fill(Color.secondary.opacity(0.3))
                                    .frame(height: 1)
                                Text(NSLocalizedString("onboarding.or", comment: "or"))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Rectangle()
                                    .fill(Color.secondary.opacity(0.3))
                                    .frame(height: 1)
                            }
                            .padding(.horizontal, 32)
                            .padding(.top, 24)
                            
                            Button(action: onNavigateToLogin) {
                                HStack {
                                    Text(NSLocalizedString("auth.already.have.account", comment: "Already have an account?"))
                                        .foregroundColor(.secondary)
                                        .fontWeight(.medium)
                                    Text(NSLocalizedString("auth.sign.in.link", comment: "Sign In"))
                                        .fontWeight(.bold)
                                        .foregroundStyle(
                                            LinearGradient(
                                                colors: [Color.green, Color.blue],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        )
                                }
                                .font(.subheadline)
                                .padding(.vertical, 16)
                                .padding(.horizontal, 32)
                                .background(
                                    Capsule()
                                        .fill(Color(.systemBackground))
                                        .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                )
                            }
                        }
                        .opacity(animateContent ? 1.0 : 0.0)
                        .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.6), value: animateContent)
                        .padding(.bottom, 40)
                    }
                }
            }
            .navigationBarHidden(true)
            .onAppear {
                withAnimation {
                    animateContent = true
                }
            }
        }
    }
    
    private var isFormValid: Bool {
        // NOW USING SHARED KMP VALIDATORS! 🎉
        let nameValid = shared.AccountValidator.shared.validateName(name: name).isValid
        let emailValid = shared.AccountValidator.shared.validateEmail(email: email).isValid
        let passwordValid = shared.AccountValidator.shared.validatePassword(password: password).isValid
        let passwordsMatch = password == confirmPassword && !password.isEmpty
        
        return nameValid && emailValid && passwordValid && passwordsMatch
    }
    
    private var passwordsMatch: Bool {
        !password.isEmpty && !confirmPassword.isEmpty && password == confirmPassword
    }
    
    private var passwordStrength: Int {
        var strength = 0
        if password.count >= 6 { strength += 1 }
        if password.count >= 8 { strength += 1 }
        if password.rangeOfCharacter(from: .decimalDigits) != nil &&
           password.rangeOfCharacter(from: .letters) != nil {
            strength += 1
        }
        return strength
    }
    
    private var strengthColor: Color {
        switch passwordStrength {
        case 0, 1: return .red
        case 2: return .orange
        case 3: return .green
        default: return .gray
        }
    }
    
    private var strengthText: String {
        switch passwordStrength {
        case 0, 1: return NSLocalizedString("error.password.strength.weak", comment: "Weak")
        case 2: return NSLocalizedString("error.password.strength.medium", comment: "Medium")
        case 3: return NSLocalizedString("error.password.strength.strong", comment: "Strong")
        default: return ""
        }
    }
    
    private func handleRegister() {
        guard isFormValid else { return }
        
        showError = false
        
        Task {
            do {
                try await authViewModel.register(name: name, email: email, password: password)
                onRegisterSuccess()
            } catch {
                withAnimation {
                    showError = true
                    errorMessage = error.localizedDescription
                }
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
    @State private var animateContent = false
    
    let onNavigateToLogin: () -> Void
    
    var body: some View {
        NavigationView {
            ZStack {
                // Animated Background Gradient
                LinearGradient(
                    colors: [
                        Color(red: 0.9, green: 0.5, blue: 0.3),
                        Color(red: 0.7, green: 0.4, blue: 0.9),
                        Color(red: 0.5, green: 0.5, blue: 1.0)
                    ],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                .opacity(0.15)
                
                // Floating circles
                Circle()
                    .fill(Color.orange.opacity(0.1))
                    .frame(width: 260, height: 260)
                    .offset(x: -130, y: -160)
                    .blur(radius: 50)
                
                Circle()
                    .fill(Color.purple.opacity(0.1))
                    .frame(width: 210, height: 210)
                    .offset(x: 130, y: 260)
                    .blur(radius: 50)
                
                ScrollView(showsIndicators: false) {
                    VStack(spacing: 0) {
                        if showSuccess {
                            // Success State with Modern Design
                            VStack(spacing: 32) {
                                Spacer()
                                    .frame(height: 80)
                                
                                // Success Icon
                                ZStack {
                                    Circle()
                                        .fill(
                                            LinearGradient(
                                                colors: [
                                                    Color.green.opacity(0.2),
                                                    Color.green.opacity(0.1)
                                                ],
                                                startPoint: .topLeading,
                                                endPoint: .bottomTrailing
                                            )
                                        )
                                        .frame(width: 140, height: 140)
                                        .blur(radius: 20)
                                    
                                    Circle()
                                        .fill(Color(.systemBackground))
                                        .frame(width: 120, height: 120)
                                        .shadow(color: Color.green.opacity(0.3), radius: 30, x: 0, y: 15)
                                    
                                    Image(systemName: "envelope.badge.fill")
                                        .font(.system(size: 50, weight: .medium))
                                        .foregroundStyle(
                                            LinearGradient(
                                                colors: [Color.green, Color.blue],
                                                startPoint: .topLeading,
                                                endPoint: .bottomTrailing
                                            )
                                        )
                                }
                                .scaleEffect(animateContent ? 1.0 : 0.5)
                                .opacity(animateContent ? 1.0 : 0.0)
                                
                                VStack(spacing: 16) {
                                    Text(NSLocalizedString("auth.email.sent", comment: "Email Sent"))
                                        .font(.system(size: 28, weight: .bold))
                                        .foregroundColor(.primary)
                                    
                                    Text(NSLocalizedString("auth.email.sent.description", comment: "Email sent description"))
                                        .font(.subheadline)
                                        .foregroundColor(.secondary)
                                        .multilineTextAlignment(.center)
                                        .lineSpacing(6)
                                        .padding(.horizontal, 32)
                                }
                                .opacity(animateContent ? 1.0 : 0.0)
                                .offset(y: animateContent ? 0 : 20)
                                
                                VStack(spacing: 16) {
                                    Button(action: onNavigateToLogin) {
                                        HStack(spacing: 12) {
                                            Text(NSLocalizedString("auth.back.to.login", comment: "Back to Login"))
                                                .fontWeight(.bold)
                                                .font(.system(size: 17))
                                            Image(systemName: "arrow.right")
                                                .font(.system(size: 16, weight: .bold))
                                        }
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 56)
                                        .background(
                                            LinearGradient(
                                                colors: [Color.green, Color.blue],
                                                startPoint: .leading,
                                                endPoint: .trailing
                                            )
                                        )
                                        .foregroundColor(.white)
                                        .cornerRadius(16)
                                        .shadow(color: Color.green.opacity(0.4), radius: 15, x: 0, y: 8)
                                    }
                                    
                                    Text(NSLocalizedString("auth.check.spam", comment: "If you don't receive the email, check your spam folder"))
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                        .multilineTextAlignment(.center)
                                }
                                .padding(.horizontal, 32)
                                .padding(.top, 16)
                                .opacity(animateContent ? 1.0 : 0.0)
                                .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.3), value: animateContent)
                                
                                Spacer()
                            }
                        } else {
                            // Form State with Modern Design
                            VStack(spacing: 0) {
                                // Modern Header
                                VStack(spacing: 24) {
                                    ZStack {
                                        Circle()
                                            .fill(
                                                LinearGradient(
                                                    colors: [
                                                        Color.orange.opacity(0.3),
                                                        Color.purple.opacity(0.3)
                                                    ],
                                                    startPoint: .topLeading,
                                                    endPoint: .bottomTrailing
                                                )
                                            )
                                            .frame(width: 120, height: 120)
                                            .blur(radius: 20)
                                        
                                        Circle()
                                            .fill(Color(.systemBackground).opacity(0.9))
                                            .frame(width: 100, height: 100)
                                            .shadow(color: Color.orange.opacity(0.3), radius: 20, x: 0, y: 10)
                                        
                                        Image(systemName: "lock.rotation")
                                            .font(.system(size: 45, weight: .medium))
                                            .foregroundStyle(
                                                LinearGradient(
                                                    colors: [Color.orange, Color.purple],
                                                    startPoint: .topLeading,
                                                    endPoint: .bottomTrailing
                                                )
                                            )
                                    }
                                    .scaleEffect(animateContent ? 1.0 : 0.8)
                                    .opacity(animateContent ? 1.0 : 0.0)
                                    
                                    VStack(spacing: 12) {
                                        Text(NSLocalizedString("auth.forgot.password.title", comment: "Forgot Your Password?"))
                                            .font(.system(size: 32, weight: .bold))
                                            .foregroundColor(.primary)
                                        
                                        Text(NSLocalizedString("auth.forgot.password.description", comment: "Enter your email address, we'll send you a password reset link"))
                                            .font(.subheadline)
                                            .foregroundColor(.secondary)
                                            .multilineTextAlignment(.center)
                                            .lineSpacing(4)
                                            .padding(.horizontal, 16)
                                    }
                                    .opacity(animateContent ? 1.0 : 0.0)
                                    .offset(y: animateContent ? 0 : 20)
                                }
                                .padding(.top, 60)
                                .padding(.bottom, 40)
                                
                                // Modern Form Card
                                VStack(spacing: 24) {
                                    // Email Field
                                    VStack(alignment: .leading, spacing: 12) {
                                        HStack {
                                            Image(systemName: "envelope.fill")
                                                .font(.system(size: 14))
                                                .foregroundColor(.secondary)
                                            Text(NSLocalizedString("auth.email", comment: "Email Address"))
                                                .font(.subheadline)
                                                .fontWeight(.semibold)
                                                .foregroundColor(.primary)
                                        }
                                        
                                        HStack(spacing: 12) {
                                            Image(systemName: "at")
                                                .foregroundColor(.secondary)
                                                .frame(width: 20)
                                            
                                            TextField("ornek@email.com", text: $email)
                                                .textFieldStyle(PlainTextFieldStyle())
                                                .keyboardType(.emailAddress)
                                                .autocapitalization(.none)
                                                .disableAutocorrection(true)
                                        }
                                        .padding()
                                        .background(
                                            RoundedRectangle(cornerRadius: 16)
                                                .fill(Color(.systemBackground))
                                                .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                        )
                                    }
                                    .opacity(animateContent ? 1.0 : 0.0)
                                    .offset(y: animateContent ? 0 : 20)
                                    .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.1), value: animateContent)
                                    
                                    // Error Message
                                    if showError {
                                        HStack(spacing: 12) {
                                            Image(systemName: "exclamationmark.triangle.fill")
                                                .foregroundColor(.red)
                                            Text(errorMessage)
                                                .font(.caption)
                                                .foregroundColor(.red)
                                            Spacer()
                                        }
                                        .padding()
                                        .background(
                                            RoundedRectangle(cornerRadius: 12)
                                                .fill(Color.red.opacity(0.1))
                                        )
                                        .transition(.scale.combined(with: .opacity))
                                    }
                                    
                                    // Info Box
                                    HStack(spacing: 12) {
                                        Image(systemName: "info.circle.fill")
                                            .foregroundColor(.blue)
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text(NSLocalizedString("auth.use.registered.email", comment: "Use your registered email address"))
                                                .font(.caption)
                                                .fontWeight(.medium)
                                                .foregroundColor(.primary)
                                            Text(NSLocalizedString("auth.link.valid.24h", comment: "The link will be valid for 24 hours"))
                                                .font(.caption2)
                                                .foregroundColor(.secondary)
                                        }
                                        Spacer()
                                    }
                                    .padding()
                                    .background(
                                        RoundedRectangle(cornerRadius: 12)
                                            .fill(Color.blue.opacity(0.1))
                                    )
                                    .opacity(animateContent ? 1.0 : 0.0)
                                    .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.2), value: animateContent)
                                    
                                    // Send Button
                                    Button(action: handleForgotPassword) {
                                        HStack(spacing: 12) {
                                            if authViewModel.isLoading {
                                                ProgressView()
                                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                            } else {
                                                Text(NSLocalizedString("auth.send.reset.link", comment: "Send Reset Link"))
                                                    .fontWeight(.bold)
                                                    .font(.system(size: 17))
                                                Image(systemName: "paperplane.fill")
                                                    .font(.system(size: 16, weight: .bold))
                                            }
                                        }
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 56)
                                        .background(
                                            Group {
                                                if authViewModel.isLoading || email.isEmpty {
                                                    LinearGradient(
                                                        colors: [Color.gray, Color.gray.opacity(0.8)],
                                                        startPoint: .leading,
                                                        endPoint: .trailing
                                                    )
                                                } else {
                                                    LinearGradient(
                                                        colors: [Color.orange, Color.purple],
                                                        startPoint: .leading,
                                                        endPoint: .trailing
                                                    )
                                                }
                                            }
                                        )
                                        .foregroundColor(.white)
                                        .cornerRadius(16)
                                        .shadow(color: (authViewModel.isLoading || email.isEmpty) ? Color.clear : Color.orange.opacity(0.4), radius: 15, x: 0, y: 8)
                                    }
                                    .disabled(authViewModel.isLoading || email.isEmpty)
                                    .scaleEffect(animateContent ? 1.0 : 0.9)
                                    .opacity(animateContent ? 1.0 : 0.0)
                                    .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.3), value: animateContent)
                                }
                                .padding(.horizontal, 32)
                                .padding(.vertical, 32)
                                .background(
                                    RoundedRectangle(cornerRadius: 30)
                                        .fill(Color(.systemBackground).opacity(0.7))
                                        .shadow(color: Color.black.opacity(0.1), radius: 30, x: 0, y: 15)
                                )
                                .padding(.horizontal, 24)
                                
                                // Back to Login Link
                                Button(action: onNavigateToLogin) {
                                    HStack(spacing: 8) {
                                        Image(systemName: "arrow.left.circle.fill")
                                            .font(.system(size: 20))
                                        Text(NSLocalizedString("auth.back.to.login", comment: "Back to Login"))
                                            .fontWeight(.semibold)
                                    }
                                    .font(.subheadline)
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [Color.orange, Color.purple],
                                            startPoint: .leading,
                                            endPoint: .trailing
                                        )
                                    )
                                    .padding(.vertical, 16)
                                    .padding(.horizontal, 32)
                                    .background(
                                        Capsule()
                                            .fill(Color(.systemBackground))
                                            .shadow(color: Color.black.opacity(0.05), radius: 8, x: 0, y: 4)
                                    )
                                }
                                .padding(.top, 32)
                                .opacity(animateContent ? 1.0 : 0.0)
                                .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(0.4), value: animateContent)
                                .padding(.bottom, 40)
                            }
                        }
                    }
                }
            }
            .navigationBarHidden(true)
            .onAppear {
                withAnimation {
                    animateContent = true
                }
            }
        }
    }
    
    private func handleForgotPassword() {
        guard !email.isEmpty else { return }
        
        showError = false
        
        Task {
            do {
                try await authViewModel.sendPasswordReset(email: email)
                withAnimation {
                    showSuccess = true
                    animateContent = false
                }
                // Re-animate for success state
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    withAnimation {
                        animateContent = true
                    }
                }
            } catch {
                withAnimation {
                    showError = true
                    errorMessage = error.localizedDescription
                }
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
