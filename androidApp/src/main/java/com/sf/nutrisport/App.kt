package com.sf.nutrisport

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.initialize
import com.nutrisport.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(config={
            androidContext(this@App)
        })
        FirebaseApp.initializeApp(this)
    }
}