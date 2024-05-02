package app.test.xyz

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.test.xyz.databinding.ActivityDeviceInfoBinding
import app.test.xyz.databinding.ActivityHomeBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import org.json.JSONArray
import org.json.JSONException

class DeviceInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeviceInfoBinding
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    var status:Int=0
    lateinit var link:String
    lateinit var link1:String
    lateinit var url:String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDeviceInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = false
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MobileAds.initialize(this) {}
        loadInterstialads()
        nativeads()

        binding.back.setOnClickListener { finish() }
        binding.revealInfo.setOnClickListener {
            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            val intent =
                                Intent(this@DeviceInfoActivity, DeviceDetailsActivity::class.java)
                            startActivity(intent)
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun loadInterstialads() {
        var adRequest = AdManagerAdRequest.Builder().build()

        AdManagerInterstitialAd.load(
            this,
            "/6499/example/interstitial",
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.toString()?.let { Log.d(ContentValues.TAG, it) }
                    mAdManagerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.d(ContentValues.TAG, "Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                }
            })
    }

    @SuppressLint("ResourceType")
    private fun nativeads(){
        val adLoader = AdLoader.Builder(this, "/6499/example/native")
            .forNativeAd { nativeAd ->
                val adView = findViewById<NativeAdView>(R.id.nativead)
                populateNativeAdView(nativeAd, adView)
            }
            .build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }
    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Register the NativeAdView with the native ad
        adView.setNativeAd(nativeAd)

    }
}