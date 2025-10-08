//
//  NotificationsView.swift
//  SpendCraftiOS
//
//  Bildirimler Ekranı
//

import SwiftUI

struct NotificationsView: View {
    @StateObject private var viewModel = NotificationsViewModel()
    @Environment(\.dismiss) private var dismiss
    
    var unreadNotifications: [NotificationItem] {
        viewModel.notifications.filter { !$0.isRead }
    }
    
    var readNotifications: [NotificationItem] {
        viewModel.notifications.filter { $0.isRead }
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(.systemGroupedBackground)
                    .ignoresSafeArea()
                
                if viewModel.notifications.isEmpty {
                    emptyState
                } else {
                    notificationsList
                }
            }
            .navigationTitle("Bildirimler")
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
                    if !unreadNotifications.isEmpty {
                        Button {
                            withAnimation {
                                viewModel.markAllAsRead()
                            }
                        } label: {
                            Image(systemName: "checkmark.circle")
                            Text("Tümü")
                        }
                    }
                }
            }
        }
    }
    
    // MARK: - Empty State
    
    private var emptyState: some View {
        VStack(spacing: 20) {
            Image(systemName: "bell.slash")
                .font(.system(size: 70))
                .foregroundColor(.secondary)
            
            Text("Bildirim Yok")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text("Henüz hiç bildiriminiz yok")
                .font(.body)
                .foregroundColor(.secondary)
        }
    }
    
    // MARK: - Notifications List
    
    private var notificationsList: some View {
        List {
            // Unread notifications
            if !unreadNotifications.isEmpty {
                Section {
                    ForEach(unreadNotifications) { notification in
                        NotificationRow(
                            notification: notification,
                            onMarkAsRead: {
                                withAnimation {
                                    viewModel.markAsRead(notification.id)
                                }
                            },
                            onDelete: {
                                withAnimation {
                                    viewModel.deleteNotification(notification.id)
                                }
                            }
                        )
                    }
                } header: {
                    HStack {
                        Text("Okunmamış")
                        Spacer()
                        Text("\(unreadNotifications.count)")
                            .font(.caption)
                            .foregroundColor(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 2)
                            .background(Color.blue)
                            .cornerRadius(10)
                    }
                }
            }
            
            // Read notifications
            if !readNotifications.isEmpty {
                Section("Okunmuş") {
                    ForEach(readNotifications) { notification in
                        NotificationRow(
                            notification: notification,
                            onMarkAsRead: {},
                            onDelete: {
                                withAnimation {
                                    viewModel.deleteNotification(notification.id)
                                }
                            }
                        )
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
    }
}

// MARK: - Notification Row

struct NotificationRow: View {
    let notification: NotificationItem
    let onMarkAsRead: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            // Icon
            ZStack {
                Circle()
                    .fill(iconColor.opacity(0.1))
                    .frame(width: 40, height: 40)
                
                Image(systemName: notification.type.icon)
                    .foregroundColor(iconColor)
            }
            
            // Content
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(notification.title)
                        .font(.headline)
                        .fontWeight(notification.isRead ? .regular : .semibold)
                    
                    Spacer()
                    
                    if !notification.isRead {
                        Circle()
                            .fill(Color.blue)
                            .frame(width: 8, height: 8)
                    }
                }
                
                Text(notification.message)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(3)
                
                Text(notification.formattedDate)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
            Button(role: .destructive) {
                onDelete()
            } label: {
                Label("Sil", systemImage: "trash")
            }
            
            if !notification.isRead {
                Button {
                    onMarkAsRead()
                } label: {
                    Label("Okundu", systemImage: "checkmark")
                }
                .tint(.blue)
            }
        }
    }
    
    private var iconColor: Color {
        switch notification.type {
        case .budgetAlert:
            return .red
        case .spendingReminder:
            return .blue
        case .achievement:
            return .yellow
        case .system:
            return .gray
        }
    }
}

// Badge modifier for tab bar
extension View {
    func badge(_ count: Int) -> some View {
        self.overlay(
            Group {
                if count > 0 {
                    Text("\(count)")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(5)
                        .background(Color.red)
                        .clipShape(Circle())
                        .offset(x: 10, y: -10)
                }
            },
            alignment: .topTrailing
        )
    }
}

#Preview {
    NotificationsView()
}
