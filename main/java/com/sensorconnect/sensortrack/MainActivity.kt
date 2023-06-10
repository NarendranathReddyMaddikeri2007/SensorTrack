package com.sensorconnect.sensortrack

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() , FragmentsCallback {
//    private val TAG : String = "MainActivity"
//    private var adview1_banner_ad : AdView? =null
//    private var adview2_banner_ad : AdView? =null
    //private AdView mAdView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        adview1_banner_ad = findViewById(R.id.adView1Up_banner_ad) as AdView
//        adview2_banner_ad = findViewById(R.id.adView2Down_banner_ad) as AdView
//
//        val adRequest : AdRequest = AdRequest.Builder().build()
//        adview1_banner_ad?.loadAd(adRequest)
//        adview2_banner_ad?.loadAd(adRequest)
    }

    override fun getContext(): Context {
        return this@MainActivity
    }


}