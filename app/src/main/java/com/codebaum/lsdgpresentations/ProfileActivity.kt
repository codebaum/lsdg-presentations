package com.codebaum.lsdgpresentations

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codebaum.lsdgpresentations.data.Repository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val storedUser = Repository.getStoredUser()
        if (storedUser == null) {
            finish()
            return
        }

        showDetails(storedUser)

        btn_sign_out.setOnClickListener {
            Repository.signOut()
            setResult(Activity.RESULT_CANCELED)
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
