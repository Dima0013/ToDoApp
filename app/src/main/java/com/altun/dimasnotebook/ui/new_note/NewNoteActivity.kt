package com.altun.dimasnotebook.ui.new_note

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.altun.dimasnotebook.R
import com.altun.dimasnotebook.room.DatabaseBuilder
import com.altun.dimasnotebook.room.NoteModel
import kotlinx.android.synthetic.main.activity_new_note.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        init()

    }

    private fun init() {

        saveButton.setOnClickListener {
            if (textEditText.text.isNotEmpty() && titleEditText.text.isNotEmpty()) {
                val title = titleEditText.text.toString()
                val text = textEditText.text.toString()
                val note = NoteModel(null, title, text)
                intent.putExtra("note",note)
                GlobalScope.launch(Dispatchers.IO) {
                    DatabaseBuilder.getInstance().noteDao().insert(note)
                    withContext(Dispatchers.Main){
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }
                }

            }
        }
    }

}