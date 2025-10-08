//
//  ExportView.swift
//  SpendCraftiOS
//
//  Dışa/İçe Aktarım Ekranı
//

import SwiftUI
import UniformTypeIdentifiers

struct ExportView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @Environment(\.dismiss) private var dismiss
    
    @State private var showShareSheet = false
    @State private var fileURL: URL?
    @State private var exportType: ExportType = .csv
    @State private var showImportPicker = false
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    enum ExportType: String, CaseIterable {
        case csv = "CSV"
        case pdf = "PDF"
        
        var icon: String {
            switch self {
            case .csv: return "doc.text"
            case .pdf: return "doc.richtext"
            }
        }
        
        var description: String {
            switch self {
            case .csv: return "Excel ve diğer tablolama uygulamalarında açılabilir"
            case .pdf: return "Profesyonel raporlar için ideal"
            }
        }
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(.systemGroupedBackground)
                    .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 20) {
                        // Export Section
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Image(systemName: "square.and.arrow.up")
                                    .font(.title2)
                                    .foregroundColor(.blue)
                                Text("Dışa Aktar")
                                    .font(.title2)
                                    .fontWeight(.bold)
                            }
                            .padding(.horizontal)
                            
                            Text("İşlemlerinizi farklı formatlarda dışa aktarın")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .padding(.horizontal)
                            
                            // Export type selection
                            ForEach(ExportType.allCases, id: \.self) { type in
                                Button {
                                    exportType = type
                                } label: {
                                    HStack {
                                        Image(systemName: type.icon)
                                            .font(.title3)
                                            .frame(width: 40)
                                            .foregroundColor(exportType == type ? .white : .blue)
                                        
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text(type.rawValue)
                                                .font(.headline)
                                                .foregroundColor(exportType == type ? .white : .primary)
                                            
                                            Text(type.description)
                                                .font(.caption)
                                                .foregroundColor(exportType == type ? .white.opacity(0.8) : .secondary)
                                        }
                                        
                                        Spacer()
                                        
                                        if exportType == type {
                                            Image(systemName: "checkmark.circle.fill")
                                                .foregroundColor(.white)
                                        }
                                    }
                                    .padding()
                                    .background(
                                        RoundedRectangle(cornerRadius: 12)
                                            .fill(exportType == type ? Color.blue : Color(.systemBackground))
                                    )
                                }
                                .padding(.horizontal)
                            }
                            
                            // Export button
                            Button {
                                exportData()
                            } label: {
                                HStack {
                                    Image(systemName: "arrow.down.doc")
                                    Text("Dışa Aktar")
                                        .fontWeight(.semibold)
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(12)
                            }
                            .padding(.horizontal)
                        }
                        .padding(.vertical)
                        
                        Divider()
                            .padding(.horizontal)
                        
                        // Import Section
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Image(systemName: "square.and.arrow.down")
                                    .font(.title2)
                                    .foregroundColor(.green)
                                Text("İçe Aktar")
                                    .font(.title2)
                                    .fontWeight(.bold)
                            }
                            .padding(.horizontal)
                            
                            Text("CSV dosyasından işlem aktarın")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .padding(.horizontal)
                            
                            VStack(alignment: .leading, spacing: 8) {
                                Text("CSV Formatı:")
                                    .font(.caption)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.secondary)
                                
                                Text("Tarih,Kategori,Tutar,Not,Tür")
                                    .font(.caption)
                                    .fontWeight(.medium)
                                    .padding(8)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .background(Color(.systemGray6))
                                    .cornerRadius(8)
                            }
                            .padding()
                            .background(Color(.systemBackground))
                            .cornerRadius(12)
                            .padding(.horizontal)
                            
                            Button {
                                showImportPicker = true
                            } label: {
                                HStack {
                                    Image(systemName: "arrow.up.doc")
                                    Text("CSV Dosyası Seç")
                                        .fontWeight(.semibold)
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.green)
                                .foregroundColor(.white)
                                .cornerRadius(12)
                            }
                            .padding(.horizontal)
                        }
                        .padding(.vertical)
                        
                        // Stats
                        VStack(alignment: .leading, spacing: 8) {
                            Text("İstatistikler")
                                .font(.headline)
                                .padding(.horizontal)
                            
                            HStack(spacing: 16) {
                                StatCard(
                                    title: "Toplam İşlem",
                                    value: "\(transactionsViewModel.transactions.count)",
                                    icon: "list.bullet",
                                    color: .blue
                                )
                                
                                StatCard(
                                    title: "Kategori",
                                    value: "\(transactionsViewModel.categories.count)",
                                    icon: "folder",
                                    color: .orange
                                )
                            }
                            .padding(.horizontal)
                        }
                        .padding(.vertical)
                    }
                    .padding(.vertical)
                }
            }
            .navigationTitle("Dışa/İçe Aktar")
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
            }
            .sheet(isPresented: $showShareSheet) {
                if let fileURL = fileURL {
                    ShareSheet(items: [fileURL])
                }
            }
            .fileImporter(
                isPresented: $showImportPicker,
                allowedContentTypes: [.commaSeparatedText],
                allowsMultipleSelection: false
            ) { result in
                handleImport(result)
            }
            .alert("Bilgi", isPresented: $showAlert) {
                Button("Tamam", role: .cancel) {}
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    // MARK: - Export
    
    private func exportData() {
        switch exportType {
        case .csv:
            exportCSV()
        case .pdf:
            exportPDF()
        }
    }
    
    private func exportCSV() {
        guard let url = ExportManager.exportToCSV(transactions: transactionsViewModel.transactions) else {
            alertMessage = "CSV dışa aktarımı başarısız oldu"
            showAlert = true
            return
        }
        
        fileURL = url
        showShareSheet = true
    }
    
    private func exportPDF() {
        guard let url = ExportManager.exportToPDF(
            transactions: transactionsViewModel.transactions,
            totalIncome: transactionsViewModel.totalIncome,
            totalExpense: transactionsViewModel.totalExpense,
            balance: transactionsViewModel.currentBalance
        ) else {
            alertMessage = "PDF dışa aktarımı başarısız oldu"
            showAlert = true
            return
        }
        
        fileURL = url
        showShareSheet = true
    }
    
    // MARK: - Import
    
    private func handleImport(_ result: Result<[URL], Error>) {
        switch result {
        case .success(let urls):
            guard let url = urls.first else { return }
            
            let (success, failed) = ExportManager.importFromCSV(
                url: url,
                context: CoreDataStack.shared.container.viewContext,
                categories: transactionsViewModel.categories,
                accounts: transactionsViewModel.accounts
            )
            
            if success > 0 {
                transactionsViewModel.loadData()
                alertMessage = "✅ \(success) işlem başarıyla içe aktarıldı.\n❌ \(failed) işlem başarısız oldu."
            } else {
                alertMessage = "❌ İçe aktarım başarısız oldu. Dosya formatını kontrol edin."
            }
            showAlert = true
            
        case .failure(let error):
            alertMessage = "❌ Dosya seçimi başarısız: \(error.localizedDescription)"
            showAlert = true
        }
    }
}

// MARK: - Stat Card

struct StatCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(color)
            
            Text(value)
                .font(.title)
                .fontWeight(.bold)
            
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
    }
}

// MARK: - Share Sheet

struct ShareSheet: UIViewControllerRepresentable {
    let items: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: items, applicationActivities: nil)
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

#Preview {
    ExportView()
        .environmentObject(TransactionsViewModel())
}
