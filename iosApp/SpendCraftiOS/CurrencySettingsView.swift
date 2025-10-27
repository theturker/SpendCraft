//
//  CurrencySettingsView.swift
//  SpendCraftiOS
//
//  Para Birimi Ayarları
//

import SwiftUI

struct Currency: Identifiable {
    let id: String
    let code: String
    let symbol: String
    let name: String
    let flag: String
    
    static let currencies: [Currency] = [
        Currency(id: "TRY", code: "TRY", symbol: "₺", name: "Türk Lirası", flag: "🇹🇷"),
        Currency(id: "USD", code: "USD", symbol: "$", name: "Amerikan Doları", flag: "🇺🇸"),
        Currency(id: "EUR", code: "EUR", symbol: "€", name: "Euro", flag: "🇪🇺"),
        Currency(id: "GBP", code: "GBP", symbol: "£", name: "İngiliz Sterlini", flag: "🇬🇧"),
        Currency(id: "JPY", code: "JPY", symbol: "¥", name: "Japon Yeni", flag: "🇯🇵"),
        Currency(id: "CHF", code: "CHF", symbol: "Fr", name: "İsviçre Frangı", flag: "🇨🇭"),
        Currency(id: "CAD", code: "CAD", symbol: "C$", name: "Kanada Doları", flag: "🇨🇦"),
        Currency(id: "AUD", code: "AUD", symbol: "A$", name: "Avustralya Doları", flag: "🇦🇺"),
        Currency(id: "CNY", code: "CNY", symbol: "¥", name: "Çin Yuanı", flag: "🇨🇳"),
        Currency(id: "RUB", code: "RUB", symbol: "₽", name: "Rus Rublesi", flag: "🇷🇺"),
        Currency(id: "SAR", code: "SAR", symbol: "﷼", name: "Suudi Arabistan Riyali", flag: "🇸🇦"),
        Currency(id: "AED", code: "AED", symbol: "د.إ", name: "BAE Dirhemi", flag: "🇦🇪")
    ]
}

struct CurrencySettingsView: View {
    @Environment(\.dismiss) var dismiss
    @AppStorage("selectedCurrency") private var selectedCurrency: String = "TRY"
    @AppStorage("selectedCurrencySymbol") private var selectedCurrencySymbol: String = "₺"
    
    @State private var searchText = ""
    
    var filteredCurrencies: [Currency] {
        if searchText.isEmpty {
            return Currency.currencies
        } else {
            return Currency.currencies.filter { 
                $0.name.localizedCaseInsensitiveContains(searchText) ||
                $0.code.localizedCaseInsensitiveContains(searchText)
            }
        }
    }
    
    var body: some View {
        List {
            Section {
                ForEach(filteredCurrencies) { currency in
                    Button {
                        selectCurrency(currency)
                    } label: {
                        HStack(spacing: 16) {
                            Text(currency.flag)
                                .font(.system(size: 32))
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(NSLocalizedString(currency.name, comment: ""))
                                    .font(.subheadline)
                                    .fontWeight(.medium)
                                    .foregroundColor(.primary)
                                
                                Text("\(currency.code) (\(currency.symbol))")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                            
                            if selectedCurrency == currency.code {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.blue)
                                    .font(.title3)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                    .buttonStyle(.plain)
                }
            } header: {
                Text(NSLocalizedString("currency.available", comment: "Available Currencies"))
            } footer: {
                Text(NSLocalizedString("currency.info", comment: "Currency info"))
            }
        }
        .navigationTitle(NSLocalizedString("currency.selection", comment: "Currency Selection"))
        .navigationBarTitleDisplayMode(.large)
        .searchable(text: $searchText, prompt: NSLocalizedString("currency.search", comment: "Search currency"))
    }
    
    private func selectCurrency(_ currency: Currency) {
        selectedCurrency = currency.code
        selectedCurrencySymbol = currency.symbol
        
        // Haptic feedback
        let generator = UIImpactFeedbackGenerator(style: .medium)
        generator.impactOccurred()
    }
}

#Preview {
    NavigationStack {
        CurrencySettingsView()
    }
}

