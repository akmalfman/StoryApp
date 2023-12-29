package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.DetailResponse

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    val listStoryDetail: LiveData<DetailResponse> = repository.listStoryDetail

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesDetail(token:String,id: String) = repository.getStoryDetail(token,id)

}