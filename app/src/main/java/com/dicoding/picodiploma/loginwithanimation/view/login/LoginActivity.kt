package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultState
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        factory = ViewModelFactory.getInstance(this)
        setupView()
        setupAction()
    }

    private fun setupView() {
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            loginViewModel.postLogin(
                binding.edLoginEmail.text.toString(),
                binding.edLoginPassword.text.toString()
            ).observe(this) { user ->
                if (user != null) {
                    when (user) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            saveSession(
                                UserModel(
                                    binding.edLoginEmail.text.toString(),
                                    "${user.data.loginResult?.token}",
                                    true
                                )
                            )

                            AlertDialog.Builder(this).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil login. Sudah tidak sabar untuk Menulis ya?")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }

                            showLoading(false)
                        }

                        is ResultState.Error -> {
                            Toast.makeText(this,user.error,Toast.LENGTH_SHORT).show()
                            showLoading(false)
                        }
                        else -> {

                        }
                    }
                }
            }
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun saveSession(session: UserModel) {
        loginViewModel.saveSession(session)
    }

    private fun showLoading(state: Boolean) {
        loginViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

}