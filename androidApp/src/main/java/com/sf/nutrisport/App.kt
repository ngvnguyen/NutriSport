package com.sf.nutrisport

import android.app.Application

import com.nutrisport.di.initializeKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(config={
            androidContext(this@App)
        })
        Firebase.initialize(this)
    }
}