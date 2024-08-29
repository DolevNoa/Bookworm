# BookWorm 📚

**BookWorm** is a mobile application designed to help users discover, rate, and recommend books they've read. This app provides a platform for users to share their reading experiences, discover new books, and interact with a community of fellow book lovers.

## 🚀 Project Overview

**BookWorm** allows users to:
- Create and manage personal profiles.
- Share book recommendations, including the book's name, an image, a review, and a rating.
- Edit and delete book recommendations.
- View a feed of book recommendations from all users.
- See their own list of recommended books.
- Authenticate using Firebase.
- Auto-complete book information using the Google Books API.

## 📋 Functional Requirements

### Must Have Features
- **User Profile Management**:
  - User registration and login using Firebase Authentication.
  - Auto-login after the first successful authentication.
  - Logout functionality.
  - Profile page displaying user’s profile picture and name.
  - Edit profile to update the user's name and profile picture.

- **Book Recommendation System**:
  - Ability to create, edit, and delete book recommendations.
  - Recommendations include book name, image, review, and rating.
  - Auto-complete book name and image from Google Books API.
  
- **Content Sharing**:
  - Display all book recommendations in a unified feed where all users can view shared content.
  - Separate view to see the user's own book recommendations.

### Future Improvements
- **Search Engine**: Search for books by title, author, or genre.
- **Save Recommendations**: Option to save a recommendation for later reading.
- **Bookstore Integration**: Link to online bookstores for book purchases.
- **Notifications**: Alerts for new books, reviews, and personalized notifications.
- **Follow Users**: Ability to follow specific users to see their content exclusively.

## 🔍 Use Cases

- **Registration**: Users register for the app using their email.
- **Login**: After registration, users can log in with their credentials.
- **Profile Management**: Users can update their profile, including changing their name and profile picture.
- **Home Feed**: Users can view all book recommendations posted by themselves and others.
- **My List**: Users can view a separate list of their own book recommendations.
- **Create Review**: Users can post a new book recommendation with a name, image, review, and rating.
- **Edit/Delete Review**: Users can update or delete their previously posted book recommendations.

## 📱 App Structure

### Activities
- **MainActivity**: Hosts all the app’s fragments.

### Fragments
- **LoginFragment**: User login screen.
- **RegisterFragment**: User registration screen.
- **ProfileFragment**: Edit user profile (name and picture).
- **SettingsFragment**: Manage profile settings, including logout.
- **FeedFragment**: Displays all book recommendations.
- **MyListFragment**: Displays only the user's own book recommendations.
- **AddBookRecommendationFragment**: Add a new book recommendation.
- **EditBookRecommendationFragment**: Edit an existing book recommendation.
- **BookPostFragment**: Display detailed view of a single book recommendation.
- **BaseBookListFragment**: Base fragment for book lists, inherited by `FeedFragment` and `MyListFragment`.

## 🛠️ Technologies & Libraries

- **Firebase Authentication**: User authentication.
- **Firebase Firestore**: Cloud-based database for storing user data and book recommendations.
- **Google Books API**: Auto-complete book information.
- **Glide**: Image loading and caching.
- **Material Components**: UI components for a modern and intuitive user interface.
- **Fragments and Nav Graph**: For app navigation and managing different screens.

## 🖼️ Mockup & Design

The mockup for the application was created using Figma. The app structure follows the design and layout as planned in the mockup. The design emphasizes ease of navigation, clarity, and an enjoyable user experience.
