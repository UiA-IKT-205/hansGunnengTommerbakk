package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var startButton:Button
    lateinit var coutdownDisplay:TextView
    lateinit var timerSeekBar:SeekBar
    lateinit var timerInfoText:TextView
    lateinit var pauseSeekBar:SeekBar
    lateinit var pauseInfoText:TextView
    lateinit var repititionInput:TextInputEditText

    var repititionNumber:Int = 3
    var isRunning:Boolean = false
    var timeToCountDownInMs = 900000L
    var pauseTimeToCountDownInMs = 900000L
    val timeTicks = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerSeekBar = findViewById(R.id.timerSeekBar)
        timerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                timeToCountDownInMs = p1 * 60000L
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val temptext1 = "${p0?.progress} minutes"
                timerInfoText.text = temptext1

            }
        })

        pauseSeekBar = findViewById(R.id.pauseSeekBar)
        pauseSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                pauseTimeToCountDownInMs = p1 * 60000L
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val temptext2 = "${p0?.progress} minutes"
                pauseInfoText.text = temptext2

            }
        })
        startButton = findViewById<Button>(R.id.startCountdownButton)
        startButton.setOnClickListener(){
            try {
                repititionNumber = repititionInput.text.toString().toInt()

                if (repititionNumber < 1) {
                    Toast.makeText(
                        this@MainActivity,
                        "Du trenger minst 1 repitisjon",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startCountDown()
                }
            } catch (e:NumberFormatException)
            {Toast.makeText(this@MainActivity,"Du må legge inn et tall", Toast.LENGTH_SHORT).show()}
        }
        coutdownDisplay = findViewById<TextView>(R.id.countDownView)
        timerInfoText = findViewById(R.id.timerInfoText)
        pauseInfoText = findViewById(R.id.pauseInfoText)
        repititionInput = findViewById(R.id.repititionInput)
        //repititionNumber = repititionInput.text.toString().toInt()

    }

    fun startCountDown(){
        timer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                Toast.makeText(this@MainActivity,"Økt ferdig", Toast.LENGTH_SHORT).show()
                pauseCountDown()

            }

            override fun onTick(millisUntilFinished: Long) {
                updateCountDownDisplay(millisUntilFinished)
            }
        }
        if(isRunning){
            Toast.makeText(this@MainActivity, "Timer kjører allerede", Toast.LENGTH_SHORT).show()
        }

        else {
            isRunning = true
            timer.start()
        }
    }

    fun updateCountDownDisplay(timeInMs:Long){
        coutdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

    fun pauseCountDown(){
        timer = object : CountDownTimer(pauseTimeToCountDownInMs,timeTicks) {
            override fun onFinish() {

                repititionNumber--
                Toast.makeText(this@MainActivity, "Pausen er ferdig", Toast.LENGTH_SHORT).show()
                if(repititionNumber == 0) {
                    isRunning = false
                    Toast.makeText(this@MainActivity, "Ferdig med alle repitisjoner", Toast.LENGTH_SHORT).show()
                }
                else {
                    isRunning = false
                    Toast.makeText(this@MainActivity, "Reptitisjoner igjen: " +repititionNumber, Toast.LENGTH_SHORT).show()
                    startCountDown()
                }
            }
            override fun onTick(millisUntilFinished: Long) {
                updateCountDownDisplay(millisUntilFinished)
            }
        }
        timer.start()

    }

}
