package br.com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnswerAdapter(
    private var answers: List<Answer>,
    private val onVote: (Answer, VoteType) -> Unit
) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = answers[position]
        holder.bind(answer)
    }

    override fun getItemCount(): Int = answers.size

    fun updateAnswers(newAnswers: List<Answer>) {
        answers = newAnswers
        notifyDataSetChanged()
    }

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bodyTextView: TextView = itemView.findViewById(R.id.answerBodyTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorNameTextView)
        private val voteCountTextView: TextView = itemView.findViewById(R.id.voteCountTextView)
        private val upvoteButton: ImageButton = itemView.findViewById(R.id.upvoteButton)
        private val downvoteButton: ImageButton = itemView.findViewById(R.id.downvoteButton)

        fun bind(answer: Answer) {
            bodyTextView.text = answer.body
            authorTextView.text = "por ${answer.authorName}"
            voteCountTextView.text = answer.voteCount.toString()

            upvoteButton.setOnClickListener { onVote(answer, VoteType.UP) }
            downvoteButton.setOnClickListener { onVote(answer, VoteType.DOWN) }
        }
    }
}
