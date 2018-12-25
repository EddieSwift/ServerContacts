package com.eddie.servercontacts;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


public abstract class WaitingActivity extends AppCompatActivity {

    private FrameLayout waitingFrame;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void wrapToWaitingActivity(View mainView){
        setContentView(R.layout.activity_waiting);
        mainLayout = findViewById(R.id.main);
        waitingFrame = findViewById(R.id.waiting_frame);
        waitingFrame.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        mainLayout.addView(mainView, params);
    }

    public void setWaitingMode(){
        waitingFrame.setVisibility(View.VISIBLE);
    }

    public void desetWaitingMode(){
        waitingFrame.setVisibility(View.GONE);
    }

    public void showError(String error){
        new AlertDialog.Builder(this).setTitle("Error")
                .setCancelable(false)
                .setMessage(error)
                .setPositiveButton("Ok", null)
                .create().show();
    }
}
