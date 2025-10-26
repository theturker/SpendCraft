//
//  UserProfilingView.swift
//  SpendCraftiOS
//
//  AI için kullanıcı profilleme anketi
//

import SwiftUI

struct UserProfilingView: View {
    @Environment(\.dismiss) var dismiss
    @AppStorage("userProfilingCompleted") private var isCompleted = false
    @State private var currentPage = 0
    @State private var answers: [String: [String]] = [:]
    
    let questions: [(id: String, question: String, options: [String])] = [
        ("income_frequency", "Geliriniz ne sıklıkta oluyor?", ["Haftalık", "2 Haftada bir", "Aylık", "Düzensiz"]),
        ("spending_habit", "Harcama alışkanlığınız nasıl?", ["Planlı harcıyorum", "Anlık kararlar alırım", "Karışık", "İhtiyaç olunca harcıyorum"]),
        ("savings_goal", "Tasarruf hedefiniz var mı?", ["Evet, belirli bir hedefim var", "Bazen tasarruf yaparım", "Hayır, henüz başlamadım", "Tasarruf yapmakta zorlanıyorum"]),
        ("biggest_expense", "En çok neye harcama yapıyorsunuz?", ["Yemek & Market", "Kira & Faturalar", "Eğlence & Sosyal", "Ulaşım"]),
        ("budget_management", "Bütçe yönetiminizi nasıl değerlendirirsiniz?", ["Çok iyi", "İyi", "Orta", "Geliştirilmeli"]),
        ("financial_goal", "Ana finansal hedefiniz nedir?", ["Borç ödemek", "Tasarruf yapmak", "Yatırım yapmak", "Gelir-gider dengesini sağlamak"]),
        ("debt_status", "Borç durumunuz nasıl?", ["Borcum yok", "Az borcum var", "Orta seviye borcum var", "Yüksek borcum var"])
    ]
    
    var progress: Double {
        Double(currentPage + 1) / Double(questions.count)
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                // Background gradient
                LinearGradient(
                    colors: [Color.purple.opacity(0.1), Color.blue.opacity(0.1)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                
                if currentPage < questions.count {
                    // Question View
                    VStack(spacing: 30) {
                        // Progress Bar
                        VStack(spacing: 8) {
                            HStack {
                                Text("Soru \(currentPage + 1) / \(questions.count)")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text("\(Int(progress * 100))%")
                                    .font(.caption)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.purple)
                            }
                            
                            ProgressView(value: progress)
                                .tint(.purple)
                                .scaleEffect(y: 2)
                        }
                        .padding()
                        
                        Spacer()
                        
                        // Question
                        VStack(spacing: 24) {
                            Image(systemName: "questionmark.circle.fill")
                                .font(.system(size: 60))
                                .foregroundColor(.purple)
                            
                            Text(questions[currentPage].question)
                                .font(.title2)
                                .fontWeight(.semibold)
                                .foregroundColor(.primary)  // Light ve dark mod için okunabilir
                                .multilineTextAlignment(.center)
                                .padding(.horizontal)
                        }
                        
                        Spacer()
                        
                        // Options
                        VStack(spacing: 12) {
                            ForEach(questions[currentPage].options, id: \.self) { option in
                                Button {
                                    selectOption(option)
                                } label: {
                                    let isSelected = answers[questions[currentPage].id]?.contains(option) ?? false
                                    HStack {
                                        Text(option)
                                            .fontWeight(.medium)
                                            .foregroundColor(isSelected ? .white : .primary)  // Seçili: beyaz, değil: sistem rengi (dark/light otomatik)
                                        Spacer()
                                        if isSelected {
                                            Image(systemName: "checkmark.circle.fill")
                                                .foregroundColor(.white)
                                        } else {
                                            Image(systemName: "circle")
                                                .foregroundColor(.primary.opacity(0.5))
                                        }
                                    }
                                    .padding()
                                    .background(
                                        isSelected ? Color.purple : Color(uiColor: .systemBackground).opacity(0.8)  // Light modda beyaz, dark modda siyah
                                    )
                                    .cornerRadius(12)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(Color.primary.opacity(0.1), lineWidth: 1)  // Hafif kenarlık
                                    )
                                }
                            }
                            
                            // Multiple selection hint
                            Text("* Birden fazla seçenek işaretleyebilirsiniz")
                                .font(.caption)
                                .foregroundColor(.secondary)  // Light ve dark mod için uyumlu
                                .padding(.top, 4)
                        }
                        .padding(.horizontal)
                        
                        // Navigation Buttons
                        HStack(spacing: 16) {
                            if currentPage > 0 {
                                Button {
                                    withAnimation {
                                        currentPage -= 1
                                    }
                                } label: {
                                    HStack {
                                        Image(systemName: "chevron.left")
                                        Text("Geri")
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.secondary.opacity(0.2))  // Light/dark mod uyumlu
                                    .foregroundColor(.primary)  // Light modda siyah, dark modda beyaz
                                    .cornerRadius(12)
                                }
                            }
                            
                            Button {
                                nextQuestion()
                            } label: {
                                HStack {
                                    Text(currentPage == questions.count - 1 ? "Bitir" : "İleri")
                                    Image(systemName: "chevron.right")
                                }
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background((answers[questions[currentPage].id]?.isEmpty ?? true) ? Color.gray : Color.purple)
                                    .foregroundColor(.white)  // Buton metni her zaman beyaz
                                    .cornerRadius(12)
                            }
                            .disabled(answers[questions[currentPage].id]?.isEmpty ?? true)
                        }
                        .padding(.horizontal)
                        .padding(.bottom, 30)
                    }
                    .transition(.slide)
                } else {
                    // Summary View
                    VStack(spacing: 30) {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 80))
                            .foregroundColor(.green)
                        
                        Text("Profilleme Tamamlandı!")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                        
                        Text("Cevaplarınız AI önerilerimizi kişiselleştirmek için kullanılacak.")
                            .font(.body)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 40)
                        
                        Button {
                            saveAndClose()
                        } label: {
                            Text("Tamam")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.purple)
                                .foregroundColor(.white)
                                .cornerRadius(12)
                        }
                        .padding(.horizontal, 40)
                        .padding(.top, 20)
                    }
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.secondary)
                    }
                }
            }
        }
    }
    
    func selectOption(_ option: String) {
        withAnimation {
            let questionId = questions[currentPage].id
            if answers[questionId] == nil {
                answers[questionId] = []
            }
            
            // Toggle selection
            if let index = answers[questionId]?.firstIndex(of: option) {
                answers[questionId]?.remove(at: index)
            } else {
                answers[questionId]?.append(option)
            }
        }
    }
    
    func nextQuestion() {
        guard !(answers[questions[currentPage].id]?.isEmpty ?? true) else { return }
        
        withAnimation {
            currentPage += 1
        }
    }
    
    func saveAndClose() {
        // Save answers to UserDefaults
        for (key, value) in answers {
            // Join multiple answers with comma
            let answerString = value.joined(separator: ", ")
            UserDefaults.standard.set(answerString, forKey: "profiling_\(key)")
        }
        isCompleted = true
        dismiss()
    }
}

#Preview {
    UserProfilingView()
}

