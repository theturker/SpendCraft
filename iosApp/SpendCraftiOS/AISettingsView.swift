//
//  AISettingsView.swift
//  SpendCraftiOS
//
//  AI Ayarları Ekranı
//

import SwiftUI

struct AISettingsView: View {
    @AppStorage("aiEnabled") private var aiEnabled = true
    @AppStorage("aiModel") private var selectedModel = "llama-3.1-8b-instant"
    @AppStorage("aiTemperature") private var temperature = 0.7
    @AppStorage("aiMaxTokens") private var maxTokens = 500.0
    @AppStorage("showAIBadges") private var showAIBadges = true
    @AppStorage("aiAutoSuggest") private var aiAutoSuggest = false
    
    @Environment(\.dismiss) private var dismiss
    
    let availableModels = [
        ("llama-3.1-8b-instant", "Llama 3.1 8B (Fast)"),
        ("llama-3.2-3b-preview", "Llama 3.2 3B (Light)"),
        ("mixtral-8x7b-32768", "Mixtral 8x7B (Powerful)")
    ]
    
    var body: some View {
        NavigationView {
            Form {
                // AI Status
                Section {
                    Toggle(isOn: $aiEnabled) {
                        HStack {
                            Image(systemName: "sparkles")
                                .foregroundColor(.purple)
                            Text(NSLocalizedString("ai.enable.suggestions", comment: "Enable AI Suggestions"))
                        }
                    }
                } header: {
                    Text(NSLocalizedString("ai.status", comment: "Status"))
                } footer: {
                    Text(aiEnabled ?
                         NSLocalizedString("ai.status.active", comment: "AI-powered financial suggestions are active.") :
                         NSLocalizedString("ai.status.inactive", comment: "AI features are disabled."))
                }
                
                if aiEnabled {
                    // Model Selection
                    Section {
                        Picker(NSLocalizedString("ai.model", comment: "Model"), selection: $selectedModel) {
                            ForEach(availableModels, id: \.0) { model in
                                Text(model.1).tag(model.0)
                            }
                        }
                        .pickerStyle(.menu)
                        
                        HStack {
                            Text(NSLocalizedString("ai.model.current", comment: "Current Model"))
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(getModelName(selectedModel))
                                .foregroundColor(.blue)
                        }
                    } header: {
                        Text(NSLocalizedString("ai.model", comment: "AI Model"))
                    } footer: {
                        Text(NSLocalizedString("ai.model.description", comment: "Different models provide different speed and quality results. Llama 3.1 8B offers balanced performance."))
                    }
                    
                    // Advanced Settings
                    Section {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(NSLocalizedString("ai.creativity.level", comment: "Creativity Level"))
                                Spacer()
                                Text(String(format: "%.1f", temperature))
                                    .foregroundColor(.secondary)
                            }
                            
                            Slider(value: $temperature, in: 0.1...1.0, step: 0.1)
                                .accentColor(.purple)
                            
                            Text(NSLocalizedString("ai.creativity.low", comment: "Low: More consistent, High: More creative"))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(NSLocalizedString("ai.max.length", comment: "Maximum Length"))
                                Spacer()
                                Text("\(Int(maxTokens))")
                                    .foregroundColor(.secondary)
                            }
                            
                            Slider(value: $maxTokens, in: 200...1000, step: 100)
                                .accentColor(.purple)
                            
                            Text(NSLocalizedString("ai.max.length.description", comment: "Maximum length of suggestion text"))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    } header: {
                        Text(NSLocalizedString("ai.advanced.settings", comment: "Advanced Settings"))
                    } footer: {
                        Text(NSLocalizedString("ai.advanced.settings", comment: "This settings affect AI behavior. Default values are suitable for most usage."))
                    }
                    
                    // Features
                    Section {
                        Toggle(isOn: $showAIBadges) {
                            HStack {
                                Image(systemName: "sparkles.square.filled.on.square")
                                    .foregroundColor(.blue)
                                VStack(alignment: .leading) {
                                    Text(NSLocalizedString("ai.show.badges", comment: "Show AI Badges"))
                                    Text(NSLocalizedString("ai.show.badges.description", comment: "Highlight AI-powered features"))
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                        
                        Toggle(isOn: $aiAutoSuggest) {
                            HStack {
                                Image(systemName: "wand.and.stars")
                                    .foregroundColor(.purple)
                                VStack(alignment: .leading) {
                                    Text(NSLocalizedString("ai.auto.suggest", comment: "Auto Suggestions"))
                                    Text(NSLocalizedString("ai.auto.suggest.description", comment: "Get automatic AI suggestions at certain intervals"))
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                    } header: {
                        Text(NSLocalizedString("ai.features", comment: "Features"))
                    }
                    
                    // API Info
                    Section {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(NSLocalizedString("ai.api.provider", comment: "API Provider"))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text("Groq")
                                    .font(.body)
                                    .fontWeight(.medium)
                            }
                            Spacer()
                            Image(systemName: "checkmark.seal.fill")
                                .foregroundColor(.green)
                        }
                        
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(NSLocalizedString("ai.api.status", comment: "Connection Status"))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text(NSLocalizedString("common.active", comment: "Active"))
                                    .font(.body)
                                    .fontWeight(.medium)
                            }
                            Spacer()
                            Circle()
                                .fill(Color.green)
                                .frame(width: 12, height: 12)
                        }
                    } header: {
                        Text(NSLocalizedString("ai.api.info", comment: "API Information"))
                    } footer: {
                        Text(NSLocalizedString("ai.api.secure", comment: "API connection is secure and encrypted."))
                    }
                    
                    // Reset
                    Section {
                        Button {
                            resetToDefaults()
                        } label: {
                            HStack {
                                Spacer()
                                Text(NSLocalizedString("ai.reset.defaults", comment: "Reset to Defaults"))
                                    .foregroundColor(.red)
                                Spacer()
                            }
                        }
                    }
                }
                
                // Info
                Section {
                    VStack(alignment: .leading, spacing: 12) {
                        Label {
                            Text(NSLocalizedString("ai.analyze.spending", comment: "Analyze your spending with AI support"))
                        } icon: {
                            Image(systemName: "chart.line.uptrend.xyaxis")
                                .foregroundColor(.blue)
                        }
                        
                        Label {
                            Text(NSLocalizedString("ai.personalized.suggestions", comment: "Get personalized financial suggestions"))
                        } icon: {
                            Image(systemName: "lightbulb.fill")
                                .foregroundColor(.yellow)
                        }
                        
                        Label {
                            Text(NSLocalizedString("ai.discover.savings", comment: "Discover saving opportunities"))
                        } icon: {
                            Image(systemName: "banknote")
                                .foregroundColor(.green)
                        }
                    }
                    .font(.caption)
                } header: {
                    Text(NSLocalizedString("ai.features.title", comment: "AI Features"))
                }
            }
            .navigationTitle(NSLocalizedString("ai.settings.title", comment: "AI Settings"))
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.secondary)
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(NSLocalizedString("common.ok", comment: "OK")) {
                        dismiss()
                    }
                }
            }
        }
    }
    
    // MARK: - Helpers
    
    private func getModelName(_ model: String) -> String {
        availableModels.first(where: { $0.0 == model })?.1 ?? NSLocalizedString("common.unknown", comment: "Unknown")
    }
    
    private func resetToDefaults() {
        aiEnabled = true
        selectedModel = "llama-3.1-8b-instant"
        temperature = 0.7
        maxTokens = 500.0
        showAIBadges = true
        aiAutoSuggest = false
    }
}

#Preview {
    AISettingsView()
}

