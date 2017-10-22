package com.example.siddhant.readyfornews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CustomAdapter extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String>titles;
    private ArrayList<String>imagesUrl;

    public CustomAdapter(Activity context, ArrayList<String>titles, ArrayList<String>imagesUrl) {
        super(context,R.layout.custom_row, titles);
        this.titles=new ArrayList<String>();
        this.imagesUrl=new ArrayList<String>();
        this.context=context;
        this.titles=titles;
        this.imagesUrl=imagesUrl;
    }
    public class DownloadImage extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url=null;
            try {
                url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.getResponseCode();
                InputStream in=connection.getErrorStream();
                if (in == null) {
                    in = connection.getInputStream();
                }
                connection.connect();
                Bitmap image=BitmapFactory.decodeStream(in);
                return image;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View customView=inflater.inflate(R.layout.custom_row,null,true);

        ImageView imageView=(ImageView)customView.findViewById(R.id.image_view);
        TextView textView=(TextView)customView.findViewById(R.id.text_view);

        textView.setText(titles.get(position));

        DownloadImage downloadImage=new DownloadImage();
        try {
            Bitmap myImage=downloadImage.execute(imagesUrl.get(position)).get();
            imageView.setImageBitmap(myImage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return customView;
    }
}
