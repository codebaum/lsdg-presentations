package com.codebaum.lsdgpresentations

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.codebaum.lsdgpresentations.data.Presentation
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildNavigation()

        buildListView()

        updateListView()

    }

    private fun buildNavigation() {
        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_upcoming -> {
                    viewAdapter.filter("upcoming")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_suggested -> {
                    viewAdapter.filter("suggested")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_past -> {
                    viewAdapter.filter("completed")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun updateListView() {
        val db = FirebaseFirestore.getInstance()

        db.collection("presentations").get().addOnCompleteListener {
            if (it.isSuccessful) {

                val presentationList = ArrayList<Presentation>()

                it.result?.forEach { documentSnapshot ->

                    val name = documentSnapshot.get("name") as String
                    val presenterReference = documentSnapshot.get("presenter") as DocumentReference
                    val presenter = presenterReference.id
                    val state = documentSnapshot.get("state") as String
                    val presentation = Presentation(name, presenter, state)

                    presentationList.add(presentation)
                    name.logDebug()
                }
                viewAdapter.update(presentationList)
            } else {
                val message = "Error occurred."
                toast(message)
                message.logDebug()
            }
        }
    }

    private fun buildListView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = MyAdapter(applicationContext, arrayListOf())

        recyclerView = findViewById<RecyclerView>(R.id.rv_presentations).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.logDebug() {
    Log.d("DEBUG", this)
}