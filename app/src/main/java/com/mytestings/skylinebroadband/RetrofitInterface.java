package com.mytestings.skylinebroadband;

import java.util.List;

import Database.Entity;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    @GET("data")
    Call<List<Entity>> listRepos();
}
