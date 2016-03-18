package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.tecnologiajo.diagnostictestsuniajc.R;

public class TestActivity extends AppCompatActivity {

    public boolean seleccion,finalizar;
    public ViewAnimator viewAnimator;
    public TextView timeTest;
    public RelativeLayout relativeLayout;
    Animation slide_in_left, slide_out_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        timeTest = (TextView) findViewById(R.id.timeTest);
        viewAnimator = (ViewAnimator)findViewById(R.id.viewanimator);

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(slide_in_left);
        viewAnimator.setOutAnimation(slide_out_right);
        seleccion=false;
        finalizar=false;

        CardView  cardview = new CardView(this);
        cardview.setPadding(2,2,2,2);

        //relativeLayout.addView(cardview);
        sincronizatedTest();

    }

    public void sincronizatedTest(){
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTest.setText(String.valueOf((millisUntilFinished/1000)));
                if(seleccion){
                    // cambiar de evaluacion
                    viewAnimator.showNext();
                    sincronizatedTest();
                }
            }

            public void onFinish() {
                viewAnimator.showNext();
                sincronizatedTest();
            }
        }.start();
    }

}
