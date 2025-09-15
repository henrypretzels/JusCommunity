package com.example.juscom

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        val logoImageView = findViewById<View>(R.id.logoImageView)
        val loadingTextView = findViewById<View>(R.id.loadingTextView)
        
        // Animação de fade in para o logo
        val logoFadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f)
        logoFadeIn.duration = 1000
        
        // Animação de escala para o logo
        val logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.5f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.5f, 1f)
        logoScaleX.duration = 1000
        logoScaleY.duration = 1000
        
        // Animação de fade in para o texto
        val textFadeIn = ObjectAnimator.ofFloat(loadingTextView, "alpha", 0f, 1f)
        textFadeIn.duration = 500
        textFadeIn.startDelay = 1500
        
        // Executa as animações
        val logoAnimatorSet = AnimatorSet()
        logoAnimatorSet.playTogether(logoFadeIn, logoScaleX, logoScaleY)
        logoAnimatorSet.start()
        
        textFadeIn.start()
        
        // Simula carregamento e navega para a tela de login após 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
