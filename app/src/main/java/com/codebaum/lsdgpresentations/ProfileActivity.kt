package com.codebaum.lsdgpresentations

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codebaum.lsdgpresentations.data.Repository
import com.codebaum.lsdgpresentations.utils.RESULT_SIGN_OUT
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setTitle(R.string.profile_title)

        val currentUser = repository.currentFirebaseUser
        if (currentUser == null) {
            finish()
            return
        }

        showDetails(currentUser)

        btn_sign_out.setOnClickListener {
            repository.signOut()
            setResult(RESULT_SIGN_OUT)
            finish()
        }
    }

    private fun showDetails(user: FirebaseUser) {
        user.apply {
            tv_name_value.text = displayName

            tv_email_value.text = email

            tv_phone_number_value.text = phoneNumber
        }
    }

    companion object {

        fun getStartIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
