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
        ("llama-3.1-8b-instant", "Llama 3.1 8B (Hızlı)"),
        ("llama-3.2-3b-preview", "Llama 3.2 3B (Hafif)"),
        ("mixtral-8x7b-32768", "Mixtral 8x7B (Güçlü)")
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
                            Text("AI Önerilerini Etkinleştir")
                        }
                    }
                } header: {
                    Text("Durum")
                } footer: {
                    Text(aiEnabled ?
                         "AI destekli finansal öneriler aktif." :
                         "AI özellikleri devre dışı.")
                }
                
                if aiEnabled {
                    // Model Selection
                    Section {
                        Picker("Model", selection: $selectedModel) {
                            ForEach(availableModels, id: \.0) { model in
                                Text(model.1).tag(model.0)
                            }
                        }
                        .pickerStyle(.menu)
                        
                        HStack {
                            Text("Mevcut Model")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(getModelName(selectedModel))
                                .foregroundColor(.blue)
                        }
                    } header: {
                        Text("AI Modeli")
                    } footer: {
                        Text("Farklı modeller farklı hızda ve kalitede sonuç verir. Llama 3.1 8B dengeli performans sunar.")
                    }
                    
                    // Advanced Settings
                    Section {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text("Yaratıcılık Seviyesi")
                                Spacer()
                                Text(String(format: "%.1f", temperature))
                                    .foregroundColor(.secondary)
                            }
                            
                            Slider(value: $temperature, in: 0.1...1.0, step: 0.1)
                                .accentColor(.purple)
                            
                            Text("Düşük: Daha tutarlı, Yüksek: Daha yaratıcı")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text("Maksimum Uzunluk")
                                Spacer()
                                Text("\(Int(maxTokens))")
                                    .foregroundColor(.secondary)
                            }
                            
                            Slider(value: $maxTokens, in: 200...1000, step: 100)
                                .accentColor(.purple)
                            
                            Text("Öneri metninin maksimum uzunluğu")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    } header: {
                        Text("Gelişmiş Ayarlar")
                    } footer: {
                        Text("Bu ayarlar AI'ın davranışını etkiler. Varsayılan değerler çoğu kullanım için uygundur.")
                    }
                    
                    // Features
                    Section {
                        Toggle(isOn: $showAIBadges) {
                            HStack {
                                Image(systemName: "sparkles.square.filled.on.square")
                                    .foregroundColor(.blue)
                                VStack(alignment: .leading) {
                                    Text("AI Rozetlerini Göster")
                                    Text("AI destekli özellikleri vurgula")
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
                                    Text("Otomatik Öneriler")
                                    Text("Belirli aralıklarla otomatik AI önerisi al")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                    } header: {
                        Text("Özellikler")
                    }
                    
                    // API Info
                    Section {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("API Sağlayıcısı")
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
                                Text("Bağlantı Durumu")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text("Aktif")
                                    .font(.body)
                                    .fontWeight(.medium)
                            }
                            Spacer()
                            Circle()
                                .fill(Color.green)
                                .frame(width: 12, height: 12)
                        }
                    } header: {
                        Text("API Bilgileri")
                    } footer: {
                        Text("API bağlantısı güvenli ve şifrelenmiş.")
                    }
                    
                    // Reset
                    Section {
                        Button {
                            resetToDefaults()
                        } label: {
                            HStack {
                                Spacer()
                                Text("Varsayılanlara Sıfırla")
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
                            Text("Yapay zeka desteği ile harcamalarınızı analiz edin")
                        } icon: {
                            Image(systemName: "chart.line.uptrend.xyaxis")
                                .foregroundColor(.blue)
                        }
                        
                        Label {
                            Text("Kişiselleştirilmiş finansal öneriler alın")
                        } icon: {
                            Image(systemName: "lightbulb.fill")
                                .foregroundColor(.yellow)
                        }
                        
                        Label {
                            Text("Tasarruf fırsatlarını keşfedin")
                        } icon: {
                            Image(systemName: "banknote")
                                .foregroundColor(.green)
                        }
                    }
                    .font(.caption)
                } header: {
                    Text("AI Özellikleri")
                }
            }
            .navigationTitle("AI Ayarları")
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
                    Button("Tamam") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    // MARK: - Helpers
    
    private func getModelName(_ model: String) -> String {
        availableModels.first(where: { $0.0 == model })?.1 ?? "Bilinmeyen"
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
