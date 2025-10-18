package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.example.juscom.databinding.ActivityRegisterBinding
import br.com.example.juscom.manager.UserSessionManager
import br.com.example.juscom.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: UserSessionManager
    private lateinit var firestore: FirebaseFirestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authRepository = AuthRepository()
        sessionManager = UserSessionManager(this)
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            if (validateInput()) {
                performRegister()
            }
        }
        
        binding.alreadyHaveAccountButton.setOnClickListener {
            finish() // Go back to the login screen
        }
    }
    
    private fun performRegister() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val institution = binding.institutionEditText.text.toString().trim()
        val fedUnit = binding.fedUnitEditText.text.toString().trim()
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = authRepository.register(email, password, name)
            
            result.fold(
                onSuccess = { user: FirebaseUser ->
                    // Auth user created, now create the profile in Firestore
                    createUserProfile(user, name, email, institution, fedUnit)
                },
                onFailure = { exception: Throwable ->
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, "Erro no cadastro: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun createUserProfile(user: FirebaseUser, name: String, email: String, institution: String, uf: String) {
        val uid = user.uid
        // Create a data class or a map for the user profile
        val userProfile = hashMapOf(
            "name" to name,
            "email" to email,
            "institution" to institution,
            "uf" to uf,
            "level" to 1,      // Initial level
            "points" to 0       // Initial points
        )

        // Save the profile to Firestore
        firestore.collection("users").document(uid)
            .set(userProfile)
            .addOnSuccessListener {
                showLoading(false)
                sessionManager.saveUserSession(user)
                Toast.makeText(this@RegisterActivity, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Toast.makeText(this@RegisterActivity, "Erro ao salvar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                // Optional: Delete the created auth user if firestore fails, to keep things consistent
            }
    }
    
    private fun showLoading(show: Boolean) {
        binding.registerButton.isEnabled = !show
        binding.registerButton.text = if (show) "Criando conta..." else "Criar conta"
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun validateInput(): Boolean {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
        val institution = binding.institutionEditText.text.toString().trim()
        val fedUnit = binding.fedUnitEditText.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            binding.nameEditText.error = getString(R.string.error_field_required)
            return false
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.error = getString(R.string.error_field_required)
            return false
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = getString(R.string.error_invalid_email)
            return false
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = getString(R.string.error_field_required)
            return false
        }
        
        if (password.length < 6) {
            binding.passwordEditText.error = getString(R.string.error_password_short)
            return false
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordEditText.error = getString(R.string.error_field_required)
            return false
        }
        
        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = getString(R.string.error_password_mismatch)
            return false
        }

        if (TextUtils.isEmpty(institution)) {
            binding.institutionEditText.error = getString(R.string.error_field_required)
            return false
        }

        if (TextUtils.isEmpty(fedUnit)) {
            binding.fedUnitEditText.error = getString(R.string.error_field_required)
            return false
        }
        
        return true
    }
}
