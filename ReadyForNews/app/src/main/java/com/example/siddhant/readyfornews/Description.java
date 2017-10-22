package com.example.siddhant.readyfornews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Description extends AppCompatActivity {
    //ArrayList<String>imageUrl;
    public class DownloadImage extends AsyncTask<String,Void,Bitmap> {
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
                Bitmap image= BitmapFactory.decodeStream(in);
                return image;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        ActionBar actionBar=this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",-1);
        /*if(intent.hasExtra(Intent.EXTRA_TEXT)){
            id=intent.getIntExtra("id",-1);
        }*/
        //imageUrl = new ArrayList<String>(MainActivity.image);
        ImageView imageView=(ImageView)findViewById(R.id.description_image_view);
        TextView textView=(TextView) findViewById(R.id.description_text_view);

        textView.setText(MainActivity.description.get(id).toString());
        DownloadImage downloadImage=new DownloadImage();
        try {
            Bitmap image=downloadImage.execute(MainActivity.image.get(id)).get();
            imageView.setImageBitmap(image);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
