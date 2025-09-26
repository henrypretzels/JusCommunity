package com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class QAAdapter(private val questions: List<Question>) : RecyclerView.Adapter<QAAdapter.QAViewHolder>() {

    class QAViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.qaCard)
        val questionText: TextView = itemView.findViewById(R.id.questionText)
        val answerText: TextView = itemView.findViewById(R.id.answerText)
        val authorText: TextView = itemView.findViewById(R.id.authorText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val likesText: TextView = itemView.findViewById(R.id.likesText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_qa, parent, false)
        return QAViewHolder(view)
    }

    override fun onBindViewHolder(holder: QAViewHolder, position: Int) {
        val question = questions[position]
        
        holder.questionText.text = question.question
        holder.answerText.text = question.answer
        holder.authorText.text = question.author
        holder.dateText.text = question.date
        holder.likesText.text = "${question.likes} curtidas"
    }

    override fun getItemCount(): Int = questions.size
}
