package ca.rjreid.warehousewalk.data.directions;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionsQuery {
    @GET("directions/json")
    Call<ResponseBody> getDirections(@Query("origin") String origin,
                                     @Query("destination") String destination,
                                     @Query("sensor") boolean sensor,
                                     @Query("units") String units,
                                     @Query("mode") String mode);
}
