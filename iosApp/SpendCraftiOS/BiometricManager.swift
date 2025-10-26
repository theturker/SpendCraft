//
//  BiometricManager.swift
//  SpendCraftiOS
//
//  Face ID / Touch ID Yönetimi
//

import Foundation
import LocalAuthentication

enum BiometricType {
    case none
    case touchID
    case faceID
}

@MainActor
class BiometricManager: ObservableObject {
    static let shared = BiometricManager()
    
    @Published var biometricType: BiometricType = .none
    @Published var isAvailable: Bool = false
    
    private init() {
        Task { @MainActor in
            checkBiometricAvailability()
        }
    }
    
    func checkBiometricAvailability() {
        let context = LAContext()
        var error: NSError?
        
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            // Biometric tipini belirle
            let biometryType = context.biometryType
            DispatchQueue.main.async { [weak self] in
                self?.isAvailable = true
                switch biometryType {
                case .faceID:
                    self?.biometricType = .faceID
                case .touchID:
                    self?.biometricType = .touchID
                case .none:
                    self?.biometricType = .none
                @unknown default:
                    self?.biometricType = .none
                }
            }
        } else {
            DispatchQueue.main.async { [weak self] in
                self?.isAvailable = false
                self?.biometricType = .none
            }
        }
    }
    
    func getBiometricTypeName() -> String {
        switch biometricType {
        case .faceID:
            return "Face ID"
        case .touchID:
            return "Touch ID"
        case .none:
            return "Biometric"
        }
    }
    
    func authenticate(reason: String = "Uygulamaya giriş yapın") async throws -> Bool {
        let context = LAContext()
        context.localizedFallbackTitle = "" // Şifre fallback'i gizle
        
        do {
            let success = try await context.evaluatePolicy(
                .deviceOwnerAuthenticationWithBiometrics,
                localizedReason: reason
            )
            return success
        } catch {
            throw error
        }
    }
    
    var isBiometricEnabled: Bool {
        UserDefaults.standard.bool(forKey: "biometric_enabled")
    }
    
    func setBiometricEnabled(_ enabled: Bool) {
        UserDefaults.standard.set(enabled, forKey: "biometric_enabled")
    }
}
