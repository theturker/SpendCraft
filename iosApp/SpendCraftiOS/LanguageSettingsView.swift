//
//  LanguageSettingsView.swift
//  SpendCraftiOS
//
//  Dil Seçici Ayarları
//

import SwiftUI

struct Language {
    let code: String
    let name: String
    let nativeName: String
    let flag: String
}

struct LanguageSettingsView: View {
    @Environment(\.dismiss) var dismiss
    @AppStorage("selectedLanguage") private var selectedLanguage: String = ""
    @State private var showSuccessAlert = false
    
    private let languages = [
        Language(code: "tr", name: "Turkish", nativeName: "Türkçe", flag: "🇹🇷"),
        Language(code: "en", name: "English", nativeName: "English", flag: "🇬🇧")
    ]
    
    var body: some View {
        List {
            Section {
                ForEach(languages, id: \.code) { language in
                    Button {
                        // Sadece farklı bir dil seçilirse
                        if selectedLanguage != language.code {
                            selectedLanguage = language.code
                            // Dil değişikliğini hemen uygula
                            LanguageHelper.shared.setLanguage(language.code)
                            showSuccessAlert = true
                        }
                    } label: {
                        HStack {
                            Text(language.flag)
                                .font(.largeTitle)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(language.nativeName)
                                    .font(.body)
                                    .fontWeight(.medium)
                                    .foregroundColor(.primary)
                                Text(language.name)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                            
                            if selectedLanguage == language.code || (selectedLanguage.isEmpty && language.code == "tr") {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.blue)
                            }
                        }
                    }
                    .foregroundColor(.primary)
                }
            } footer: {
                Text(NSLocalizedString("settings.language.restart.message", comment: "Restart message"))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .navigationTitle(NSLocalizedString("settings.language.selection", comment: "Language Selection"))
        .navigationBarTitleDisplayMode(.inline)
        .alert(NSLocalizedString("language.changed", comment: "Language Changed"), isPresented: $showSuccessAlert) {
            Button(NSLocalizedString("common.ok", comment: "OK")) {}
        } message: {
            Text(NSLocalizedString("language.changed.message", comment: "Restart message"))
        }
    }
}

#Preview {
    NavigationView {
        LanguageSettingsView()
    }
}
