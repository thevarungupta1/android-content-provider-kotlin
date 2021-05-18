package com.example.otherapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val PROVIDER_NAME = "com.example.contentproviderdemo"

    // defining content URI
    val URI = "content://$PROVIDER_NAME/users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        button_load_data.setOnClickListener {
            var data = ""
            var cursor = contentResolver.query(Uri.parse(URI), null, null, null, null)
            if (cursor != null && cursor.moveToFirst() ) {
                do {
                    var name = cursor.getString(cursor.getColumnIndex("name"))
                    data += "$name \n"
                } while (cursor!!.moveToNext())
                text_view_data.text = data
            } else {
                text_view_data.text = "No Data"
            }
        }
    }
}