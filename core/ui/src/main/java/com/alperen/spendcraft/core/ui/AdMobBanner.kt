package com.alperen.spendcraft.core.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdListener
import com.alperen.spendcraft.core.ui.R

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = stringResource(R.string.admob_banner_id),
    isPremium: Boolean = false,
    onAdLoaded: (() -> Unit)? = null,
    onAdFailedToLoad: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }
    var adView by remember { mutableStateOf<AdView?>(null) }

    // Premium kullanıcılar için reklam gösterme
    if (isPremium) {
        return
    }

    // AdMob'u başlat ve reklamı yükle
    DisposableEffect(adUnitId) {
        MobileAds.initialize(context) {}

        val newAdView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    isAdLoaded = true
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adError = loadAdError.message
                    isAdLoaded = false // Reklam yüklenemediğinde gizle
                    onAdFailedToLoad?.invoke(loadAdError.message)
                }
            }
            loadAd(AdRequest.Builder().build())
        }
        adView = newAdView

        onDispose {
            newAdView.destroy()
        }
    }

    // Sadece reklam yüklendiğinde göster
    if (isAdLoaded && adView != null) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            factory = {
                adView ?: AdView(it).apply { // Fallback if adView is null, though it shouldn't be
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { view ->
                // Reklam görünümünü güncellemek gerekirse buraya eklenebilir
            }
        )
    }
}

@Composable
fun AdMobBannerWithPadding(
    modifier: Modifier = Modifier,
    adUnitId: String = stringResource(R.string.admob_banner_id),
    isPremium: Boolean = false,
    onAdLoaded: (() -> Unit)? = null,
    onAdFailedToLoad: ((String) -> Unit)? = null
) {
    // Premium kullanıcılar için hiçbir boşluk bırakma
    if (isPremium) return

    var loaded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        AdMobBanner(
            adUnitId = adUnitId,
            isPremium = false,
            onAdLoaded = {
                loaded = true
                onAdLoaded?.invoke()
            },
            onAdFailedToLoad = { msg ->
                loaded = false
                onAdFailedToLoad?.invoke(msg)
            }
        )
        if (loaded) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
