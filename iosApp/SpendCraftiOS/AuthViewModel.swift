import Foundation
import SwiftUI
import FirebaseAuth

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
        // Firebase auth state listener
        Auth.auth().addStateDidChangeListener { [weak self] _, user in
            DispatchQueue.main.async {
                if let user = user {
                    self?.currentUser = UserModel(
                        id: user.uid,
                        email: user.email ?? "",
                        displayName: user.displayName ?? user.email?.components(separatedBy: "@").first ?? "Kullanıcı",
                        isEmailVerified: user.isEmailVerified
                    )
                    self?.isAuthenticated = true
                } else {
                    self?.currentUser = nil
                    self?.isAuthenticated = false
                }
            }
        }
    }
    
    // MARK: - Auth State Management
    
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
        
        // Firebase ile giriş yap
        do {
            let result = try await Auth.auth().signIn(withEmail: email, password: password)
            // Firebase auth state listener otomatik olarak güncelleyecek
            isLoading = false
        } catch {
            isLoading = false
            errorMessage = error.localizedDescription
            throw AuthError.networkError
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
        
        // Firebase ile kayıt ol
        do {
            let result = try await Auth.auth().createUser(withEmail: email, password: password)
            
            // Kullanıcı adını güncelle
            let changeRequest = result.user.createProfileChangeRequest()
            changeRequest.displayName = name
            try await changeRequest.commitChanges()
            
            // Email doğrulama gönder
            try await result.user.sendEmailVerification()
            
            // Firebase auth state listener otomatik olarak güncelleyecek
            isLoading = false
        } catch {
            isLoading = false
            errorMessage = error.localizedDescription
            throw AuthError.networkError
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
        
        // Firebase ile şifre sıfırlama gönder
        do {
            try await Auth.auth().sendPasswordReset(withEmail: email)
            isLoading = false
        } catch {
            isLoading = false
            errorMessage = error.localizedDescription
            throw AuthError.networkError
        }
    }
    
    func signOut() async throws {
        // Firebase'den çıkış yap
        do {
            try Auth.auth().signOut()
            
            // CoreData'yı temizle
            CoreDataStack.shared.clearAllUserData()
            
            // Seed initial data for next user
            CoreDataStack.shared.seedInitialData()
            
            // Firebase auth state listener otomatik olarak güncelleyecek
            print("✅ User signed out and data cleared")
        } catch {
            errorMessage = error.localizedDescription
            throw AuthError.networkError
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