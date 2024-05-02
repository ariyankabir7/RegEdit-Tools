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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONException

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null

    var status:Int=0
    lateinit var link:String
    lateinit var link1:String
    lateinit var url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityHomeBinding.inflate(layoutInflater)
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

        val queue = Volley.newRequestQueue(this)
        val url4 = "${Constant.native_ads_link}get_native_data.php"

// Request a string response from the provided URL.
        val stringRequest4 = StringRequest(
            Request.Method.GET, url4,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        val jsonObject = jsonArray.getJSONObject(0) // Assuming there's only one object in the array
                        val iconImageUrl = jsonObject.getString("icon_image")
                        val Image = jsonObject.getString("image")
                        val title = jsonObject.getString("ads_title")
                        val subtitle = jsonObject.getString("ads_subtitle")
                        val btntext = jsonObject.getString("ads_btn_text")
                        status = jsonObject.getInt("status")
                        link = jsonObject.getString("link")

                        // Set the icon image using Glide or any other image loading library
                        Glide.with(this)
                            .load(iconImageUrl)
                            .into(binding.nativeIconImage) // Assuming binding is the reference to your layout binding object
// Set the icon image using Glide or any other image loading library
                        Glide.with(this)
                            .load(Image)
                            .into(binding.nativeMainImage)
                        // Set the title text
                        binding.nativeTitle.text = title
                        binding.nativeSubTitle.text = subtitle
                        binding.nativeBtnTitle.text = btntext
                        binding.nativeBtn.visibility= View.VISIBLE
                    }
                }catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing JSON: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { Toast.makeText(this, "Try After Some Time !", Toast.LENGTH_SHORT).show()})

// Add the request to the RequestQueue.
        queue.add(stringRequest4)

        binding.nativeads.setOnClickListener {
            if (status==0){
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(link)
                startActivity(i)

            }else if(status==1){
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(link))
            }
        }

        binding.ivDevice.setOnClickListener {

            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            val intent = Intent(this@HomeActivity, DeviceInfoActivity::class.java)
                            startActivity(intent)
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }

        }
        binding.ivVipmode.setOnClickListener {

            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            val intent = Intent(this@HomeActivity, VipModeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }


        }
        binding.ivOptimaze.setOnClickListener {
            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            val intent = Intent(this@HomeActivity, OptimizationActivity::class.java)
                            startActivity(intent)
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }

        }
        binding.ivGraphics.setOnClickListener {
            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            val intent = Intent(this@HomeActivity, GraphicsActivity::class.java)
                            startActivity(intent)
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }

        }
        binding.ivShadow.setOnClickListener {
            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            val intent = Intent(this@HomeActivity, ShadowActivity::class.java)
                            startActivity(intent)
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }


        }
        binding.ivRateus.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
        binding.ivShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val appLink = "https://play.google.com/store/apps/details?id=$packageName"
            val shareMessage =
                "This is FFF Reg Edit  Tool App to Get Free Game Settings Etc.:\n$appLink"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share app via"))
        }
        binding.ivWhatsapp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.setPackage("com.whatsapp")
            val appLink = "https://play.google.com/store/apps/details?id=$packageName"
            val shareMessage =
                "This is FFF Reg Edit  Tool App to Get Free Game Settings Etc.:\n$appLink"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share app via"))
        }

        binding.menu.setOnClickListener {
            // Open the side navigation drawer
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.shared -> {

                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    val appLink = "https://play.google.com/store/apps/details?id=$packageName"
                    val shareMessage =
                        "This is FFF Reg Edit  Tool App to Get Free Game Settings Etc.:\n$appLink"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "Share app via"))
                    true
                }

                R.id.rate -> {

                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                    true
                }

                R.id.privacy -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
                    true
                }

                R.id.exit -> {
                    showPpopupDialog()
                    true
                }

                else -> {
                    false
                }
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

    private fun showPpopupDialog() {
        AlertDialog.Builder(this, R.style.TransparentDialogTheme).setView(R.layout.back_popup)
            .setCancelable(true).create().apply {
                show()

                findViewById<MaterialButton>(R.id.buttonCancel)?.setOnClickListener {
                    dismiss()
                }
                findViewById<MaterialButton>(R.id.buttonConfirm)?.setOnClickListener {
                    dismiss()
                    super.onBackPressed()
                    finish()
                }
            }

    }

    override fun onBackPressed() {
       showPpopupDialog()
    }
}