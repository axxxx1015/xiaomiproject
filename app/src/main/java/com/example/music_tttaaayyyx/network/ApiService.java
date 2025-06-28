package com.example.music_tttaaayyyx.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    
    @GET("music/homePage")
    Call<HomePageResponse> getHomePage(
        @Query("current") int current,
        @Query("size") int size
    );
} 