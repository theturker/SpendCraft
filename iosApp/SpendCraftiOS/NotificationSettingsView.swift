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
                            Text(NSLocalizedString("notification.status", comment: "Notification Status"))
                                .font(.subheadline)
                                .fontWeight(.medium)
                            Text(notificationManager.isAuthorized ? NSLocalizedString("notification.active", comment: "Active") : NSLocalizedString("notification.inactive", comment: "Inactive"))
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
                                Text(NSLocalizedString("notification.open.settings", comment: "Open Notifications in Settings"))
                                Spacer()
                                Image(systemName: "arrow.right")
                            }
                        }
                    }
                } header: {
                    Text(NSLocalizedString("notification.status", comment: "Status"))
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
                    Text(String(format: NSLocalizedString("notification.templates", comment: "Ready Notifications (%d)"), notificationManager.templates.count))
                } footer: {
                    Text(NSLocalizedString("notification.templates.description", comment: "You can activate ready notification scenarios and adjust their times."))
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
                                Label(NSLocalizedString("accounts.delete", comment: "Delete"), systemImage: "trash")
                            }
                        }
                    }
                    
                    Button {
                        showAddCustomNotification = true
                    } label: {
                        HStack {
                            Image(systemName: "plus.circle.fill")
                                .foregroundColor(.blue)
                            Text(NSLocalizedString("notification.add.custom", comment: "Add Custom Notification"))
                                .foregroundColor(.blue)
                        }
                    }
                } header: {
                    Text(String(format: NSLocalizedString("notification.custom", comment: "My Custom Notifications (%d)"), notificationManager.customNotifications.count))
                } footer: {
                    Text(NSLocalizedString("notification.custom.description", comment: "Create your own custom reminders."))
                }
            }
            .navigationTitle(NSLocalizedString("notification.settings.title", comment: "Notification Settings"))
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(NSLocalizedString("notification.close", comment: "Close")) {
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
        case NSLocalizedString("notification.morning", comment: "Morning"): return "sun.max.fill"
        case NSLocalizedString("notification.noon", comment: "Noon"): return "sun.min.fill"
        case NSLocalizedString("notification.evening", comment: "Evening"): return "moon.stars.fill"
        case NSLocalizedString("notification.weekly", comment: "Weekly"): return "calendar"
        case NSLocalizedString("notification.monthly", comment: "Monthly"): return "calendar.badge.clock"
        case NSLocalizedString("notification.motivation", comment: "Motivation"): return "star.fill"
        case NSLocalizedString("notification.special", comment: "Special"): return "gift.fill"
        default: return "bell.fill"
        }
    }
    
    private func categoryColor(for category: String) -> Color {
        switch category {
        case NSLocalizedString("notification.morning", comment: "Morning"): return .orange
        case NSLocalizedString("notification.noon", comment: "Noon"): return .yellow
        case NSLocalizedString("notification.evening", comment: "Evening"): return .indigo
        case NSLocalizedString("notification.weekly", comment: "Weekly"): return .blue
        case NSLocalizedString("notification.monthly", comment: "Monthly"): return .green
        case NSLocalizedString("notification.motivation", comment: "Motivation"): return .purple
        case NSLocalizedString("notification.special", comment: "Special"): return .pink
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
                            Text("• \(NSLocalizedString("notification.month.days", comment: "Month days")) \(monthDaysText(monthDays))")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        } else if let weekDays = template.daysOfWeek {
                            Text("• \(daysText(weekDays))")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        } else {
                            Text("• \(NSLocalizedString("notification.every.day", comment: "Every day"))")
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
        let dayNames = [
            NSLocalizedString("day.sun", comment: "Sun"),
            NSLocalizedString("day.mon", comment: "Mon"),
            NSLocalizedString("day.tue", comment: "Tue"),
            NSLocalizedString("day.wed", comment: "Wed"),
            NSLocalizedString("day.thu", comment: "Thu"),
            NSLocalizedString("day.fri", comment: "Fri"),
            NSLocalizedString("day.sat", comment: "Sat")
        ]
        return days.map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
    
    private func monthDaysText(_ days: [Int]) -> String {
        if days.count > 4 {
            return "\(days.first ?? 1)-\(days.last ?? 1). \(NSLocalizedString("notification.days", comment: "days"))"
        } else {
            return days.map { "\($0)." }.joined(separator: ", ") + " \(NSLocalizedString("notification.days", comment: "days"))"
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
                            Text("• \(NSLocalizedString("notification.every.day", comment: "Every day"))")
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
                
                Section(NSLocalizedString("notification.settings", comment: "Settings")) {
                    Toggle(NSLocalizedString("recurring.active", comment: "Active"), isOn: $isEnabled)
                    
                    HStack {
                        Text(NSLocalizedString("notification.hour", comment: "Hour"))
                        Spacer()
                        Picker(NSLocalizedString("notification.hour", comment: "Hour"), selection: $hour) {
                            ForEach(0..<24, id: \.self) { h in
                                Text(String(format: "%02d", h)).tag(h)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                        
                        Text(":")
                        
                        Picker(NSLocalizedString("notification.minute", comment: "Minute"), selection: $minute) {
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
                            Text(NSLocalizedString("notification.days.of.month", comment: "Which Days of the Month?"))
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
                                        Text(NSLocalizedString("notification.every.day", comment: "Every Day"))
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
                            Text(NSLocalizedString("notification.days.of.week", comment: "Days of Week"))
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
                            Text(NSLocalizedString("common.save", comment: "Save"))
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                }
            }
            .navigationTitle(NSLocalizedString("notification.edit", comment: "Edit Notification"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                        Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
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
        let dayNames = [
            NSLocalizedString("day.sun", comment: "Sunday"),
            NSLocalizedString("day.mon", comment: "Monday"),
            NSLocalizedString("day.tue", comment: "Tuesday"),
            NSLocalizedString("day.wed", comment: "Wednesday"),
            NSLocalizedString("day.thu", comment: "Thursday"),
            NSLocalizedString("day.fri", comment: "Friday"),
            NSLocalizedString("day.sat", comment: "Saturday")
        ]
        return days.map { dayNames[$0 - 1] }.joined(separator: ", ")
    }
    
    private func monthDaysText(_ days: [Int]) -> String {
        if days.count > 4 {
            return "\(days.first ?? 1)-\(days.last ?? 1). \(NSLocalizedString("notification.days", comment: "days"))"
        } else {
            return days.map { "\($0)." }.joined(separator: ", ") + " \(NSLocalizedString("notification.days", comment: "days"))"
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
                Section(NSLocalizedString("notification.content", comment: "Notification Content")) {
                    TextField(NSLocalizedString("notification.title", comment: "Title"), text: $title)
                    TextField(NSLocalizedString("notification.message", comment: "Message"), text: $messageBody, axis: .vertical)
                        .lineLimit(3...5)
                }
                
                Section(NSLocalizedString("notification.time", comment: "Time")) {
                    HStack {
                        Text(NSLocalizedString("notification.hour", comment: "Hour"))
                        Spacer()
                        Picker(NSLocalizedString("notification.hour", comment: "Hour"), selection: $hour) {
                            ForEach(0..<24, id: \.self) { h in
                                Text(String(format: "%02d", h)).tag(h)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                        
                        Text(":")
                        
                        Picker(NSLocalizedString("notification.minute", comment: "Minute"), selection: $minute) {
                            ForEach(0..<60, id: \.self) { m in
                                Text(String(format: "%02d", m)).tag(m)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                    }
                }
                
                Section(NSLocalizedString("notification.recurrence", comment: "Recurrence")) {
                    if selectedDays.isEmpty {
                        Text(NSLocalizedString("notification.every.day", comment: "Every Day"))
                            .foregroundColor(.secondary)
                    } else {
                        Text(selectedDaysText())
                            .foregroundColor(.secondary)
                    }
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        ForEach([(1, NSLocalizedString("day.sun", comment: "Sun")), (2, NSLocalizedString("day.mon", comment: "Mon")), (3, NSLocalizedString("day.tue", comment: "Tue")), (4, NSLocalizedString("day.wed", comment: "Wed")), (5, NSLocalizedString("day.thu", comment: "Thu")), (6, NSLocalizedString("day.fri", comment: "Fri")), (7, NSLocalizedString("day.sat", comment: "Sat"))], id: \.0) { day in
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
                            Text(NSLocalizedString("notification.create", comment: "Create"))
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                    .disabled(title.isEmpty || messageBody.isEmpty)
                }
            }
            .navigationTitle(NSLocalizedString("notification.custom", comment: "Custom Notification"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                        Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
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
        let dayNames = [
            NSLocalizedString("day.sun", comment: "Sunday"),
            NSLocalizedString("day.mon", comment: "Monday"),
            NSLocalizedString("day.tue", comment: "Tuesday"),
            NSLocalizedString("day.wed", comment: "Wednesday"),
            NSLocalizedString("day.thu", comment: "Thursday"),
            NSLocalizedString("day.fri", comment: "Friday"),
            NSLocalizedString("day.sat", comment: "Saturday")
        ]
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
                Section(NSLocalizedString("notification.content", comment: "Notification Content")) {
                    TextField(NSLocalizedString("notification.title", comment: "Title"), text: $title)
                    TextField(NSLocalizedString("notification.message", comment: "Message"), text: $messageBody, axis: .vertical)
                        .lineLimit(3...5)
                }
                
                Section(NSLocalizedString("notification.settings", comment: "Settings")) {
                    Toggle(NSLocalizedString("recurring.active", comment: "Active"), isOn: $isEnabled)
                    
                    HStack {
                        Text(NSLocalizedString("notification.hour", comment: "Hour"))
                        Spacer()
                        Picker(NSLocalizedString("notification.hour", comment: "Hour"), selection: $hour) {
                            ForEach(0..<24, id: \.self) { h in
                                Text(String(format: "%02d", h)).tag(h)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                        
                        Text(":")
                        
                        Picker(NSLocalizedString("notification.minute", comment: "Minute"), selection: $minute) {
                            ForEach(0..<60, id: \.self) { m in
                                Text(String(format: "%02d", m)).tag(m)
                            }
                        }
                        .pickerStyle(.wheel)
                        .frame(width: 70, height: 100)
                    }
                }
                
                Section(NSLocalizedString("notification.recurrence", comment: "Recurrence")) {
                    if selectedDays.isEmpty {
                        Text(NSLocalizedString("notification.every.day", comment: "Every Day"))
                            .foregroundColor(.secondary)
                    } else {
                        Text(selectedDaysText())
                            .foregroundColor(.secondary)
                    }
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        ForEach([(1, NSLocalizedString("day.sun", comment: "Sun")), (2, NSLocalizedString("day.mon", comment: "Mon")), (3, NSLocalizedString("day.tue", comment: "Tue")), (4, NSLocalizedString("day.wed", comment: "Wed")), (5, NSLocalizedString("day.thu", comment: "Thu")), (6, NSLocalizedString("day.fri", comment: "Fri")), (7, NSLocalizedString("day.sat", comment: "Sat"))], id: \.0) { day in
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
                            Text(NSLocalizedString("common.save", comment: "Save"))
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                    .disabled(title.isEmpty || messageBody.isEmpty)
                }
            }
            .navigationTitle(NSLocalizedString("notification.edit", comment: "Edit Notification"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                        Button(NSLocalizedString("common.cancel", comment: "Cancel")) {
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
        let dayNames = [
            NSLocalizedString("day.sun", comment: "Sunday"),
            NSLocalizedString("day.mon", comment: "Monday"),
            NSLocalizedString("day.tue", comment: "Tuesday"),
            NSLocalizedString("day.wed", comment: "Wednesday"),
            NSLocalizedString("day.thu", comment: "Thursday"),
            NSLocalizedString("day.fri", comment: "Friday"),
            NSLocalizedString("day.sat", comment: "Saturday")
        ]
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
