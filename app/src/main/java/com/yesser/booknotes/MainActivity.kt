package com.yesser.booknotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yesser.booknotes.BookListAdapter.OnDeleteClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnDeleteClickListener {
    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bookListAdapter = BookListAdapter(this, this)
        recyclerview.adapter = bookListAdapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener{
            val intent = Intent(this, NewBookActivity::class.java)
            startActivityForResult(intent,NEW_NOTE_ACTIVITY_REQUEST_CODE)
        }

        bookViewModel = ViewModelProvider(this, BookViewModelFactory(application))[BookViewModel::class.java]
        bookViewModel.allBooks.observe(this, androidx.lifecycle.Observer { books ->
            books?.let {
                bookListAdapter.setBooks(books)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_NOTE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val id = UUID.randomUUID().toString()
            val authorName = data!!.getStringExtra(NewBookActivity.NEW_AUTHOR)
            val bookName = data!!.getStringExtra(NewBookActivity.NEW_BOOK)

            val book = Book(id, authorName, bookName)
            bookViewModel.insert(book)
            Toast.makeText(
                applicationContext,
                R.string.saved,
                Toast.LENGTH_LONG
            ).show()
        } else if(requestCode == UPDATE_NOTE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val id = data!!.getStringExtra(EditBookActivity.ID)
            val authorName = data!!.getStringExtra(EditBookActivity.UPDATED_AUTHOR)
            val bookName = data!!.getStringExtra(EditBookActivity.UPDATED_BOOK)

            val book = Book(id, authorName, bookName)

            //codigo para actualizar
            bookViewModel.update(book)

            Toast.makeText(applicationContext, "Actualizado", Toast.LENGTH_LONG).show()

        }else {
            Toast.makeText(
                applicationContext,
                R.string.not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        private const val NEW_NOTE_ACTIVITY_REQUEST_CODE = 1
        const val UPDATE_NOTE_ACTIVITY_REQUEST_CODE = 2
    }

    override fun onDeleteClickListener(myBook: Book) {
        bookViewModel.delete(myBook)
        Toast.makeText(applicationContext, "Borrado",Toast.LENGTH_LONG).show()
    }
}