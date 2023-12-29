package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                Log.d("sat", "onCreate: {$user}")
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }

            val id = intent.getStringExtra("story_id")
            Log.e("idbang", "onCreate: $id", )
            if (id != null) {
                detailViewModel.getStoriesDetail(user.token,id)
            }
        }
        detailViewModel.listStoryDetail.observe(this) { detailItems ->
            setDetailData(detailItems)
        }
    }

    private fun setDetailData(detailItems: DetailResponse) {
        binding.tvItemName.text = detailItems.story?.name
        binding.tvItemDesc.text = detailItems.story?.description
        Glide.with(this@DetailActivity)
            .load("${detailItems.story?.photoUrl}")
            .into(binding.imgItemPhoto)
    }
}