//
//  CurrencyTextField.swift
//  SpendCraftiOS
//
//  Para birimi formatında miktar girişi için özel TextField
//

import SwiftUI
import Combine

struct CurrencyTextField: View {
    let title: String
    @Binding var value: String
    @FocusState.Binding var isFocused: Bool
    
    @State private var displayValue: String = ""
    
    var body: some View {
        TextField(title, text: $displayValue)
            .keyboardType(.decimalPad)
            .font(.title2)
            .focused($isFocused)
            .onChange(of: displayValue) { newValue in
                formatInput(newValue)
            }
            .onAppear {
                // Initialize display value
                if !value.isEmpty {
                    displayValue = formatNumber(value)
                }
            }
    }
    
    private func formatInput(_ input: String) {
        // Remove all non-digit characters except comma and dot
        let cleaned = input.replacingOccurrences(of: "[^0-9,.]", with: "", options: .regularExpression)
        
        // Parse the number
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        
        // Get currency settings
        let currency = getCurrentCurrencyCode()
        if currency == "TRY" {
            formatter.locale = Locale(identifier: "tr_TR")
            formatter.groupingSeparator = "."
            formatter.decimalSeparator = ","
        } else {
            formatter.locale = Locale(identifier: "en_US")
            formatter.groupingSeparator = ","
            formatter.decimalSeparator = "."
        }
        
        // Split by decimal separator
        let decimalSep = currency == "TRY" ? "," : "."
        let parts = cleaned.split(separator: Character(decimalSep), maxSplits: 1)
        
        var integerPart = String(parts.first ?? "")
        var decimalPart = parts.count > 1 ? String(parts[1]) : ""
        
        // Remove any grouping separators from input
        integerPart = integerPart.replacingOccurrences(of: ".", with: "")
        integerPart = integerPart.replacingOccurrences(of: ",", with: "")
        
        // Limit decimal places to 2
        if decimalPart.count > 2 {
            decimalPart = String(decimalPart.prefix(2))
        }
        
        // Update the raw value (without formatting) for binding
        if decimalPart.isEmpty {
            value = integerPart
        } else {
            value = "\(integerPart).\(decimalPart)"
        }
        
        // Format and update display
        if !integerPart.isEmpty {
            displayValue = formatNumber(value)
        } else {
            displayValue = ""
        }
    }
    
    private func formatNumber(_ number: String) -> String {
        guard let doubleValue = Double(number) else {
            return number
        }
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        
        let currency = getCurrentCurrencyCode()
        if currency == "TRY" {
            formatter.locale = Locale(identifier: "tr_TR")
            formatter.groupingSeparator = "."
            formatter.decimalSeparator = ","
        } else {
            formatter.locale = Locale(identifier: "en_US")
            formatter.groupingSeparator = ","
            formatter.decimalSeparator = "."
        }
        
        return formatter.string(from: NSNumber(value: doubleValue)) ?? number
    }
}

#Preview {
    struct PreviewWrapper: View {
        @State private var amount = ""
        @FocusState private var isFocused: Bool
        
        var body: some View {
            Form {
                Section("Miktar") {
                    HStack {
                        CurrencyTextField(title: "0.00", value: $amount, isFocused: $isFocused)
                        Text(getCurrentCurrencySymbol())
                            .font(.title2)
                            .foregroundColor(.secondary)
                    }
                }
                
                Section("Debug") {
                    Text("Raw Value: \(amount)")
                }
            }
        }
    }
    
    return PreviewWrapper()
}


