package ca.rjreid.warehousewalk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import ca.rjreid.warehousewalk.R;


public abstract class BaseActivity extends AppCompatActivity {
    @Nullable protected AppBarLayout appBarLayout;
    protected ActionBar actionBar;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getLayoutId());
        bindViews();
        onInitializeActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected final void onInitializeActionBar() {
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setHomeAsUpIndicatorToBack();
        }
    }

    private void bindViews() {
        appBarLayout = ButterKnife.findById(this, R.id.appbarlayout);
        toolbar = ButterKnife.findById(this, R.id.toolbar);
    }

    public void setTitle(String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void setTitle(int resId) {
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    public void setHomeAsUpIndicator(int resId) {
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(resId);
        }
    }

    public void setHomeAsUpIndicatorToBack() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    public void hideUpIndicator() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    abstract protected int getLayoutId();
}
