package com.nishant.ecko.data.network;

import com.nishant.ecko.util.AppConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    static ApiInterface apiInterface;
    public static ApiInterface getApiInterface(){
        if(apiInterface==null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;

    }
}
