package com.example.bookworm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.models.BookPost

class FeedAdapter(private val bookPosts: List<BookPost>) : RecyclerView.Adapter<FeedAdapter.BookPostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_book_post, parent, false)
        return BookPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookPostViewHolder, position: Int) {
        val bookPost = bookPosts[position]
        holder.bind(bookPost)
    }

    override fun getItemCount(): Int = bookPosts.size

    class BookPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.postTitle)
        private val ratingView: TextView = itemView.findViewById(R.id.postRating)
        private val descView: TextView = itemView.findViewById(R.id.postDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.postImage)

        fun bind(bookPost: BookPost) {
            titleView.text = bookPost.title
            ratingView.text = bookPost.rating.toString()
            descView.text = bookPost.desc

            // Load image using Glide
            Glide.with(itemView.context)
                .load(bookPost.image) // URL or resource ID
                .placeholder(R.drawable.placeholder_book_image) // Optional placeholder
                .into(imageView)
        }
    }
}
