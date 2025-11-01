package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.campusvibe.Models.Post
import com.example.campusvibe.Models.User
import com.example.campusvibe.PostDetailActivity
import com.example.campusvibe.R
import com.example.campusvibe.databinding.PostRvBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.max
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private val context: Context, 
    private var postList: ArrayList<Post>,
    private val currentUserId: String
) : RecyclerView.Adapter<PostAdapter.MyHolder>() {
    
    private val TAG = "PostAdapter"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    // Track which posts are liked by the current user
    private val likedPosts = hashSetOf<Long>()
    
    // Cache user data to avoid repeated database calls
    private val userCache = mutableMapOf<String, User>()
    
    // Listener for when the like count changes
    var onLikeCountChanged: ((postId: Long, newLikeCount: Int) -> Unit)? = null
    
    init {
        // Initialize liked posts set with posts already liked by the user
        postList.forEach { post ->
            post.id?.let { postId ->
                if (post.likes > 0) {
                    likedPosts.add(postId)
                }
            }
        }
    }
    
    inner class MyHolder(val binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val post = postList[position]
        val postId = post.id ?: return
        
        Log.d(TAG, "Binding post at position $position: id=$postId, userId=${post.userId}, imageUrl=${post.imageUrl}")
        
        // Set caption
        holder.binding.caption.text = post.caption ?: ""
        
        // Format and set post time
        holder.binding.time.text = formatTimestamp(post.createdAt)
        
        // Load user data asynchronously
        loadUserData(holder, post.userId)

        holder.binding.postImage.setOnClickListener {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("postUrl", post.imageUrl)
            intent.putExtra("caption", post.caption)

            coroutineScope.launch {
                val user = userCache[post.userId] ?: fetchUserFromDatabase(post.userId)
                user?.let {
                    intent.putExtra("profileImageUrl", it.image)
                    intent.putExtra("username", it.username)
                    context.startActivity(intent)
                }
            }
        }
        
        // Set initial like state
        val isLiked = likedPosts.contains(postId)
        updateLikeButton(holder.binding.like, isLiked)
        
        // Set up like button click listener
        holder.binding.like.setOnClickListener {
            coroutineScope.launch {
                try {
                    val newLikeStatus = !isLiked
                    val newLikeCount = if (newLikeStatus) post.likes + 1 else max(0, post.likes - 1)
                    
                    // Update local data first
                    if (newLikeStatus) {
                        likedPosts.add(postId)
                    } else {
                        likedPosts.remove(postId)
                    }
                    
                    // Update UI immediately for better responsiveness
                    updateLikeButton(holder.binding.like, newLikeStatus)
                    
                    // Update the post in the list
                    val updatedPost = post.copy(likes = newLikeCount)
                    postList[position] = updatedPost
                    
                    // Notify listener if any (e.g., to update the data source)
                    onLikeCountChanged?.invoke(postId, newLikeCount)
                    
                    // Update the database
                    updateLikeInDatabase(postId, newLikeCount)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error toggling like: ${e.message}", e)
                    // Revert UI on error
                    updateLikeButton(holder.binding.like, isLiked)
                }
            }
        }
        
        // Set up share button
        holder.binding.share.setOnClickListener {
            try {
                val shareUrl = post.imageUrl.let { url ->
                    if (!url.startsWith("http")) {
                        SupabaseClient.getPostImageUrl(url)
                    } else {
                        url
                    }
                }
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Check out this post: $shareUrl")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
            } catch (e: Exception) {
                Log.e(TAG, "Error sharing post", e)
            }
        }
        
        // Load post image using Glide with proper error handling
        try {
            // Get the proper URL for the post image
            val rawImageUrl = post.imageUrl
            Log.d(TAG, "Raw image URL from database: $rawImageUrl")
            
            val imageUrl = when {
                rawImageUrl.startsWith("http://") || rawImageUrl.startsWith("https://") -> {
                    Log.d(TAG, "Using full URL as-is")
                    rawImageUrl
                }
                rawImageUrl.contains("/storage/v1/object/public/") -> {
                    // Already a storage URL, just prepend base URL if needed
                    Log.d(TAG, "Detected storage path, constructing full URL")
                    if (rawImageUrl.startsWith("/")) {
                        "https://ufkrqjcfwnfvkfmspiza.supabase.co$rawImageUrl"
                    } else {
                        rawImageUrl
                    }
                }
                else -> {
                    // Assume it's just a filename in the posts bucket
                    Log.d(TAG, "Treating as filename in posts bucket")
                    SupabaseClient.getPostImageUrl(rawImageUrl)
                }
            }
            
            Log.d(TAG, "Final image URL: $imageUrl for post $postId")
            
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .error(android.R.drawable.ic_menu_report_image)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Failed to load image for post $postId from URL: $imageUrl", e)
                        e?.logRootCauses(TAG)
                        return false
                    }
                    
                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(TAG, "Successfully loaded image for post $postId")
                        return false
                    }
                })
                .into(holder.binding.postImage)
                
        } catch (e: Exception) {
            Log.e(TAG, "Error loading post image: ${e.message}", e)
            holder.binding.postImage.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }
    
    /**
     * Update the like button appearance based on like status
     */
    private fun updateLikeButton(likeButton: android.widget.ImageView, isLiked: Boolean) {
        val drawableRes = if (isLiked) android.R.drawable.btn_star_big_on 
                         else android.R.drawable.btn_star_big_off
        likeButton.setImageResource(drawableRes)
        // Add animation for better UX
        likeButton.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(150)
            .withEndAction {
                likeButton.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    
    /**
     * Update the like status in the database
     */
    private suspend fun updateLikeInDatabase(postId: Long, newLikeCount: Int) {
        return withContext(Dispatchers.IO) {
            try {
                val supabase = SupabaseClient.client
                
                // Update the like count
                supabase.postgrest[Post.TABLE]
                    .update({
                        set("likes", newLikeCount)
                    }) {
                        filter {
                            eq("id", postId)
                        }
                    }
                
                Log.d(TAG, "Successfully updated like status for post $postId to $newLikeCount likes")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating like in database: ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * Update the posts list and refresh the adapter
     */
    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        
        // Update liked posts set
        likedPosts.clear()
        postList.forEach { post ->
            post.id?.let { postId ->
                if (post.likes > 0) {
                    likedPosts.add(postId)
                }
            }
        }
        
        notifyDataSetChanged()
    }
    
    /**
     * Load user data for a post
     */
    private fun loadUserData(holder: MyHolder, userId: String) {
        coroutineScope.launch {
            try {
                // Check cache first
                val user = userCache[userId] ?: fetchUserFromDatabase(userId)
                
                if (user != null) {
                    // Cache the user data
                    userCache[userId] = user
                    
                    // Update UI on main thread
                    withContext(Dispatchers.Main) {
                        // Set username
                        holder.binding.name.text = user.username ?: "Unknown User"
                        
                        // Load profile image
                        if (!user.image.isNullOrEmpty()) {
                            val avatarUrl = if (user.image!!.startsWith("http")) {
                                user.image
                            } else {
                                SupabaseClient.getAvatarUrl(user.image!!)
                            }
                            
                            Glide.with(context)
                                .load(avatarUrl)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.binding.profileImage)
                        } else {
                            holder.binding.profileImage.setImageResource(R.drawable.user)
                        }
                    }
                } else {
                    // User not found, show defaults
                    holder.binding.name.text = "Unknown User"
                    holder.binding.profileImage.setImageResource(R.drawable.user)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data: ${e.message}", e)
                holder.binding.name.text = "Unknown User"
                holder.binding.profileImage.setImageResource(R.drawable.user)
            }
        }
    }
    
    /**
     * Fetch user data from database
     */
    private suspend fun fetchUserFromDatabase(userId: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching user data for userId: $userId")
                val response = SupabaseClient.client.postgrest["users"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingle<User>()
                Log.d(TAG, "Successfully fetched user: ${response.username}, image: ${response.image}")
                response
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user from database for userId $userId: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * Format timestamp to relative time (e.g., "2 hours ago")
     */
    private fun formatTimestamp(timestamp: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(timestamp) ?: return timestamp
            
            val now = System.currentTimeMillis()
            val diff = now - date.time
            
            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                diff < 604800000 -> "${diff / 86400000}d ago"
                else -> "${diff / 604800000}w ago"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting timestamp: ${e.message}", e)
            timestamp
        }
    }
}