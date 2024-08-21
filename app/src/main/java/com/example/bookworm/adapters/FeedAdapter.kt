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

class FeedAdapter(private var bookRecommendations: List<BookRecommendation>,
                  private val onEditClick: (BookRecommendation) -> Unit,
                  private val onDeleteClick: (BookRecommendation) -> Unit,
                  private val currentUserId: String) : RecyclerView.Adapter<FeedAdapter.BookPostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_book_post, parent, false)
        return BookPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookPostViewHolder, position: Int) {
        val bookRecommendation = bookRecommendations[position]
        holder.bind(bookRecommendation)
        // Show or hide edit/delete buttons based on user ownership
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
        val editButton: Button = itemView.findViewById(R.id.editButton) // Correct ID
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton) // Correct ID

        fun bind(bookRecommendation: BookRecommendation) {
            titleView.text = bookRecommendation.bookName
            userNameView.text = bookRecommendation.creator
            ratingView.rating = bookRecommendation.rating
            descView.text = bookRecommendation.description
            dateCreatedView.text = bookRecommendation.timestamp.toDate().toString()
            // Load image using Glide
            Glide.with(itemView.context)
                .load(bookRecommendation.imageUrl)
                .placeholder(R.drawable.placeholder_book_image)
                .error(R.drawable.placeholder_book_image)
                .into(imageView)
        }
        }
    }