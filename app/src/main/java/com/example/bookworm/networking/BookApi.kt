package com.example.bookworm.networking

data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val description: String?,
    val imageLinks: ImageLinks?,
    val averageRating: Float?,
    val ratingsCount: Int?
)

data class ImageLinks(
    val thumbnail: String
)
