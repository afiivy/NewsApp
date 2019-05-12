package com.example.android.newsapp;

import org.apache.commons.lang3.StringUtils;

public class News {

    private String mWebUrl;
    private String mSectionName;
    private String mAuthorsName;
    private String mAuthorsLastname;
    private String mDate;
    private String mImageUrl;
    private String mWebTitle;

    public News(String title, String sectionName, String authorsName,
                String authorsLastname, String date, String url, String imageUrl) {
        mWebTitle = title;
        mSectionName = sectionName;
        mAuthorsName = authorsName;
        mAuthorsLastname = authorsLastname;
        mWebUrl = url;
        mDate = date;
        mImageUrl = imageUrl;

    }

    public String getTitle() {
        return mWebTitle;
    }

    public String getTopic() {
        return mSectionName;
    }


    public String getAuthor() {
        if (mAuthorsName != null && mAuthorsLastname != null) {
            return StringUtils.capitalize ( mAuthorsName ) + " " + StringUtils.capitalize ( mAuthorsLastname );

        }
        return null;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mWebUrl;
    }


    public String getImageUrl() {
        return mImageUrl;
    }


}
