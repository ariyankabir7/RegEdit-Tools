package app.test.xyz

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.test.xyz.databinding.ActivityOptimizationBinding
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

class OptimizationActivity : AppCompatActivity() {
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    var status: Int = 0
    lateinit var link: String
    lateinit var link1: String
    lateinit var url: String

    lateinit var binding: ActivityOptimizationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOptimizationBinding.inflate(layoutInflater)
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
                        val jsonObject =
                            jsonArray.getJSONObject(0) // Assuming there's only one object in the array
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
                        binding.nativeBtn.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing JSON: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            { Toast.makeText(this, "Try After Some Time !", Toast.LENGTH_SHORT).show() })

// Add the request to the RequestQueue.
        queue.add(stringRequest4)

        binding.nativeads.setOnClickListener {
            if (status == 0) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(link)
                startActivity(i)

            } else if (status == 1) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(link))
            }
        }
        var switchList = listOf(
            binding.switch1,
            binding.switch2,
            binding.switch3,
            binding.switch4,
            binding.switch5
        ).forEach(::setSwitchTintColor)

        binding.header.back.setOnClickListener { finish() }

        binding.checkbtn.setOnClickListener {

            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {


                            // Inflate custom layout for AlertDialog
                            val customLayout = LayoutInflater.from(this@OptimizationActivity)
                                .inflate(R.layout.custom_alert_layout, null)
                            val progressBar: ProgressBar =
                                customLayout.findViewById(R.id.progressBar)
                            val textView: TextView = customLayout.findViewById(R.id.textView)

                            // Create AlertDialog with custom layout
                            val builder = AlertDialog.Builder(this@OptimizationActivity)
                            builder.setView(customLayout)
                            builder.setCancelable(false) // Prevent dismissing AlertDialog by tapping outside or pressing back button

                            // Show AlertDialog
                            val alertDialog = builder.create()
                            alertDialog.show()

                            // Simulating some background task delay
                            Handler().postDelayed({
                                // Dismiss AlertDialog
                                alertDialog.dismiss()

                                if (!binding.switch1.isChecked) {
                                    val customLayout =
                                        LayoutInflater.from(this@OptimizationActivity).inflate(R.layout.bad_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder =
                                        AlertDialog.Builder(this@OptimizationActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true) // Prevent dismissing AlertDialog by tapping outside or pressing back button
                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn =
                                        customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }

                                } else if (!binding.switch2.isChecked) {

                                    val customLayout = LayoutInflater.from(this@OptimizationActivity)
                                        .inflate(R.layout.bad_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder =
                                        AlertDialog.Builder(this@OptimizationActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true) // Prevent dismissing AlertDialog by tapping outside or pressing back button
                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn =
                                        customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }
                                } else if (!binding.switch3.isChecked) {

                                    val customLayout = LayoutInflater.from(this@OptimizationActivity)
                                        .inflate(R.layout.bad_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder =
                                        AlertDialog.Builder(this@OptimizationActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true) // Prevent dismissing AlertDialog by tapping outside or pressing back button

                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn =
                                        customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }

                                } else if (!binding.switch4.isChecked) {
                                    val customLayout = LayoutInflater.from(this@OptimizationActivity)
                                        .inflate(R.layout.good_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder =
                                        AlertDialog.Builder(this@OptimizationActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true) // Prevent dismissing AlertDialog by tapping outside or pressing back button

                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn =
                                        customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }

                                } else if (!binding.switch5.isChecked) {
                                    val customLayout = LayoutInflater.from(this@OptimizationActivity)
                                        .inflate(R.layout.awesome_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder =
                                        AlertDialog.Builder(this@OptimizationActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true) // Prevent dismissing AlertDialog by tapping outside or pressing back button

                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn =
                                        customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }

                                } else {
                                    val customLayout = LayoutInflater.from(this@OptimizationActivity)
                                        .inflate(R.layout.best_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder =
                                        AlertDialog.Builder(this@OptimizationActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true) // Prevent dismissing AlertDialog by tapping outside or pressing back button

                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn =
                                        customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }
                                }
                            }, 2000) // Delay of 5 seconds
                        }
                    }
                mAdManagerInterstitialAd?.show(this)
                loadInterstialads()
            } else {
                Toast.makeText(this, "Try Again !", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setSwitchTintColor(switchButton: Switch) {
        switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            // Change the color when the switch is toggled
            if (isChecked) {
                switchButton.thumbDrawable.setTint(getResources().getColor(R.color.light_green))
            } else {
                switchButton.thumbDrawable.setTint(getResources().getColor(R.color.off_switch))
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
    private fun nativeads() {
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