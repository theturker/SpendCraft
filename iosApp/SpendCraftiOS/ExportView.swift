//
//  ExportView.swift
//  SpendCraftiOS
//
//  Dışa/İçe Aktarım Ekranı
//

import SwiftUI
import UniformTypeIdentifiers
import GoogleMobileAds

struct ExportView: View {
    @EnvironmentObject var transactionsViewModel: TransactionsViewModel
    @StateObject private var adsManager = AdsManager.shared
    @Environment(\.dismiss) private var dismiss
    
    @State private var showShareSheet = false
    @State private var fileURL: URL?
    @State private var exportType: ExportType = .json
    @State private var showImportPicker = false
    @State private var showAlert = false
    @State private var alertMessage = ""
    @State private var replaceExistingData = false
    
    enum ExportType: String, CaseIterable {
        case json = "export.type.json"
        case csv = "export.type.csv"
        case pdf = "export.type.pdf"
        
        var icon: String {
            switch self {
            case .json: return "doc.badge.gearshape"
            case .csv: return "doc.text"
            case .pdf: return "doc.richtext"
            }
        }
        
        var description: String {
            switch self {
            case .json: return NSLocalizedString("export.type.json.description", comment: "JSON description")
            case .csv: return NSLocalizedString("export.type.csv.description", comment: "CSV description")
            case .pdf: return NSLocalizedString("export.type.pdf.description", comment: "PDF description")
            }
        }
        
        var localizedName: String {
            return NSLocalizedString(self.rawValue, comment: "Export type")
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
                                Text(NSLocalizedString("export.title", comment: "Export"))
                                    .font(.title2)
                                    .fontWeight(.bold)
                            }
                            .padding(.horizontal)
                            
                            Text(NSLocalizedString("export.description", comment: "Export description"))
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
                                            Text(type.localizedName)
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
                                    Text(NSLocalizedString("export.button", comment: "Export Button"))
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
                                Text(NSLocalizedString("import.title", comment: "Import"))
                                    .font(.title2)
                                    .fontWeight(.bold)
                            }
                            .padding(.horizontal)
                            
                            Text(NSLocalizedString("import.description", comment: "Import description"))
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .padding(.horizontal)
                            
