package net.syntessense.app.todolist_dai2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.syntessense.app.todolist_dai2.databinding.ActivityMainBinding
import java.text.FieldPosition

class MyAdapter(private val context: Context, private var size:Int = 0) : BaseAdapter() {

    override fun getCount(): Int {
        return size
    }

    override fun getItem(i: Int): String {
        return "ToDo number $i"
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val tv = (convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)) as TextView
        tv.text = getItem(i)
        return tv
    }

    fun add() {
        size += 10
    }

}

class TodoAdapter(private val context: Context, private val todoDao: TodoDao) : BaseAdapter() {

    var todos : List<Todo> = listOf()

    init {

        CoroutineScope(SupervisorJob()).launch {
            todoDao.deleteAll()
            for(i in 10..99)
                todoDao.insertAll(Todo(
                    i + 1,
                    arrayOf(
                        Todo.Priority.RED,
                        Todo.Priority.ORANGE,
                        Todo.Priority.GREEN,
                    )[(0..2).random()],
                    "title $i",
                    "description $i",
                    creationDate = "20$i-01-01 00:00:00",
                    limitDate = "20$i-02-01 00:00:00",
                    doneDate = "20$i-03-01 00:00:00",
                ))
            todos = todoDao.getAll()
        }

    }

    override fun getCount(): Int {
        return todos.size
    }

    override fun getItem(position: Int): Any {
        return todos[position]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val tv = (convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)) as TextView
        tv.text = todos[i].title
        return tv
    }

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        supportActionBar?.hide()

        val dao= getTodoDb(applicationContext).todoDao()
        val fab = bindings.fab
        val lst = bindings.list
        val edt = bindings.filterBar.filterText
        val clr = bindings.filterBar.clearText
        val men = bindings.filterBar.menu
        val mic = bindings.filterBar.micro

        val speech2textLauncher = SpeechAnalysis(this)

        val adapter = TodoAdapter(this, dao)
        lst.adapter = adapter
        lst.divider = null
        clr.visibility = View.GONE

        mic.setOnClickListener {
            speech2textLauncher.start { result ->
                edt.text = Editable.Factory.getInstance().newEditable(result)
            }
        }

        edt.setOnLongClickListener {
            speech2textLauncher.start { result ->
                edt.text = Editable.Factory.getInstance().newEditable(result)
            }
            true
        }

        fab.setOnClickListener { view ->
            startActivity(Intent(this, TodoAdd::class.java))
            //adapter.add()
            //adapter.notifyDataSetChanged()
        }

        edt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    mic.visibility = View.GONE
                    clr.visibility = View.VISIBLE
                } else {
                    clr.visibility = View.GONE
                    mic.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        clr.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                edt.text = null
            }
            clr.performClick()
            true
        }

    }

}







