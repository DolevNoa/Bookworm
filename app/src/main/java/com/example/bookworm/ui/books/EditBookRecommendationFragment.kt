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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentEditBookRecommendationBinding
import com.example.bookworm.data.books.BookRecommendation
import com.example.bookworm.services.bookApi.BookApiService
import com.example.bookworm.services.bookApi.BookItem
import com.example.bookworm.services.bookApi.BookResponse
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditBookRecommendationFragment : Fragment() {

    private var _binding: FragmentEditBookRecommendationBinding? = null
    private val binding get() = _binding!!
//    protected val viewModel: HandleBooksViewModel by viewModels()
protected val viewModel: HandleBooksViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var bookApiService: BookApiService
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var imageUrl: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewBook.setImageURI(it)
            uploadImageToFirebaseStorage(it) { url ->
                imageUrl = url
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

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
        _binding = FragmentEditBookRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setupListeners()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        populateFields()
    }


    private fun initializeViews() {
        binding.imageViewBook.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.buttonSubmitBook.setOnClickListener {
            submitBookRecommendation()
        }
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        binding.bookNameInput.addTextChangedListener(object : TextWatcher {
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
                binding.bookNameInput.setAdapter(adapter)
                binding.bookNameInput.showDropDown()

                binding.bookNameInput.setOnItemClickListener { _, _, position, _ ->
                    val selectedBook = books[position]
                    imageUrl = selectedBook.volumeInfo.imageLinks?.thumbnail
                    displayBookDetails(selectedBook)
                }
            }
            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun displayBookDetails(book: BookItem) {
        Log.d("BookThumbnail", "Thumbnail URL: ${book.volumeInfo.imageLinks?.thumbnail}")

        val thumbnailUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")

        Log.d("BookThumbnail", "Thumbnail URL: ${thumbnailUrl}")

        Glide.with(requireContext())
            .load(thumbnailUrl)
            .placeholder(R.drawable.placeholder_book_image)
            .error(R.drawable.placeholder_book_image)
            .into(binding.imageViewBook)
    }

    private fun populateFields() {
        Log.d("EditBookRecommendationFragment", "populateFields for bookRecommendation: ${viewModel.selectedBookRecommendation.value}")
        val selectedBook = viewModel.selectedBookRecommendation.value
        if (selectedBook != null) {
            Log.d("EditBookRecommendation", "Populating fields with: $selectedBook")
            binding.bookNameInput.setText(selectedBook.bookName)
            binding.descriptionInput.setText(selectedBook.description)
            binding.ratingBar.rating = selectedBook.rating
            val existingImageUrl = selectedBook.imageUrl
            Log.d("imageUrl", "populateFields: $existingImageUrl")
            if (existingImageUrl.isEmpty()) {
                // Load default placeholder if no image URL exists
                binding.imageViewBook.setImageResource(R.drawable.placeholder_book_image)
            } else {
                // Load existing image if URL exists
                Glide.with(requireContext())
                    .load(existingImageUrl)
                    .placeholder(R.drawable.placeholder_book_image)
                    .error(R.drawable.placeholder_book_image)
                    .into(binding.imageViewBook)
            }
        } else {
            Log.d("EditBookRecommendation", "No book recommendation to populate")
        }
    }

    private fun submitBookRecommendation() {
        val bookName = binding.bookNameInput.text.toString().trim()
        val description = binding.descriptionInput.text.toString().trim()
        val rating = binding.ratingBar.rating

        if (bookName.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Preserve the existing imageUrl if it was not changed
        val updatedImageUrl = imageUrl ?: viewModel.selectedBookRecommendation.value?.imageUrl ?: ""
        Log.d("imageUrl", "submitBookRecommendation: $updatedImageUrl")

        val book = viewModel.selectedBookRecommendation.value?.copy(
            bookName = bookName,
            description = description,
            rating = rating,
            imageUrl = updatedImageUrl
        ) ?: return

        // Perform the update operation in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = viewModel.editBookRecommendation(book)
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(context, "Book recommendation updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(context, "Failed to update book recommendation", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error updating book recommendation", Toast.LENGTH_SHORT).show()
                    Log.e("EditBookRecommendation", "Error updating book recommendation", e)
                }
            }
        }
    }


    private fun uploadImageToFirebaseStorage(uri: Uri, onComplete: (String?) -> Unit) {
        val fileRef = storageRef.child("book_images/${System.currentTimeMillis()}.jpg")
        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onComplete(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                onComplete(null)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = EditBookRecommendationFragment()
    }
}
