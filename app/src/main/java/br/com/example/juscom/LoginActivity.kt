package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.example.juscom.databinding.ActivityLoginBinding
import br.com.example.juscom.manager.UserSessionManager
import br.com.example.juscom.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: UserSessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authRepository = AuthRepository()
        sessionManager = UserSessionManager(this)
        
        checkIfUserIsLoggedIn()
        setupClickListeners()
    }
    
    private fun checkIfUserIsLoggedIn() {
        if (sessionManager.isUserLoggedIn() && authRepository.isUserLoggedIn()) {
            navigateToHome()
        }
    }
    
    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            if (validateInput()) {
                performLogin()
            }
        }
        
        binding.createAccountButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        
        binding.forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, PasswordRecoveryActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun validateInput(): Boolean {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        
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
        
        return true
    }
    
    private fun performLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        
        showLoading(true)
        
        lifecycleScope.launch {
            val result = authRepository.login(email, password)
            showLoading(false)
            
            result.fold(
                onSuccess = { user: FirebaseUser ->
                    sessionManager.saveUserSession(user)
                    Toast.makeText(this@LoginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                },
                onFailure = { exception: Throwable ->
                    Toast.makeText(this@LoginActivity, "Erro no login: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.loginButton.isEnabled = !show
        binding.loginButton.text = if (show) "Entrando..." else "Entrar"
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
