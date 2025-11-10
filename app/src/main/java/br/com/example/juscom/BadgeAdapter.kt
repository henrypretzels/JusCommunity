package br.com.example.juscom

import android.content.Intent
import android.view.LayoutInflater
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
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val earnedBadge = badges[position]
                    val context = itemView.context
                    val intent = Intent(context, BadgeDetailActivity::class.java).apply {
                        putExtra("BADGE_NAME", earnedBadge.badge?.name)
                        putExtra("BADGE_DESCRIPTION", earnedBadge.badge?.description)
                        putExtra("BADGE_ICON", earnedBadge.badge?.iconUrl)
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(earnedBadge: EarnedBadge) {
            binding.badgeName.text = earnedBadge.badge?.name

            val context = binding.root.context
            val resourceId = context.resources.getIdentifier(earnedBadge.badge?.iconUrl, "drawable", context.packageName)

            if (resourceId != 0) {
                binding.badgeIcon.setImageResource(resourceId)
            } else {
                binding.badgeIcon.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }
}
