package br.com.example.juscom.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "JusCommunityPrefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
    }
    
    fun saveUserSession(user: FirebaseUser) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_NAME, user.displayName ?: "")
        editor.putString(KEY_USER_ID, user.uid)
        editor.apply()
    }
    
    fun clearUserSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
    
    fun isUserLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
}
