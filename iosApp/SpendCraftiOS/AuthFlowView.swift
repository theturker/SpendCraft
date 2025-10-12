import SwiftUI

struct AuthFlowView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @State private var currentAuthScreen: AuthScreen = .login
    
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
        .environmentObject(authViewModel)
    }
}

#Preview {
    AuthFlowView()
}

