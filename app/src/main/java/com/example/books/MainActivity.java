package com.example.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mloadingProgress;
    private RecyclerView rvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mloadingProgress = (ProgressBar) findViewById(R.id.pbloading);
        rvBooks = (RecyclerView) findViewById(R.id.rv_books);

        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvBooks.setLayoutManager(booksLayoutManager);



        //build a URL for cooking books
        try {
            URL bookUrl = ApiUtil.buildUrl("history");
            String jsonResult = ApiUtil.getJson(bookUrl);
            new BooksQueryTask().execute(bookUrl);


        }
        catch (Exception e){

            Log.d("error", e.getMessage());
        }


    }

    // create a new thread so that the network operation is executed outside the main thread

    public class BooksQueryTask extends AsyncTask<URL, Void, String>{

        //specify the tasks that should be done in the background thread
        @Override
        protected String doInBackground(URL... urls) {
           URL searchUrl = urls[0];
           String result = null;

           try {
               result = ApiUtil.getJson(searchUrl);
           }
           catch (IOException e){

               Log.e("Error", e.getMessage());
           }

           return result;

        }


        //post results in the UI after the background thread has been executed
        @Override
        protected void onPostExecute(String result) {

            TextView tverror = (TextView)  findViewById(R.id.tv_error);

            mloadingProgress.setVisibility(View.INVISIBLE);

            if (result == null)
            {

            rvBooks.setVisibility(View.INVISIBLE);
            tverror.setVisibility(View.VISIBLE);

            }

            else{

                rvBooks.setVisibility(View.VISIBLE);

                tverror.setVisibility(View.INVISIBLE);
                //tvResult.setText(result);
            }

             ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
            //String  resultString = "";
            BooksAdapter adapter = new BooksAdapter(books);
            rvBooks.setAdapter(adapter);

       /*     for (Book x :books){

                resultString =  resultString + x.title + "\n" +x.publisher +"\n" + x.publishedDate + "\n\n";
            }*/
            //tvResult.setText(resultString);





        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingProgress.setVisibility(View.VISIBLE);
        }
    }
}