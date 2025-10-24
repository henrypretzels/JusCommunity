package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import br.com.example.juscom.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupListeners()
        loadCurrentTheme()
    }

    private fun setupListeners() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference("dark")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference("light")
            }
        }

        binding.buttonChangePassword.setOnClickListener {
            // Implement password change logic
            showChangePasswordDialog()
        }

        binding.buttonDeleteAccount.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }
    }

    private fun loadCurrentTheme() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val theme = document.getString("theme_preference")
                    if (theme == "dark") {
                        binding.switchTheme.isChecked = true
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        binding.switchTheme.isChecked = false
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
        }
    }

    private fun saveThemePreference(theme: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .update("theme_preference", theme)
        }
    }

    private fun showChangePasswordDialog() {
        // In a real app, this would be a more elaborate dialog
        val currentUser = auth.currentUser
        currentUser?.email?.let {
            auth.sendPasswordResetEmail(it)
                .addOnSuccessListener {
                    Toast.makeText(this, "Password reset email sent to your email address.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to send password reset email: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteUserAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUserAccount() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 1. Delete user document from Firestore
            firestore.collection("users").document(currentUser.uid)
                .delete()
                .addOnSuccessListener {
                    // 2. Delete user from Firebase Auth
                    currentUser.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                            // Redirect to login screen
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to delete account: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete user data: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
