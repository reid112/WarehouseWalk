package ca.rjreid.warehousewalk.ui.map;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String EXTRA_ROUTE_ID = "EXTRA_ROUTE_ID";

    private GoogleMap map;
    private RouteDetails routeDetails;
    private boolean hasBeenInitialized = false;

    @BindView(R.id.checkmark) ImageView checkmark;
    @BindView(R.id.thanks_text_view) TextView thanksTextView;
    @BindView(R.id.vote_up_button) Button voteUpButton;
    @BindView(R.id.vote_down_button) Button voteDownButton;

    public static Intent createIntent(Context context, int routeId) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_ROUTE_ID, routeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setHomeAsUpIndicatorToBack();

        int routeId = getIntent().getExtras().getInt(EXTRA_ROUTE_ID);
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
        if (hasBeenInitialized || map == null || routeDetails == null) {
            return;
        }

        hasBeenInitialized = true;

        for (Point point : routeDetails.getPoints()) {
            LatLng latlng = new LatLng(point.getLat(), point.getLng());
            map.addMarker(new MarkerOptions().position(latlng).title(point.getName()));
        }

        LatLng firstPoint = new LatLng(routeDetails.getPoints().get(0).getLat(), routeDetails.getPoints().get(0).getLng());
        map.moveCamera(CameraUpdateFactory.newLatLng(firstPoint));
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
        DataManager.getInstance().startVoteUpQuery(routeDetails.getId(), new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showCheck();
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void showCheck() {
        checkmark.setVisibility(View.VISIBLE);
        thanksTextView.setVisibility(View.VISIBLE);

        voteUpButton.setVisibility(View.GONE);
        voteDownButton.setVisibility(View.GONE);
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
    protected int getLayoutId() {
        return R.layout.activity_map;
    }
}
