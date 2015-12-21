package com.example.shaunpanjabi.top_movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonStr = extras.getString(Intent.EXTRA_TEXT);
            TextView description = (TextView) findViewById(R.id.movie_description);
            TextView title = (TextView) findViewById(R.id.movie_title);
            TextView release_date = (TextView) findViewById(R.id.release_date);
            TextView vote_average = (TextView) findViewById(R.id.vote_average);
            ImageView poster = (ImageView) findViewById(R.id.poster_img_view);

            String poster_path = "";
            try {
                description.setText(getFromMovieJson(jsonStr, "overview"));
                title.setText(getFromMovieJson(jsonStr, "title"));
                release_date.setText(getFromMovieJson(jsonStr, "release_date"));
                vote_average.setText(getFromMovieJson(jsonStr, "vote_average"));
                poster_path = "https://image.tmdb.org/t/p/w185" + getFromMovieJson(jsonStr, "poster_path");
            } catch (JSONException e) {
                description.setText("I dont care");
            }

            Picasso.with(this)
                    .load(poster_path)
                    .fit()
                    .into(poster);
        }
    }

    private String getFromMovieJson(String movieString, String param)
            throws JSONException{
        JSONObject movieJson = new JSONObject(movieString);
        return movieJson.getString(param);
    }

//    @Override
//    protected View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                Bundle savedInstanceState){
//        View rootView = inflater.inflate(R.layout.activity_detail, container, false);
//
//        Intent intent = this.getIntent();
//
//        return rootView;
//
//    }
}
