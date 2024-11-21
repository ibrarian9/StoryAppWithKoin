package com.app.intermediatesubmission.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.intermediatesubmission.R
import com.app.intermediatesubmission.databinding.ActivityLoginBinding
import com.app.intermediatesubmission.di.Injection.messageToast
import com.app.intermediatesubmission.di.models.RequestLogin
import com.app.intermediatesubmission.ui.listStory.ListStoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var bind: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.apply {

            loginViewModel.loading.observe(this@LoginActivity) { isLoading ->
                loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            loginViewModel.getSession().observe(this@LoginActivity) { user ->
                if (user.isLogin){
                    alertSuccess()
                }
            }

            loginViewModel.loginResult.observe(this@LoginActivity) { result ->
                result.onFailure {
                    loginButton.isEnabled = true
                    alertFailed()
                }
            }

            loginButton.setOnClickListener {

                loginButton.isEnabled = false

                val dataEmail = edEmail.text.toString().trim()
                val dataPassword = edPassword.text.toString().trim()

                when {
                    dataEmail.isEmpty() -> checkData("Email Masih Kosong")
                    dataPassword.isEmpty() -> checkData("Password masih kosong")
                    else -> {
                        val inputLogin = RequestLogin(dataEmail, dataPassword)
                        loginViewModel.postLogin(inputLogin)
                        loginButton.isEnabled = true
                    }
                }
            }

        }
        animation()
    }

    private fun checkData(pesan: String) {
        bind.loginButton.isEnabled = true
        messageToast(this@LoginActivity,pesan)
    }

    private fun alertFailed() {
        AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage("Login Gagal")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun alertSuccess() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah")
            setMessage("Login Berhasil")
            setPositiveButton("lanjut") { _, _ ->
                val i = Intent(context, ListStoryActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            create()
            show()
        }
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(bind.titleTextView, View.ALPHA, 1f).setDuration(200)
        val message =
            ObjectAnimator.ofFloat(bind.messageTextView, View.ALPHA, 1f).setDuration(200)
        val emailTextView =
            ObjectAnimator.ofFloat(bind.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(bind.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordTextView =
            ObjectAnimator.ofFloat(bind.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(bind.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(bind.loginButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 200
        }.start()
    }
}