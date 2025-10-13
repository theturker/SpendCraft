//
//  OnboardingView.swift
//  SpendCraftiOS
//
//  iOS Native Onboarding - 6 Sayfa
//

import SwiftUI
import FirebaseAuth

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
                    displayName: user.displayName ?? (user.email?.components(separatedBy: "@").first ?? "Kullanıcı"),
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
            errorMessage = "Geçerli bir e-posta adresi girin"
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
    
    private func mapFirebaseError(_ error: Error) -> Error {
        if let err = error as NSError?, err.domain == AuthErrorDomain,
           let code = AuthErrorCode(rawValue: err.code) {
            switch code {
            case .invalidEmail:
                return AuthError.invalidEmail
            case .weakPassword:
                return AuthError.weakPassword
            case .emailAlreadyInUse:
                return LocalMappedError("Bu e-posta adresi zaten kullanımda")
            case .userNotFound:
                return AuthError.userNotFound
            case .wrongPassword:
                return LocalMappedError("E-posta veya şifre hatalı")
            case .networkError:
                return AuthError.networkError
            case .tooManyRequests:
                return LocalMappedError("Çok fazla deneme yapıldı. Lütfen sonra tekrar deneyin.")
            case .userDisabled:
                return LocalMappedError("Bu kullanıcı devre dışı bırakılmış.")
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

            VStack(spacing: 20) {
                Image("splash_icon") // Assets.xcassets içindeki ad
                    .resizable()
                    .frame(width: 180, height: 180) 
                    .shadow(color: .black.opacity(0.25), radius: 12, x: 0, y: 6)

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
                                Text("Tekrar Hoş Geldiniz")
                                    .font(.system(size: 32, weight: .bold))
                                    .foregroundColor(.primary)
                                
                                Text("Finansal yolculuğunuza devam edin")
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
                                    Text("E-posta")
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
                                    Text("Şifre")
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
                                    Text("Şifremi Unuttum")
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
                                        Text("Giriş Yap")
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
                                Text("veya")
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
                                    Text("Hesabınız yok mu?")
                                        .foregroundColor(.secondary)
                                        .fontWeight(.medium)
                                    Text("Kayıt Ol")
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
                                Text("Hesap Oluştur")
                                    .font(.system(size: 32, weight: .bold))
                                    .foregroundColor(.primary)
                                
                                Text("Finansal özgürlük yolculuğunuza başlayın")
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
                                    Text("Ad Soyad")
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(.primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "person.text.rectangle")
                                        .foregroundColor(.secondary)
                                        .frame(width: 20)
                                    
                                    TextField("Adınız ve Soyadınız", text: $name)
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
                                    Text("E-posta")
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
                                    Text("Şifre")
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
                                    Text("Şifre Tekrar")
                                        .font(.subheadline)
                                        .fontWeight(.semibold)
                                        .foregroundColor(.primary)
                                }
                                
                                HStack(spacing: 12) {
                                    Image(systemName: "checkmark.shield.fill")
                                        .foregroundColor(passwordsMatch ? .green : .secondary)
                                        .frame(width: 20)
                                    
                                    SecureField("Şifrenizi tekrar girin", text: $confirmPassword)
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
                                        Text("Hesap Oluştur")
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
                                Text("veya")
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
                                    Text("Zaten hesabınız var mı?")
                                        .foregroundColor(.secondary)
                                        .fontWeight(.medium)
                                    Text("Giriş Yap")
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
        !name.isEmpty &&
        !email.isEmpty &&
        !password.isEmpty &&
        !confirmPassword.isEmpty &&
        password == confirmPassword &&
        password.count >= 6 &&
        email.contains("@")
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
        case 0, 1: return "Zayıf"
        case 2: return "Orta"
        case 3: return "Güçlü"
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
                                    Text("E-posta Gönderildi")
                                        .font(.system(size: 28, weight: .bold))
                                        .foregroundColor(.primary)
                                    
                                    Text("E-posta adresinize şifre sıfırlama bağlantısı gönderildi. Lütfen gelen kutunuzu kontrol edin.")
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
                                            Text("Giriş Sayfasına Dön")
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
                                    
                                    Text("E-posta gelmediyse spam klasörünü kontrol edin")
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
                                        Text("Şifrenizi mi Unuttunuz?")
                                            .font(.system(size: 32, weight: .bold))
                                            .foregroundColor(.primary)
                                        
                                        Text("E-posta adresinizi girin, size şifre sıfırlama bağlantısı gönderelim")
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
                                            Text("E-posta Adresi")
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
                                            Text("Kayıtlı e-posta adresinizi kullanın")
                                                .font(.caption)
                                                .fontWeight(.medium)
                                                .foregroundColor(.primary)
                                            Text("Bağlantı 24 saat geçerli olacaktır")
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
                                                Text("Sıfırlama Bağlantısı Gönder")
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
                                        Text("Giriş Sayfasına Dön")
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
