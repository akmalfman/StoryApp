package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import org.json.JSONException
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var factory: ViewModelFactory
    private val signupViewModel: SignupViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        binding.signupButton.setOnClickListener {
            signupViewModel.postRegister(
                binding.edRegisterName.text.toString(),
                binding.edRegisterEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            )
        }

        signupViewModel.successMessage.observe(this) { successMessage ->
            successMessage?.let {
                showToast(this, it)
                finish()
            }
        }

        signupViewModel.errorLiveData.observe(this) { errorMessage ->
            errorMessage?.let {
                try {
                    val jsonError = JSONObject(errorMessage)
                    val message = jsonError.getString("message")

                    showToast(this, message)
                } catch (e: JSONException) {
                    showToast(this, errorMessage)
                }
            }
        }

        signupViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        signupViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }
}