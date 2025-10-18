package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityRoomListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RoomListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomListBinding
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        setupToolbar()
        setupRecyclerView()
        loadAllRooms()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.room_list_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with an empty list
        roomAdapter = RoomAdapter(mutableListOf(), { room ->
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }, { isEmpty ->
            // This can be used to show a 'no rooms found' message if needed
            binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        })

        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RoomListActivity)
            adapter = roomAdapter
        }
    }

    private fun loadAllRooms() {
        // Fetch all rooms from Firestore, ordered by name
        firestore.collection("rooms")
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val rooms = result.toObjects(Room::class.java)
                roomAdapter.updateRooms(rooms)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Falha ao carregar salas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
