package com.example.part3_chapter5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputBinding
import android.widget.Toast
import com.example.part3_chapter5.databinding.ActivityLoginBinding
import com.example.part3_chapter5.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null){
            startActivity(Intent(this,LoginActivity::class.java))
        } else{
            startActivity(Intent(this,LikeActivity::class.java))
            finish()
        }
    }
}