package com.codebaum.lsdgpresentations

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        db.collection("presentations").get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.forEach { presentation ->
                    val name = presentation.get("name") as String
                    toast(name)
                    name.logDebug()
                }
            } else {
                val message = "Error occurred."
                toast(message)
                message.logDebug()
            }
        }
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.logDebug() {
    Log.d("DEBUG", this)
}