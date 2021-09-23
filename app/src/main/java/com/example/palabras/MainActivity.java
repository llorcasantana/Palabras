package com.example.palabras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

class dbTask extends AsyncTask<Void, Void, Void>{
    private final Context mContext;
    DatabaseHelper myDb;
    public String cid_photo = null;
    public String cSolution = null;
    public String cCR1 = null;
    public String cCR2 = null;
    public String cCR3 = null;
    public String cCR4 = null;
    public String cDifficult = "";

    public dbTask(Context mContext,String id_photo, String solution, String CR_1, String CR_2, String CR_3, String CR_4, String Difficult) {
        super();
        this.mContext = mContext;
        this.cid_photo = id_photo;
        this.cSolution = solution;
        this.cCR1 = CR_1;
        this.cCR2 = CR_2;
        this.cCR3 = CR_3;
        this.cCR4 = CR_4;
        this.cDifficult = Difficult;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        myDb = new DatabaseHelper(mContext);
        boolean result = myDb.insertData(cid_photo,cSolution,cCR1,cCR2,cCR3,cCR4, cDifficult);
        if (result){
            //Toast.makeText(mContext, "OK", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);



        String jsonData = loadJSONFromAsset(this);


        SharedPreferences pfr = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        String st = pfr.getString("IS_DB_LOADED","FALSE");
        assert st != null;
        int SPLASH_DISPLAY_LENGHT = 1000;
        if (st.equals("TRUE")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(MainActivity.this,Home.class);
                    MainActivity.this.startActivity(mainIntent);
                    MainActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGHT);
        }else {
            int addCount = 0;
            try {
                String id_foto="";
                String solution = "";
                String cr_1="";
                String cr_2="";
                String cr_3="";
                String cr_4="";


                JSONObject jsonobject = new JSONObject(jsonData);
                JSONArray jarray = (JSONArray) jsonobject.getJSONArray("data");
                for(int i=0;i<jarray.length();i++)
                {
                    JSONObject jb =(JSONObject) jarray.get(i);
                    id_foto = jb.getString("id");
                    solution = jb.getString("solution");
                    String copyR = jb.getString("copyrights");
                    String Difficult = jb.getString("poolId");

                    JSONArray crJson = new JSONArray(copyR);
                    int totalFields = jarray.length();
                    for (int crjc = 0; crjc < crJson.length(); crjc++){
                        switch (crjc){
                            case 0:
                                cr_1=crJson.getString(crjc);
                                break;
                            case 1:
                                cr_2=crJson.getString(crjc);
                                break;
                            case 2:
                                cr_3=crJson.getString(crjc);
                                break;
                            case 3:
                                cr_4=crJson.getString(crjc);
                                break;
                        }
                    }
                    //Toast.makeText(this, id_foto+" : "+solution+" : "+cr_1+" : "+cr_2+" : "+cr_3+" : "+cr_4, Toast.LENGTH_SHORT).show();

                    ProgressBar pb = findViewById(R.id.determinateBar);
                    pb.setMax(totalFields);
                    pb.setProgress(i);



                    try {
                        new dbTask(this.getApplicationContext(),id_foto,solution,cr_1,cr_2,cr_3,cr_4,Difficult).execute();
                        if (addCount == i){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent mainIntent = new Intent(MainActivity.this,Home.class);
                                    MainActivity.this.startActivity(mainIntent);
                                    MainActivity.this.finish();
                                }
                            }, SPLASH_DISPLAY_LENGHT);
                            getApplicationContext();
                            SharedPreferences preferences = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();


                            editor.putString("IS_DB_LOADED","TRUE");
                            editor.putInt("DIFFICULT",1);
                            editor.apply();
                        }

                    }catch (NullPointerException e){
                      NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                              .setContentText(e.toString())
                              .setSmallIcon(R.mipmap.ic_launcher)
                             .setContentTitle("Error");
                      NotificationManager nf = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                      nf.notify(0,mBuilder.build());

                    }



                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    public String loadJSONFromAsset(Activity activity) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("photodata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

