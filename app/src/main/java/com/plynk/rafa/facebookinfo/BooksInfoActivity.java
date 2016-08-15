package com.plynk.rafa.facebookinfo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Rafa on 14/08/2016.
 */
public class BooksInfoActivity extends AppCompatActivity {

    Bundle b;
    List books;
    String text, text2, text3;
    TextView textView1, textView2, textView3;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booksinfo);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initActionBar();
        textView1 = (TextView)findViewById(R.id.text_1);
        textView2 = (TextView)findViewById(R.id.text_2);
        textView3 = (TextView)findViewById(R.id.text_3);
        if(getIntent().getExtras() != null) {
            b = getIntent().getExtras();
        }
        String userInfoString = "";
        if(b.getString("booksInfo") != null){
            userInfoString = b.getString("booksInfo");
        }
        JSONObject userInfo = null;
        try {
            userInfo = new JSONObject(userInfoString);
            JSONArray data = userInfo.getJSONArray("data");
            if (data.length() == 0){
                text = getString(R.string.text_no_read);
                textView1.setText(text);
            }else{
                text = getString(R.string.text_read);
                textView1.setText(text);
                books = new ArrayList();
                for(int i = 0; i < data.length(); i++){
                    JSONObject item = (JSONObject) data.get(i);
                    books.add(item.get("name"));
                }
                int numberOfBooks = books.size();
                text2 = getString(R.string.text_read_number) + "\n";
                if(numberOfBooks < 5){
                    text2 += getString(R.string.text_read_5);
                }else if(numberOfBooks < 10){
                    text2 += getString(R.string.text_read_10);
                }else if(numberOfBooks < 15){
                    text2 += getString(R.string.text_read_15);
                }else{
                    text2 += getString(R.string.text_read_more);
                }
                text3 = getString(R.string.text_read_list) + "\n";
                List usedBooks = new ArrayList();
                for(int j = 0; j < 3; j++){
                    Random rand = new Random();
                    int random = rand.nextInt(books.size());
                    if(!usedBooks.contains(random)){
                        text3 += books.get(random).toString() + "\n";
                        usedBooks.add(random);
                    }
                }
                textView2.setText(text2);
                textView3.setText(text3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
