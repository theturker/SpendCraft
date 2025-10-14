//
//  BannerAdView.swift
//  SpendCraftiOS
//
//  Banner Ad View Component for SwiftUI
//

import SwiftUI
import GoogleMobileAds

struct BannerAdView: View {
    @StateObject private var adsManager = AdsManager.shared
    
    let adSize: AdSize
    
    init(adSize: AdSize = AdSizeBanner) {
        self.adSize = adSize
    }
    
    var body: some View {
        if adsManager.shouldShowAds() {
            BannerViewRepresentable(adSize: adSize)
                .frame(width: adSize.size.width, height: adSize.size.height)
        }
    }
}

/// UIViewRepresentable wrapper for BannerView
struct BannerViewRepresentable: UIViewRepresentable {
    let adSize: AdSize
    
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }
    
    func makeUIView(context: Context) -> BannerView {
        let bannerView = BannerView(adSize: adSize)
        
        // Configure banner
        bannerView.adUnitID = AdsManager.AdUnitIDs.banner
        bannerView.delegate = context.coordinator
        
        // Get root view controller
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {
            bannerView.rootViewController = rootViewController
        }
        
        // Load ad
        let request = Request()
        bannerView.load(request)
        
        return bannerView
    }
    
    func updateUIView(_ uiView: BannerView, context: Context) {
        // Optionally reload ad on update
    }
    
    class Coordinator: NSObject, BannerViewDelegate {
        func bannerViewDidReceiveAd(_ bannerView: BannerView) {
            print("✅ Banner ad loaded successfully")
        }
        
        func bannerView(_ bannerView: BannerView, didFailToReceiveAdWithError error: Error) {
            print("❌ Banner ad failed to load: \(error.localizedDescription)")
        }
        
        func bannerViewDidRecordImpression(_ bannerView: BannerView) {
            print("📊 Banner ad recorded impression")
        }
        
        func bannerViewWillPresentScreen(_ bannerView: BannerView) {
            print("🔼 Banner ad will present screen")
        }
        
        func bannerViewWillDismissScreen(_ bannerView: BannerView) {
            print("🔽 Banner ad will dismiss screen")
        }
        
        func bannerViewDidDismissScreen(_ bannerView: BannerView) {
            print("✅ Banner ad dismissed screen")
        }
    }
}

// MARK: - Adaptive Banner Ad View

struct AdaptiveBannerAdView: View {
    @StateObject private var adsManager = AdsManager.shared
    
    var body: some View {
        if adsManager.shouldShowAds() {
            // Standard banner - her cihazda çalışır
            BannerViewRepresentable(adSize: AdSizeBanner)
                .frame(height: 50)
        }
    }
}

// MARK: - Preview

#if DEBUG
struct BannerAdView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            Text("Content Above")
                .padding()
            
            Spacer()
            
            // Standard Banner
            BannerAdView()
                .padding()
            
            Text("Content Below")
                .padding()
        }
    }
}
#endif

