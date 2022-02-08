package net.syntessense.app.todolist_dai2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


// import androidx.appcompat.app.ActionBar


class MyAdapter(val context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return 50
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

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()
        findViewById<ListView>(R.id.list).adapter = MyAdapter(this)
        val edt = findViewById<EditText>(R.id.filter_text)
        edt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0)
                    edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                else edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {

                // TODO Auto-generated method stub
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
