package com.example.shaunpanjabi.top_movies;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.InputStream;
import java.io.InputStreamReader;


public class MoviesFragment extends Fragment {

    public MoviesFragment() {
    }

    public ImageAdapter mImgAdapter;
    public String jsonString = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateTopMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_topmovies);
        mImgAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(mImgAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();

                JSONObject movieJson;
                try {
                    movieJson = getMovieJson(jsonString, position);

                } catch (JSONException e) {
                    movieJson = null;
                }

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieJson.toString());
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    public JSONObject getMovieJson(String topMoviesJsonStr, int position)
            throws JSONException {

        final String OWM_RESULTS = "results";
        final JSONObject movieJson;
        movieJson = new JSONObject(topMoviesJsonStr);
        JSONArray topMoviesArray = movieJson.getJSONArray(OWM_RESULTS);

        return topMoviesArray.getJSONObject(position);
    }

    private final String LOG_TAG = FetchTopMoviesTask.class.getSimpleName();

    private void updateTopMovies() {
        FetchTopMoviesTask movieTask = new FetchTopMoviesTask();
        movieTask.execute();

    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopMovies();
    }

    public class FetchTopMoviesTask extends AsyncTask<String, Void, String[]> {

        private String[] getTopMovieImgsFromJson(String topMoviesJsonStr)
            throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_POSTER_PATH = "poster_path";

            JSONObject topMoviesJson = new JSONObject(topMoviesJsonStr);
            JSONArray topMoviesArray = topMoviesJson.getJSONArray(OWM_RESULTS);
            String[] resultStrs = new String[topMoviesArray.length()];

            for(int i=0; i < topMoviesArray.length(); i++){
                String posterPath;
                String url = "https://image.tmdb.org/t/p/w185";

                JSONObject posterJson = topMoviesArray.getJSONObject(i);
                posterPath = posterJson.getString(OWM_POSTER_PATH);
                resultStrs[i] = url + posterPath;
                Log.v(LOG_TAG, "Paths: " + url + posterPath);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String topMoviesJsonStr = null;

            String api_key = BuildConfig.MOVIE_DB_API_KEY;

            try {
                final String MOVIES_BASE_URL =
                        "https://api.themoviedb.org/3/movie/top_rated";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, api_key)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null ) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                topMoviesJsonStr = buffer.toString();
                jsonString = topMoviesJsonStr;

                Log.v(LOG_TAG, "Top Movies JSON String: " + topMoviesJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getTopMovieImgsFromJson(topMoviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            if (result != null) {
                mImgAdapter.fillImages(result);
            }
            mImgAdapter.notifyDataSetChanged();
        }
    }
}
