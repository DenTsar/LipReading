package com.example.lipreading;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayerView youTubePlayerView;
    private final String Oauth_Key = BuildConfig.OAUTH_KEY;
    public final static String key = BuildConfig.API_KEY;
    public static List<Subtitle> captions;
    private YouTubePlayer player;
    private EditText answer;
    private Button next;
    private Button replay;
    private Button submit;
    private Button giveup;
    private Button reset;
    private Button report;
    private TextView stats;
    private TextView result;
    private long start;
    private Subtitle pSub;
    private String pToken;

    private FirebaseFirestore db;
    public static FirebaseAnalytics an;
    private AudioManager am;

    private int right;
    private int wrong;
    private int half;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        youTubePlayerView = findViewById(R.id.a_player);
        answer = findViewById(R.id.a_answer);
        next = findViewById(R.id.a_next);
        replay = findViewById(R.id.a_replay);
        submit = findViewById(R.id.a_submit);
        stats  = findViewById(R.id.a_stats);
        result  = findViewById(R.id.a_result);
        giveup  = findViewById(R.id.a_giveup);
        reset  = findViewById(R.id.a_reset);
        report  = findViewById(R.id.a_report);

        youTubePlayerView.initialize(key, this);

        db = FirebaseFirestore.getInstance();
        an = FirebaseAnalytics.getInstance(this);

        start = 0;
        pToken = "";
        captions = new ArrayList<>();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_MUTE,0);

        pref = getApplicationContext().getSharedPreferences("Prefs",Context.MODE_PRIVATE);
        editor = pref.edit();

        right = pref.getInt("right",0);
        half = pref.getInt("half",0);
        wrong = pref.getInt("wrong",0);

        updateStats();
        db.collection("Captions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult())
                            captions.add(document.toObject(Subtitle.class));
                    }
                    else {
                        Log.d("qwer", "Error getting documents: ", task.getException());
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                submit.setEnabled(true);
                playVideo(false);
            }
        });
        //gets captions from apis
        /*Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    do {
                        URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=contentDetails&pageToken=" + pToken + "&maxResults=50&playlistId=PLMs_JcuNozJY36jWny4IGZ7TDL86YnxS5&key=" + key);
                        URLConnection urlc = url.openConnection();The code execution cannot proceed because vulkan-1.dll was not found. Reinstalling the program may fix this program.
                        InputStream input = urlc.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(input));
                        StringBuilder builder = new StringBuilder();
                        String line = "";
                        while ((line = br.readLine()) != null)
                            builder.append(line);
                        JSONObject playlist = new JSONObject(builder.toString());
                        pToken = playlist.getString("nextPageToken");
                        List<String> ids = new ArrayList<>();
                        for (int i = 0; i < playlist.length(); i++)
                            ids.add(playlist.getJSONArray("items").getJSONObject(i).getJSONObject("contentDetails").getString("videoId"))
                                    ;
                        for (String id : ids) {
                            URLConnection connection = new URL("https://video.google.com/timedtext?lang=en&v=" + id).openConnection();
                            InputStream in = new BufferedInputStream(connection.getInputStream());
                            readStream(in, id);
                        }
                    } while (!pToken.equals(""));
                } catch (Exception e) {
                    Log.d("qwer", "1 " + e);
                }
            }
        });*/
        //thread.start();
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_MUTE,0);
                playVideo(true);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(false);
                Bundle b = new Bundle();
                b.putString("qwer","asdf");
                b.putInt("zxcv",5);
                an.logEvent("custom",b);
                adjustButtons();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResults(false);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                right = 0;
                                half = 0;
                                wrong = 0;
                                updateStats();
                                Toast.makeText(MainActivity.this,"Done!",Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(MainActivity.this,"Alright, score kept.",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                builder.setMessage("Are you sure? You will not be able to get your score back (Except through hard work!)")
                        .setPositiveButton("Yes",dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResults(true);
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this,IssueActivity.class);
                db.collection("Captions").whereEqualTo("videoId",pSub.getVideoId()).whereEqualTo("startTime",pSub.getStartTime()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(QueryDocumentSnapshot document : task.getResult())
                                    intent.putExtra("id", document.getId());
                                startActivity(intent);
                            }
                        });
            }
        });
    }

    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.d("qwer", "done");
        if (!b && player==null)
            player = youTubePlayer;
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        
        //if (captions.size() != 0)
          //  playVideo(false);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("qwer", "fail "+youTubeInitializationResult.toString());
    }

    private void readStream(InputStream is, String id) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            CaptionHandler captionHandler = new CaptionHandler(id);
            saxParser.parse(is, captionHandler);//<font color="#FFFF00">I trust no one. </font><font color="#00FFFF">None of us are trustworthy.</font>
        } catch (Exception e) {
            Log.d("qwer", "" + e);
        }
    }
    public void updateStats(){
        stats.setText(Html.fromHtml("<font color=#2BAF10>"+right+"</font> - <font color=#0d33a3>"+half+"</font> - <font color=#EE1818>"+wrong+"</font>"));
    }
    public void adjustButtons(){
        boolean b = report.getVisibility()==View.INVISIBLE;
        if(b) {
            report.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            giveup.setVisibility(View.INVISIBLE);
        }
        else{
            report.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            result.setVisibility(View.INVISIBLE);
        }
        replay.setEnabled(!b);
        giveup.setEnabled(!b);
        submit.setEnabled(!b);
        answer.setEnabled(!b);

    }
    public void getResults(boolean fin){
        String t = pSub.getText().replaceAll("[^a-zA-Z0-9\\s']","");
        List<String> tList = Arrays.asList(t.split(" "));
        String r = answer.getText().toString().replaceAll("[^a-zA-Z0-9\\s']","").toLowerCase();
        List<String> rList = Arrays.asList(r.split(" "));
        List<Integer> incorrect = new ArrayList<>();
        for(int i = 0; i<tList.size(); i++)
            if(!rList.contains(tList.get(i).toLowerCase()))
                incorrect.add(i);
        if (incorrect.size() == 0) {
            right++;
            Toast toast = Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT);
            TextView text = toast.getView().findViewById(android.R.id.message);
            toast.setGravity(Gravity.TOP,0,0);
            text.setTextColor(Color.GREEN);
            text.setTextSize(25);
            toast.show();
            result.setText(Html.fromHtml("<font color=#2BAF10>\""+pSub.getText()+"\"</font?"));
            fin = true;
        }
        else if (incorrect.size()<tList.size()) {
            half++;
            StringBuilder res = new StringBuilder("\"");
            boolean correct = !incorrect.contains(0);
            if(correct)
                res.append("<font color=#2BAF10>");
            else
                res.append("<font color=#EE1818>");
            res.append(tList.get(0));
            for(int i = 1; i<tList.size(); i++){
                if(!incorrect.contains(i)==correct)
                    res.append(" ").append(tList.get(i));
                else{
                    res.append("</font> ");
                    if(!incorrect.contains(i))
                        res.append("<font color=#2BAF10>");
                    else
                        res.append("<font color=#EE1818>");
                    res.append(tList.get(i));
                    correct = !correct;
                }
            }
            res.append("</font>\"");
            result.setText(Html.fromHtml(res.toString()));
            Toast toast = Toast.makeText(MainActivity.this,"Close!",Toast.LENGTH_SHORT);
            TextView text = toast.getView().findViewById(android.R.id.message);
            toast.setGravity(Gravity.TOP,0,0);
            text.setTextColor(Color.BLUE);
            text.setTextSize(25);
            toast.show();
        }
        else {
            wrong++;
            result.setText(Html.fromHtml("<font color=#EE1818>\""+pSub.getText()+"\"</font?"));
            Toast toast = Toast.makeText(MainActivity.this,"Incorrect!",Toast.LENGTH_SHORT);
            TextView text = toast.getView().findViewById(android.R.id.message);
            toast.setGravity(Gravity.TOP,0,0);
            text.setTextColor(Color.RED);
            text.setTextSize(25);
            toast.show();
        }
        updateStats();
        if(fin) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC,5,0);
            playVideo(true);
            adjustButtons();
            result.setVisibility(View.VISIBLE);
            answer.setText("");
            Log.d("qwer",result.getText()+"\n"+result.getVisibility());
        }
        else {
            giveup.setVisibility(View.VISIBLE);
            Log.d("qwer","gdsafds");
        }
    }
    public void playVideo(boolean rep) {
        if (!rep) {
            //potentially some way of getting random caption w/o getting all at start
            /*DocumentReference docRef = db.collection("Captions").document("BJ");
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    pSub = documentSnapshot.toObject(Subtitle.class);
                }
            });*/
            pSub = captions.get((int) (Math.random() * captions.size()));
            am.setStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_MUTE,0);
        }
        //adds all captions to firestore
        /*for (Subtitle a : captions)
            db.collection("Captions").add(a);
            */
        Log.d("qwer", pSub + "");
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(0);
        Runnable helloRunnable = new Runnable() {
            public void run() {
                if (player.isPlaying() && start == 0)
                    start = System.currentTimeMillis();
                if (start != 0 && System.currentTimeMillis() >= start + pSub.getDuration() + pSub.getZOff()) {
                    player.pause();
                    start = 0;
                    executor.shutdownNow();
                }
            }
        };
        executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.MILLISECONDS);
        player.loadVideo(pSub.getVideoId(), pSub.getStartTime() + pSub.getAOff());
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        editor.putInt("right",right);
        editor.putInt("wrong",wrong);
        editor.putInt("half",half);
        editor.commit();
    }
}
