package br.com.example.juscom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class RoomListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> = _rooms

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadRooms() {
        db.collection("rooms")
            .get()
            .addOnSuccessListener { result ->
                val roomList = result.toObjects(Room::class.java)
                _rooms.postValue(roomList)
            }
            .addOnFailureListener { exception ->
                _error.postValue("Error getting documents: ${exception.message}")
            }
    }
}
