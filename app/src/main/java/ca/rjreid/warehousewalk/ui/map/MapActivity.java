package ca.rjreid.warehousewalk.ui.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.rjreid.warehousewalk.R;
import ca.rjreid.warehousewalk.data.DataManager;
import ca.rjreid.warehousewalk.data.routes.Point;
import ca.rjreid.warehousewalk.data.routes.Route;
import ca.rjreid.warehousewalk.data.routes.RouteDetails;
import ca.rjreid.warehousewalk.ui.BaseActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String EXTRA_ROUTE_ID = "EXTRA_ROUTE_ID";
    public static final String EXTRA_ROUTE_NAME = "EXTRA_ROUTE_NAME";
    private static final String TAG = MapActivity.class.getCanonicalName();

    private GoogleMap map;
    private RouteDetails routeDetails;
    private boolean hasBeenInitialized = false;

    @BindView(R.id.voting_container) LinearLayout votingCont;
    @BindView(R.id.modal) LinearLayout modal;
    @BindView(R.id.modal_title) TextView modalTitle;
    @BindView(R.id.modal_image) ImageView modalImage;

    public static Intent createIntent(Context context, int routeId, String name) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_ROUTE_ID, routeId);
        intent.putExtra(EXTRA_ROUTE_NAME, name);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setHomeAsUpIndicatorToBack();

        int routeId = getIntent().getExtras().getInt(EXTRA_ROUTE_ID);
        String routeName = getIntent().getExtras().getString(EXTRA_ROUTE_NAME);
        setTitle(routeName);

        DataManager.getInstance().startRouteDetailsQuery(routeId, new Callback<RouteDetails>() {
            @Override public void onResponse(Call<RouteDetails> call, Response<RouteDetails> response) {
                routeDetails = response.body();
                initPoints();
            }

            @Override public void onFailure(Call<RouteDetails> call, Throwable t) {
                routeDetails = null;
                Toast.makeText(MapActivity.this, "Oops, something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initPoints() {
        if (hasBeenInitialized || map == null || routeDetails == null || routeDetails.getPoints() == null || routeDetails.getPoints().size() <= 0) {
            return;
        }

        hasBeenInitialized = true;
        Point prevPoint = null;

        for (Point point : routeDetails.getPoints()) {
            LatLng latlng = new LatLng(point.getLat(), point.getLng());
            map.addMarker(new MarkerOptions().position(latlng).title(point.getName()));

            if (prevPoint != null) {
                DataManager.getInstance().startDirectionsQuery(prevPoint.getLat(), prevPoint.getLng(), point.getLat(), point.getLng(), new Callback<ResponseBody>() {
                    @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                       try {
                           drawPath(response.body().string());
                       } catch (IOException e) {
                           Log.d(TAG, e.toString());
                       }
                    }

                    @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("TEST", "onFailure: BOOO");
                    }
                });
            }

            prevPoint = point;
        }

        LatLng firstPoint = new LatLng(routeDetails.getPoints().get(0).getLat(), routeDetails.getPoints().get(0).getLng());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15));
        map.setOnMarkerClickListener(this);
    }

    @OnClick(R.id.vote_up_button)
    public void voteUp() {
        DataManager.getInstance().startVoteUpQuery(routeDetails.getId(), new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showCheck();
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.vote_down_button)
    public void voteDown() {
        DataManager.getInstance().startVoteDownQuery(routeDetails.getId(), new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showCheck();
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void showCheck() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        votingCont.animate().translationY(-1 * px).setDuration(300);
    }

    public void drawPath(String  result) {

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = map.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    private void showInfo(Point point) {
        if (modal.getVisibility() == View.VISIBLE) {
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
            modal.startAnimation(bottomDown);
            modal.setVisibility(View.GONE);
        } else {
            modalTitle.setText(point.getName());

            Glide.with(getApplicationContext())
                    .load(point.getImage())
                    .centerCrop()
                    .error(R.drawable.ic_android)
                    .crossFade()
                    .into(modalImage);

            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            modal.startAnimation(bottomUp);
            modal.setVisibility(View.VISIBLE);
        }
    }

    Drawable getProgressBarIndeterminate(Context context) {
        final int[] attrs = {android.R.attr.indeterminateDrawable};
        final int attrs_indeterminateDrawable_index = 0;
        TypedArray a = context.obtainStyledAttributes(android.R.style.Widget_ProgressBar, attrs);
        try {
            return a.getDrawable(attrs_indeterminateDrawable_index);
        } finally {
            a.recycle();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        initPoints();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Point point : routeDetails.getPoints()) {
            if (point.getName().equals(marker.getTitle())) {
                showInfo(point);
                break;
            }
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map;
    }
}
