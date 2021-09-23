package com.example.palabras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    ImageButton btnPlay;
    DatabaseHelper myDb;
    DatabaseHelper myDbTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDb = new DatabaseHelper(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        String lastQuiz = readData("idQuiz");
        String quizToFind = "";
        boolean findNew = false;
        if (readData("idQuiz")==null || readData("idQuiz").equals("")){
            findNew = true;
        }
        if (findNew){
            getApplicationContext();
            SharedPreferences preferences = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putInt("DIFFICULT",1);
            String difficult = readDataInt("DIFFICULT")+"";
            Cursor res = myDb.getRandomQuiz(difficult);
            if (res!=null && res.getCount() >0){
                while (res.moveToNext()){
                    quizToFind=res.getString(1);

                }
            }
            editor.putString("idQuiz",quizToFind);
            editor.putInt("userLevel",1);
            editor.putInt("userCoins",500);
            editor.apply();
            //Toast.makeText(this, "No esta. se generara uno nuevo: "+quizToFind, Toast.LENGTH_SHORT).show();
        }else{

        }

        //Toast.makeText(this, ""+readDataInt("DIFFICULT"), Toast.LENGTH_SHORT).show();







        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,GameActivity.class);
                startActivity(i);

            }
        });

        super.onCreate(savedInstanceState);
    }

    public int readDataInt(String data){
        getApplicationContext();
        SharedPreferences pfr = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        return pfr.getInt(data,0);
    }
    public String readData(String data){
        getApplicationContext();
        SharedPreferences pfr = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        String st = pfr.getString(data,"");
        return st;
    }
}