package com.example.campusvibe.Models

data class User(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val image: String? = null,
    val followers: ArrayList<String> = ArrayList(),
    val following: ArrayList<String> = ArrayList()
)
