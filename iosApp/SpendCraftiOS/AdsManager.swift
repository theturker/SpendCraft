//
//  AdsManager.swift
//  SpendCraftiOS
//
//  Google Ads Manager - Singleton pattern
//

import Foundation
import GoogleMobileAds
import SwiftUI

class AdsManager: NSObject, ObservableObject {
    static let shared = AdsManager()
    
    @Published var isPremium: Bool = false
    
    // Test Ad Unit IDs - Production'da gerçek ID'ler kullanılmalı
    struct AdUnitIDs {
        // Test IDs - Google tarafından sağlanan test ID'leri
        static let bannerTest = "ca-app-pub-3940256099942544/2934735716"
        static let interstitialTest = "ca-app-pub-3940256099942544/4411468910"
        static let rewardedTest = "ca-app-pub-3940256099942544/1712485313"
        
        // Production IDs - Gerçek AdMob hesabınızdan alınacak
        // Release build'de bu ID'ler kullanılmalı
        #if DEBUG
        // DEBUG modda TEST reklamlarını kullan (gerçek reklamlar hazır olana kadar)
        // AdMob'da reklam birimleri aktif olunca gerçek ID'leri kullanın
        static let banner = bannerTest  // Geçici olarak test ID
        static let interstitial = interstitialTest  // Geçici olarak test ID
        static let rewarded = rewardedTest
        #else
        // Production IDs - Gerçek AdMob hesabından alınan ID'ler
        static let banner = "ca-app-pub-3199248450820147/4629737731"
        static let interstitial = "ca-app-pub-3199248450820147/4092156468"
        static let rewarded = rewardedTest // Henüz rewarded ad oluşturulmadı, test ID kullanılıyor
        #endif
    }
    
    private var interstitialAd: InterstitialAd?
    private var rewardedAd: RewardedAd?
    
    private override init() {
        super.init()
        // Premium durumunu UserDefaults'tan oku
        self.isPremium = UserDefaults.standard.bool(forKey: "isPremium")
    }
    
    /// Google Mobile Ads SDK'sını başlat
    func initializeAds() {
        MobileAds.shared.start { status in
            print("Google Mobile Ads SDK initialized")
            print("Adapter statuses:")
            status.adapterStatusesByClassName.forEach { className, adapterStatus in
                print("- \(className): \(adapterStatus.state.rawValue) - \(adapterStatus.description)")
            }
        }
        
        // Test cihazları ekle (geliştirme sırasında)
        #if DEBUG
        let testDeviceIdentifier = "Simulator"
        MobileAds.shared.requestConfiguration.testDeviceIdentifiers = [testDeviceIdentifier]
        #endif
    }
    
    /// Premium durumunu güncelle
    func updatePremiumState(_ premium: Bool) {
        isPremium = premium
        UserDefaults.standard.set(premium, forKey: "isPremium")
    }
    
    /// Banner reklam gösterilmeli mi?
    func shouldShowAds() -> Bool {
        return !isPremium
    }
    
    // MARK: - Interstitial Ads
    
    /// Interstitial reklam yükle
    func loadInterstitialAd() {
        guard !isPremium else {
            print("⚠️ Interstitial ad not loaded - User is premium")
            return
        }
        
        print("🔄 Loading interstitial ad with ID: \(AdUnitIDs.interstitial)")
        
        InterstitialAd.load(
            with: AdUnitIDs.interstitial,
            request: Request()
        ) { [weak self] ad, error in
            if let error = error {
                print("❌ Failed to load interstitial ad: \(error.localizedDescription)")
                self?.interstitialAd = nil
                return
            }
            
            self?.interstitialAd = ad
            self?.interstitialAd?.fullScreenContentDelegate = self
            print("✅ Interstitial ad loaded successfully!")
        }
    }
    
    /// Interstitial reklam göster
    func showInterstitialAd(from viewController: UIViewController?, onAdClosed: @escaping () -> Void = {}) {
        showInterstitialAd(from: viewController, retryCount: 0, onAdClosed: onAdClosed)
    }
    
    private func showInterstitialAd(from viewController: UIViewController?, retryCount: Int, onAdClosed: @escaping () -> Void = {}) {
        let maxRetries = 3 // Maksimum 3 kez dene
        
        guard !isPremium else {
            print("⚠️ Interstitial ad not shown - User is premium")
            onAdClosed()
            return
        }
        
        guard let interstitialAd = interstitialAd else {
            print("⚠️ Interstitial ad not ready - Loading new ad...")
            onAdClosed()
            // Tekrar yükle
            loadInterstitialAd()
            return
        }
        
        if let viewController = viewController {
            // View controller'ın meşgul olup olmadığını kontrol et
            if viewController.presentedViewController != nil {
                if retryCount < maxRetries {
                    print("⚠️ View controller is busy presenting another view controller (attempt \(retryCount + 1)/\(maxRetries))")
                    print("🔄 Retrying in 2 seconds...")
                    
                    // 2 saniye sonra tekrar dene
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                        self.showInterstitialAd(from: viewController, retryCount: retryCount + 1, onAdClosed: onAdClosed)
                    }
                    return
                } else {
                    print("❌ Max retries reached. View controller is still busy. Skipping interstitial ad.")
                    onAdClosed()
                    return
                }
            }
            
            print("🎬 Presenting interstitial ad...")
            interstitialAd.present(from: viewController)
        } else {
            print("❌ No view controller to present interstitial ad")
            onAdClosed()
        }
    }
    
    // MARK: - Rewarded Ads
    
    /// Rewarded reklam yükle
    func loadRewardedAd() {
        guard !isPremium else { return }
        
        RewardedAd.load(
            with: AdUnitIDs.rewarded,
            request: Request()
        ) { [weak self] ad, error in
            if let error = error {
                print("Failed to load rewarded ad: \(error.localizedDescription)")
                self?.rewardedAd = nil
                return
            }
            
            self?.rewardedAd = ad
            self?.rewardedAd?.fullScreenContentDelegate = self
            print("Rewarded ad loaded successfully")
        }
    }
    
    /// Rewarded reklam göster
    func showRewardedAd(from viewController: UIViewController?, onRewarded: @escaping () -> Void = {}, onAdClosed: @escaping () -> Void = {}) {
        guard !isPremium else {
            onRewarded()
            onAdClosed()
            return
        }
        
        guard let rewardedAd = rewardedAd else {
            print("Rewarded ad not ready")
            onAdClosed()
            // Tekrar yükle
            loadRewardedAd()
            return
        }
        
        if let viewController = viewController {
            rewardedAd.present(from: viewController) {
                let reward = rewardedAd.adReward
                print("User earned reward: \(reward.amount) \(reward.type)")
                onRewarded()
            }
        } else {
            onAdClosed()
        }
    }
}

// MARK: - FullScreenContentDelegate

extension AdsManager: FullScreenContentDelegate {
    func adDidRecordImpression(_ ad: FullScreenPresentingAd) {
        print("Ad recorded an impression")
    }
    
    func adDidRecordClick(_ ad: FullScreenPresentingAd) {
        print("Ad recorded a click")
    }
    
    func ad(_ ad: FullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        print("Ad failed to present: \(error.localizedDescription)")
    }
    
    func adWillPresentFullScreenContent(_ ad: FullScreenPresentingAd) {
        print("Ad will present")
    }
    
    func adWillDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        print("Ad will dismiss")
    }
    
    func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        print("Ad dismissed")
        
        // Reklam kapandıktan sonra yeni reklam yükle
        if ad is InterstitialAd {
            loadInterstitialAd()
        } else if ad is RewardedAd {
            loadRewardedAd()
        }
    }
}

