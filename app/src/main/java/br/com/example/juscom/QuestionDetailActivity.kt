package br.com.example.juscom

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
        setupClickListeners()
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
        answerAdapter = AnswerAdapter(emptyList()) { answer ->
            viewModel.handleVote(answer.id)
        }
        binding.answersRecyclerView.adapter = answerAdapter
        binding.answersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.sendAnswerButton.setOnClickListener { 
            val answerText = binding.answerEditText.text.toString().trim()
            if (answerText.isNotEmpty()) {
                questionId?.let { 
                    viewModel.postAnswer(it, answerText)
                } 
            } else {
                Toast.makeText(this, "A resposta não pode estar em branco.", Toast.LENGTH_SHORT).show()
            }
        }
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

        viewModel.voteStatus.observe(this, Observer { voteStatus ->
            answerAdapter.updateVoteStatus(voteStatus)
        })

        viewModel.postResult.observe(this, Observer { success ->
            if (success) {
                binding.answerEditText.text.clear()
                // Ocultar o teclado
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.answerEditText.windowToken, 0)
            }
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
