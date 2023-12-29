package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultState
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private val storyViewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.uploadButton.setOnClickListener { uploadStory(user.token) }
                showLoading(true)
            }
            binding.galleryButton.setOnClickListener { startGallery() }

        }

        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun     uploadStory(token:String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            showLoading(true)

            storyViewModel.addStory(
                token,
                multipartBody,
                binding.descriptionEditText.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            ).observe(this) {
                if (it != null) {
                    when (it) {
                        is ResultState.Success -> {
//                            showLoading(false)
//                            val intent = Intent(this@StoryActivity, MainActivity::class.java)
//                            intent.flags =
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                            startActivity(intent)
                             }

                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Error -> {
                            showLoading(false)
                        }
                        else -> {}
                    }
                }
            }
        }
    }


    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(
            buffer,
            0,
            length
        )
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    private fun showLoading(state: Boolean) {
        storyViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private const val MAXIMAL_SIZE = 1000000
    }
}