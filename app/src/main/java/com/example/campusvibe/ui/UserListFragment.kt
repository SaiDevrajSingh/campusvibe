package com.example.campusvibe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentUserListBinding
import com.example.campusvibe.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserListFragment : Fragment() {

    private lateinit var binding: FragmentUserListBinding
    private lateinit var adapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter { user ->
            val action = UserListFragmentDirections.actionUserListFragmentToChatFragment(user.uid)
            findNavController().navigate(action)
        }

        binding.rvUserList.adapter = adapter
        binding.rvUserList.layoutManager = LinearLayoutManager(context)

        fetchUsers()
    }

    private fun fetchUsers() {
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.map { document ->
                    User(
                        uid = document.id,
                        displayName = document.getString("displayName"),
                        photoUrl = document.getString("photoUrl")
                    )
                }
                adapter.submitList(users)
            }
    }
}
