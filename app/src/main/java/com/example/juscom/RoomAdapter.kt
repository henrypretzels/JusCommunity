package com.example.juscom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.juscom.databinding.ItemRoomBinding

class RoomAdapter(
    private var rooms: MutableList<Room>,
    private val onRoomClick: (Room) -> Unit,
    private val onFilter: (Boolean) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>(), Filterable {

    private var roomsListFull: List<Room> = ArrayList(rooms)

    inner class RoomViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.roomName.text = room.name
            binding.root.setOnClickListener { onRoomClick(room) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    override fun getFilter(): Filter = roomFilter

    private val roomFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Room>()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(roomsListFull)
            } else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (room in roomsListFull) {
                    if (room.name.lowercase().contains(filterPattern) ||
                        room.description.lowercase().contains(filterPattern) ||
                        room.category.lowercase().contains(filterPattern)) {
                        filteredList.add(room)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            rooms.clear()
            val resultList = results.values as List<Room>
            rooms.addAll(resultList)
            onFilter(resultList.isEmpty() && !constraint.isNullOrEmpty())
            notifyDataSetChanged()
        }
    }
    fun updateData(newRooms: List<Room>) {
        rooms.clear()
        rooms.addAll(newRooms)
        notifyDataSetChanged()
    }
}