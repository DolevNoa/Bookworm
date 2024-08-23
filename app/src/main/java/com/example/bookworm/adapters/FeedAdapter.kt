package com.example.bookworm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.data.books.BookRecommendation
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.bookworm.data.books.UserProfile
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class FeedAdapter(
    private var bookRecommendations: List<BookRecommendation>,
    private var userProfiles: Map<String, UserProfile>,
    private val onEditClick: (BookRecommendation) -> Unit,
    private val onDeleteClick: (BookRecommendation) -> Unit,
    private val currentUserId: String,
) : RecyclerView.Adapter<FeedAdapter.BookPostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPostViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_book_post, parent, false)
        return BookPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookPostViewHolder, position: Int) {
        val bookRecommendation = bookRecommendations[position]
        holder.bind(
            bookRecommendation,
            userProfiles[bookRecommendation.creator]
        ) // Pass user profile to bind method

        if (bookRecommendation.creator == currentUserId) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }

        // Set up click listeners for edit and delete buttons
        holder.editButton.setOnClickListener { onEditClick(bookRecommendation) }
        holder.deleteButton.setOnClickListener { onDeleteClick(bookRecommendation) }
    }

    override fun getItemCount(): Int = bookRecommendations.size


    fun updateData(
        newBookRecommendations: List<BookRecommendation>,
        newUserProfiles: Map<String, UserProfile>,
    ) {
        bookRecommendations = newBookRecommendations
        userProfiles = newUserProfiles
        notifyDataSetChanged()

    }

    class BookPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.postTitle)
        private val ratingView: AppCompatRatingBar = itemView.findViewById(R.id.postRating)
        private val descView: TextView = itemView.findViewById(R.id.postDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.postImage)
        private val dateCreatedView: TextView = itemView.findViewById(R.id.postCreatedDate)
        private val userNameView: TextView = itemView.findViewById(R.id.postUserName)
        private val userImageView: ImageView =
            itemView.findViewById(R.id.postProfileImage) // New ImageView for user's profile pic
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(bookRecommendation: BookRecommendation, userProfile: UserProfile?) {
            titleView.text = bookRecommendation.bookName
            ratingView.rating = bookRecommendation.rating
            descView.text = bookRecommendation.description
            dateCreatedView.text = formatTimestamp(bookRecommendation.timestamp)

            // Load book image using Glide
            Glide.with(itemView.context)
                .load(bookRecommendation.imageUrl)
                .placeholder(R.drawable.placeholder_book_image)
                .error(R.drawable.placeholder_book_image)
                .into(imageView)

            // Set user's name and profile picture if available
            if (userProfile != null) {
                userNameView.text = userProfile.fullName
                Glide.with(itemView.context)
                    .load(userProfile.photoUrl)
                    .placeholder(R.drawable.placeholder_user_image)
                    .error(R.drawable.placeholder_user_image) // Display placeholder if image is missing
                    .into(userImageView)
            } else {
                userNameView.text = bookRecommendation.creator // fallback to userId
                userImageView.setImageResource(R.drawable.placeholder_book_image)
            }
        }

        // Function to format the Timestamp
        private fun formatTimestamp(timestamp: Timestamp): String {
            val date = timestamp.toDate()
            val formatter = SimpleDateFormat("hh:mm a MMM dd yyyy", Locale.getDefault())
            return formatter.format(date)
        }    }
}