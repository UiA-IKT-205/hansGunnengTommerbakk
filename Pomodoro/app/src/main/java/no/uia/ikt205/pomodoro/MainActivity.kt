package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime

class MainActivity : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var startButton:Button
    lateinit var coutdownDisplay:TextView
    lateinit var timer30:Button
    lateinit var timer60:Button
    lateinit var timer90:Button
    lateinit var timer120:Button
    var isRunning:Boolean = false

    var timeToCountDownInMs = 0L
    val timeTicks = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       startButton = findViewById<Button>(R.id.startCountdownButton)
       startButton.setOnClickListener(){
           startCountDown(it)
       }
       coutdownDisplay = findViewById<TextView>(R.id.countDownView)


        timer30 = findViewById(R.id.timer30Button)
        timer30.setOnClickListener(){
            timeToCountDownInMs = 1800000L
            updateCountDownDisplay(timeToCountDownInMs)
        }
        timer60 = findViewById(R.id.timer60Button)
        timer60.setOnClickListener(){
            timeToCountDownInMs = 3600000L
            updateCountDownDisplay(timeToCountDownInMs)
        }
        timer90 = findViewById(R.id.timer90Button)
        timer90.setOnClickListener(){
            timeToCountDownInMs = 5400000L
            updateCountDownDisplay(timeToCountDownInMs)
        }
        timer120 = findViewById(R.id.timer120Button)
        timer120.setOnClickListener(){
            timeToCountDownInMs = 7200000L
            updateCountDownDisplay(timeToCountDownInMs)
        }
    }

    fun startCountDown(v: View){
        timer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                Toast.makeText(this@MainActivity,"Arbeidsøkt er ferdig", Toast.LENGTH_SHORT).show()
                isRunning = false;
            }

            override fun onTick(millisUntilFinished: Long) {
               updateCountDownDisplay(millisUntilFinished)
            }
        }
        if(isRunning){
            Toast.makeText(this@MainActivity, "Timer kjører allerede", Toast.LENGTH_SHORT).show()
        }

        else {
            isRunning = true;
            timer.start()
        }
    }

    fun updateCountDownDisplay(timeInMs:Long){
        coutdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

}