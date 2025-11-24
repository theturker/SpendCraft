//
//  ReceiptAnalysisView.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import SwiftUI

struct ReceiptAnalysisView: View {
    let image: UIImage
    let analysisResult: ReceiptAnalysisResult
    let onConfirm: (Double, String?, Date) -> Void
    let onCancel: () -> Void
    
    @State private var amount: String
    @State private var note: String
    @State private var date: Date
    
    init(image: UIImage, analysisResult: ReceiptAnalysisResult, onConfirm: @escaping (Double, String?, Date) -> Void, onCancel: @escaping () -> Void) {
        self.image = image
        self.analysisResult = analysisResult
        self.onConfirm = onConfirm
        self.onCancel = onCancel
        
        _amount = State(initialValue: analysisResult.amount != nil ? String(format: "%.2f", analysisResult.amount!) : "")
        _note = State(initialValue: analysisResult.merchant ?? "")
        _date = State(initialValue: analysisResult.date ?? Date())
    }
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Fotoğraf önizleme
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 300)
                        .cornerRadius(12)
                        .padding(.horizontal)
                    
                    // Analiz sonuçları
                    VStack(alignment: .leading, spacing: 16) {
                        // Tutar
                        VStack(alignment: .leading, spacing: 8) {
                            Text(NSLocalizedString("receipt.amount", comment: "Amount"))
                                .font(.headline)
                            TextField(NSLocalizedString("receipt.amount.placeholder", comment: "Enter amount"), text: $amount)
                                .keyboardType(.decimalPad)
                                .textFieldStyle(.roundedBorder)
                        }
                        
                        // Not/İşyeri
                        VStack(alignment: .leading, spacing: 8) {
                            Text(NSLocalizedString("receipt.merchant", comment: "Merchant/Note"))
                                .font(.headline)
                            TextField(NSLocalizedString("receipt.merchant.placeholder", comment: "Merchant name or note"), text: $note)
                                .textFieldStyle(.roundedBorder)
                        }
                        
                        // Tarih
                        VStack(alignment: .leading, spacing: 8) {
                            Text(NSLocalizedString("receipt.date", comment: "Date"))
                                .font(.headline)
                            DatePicker("", selection: $date, displayedComponents: [.date, .hourAndMinute])
                                .datePickerStyle(.compact)
                        }
                        
                        // Tespit edilen ürünler (varsa)
                        if !analysisResult.items.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text(NSLocalizedString("receipt.items", comment: "Detected Items"))
                                    .font(.headline)
                                ForEach(analysisResult.items.prefix(5), id: \.self) { item in
                                    Text("• \(item)")
                                        .font(.subheadline)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                    }
                    .padding()
                    
                    // Onayla butonu
                    Button {
                        if let amountValue = Double(amount.replacingOccurrences(of: ",", with: ".")) {
                            onConfirm(amountValue, note.isEmpty ? nil : note, date)
                        }
                    } label: {
                        Text(NSLocalizedString("receipt.confirm", comment: "Add Expense"))
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(12)
                    }
                    .padding(.horizontal)
                    .disabled(amount.isEmpty || Double(amount.replacingOccurrences(of: ",", with: ".")) == nil)
                }
                .padding(.vertical)
            }
            .navigationTitle(NSLocalizedString("receipt.review", comment: "Review Receipt"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
                        onCancel()
                    }
                }
            }
        }
    }
}


