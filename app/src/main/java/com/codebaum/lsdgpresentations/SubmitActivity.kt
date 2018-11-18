package com.codebaum.lsdgpresentations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codebaum.lsdgpresentations.data.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.activity_details.*

class SubmitActivity : AppCompatActivity() {

    /// name
    /// presenter
    /// state
    /// description
    /// starred

    private val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        setTitle(R.string.submit_title)
    }

    companion object {

        fun getStartIntent(context: Context): Intent {
            return Intent(context, SubmitActivity::class.java)
        }
    }
}
