package com.example.lipreading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class IssueActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private MyAdapter adapter;
    private List<Issue> data;
    private Button submit;
    private String id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        recycler  = findViewById(R.id.b_recycler);
        submit  = findViewById(R.id.b_submit);

        id = getIntent().getStringExtra("id");

        data = new ArrayList<>();
        data.add(new Issue("Incorrect Start Time","The video clip starts too early or late.",true));
        data.add(new Issue("Incorrect End Time","The video clip ends too early or late.",true));
        data.add(new Issue("No Face Seen","The video clip does not show the face of the speaker.",false));
        data.add(new Issue("Incorrect Text","The subtitle does not match what is being said in the video.",false));
        data.add(new Issue("Other","Anything else that's wrong with this video clip.",false));
        adapter = new MyAdapter(data,this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("qwer",""+adapter.getPos());
                if(adapter.getPos()==RecyclerView.NO_POSITION)
                    Toast.makeText(IssueActivity.this, "Please make a selection.", Toast.LENGTH_SHORT).show();
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("firestoreId", id);
                    bundle.putString("option", data.get(adapter.getPos()).getTitle());
                    if (adapter.getPos() == 0 || adapter.getPos() == 1)
                        bundle.putInt("subOption", adapter.getClicked());
                    MainActivity.an.logEvent("Issue",bundle);
                    finish();
                }
            }
        });
    }
}