                            VStack(alignment: .leading, spacing: 12) {
                                Text(NSLocalizedString("import.important", comment: "Important"))
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(.orange)
                                
                                Text(NSLocalizedString("import.warning", comment: "Import warning"))
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                
                                Toggle(NSLocalizedString("import.replace", comment: "Replace data"), isOn: $replaceExistingData)
                                    .font(.caption)
                                    .tint(.red)
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
                                    Text(NSLocalizedString("import.select.file", comment: "Select File"))
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
                            Text(NSLocalizedString("export.stats", comment: "Statistics"))
                                .font(.headline)
                                .padding(.horizontal)
                            
                            HStack(spacing: 16) {
                                StatCard(
                                    title: NSLocalizedString("export.stats.transactions", comment: "Total Transactions"),
                                    value: "\(transactionsViewModel.transactions.count)",
                                    icon: "list.bullet",
                                    color: .blue
                                )
                                
                                StatCard(
                                    title: NSLocalizedString("export.stats.categories", comment: "Total Categories"),
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
            .navigationTitle(NSLocalizedString("export.title", comment: "Export/Import"))
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
                allowedContentTypes: [.json, .commaSeparatedText],
                allowsMultipleSelection: false
            ) { result in
                handleImport(result)
            }
            .alert(NSLocalizedString("common.info", comment: "Info"), isPresented: $showAlert) {
                Button(NSLocalizedString("common.ok", comment: "OK"), role: .cancel) {}
            } message: {
                Text(alertMessage)
            }
            .onAppear {
                // Ekran geçişinin tamamen bitmesini bekle (daha uzun süre)
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    showInterstitialAd()
                }
            }
        }
    }
    
    // MARK: - Export
    
    private func exportData() {
        switch exportType {
        case .json:
            exportJSON()
        case .csv:
            exportCSV()
        case .pdf:
            exportPDF()
        }
    }
    
    private func exportJSON() {
        guard let url = ExportManager.exportToJSON(
            transactions: transactionsViewModel.transactions,
            categories: transactionsViewModel.categories,
            accounts: transactionsViewModel.accounts
        ) else {
            alertMessage = "JSON yedekleme başarısız oldu"
            showAlert = true
            return
        }
        
        fileURL = url
        showShareSheet = true
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
            
            // Start accessing security-scoped resource
            let didStartAccessing = url.startAccessingSecurityScopedResource()
            defer {
                if didStartAccessing {
                    url.stopAccessingSecurityScopedResource()
                }
            }
            
            // Copy file to temporary directory to avoid permission issues
            let fileManager = FileManager.default
            let tempDirectory = fileManager.temporaryDirectory
            let tempFileName = UUID().uuidString + "." + url.pathExtension
            let tempFileURL = tempDirectory.appendingPathComponent(tempFileName)
            
            do {
                print("📂 Original file: \(url.path)")
                print("📂 Is iCloud file: \(url.path.contains("com~apple~CloudDocs"))")
                
                // Remove existing temp file if any
                if fileManager.fileExists(atPath: tempFileURL.path) {
                    try fileManager.removeItem(at: tempFileURL)
                    print("🗑️ Removed existing temp file")
                }
                
                // Use NSFileCoordinator for iCloud files (required by iOS)
                let coordinator = NSFileCoordinator()
                var coordinatorError: NSError?
                var fileData: Data?
                
                coordinator.coordinate(readingItemAt: url, options: .withoutChanges, error: &coordinatorError) { coordinatedURL in
                    print("📂 Coordinated URL: \(coordinatedURL.path)")
                    
                    do {
                        // Read file data
                        fileData = try Data(contentsOf: coordinatedURL)
                        print("✅ File data loaded: \(fileData?.count ?? 0) bytes")
                    } catch {
                        print("❌ Error reading coordinated file: \(error)")
                    }
                }
                
                // Check for coordination error
                if let error = coordinatorError {
                    throw error
                }
                
                // Check if we got data
                guard let data = fileData else {
                    throw NSError(domain: "com.spendcraft", code: 2, userInfo: [NSLocalizedDescriptionKey: "Dosya okunamadı"])
                }
                
                // Write to temp location
                try data.write(to: tempFileURL)
                
                print("✅ File written to temp: \(tempFileURL.path)")
                print("✅ Temp file size: \(data.count) bytes")
                
                if let attributes = try? fileManager.attributesOfItem(atPath: tempFileURL.path) {
                    print("✅ Temp file verified: \(attributes[.size] ?? 0) bytes")
                }
                
                // Check file type
                let fileExtension = tempFileURL.pathExtension.lowercased()
                
                if fileExtension == "json" {
                    // Import JSON backup
                    let (_, _, message) = ExportManager.importFromJSON(
                        url: tempFileURL,
                        context: CoreDataStack.shared.container.viewContext,
                        replaceExisting: replaceExistingData
                    )
                    
                    transactionsViewModel.loadData()
                    alertMessage = message
                    showAlert = true
                    
                } else if fileExtension == "csv" {
                    // Import CSV
                    let (success, failed) = ExportManager.importFromCSV(
                        url: tempFileURL,
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
                } else {
                    alertMessage = "❌ Desteklenmeyen dosya formatı. Lütfen JSON veya CSV dosyası seçin."
                    showAlert = true
                }
                
                // Clean up temp file
                try? fileManager.removeItem(at: tempFileURL)
                
            } catch {
                print("❌ File copy error: \(error.localizedDescription)")
                alertMessage = "❌ Dosya işleme hatası: \(error.localizedDescription)"
                showAlert = true
            }
            
        case .failure(let error):
            alertMessage = "❌ Dosya seçimi başarısız: \(error.localizedDescription)"
            showAlert = true
        }
    }
    
    // MARK: - Interstitial Ad
    
    private func showInterstitialAd() {
        print("🎯 Export View - Attempting to show interstitial ad...")
        
        // Premium kullanıcılar için reklam gösterme
        guard adsManager.shouldShowAds() else {
            print("⚠️ Export View - User is premium, skipping ad")
            return
        }
        
        // Farklı view controller bulma yöntemleri dene
        var targetViewController: UIViewController?
        
        // Yöntem 1: Window scene'den root view controller
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {
            targetViewController = rootViewController
            print("✅ Export View - Found root view controller via window scene")
        }
        
        // Yöntem 2: Key window'dan root view controller
        if targetViewController == nil,
           let keyWindow = UIApplication.shared.windows.first(where: { $0.isKeyWindow }),
           let rootViewController = keyWindow.rootViewController {
            targetViewController = rootViewController
            print("✅ Export View - Found root view controller via key window")
        }
        
        guard let viewController = targetViewController else {
            print("❌ Export View - Could not find any root view controller")
            return
        }
        
        // UI transition'ın tamamlanmasını bekle
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            print("🚀 Export View - Showing interstitial ad now...")
            self.adsManager.showInterstitialAd(from: viewController) {
                // Reklam kapandıktan sonra yeni reklam yükle
                print("✅ Export View - Interstitial ad closed, loading new ad")
            }
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
