package net.syntessense.app.todolist_dai2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
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

class MyEditText : androidx.appcompat.widget.AppCompatEditText {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            //this.compoundDrawables[2].
            if ( this.compoundDrawablesRelative[2] != null ) {
                var location : IntArray = intArrayOf(0, 0)
                this.getLocationOnScreen(location)

                val iconXmin = location[0] + this.width - this.compoundPaddingEnd
                val iconYmin = location[1] + (this.height - this.compoundDrawablesRelative[2].bounds.height()) / 2

                val iconXmax = iconXmin + this.compoundDrawablesRelative[2].bounds.width()
                val iconYmax = iconYmin + this.compoundDrawablesRelative[2].bounds.height()

                Log.i("dimension", this.compoundDrawablesRelative[2].bounds.toString())
                Log.i("iconXmin", iconXmin.toString())
                Log.i("iconXmax", iconXmax.toString())
                Log.i("iconYmin", iconYmin.toString())
                Log.i("iconYmax", iconYmax.toString())
                Log.i("eventX", event.rawX.toString())
                Log.i("eventY", event.rawY.toString())

                if ( iconXmin <= event.rawX && event.rawX < iconXmax &&
                    iconYmin <= event.rawY && event.rawY < iconYmax )
                    this.text = null
            }
            //if ( event.x >= location[0] + this.width * 0.9 )
            //    this.text = null
        }
        return super.onTouchEvent(event)
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
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.remove_text, 0)
                else edt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
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
