package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _theme = MutableLiveData<String>()
    val theme: LiveData<String> = _theme

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _accountDeleted = MutableLiveData<Boolean>()
    val accountDeleted: LiveData<Boolean> = _accountDeleted

    fun loadCurrentTheme() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val themePreference = document.getString("theme_preference")
                    _theme.postValue(themePreference ?: "light")
                }
        }
    }

    fun saveThemePreference(theme: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .update("theme_preference", theme)
        }
    }

    fun sendPasswordResetEmail() {
        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    _toastMessage.postValue("Password reset email sent to your email address.")
                }
                .addOnFailureListener { e ->
                    _toastMessage.postValue("Failed to send password reset email: ${e.message}")
                }
        }
    }

    fun deleteUserAccount() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .delete()
                .addOnSuccessListener {
                    currentUser.delete()
                        .addOnSuccessListener {
                            _toastMessage.postValue("Account deleted successfully.")
                            _accountDeleted.postValue(true)
                        }
                        .addOnFailureListener { e ->
                            _toastMessage.postValue("Failed to delete account: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    _toastMessage.postValue("Failed to delete user data: ${e.message}")
                }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
