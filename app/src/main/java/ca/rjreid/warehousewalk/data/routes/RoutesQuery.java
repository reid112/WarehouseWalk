package ca.rjreid.warehousewalk.data.routes;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoutesQuery {
    @GET("tours")
    Call<List<Route>> getRoutes();

    @GET("tours/{id}")
    Call<RouteDetails> getRouteDetails(@Path("id") int id);

    @POST("tours/{id}/vote_up")
    Call<ResponseBody> voteUp(@Path("id") int id);

    @POST("tours/{id}/vote_down")
    Call<ResponseBody> voteDown(@Path("id") int id);
}

