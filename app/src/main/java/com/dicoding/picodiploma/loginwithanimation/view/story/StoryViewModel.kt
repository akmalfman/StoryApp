package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val repository: UserRepository) : ViewModel() {

    val isLoading: LiveData<Boolean> = repository.isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun addStory(token:String, file: MultipartBody.Part, description: RequestBody) =
        repository.addStory(token,file, description)

}