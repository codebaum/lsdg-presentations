package com.codebaum.lsdgpresentations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.codebaum.lsdgpresentations.data.PresentationMapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    /// name
    /// presenter
    /// state
    /// description
    /// starred

    private val presentationMapper = PresentationMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val docId = intent.getStringExtra(KEY_DOC_ID)

        val db = FirebaseFirestore.getInstance()
        db.collection("presentations").document(docId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            documentSnapshot?.apply {
                val presentation = presentationMapper.from(documentSnapshot)
                tv_description_value.text = presentation.description

                title = presentation.name
            }
        }

        fab_starred.setOnClickListener {
            Snackbar.make(it, "test", Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {

        const val KEY_DOC_ID = "KEY_DOC_ID"

        fun getStartIntent(context: Context, docId: String): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(KEY_DOC_ID, docId)
            return intent
        }
    }
}
