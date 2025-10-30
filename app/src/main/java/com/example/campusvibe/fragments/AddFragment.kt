package com.example.campusvibe.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.campusvibe.Post.PostActivity
import com.example.campusvibe.Post.ReelsActivity
import com.example.campusvibe.Post.AddStoryActivity
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddBinding.inflate(inflater,container,false)
        binding.post.setOnClickListener{
            activity?.startActivity(Intent(requireContext(), PostActivity::class.java))
            activity?.finish()
        }
        binding.story.setOnClickListener{
            activity?.startActivity(Intent(requireContext(), AddStoryActivity::class.java))
        }
        binding.reel.setOnClickListener{
            activity?.startActivity(Intent(requireContext(), ReelsActivity::class.java))
        }
        return binding.root
    }

    companion object {


    }

}