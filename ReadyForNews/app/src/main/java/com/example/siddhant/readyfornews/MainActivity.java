package com.example.siddhant.readyfornews;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> image;
    ArrayList<String> title;
    static ArrayList<String> description;
    static ArrayList<String> web;
    ArrayList<Bitmap> downloadedImages;
    ListView listView;
    SharedPreferences sharedPreferences;
    Intent descriptionIntent;
    Intent webIntent;
    String s;

    class Download extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection connection;
            String result = "";
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                /*connection.getResponseCode();
                InputStream in = connection.getErrorStream();
                if (in == null) {
                    in = connection.getInputStream();
                }*/
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    result += (char) data;
                    data = reader.read();
                }
                return result;

            }/* catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/ catch (Exception e) {
                e.printStackTrace();
            }
            return "false";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = new ArrayList<>();
        image = new ArrayList<>();
        description = new ArrayList<>();
        web = new ArrayList<>();
        downloadedImages = new ArrayList<>();
        sharedPreferences = this.getSharedPreferences("com.example.siddhant.readyfornews", Context.MODE_PRIVATE);
        s = sharedPreferences.getString("type", "");
        listView = (ListView) findViewById(R.id.list_view);
        descriptionIntent=new Intent(this,Description.class);
        webIntent=new Intent(this,WebView.class);
        SharedPreferences sharedPreferences2= PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("boolean",String.valueOf(sharedPreferences2.getBoolean("view_type",false)));

        listView.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.i("type",s);
                        final int itemId=i;
                        Log.i("id","first "+itemId);
                        if (s.equals(""))

                        {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Select View")
                                    .setPositiveButton("Web", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            sharedPreferences.edit().putString("type", "web").apply();
                                            Log.i("id",itemId+"");
                                            s=sharedPreferences.getString("type","");
                                            webIntent.putExtra("id",itemId);
                                            startActivity(webIntent);
                                        }
                                    })
                                    .setNegativeButton("Description", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            sharedPreferences.edit().putString("type", "web").apply();
                                            s=sharedPreferences.getString("type","");
                                            descriptionIntent.putExtra("id",itemId);
                                            startActivity(descriptionIntent);
                                        }
                                    })
                                    .show();
                        }else{
                            switch (s){
                                case "description":
                                    descriptionIntent.putExtra("id",itemId);
                                    startActivity(descriptionIntent);
                                    return;
                                case "web":
                                    webIntent.putExtra("id",itemId);
                                    startActivity(webIntent);
                                    return;
                                default:
                                    return;
                            }
                        }
                    }
                });

        Download downloadObject = new Download();
        try {
            String result = downloadObject.execute("https://newsapi.org/v1/articles?source=the-hindu&sortBy=top&apiKey=33cb76ec4475443db38c3c541ce6729b").get();
            Log.i("result", result);
            JSONObject jsonObject = new JSONObject(result);

            //GETTING DATA FROM JSON ARRAY
            String newsInfo = jsonObject.getString("articles");
            JSONArray arr = new JSONArray(newsInfo);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonPart = arr.getJSONObject(i);
                title.add(jsonPart.getString("title"));
                web.add(jsonPart.getString("url"));
                image.add(jsonPart.getString("urlToImage"));
                description.add(jsonPart.getString("description"));
            }
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/ catch (Exception e) {
            e.printStackTrace();
        }

        CustomAdapter customAdapter = new CustomAdapter(this, title, image);
        listView.setAdapter(customAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.description:
                sharedPreferences.edit().putString("type", "description").apply();
                s=sharedPreferences.getString("type","");
                return true;
            case R.id.web:
                sharedPreferences.edit().putString("type", "web").apply();
                s=sharedPreferences.getString("type","");
                return true;
            case R.id.settings:
                Intent settings=new Intent(this,SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return false;
        }
    }
}
