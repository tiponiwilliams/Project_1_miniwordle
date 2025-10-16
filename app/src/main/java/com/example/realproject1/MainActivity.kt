package com.example.realproject1

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var guessInput: EditText
    private lateinit var submitButton: Button
    private lateinit var resetButton: Button
    private lateinit var guess1: TextView
    private lateinit var guess2: TextView
    private lateinit var guess3: TextView
    private lateinit var result1: TextView
    private lateinit var result2: TextView
    private lateinit var result3: TextView
    private lateinit var answerText: TextView

    private var wordToGuess: String = ""
    private var guessesTaken = 0
    private val maxGuesses = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        startNewGame()

        submitButton.setOnClickListener { handleSubmit() }
        resetButton.setOnClickListener { startNewGame() }

        // Allow keyboard actionDone to submit
        guessInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleSubmit()
                true
            } else false
        }
    }

    private fun bindViews() {
        guessInput = findViewById(R.id.guessInput)
        submitButton = findViewById(R.id.submitButton)
        resetButton = findViewById(R.id.resetButton)

        guess1 = findViewById(R.id.guess1)
        guess2 = findViewById(R.id.guess2)
        guess3 = findViewById(R.id.guess3)
        result1 = findViewById(R.id.result1)
        result2 = findViewById(R.id.result2)
        result3 = findViewById(R.id.result3)
        answerText = findViewById(R.id.answerText)
    }

    private fun startNewGame() {
        wordToGuess = FourLetterWordList.getRandomFourLetterWord()
        guessesTaken = 0

        // Reset UI
        guess1.text = "Guess 1:"
        guess2.text = "Guess 2:"
        guess3.text = "Guess 3:"
        result1.text = "—"
        result2.text = "—"
        result3.text = "—"
        answerText.text = ""
        answerText.visibility = TextView.GONE

        submitButton.isEnabled = true
        submitButton.alpha = 1f
        resetButton.visibility = Button.GONE

        guessInput.setText("")
        guessInput.isEnabled = true
        guessInput.requestFocus()
    }

    private fun handleSubmit() {
        val raw = guessInput.text?.toString()?.trim().orEmpty()
        val guess = raw.uppercase()

        if (guess.length != 4 || !guess.all { it.isLetter() }) {
            Toast.makeText(this, "Please enter exactly 4 letters.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!submitButton.isEnabled) {
            Toast.makeText(this, "No more guesses left.", Toast.LENGTH_SHORT).show()
            return
        }

        val feedback = checkGuess(guess, wordToGuess)
        guessesTaken++

        when (guessesTaken) {
            1 -> { guess1.text = "Guess 1: $guess"; result1.text = feedback }
            2 -> { guess2.text = "Guess 2: $guess"; result2.text = feedback }
            3 -> { guess3.text = "Guess 3: $guess"; result3.text = feedback }
        }

        // Correct guess ends game early
        if (guess == wordToGuess || guessesTaken >= maxGuesses) {
            endGame(won = (guess == wordToGuess))
        }

        guessInput.setText("")
    }

    private fun endGame(won: Boolean) {
        submitButton.isEnabled = false
        submitButton.alpha = 0.5f
        guessInput.isEnabled = false

        val reveal = if (won) "You got it! Answer: $wordToGuess"
        else "Out of guesses. Answer: $wordToGuess"
        answerText.text = reveal
        answerText.visibility = TextView.VISIBLE
        resetButton.visibility = Button.VISIBLE
    }

    /**
     * Returns a string of length 4 composed of 'O', '+', 'X'
     * O = right letter, right place
     * + = right letter, wrong place
     * X = letter not in target
     */
    private fun checkGuess(guess: String, target: String): String {
        var res = ""
        for (i in 0..3) {
            res += when {
                guess[i] == target[i] -> "O"
                target.contains(guess[i]) -> "+"
                else -> "X"
            }
        }
        return res
    }
}
