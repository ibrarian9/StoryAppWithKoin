package com.app.intermediatesubmission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.intermediatesubmission.R
import com.app.intermediatesubmission.databinding.ActivityWelcomeBinding
import com.app.intermediatesubmission.ui.listStory.ListStoryActivity
import com.app.intermediatesubmission.ui.listStory.ListStoryViewModel
import com.app.intermediatesubmission.ui.login.LoginActivity
import com.app.intermediatesubmission.ui.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityWelcomeBinding
    private val listStoryViewModel: ListStoryViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        listStoryViewModel.getSession().observe(this@WelcomeActivity) {
            if (it.isLogin) {
                val i = Intent(this@WelcomeActivity, ListStoryActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fullscreenApp()

        bind.apply {
            loginButton.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            }

            registerButton.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, RegisterActivity::class.java))
            }
        }

        animation()
    }

    private fun fullscreenApp() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun animation() {
        ObjectAnimator.ofFloat(bind.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(bind.loginButton, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(bind.registerButton, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(bind.titleTextView, View.ALPHA, 1f).setDuration(300)
        val desc = ObjectAnimator.ofFloat(bind.descTextView, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}