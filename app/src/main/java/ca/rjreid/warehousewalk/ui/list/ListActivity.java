package ca.rjreid.warehousewalk.ui.list;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.rjreid.warehousewalk.R;
import ca.rjreid.warehousewalk.RequestCodes;
import ca.rjreid.warehousewalk.data.DataManager;
import ca.rjreid.warehousewalk.data.routes.Route;
import ca.rjreid.warehousewalk.ui.BaseActivity;
import ca.rjreid.warehousewalk.ui.map.MapActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends BaseActivity {

    private static final String TAG = ListActivity.class.getCanonicalName();
    private ListAdapter adapter;
    private Location location;

    @BindView(R.id.swipe_to_refresh) SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.list_recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        hideUpIndicator();
        initSwipeToRefresh();

        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoutes();
    }

    private void initSwipeToRefresh() {
        swipeToRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadRoutes();
                    }
                }
        );

    }

    private void checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        RequestCodes.REQUEST_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        RequestCodes.REQUEST_FINE_LOCATION);
            }
        } else {
            getLocation();
        }
    }

    private void loadRoutes() {
        swipeToRefresh.setRefreshing(true);

        DataManager.getInstance().startRoutesQuery(new Callback<List<Route>>() {
            @Override public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                List<Route> routes = response.body();

                bindRoutesList(sortRoutes(routes), new ListAdapter.Listener() {
                    @Override public void routeClicked(Route route) {
                        startActivity(MapActivity.createIntent(ListActivity.this, route.getId()));
                    }
                });
            }

            @Override public void onFailure(Call<List<Route>> call, Throwable t) {
                Toast.makeText(ListActivity.this, "Oops, something went wrong.  Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindRoutesList(List<Route> routes, ListAdapter.Listener rowClickListener) {
        swipeToRefresh.setRefreshing(false);

        recyclerView.setVisibility(View.VISIBLE);

        adapter = new ListAdapter(getApplicationContext(), routes, rowClickListener);
        initializeRecyclerView();
    }

    private List<Route> sortRoutes(List<Route> routes) {
        Collections.sort(routes, new Comparator<Route>(){
            public int compare(Route r1, Route r2){
                if(r1.getUpVotes() == r2.getUpVotes()) {
                    return 0;
                }

                return r1.getUpVotes() > r2.getUpVotes() ? -1 : 1;
            }
        });

        return routes;
    }

    private void initializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            }
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                ListActivity.this.location = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list;
    }
}
