package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "User not authenticated."
            return
        }

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                _user.value = document.toObject(User::class.java)
            }
            .addOnFailureListener { exception ->
                _error.value = "Failed to fetch user data: ${exception.message}"
            }
    }

    fun updateUserData(name: String, institution: String, uf: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "User not authenticated."
            return
        }

        val userUpdates = mapOf(
            "name" to name,
            "institution" to institution,
            "uf" to uf
        )

        firestore.collection("users").document(userId).update(userUpdates)
            .addOnSuccessListener {
                _updateResult.value = true
            }
            .addOnFailureListener { e ->
                _error.value = "Failed to update profile: ${e.message}"
                _updateResult.value = false
            }
    }
}
