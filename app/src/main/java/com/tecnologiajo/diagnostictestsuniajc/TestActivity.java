package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnosticos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener{

    public boolean seleccion,finalizar;
    public ViewAnimator viewAnimator;
    public TextView timeTest,textquestion,Q1,Q2,Q3,Q4,itempregunta,txtnextlayout;
    public ImageView imgnextlayout;
    public Button btnnextlayout;
    public LinearLayout nextlayout;

    public RelativeLayout relativeLayout;
    Animation slide_in_left, slide_out_right;
    /** The async service. */
    private AsyncApp42ServiceApi asyncService;
    /** The progress dialog. */
    private ProgressDialog progressDialog;
    /** The id . */
    private String Id = "";
    /** The doc id. */
    private String docId = "";
    /** resive el documento json devuelto del servicio */
    private String docDocument="";
    /** The doc id_creator id padre. */
    private String id_creator = "";
    /** Documento asignatura */
    private String diagnostico = "";
    /** Documento tipo */
    private String tipo = "";
    /** json array para el grupo de respuestas */
    JSONArray jsonArray;
    JSONArray jsonArrayResult;
    JSONObject jsonDiagnostico;
    JSONObject jsonObject1;
    /** contador para las preguntas */
    private int next;
    /** Controlador de tiempos */
    CountDownTimer countDownTimer;
    private SharedPreferences sharedpreferences;
    private String dowload_service="F";

    double startTime = 0;
    double finalTime = 0;
    final Handler myHandler = new Handler();;
    int oneTimeOnly=0;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //textquestion =(TextView) findViewById(R.id.textquestion);
        Q1 =(TextView) findViewById(R.id.txtQ1);
        Q2 =(TextView) findViewById(R.id.txtQ2);
        Q3 =(TextView) findViewById(R.id.txtQ3);
        Q4 =(TextView) findViewById(R.id.txtQ4);
        itempregunta =(TextView) findViewById(R.id.idpregunta);
        txtnextlayout =(TextView) findViewById(R.id.txtnextlayout);
        timeTest = (TextView) findViewById(R.id.timeTest);

        imgnextlayout = (ImageView) findViewById(R.id.imgnextlayout);
        btnnextlayout = (Button) findViewById(R.id.btnnextlayout);
        nextlayout = (LinearLayout) findViewById(R.id.nextlayout);
        viewAnimator = (ViewAnimator)findViewById(R.id.viewanimator);

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(slide_in_left);
        viewAnimator.setOutAnimation(slide_out_right);
        /** Inicializamos las banderas */
        seleccion=false;
        finalizar=false;
        next=0;
        /** Obtenemos el id del diagnostico*/
        docId= getIntent().getExtras().getString("id", "");
        /** obtenemos la prueba si se descargo */
        sharedpreferences = getSharedPreferences("diagnosticos", Context.MODE_PRIVATE);
        String diagnostic_dowload=sharedpreferences.getString(docId,"");
        diagnostico = getIntent().getExtras().getString("diagnostico","");
        String dowload = getIntent().getExtras().getString("dowload","");
        dowload_service= dowload;
        if(dowload.equals("D")){

            docDocument=diagnostico;
            Id = docId;
            try {
                jsonDiagnostico = new JSONObject(docDocument);
                jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));
                id_creator = jsonDiagnostico.getString("id_creator");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assignQuestion();
        } else if(dowload.equals("A")){
            String codigo = getIntent().getExtras().getString("codigo","");
            progressDialog = ProgressDialog.show(this, "", "Searching..");
            progressDialog.setCancelable(true);
            /** Obtenemos el servicio App42 */
            asyncService = AsyncApp42ServiceApi.instance(this);
            asyncService.findDocByKeyValue(Constants.App42DBName, "diagnosticos", "codigo", codigo, this);
        } else{
            progressDialog = ProgressDialog.show(this, "", "Searching..");
            progressDialog.setCancelable(true);
            /** Obtenemos el servicio App42 */
            asyncService = AsyncApp42ServiceApi.instance(this);
            asyncService.findDocByDocId(Constants.App42DBName, "diagnosticos", docId, this);
        }
        /** definimos la primera pregunta de la prueba **/
        itempregunta.setText("Q"+String.valueOf((next + 1)));
        /** Json para el resultado final **/
        jsonArrayResult = new JSONArray();



    }


    public void TestType(String opt){
        LayoutInflater inflater = LayoutInflater.from(this);
        int ids = R.id.contentQ;
        LinearLayout linearLayout = (LinearLayout) findViewById(ids);
        linearLayout.removeAllViews();
        switch (opt){
            case "TEXTO":
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams
                        ((int) RelativeLayout.LayoutParams.MATCH_PARENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 10;
                params.topMargin  = 10;
                textquestion = new TextView(this);
                textquestion.setGravity(Gravity.CENTER);
                textquestion.setTextColor(Color.RED);
                textquestion.setTextSize(25);
                textquestion.setShadowLayer(1,2,2,Color.GRAY);
                textquestion.setLayoutParams(params);
                linearLayout.addView(textquestion);
                break;
            case "AUDIO":
                mediaPlayer = MediaPlayer.create(this, Uri.parse("http://programmerguru.com/android-tutorial/wp-content/uploads/2013/04/hosannatelugu.mp3"));

                RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams
                        ((int) RelativeLayout.LayoutParams.MATCH_PARENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);
                params1.leftMargin = 5;
                params1.topMargin  = 5;
                textquestion = new TextView(this);
                textquestion.setGravity(Gravity.CENTER);
                textquestion.setTextColor(Color.RED);
                textquestion.setTextSize(15);
                textquestion.setShadowLayer(1, 2, 2, Color.GRAY);
                textquestion.setLayoutParams(params1);

                RelativeLayout.LayoutParams paramsskb=new RelativeLayout.LayoutParams
                        ((int) RelativeLayout.LayoutParams.MATCH_PARENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);
                seekBar = new SeekBar(this);
                seekBar.setLayoutParams(paramsskb);
                seekBar.setClickable(false);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            seekChanged(seekBar, progress, fromUser);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                RelativeLayout.LayoutParams paramsbtn=new RelativeLayout.LayoutParams
                        ((int) RelativeLayout.LayoutParams.WRAP_CONTENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);
                final Button play = new Button(this);
                paramsbtn.addRule(LinearLayout.LAYOUT_DIRECTION_INHERIT);
                play.setText("Play");
                play.setGravity(Gravity.CENTER);
                play.setLayoutParams(paramsbtn);
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            play.setText("stop");
                            updateSeekBar();

                        } else {
                            mediaPlayer.stop();
                            play.setText("play");
                            seekBar.setProgress(0);
                            seekBar.invalidate();
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        seekBar.setProgress(0);
                        seekBar.invalidate();
                    }
                });
                linearLayout.addView(textquestion);
                linearLayout.addView(seekBar);
                linearLayout.addView(play);
                break;
        }
    }
    public void seekChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mediaPlayer.seekTo(progress);
    }

    public void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition()/100);

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    updateSeekBar();
                }
            };
            myHandler.postDelayed(notification, 1000);
        }
    }



    public void sincronizatedTest(){
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTest.setText(String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish() {
                mediaPlayer.stop();
                txtnextlayout.setText("Incorrect!");
                imgnextlayout.setImageResource(R.drawable.xx);
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
                viewAnimator.showNext();
            }
        }.start();
    }
    /** recorre el documento json extraido del servicio
     *  para esteblecer la pregunta con sus posible
     *  respuestas y activa el cronometro
     * */
    public void assignQuestion(){
        try {
            sincronizatedTest();

            jsonObject1  = jsonArray.getJSONObject(next);
            tipo = jsonObject1.getString("tipo");
            TestType(tipo);
            textquestion.setText(jsonObject1.getString("descripcion"));
            JSONObject jsonObject2  = new JSONObject(jsonObject1.getString("respuesta1"));
            JSONObject jsonObject3  = new JSONObject(jsonObject1.getString("respuesta2"));
            JSONObject jsonObject4  = new JSONObject(jsonObject1.getString("respuesta3"));
            JSONObject jsonObject5  = new JSONObject(jsonObject1.getString("respuesta4"));
            Q1.setText(jsonObject2.getString("descripcion"));
            Q2.setText(jsonObject3.getString("descripcion"));
            Q3.setText(jsonObject4.getString("descripcion"));
            Q4.setText(jsonObject5.getString("descripcion"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** pasa a la siguientee pregunta */
    public void nextQuestion(View view){
        if(next<(jsonArray.length()-1)) {
            next++;
            itempregunta.setText("Q" + String.valueOf((next + 1)));
            jsonArrayResult.put(jsonObject1);
            assignQuestion();
            viewAnimator.showNext();

        }else{
            try {
                jsonArrayResult.put(jsonObject1);
                jsonDiagnostico.put("preguntas", jsonArrayResult);
                jsonDiagnostico.put("id_usuario","");
                jsonDiagnostico.put("id_creator",id_creator);

                progressDialog = ProgressDialog.show(this, "", "Input..");
                progressDialog.setCancelable(true);
                /** Obtenemos el servicio App42 */
                if(!dowload_service.equals("D")) {
                    asyncService.insertJSONDoc(Constants.App42DBName, "historial", jsonDiagnostico, this);
                }else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(),RsultActivity.class);
                    intent.putExtra("result",jsonDiagnostico.toString());
                    intent.putExtra("diagnostico", diagnostico);
                    intent.putExtra("id", Id);
                    intent.putExtra("dowload", dowload_service);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /** selecciona repuesta opcion 1 */
    public void Answer1(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta1"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else{
                txtnextlayout.setText("Incorrecto!");
                imgnextlayout.setImageResource(R.drawable.xx);
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta1", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 2 */
    public void Answer2(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta2"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else {
                txtnextlayout.setText("Incorrecto!");
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta2", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 3 */
    public void Answer3(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta3"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else{
                txtnextlayout.setText("Incorrecto!");
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta3", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 4 */
    public void Answer4(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta4"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else{
                txtnextlayout.setText("Incorrecto!");
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta4", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDocumentInserted(Storage response) {
        /*if(response.isFromCache()){
            Diagnosticos diagnosticos= new Diagnosticos(this);
            try {
                jsonDiagnostico.put("registrado",false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            diagnosticos.insertarDIAGNOSTICOS(Id, jsonDiagnostico.toString());
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(),RsultActivity.class);
            intent.putExtra("result", jsonDiagnostico.toString());
            startActivity(intent);

        }else{*/
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(),RsultActivity.class);
            intent.putExtra("result",jsonDiagnostico.toString());
            intent.putExtra("diagnostico", diagnostico);
            intent.putExtra("id", Id);
            intent.putExtra("dowload", dowload_service);
            startActivity(intent);
            finish();
        //}

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        //if(response.isFromCache()){
            docDocument=response.getJsonDocList().get(0).getJsonDoc();
            Id = response.getJsonDocList().get(0).getDocId();
            try {
                jsonDiagnostico = new JSONObject(docDocument);
                jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));
                id_creator = jsonDiagnostico.getString("id_creator");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            assignQuestion();
            progressDialog.dismiss();

        /*}else{
            createAlertDialog("Sin conexion : ");
            progressDialog.dismiss();
        }*/

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Exception Occurred : "+ ex.getMessage());
    }
    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Exception Occurred : "+ ex.getMessage());
    }
    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }
    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(TestActivity.this);
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        alertbox.show();
    }
}
