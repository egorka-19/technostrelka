package com.example.main_screen.api;

import com.example.main_screen.api.dto.AssistantChatRequestDto;
import com.example.main_screen.api.dto.AssistantChatResponseDto;
import com.example.main_screen.api.dto.EventCategoryDto;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.api.dto.FavoriteStatusResponseDto;
import com.example.main_screen.api.dto.LoginBody;
import com.example.main_screen.api.dto.RatingDto;
import com.example.main_screen.api.dto.RegisterBody;
import com.example.main_screen.api.dto.ReviewDto;
import com.example.main_screen.api.dto.ReviewUpsertBody;
import com.example.main_screen.api.dto.RouteQuizRequestDto;
import com.example.main_screen.api.dto.RouteQuizResponseDto;
import com.example.main_screen.api.dto.TokenResponseDto;
import com.example.main_screen.api.dto.UrlsResponseDto;
import com.example.main_screen.api.dto.UserMeDto;

import java.util.List;

import com.google.gson.JsonElement;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Контракт с бекендом Technostrelka (см. Heard_UDM_backend docs/API_CONTRACTS.md).
 * {@link com.example.main_screen.BuildConfig#API_BASE_URL} должен заканчиваться на {@code /api/v1/}.
 */
public interface ApiService {

    @POST("auth/register")
    Call<TokenResponseDto> register(@Body RegisterBody body);

    @POST("auth/login")
    Call<TokenResponseDto> login(@Body LoginBody body);

    @GET("users/me")
    Call<UserMeDto> getMe();

    /**
     * Тело ответа парсится вручную ({@link com.example.main_screen.api.dto.HomeCategoriesJsonParser}),
     * т.к. бекенд может отдавать массив или объект с массивом и разные имена полей.
     */
    @GET("home-categories")
    Call<JsonElement> getHomeCategories();

    @GET("events")
    Call<List<EventItemDto>> listEvents(
            @Query("type") String type,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("events/search")
    Call<List<EventItemDto>> searchEvents(
            @Query("q") String q,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("events/{event_id}")
    Call<EventItemDto> getEvent(@Path("event_id") String eventId);

    @GET("events/{event_id}/rating-summary")
    Call<RatingDto> getEventRatingSummary(@Path("event_id") String eventId);

    @GET("events/{event_id}/reviews")
    Call<List<ReviewDto>> listReviews(@Path("event_id") String eventId);

    @POST("events/{event_id}/reviews")
    Call<ReviewDto> createOrUpdateReview(@Path("event_id") String eventId, @Body ReviewUpsertBody body);

    @Multipart
    @POST("uploads/review-photos")
    Call<UrlsResponseDto> uploadReviewPhotos(
            @Part("event_id") RequestBody eventId,
            @Part List<MultipartBody.Part> files
    );

    @GET("users/me/favorites")
    Call<List<EventItemDto>> listMyFavorites();

    @PUT("users/me/favorites/{event_id}")
    Call<ResponseBody> addFavorite(@Path("event_id") String eventId);

    @DELETE("users/me/favorites/{event_id}")
    Call<ResponseBody> removeFavorite(@Path("event_id") String eventId);

    @GET("users/me/favorites/status")
    Call<FavoriteStatusResponseDto> getFavoritesStatus(@Query("event_ids") List<String> eventIds);

    @POST("assistant/route-quiz")
    Call<RouteQuizResponseDto> routeQuiz(@Body RouteQuizRequestDto body);

    @POST("assistant/chat")
    Call<AssistantChatResponseDto> assistantChat(@Body AssistantChatRequestDto body);

    @Multipart
    @POST("users/me/avatar")
    Call<UserMeDto> uploadAvatar(@Part MultipartBody.Part file);
}
