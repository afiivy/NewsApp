package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String LOG_TAG = NewsActivity.class.getName ();

    private static final String URL_REQUEST =
            "https://content.guardianapis.com/search?";
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.news_activity );


        ListView newsListView = findViewById ( R.id.list );

        mEmptyStateTextView = findViewById ( R.id.empty_view );
        newsListView.setEmptyView ( mEmptyStateTextView );

        mAdapter = new NewsAdapter ( this, new ArrayList<News> () );
        newsListView.setAdapter ( mAdapter );

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences ( this );
        prefs.registerOnSharedPreferenceChangeListener ( this );

        newsListView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = mAdapter.getItem ( position );
                Uri newsUri = Uri.parse ( Objects.requireNonNull ( currentNews ).getUrl () );
                Intent websiteIntent = new Intent ( Intent.ACTION_VIEW, newsUri );
                startActivity ( websiteIntent );
            }

        } );
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService ( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = (connMgr).getActiveNetworkInfo ();
        if (networkInfo != null && networkInfo.isConnected ()) {
            LoaderManager loaderManager = getLoaderManager ();
            loaderManager.initLoader ( NEWS_LOADER_ID, null, this );

        } else {
            View loadingIndicator = findViewById ( R.id.loading_indicator );
            loadingIndicator.setVisibility ( View.GONE );
            mEmptyStateTextView.setText ( R.string.no_internet_connection );
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals ( getString ( R.string.settings_number_of_news_key ) ) ||
                key.equals ( getString ( R.string.settings_order_by_key ) )) {
            mAdapter.clear ();

            mEmptyStateTextView.setVisibility ( View.GONE );
            View loadingIndicator = findViewById ( R.id.loading_indicator );
            loadingIndicator.setVisibility ( View.VISIBLE );
            getLoaderManager ().restartLoader ( NEWS_LOADER_ID, null, this );

        }

    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences ( this );
        String numberOfNews = sharedPrefs.getString (
                getString ( R.string.settings_number_of_news_key ),
                getString ( R.string.settings_number_of_news_default ) );
        String orderBy = sharedPrefs.getString (
                getString ( R.string.settings_order_by_key ),
                getString ( R.string.settings_order_by_default )

        );


        Uri baseUri = Uri.parse ( URL_REQUEST );
        Uri.Builder uriBuilder = baseUri.buildUpon ();
        uriBuilder.appendQueryParameter ( "show-fields", "thumbnail" );
        uriBuilder.appendQueryParameter ( "show-tags", "contributor" );
        uriBuilder.appendQueryParameter ( "page-size", numberOfNews );
        uriBuilder.appendQueryParameter ( "order-by", orderBy );
        uriBuilder.appendQueryParameter ( "api-key", "fdfaedea-ffcf-4989-9f28-b89c216f0aca" );
        Log.i ( "NewsActivity", uriBuilder.toString () );
        return new NewsLoader ( this, uriBuilder.toString () );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        View loadingIndicator = findViewById ( R.id.loading_indicator );
        loadingIndicator.setVisibility ( View.GONE );
        mEmptyStateTextView.setText ( R.string.no_news );
        mAdapter.clear ();
        if (data != null && !data.isEmpty ()) {
            mAdapter.addAll ( data );

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        mAdapter.clear ();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate ( R.menu.main, menu );
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent ( this, SettingsActivity.class );
            startActivity ( settingsIntent );
            return true;

        }
        return super.onOptionsItemSelected ( item );


    }
}
