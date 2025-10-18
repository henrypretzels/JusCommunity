package br.com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class RoomAdapter(
    private var rooms: MutableList<Room>,
    private val onRoomClick: (Room) -> Unit,
    private val onFilter: (Boolean) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>(), Filterable {

    // Keep a full list for the filter
    private var roomsListFull: List<Room> = ArrayList(rooms)

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.roomCard)
        // The roomImage is in the layout but we don't have a dynamic source for it yet
        // val roomImage: ImageView = itemView.findViewById(R.id.roomImage) 
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
        
        // Since we removed imageResource from Room.kt, we can't set it here anymore.
        // We can add a placeholder or leave it as is in the XML.
        holder.roomName.text = room.name
        holder.roomDescription.text = room.description
        holder.roomCategory.text = room.category
        holder.subscribersCount.text = "${room.subscribersCount} inscritos"
        
        holder.cardView.setOnClickListener {
            onRoomClick(room)
        }
    }

    override fun getItemCount(): Int = rooms.size
    
    // New function to update the adapter's data source
    fun updateRooms(newRooms: List<Room>) {
        rooms.clear()
        rooms.addAll(newRooms)
        roomsListFull = ArrayList(newRooms) // Update the full list for filtering
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = roomFilter

    private val roomFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Room>()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(roomsListFull)
            }
            else {
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
            // This cast is what causes the warning. It's generally safe but noted.
            val resultList = results.values as List<Room>
            rooms.addAll(resultList)
            onFilter(resultList.isEmpty() && !constraint.isNullOrEmpty())
            notifyDataSetChanged()
        }
    }
}
