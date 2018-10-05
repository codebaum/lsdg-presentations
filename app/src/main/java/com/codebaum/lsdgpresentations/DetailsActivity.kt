package com.codebaum.lsdgpresentations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.codebaum.lsdgpresentations.data.PresentationMapper
import com.codebaum.lsdgpresentations.data.UserMapper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    /// name
    /// presenter
    /// state
    /// description
    /// starred

    private val presentationMapper = PresentationMapper()
    private val userMapper = UserMapper()

    private val db = FirebaseFirestore.getInstance()

    private lateinit var presentationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        presentationId = intent.getStringExtra(KEY_PRESENTATION_ID)

        fab_starred.setOnClickListener {
            Snackbar.make(it, "test", Snackbar.LENGTH_SHORT).show()
        }

        updateContent(presentationId)
        updateFAB()
    }

    private fun updateContent(docId: String) {
        db.collection("presentations").document(docId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

            documentSnapshot?.apply {
                val presentation = presentationMapper.from(documentSnapshot)
                tv_description_value.text = presentation.description

                title = presentation.name
            }
        }
    }

    private fun updateFAB() {
        db.collection("users").document("dVu3SiDyQZDh80Bgfwcr").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.apply {
                val user = userMapper.from(documentSnapshot)
                if (user.starredPresentations.contains(presentationId)) {
                    fab_starred.setImageResource(android.R.drawable.btn_star_big_on)
                }
            }
        }
    }

    companion object {

        const val KEY_PRESENTATION_ID = "KEY_PRESENTATION_ID"

        fun getStartIntent(context: Context, docId: String): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(KEY_PRESENTATION_ID, docId)
            return intent
        }
    }
}
