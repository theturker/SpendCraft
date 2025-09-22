package com.alperen.spendcraft

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.alperen.spendcraft.R

class SplashActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        setupClickListeners()
        
        // Auto navigate after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, 3000)
    }
    
    private fun setupClickListeners() {
        findViewById<android.widget.Button>(R.id.btn_get_started).setOnClickListener {
            navigateToMain()
        }
        
        findViewById<android.widget.Button>(R.id.btn_sign_in).setOnClickListener {
            // TODO: Implement sign in logic
            navigateToMain()
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
