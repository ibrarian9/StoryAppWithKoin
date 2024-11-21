package com.app.intermediatesubmission.ui.register

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
import com.app.intermediatesubmission.databinding.ActivityRegisterBinding
import com.app.intermediatesubmission.di.Injection.messageToast
import com.app.intermediatesubmission.di.models.RequestRegister
import com.app.intermediatesubmission.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var bind: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.apply {

            registerViewModel.registerResult.observe(this@RegisterActivity) { result ->
                result.onSuccess {
                    alertSuccess()
                }.onFailure {
                    signupButton.isEnabled = true
                    alertFailed()
                }
            }

            registerViewModel.loading.observe(this@RegisterActivity) { isLoading ->
                loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            signupButton.setOnClickListener {

                signupButton.isEnabled = false

                val dataName = edName.text.toString().trim()
                val dataEmail = edEmail.text.toString().trim()
                val dataPassword = edPassword.text.toString().trim()

                when {
                    dataName.isEmpty() -> checkData("Nama Masih Kosong")
                    dataEmail.isEmpty() -> checkData("Email Masih Kosong")
                    dataPassword.isEmpty() -> checkData("Password Masih Kosong")
                    else -> {
                        val inputRegister = RequestRegister(
                            name = dataName,
                            email = dataEmail,
                            password = dataPassword
                        )
                        registerViewModel.postRegister(inputRegister)
                        signupButton.isEnabled = true
                    }
                }
            }
        }
        animation()
    }

    private fun checkData(pesan: String) {
        bind.signupButton.isEnabled = true
        messageToast(this@RegisterActivity, pesan)
    }

    private fun alertSuccess() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah")
            setMessage("Register Berhasil")
            setPositiveButton("Next") { _, _ ->
                val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            create()
            show()
        }
    }

    private fun alertFailed() {
        AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage("Register Gagal")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(bind.titleTextView, View.ALPHA, 1f).setDuration(300)
        val nameTextView =
            ObjectAnimator.ofFloat(bind.nameTextView, View.ALPHA, 1f).setDuration(300)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(bind.edNameLayout, View.ALPHA, 1f).setDuration(300)
        val emailTextView =
            ObjectAnimator.ofFloat(bind.emailTextView, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(bind.edEmailLayout, View.ALPHA, 1f).setDuration(300)
        val passwordTextView =
            ObjectAnimator.ofFloat(bind.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(bind.edPassLayout, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(bind.signupButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 300
        }.start()
    }
}