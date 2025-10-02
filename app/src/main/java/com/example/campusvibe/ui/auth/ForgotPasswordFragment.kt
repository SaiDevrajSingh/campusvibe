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
import com.example.campusvibe.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.SUCCESS -> {
                    Snackbar.make(binding.root, "Reset password email sent", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                }
                is AuthState.ERROR -> Snackbar.make(binding.root, it.message ?: "", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            viewModel.resetPassword(email)
        }

        binding.backToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

