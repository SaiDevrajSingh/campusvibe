package com.example.campusvibe.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.campusvibe.R
import com.example.campusvibe.data.AuthViewModel
import com.example.campusvibe.data.AuthState
import com.example.campusvibe.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.SUCCESS -> findNavController().navigate(R.id.action_login_to_home)
                is AuthState.ERROR -> Snackbar.make(binding.root, it.message ?: "", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val username = binding.usernameEditText.text.toString()
            val fullName = binding.fullNameEditText.text.toString()

            if (email.isNotBlank() && password.isNotBlank() && username.isNotBlank() && fullName.isNotBlank()) {
                viewModel.signUp(email, password, username, fullName)
            } else {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.backToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

