package com.example.juscom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juscom.databinding.ActivityPwrecoveryBinding

class PasswordRecoveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPwrecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPwrecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToLoginButton.setOnClickListener {
            finish()
        }

        binding.sendRecoveryButton.setOnClickListener {
            // TODO: Add logic to validate email and send recovery link
            Toast.makeText(this, R.string.recovery_link_sent, Toast.LENGTH_SHORT).show()
        }
    }
}