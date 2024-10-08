package com.example.bookworm.ui.books

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentAddBookRecommendationBinding
import com.example.bookworm.data.models.BookRecommendation
import com.example.bookworm.networking.BookApiService
import com.example.bookworm.networking.BookItem
import com.example.bookworm.networking.BookResponse
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddBookRecommendationFragment : Fragment() {

    private var _binding: FragmentAddBookRecommendationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HandleBooksViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var bookApiService: BookApiService

    private lateinit var imageViewBook: ImageView
    private lateinit var bookNameInput: AutoCompleteTextView
    private lateinit var descriptionInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonSubmitBook: Button
    private lateinit var uploadProgress: ProgressBar

    private var imageUrl: String? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewBook.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Retrofit (HTTP Client - for API)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookApiService = retrofit.create(BookApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setupListeners()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
    }

    private fun initializeViews() {
        imageViewBook = binding.imageViewBook
        bookNameInput = binding.bookNameInput
        descriptionInput = binding.descriptionInput
        ratingBar = binding.ratingBar
        buttonSubmitBook = binding.buttonSubmitBook
        uploadProgress = binding.uploadProgress
    }

    private fun setupListeners() {
        buttonSubmitBook.setOnClickListener {
            submitBookRecommendation()
        }

        imageViewBook.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Setup autocomplete listeners to fetch suggestions from the API
        bookNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length > 2) {
                    searchBooks(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchBooks(query: String) {
        val call = bookApiService.searchBooks(query)
        call.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                val books = response.body()?.items ?: emptyList()
                val titles = books.map { it.volumeInfo.title }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    titles
                )
                bookNameInput.setAdapter(adapter)
                bookNameInput.showDropDown()


                // Handle book selection to display additional info
                bookNameInput.setOnItemClickListener { _, _, position, _ ->
                    val selectedBook = books[position]
                    imageUrl = selectedBook.volumeInfo.imageLinks?.thumbnail
                    displayBookDetails(selectedBook)
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                // Log the error for debugging purposes and Toast a message to the user
                Log.e("BookSearch", "Error fetching books: ${t.message}")
                Toast.makeText(context, "Failed to load book suggestions. Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayBookDetails(book: BookItem) {
        Log.d("BookThumbnail", "Thumbnail URL: ${book.volumeInfo.imageLinks?.thumbnail}")

        val thumbnailUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

        Log.d("BookThumbnail", "Thumbnail URL: $thumbnailUrl")

        Glide.with(requireContext())
            .load(thumbnailUrl)
            .placeholder(R.drawable.placeholder_book_image)
            .error(R.drawable.placeholder_book_image)
            .into(imageViewBook)
    }

    private fun submitBookRecommendation() {
        val bookName = bookNameInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val rating = ratingBar.rating

        if (bookName.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

            val creator = viewModel.getCurrentUserId() ?: ""
            val book = BookRecommendation(
                creator = creator,
                timestamp = Timestamp.now(),
                bookName = bookName,
                description = description,
                rating = rating,
                imageUrl = imageUrl ?: ""
            )
        viewModel.addBookRecommendation(book) { success ->
            if (success) {
                Toast.makeText(context, "Book recommendation added successfully", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(context, "Failed to add book recommendation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearForm() {
        bookNameInput.text.clear()
        descriptionInput.text.clear()
        ratingBar.rating = 0f
        imageViewBook.setImageResource(R.drawable.placeholder_book_image)
        imageUrl = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
