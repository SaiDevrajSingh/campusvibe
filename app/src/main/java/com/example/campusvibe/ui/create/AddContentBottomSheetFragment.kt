package com.example.campusvibe.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.campusvibe.R
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
            // TODO: Navigate to the create reel screen
            dismiss()
        }

        view.findViewById<View>(R.id.text_view_add_story).setOnClickListener {
            // TODO: Navigate to the create story screen
            dismiss()
        }
    }
}


