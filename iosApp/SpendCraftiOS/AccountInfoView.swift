//
//  AccountInfoView.swift
//  SpendCraftiOS
//
//  Kullanıcı hesap bilgileri ekranı
//

import SwiftUI
import FirebaseAuth

struct AccountInfoView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var authViewModel: AuthViewModel
    
    @State private var isEditingName = false
    @State private var newName = ""
    @State private var showChangePassword = false
    @State private var showDeleteAccount = false
    @State private var isLoading = false
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        List {
            // User Profile Section
            Section {
                HStack(spacing: 16) {
                    // Avatar
                    Circle()
                        .fill(LinearGradient(
                            colors: [.blue, .purple],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ))
                        .frame(width: 80, height: 80)
                        .overlay(
                            Text(authViewModel.userDisplayName.prefix(1).uppercased())
                                .font(.largeTitle)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                        )
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text(authViewModel.userDisplayName)
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        Text(authViewModel.userEmail)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        
                        HStack(spacing: 6) {
                            Image(systemName: authViewModel.isEmailVerified ? "checkmark.seal.fill" : "exclamationmark.triangle.fill")
                                .font(.caption)
                                .foregroundColor(authViewModel.isEmailVerified ? .green : .orange)
                            Text(authViewModel.isEmailVerified ? "E-posta doğrulandı" : "E-posta doğrulanmadı")
                                .font(.caption)
                                .foregroundColor(authViewModel.isEmailVerified ? .green : .orange)
                        }
                    }
                }
                .padding(.vertical, 8)
            }
            
            // Account Actions
            Section("Hesap İşlemleri") {
                // Edit Name
                Button {
                    newName = authViewModel.userDisplayName
                    isEditingName = true
                } label: {
                    HStack {
                        Image(systemName: "person.fill")
                            .foregroundColor(.blue)
                        Text("Adı Düzenle")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
                
                // Change Password
                Button {
                    showChangePassword = true
                } label: {
                    HStack {
                        Image(systemName: "lock.fill")
                            .foregroundColor(.orange)
                        Text("Şifre Değiştir")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
                
                // Email Verification
                if !authViewModel.isEmailVerified {
                    Button {
                        sendVerificationEmail()
                    } label: {
                        HStack {
                            Image(systemName: "envelope.fill")
                                .foregroundColor(.green)
                            Text("E-posta Doğrulama Gönder")
                            Spacer()
                            if isLoading {
                                ProgressView()
                            }
                        }
                    }
                    .foregroundColor(.primary)
                    .disabled(isLoading)
                }
            }
            
            // Danger Zone
            Section("Tehlikeli Bölge") {
                Button {
                    showDeleteAccount = true
                } label: {
                    HStack {
                        Image(systemName: "trash.fill")
                            .foregroundColor(.red)
                        Text("Hesabı Sil")
                    }
                }
                .foregroundColor(.red)
            }
        }
        .navigationTitle("Hesap Bilgileri")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Bilgi", isPresented: $showAlert) {
            Button("Tamam", role: .cancel) {}
        } message: {
            Text(alertMessage)
        }
        .sheet(isPresented: $isEditingName) {
            EditNameSheet(name: $newName, authViewModel: authViewModel)
        }
        .sheet(isPresented: $showChangePassword) {
            ChangePasswordSheet()
        }
        .alert("Hesabı Sil", isPresented: $showDeleteAccount) {
            Button("İptal", role: .cancel) {}
            Button("Sil", role: .destructive) {
                deleteAccount()
            }
        } message: {
            Text("Hesabınızı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz ve tüm verileriniz silinecektir.")
        }
    }
    
    func sendVerificationEmail() {
        isLoading = true
        Auth.auth().currentUser?.sendEmailVerification { error in
            isLoading = false
            if let error = error {
                alertMessage = "Doğrulama e-postası gönderilemedi: \(error.localizedDescription)"
            } else {
                alertMessage = "Doğrulama e-postası gönderildi! E-postanızı kontrol edin."
            }
            showAlert = true
        }
    }
    
    func deleteAccount() {
        alertMessage = "Hesap silme işlemi için lütfen destek ekibiyle iletişime geçin."
        showAlert = true
    }
}

// MARK: - Edit Name Sheet

struct EditNameSheet: View {
    @Environment(\.dismiss) var dismiss
    @Binding var name: String
    @ObservedObject var authViewModel: AuthViewModel
    @State private var isLoading = false
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            Form {
                Section("Yeni Ad") {
                    TextField("Adınız", text: $name)
                }
            }
            .navigationTitle("Adı Düzenle")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Kaydet") {
                        saveName()
                    }
                    .disabled(name.isEmpty || isLoading)
                }
            }
            .alert("Bilgi", isPresented: $showAlert) {
                Button("Tamam", role: .cancel) {
                    if alertMessage.contains("başarıyla") {
                        dismiss()
                    }
                }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    func saveName() {
        isLoading = true
        let changeRequest = Auth.auth().currentUser?.createProfileChangeRequest()
        changeRequest?.displayName = name
        changeRequest?.commitChanges { error in
            isLoading = false
            if let error = error {
                alertMessage = "Ad güncellenemedi: \(error.localizedDescription)"
            } else {
                alertMessage = "Adınız başarıyla güncellendi!"
            }
            showAlert = true
        }
    }
}

// MARK: - Change Password Sheet

struct ChangePasswordSheet: View {
    @Environment(\.dismiss) var dismiss
    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var isLoading = false
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            Form {
                Section("Mevcut Şifre") {
                    SecureField("Mevcut şifreniz", text: $currentPassword)
                }
                
                Section("Yeni Şifre") {
                    SecureField("Yeni şifre", text: $newPassword)
                    SecureField("Yeni şifre (tekrar)", text: $confirmPassword)
                }
                
                Section {
                    Text("Şifreniz en az 6 karakter olmalıdır.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            .navigationTitle("Şifre Değiştir")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Kaydet") {
                        changePassword()
                    }
                    .disabled(!isValid || isLoading)
                }
            }
            .alert("Bilgi", isPresented: $showAlert) {
                Button("Tamam", role: .cancel) {
                    if alertMessage.contains("başarıyla") {
                        dismiss()
                    }
                }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    var isValid: Bool {
        !currentPassword.isEmpty && !newPassword.isEmpty &&
        newPassword == confirmPassword && newPassword.count >= 6
    }
    
    func changePassword() {
        guard isValid else { return }
        
        isLoading = true
        guard let user = Auth.auth().currentUser,
              let email = user.email else {
            alertMessage = "Kullanıcı bilgileri alınamadı"
            showAlert = true
            isLoading = false
            return
        }
        
        // Re-authenticate first
        let credential = EmailAuthProvider.credential(withEmail: email, password: currentPassword)
        user.reauthenticate(with: credential) { _, error in
            if let error = error {
                isLoading = false
                alertMessage = "Mevcut şifre hatalı: \(error.localizedDescription)"
                showAlert = true
                return
            }
            
            // Update password
            user.updatePassword(to: newPassword) { error in
                isLoading = false
                if let error = error {
                    alertMessage = "Şifre güncellenemedi: \(error.localizedDescription)"
                } else {
                    alertMessage = "Şifreniz başarıyla güncellendi!"
                }
                showAlert = true
            }
        }
    }
}

#Preview {
    NavigationStack {
        AccountInfoView()
            .environmentObject(AuthViewModel())
    }
}

