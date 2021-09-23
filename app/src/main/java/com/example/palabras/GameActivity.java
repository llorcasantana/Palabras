package com.example.palabras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Visibility;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class GameActivity<mContext> extends AppCompatActivity {
    public static int dpiLayout = 0;
    public static int characterCount = 0;
    public static String word = "";
    public static String idQuiz = "";
    public static String pictureOne = "";
    public static String pictureTwo = "";
    public static String pictureThree = "";
    public static String pictureFour = "";

    public static String pictureOneCR = "";
    public static String pictureTwoCR = "";
    public static String pictureThreeCR = "";
    public static String pictureFourCR = "";
    public static int level = 0;
    public static int totalCoins = 0;
    public static int difficult=0;
    DatabaseHelper myDb;
    public static Context mContext;

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        characterCount=0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        myDb = new DatabaseHelper(this);
        String lastQuiz = readData("idQuiz");
        level = readDataInt("userLevel");
        totalCoins = readDataInt("userCoins");
        difficult = readDataInt("DIFFICULT");
        mContext = getApplicationContext();



        Cursor res = myDb.getQuiz(lastQuiz);
        if (res!=null && res.getCount() >0){
            while (res.moveToNext()){
                word = res.getString(2);
                idQuiz = res.getString(1);
                pictureOne = "_"+res.getString(1)+"_1.jpg";
                pictureTwo = "_"+res.getString(1)+"_2.jpg";
                pictureThree = "_"+res.getString(1)+"_3.jpg";
                pictureFour = "_"+res.getString(1)+"_4.jpg";
                //TODO Copyright
                pictureOneCR = res.getString(3);
                pictureTwoCR = res.getString(4);
                pictureThreeCR = res.getString(5);
                pictureFourCR = res.getString(6);
                //Toast.makeText(this, ""+res.getString(2), Toast.LENGTH_SHORT).show();
            }
        }





        TextView userLevel = findViewById(R.id.userLevel);
        dpiLayout = getResources().getDisplayMetrics().densityDpi;




        LinearLayout imageQuizOne = findViewById(R.id.quizOne);
        LinearLayout imageQuizTwo = findViewById(R.id.quizTwo);
        LinearLayout imageQuizThree = findViewById(R.id.quizThree);
        LinearLayout imageQuizFour = findViewById(R.id.quizFour);
        adjustLayouts(imageQuizOne);
        adjustLayouts(imageQuizTwo);
        adjustLayouts(imageQuizThree);
        adjustLayouts(imageQuizFour);


        ImageView bgRotater = findViewById(R.id.bglineTwo);
        ViewGroup.LayoutParams params = bgRotater.getLayoutParams();
        DisplayMetrics dp = new DisplayMetrics();
        ((Activity)bgRotater.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dp);

        int newWidth = dp.widthPixels;
        int newHeight = dp.heightPixels;

        TextView winner_box_text = findViewById(R.id.winner_box_text);
        float newTextWinnerSize = dp.heightPixels/15f;
        winner_box_text.setTextSize(newTextWinnerSize - (newTextWinnerSize/1.5f));

        params.height=newWidth+200;
        params.width=newWidth+200;
        bgRotater.setLayoutParams(params);
        bgRotater.requestLayout();

        RelativeLayout winnerBox = findViewById(R.id.winner_box);
        ImageButton btn_next = findViewById(R.id.btn_next);
        ViewGroup.LayoutParams btn_nextParams = btn_next.getLayoutParams();

        btn_nextParams.height=newHeight/11;
        btn_nextParams.width=newWidth/2;

        btn_next.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                view.setBackgroundResource(R.drawable.btn_next_ac);
            }
        });


        ViewGroup.LayoutParams winnerBoxParams = winnerBox.getLayoutParams();

        winnerBoxParams.width = newWidth;
        winnerBoxParams.height = newHeight/2;

        winnerBox.setLayoutParams(winnerBoxParams);
        winnerBox.requestLayout();



        Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotate_clock);
        bgRotater.startAnimation(animation);




        TextView btnSkipQuiz = findViewById(R.id.btn_skip_word);
        btnSkipQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipLevel();
            }
        });




        final RelativeLayout zoom = (RelativeLayout) findViewById(R.id.pictureZoom);
        final ImageView zoomIn = (ImageView) findViewById(R.id.pictureZoomIn);
        final RelativeLayout zoomBox = findViewById(R.id.picZoomBox);
        adjustPictureZoom(zoomBox);
        final ImageView imgFour = (ImageView)findViewById(R.id.quizFourImg);
        imgFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoom.setVisibility(View.VISIBLE);

                try {
                    TextView tv = findViewById(R.id.tvCopyRight);
                    tv.setText(pictureFourCR);
                    // get input stream
                    InputStream ims = getAssets().open(pictureFour);
                    // load image as Drawable
                    Drawable d = Drawable.createFromStream(ims, null);
                    // set image to ImageView
                    zoomIn.setBackground(d);


                }
                catch(IOException ex) {
                    return;
                }
            }
        });

        LinearLayout wordZone = findViewById(R.id.characterZone);
        reorderWordZone(wordZone);

        final ImageView imgOne = (ImageView)findViewById(R.id.quizOneImg);
        imgOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoom.setVisibility(View.VISIBLE);
                try {
                    TextView tv = findViewById(R.id.tvCopyRight);
                    tv.setText(pictureOneCR);
                    // get input stream
                    InputStream ims = getAssets().open(pictureOne);
                    // load image as Drawable
                    Drawable d = Drawable.createFromStream(ims, null);
                    // set image to ImageView
                    zoomIn.setBackground(d);
                }
                catch(IOException ex) {
                    return;
                }
                //imgFour.getBackground();
                //zoomIn.setBackground(R.drawable._12_1);
            }
        });

        final ImageView imgTwo = (ImageView)findViewById(R.id.quizTwoImg);
        imgTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoom.setVisibility(View.VISIBLE);

                try {
                    TextView tv = findViewById(R.id.tvCopyRight);
                    tv.setText(pictureTwoCR);
                    // get input stream
                    InputStream ims = getAssets().open(pictureTwo);
                    // load image as Drawable
                    Drawable d = Drawable.createFromStream(ims, null);
                    // set image to ImageView
                    zoomIn.setBackground(d);
                }
                catch(IOException ex) {
                    return;
                }
            }
        });

        final ImageView imgThree = (ImageView)findViewById(R.id.quizThreeImg);
        imgThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoom.setVisibility(View.VISIBLE);
                try {

                    TextView tv = findViewById(R.id.tvCopyRight);
                    tv.setText(pictureThreeCR);
                    // get input stream
                    InputStream ims = getAssets().open(pictureThree);
                    // load image as Drawable
                    Drawable d = Drawable.createFromStream(ims, null);
                    // set image to ImageView
                    zoomIn.setBackground(d);
                }
                catch(IOException ex) {
                    return;
                }
            }
        });

        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoom.setVisibility(View.INVISIBLE);
            }
        });
        readCache();
        setImages();
        userLevel.setText(level+"");
        TextView tCoins = findViewById(R.id.tCoins);
        tCoins.setText(totalCoins+"");


    }
    public void setImages(){
        try {
            // get input stream
            InputStream imOne = getAssets().open(pictureOne);
            InputStream imTwo = getAssets().open(pictureTwo);
            InputStream imThree = getAssets().open(pictureThree);
            InputStream imFour = getAssets().open(pictureFour);

            // load image as Drawable
            Drawable dOne = Drawable.createFromStream(imOne, null);
            Drawable dTwo = Drawable.createFromStream(imTwo, null);
            Drawable dThree = Drawable.createFromStream(imThree, null);
            Drawable dFour = Drawable.createFromStream(imFour, null);

            // set image to ImageView
            ImageView imgOne = findViewById(R.id.quizOneImg);
            ImageView imgTwo = findViewById(R.id.quizTwoImg);
            ImageView imgThree = findViewById(R.id.quizThreeImg);
            ImageView imgFour = findViewById(R.id.quizFourImg);

            imgOne.setBackground(dOne);
            imgOne.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imgOne.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

            imgTwo.setBackground(dTwo);
            imgTwo.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imgTwo.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

            imgThree.setBackground(dThree);
            imgThree.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imgThree.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

            imgFour.setBackground(dFour);
            imgFour.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imgFour.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

        }
        catch(IOException ex) {
            return;
        }
    }
    public void readCache(){
        String lastLetters = readData("Char_Zone");
        if (lastLetters==null || lastLetters.equals("")){
            generateLetters(this);
            saveData();
        }else{
            LinearLayout vo = (LinearLayout) findViewById(R.id.fileOne);
            LinearLayout vt = (LinearLayout) findViewById(R.id.fileTwo);
            restoreCharacters();

            String dataToPutLineOne = readData("Char_Line_One");
            String dataToPutLineOneVisibility = readData("Char_Line_One_Visibility");
            restoreLetters(vo,dataToPutLineOne,dataToPutLineOneVisibility);

            String dataToPutLineTwo = readData("Char_Line_Two");
            String dataToPutLineTwoVisibility = readData("Char_Line_Two_Visibility");
            restoreLetters(vt,dataToPutLineTwo,dataToPutLineTwoVisibility);
        }
    }

    public void generateNewWord(){
        difficult = readDataInt("DIFFICULT");
        String quizToFind="";
        myDb = new DatabaseHelper(this);
        getApplicationContext();
        SharedPreferences preferences = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String diff=difficult+"";
        Cursor res = myDb.getRandomQuiz(diff);
        if (res!=null && res.getCount() >0){
            while (res.moveToNext()){
                quizToFind=res.getString(1);
                word = res.getString(2);
                idQuiz = res.getString(1);
                pictureOne = "_"+res.getString(1)+"_1.jpg";
                pictureTwo = "_"+res.getString(1)+"_2.jpg";
                pictureThree = "_"+res.getString(1)+"_3.jpg";
                pictureFour = "_"+res.getString(1)+"_4.jpg";
                //TODO Copyright
                pictureOneCR = res.getString(3);
                pictureTwoCR = res.getString(4);
                pictureThreeCR = res.getString(5);
                pictureFourCR = res.getString(6);
                characterCount = 0;
            }
        }else {
            difficult++;
            editor.putInt("DIFFICULT",difficult);
            editor.apply();
            generateNewWord();
        }
        editor.putString("Char_Zone","__________");
        editor.putString("Char_Line_One_Visibility","________");
        editor.putString("Char_Line_Two_Visibility","________");

        editor.putString("idQuiz",quizToFind);
        editor.apply();

        LinearLayout vo = (LinearLayout) findViewById(R.id.fileOne);
        LinearLayout vt = (LinearLayout) findViewById(R.id.fileTwo);

        restoreCharacters();
        generateLetters(this);

        saveData();

        //readLineLetter(vo,true)

        setImages();
        readCache();

        LinearLayout wordZone = findViewById(R.id.characterZone);

        for (int iVo=0;iVo<vo.getChildCount();iVo++){
            Object child = vo.getChildAt(iVo);
            if (child instanceof TextView){
                ((TextView) child).setVisibility(View.VISIBLE);
            }
        }
        for (int iVt=0;iVt<vt.getChildCount();iVt++){
            Object child = vt.getChildAt(iVt);
            if (child instanceof TextView){
                ((TextView) child).setVisibility(View.VISIBLE);
            }
        }
        for (int iVt=0;iVt<wordZone.getChildCount();iVt++){
            Object child = wordZone.getChildAt(iVt);
            if (child instanceof TextView){
                int wordLenght = word.length();
                if (iVt<wordLenght){
                    ((TextView) child).setVisibility(View.VISIBLE);
                }
            }
        }



        saveData();
        TextView userLevel = findViewById(R.id.userLevel);
        userLevel.setText(level+"");

        TextView tCoins = findViewById(R.id.tCoins);
        tCoins.setText(totalCoins+"");

    }
    public void hideThis(View view){
        RelativeLayout congratsLayout = findViewById(R.id.congratsLayout);
        congratsLayout.setVisibility(View.INVISIBLE);
    }
    public void checkWord(){
        characterCount = 0;
        StringBuilder wordActual= new StringBuilder();
        LinearLayout wordComplette = findViewById(R.id.characterZone);
        for (int i=0;i<wordComplette.getChildCount();i++){
            Object child = wordComplette.getChildAt(i);
            if (child instanceof TextView){
                String text = (String) ((TextView) child).getText();
                if (!text.equals("")){
                    characterCount++;
                }
            }
        }

        int wordCount = word.length();
        if (characterCount==wordCount){
            for (int i=0;i<wordComplette.getChildCount();i++){
                Object child = wordComplette.getChildAt(i);
                if (child instanceof TextView){
                    String text = (String) ((TextView) child).getText();
                    if (!text.equals("")){
                        wordActual.append(text);
                    }
                }
            }
            if (wordActual.toString().equals(word)){
                myDb = new DatabaseHelper(this);
                boolean res = myDb.setCompleted(idQuiz);
                if (res){
                    TextView winner_box_text = findViewById(R.id.winner_box_text);
                    winner_box_text.setText(word);
                    RelativeLayout congratsLayout = findViewById(R.id.congratsLayout);
                    congratsLayout.setVisibility(View.VISIBLE);
                    level = level+1;
                    totalCoins = totalCoins+4;
                    generateNewWord();
                }else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }else{
                LinearLayout ln = findViewById(R.id.characterZone);
                Animation animation = AnimationUtils.loadAnimation(this,R.anim.blink);
                ln.startAnimation(animation);
            }
        }else{
            int minus = wordCount-characterCount;
            //Toast.makeText(this, wordCount+" | "+characterCount, Toast.LENGTH_SHORT).show();
        }
    }
    public void restoreLetters(LinearLayout ln,String text, String visibility){
        int tvCount = 0;
        try {
            for (int i = 0; i < ln.getChildCount(); i++) {
                Object child = ln.getChildAt(i);
                if (child instanceof TextView) {

                    String txtSb = text.substring(tvCount,tvCount+1);
                    String txtVb = visibility.substring(tvCount,tvCount+1);
                    //String textData = ((TextView) child).getText().toString();
                    ((TextView) child).setText(txtSb);
                    if(txtVb.equals("0")){
                        ((TextView) child).setVisibility(View.INVISIBLE);
                    }else if(txtSb.equals("_")){
                        ((TextView) child).setText("");
                    }
                    tvCount++;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void restoreCharacters(){

        LinearLayout characterZone = findViewById(R.id.characterZone);
        int totalWordLength=word.length();

        for (int i=0;i<characterZone.getChildCount();i++){
            Object child = characterZone.getChildAt(i);
            if (child instanceof TextView){
                if (i>=totalWordLength){
                    ((TextView) child).setVisibility(View.GONE);
                }

            }
        }
        String dataToPut = readData("Char_Zone");
        int tvCount = 0;
        try {
            for (int i = 0; i < characterZone.getChildCount(); i++) {
                Object child = characterZone.getChildAt(i);
                if (child instanceof TextView) {

                    String txtSb = dataToPut.substring(tvCount,tvCount+1);
                    //String textData = ((TextView) child).getText().toString();
                    if (txtSb.equals("_")){
                        ((TextView) child).setText("");
                    }else if(txtSb.equals("0")){
                        ((TextView) child).setVisibility(View.GONE);
                    }else{
                        ((TextView) child).setText(txtSb);
                        characterCount++;
                    }

                    tvCount++;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public String readData(String data){
        getApplicationContext();
        SharedPreferences pfr = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        return pfr.getString(data,"");
    }
    public int readDataInt(String data){
        getApplicationContext();
        SharedPreferences pfr = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        return pfr.getInt(data,0);
    }


    public void saveData(){
        getApplicationContext();
        SharedPreferences preferences = getSharedPreferences("GAME_SAVE_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("idQuiz",idQuiz);
        //TODO Palabra
        editor.putString("Word",word);
        //TODO Fotos
        editor.putString("Picture_One",pictureOne);
        editor.putString("Picture_Two",pictureTwo);
        editor.putString("Picture_Three",pictureThree);
        editor.putString("Picture_Four",pictureFour);

        //TODO Letras usadas
        LinearLayout cz = findViewById(R.id.characterZone);
        editor.putString("Char_Zone",readLineLetter(cz,true));

        //TODO Level y monedas
        editor.putInt("userLevel",level);
        editor.putInt("userCoins",totalCoins);

        //TODO Letras faltantes
        LinearLayout lo = findViewById(R.id.fileOne);
        LinearLayout lt = findViewById(R.id.fileTwo);
        editor.putString("Char_Line_One",readLineLetter(lo,false));
        editor.putString("Char_Line_Two",readLineLetter(lt,false));
        editor.putString("Char_Line_One_Visibility",readVisibility(lo));
        editor.putString("Char_Line_Two_Visibility",readVisibility(lt));

        //Toast.makeText(this, readLineLetter(lo,false)+" : "+readVisibility(lo), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, readLineLetter(lt,false)+" : "+readVisibility(lt), Toast.LENGTH_SHORT).show();

        editor.apply();
    }
    public String readVisibility(LinearLayout line){
        String data = "";
        String send = "";
        for (int i = 0; i < line.getChildCount(); i++) {
            Object child = line.getChildAt(i);
            if (child instanceof TextView) {
                String textData = ((TextView) child).getText().toString();
                if (textData.equals("")) {
                    send = send + "_";
                }else if(((TextView) child).getVisibility()==View.INVISIBLE || ((TextView) child).getVisibility()==View.GONE){
                    send = send + "0";
                }else{
                    data=((TextView) child).getText().toString();
                    send=send+data;
                }
            }
        }

        return send;
    }
    public String readLineLetter(LinearLayout line, boolean setVisibility){
        String data = "";
        String send = "";
        for (int i = 0; i < line.getChildCount(); i++) {
            Object child = line.getChildAt(i);
            if (child instanceof TextView) {
                String textData = ((TextView) child).getText().toString();
                if (textData.equals("")) {
                    send = send + "_";
                }else if(((TextView) child).getVisibility()==View.INVISIBLE || ((TextView) child).getVisibility()==View.GONE){
                    if (setVisibility){
                        send = send + "0";
                    }else{
                        data=((TextView) child).getText().toString();
                        send=send+data;
                    }
                }else{
                    data=((TextView) child).getText().toString();
                    send=send+data;
                }
            }
        }

        return send;
    }
    public void backCharacterToQuizLetter(View v){

        LinearLayout fileOne = findViewById(R.id.fileOne);
        RelativeLayout quizLetter = findViewById(R.id.quizLetter);
        TextView t = (TextView)v;
        int tvCount = 0;
        int putText = 1;
        characterCount--;


        try {
            for (int i = 0; i < fileOne.getChildCount(); i++) {

                Object child = fileOne.getChildAt(i);
                if (child instanceof TextView) {
                    tvCount++;
                    if (((TextView) child).getVisibility() == View.INVISIBLE) {
                        if (((TextView) child).getText().equals(t.getText())) {
                            if (putText==1){
                                t.setText("");

                                ((TextView) child).setVisibility(View.VISIBLE);
                                putText=0;

                            }

                        }
                    }else if (tvCount==7){
                        putInSecondLine(v);
                    }
                }
            }




        }catch (Exception e){
            e.printStackTrace();
        }

        saveData();
    }
    public void putInSecondLine(View v){
        int putText = 1;
        TextView t = (TextView)v;
        LinearLayout fileTwo = findViewById(R.id.fileTwo);
        for (int i = 0; i < fileTwo.getChildCount(); i++) {

            Object child2 = fileTwo.getChildAt(i);
            if (child2 instanceof TextView) {
                if (((TextView) child2).getVisibility() == View.INVISIBLE) {
                    if (((TextView) child2).getText().equals(t.getText())) {
                        if (putText==1){
                            t.setText("");
                            ((TextView) child2).setVisibility(View.VISIBLE);
                            putText=0;
                        }
                    }
                }
            }
        }
        saveData();
    }
    public static void reorderWordZone(View pictureZoom){
        ViewGroup.LayoutParams params = pictureZoom.getLayoutParams();
        DisplayMetrics dp = new DisplayMetrics();
        ((Activity)pictureZoom.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dp);

        int newHeight = dp.heightPixels;
        params.height=newHeight/5;
        pictureZoom.setLayoutParams(params);
        pictureZoom.requestLayout();
    }
    public void addCharacterSelected(View v){
        LinearLayout characterZone = findViewById(R.id.characterZone);
        int putText = 1;
        for (int i=0;i<characterZone.getChildCount();i++){
            Object child = characterZone.getChildAt(i);
            if (child instanceof TextView){
                if (((TextView) child).getVisibility()==View.VISIBLE){
                    if (((TextView) child).getText().equals("")){
                        if (putText==1){
                            characterCount++;
                            TextView t = (TextView)v;
                            ((TextView) child).setText(t.getText());
                            t.setVisibility(View.INVISIBLE);
                            putText=0;
                        }
                    }
                }
            }
        }
        if(v instanceof TextView){

        }

        checkWord();
        saveData();

    }
    public static void adjustPictureZoom(View pictureZoom){
        ViewGroup.LayoutParams params = pictureZoom.getLayoutParams();
        DisplayMetrics dp = new DisplayMetrics();
        ((Activity)pictureZoom.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dp);

        int newWidth = dp.widthPixels;
        params.height=newWidth-100;
        params.width=newWidth-100;
        pictureZoom.setLayoutParams(params);
        pictureZoom.requestLayout();
    }
    public static void adjustLayouts(View v){
        //ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        DisplayMetrics dp = new DisplayMetrics();
        ((Activity)v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dp);


        int newWidth = dp.widthPixels;
        params.height=(newWidth/2)-30;
        params.width=(newWidth/2)-30;
        v.setLayoutParams(params);
        v.requestLayout();

    }
    public static void findTextViewLetters(View v,String str,int numer,int file){

        int num = 0;
        try {
            if (v instanceof ViewGroup){
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++){
                    View child = vg.getChildAt(i);
                    findTextViewLetters(child,str,i,file);
                }
            }else if(v instanceof TextView){
                ViewGroup.LayoutParams params = v.getLayoutParams();
                DisplayMetrics dp = new DisplayMetrics();
                ((Activity)v.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dp);
                int newWidth = dp.widthPixels;
                float sp = (newWidth/10)/dp.scaledDensity;
                    v.getLayoutParams().height=newWidth/9;
                    v.getLayoutParams().width=newWidth/9;
                    //params.height=(newWidth);
                    //params.width=(newWidth);
                //v.setLayoutParams(params);
                ((TextView) v).setTextSize(sp-(sp/3f));

                v.requestLayout();



                if (((TextView) v).getText().equals("")){

                }else {
                    while (num<7){
                        String txtSb = str.substring(numer,numer+1);
                        ((TextView) v).setText(txtSb);
                        num++;

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void generateLetters(Context c){
        //String word = "frio";
        char[] chars = "abcdefghijklmnñopqrstuvwxyz".toCharArray();
        int totalCh = word.length();
        int totalToGenerate = 12-totalCh;




        StringBuilder sb = new StringBuilder(totalToGenerate);
        Random random = new Random();
        for (int i = 0; i < totalToGenerate; i++){
            char ch = chars[random.nextInt(chars.length)];

            sb.append(ch);
        }
        String fullWord = word+sb.toString();

        //Desorganizar
        Random r = new Random();

        String completeWord = scramble(r,fullWord).toUpperCase();

        String wordOne = completeWord.substring(0,6);
        String wordTwo = completeWord.substring(6,12);

        LinearLayout vc = (LinearLayout) findViewById(R.id.characterZone);
        LinearLayout vo = (LinearLayout) findViewById(R.id.fileOne);
        LinearLayout vt = (LinearLayout) findViewById(R.id.fileTwo);

        int totalWordLength=word.length();

        for (int i=0;i<vc.getChildCount();i++){
            Object child = vc.getChildAt(i);
            if (child instanceof TextView){
                if (i>=totalWordLength){
                    ((TextView) child).setVisibility(View.GONE);
                }

            }
        }

        findTextViewLetters(vo,wordOne,0,1);
        findTextViewLetters(vt,wordTwo,0,2);

        saveData();
    }

    public static String scramble(Random random, String inputString){
        char a[]=inputString.toCharArray();
        for (int i=0;i<a.length;i++){
            int j = random.nextInt(a.length);
            char temp = a[i];a[i]=a[j];a[j]=temp;
        }
        return new String(a);
    }

    public void skipLevel(){
        showDialog(this);
    }
    public static void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newcustom_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Cancel" ,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Okay" ,Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void generateLettersa(){
        Typeface face = ResourcesCompat.getFont(this,R.font.avenirnextheavy);
        LinearLayout fileone = (LinearLayout)findViewById(R.id.fileOne);
        TextView tv = new TextView(this);
        tv.setTypeface(face);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,2,0);
        tv.setPadding(3,0,0,0);
        tv.setText("Z");
        tv.setBackgroundResource(R.drawable.cps_actionbar_btn);
        tv.setWidth(40);
        tv.setHeight(40);
        tv.setLayoutParams(params);
        fileone.addView(tv);
    }
}