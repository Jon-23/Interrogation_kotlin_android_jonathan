package net.syntessense.app.todolist_dai2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.syntessense.app.todolist_dai2.databinding.ActivityMainBinding
import net.syntessense.app.todolist_dai2.databinding.ListItemBinding

/*
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
*/

class TodoAdapter(private val context: Activity, private val todoDao: TodoDao) : BaseAdapter() {

    var todos : List<Todo> = listOf()

    init {
        refresh()
    }

    fun refresh() {
        val self = this
        CoroutineScope(SupervisorJob()).launch {
            todos = todoDao.getAll()
            context.runOnUiThread {
                self.notifyDataSetChanged()
            }
        }
    }

    override fun getCount(): Int {
        return todos.size
    }

    override fun getItem(position: Int): Any {
        return todos[position]
    }

    override fun getItemId(position: Int): Long {
        return todos[position].id.toLong()
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = if ( convertView != null )
            ListItemBinding.bind(convertView) else
                ListItemBinding.inflate(context.layoutInflater, parent, false)

        binding.item.text = todos[i].title
        binding.priorityColor.setBackgroundColor(todos[i].priority.color)
        return binding.root
    }

}

class MainActivity : AppCompatActivity() {

    lateinit var adapter : TodoAdapter

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        supportActionBar?.hide()

        val speech2textLauncher = SpeechAnalysis(this)

        val dao = getTodoDb(applicationContext).todoDao()
        val fab = bindings.fab
        val lst = bindings.list
        val edt = bindings.filterBar.filterText
        val clr = bindings.filterBar.clearText
        val men = bindings.filterBar.menu
        val mic = bindings.filterBar.micro



        adapter = TodoAdapter(this, dao)
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







