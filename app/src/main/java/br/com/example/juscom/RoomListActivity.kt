package br.com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityRoomListBinding

class RoomListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomListBinding
    private lateinit var roomAdapter: RoomAdapter
    private val viewModel: RoomListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadRooms()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.room_list_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        roomAdapter = RoomAdapter(mutableListOf(), { room ->
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }, { isEmpty ->
            binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        })

        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RoomListActivity)
            adapter = roomAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.rooms.observe(this, Observer { rooms ->
            roomAdapter.updateRooms(rooms)
        })

        viewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, "Falha ao carregar salas: $error", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
