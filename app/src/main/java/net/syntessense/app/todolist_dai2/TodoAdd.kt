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
import android.widget.*
import androidx.fragment.app.DialogFragment
import net.syntessense.app.todolist_dai2.databinding.ActivityTodoAddBinding
import java.text.DateFormat
import java.util.*

class TimePickerFragment(val time: Pair<Int, Int>, val callback: (Int, Int) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    constructor(callback: (Int, Int) -> Unit): this(
        Calendar.getInstance().let{ Pair(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))},
        callback
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity, this, time.first, time.second, DF.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        callback(hourOfDay, minute)
    }
}
class DatePickerFragment(private val activity: Context, private val showDate: Triple<Int, Int, Int>, val callback: (Int, Int, Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    constructor(activity: Context, callback: (Int, Int, Int) -> Unit) : this(
        activity,
        Calendar.getInstance().let{ Triple(it.get(Calendar.YEAR), it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))},
        callback
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(activity, this, showDate.first, showDate.second, showDate.third)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        callback(year, month, day)
    }
}

class TodoAdd : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()
        //getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        val bindings = ActivityTodoAddBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        getSupportActionBar()?.elevation = 0F

        val title = bindings.titleText
        val fab = bindings.fab


        fab.setOnClickListener(View.OnClickListener {
            finish()
            true
        })

        var priority = 0
        var priorities = arrayOf("#00bb00", "#ff9900", "#ff0000")
        val prio = bindings.priorityText
        prio.setOnClickListener(View.OnClickListener {
            priority = (priority + 1) % priorities.size
            prio.setBackgroundColor(Color.parseColor(priorities[priority]))
        })


        val todoDate = Date()
        val tpicker = bindings.timeText
        val dpicker = bindings.dateText

        tpicker.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(todoDate)
        dpicker.text = DateFormat.getDateInstance(DateFormat.MEDIUM, ).format(todoDate)

        //SimpleDateFormat()

        tpicker.setOnClickListener(View.OnClickListener {
            val tp = TimePickerFragment(Pair(todoDate.hours, todoDate.minutes)) { h, m ->
                todoDate.hours = h
                todoDate.minutes = m
                tpicker.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(todoDate)
            }.show(supportFragmentManager, "timePicker")
            true
        })

        dpicker.setOnClickListener(View.OnClickListener {
            val newFragment = DatePickerFragment(this, Triple(todoDate.year + 1900, todoDate.month, todoDate.date)) {y, m, d ->
                todoDate.year = y - 1900
                todoDate.month = m
                todoDate.date = d
                dpicker.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(todoDate)
            }.show(supportFragmentManager, "datePicker")
            true
        })

    }

}
