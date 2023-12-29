package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    val successMessage: MutableLiveData<String?> = repository.successMessage
    val errorLiveData: MutableLiveData<String?> = repository.errorLiveData
    val isLoading: LiveData<Boolean> = repository.isLoading

    fun postRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.postRegister(name, email, password)
        }
    }


}