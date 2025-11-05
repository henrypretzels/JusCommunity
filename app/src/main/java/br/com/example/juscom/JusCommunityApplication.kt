package br.com.example.juscom

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JusCommunityApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        applyUserTheme()
    }

    private fun applyUserTheme() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Set a default theme immediately.
        // If a user is logged in, we will try to load their preference.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) 

        if (currentUser != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val theme = document.getString("theme_preference")
                    if (theme == "dark") {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                .addOnFailureListener { 
                    // Silently fail and stick with the default light theme.
                }
        } 
    }
}
