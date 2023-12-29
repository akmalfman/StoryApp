package com.dicoding.picodiploma.loginwithanimation.view.maps


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    val listStoryLocation: LiveData<StoryResponse> = repository.listStoryLocation

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(token:String) {
        repository.getStoryWithLocation(token)
    }
}