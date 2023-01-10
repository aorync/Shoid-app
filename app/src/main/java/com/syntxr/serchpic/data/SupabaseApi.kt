package com.syntxr.serchpic.data

import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.model.comment.Comment
import com.syntxr.serchpic.model.comment.CommentResponse
import com.syntxr.serchpic.model.explore.ExploreRensponse
import com.syntxr.serchpic.model.favorite.Favorite
import com.syntxr.serchpic.model.favorite.FavoriteRensponse
import com.syntxr.serchpic.model.follow.Follow
import com.syntxr.serchpic.model.follow.FollowResponse
import com.syntxr.serchpic.model.follow.FollowResponseItem
import com.syntxr.serchpic.model.post.AddPost
import com.syntxr.serchpic.model.post.PostRensponse
import com.syntxr.serchpic.model.post.UpdatePost
import com.syntxr.serchpic.model.register.Register
import com.syntxr.serchpic.model.register.RegisterRensponse
import com.syntxr.serchpic.model.register.UserRensponse
import com.syntxr.serchpic.model.register.UpdateDataUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {
    @POST("user_list")
    suspend fun register(
        @Body register: Register,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer : String = Api.PREFER
    ) : UserRensponse

    @GET("user_list")
    suspend fun getUserData(
        @Query("id") id : String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : UserRensponse

    @GET("user_list")
    suspend fun login(
        @Query("select") select  :String,
        @Query("email") email : String,
        @Query("password") password : String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : UserRensponse

    @GET("user_list")
    suspend fun isUsernameExist(
        @Query("select") select: String,
        @Query("username") username: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : UserRensponse

    @GET("post_list")
    suspend fun homeExplore(
        @Query("select") select: String,
//        @Query("user_id") userId : String,
        @Header("apikey") apiKey: String = Api.APIKEY,
    ) : ExploreRensponse

    @GET("post_list")
    suspend fun getOtherPost(
        @Query("select") select: String,
        @Query("id") id: String,
        @Header("apikey")  apiKey: String = Api.APIKEY
    ) : ExploreRensponse

    @POST("post_list")
    suspend fun uploadPost(
        @Body addPost: AddPost,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer: String = Api.PREFER
    ) : PostRensponse

    @GET("post_list")
    suspend fun getPost(
        @Query("user_id") userId : String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : PostRensponse

    @GET("post_list")
    suspend fun getPostSearch(
        @Query("title") title : String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : ExploreRensponse

    @GET("user_list")
    suspend fun getUserSearch(
        @Query("username") username: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : UserRensponse

    @GET("post_list")
    suspend fun getPostDetail(
        @Query("id") id: String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : PostRensponse

    @GET("post_list")
    suspend fun getUserPost(
        @Query("user_id") userId: String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : ExploreRensponse

    @PATCH("user_list")
    suspend fun updateUserData(
        @Query("id") id: String,
        @Body updateDataUser: UpdateDataUser,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer: String = Api.PREFER
    ) : RegisterRensponse

    @PATCH("post_list")
    suspend fun updatePost(
        @Query("id") postId : String,
        @Body updatePost: UpdatePost,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer: String = Api.PREFER
    ) : PostRensponse

    @DELETE("post_list")
    suspend fun deletePost(
        @Query("id") id: String,
        @Header("apikey") apiKey: String = Api.APIKEY,
    ) : Response<Unit>

    @DELETE("favorite")
    suspend fun deletePostFavorite(
        @Query("post_id") postId: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : Response<Unit>

    @DELETE("comments")
    suspend fun deleteComments(
        @Query("post_id") postId: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : Response<Unit>
    @GET("favorite")
    suspend fun getFav(
        @Query("post_id") postId: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : FavoriteRensponse

    @GET("favorite")
    suspend fun isFav(
        @Query("user_id") userId: String,
        @Query("post_id") postId : String,
        @Header("apikey") apiKey: String = Api.APIKEY,
    ) : FavoriteRensponse

    @POST("favorite")
    suspend fun favorite(
        @Body favorite: Favorite,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer: String = Api.PREFER
    ) : FavoriteRensponse

    @DELETE("favorite")
    suspend fun unfavorite(
        @Query("id") id: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : Response<Unit>

    @GET("favorite")
    suspend fun getFavorite(
        @Query("user_id") userId: String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : FavoriteRensponse

    @GET("follows")
    suspend fun isFollow(
        @Query("user_id") userId: String,
        @Query("followers_id") followersId : String,
        @Header("apikey") apiKey: String = Api.APIKEY,
    ) : FollowResponse

    @POST("follows")
    suspend fun follow(
        @Body follow: Follow,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer: String = Api.PREFER
    ) : FollowResponse

    @GET("follows")
    suspend fun getFollower(
        @Query("user_id") userId: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : FollowResponse

    @DELETE("follows")
    suspend fun unfollow(
        @Query("id") id: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ) : Response<Unit>

    @GET("comments")
    suspend fun getComments(
        @Query("post_id") postId: String,
        @Query("select") select: String,
        @Header("apikey") apiKey: String = Api.APIKEY
    ): CommentResponse

    @POST("comments")
    suspend fun comments(
        @Body comment: Comment,
        @Header("apikey") apiKey: String = Api.APIKEY,
        @Header("Prefer") prefer: String = Api.PREFER
    ) : CommentResponse
}