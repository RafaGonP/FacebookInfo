package com.plynk.rafa.facebookinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Rafa on 14/08/2016.
 */
public class BasicInfoActivity extends AppCompatActivity {

    Bundle b;
    String name;
    String email;
    TextView name_text, email_text;
    ImageView profile_image;
    Button books_button;
    String id;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicinfo);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initActionBar();
        name_text = (TextView)findViewById(R.id.name);
        email_text = (TextView)findViewById(R.id.email);
        profile_image = (ImageView)findViewById(R.id.image);
        books_button = (Button)findViewById(R.id.books_button);
        if(getIntent().getExtras() != null) {
            b = getIntent().getExtras();
        }
        String userInfoString = "";
        if(b.getString("userInfo") != null){
            userInfoString = b.getString("userInfo");
        }
        JSONObject userInfo = null;
        try {
            userInfo = new JSONObject(userInfoString);
            name = userInfo.getString("first_name") + " " + userInfo.getString("last_name");
            email = userInfo.getString("email");
            name_text.setText(getString(R.string.name) + " " + name);
            email_text.setText(getString(R.string.email) + " " + email);
            id = userInfo.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DownloadImageTask task = new DownloadImageTask();
        task.execute();

        books_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooksInfo();
            }
        });

    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.title);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    class DownloadImageTask extends AsyncTask<String, Void, Drawable> {

        protected Drawable doInBackground(String... urls) {
            if(AccessToken.getCurrentAccessToken() != null) {
                String id = AccessToken.getCurrentAccessToken().getUserId();
                String urldisplay = "https://graph.facebook.com/" + id + "/picture?type=large&redirect=true&width=300&height=300";
                Drawable d = null;
                try {
                    InputStream in = (InputStream) new URL(urldisplay).getContent();
                    Drawable dw = Drawable.createFromStream(in, "src name");
                    Bitmap bitmap = ((BitmapDrawable) dw).getBitmap();

                    RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), Bitmap.createScaledBitmap(bitmap, 300, 300, true));
                    rbd.setCornerRadius(150);
                    d = rbd;
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return d;
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            profile_image.setImageDrawable(result);
        }
    }

    public void getBooksInfo(){

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/books",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        Intent i = new Intent(BasicInfoActivity.this, BooksInfoActivity.class);
                        i.putExtra("booksInfo", jsonObject.toString());
                        startActivity(i);
                    }
                }
        ).executeAsync();
    }



}
