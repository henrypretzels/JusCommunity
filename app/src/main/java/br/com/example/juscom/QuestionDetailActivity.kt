package br.com.example.juscom

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.example.juscom.databinding.ActivityQuestionDetailBinding

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionDetailBinding
    private val viewModel: QuestionDetailViewModel by viewModels()
    private lateinit var answerAdapter: AnswerAdapter
    private var questionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        questionId = intent.getStringExtra("QUESTION_ID")

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        questionId?.let {
            viewModel.loadQuestionAndAnswers(it)
        } ?: run {
            Toast.makeText(this, "Error: Question ID is missing.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        answerAdapter = AnswerAdapter(emptyList()) { answer, voteType ->
            viewModel.handleVote(answer, voteType)
        }
        binding.answersRecyclerView.adapter = answerAdapter
        binding.answersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.question.observe(this, Observer { question ->
            question?.let {
                binding.questionTitleTextView.text = it.title
                binding.questionBodyTextView.text = it.body
                binding.authorNameTextView.text = "por ${it.authorName}"
                supportActionBar?.title = it.title
            }
        })

        viewModel.answers.observe(this, Observer { answers ->
            answerAdapter.updateAnswers(answers)
            binding.answersHeaderTextView.text = "${answers.size} Respostas"
        })

        viewModel.error.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
