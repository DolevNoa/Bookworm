package com.example.bookworm.ui.books

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.adapters.ListAdapter
import com.example.bookworm.data.models.BookRecommendation
import com.example.bookworm.data.models.UserProfile
import com.example.bookworm.ui.feed.FeedFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseBookListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: ListAdapter
    protected lateinit var currentUserId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var noRecommendationsText: TextView
    protected val viewModel: HandleBooksViewModel by activityViewModels()

    abstract fun fetchBookRecommendations()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)
        progressBar = view.findViewById(R.id.paginationProgressBar)
        noRecommendationsText = view.findViewById(R.id.noRecommendationsText)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        feedAdapter = ListAdapter(
            bookRecommendations = emptyList(),
            userProfiles = emptyMap(),
            onEditClick = { bookRecommendation ->
                viewModel.selectedBookRecommendation.value = bookRecommendation
                val action = when (this) {
                    is FeedFragment -> R.id.action_feedFragment_to_editBookRecommendationFragment
                    is MyListFragment -> R.id.action_myListFragment_to_editBookRecommendationFragment
                    else -> throw IllegalArgumentException("Unknown Fragment")
                }
                findNavController().navigate(action)
            },
            onDeleteClick = { bookRecommendation ->
                CoroutineScope(Dispatchers.Main).launch {
                    val success = viewModel.deleteBookRecommendation(bookRecommendation)
                    if (success) {
                        Toast.makeText(context, "Book recommendation deleted successfully", Toast.LENGTH_SHORT).show()
                        fetchBookRecommendations()
                    } else {
                        Toast.makeText(context, "Failed to delete book recommendation", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            currentUserId = currentUserId
        )
        recyclerView.adapter = feedAdapter
        fetchBookRecommendations()
        return view
    }

    private fun updateAdapter(bookRecommendations: List<BookRecommendation>, userProfiles: Map<String, UserProfile>) {
        CoroutineScope(Dispatchers.Main).launch {
            feedAdapter.updateData(bookRecommendations, userProfiles)
            progressBar.visibility = View.GONE

            if (bookRecommendations.isEmpty()) {
                noRecommendationsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                Log.d(
                    "BaseBookListFragment",
                    "No recommendations found. Showing noRecommendationsText."
                )
            } else {
                noRecommendationsText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                Log.d(
                    "BaseBookListFragment",
                    "Recommendations found. Hiding noRecommendationsText."
                )
            }
        }
    }

    protected fun fetchRecommendationsFromViewModel(fetchMethod: suspend () -> List<BookRecommendation>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Show progress bar
                progressBar.visibility = View.VISIBLE

                // Fetch data on the IO dispatcher
                val bookRecommendations = withContext(Dispatchers.IO) { fetchMethod() }
                val userProfiles = withContext(Dispatchers.IO) { fetchUserProfiles(bookRecommendations.map { it.creator }.distinct()) }

                // Update the adapter on the main thread
                updateAdapter(bookRecommendations, userProfiles)
            } catch (e: Exception) {
                // Handle the error and update the UI on the main thread
                Log.e("BaseBookListFragment", "Error fetching book recommendations", e)
                progressBar.visibility = View.GONE
                noRecommendationsText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
    }



    private suspend fun fetchUserProfiles(userIds: List<String>): Map<String, UserProfile> {
        val userProfiles = mutableMapOf<String, UserProfile>()
        userIds.forEach { userId ->
            val profile = viewModel.getUserProfile(userId)
            if (profile != null) {
                userProfiles[userId] = profile
            }
        }
        return userProfiles
    }
}
