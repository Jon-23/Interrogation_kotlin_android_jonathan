package net.syntessense.app.todolist_dai2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat as DF
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

    lateinit var bindings : ActivityTodoAddBinding
    lateinit var speech2textLauncher : SpeechAnalysis
    lateinit var todoDao : TodoDao
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()
        //getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        todoDao = getTodoDb(this).todoDao()
        bindings = ActivityTodoAddBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        getSupportActionBar()?.elevation = 0F

        speech2textLauncher = SpeechAnalysis(this)


        val special = Todo(
            0,
            Todo.Priority.RED,
            "Mon Special Todo",
            "Mon Special todo description",
            "2020-08-31 04:00",
            "2020-12-31 23:59",
            "2020-10-31 01:00"
        )
        render(special)

        /*var position = 20
        var self = this
        CoroutineScope(SupervisorJob()).launch {
            val todo = todoDao.getPage(0, position).get(0)
            self.runOnUiThread {

            }
        }*/

    }

    private fun render(todo: Todo) {

        val todoDate = Date()

        val dt = todo.dueDate.split(" ")
        val timetxt = dt[1].split(":")
        val datetxt = dt[0].split("-")
        todoDate.year = datetxt[0].toInt() - 1900
        todoDate.month = datetxt[1].toInt() - 1
        todoDate.date = datetxt[2].toInt()
        todoDate.hours = timetxt[0].toInt()
        todoDate.minutes = timetxt[1].toInt()



        //todoDate
        bindings.titleText.text = Editable.Factory.getInstance().newEditable(todo.title)
        bindings.descText.text = Editable.Factory.getInstance().newEditable(todo.description)
        bindings.priorityColor.setBackgroundColor(todo.priority.color.toInt())


        val fab = bindings.fab


        bindings.titleText.setOnLongClickListener {
            speech2textLauncher.start { result ->
                bindings.titleText.text = Editable.Factory.getInstance().newEditable(result)
            }
            true
        }

        bindings.descText.setOnLongClickListener {
            speech2textLauncher.start { result ->
                bindings.descText.text = Editable.Factory.getInstance().newEditable(result)
            }
            true
        }

        var priorities = arrayOf(Todo.Priority.GREEN, Todo.Priority.ORANGE, Todo.Priority.RED)
        var priorityIndex = priorities.indexOf(todo.priority)

        fab.setOnClickListener(View.OnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                todoDao.insertAll(Todo(
                    0,
                    priorities[priorityIndex],
                    bindings.titleText.text.toString(),
                    bindings.descText.text.toString(),
                    "2020-08-31 04:00",
                    "${todoDate.year + 1900}-${todoDate.month + 1}-${todoDate.date} ${todoDate.hours}:${todoDate.minutes}",
                    "2020-10-31 01:00"
                ))
                finish()
            }

            true
        })

        val prio = bindings.priorityColor
        prio.setOnClickListener(View.OnClickListener {
            priorityIndex = (priorityIndex + 1) % priorities.size
            prio.setBackgroundColor(priorities[priorityIndex].color)
        })

        val tpicker = bindings.timeText
        val dpicker = bindings.dateText

        tpicker.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(todoDate)
        dpicker.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(todoDate)

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
            val newFragment = DatePickerFragment(this, Triple(todoDate.year + 1900, todoDate.month + 1, todoDate.date)) {y, m, d ->
                todoDate.year = y - 1900
                todoDate.month = m - 1
                todoDate.date = d
                dpicker.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(todoDate)
            }.show(supportFragmentManager, "datePicker")
            true
        })

    }

}
