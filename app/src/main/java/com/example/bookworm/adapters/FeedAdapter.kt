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

class FeedAdapter(private var bookRecommendations: List<BookRecommendation>) : RecyclerView.Adapter<FeedAdapter.BookPostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_book_post, parent, false)
        return BookPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookPostViewHolder, position: Int) {
        val bookRecommendation = bookRecommendations[position]
        holder.bind(bookRecommendation)
    }

    override fun getItemCount(): Int = bookRecommendations.size

    fun updateData(newBookRecommendations: List<BookRecommendation>) {
        bookRecommendations = newBookRecommendations
        notifyDataSetChanged()
    }

    class BookPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.postTitle)
        private val ratingView: AppCompatRatingBar = itemView.findViewById(R.id.postRating)
        private val descView: TextView = itemView.findViewById(R.id.postDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.postImage)
        private val dateCreatedView: TextView = itemView.findViewById(R.id.postCreatedDate)
        private val userNameView: TextView = itemView.findViewById(R.id.postUserName)

        fun bind(bookRecommendation: BookRecommendation) {
            titleView.text = bookRecommendation.bookName
            userNameView.text = bookRecommendation.creator
            ratingView.rating = bookRecommendation.rating
            descView.text = bookRecommendation.description
            dateCreatedView.text = bookRecommendation.timestamp.toDate().toString()
            // Load image using Glide
            Glide.with(itemView.context)
                .load(bookRecommendation.imageUrl) // URL or resource ID
                .placeholder(R.drawable.placeholder_book_image) // Optional placeholder
                .into(imageView)
        }
    }
}
