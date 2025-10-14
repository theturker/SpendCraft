//
//  NotificationSettingsView.swift
//  SpendCraftiOS
//
//  Gelişmiş Bildirim Ayarları
//

import SwiftUI

struct NotificationSettingsView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var notificationManager = NotificationManager.shared
    @State private var showAddCustomNotification = false
    @State private var selectedTemplate: NotificationTemplate?
    @State private var selectedCustom: CustomNotification?
    
    var body: some View {
        NavigationStack {
            List {
                // Authorization Status
                Section {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Bildirim Durumu")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            Text(notificationManager.isAuthorized ? "Aktif" : "Kapalı")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        if notificationManager.isAuthorized {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                                .font(.title2)
                        } else {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.red)
                                .font(.title2)
                        }
                    }
                    
                    if !notificationManager.isAuthorized {
                        Button {
                            openSettings()
                        } label: {
                            HStack {
                                Image(systemName: "gear")
                                Text("Ayarlarda Bildirimleri Aç")
                                Spacer()
                                Image(systemName: "arrow.right")
                            }
                        }
                    }
                } header: {
                    Text("Durum")
                }
                
                // Template Notifications
                Section {
                    ForEach(groupedTemplates().keys.sorted(), id: \.self) { category in
                        DisclosureGroup {
                            ForEach(groupedTemplates()[category] ?? []) { template in
                                NotificationTemplateRow(template: template) {
                                    selectedTemplate = template
                                }
                            }
                        } label: {
                            HStack {
                                Image(systemName: categoryIcon(for: category))
                                    .foregroundColor(categoryColor(for: category))
                                Text(category)
                                    .fontWeight(.medium)
                                Spacer()
                                Text("\(groupedTemplates()[category]?.filter { $0.isEnabled }.count ?? 0)/\(groupedTemplates()[category]?.count ?? 0)")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                } header: {
                    Text("Hazır Bildirimler (\(notificationManager.templates.count))")
                } footer: {
                    Text("Hazır bildirim senaryolarını aktifleştirip saatlerini düzenleyebilirsiniz.")
                }
                
                // Custom Notifications
                Section {
                    ForEach(notificationManager.customNotifications) { notification in
                        CustomNotificationRow(notification: notification) {
                            selectedCustom = notification
                        }
                        .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                            Button(role: .destructive) {
                                notificationManager.deleteCustomNotification(notification)
                            } label: {
                                Label("Sil", systemImage: "trash")
                            }
                        }
                    }
                    
                    Button {
                        showAddCustomNotification = true
                    } label: {
                        HStack {
                            Image(systemName: "plus.circle.fill")
                                .foregroundColor(.blue)
                            Text("Özel Bildirim Ekle")
                                .foregroundColor(.blue)
                        }
                    }
                } header: {
                    Text("Özel Bildirimlerim (\(notificationManager.customNotifications.count))")
                } footer: {
                    Text("Kendi özel hatırlatmalarınızı oluşturun.")
                }
            }
            .navigationTitle("Bildirim Ayarları")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Kapat") {
                        dismiss()
                    }
                }
            }
            .sheet(item: $selectedTemplate) { template in
                EditTemplateView(template: template)
            }
            .sheet(item: $selectedCustom) { notification in
                EditCustomNotificationView(notification: notification)
            }
            .sheet(isPresented: $showAddCustomNotification) {
                AddCustomNotificationView()
            }
        }
    }
    
    private func groupedTemplates() -> [String: [NotificationTemplate]] {
        Dictionary(grouping: notificationManager.templates) { $0.category }
    }
    
    private func categoryIcon(for category: String) -> String {
        switch category {
        case "Sabah": return "sun.max.fill"
        case "Öğlen": return "sun.min.fill"
        case "Akşam": return "moon.stars.fill"
        case "Haftalık": return "calendar"
        case "Aylık": return "calendar.badge.clock"
        case "Motivasyon": return "star.fill"
        case "Özel": return "gift.fill"
        default: return "bell.fill"
        }
    }
    
    private func categoryColor(for category: String) -> Color {
        switch category {
        case "Sabah": return .orange
        case "Öğlen": return .yellow
        case "Akşam": return .indigo
        case "Haftalık": return .blue
        case "Aylık": return .green
        case "Motivasyon": return .purple
        case "Özel": return .pink
        default: return .gray
        }
    }
    
    private func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
}

// MARK: - Template Row

