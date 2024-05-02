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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.test.xyz.databinding.ActivityGraphicsBinding
import app.test.xyz.databinding.ActivityShadowBinding
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

class GraphicsActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphicsBinding
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    var status:Int=0
    lateinit var link:String
    lateinit var link1:String
    lateinit var url:String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityGraphicsBinding.inflate(layoutInflater)
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

        binding.header.back.setOnClickListener { finish() }

        var g = ""
        var r = ""
        var f = ""
        var t = ""

        val graphics = listOf("Smooth", "Balanced", "HD", "HDR", "Ultra")
        val resolution =
            listOf("960x540", "1024x576", "1280(HD)", "1366x768", "1440(HD+)", "1600x900")
        val fps = listOf("30FPS", "60FPS", "90FPS", "120FPS", "150FPS", "180FPS")
        val text = listOf("Default", "Low", "Medium", "120FPS", "150FPS", "180FPS")

        binding.spinner1.setSelection(0) // Select the first item by default

// Define an adapter for the spinner
        val adapter =
            ArrayAdapter.createFromResource(this, R.array.resolution_array, R.layout.spinner_dropdown_item)
        // Specify the layout to use when the list of choices appearss
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the spinner
        binding.spinner1.adapter = adapter
// Set the selection based on the index of the item in the graphics list
        binding.spinner1.setSelection(0)
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                g = graphics[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection if needed
            }
        }
        binding.spinner2.setSelection(0) // Select the first item by default


        val adapter1 =
            ArrayAdapter.createFromResource(this, R.array.Style_array, R.layout.spinner_dropdown_item)
        // Specify the layout to use when the list of choices appearss
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the spinner
        binding.spinner2.adapter = adapter1


// Set the selection based on the index of the item in the graphics list
        binding.spinner2.setSelection(0)

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                r = resolution[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection if needed
            }
        }
        binding.spinner3.setSelection(0) // Select the first item by default

// Define an adapter for the spinner

        val adapter3 =
            ArrayAdapter.createFromResource(this, R.array.graphics_array, R.layout.spinner_dropdown_item)
        // Specify the layout to use when the list of choices appearss
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the spinner
        binding.spinner3.adapter = adapter3
// Set the selection based on the index of the item in the graphics list
        binding.spinner3.setSelection(0)
        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                f = fps[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection if needed
            }
        }
// Define an adapter for the spinner

        val adapter4 =
            ArrayAdapter.createFromResource(this, R.array.Shadow, R.layout.spinner_dropdown_item)
        // Specify the layout to use when the list of choices appearss
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the spinner
        binding.spinner4.adapter = adapter4
// Set the selection based on the index of the item in the graphics list
        binding.spinner4.setSelection(0)
        binding.spinner4.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                t = text[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection if needed
            }
        }
        // Define an adapter for the spinner

        val adapter5 =
            ArrayAdapter.createFromResource(this, R.array.fps_array, R.layout.spinner_dropdown_item)
        // Specify the layout to use when the list of choices appearss
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the spinner
        binding.spinner5.adapter = adapter5
// Set the selection based on the index of the item in the graphics list
        binding.spinner5.setSelection(0)
        binding.spinner5.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                t = text[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection if needed
            }
        }
        // Define an adapter for the spinner

        val adapter6 =
            ArrayAdapter.createFromResource(this, R.array.Lgraphics_array, R.layout.spinner_dropdown_item)
        // Specify the layout to use when the list of choices appearss
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter to the spinner
        binding.spinner6.adapter = adapter6
// Set the selection based on the index of the item in the graphics list
        binding.spinner6.setSelection(0)
        binding.spinner6.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                t = text[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection if needed
            }
        }

        binding.checkbtn.setOnClickListener {

            // Check if the interstitial ad is loaded
            if (mAdManagerInterstitialAd != null) {
                // Show the interstitial ad
                mAdManagerInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {

                            // Inflate custom layout for AlertDialog
                            val customLayout = LayoutInflater.from(this@GraphicsActivity)
                                .inflate(R.layout.custom_alert_layout, null)
                            val progressBar: ProgressBar =
                                customLayout.findViewById(R.id.progressBar)
                            val textView: TextView = customLayout.findViewById(R.id.textView)

                            // Create AlertDialog with custom layout
                            val builder = AlertDialog.Builder(this@GraphicsActivity)
                            builder.setView(customLayout)
                            builder.setCancelable(false) // Prevent dismissing AlertDialog by tapping outside or pressing back button

                            // Show AlertDialog
                            val alertDialog = builder.create()
                            alertDialog.show()

                            // Simulating some background task delay
                            Handler().postDelayed({
                                // Dismiss AlertDialog
                                alertDialog.dismiss()

                                // Show toast indicating successful operation completion
                                //Toast.makeText(this@GraphicsActivity, "Settings Apply successfully!", Toast.LENGTH_SHORT).show()
                                //finish()
                                if (g == "Smooth") {
                                    val customLayout = LayoutInflater.from(this@GraphicsActivity)
                                        .inflate(R.layout.bad_popup, null)
                                    // Create AlertDialog with custom layout
                                    val builder = AlertDialog.Builder(this@GraphicsActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true)

                                    val popupDialog = builder.create()
                                    popupDialog.show()
                                    // Prevent dismissing AlertDialog by tapping outside or pressing back button
                                    val okaybtn = customLayout.findViewById<LinearLayout>(R.id.okbtn)

                                    okaybtn.setOnClickListener {
                                        popupDialog.dismiss()
                                    }
                                } else if (r == "960x540") {
                                    val customLayout = LayoutInflater.from(this@GraphicsActivity)
                                        .inflate(R.layout.bad_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder = AlertDialog.Builder(this@GraphicsActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true)

                                    val popupDialog = builder.create()
                                    popupDialog.show()
                                    // Prevent dismissing AlertDialog by tapping outside or pressing back button
                                    val okaybtn = customLayout.findViewById<LinearLayout>(R.id.okbtn)

                                    okaybtn.setOnClickListener {
                                        popupDialog.dismiss()
                                    }

                                } else if (f == "30FPS") {
                                    val customLayout = LayoutInflater.from(this@GraphicsActivity)
                                        .inflate(R.layout.good_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder = AlertDialog.Builder(this@GraphicsActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true)
                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn = customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }
                                } else if (t == "Default") {
                                    val customLayout = LayoutInflater.from(this@GraphicsActivity)
                                        .inflate(R.layout.best_popup, null)

                                    val okaybtn4 = findViewById<LinearLayout>(R.id.okbtn)

                                    // Create AlertDialog with custom layout
                                    val builder = AlertDialog.Builder(this@GraphicsActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true)

                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn = customLayout.findViewById<LinearLayout>(R.id.okbtn)
                                    okaybtn.setOnClickListener {
                                        popupAlert.dismiss()
                                    }
                                } else {
                                    val customLayout =
                                        LayoutInflater.from(this@GraphicsActivity).inflate(R.layout.awesome_popup, null)

                                    // Create AlertDialog with custom layout
                                    val builder = AlertDialog.Builder(this@GraphicsActivity, R.style.TransparentDialogTheme)
                                    builder.setView(customLayout)
                                    builder.setCancelable(true)
                                    val popupAlert = builder.create()
                                    popupAlert.show()

                                    val okaybtn = customLayout.findViewById<LinearLayout>(R.id.okbtn)
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