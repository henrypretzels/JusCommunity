package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // LiveData for user information
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // LiveData for rooms
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> = _rooms

    // LiveData for errors
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadUserData()
        loadRooms()
    }

    fun loadUserData() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _user.value = null
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    _user.value = user
                } else {
                    _user.value = null
                }
            }
            .addOnFailureListener {
                _user.value = null
                _error.value = "Failed to load user information."
            }
    }

    fun loadRooms() {
        firestore.collection("rooms")
            .orderBy("subscribersCount", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                val rooms = result.toObjects(Room::class.java)
                _rooms.value = rooms
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to load rooms: ${exception.message}"
            }
    }
}
