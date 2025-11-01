package com.example.campusvibe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.databinding.ActivityLoginBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        SupabaseClient.initialize(this)

        binding.loginBtn.setOnClickListener {
            if (binding.email.editText?.text.toString().equals("") or
                binding.pass.editText?.text.toString().equals("")
            ) {
                Toast.makeText(this@LoginActivity, "please fill the details", Toast.LENGTH_SHORT)
                    .show()
            } else {
                lifecycleScope.launch {
                    try {
                        val email = binding.email.editText?.text.toString()
                        val password = binding.pass.editText?.text.toString()

                        SupabaseClient.client.auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                        }

                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@LoginActivity,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.createNewAccountBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data: Uri? = intent?.data

        if (data != null && data.scheme == "campusvibe" && data.host == "login") {
            Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_LONG).show()

            // Optional: redirect to home or dashboard
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}