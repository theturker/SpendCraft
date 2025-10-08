//
//  OnboardingView.swift
//  SpendCraftiOS
//
//  iOS Native Onboarding - 6 Sayfa
//

import SwiftUI

struct OnboardingPage: Identifiable {
    let id = UUID()
    let title: String
    let description: String
    let systemImage: String
    let gradientColors: [Color]
}

struct OnboardingView: View {
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @State private var currentPage = 0
    
    let pages = [
        OnboardingPage(
            title: "Paratik'e Hoş Geldiniz",
            description: "Kişisel finans yönetiminizi kolaylaştıran akıllı asistanınız. Harcamalarınızı takip edin, bütçe oluşturun ve finansal hedeflerinize ulaşın.",
            systemImage: "chart.line.uptrend.xyaxis",
            gradientColors: [Color.blue, Color.purple]
        ),
        OnboardingPage(
            title: "Akıllı Kategorilendirme",
            description: "İşlemlerinizi otomatik olarak kategorilere ayırın. Harcama alışkanlıklarınızı analiz edin ve nerede tasarruf edebileceğinizi keşfedin.",
            systemImage: "square.grid.2x2",
            gradientColors: [Color.green, Color.mint]
        ),
        OnboardingPage(
            title: "Bütçe Yönetimi",
            description: "Kategoriler için bütçe belirleyin ve harcamalarınızı kontrol altında tutun. Bütçe aşımlarında anında bildirim alın.",
            systemImage: "chart.pie.fill",
            gradientColors: [Color.orange, Color.yellow]
        ),
        OnboardingPage(
            title: "AI Destekli Öneriler",
            description: "Yapay zeka teknolojisi ile kişiselleştirilmiş finansal öneriler alın. Harcama alışkanlıklarınızı optimize edin.",
            systemImage: "sparkles",
            gradientColors: [Color.purple, Color.pink]
        ),
        OnboardingPage(
            title: "Detaylı Raporlar",
            description: "Gelir-gider analizleri, kategori bazında harcama dağılımları ve trend analizleri ile finansal durumunuzu takip edin.",
            systemImage: "chart.bar.fill",
            gradientColors: [Color.red, Color.orange]
        ),
        OnboardingPage(
            title: "Hemen Başlayın",
            description: "Tüm özellikler sizin için hazır! Finansal özgürlük yolculuğunuza şimdi başlayın.",
            systemImage: "checkmark.circle.fill",
            gradientColors: [Color.cyan, Color.blue]
        )
    ]
    
    var body: some View {
        ZStack {
            // Background gradient
            LinearGradient(
                colors: pages[currentPage].gradientColors,
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
            .animation(.easeInOut(duration: 0.5), value: currentPage)
            
            VStack {
                // Skip button
                HStack {
                    Spacer()
                    if currentPage < pages.count - 1 {
                        Button {
                            completeOnboarding()
                        } label: {
                            Text("Atla")
                                .foregroundColor(.white.opacity(0.8))
                                .padding()
                        }
                    }
                }
                
                Spacer()
                
                // Page content
                TabView(selection: $currentPage) {
                    ForEach(0..<pages.count, id: \.self) { index in
                        OnboardingPageView(page: pages[index])
                            .tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))
                
                Spacer()
                
                // Page indicators
                HStack(spacing: 8) {
                    ForEach(0..<pages.count, id: \.self) { index in
                        Circle()
                            .fill(currentPage == index ? Color.white : Color.white.opacity(0.5))
                            .frame(width: currentPage == index ? 10 : 8, height: currentPage == index ? 10 : 8)
                            .animation(.spring(), value: currentPage)
                    }
                }
                .padding(.bottom, 20)
                
                // Navigation buttons
                HStack(spacing: 20) {
                    if currentPage > 0 {
                        Button {
                            withAnimation {
                                currentPage -= 1
                            }
                        } label: {
                            Image(systemName: "chevron.left")
                                .font(.title3)
                                .foregroundColor(.white)
                                .frame(width: 50, height: 50)
                                .background(Color.white.opacity(0.2))
                                .clipShape(Circle())
                        }
                    }
                    
                    Spacer()
                    
                    Button {
                        if currentPage < pages.count - 1 {
                            withAnimation {
                                currentPage += 1
                            }
                        } else {
                            completeOnboarding()
                        }
                    } label: {
                        HStack {
                            Text(currentPage == pages.count - 1 ? "Başla" : "İleri")
                                .fontWeight(.semibold)
                            Image(systemName: currentPage == pages.count - 1 ? "checkmark" : "chevron.right")
                        }
                        .foregroundColor(.white)
                        .frame(height: 50)
                        .frame(maxWidth: currentPage == pages.count - 1 ? .infinity : 120)
                        .background(
                            LinearGradient(
                                colors: [Color.white.opacity(0.3), Color.white.opacity(0.2)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(25)
                    }
                }
                .padding(.horizontal, 30)
                .padding(.bottom, 40)
            }
        }
    }
    
    private func completeOnboarding() {
        withAnimation {
            hasCompletedOnboarding = true
        }
    }
}

struct OnboardingPageView: View {
    let page: OnboardingPage
    
    @State private var isAnimating = false
    
    var body: some View {
        VStack(spacing: 30) {
            Spacer()
            
            // Icon with animation
            Image(systemName: page.systemImage)
                .font(.system(size: 80))
                .foregroundColor(.white)
                .scaleEffect(isAnimating ? 1.0 : 0.8)
                .opacity(isAnimating ? 1.0 : 0.5)
                .animation(
                    .easeInOut(duration: 1.5)
                    .repeatForever(autoreverses: true),
                    value: isAnimating
                )
                .onAppear {
                    isAnimating = true
                }
            
            // Title
            Text(page.title)
                .font(.system(size: 32, weight: .bold))
                .foregroundColor(.white)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
            
            // Description
            Text(page.description)
                .font(.system(size: 18))
                .foregroundColor(.white.opacity(0.9))
                .multilineTextAlignment(.center)
                .lineSpacing(8)
                .padding(.horizontal, 40)
            
            Spacer()
        }
    }
}

// Main app entry point düzenlemesi
struct RootView: View {
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @State private var showSplash = true
    
    var body: some View {
        Group {
            if showSplash {
                SplashView()
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                            withAnimation {
                                showSplash = false
                            }
                        }
                    }
            } else if !hasCompletedOnboarding {
                OnboardingView()
            } else {
                ContentView()
            }
        }
    }
}

struct SplashView: View {
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.blue, Color.purple],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
            
            VStack {
                Image(systemName: "chart.line.uptrend.xyaxis.circle.fill")
                    .font(.system(size: 100))
                    .foregroundColor(.white)
                
                Text("Paratik")
                    .font(.system(size: 42, weight: .bold))
                    .foregroundColor(.white)
            }
        }
    }
}

#Preview {
    OnboardingView()
}
