import Foundation
import SwiftUI

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