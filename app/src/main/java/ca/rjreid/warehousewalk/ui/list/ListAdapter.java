package ca.rjreid.warehousewalk.ui.list;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.rjreid.warehousewalk.R;
import ca.rjreid.warehousewalk.data.routes.Route;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RouteRowHolder> {

    public interface Listener {
        void routeClicked(Route route);
    }

    public static class RouteRowHolder extends RecyclerView.ViewHolder {

        private Route route;
        private Listener listener;

        @BindView(R.id.icon_image_view) ImageView iconImageView;
        @BindView(R.id.route_name_text_view) TextView nameTextView;
        @BindView(R.id.thumbs_up_text_view) TextView thumbsUpTextView;
        @BindView(R.id.thumbs_down_text_view) TextView thumbsDownTextView;

        public RouteRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindRouteRow(Context context, Route route, Listener listener) {
            this.route = route;
            this.listener = listener;

            loadIcon(context, route.getIcon());

            nameTextView.setText(route.getName());
            thumbsUpTextView.setText(Integer.toString(route.getUpVotes()));
            thumbsDownTextView.setText(Integer.toString(route.getDownVotes()));
        }

        private void loadIcon(Context context, String imageUrl) {
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(getProgressBarIndeterminate(context))
                    .error(R.drawable.ic_android)
                    .crossFade()
                    .into(iconImageView);
        }

        @OnClick(R.id.route_row_container)
        public void rowClicked() {
            if (listener != null) {
                listener.routeClicked(route);
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
    }

    private Context context;
    private List<Route> routes;
    private Listener listener;

    public ListAdapter(Context context, List<Route> routes, Listener listener) {
        this.context = context;
        this.routes = routes;
        this.listener = listener;
    }

    @Override
    public RouteRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_route_row, parent, false);
        return new RouteRowHolder(row);
    }

    @Override
    public void onBindViewHolder(RouteRowHolder holder, int position) {
        Route route = routes.get(position);
        holder.bindRouteRow(context, route, listener);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public void updateList(List<Route> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }
}
