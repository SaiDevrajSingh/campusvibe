package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.Models.User
import com.example.campusvibe.adapter.SearchAdapter
import com.example.campusvibe.databinding.FragmentSearchBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: SearchAdapter
    var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSearchBinding.inflate(inflater, container, false)
        binding.rv.layoutManager=LinearLayoutManager(requireContext())
        adapter= SearchAdapter(requireContext(), userList)
        binding.rv.adapter=adapter

        viewLifecycleOwner.lifecycleScope.launch {
            fetchAllUsers()
        }

        binding.searchButton.setOnClickListener {
            val text = binding.searchView.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                searchUsers(text)
            }
        }


        return  binding.root
    }

    private suspend fun fetchAllUsers() {
        try {
            val supabase = SupabaseClient.client
            val currentUserId = supabase.auth.currentUserOrNull()?.id
            val response = supabase.postgrest["users"].select()
            val allUsers = response.decodeList<User>()
            userList.clear()
            userList.addAll(allUsers.filter { it.id != currentUserId })
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    private suspend fun searchUsers(name: String) {
        try {
            val supabase = SupabaseClient.client
            val currentUserId = supabase.auth.currentUserOrNull()?.id
            val response = supabase.postgrest["users"].select {
                filter {
                    ilike("name", "%$name%")
                }
            }
            val searchedUsers = response.decodeList<User>()
            userList.clear()
            userList.addAll(searchedUsers.filter { it.id != currentUserId })
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    companion object {

    }
}