package com.example.juscom

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juscom.databinding.ActivityRoomDetailBinding

class RoomDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomDetailBinding
    private lateinit var room: Room
    private var isSubscribed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        room = intent.getSerializableExtra("room") as Room
        isSubscribed = room.isSubscribed

        setupToolbar()
        setupRoomInfo()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = room.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRoomInfo() {
        binding.roomImage.setImageResource(room.imageResource)
        binding.roomName.text = room.name
        binding.roomDescription.text = room.description
        binding.roomCategory.text = room.category
        binding.subscribersCount.text = getString(R.string.subscribers_count, room.subscribersCount)
        
        updateSubscribeButton()
    }

    private fun setupClickListeners() {
        binding.subscribeButton.setOnClickListener {
            toggleSubscription()
        }

        binding.viewQaButton.setOnClickListener {
            val intent = Intent(this, QAActivity::class.java)
            intent.putExtra("room", room)
            startActivity(intent)
        }
    }

    private fun toggleSubscription() {
        isSubscribed = !isSubscribed
        
        if (isSubscribed) {
            Toast.makeText(this, R.string.subscribed_success, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.unsubscribed_success, Toast.LENGTH_SHORT).show()
        }
        
        updateSubscribeButton()
    }

    private fun updateSubscribeButton() {
        if (isSubscribed) {
            binding.subscribeButton.text = getString(R.string.unsubscribe_button)
            binding.subscribeButton.setBackgroundColor(getColor(R.color.error_red))
        } else {
            binding.subscribeButton.text = getString(R.string.subscribe_button)
            binding.subscribeButton.setBackgroundColor(getColor(R.color.primary_blue))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
