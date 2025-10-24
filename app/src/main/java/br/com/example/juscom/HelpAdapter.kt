package br.com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HelpAdapter(private val helpItems: MutableList<HelpItem>) : RecyclerView.Adapter<HelpAdapter.HelpViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            HelpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_help, parent, false)
        return HelpViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        val helpItem = helpItems[position]
        holder.bind(helpItem)
    }

    override fun getItemCount(): Int {
        return helpItems.size
    }

    fun updateHelpItems(newHelpItems: List<HelpItem>) {
        helpItems.clear()
        helpItems.addAll(newHelpItems)
        notifyDataSetChanged()
    }

    inner class HelpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.question_textview)
        private val answerTextView: TextView = itemView.findViewById(R.id.answer_textview)
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.content_layout)
        private val headerLayout: RelativeLayout = itemView.findViewById(R.id.header_layout)
        private val arrowImageView: ImageView = itemView.findViewById(R.id.arrow_imageview)

        fun bind(helpItem: HelpItem) {
            questionTextView.text = helpItem.question
            answerTextView.text = helpItem.answer

            contentLayout.visibility = if (helpItem.isExpanded) View.VISIBLE else View.GONE
            arrowImageView.rotation = if (helpItem.isExpanded) 180f else 0f

            headerLayout.setOnClickListener {
                helpItem.isExpanded = !helpItem.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
