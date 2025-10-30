package com.example.campusvibe

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.Models.User
import com.example.campusvibe.databinding.ActivitySignUpBinding
import com.example.campusvibe.utils.USER_PROFILE_FOLDER
import com.example.campusvibe.utils.SupabaseClient
import com.example.campusvibe.utils.uploadImage
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            lifecycleScope.launch {
                imageUrl = uploadImage(this@SignUpActivity, it, USER_PROFILE_FOLDER)
                if (imageUrl != null) {
                    binding.profileImage.setImageURI(it)
                } else {
                    Toast.makeText(this@SignUpActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.login.text = Html.fromHtml("<font color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login</font>", Html.FROM_HTML_MODE_COMPACT)
        } else {
            binding.login.text = Html.fromHtml("<font color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login</font>")
        }
        binding.signUpBtn.setOnClickListener {
            if (binding.name.editText?.text.toString().equals("") or
                binding.email.editText?.text.toString().equals("") or
                binding.password.editText?.text.toString().equals("")
            ) {
                Toast.makeText(this@SignUpActivity, "Please fill the all details", Toast.LENGTH_SHORT)
                    .show()
            } else {
                lifecycleScope.launch {
                    try {
                        val supabase = SupabaseClient.client
                        val authenticatedUser = supabase.auth.signUpWith(Email) {
                            email = binding.email.editText?.text.toString()
                            password = binding.password.editText?.text.toString()
                        }

                        authenticatedUser?.let {
                            val user = User(
                                id = it.id,
                                name = binding.name.editText?.text.toString(),
                                email = binding.email.editText?.text.toString(),
                                image = imageUrl
                            )
                            supabase.postgrest["users"].insert(user)
                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        } ?: run {
                            Toast.makeText(this@SignUpActivity, "Sign up failed", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this@SignUpActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.addImage.setOnClickListener{
            launcher.launch("image/*")
        }
        binding.login.setOnClickListener{
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }
}
