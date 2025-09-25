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
    onAdLoaded: (() -> Unit)? = null,
    onAdFailedToLoad: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }

    // AdMob'u başlat
    LaunchedEffect(Unit) {
        MobileAds.initialize(context) {}
    }

    // Reklam yükleme işlemini arka planda başlat
    LaunchedEffect(Unit) {
        if (!isAdLoaded) {
            val adView = AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                        onAdLoaded?.invoke()
                    }
                    
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        adError = loadAdError.message
                        onAdFailedToLoad?.invoke(loadAdError.message)
                    }
                }
                
                loadAd(AdRequest.Builder().build())
            }
        }
    }

    // Reklam yüklenmediğinde hiçbir şey gösterme
    if (!isAdLoaded) {
        return // Reklam yüklenmediğinde hiçbir şey gösterme
    }

    // Sadece reklam yüklendiğinde göster
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                        onAdLoaded?.invoke()
                    }
                    
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        adError = loadAdError.message
                        onAdFailedToLoad?.invoke(loadAdError.message)
                    }
                }
                
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun AdMobBannerWithPadding(
    modifier: Modifier = Modifier,
    adUnitId: String = stringResource(R.string.admob_banner_id),
    onAdLoaded: (() -> Unit)? = null,
    onAdFailedToLoad: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        AdMobBanner(
            adUnitId = adUnitId,
            onAdLoaded = onAdLoaded,
            onAdFailedToLoad = onAdFailedToLoad
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
