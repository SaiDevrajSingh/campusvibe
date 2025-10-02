package com.example.campusvibe.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.campusvibe.R
import com.example.campusvibe.ui.story.AddStoryActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddContentBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_content_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.text_view_add_post).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateFragment())
                .commit()
            dismiss()
        }

        view.findViewById<View>(R.id.text_view_add_reel).setOnClickListener {
            // TODO: Implement reel creation activity
            // For now, show a message that reels are not implemented yet
            android.widget.Toast.makeText(requireContext(), "Reels feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
            dismiss()
        }

        view.findViewById<View>(R.id.text_view_add_story).setOnClickListener {
            startActivity(Intent(requireContext(), AddStoryActivity::class.java))
            dismiss()
        }
    }
}


