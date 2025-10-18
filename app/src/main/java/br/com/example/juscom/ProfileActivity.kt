package br.com.example.juscom

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.example.juscom.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase using the standard SDK calls
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Meu Perfil"

        // Check if user is logged in
        if (currentUser == null) {
            // If not, finish activity. Or redirect to LoginActivity
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load user data and setup button listeners
        loadUserProfile()
        setupClickListeners()
    }

    private fun loadUserProfile() {
        val uid = currentUser?.uid
        if (uid != null) {
            val docRef = firestore.collection("users").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Populate the UI with user data
                        binding.profileName.setText(document.getString("name"))
                        binding.profileEmail.setText(document.getString("email"))
                        binding.profileInstitution.setText(document.getString("institution"))
                        binding.profileFedUnit.setText(document.getString("uf"))

                        val level = document.getLong("level") ?: 1
                        binding.profileLevel.text = "Nível $level"

                    } else {
                        Toast.makeText(this, "Perfil não encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Falha ao carregar perfil: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupClickListeners() {
        binding.buttonEditProfile.setOnClickListener {
            enterEditMode()
        }

        binding.buttonSaveChanges.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun enterEditMode() {
        // Enable editing for the fields
        binding.profileName.isEnabled = true
        binding.profileInstitution.isEnabled = true
        binding.profileFedUnit.isEnabled = true
        // Email is generally not editable this way for security reasons
        binding.profileEmail.isEnabled = false 

        // Toggle button visibility
        binding.buttonEditProfile.visibility = View.GONE
        binding.buttonSaveChanges.visibility = View.VISIBLE
    }

    private fun exitEditMode() {
        // Disable editing
        binding.profileName.isEnabled = false
        binding.profileInstitution.isEnabled = false
        binding.profileFedUnit.isEnabled = false
        binding.profileEmail.isEnabled = false

        // Toggle button visibility
        binding.buttonEditProfile.visibility = View.VISIBLE
        binding.buttonSaveChanges.visibility = View.GONE
    }

    private fun saveProfileChanges() {
        val uid = currentUser?.uid
        if (uid != null) {
            // Create a map of the data to update
            val updatedData = hashMapOf(
                "name" to binding.profileName.text.toString(),
                "institution" to binding.profileInstitution.text.toString(),
                "uf" to binding.profileFedUnit.text.toString()
            )

            firestore.collection("users").document(uid)
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    exitEditMode()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Falha ao atualizar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle the back button on the toolbar
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
