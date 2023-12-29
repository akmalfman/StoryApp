import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse


    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ):StoryResponse

    @GET("stories/{id}")
     fun getDetailStories(
        @Header("Authorization") token: String,
        @Path("id") id : String)
     : Call<DetailResponse>

     @Multipart
     @POST("stories")
     suspend fun uploadImage(
         @Header("Authorization") token: String,
         @Part file: MultipartBody.Part,
         @Part("description") description: RequestBody,
     ): AddStoryResponse

    @GET("stories")
    fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): Call<StoryResponse>
}