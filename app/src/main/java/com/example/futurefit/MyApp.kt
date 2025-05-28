package com.example.futurefit

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = HashMap<String, String>()
        config["cloud_name"] = "deytqlmii"
        config["api_key"] = "234961529499689"
        config["api_secret"] = "SeaNcS3qmTvPKjEWAd35XEiOFiU"
        MediaManager.init(this, config)
    }
}
