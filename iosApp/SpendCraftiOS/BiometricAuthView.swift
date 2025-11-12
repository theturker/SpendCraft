//
//  BiometricAuthView.swift
//  SpendCraftiOS
//
//  Face ID / Touch ID Authentication Ekranı
//

import SwiftUI

struct BiometricAuthView: View {
    @EnvironmentObject var biometricManager: BiometricManager
    @State private var showError = false
    @State private var errorMessage = ""
    
    let onAuthenticated: () -> Void
    
    var body: some View {
        ZStack {
            // Background
            Color.black.opacity(0.95)
                .ignoresSafeArea()
            
            VStack(spacing: 40) {
                Spacer()
                
                // App Icon
                Image(systemName: "lock.shield.fill")
                    .font(.system(size: 80))
                    .foregroundColor(.white)
                
                // Title
                Text(NSLocalizedString("settings.security", comment: "Security"))
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                
                // Message
                VStack(spacing: 12) {
                    Text(NSLocalizedString("biometric.auth.message", comment: "Biometric auth message"))
                        .font(.body)
                        .foregroundColor(.white.opacity(0.8))
                    
                    Text(biometricManager.getBiometricTypeName())
                        .font(.title3)
                        .fontWeight(.semibold)
                        .foregroundColor(.white)
                    
                    Text(NSLocalizedString("biometric.auth.message.end", comment: "Biometric auth end"))
                        .font(.body)
                        .foregroundColor(.white.opacity(0.8))
                }
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)
                
                Spacer()
                
                // Auth Button
                Button {
                    authenticate()
                } label: {
                    HStack {
                        Image(systemName: biometricManager.biometricType == .faceID ? "faceid" : "touchid")
                            .font(.title2)
                        Text(String(format: NSLocalizedString("biometric.auth.button", comment: "Authenticate button"), biometricManager.getBiometricTypeName()))
                            .fontWeight(.semibold)
                    }
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.white)
                    .cornerRadius(12)
                    .padding(.horizontal, 40)
                }
                
                Spacer()
                    .frame(height: 100)
            }
        }
        .onAppear {
            // Otomatik olarak biometric authentication başlat
            authenticate()
        }
        .alert(NSLocalizedString("biometric.auth.alert.title", comment: "Authentication error"), isPresented: $showError) {
            Button(NSLocalizedString("biometric.auth.alert.retry", comment: "Retry")) {
                authenticate()
            }
            Button(NSLocalizedString("common.cancel", comment: "Cancel"), role: .cancel) {}
        } message: {
            Text(errorMessage)
        }
    }
    
    private func authenticate() {
        Task {
            do {
                let success = try await biometricManager.authenticate(reason: NSLocalizedString("biometric.auth.reason", comment: "Biometric auth reason"))
                if success {
                    onAuthenticated()
                }
            } catch {
                errorMessage = error.localizedDescription
                showError = true
            }
        }
    }
}

#Preview {
    BiometricAuthView(onAuthenticated: {})
        .environmentObject(BiometricManager.shared)
}
