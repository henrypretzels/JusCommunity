package com.example.juscom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.juscom.databinding.ActivityRoomListBinding

class RoomListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomListBinding
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.room_list_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        val rooms = getAllRooms()
        roomAdapter = RoomAdapter(rooms) { room ->
            val intent = Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }

        binding.roomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RoomListActivity)
            adapter = roomAdapter
        }
    }

    private fun getAllRooms(): List<Room> {
        return listOf(
            Room(1, "Direito Civil", "Discussões sobre direito civil, contratos e obrigações", "Civil", 1250),
            Room(2, "Direito Penal", "Debates sobre direito penal e processo penal", "Penal", 980),
            Room(3, "Direito Trabalhista", "Temas de direito do trabalho e previdenciário", "Trabalhista", 750),
            Room(4, "Direito Tributário", "Discussões sobre direito tributário e fiscal", "Tributário", 650),
            Room(5, "Direito Constitucional", "Debates sobre direito constitucional", "Constitucional", 890),
            Room(6, "Direito Administrativo", "Temas de direito administrativo", "Administrativo", 720),
            Room(7, "Direito Empresarial", "Discussões sobre direito empresarial e societário", "Empresarial", 580),
            Room(8, "Direito Ambiental", "Temas de direito ambiental e sustentabilidade", "Ambiental", 420),
            Room(9, "Direito da Família", "Debates sobre direito de família e sucessões", "Família", 680),
            Room(10, "Direito do Consumidor", "Discussões sobre direito do consumidor", "Consumidor", 540)
        ).sortedByDescending { it.subscribersCount }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
