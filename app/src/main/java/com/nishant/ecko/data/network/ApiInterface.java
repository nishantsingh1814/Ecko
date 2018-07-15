package com.nishant.ecko.data.network;

import com.nishant.ecko.data.network.model.FlickrSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("services/rest/")
    Call<FlickrSearchResponse> searchImages(
            @Query("method") String method,
            @Query("api_key") String apiKey,
            @Query("tags") String tags,
            @Query("page") long page,
            @Query("format") String format,
            @Query("nojsoncallback") int callback,
            @Query("per_page") int perPage
    );
}
