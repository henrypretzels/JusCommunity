package br.com.example.juscom

import android.app.Application
import com.google.firebase.FirebaseApp

class JusCommunityApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
