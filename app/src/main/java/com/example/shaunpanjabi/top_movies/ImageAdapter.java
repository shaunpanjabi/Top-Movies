package com.example.shaunpanjabi.top_movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ImageView imageView;

    public String[] img_urls = new String[0];
    private double img_width;
    private double img_height;

//    Log.d("ApplicationTagName", "Display width in px is " + width);

    public ImageAdapter(Context c) {
        mContext = c;
        int width = c.getResources().getDisplayMetrics().widthPixels;
        img_width = width / 2;
        double img_ratio = 0.66; // This assumes all pictures are the same aspect ratio
        img_height = img_width / img_ratio;
    }

    public int getCount() {
        return img_urls.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int) img_width, (int) img_height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        if (img_urls != null) {
            Picasso.with(mContext)
                    .load(img_urls[position])
                    .fit()
                    .into(imageView);
        }
//        imageView.setImageResource(mThumbsIds[position]);
        return imageView;
    }

    public void fillImages(String[] img_list){
        img_urls = img_list;
    }

}