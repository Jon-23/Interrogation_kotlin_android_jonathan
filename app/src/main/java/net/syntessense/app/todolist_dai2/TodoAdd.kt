package net.syntessense.app.todolist_dai2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.SpannableStringBuilder
import android.text.format.DateFormat as DF
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.DateFormat
import java.util.*

class TimePickerFragment(val callback: (Int, Int) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DF.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        callback(hourOfDay, minute)
    }
}
class DatePickerFragment(private val activity: Context, val callback: (Int, Int, Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        callback(year, month, day)
    }
}

class TodoAdd : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()
        //getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_todo_add)
        getSupportActionBar()?.elevation = 0F

        var title = findViewById<EditText>(R.id.title_text)
        val sr = SpeechRecognizer.createSpeechRecognizer(this)

        val fab = findViewById<View>(R.id.fab)
        fab.setOnLongClickListener(View.OnLongClickListener {
            val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            sr.startListening(speechRecognizerIntent)
            sr.setRecognitionListener(object: RecognitionListener {

                override fun onResults(p0: Bundle?) {
                    title.text = SpannableStringBuilder("Reconnaissance vocale")
                }

                override fun onReadyForSpeech(p0: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onBeginningOfSpeech() {
                    TODO("Not yet implemented")
                }

                override fun onRmsChanged(p0: Float) {
                    TODO("Not yet implemented")
                }

                override fun onBufferReceived(p0: ByteArray?) {
                    TODO("Not yet implemented")
                }

                override fun onEndOfSpeech() {
                    TODO("Not yet implemented")
                }

                override fun onError(p0: Int) {
                    TODO("Not yet implemented")
                }

                override fun onPartialResults(p0: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onEvent(p0: Int, p1: Bundle?) {
                    TODO("Not yet implemented")
                }
            })
            true
        })

        fab.setOnClickListener(View.OnClickListener {
            finish()
            true
        })

        var priority = 0
        var priorities = arrayOf("#00bb00", "#ff9900", "#ff0000")
        val prio = findViewById<TextView>(R.id.priority_text)
        prio.setOnClickListener(View.OnClickListener {
            priority = (priority + 1) % priorities.size
            prio.setBackgroundColor(Color.parseColor(priorities[priority]))
        })


        val todoDate = Date()
        val tpicker = findViewById<TextView>(R.id.time_text)
        val dpicker = findViewById<TextView>(R.id.date_text)

        tpicker.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(todoDate)
        dpicker.text = DateFormat.getDateInstance(DateFormat.MEDIUM, ).format(todoDate)

        SimpleDateFormat()

        tpicker.setOnClickListener(View.OnClickListener {
            val tp = TimePickerFragment { h, m ->
                todoDate.hours = h
                todoDate.minutes = m
                tpicker.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(todoDate)
            }.show(supportFragmentManager, "timePicker")
            true
        })

        dpicker.setOnClickListener(View.OnClickListener {
            val newFragment = DatePickerFragment(this) {y, m, d ->
                todoDate.year = y
                todoDate.month = m
                todoDate.date = d
                dpicker.text = DateFormat.getDateInstance(DateFormat.MEDIUM, ).format(todoDate)
            }.show(supportFragmentManager, "datePicker")
            true
        })

    }

}