struct NotificationTemplateRow: View {
    let template: NotificationTemplate
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                Image(systemName: template.icon)
                    .foregroundColor(template.isEnabled ? .blue : .gray)
                    .frame(width: 32)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(template.title)
                        .font(.subheadline)
                        .fontWeight(.medium)
                        .foregroundColor(.primary)
                    
                    HStack {
                        Text("\(String(format: "%02d:%02d", template.hour, template.minute))")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundColor(.blue)
                        
                        if let monthDays = template.daysOfMonth {
                            Text("• Ayın \(monthDaysText(monthDays))")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        } else if let weekDays = template.daysOfWeek {
                            Text("• \(daysText(weekDays))")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        } else {
                            Text("• Her gün")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                Spacer()
                
                Image(systemName: template.isEnabled ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(template.isEnabled ? .green : .gray)
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(.plain)
    }
    
    private func daysText(_ days: [Int]) -> String {
        let dayNames = ["Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt"]
        return days.map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
    
    private func monthDaysText(_ days: [Int]) -> String {
        if days.count > 4 {
            return "\(days.first ?? 1)-\(days.last ?? 1). günleri"
        } else {
            return days.map { "\($0)." }.joined(separator: ", ") + " günleri"
        }
    }
}

// MARK: - Custom Notification Row

struct CustomNotificationRow: View {
    let notification: CustomNotification
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                Image(systemName: "bell.badge.fill")
                    .foregroundColor(notification.isEnabled ? .purple : .gray)
                    .frame(width: 32)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(notification.title)
                        .font(.subheadline)
                        .fontWeight(.medium)
                        .foregroundColor(.primary)
                    
                    HStack {
                        Text("\(String(format: "%02d:%02d", notification.hour, notification.minute))")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundColor(.purple)
                        
                        if let days = notification.daysOfWeek {
                            Text("• \(daysText(days))")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        } else {
                            Text("• Her gün")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                Spacer()
                
                Image(systemName: notification.isEnabled ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(notification.isEnabled ? .green : .gray)
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(.plain)
    }
    
    private func daysText(_ days: [Int]) -> String {
        let dayNames = ["Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt"]
        return days.map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
}

// MARK: - Edit Template View

struct EditTemplateView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var notificationManager = NotificationManager.shared
    
    let template: NotificationTemplate
    @State private var hour: Int
    @State private var minute: Int
    @State private var isEnabled: Bool
    @State private var selectedMonthDays: Set<Int>
    
    init(template: NotificationTemplate) {
        self.template = template
        _hour = State(initialValue: template.hour)
        _minute = State(initialValue: template.minute)
        _isEnabled = State(initialValue: template.isEnabled)
        _selectedMonthDays = State(initialValue: Set(template.daysOfMonth ?? []))
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section {
                    HStack {
                        Image(systemName: template.icon)
                            .foregroundColor(.blue)
                        Text(template.title)
                            .fontWeight(.semibold)
                    }
                    
                    Text(template.body)
                        .font(.callout)
                        .foregroundColor(.secondary)
                }
                
                Section("Ayarlar") {
                    Toggle("Aktif", isOn: $isEnabled)
                    
                    HStack {
                        Text("Saat")
                        Spacer()
                        Picker("Saat", selection: $hour) {
                            ForEach(0..<24, id: \.self) { h in
                                Text(String(format: "%02d", h)).tag(h)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                        
                        Text(":")
                        
                        Picker("Dakika", selection: $minute) {
                            ForEach(0..<60, id: \.self) { m in
                                Text(String(format: "%02d", m)).tag(m)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                    }
                    
                    // Aylık bildirimler için gün seçimi
                    if template.daysOfMonth != nil {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Ayın Hangi Günlerinde?")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            
                            // Hızlı seçimler
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 8) {
                                    QuickSelectButton(title: "1-5", days: Set(1...5), selection: $selectedMonthDays)
                                    QuickSelectButton(title: "5-15", days: Set(5...15), selection: $selectedMonthDays)
                                    QuickSelectButton(title: "15-20", days: Set(15...20), selection: $selectedMonthDays)
                                    QuickSelectButton(title: "20-31", days: Set(20...31), selection: $selectedMonthDays)
                                    QuickSelectButton(title: "25-30", days: Set(25...30), selection: $selectedMonthDays)
                                    Button {
                                        selectedMonthDays = Set(1...31)
                                    } label: {
                                        Text("Her Gün")
                                            .font(.caption)
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 6)
                                            .background(selectedMonthDays.count == 31 ? Color.blue : Color.gray.opacity(0.2))
                                            .foregroundColor(selectedMonthDays.count == 31 ? .white : .primary)
                                            .cornerRadius(8)
                                    }
                                }
                            }
                            
                            // Gün seçiciler
                            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 7), spacing: 8) {
                                ForEach(1...31, id: \.self) { day in
                                    Button {
                                        if selectedMonthDays.contains(day) {
                                            selectedMonthDays.remove(day)
                                        } else {
                                            selectedMonthDays.insert(day)
                                        }
                                    } label: {
                                        Text("\(day)")
                                            .font(.caption)
                                            .fontWeight(.semibold)
                                            .frame(width: 36, height: 36)
                                            .background(selectedMonthDays.contains(day) ? Color.blue : Color.gray.opacity(0.2))
                                            .foregroundColor(selectedMonthDays.contains(day) ? .white : .primary)
                                            .cornerRadius(8)
                                    }
                                }
                            }
                        }
                        .padding(.vertical, 4)
                    } else if let weekDays = template.daysOfWeek {
                        HStack {
                            Text("Haftanın Günleri")
                            Spacer()
                            Text(daysText(weekDays))
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                Section {
                    Button {
                        saveChanges()
                    } label: {
                        Text("Kaydet")
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                }
            }
            .navigationTitle("Bildirimi Düzenle")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func saveChanges() {
        var updatedTemplate = template
        updatedTemplate.hour = hour
        updatedTemplate.minute = minute
        updatedTemplate.isEnabled = isEnabled
        
        // Eğer template aylık ise, günleri güncelle
        if template.daysOfMonth != nil {
            updatedTemplate.daysOfMonth = selectedMonthDays.isEmpty ? nil : Array(selectedMonthDays).sorted()
        }
        
        notificationManager.updateTemplate(updatedTemplate)
        dismiss()
    }
    
    private func daysText(_ days: [Int]) -> String {
        let dayNames = ["Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"]
        return days.map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
    
    private func monthDaysText(_ days: [Int]) -> String {
        if days.count > 4 {
            return "\(days.first ?? 1)-\(days.last ?? 1). günleri"
        } else {
            return days.map { "\($0)." }.joined(separator: ", ") + " günleri"
        }
    }
}

// MARK: - Add/Edit Custom Notification

struct AddCustomNotificationView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var notificationManager = NotificationManager.shared
    
    @State private var title = ""
    @State private var messageBody = ""
    @State private var hour = 9
    @State private var minute = 0
    @State private var selectedDays: Set<Int> = []
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Bildirim İçeriği") {
                    TextField("Başlık", text: $title)
                    TextField("Mesaj", text: $messageBody, axis: .vertical)
                        .lineLimit(3...5)
                }
                
                Section("Zaman") {
                    HStack {
                        Text("Saat")
                        Spacer()
                        Picker("Saat", selection: $hour) {
                            ForEach(0..<24, id: \.self) { h in
                                Text(String(format: "%02d", h)).tag(h)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                        
                        Text(":")
                        
                        Picker("Dakika", selection: $minute) {
                            ForEach(0..<60, id: \.self) { m in
                                Text(String(format: "%02d", m)).tag(m)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                    }
                }
                
                Section("Tekrarlama") {
                    if selectedDays.isEmpty {
                        Text("Her gün")
                            .foregroundColor(.secondary)
                    } else {
                        Text(selectedDaysText())
                            .foregroundColor(.secondary)
                    }
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        ForEach([(1, "Paz"), (2, "Pzt"), (3, "Sal"), (4, "Çar"), (5, "Per"), (6, "Cum"), (7, "Cmt")], id: \.0) { day in
                            Button {
                                if selectedDays.contains(day.0) {
                                    selectedDays.remove(day.0)
                                } else {
                                    selectedDays.insert(day.0)
                                }
                            } label: {
                                Text(day.1)
                                    .font(.caption)
                                    .fontWeight(.semibold)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 8)
                                    .background(selectedDays.contains(day.0) ? Color.blue : Color.gray.opacity(0.2))
                                    .foregroundColor(selectedDays.contains(day.0) ? .white : .primary)
                                    .cornerRadius(8)
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Section {
                    Button {
                        saveNotification()
                    } label: {
                        Text("Oluştur")
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                    .disabled(title.isEmpty || messageBody.isEmpty)
                }
            }
            .navigationTitle("Özel Bildirim")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func saveNotification() {
        let notification = CustomNotification(
            title: title,
            body: messageBody,
            hour: hour,
            minute: minute,
            isEnabled: true,
            daysOfWeek: selectedDays.isEmpty ? nil : Array(selectedDays).sorted()
        )
        
        notificationManager.addCustomNotification(notification)
        dismiss()
    }
    
    private func selectedDaysText() -> String {
        let dayNames = ["Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"]
        return Array(selectedDays).sorted().map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
}

struct EditCustomNotificationView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var notificationManager = NotificationManager.shared
    
    let notification: CustomNotification
    @State private var title: String
    @State private var messageBody: String
    @State private var hour: Int
    @State private var minute: Int
    @State private var isEnabled: Bool
    @State private var selectedDays: Set<Int>
    
    init(notification: CustomNotification) {
        self.notification = notification
        _title = State(initialValue: notification.title)
        _messageBody = State(initialValue: notification.body)
        _hour = State(initialValue: notification.hour)
        _minute = State(initialValue: notification.minute)
        _isEnabled = State(initialValue: notification.isEnabled)
        _selectedDays = State(initialValue: Set(notification.daysOfWeek ?? []))
    }
    
    var body: some View {
        NavigationStack {
            Form {
                Section("Bildirim İçeriği") {
                    TextField("Başlık", text: $title)
                    TextField("Mesaj", text: $messageBody, axis: .vertical)
                        .lineLimit(3...5)
                }
                
                Section("Ayarlar") {
                    Toggle("Aktif", isOn: $isEnabled)
                    
                    HStack {
                        Text("Saat")
                        Spacer()
                        Picker("Saat", selection: $hour) {
                            ForEach(0..<24, id: \.self) { h in
                                Text(String(format: "%02d", h)).tag(h)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                        
                        Text(":")
                        
                        Picker("Dakika", selection: $minute) {
                            ForEach(0..<60, id: \.self) { m in
                                Text(String(format: "%02d", m)).tag(m)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                    }
                }
                
                Section("Tekrarlama") {
                    if selectedDays.isEmpty {
                        Text("Her gün")
                            .foregroundColor(.secondary)
                    } else {
                        Text(selectedDaysText())
                            .foregroundColor(.secondary)
                    }
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        ForEach([(1, "Paz"), (2, "Pzt"), (3, "Sal"), (4, "Çar"), (5, "Per"), (6, "Cum"), (7, "Cmt")], id: \.0) { day in
                            Button {
                                if selectedDays.contains(day.0) {
                                    selectedDays.remove(day.0)
                                } else {
                                    selectedDays.insert(day.0)
                                }
                            } label: {
                                Text(day.1)
                                    .font(.caption)
                                    .fontWeight(.semibold)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 8)
                                    .background(selectedDays.contains(day.0) ? Color.blue : Color.gray.opacity(0.2))
                                    .foregroundColor(selectedDays.contains(day.0) ? .white : .primary)
                                    .cornerRadius(8)
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Section {
                    Button {
                        saveChanges()
                    } label: {
                        Text("Kaydet")
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                    .disabled(title.isEmpty || messageBody.isEmpty)
                }
            }
            .navigationTitle("Bildirimi Düzenle")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("İptal") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func saveChanges() {
        var updatedNotification = notification
        updatedNotification.title = title
        updatedNotification.body = messageBody
        updatedNotification.hour = hour
        updatedNotification.minute = minute
        updatedNotification.isEnabled = isEnabled
        updatedNotification.daysOfWeek = selectedDays.isEmpty ? nil : Array(selectedDays).sorted()
        
        notificationManager.updateCustomNotification(updatedNotification)
        dismiss()
    }
    
    private func selectedDaysText() -> String {
        let dayNames = ["Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"]
        return Array(selectedDays).sorted().map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
}

// MARK: - Helper Components

struct QuickSelectButton: View {
    let title: String
    let days: Set<Int>
    @Binding var selection: Set<Int>
    
    var body: some View {
        Button {
            selection = days
        } label: {
            Text(title)
                .font(.caption)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(selection == days ? Color.blue : Color.gray.opacity(0.2))
                .foregroundColor(selection == days ? .white : .primary)
                .cornerRadius(8)
        }
    }
}
