package com.raywenderlich.timefighter


import android.content.IntentSender.OnFinished
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    //Assign the name of the class to TAG to see which class the msg comes from
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button
    private var gameStarted = false

    //define countdown timer and show how many seconds are left
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 10000
    private var countDownInterval: Long = 1000
    private var timeLeft = 60

    private var score = 0
    //defines new variable Score
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Log a message when activity is created. Sends a msg when onCreate is called
        //and displays the score
        Log.d(TAG, "onCreate called. Score is: $score")


        // connect views to variables
        gameScoreTextView = findViewById(R.id.game_score_text_view)

        timeLeftTextView = findViewById(R.id.time_left_text_view)

        tapMeButton = findViewById(R.id.tap_me_button)
        // searches through activity_main layout to find the View with the listed ID and creates
        //a reference to it

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }
        //attaches a click/tap listener to button and calls incrementScore when tapped

        //Check to see if savedInstanceState has a value. If so, we get the values from
        //savedInstanceState
        if (savedInstanceState !=null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else  {
            resetGame()
        }
    }

    //override onSaveInstanceState to insert and save the score and timeLeft values before
    //a configuration change happens.
    //Also cancels countdowntimer and add a log to track when the method is called
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeft")
    }

    //Override onDestroy and call super to clean up
    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) {
            showInfo()
        }
        return true
    }


    private fun incrementScore() {
        //starts the game after the button is tapped
        if (!gameStarted) {
            startGame()
        }

        // increment score logic
        score++

        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore
        //increment Score variable to a new number and create a string newScore
        //use newScore to set test of gameScoreTextView

    }
    private fun resetGame() {
        // reset game logic
        //1
        //set the score to 0 so we can increment when the button is tapped
        score = 0

        //create a variable to store the score as a string
        //and use getString to insert the score value into strings.xml
        val initialScore = getString(R.string.your_score, score)
        //set the text value of gameScoreTextView with initialScore value
        gameScoreTextView.text = initialScore

        //create variable to store the time left as a string
        //and use getString to insert the value into strings.xml
        val initialTimeLeft = getString(R.string.time_left, 10)
        //set the text value of timeLeftTextView to initialTimeLeft
        timeLeftTextView.text = initialTimeLeft

        //2
        //create the count down timer and pass it to the constructor
        //initialCountDown and countDownInterval, set to count from 60000 milliseconds
        //every 1000 milliseconds, or 1 second, until 0 is reached
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            //3
            //call onTick at every interval that is passed into the timer (once a second)
            //also passes in millisUntilFinished
            override fun onTick(millisUntilFinished: Long) {
                //timeLeft is updated every interval to reflect the time remaining
                //and converts to seconds
                timeLeft = millisUntilFinished.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                //end the game
                endGame()
            }
        }
        //4
        //inform this property that the game is not started on load
        gameStarted = false
    }

    //Sets up the text views and countdown timer properties using the inserted values
    private fun restoreGame() {
        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = restoredTime

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000

                val  timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun startGame() {
        // start game logic
        //inform the countdown timer to start
        countDownTimer.start()
        //inform the property that the game is started
        gameStarted = true

    }
    private fun endGame() {
        // end game logic
        //create a Toast to notify the user that the game has ended and to show the final score
        //Use LENGTH_LONG to display the Toast for a longer time
        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()

        //once the Toast is shown, reset the game
        resetGame()
    }

    //Add the companion object to the bottom of the class
    //That stores the string constants and tracks the variables we want to save
    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    private fun  showInfo() {
        val dialogueTitle = getString(R.string.about_title)
        val dialogueMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogueTitle)
        builder.setMessage(dialogueMessage)
        builder.create().show()
    }
}



