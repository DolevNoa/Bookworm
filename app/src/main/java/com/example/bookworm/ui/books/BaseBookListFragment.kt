package com.example.bookworm.ui.common

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
import com.example.bookworm.ui.books.HandleBooksViewModel
import com.example.bookworm.ui.feed.FeedFragment
import com.example.bookworm.ui.mylist.MyListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseBookListFragment : Fragment() {

    protected lateinit var recyclerView: RecyclerView
    protected lateinit var feedAdapter: ListAdapter
    protected lateinit var currentUserId: String
    protected lateinit var progressBar: ProgressBar
    protected lateinit var noRecommendationsText: TextView
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

    protected fun updateAdapter(bookRecommendations: List<BookRecommendation>, userProfiles: Map<String, UserProfile>) {
        feedAdapter.updateData(bookRecommendations, userProfiles)
        progressBar.visibility = View.GONE

        if (bookRecommendations.isEmpty()) {
            noRecommendationsText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            Log.d("BaseBookListFragment", "No recommendations found. Showing noRecommendationsText.")
        } else {
            noRecommendationsText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            Log.d("BaseBookListFragment", "Recommendations found. Hiding noRecommendationsText.")
        }
    }

    protected fun fetchRecommendationsFromViewModel(fetchMethod: suspend () -> List<BookRecommendation>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Switch to Main thread before making UI updates
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.VISIBLE
                }

                val bookRecommendations = fetchMethod()
                val userProfiles = fetchUserProfiles(bookRecommendations.map { it.creator }.distinct())

                // Switch to Main thread before making UI updates
                withContext(Dispatchers.Main) {
                    updateAdapter(bookRecommendations, userProfiles)
                }
            } catch (e: Exception) {
                Log.e("BaseBookListFragment", "Error fetching book recommendations", e)
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
