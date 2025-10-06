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
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authRepository = AuthRepository()
        sessionManager = UserSessionManager(this)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            if (validateInput()) {
                performRegister()
            }
        }
        
        binding.alreadyHaveAccountButton.setOnClickListener {
            finish() // Volta para a tela de login
        }
    }
    
    private fun performRegister() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = authRepository.register(email, password, name)
            showLoading(false)
            
            result.fold(
                onSuccess = { user: FirebaseUser ->
                    sessionManager.saveUserSession(user)
                    Toast.makeText(this@RegisterActivity, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                },
                onFailure = { exception: Throwable ->
                    Toast.makeText(this@RegisterActivity, "Erro no cadastro: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.registerButton.isEnabled = !show
        binding.registerButton.text = if (show) "Criando conta..." else "Criar conta"
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun validateInput(): Boolean {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
        
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
        
        return true
    }
}
