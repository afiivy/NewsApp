package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class NewsAdapter extends ArrayAdapter<News> {
    public static final String LOG_TAG = NewsAdapter.class.getSimpleName ();

    public NewsAdapter(Context context, List<News> news) {
        super ( context, 0, news );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from ( getContext () ).inflate (
                    R.layout.news_list, parent, false );

        }

        News currentNews = getItem ( position );

        ImageView newsImageView = convertView.findViewById ( R.id.image );
        String imageUrl = Objects.requireNonNull ( currentNews ).getImageUrl ();
        if (imageUrl != null) {
            Picasso.get ().load ( imageUrl ).into ( newsImageView );

        } else {
            Picasso.get ().load ( R.drawable.ic_launcher_background ).into ( newsImageView );
        }


        TextView topicTextView = convertView.findViewById ( R.id.section );
        topicTextView.setText ( currentNews.getTopic () );


        TextView newsAuthorTextView = convertView.findViewById ( R.id.authors_name );
        String author = currentNews.getAuthor ();

        if (author != null) {
            newsAuthorTextView.setText ( author );
        } else {
            newsAuthorTextView.setVisibility ( View.GONE );
        }

        TextView newsTitleTextView = convertView.findViewById ( R.id.title );
        newsTitleTextView.setText ( currentNews.getTitle () );

        TextView newswebUrlTextView = convertView.findViewById ( R.id.webUrl );
        newswebUrlTextView.setText ( currentNews.getUrl () );


        TextView newsDateTextView = convertView.findViewById ( R.id.date );
        newsDateTextView.setText ( currentNews.getDate () );


        return convertView;


    }
}
