package br.com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.example.juscom.databinding.ItemBadgeBinding

class BadgeAdapter(private val badges: List<EarnedBadge>) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount() = badges.size

    inner class BadgeViewHolder(private val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(earnedBadge: EarnedBadge) {
            binding.badgeName.text = earnedBadge.badge?.name

            // Get the drawable resource ID from the badge's iconUrl field
            val context = binding.root.context
            val resourceId = context.resources.getIdentifier(earnedBadge.badge?.iconUrl, "drawable", context.packageName)

            if (resourceId != 0) {
                binding.badgeIcon.setImageResource(resourceId)
            } else {
                // Optionally set a default icon if the resource is not found
                binding.badgeIcon.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }
}
