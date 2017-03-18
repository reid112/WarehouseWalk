package ca.rjreid.warehousewalk.data;


import java.util.List;

import ca.rjreid.warehousewalk.data.directions.DirectionsQuery;
import ca.rjreid.warehousewalk.data.routes.Route;
import ca.rjreid.warehousewalk.data.routes.RouteDetails;
import ca.rjreid.warehousewalk.data.routes.RoutesQuery;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataManager {
    //region Constants
    private static final String ENDPOINT = "https://arcane-hollows-68750.herokuapp.com/api/";
    private static final String DIRECTIONS_ENDPOINT = "http://maps.googleapis.com/maps/api/";
    private final Retrofit retrofit = initRetrofit();
    private static volatile DataManager instance;
    private static final Object lock = new Object();
    //endregion

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }

        return instance;
    }

    //region Helpers
    private Retrofit initRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    //endregion

    //region Queries
    public void startRoutesQuery(Callback<List<Route>> callback) {
        RoutesQuery routesQuery = retrofit.create(RoutesQuery.class);
        Call<List<Route>> call = routesQuery.getRoutes();
        call.enqueue(callback);
    }

    public void startRouteDetailsQuery(int id, Callback<RouteDetails> callback) {
        RoutesQuery routesQuery = retrofit.create(RoutesQuery.class);
        Call<RouteDetails> call = routesQuery.getRouteDetails(id);
        call.enqueue(callback);
    }

    public void startVoteUpQuery(int id, Callback<ResponseBody> callback) {
        RoutesQuery routesQuery = retrofit.create(RoutesQuery.class);
        Call<ResponseBody> call = routesQuery.voteUp(id);
        call.enqueue(callback);
    }

    public void startVoteDownQuery(int id, Callback<ResponseBody> callback) {
        RoutesQuery routesQuery = retrofit.create(RoutesQuery.class);
        Call<ResponseBody> call = routesQuery.voteDown(id);
        call.enqueue(callback);
    }

    public void startDirectionsQuery(double srclat, double srclng, double destlat, double destlng, Callback<ResponseBody> callback) {
        String src = srclat + "," + srclng;
        String dest = destlat + "," + destlng;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DIRECTIONS_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsQuery directionsQuery = retrofit.create(DirectionsQuery.class);
        Call<ResponseBody> call = directionsQuery.getDirections(src, dest, false, "metric", "walking");
        call.enqueue(callback);
    }
    //endregion
}