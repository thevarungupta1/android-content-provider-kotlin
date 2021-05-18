package com.example.contentproviderdemo

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        button_insert.setOnClickListener(this)
        button_read.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            button_read -> readData()
            button_insert -> insertData()
        }
    }

    private fun insertData() {
        var name = edit_text_name.text.toString()
        val contentValues = ContentValues()
        contentValues.put(MyContentProvider.name, name)
        contentResolver.insert(MyContentProvider.CONTENT_URI, contentValues)
        //Toast.makeText(applicationContext, "record inserted", Toast.LENGTH_SHORT).show()

    }

    private fun readData() {
        var data = ""
        var cursor = contentResolver.query(Uri.parse(MyContentProvider.URI), null, null, null, null)
        if (cursor!!.moveToFirst() && cursor != null) {
            do {
                var name = cursor.getString(cursor.getColumnIndex("name"))
                data += "$name \n"
            } while (cursor!!.moveToNext())
            text_view_read.text = data
        } else {
            text_view_read.text = "No Data"
        }
    }
}