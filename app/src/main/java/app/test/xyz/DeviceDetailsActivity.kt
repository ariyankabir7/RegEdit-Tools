package app.test.xyz

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.test.xyz.databinding.ActivityDeviceDetailsBinding
import app.test.xyz.databinding.ActivityGraphicsBinding
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import org.json.JSONArray
import org.json.JSONException

class DeviceDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeviceDetailsBinding
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null

    var status:Int=0
    lateinit var link:String
    lateinit var link1:String
    lateinit var url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityDeviceDetailsBinding.inflate(layoutInflater)
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
        nativeads()

        binding.header.back.setOnClickListener { finish() }

        binding.processor.text = Build.HARDWARE
        binding.boot.text = Build.BOOTLOADER
       // binding.model.text = Build.MODEL
        binding.sdk.text = Build.VERSION.SDK_INT.toString()
        binding.android.text = Build.VERSION.RELEASE.toString()
        binding.board.text = Build.BOARD

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        binding.carrier.text = telephonyManager.networkOperatorName

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

    }
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