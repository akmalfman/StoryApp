package com.dicoding.picodiploma.loginwithanimation.data

import ApiService
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultState
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    private val _registerUser = MutableLiveData<RegisterResponse>()

    private val _listStoryLocation = MutableLiveData<StoryResponse>()
    val listStoryLocation: LiveData<StoryResponse> = _listStoryLocation

    private val _listStoryDetail = MutableLiveData<DetailResponse>()
    val listStoryDetail: LiveData<DetailResponse> = _listStoryDetail

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: MutableLiveData<String?> = _successMessage

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: MutableLiveData<String?> = _errorLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerUser.value = response.body()
                    val successMessage = response.body()?.message
                    _successMessage.postValue(successMessage)


                } else {
                    val errorMessage = response.errorBody()?.string()
                    _errorLiveData.postValue(errorMessage)
                    Log.e("postRegister", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("postRegister", "onFailure: ${t.message}")
            }
        })
    }

    fun postLogin(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.login(email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(userPreference,apiService)
            }
        ).liveData
    }

    fun getStoryDetail(token:String,id: String) {
        _isLoading.value = true
        val client = apiService.getDetailStories("Bearer $token",id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStoryDetail.value = response.body()


                } else {
                    Log.e("detailStory", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("detailStory", "onFailure: ${t.message}")
            }
        })
    }

    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.uploadImage("Bearer $token", file, description)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }

    }

    fun getStoryWithLocation(token: String) {
        _isLoading.value = true
        val client = apiService.getStoriesWithLocation("Bearer $token")

        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _listStoryLocation.value = response.body()
                } else {
                    Log.e(
                        "getStory",
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e("getStory", "onFailure: ${t.message.toString()}")
            }
        })
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}