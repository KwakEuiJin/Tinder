package com.example.part3_chapter5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.part3_chapter5.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()




    }



    private fun initEmailAndPasswordEditText() {
        binding.emailEditText.addTextChangedListener {
            val enable=binding.emailEditText.text.isNotEmpty()&&binding.passwordEditText.text.isNotEmpty()
            binding.loginButton.isEnabled=enable
            binding.signUpButton.isEnabled=enable
        }
        binding.passwordEditText.addTextChangedListener {
            val enable=binding.emailEditText.text.isNotEmpty()&&binding.passwordEditText.text.isNotEmpty()
            binding.loginButton.isEnabled=enable
            binding.signUpButton.isEnabled=enable
        }
    }

    private fun initLoginButton() {
        binding.loginButton.setOnClickListener {
            if (binding.emailEditText.text.isEmpty()||binding.passwordEditText.text.isEmpty()){
                return@setOnClickListener
            }
            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful){
                        handleSuccessLogin()
                    } else{
                        Toast.makeText(this,"오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



    private fun initSignUpButton() {
        binding.signUpButton.setOnClickListener {
            if (binding.emailEditText.text.isEmpty()||binding.passwordEditText.text.isEmpty()){
                return@setOnClickListener
            }
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {  task->              //this를 넘겨보기
                    if (task.isSuccessful){
                        Toast.makeText(this,"회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun initFacebookLoginButton() {
        binding.facebookLoginButton.setPermissions("email","public_profile")
        binding.facebookLoginButton.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful){
                            handleSuccessLogin()
                        }else{
                            Toast.makeText(this@LoginActivity,"로그인 상황 중 에러발생", Toast.LENGTH_SHORT).show()
                        }

                    }
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity,"로그인 상황 중 에러발생", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun handleSuccessLogin(){
        if (auth.currentUser==null){
            Toast.makeText(this,"로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId= auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String,Any>()
        user["userId"] = userId
        currentUserDB.updateChildren(user)
        finish()
    }

    private fun getInputPassword() = binding.passwordEditText.text.toString()

    private fun getInputEmail() = binding.emailEditText.text.toString()


}