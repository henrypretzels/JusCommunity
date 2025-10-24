package br.com.example.juscom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionListAdapter(
    private var questions: List<Question>,
    private val onQuestionClick: (Question) -> Unit
) : RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int = questions.size

    fun updateQuestions(newQuestions: List<Question>) {
        questions = newQuestions
        notifyDataSetChanged()
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.questionTitleTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorNameTextView)
        private val answerCountTextView: TextView = itemView.findViewById(R.id.answerCountTextView)

        fun bind(question: Question) {
            titleTextView.text = question.title
            authorTextView.text = "por ${question.authorName}"
            answerCountTextView.text = "${question.answerCount} respostas"

            itemView.setOnClickListener {
                onQuestionClick(question)
            }
        }
    }
}
