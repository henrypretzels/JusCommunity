package com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juscom.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            if (validateInput()) {
                // Simula registro bem-sucedido e navega para home
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        binding.alreadyHaveAccountButton.setOnClickListener {
            finish() // Volta para a tela de login
        }
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
