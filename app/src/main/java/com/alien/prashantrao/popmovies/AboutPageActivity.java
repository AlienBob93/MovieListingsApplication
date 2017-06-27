package com.alien.prashantrao.popmovies;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.alien.prashantrao.popmovies.R;
import com.alien.prashantrao.popmovies.databinding.ActivityAboutPageBinding;

public class AboutPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        // set content view using DataBindingUtil
        ActivityAboutPageBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_page);

        // Find the toolbar view inside the activity layout
        this.setSupportActionBar(mBinding.layoutActionBar.toolbar);

        // set the toolBar text
        mBinding.layoutActionBar.toolbarTitle.setText(getString(R.string.menu_title_about));

        // set the actionBar back button
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // set the correct actionBar back button properties
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
