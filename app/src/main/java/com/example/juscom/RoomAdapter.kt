package com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class RoomAdapter(
    private val rooms: List<Room>,
    private val onRoomClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.roomCard)
        val roomImage: ImageView = itemView.findViewById(R.id.roomImage)
        val roomName: TextView = itemView.findViewById(R.id.roomName)
        val roomDescription: TextView = itemView.findViewById(R.id.roomDescription)
        val roomCategory: TextView = itemView.findViewById(R.id.roomCategory)
        val subscribersCount: TextView = itemView.findViewById(R.id.subscribersCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        
        holder.roomImage.setImageResource(room.imageResource)
        holder.roomName.text = room.name
        holder.roomDescription.text = room.description
        holder.roomCategory.text = room.category
        holder.subscribersCount.text = "${room.subscribersCount} inscritos"
        
        holder.cardView.setOnClickListener {
            onRoomClick(room)
        }
    }

    override fun getItemCount(): Int = rooms.size
}
