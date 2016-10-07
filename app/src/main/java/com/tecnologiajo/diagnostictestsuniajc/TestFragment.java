package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.ContentManager;
import com.tecnologiajo.diagnostictestsuniajc.modelos.RequestResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener{

    public boolean seleccion,finalizar;
    public ViewAnimator viewAnimator;
    public TextView timeTest,textquestion,Q1,Q2,Q3,Q4,itempregunta,txtnextlayout;
    public ImageView imgnextlayout;
    public Button btnnextlayout;
    public RelativeLayout btnanswer1,btnanswer2,btnanswer3,btnanswer4;
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
    private CountDownTimer countDownTimer;
    private String dowload_service="F";
    private MediaPlayer mediaPlayer;
    private String dowload;
    private ContentManager contentManager;
    private LinearLayout linearLayout;
    private JSONObject jsonEmit;
    private Socket mSocket;
    private String codigo;
    private String device;
    private Boolean grouptest=false;
    private String codigoser;
    private Boolean begin=false;
    {
        try {
            mSocket = IO.socket(Constants.HOSTSERVER);
        } catch (URISyntaxException e) {}
    }

    public static TestFragment newInstance(Bundle arguments){
        TestFragment f = new TestFragment();
        if(arguments != null){
            f.setArguments(arguments);
        }
        return f;
    }

    public TestFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_diagnostics);
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        contentManager = new ContentManager(getActivity());

        //textquestion =(TextView) findViewById(R.id.textquestion);
        Q1 =(TextView) view.findViewById(R.id.txtQ1);
        Q2 =(TextView) view.findViewById(R.id.txtQ2);
        Q3 =(TextView) view.findViewById(R.id.txtQ3);
        Q4 =(TextView) view.findViewById(R.id.txtQ4);

        itempregunta =(TextView) view.findViewById(R.id.idpregunta);
        txtnextlayout =(TextView) view.findViewById(R.id.txtnextlayout);
        timeTest = (TextView) view.findViewById(R.id.timeTest);
        linearLayout= (LinearLayout) view.findViewById(R.id.contentQ);
        imgnextlayout = (ImageView) view.findViewById(R.id.imgnextlayout);
        btnnextlayout = (Button) view.findViewById(R.id.btnnextlayout);
        btnnextlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
        btnanswer1 = (RelativeLayout) view.findViewById(R.id.Q1);
        btnanswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer1();
            }
        });
        btnanswer2 = (RelativeLayout) view.findViewById(R.id.Q2);
        btnanswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer2();
            }
        });
        btnanswer3 = (RelativeLayout) view.findViewById(R.id.Q3);
        btnanswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer3();
            }
        });
        btnanswer4 = (RelativeLayout) view.findViewById(R.id.Q4);
        btnanswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer4();
            }
        });
        nextlayout = (LinearLayout) view.findViewById(R.id.nextlayout);
        viewAnimator = (ViewAnimator) view.findViewById(R.id.viewanimator);

        slide_in_left = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(slide_in_left);
        viewAnimator.setOutAnimation(slide_out_right);
        /** Inicializamos las banderas */
        seleccion=false;
        finalizar=false;
        next=0;
        /** Obtenemos el id del diagnostico*/
        docId= getArguments().getString("id", "");
        /** obtenemos la prueba si se descargo */

        diagnostico = getArguments().getString("diagnostico","");
        dowload = getArguments().getString("dowload","");
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
            grouptest=true;
            mSocket.connect();

            codigo = getArguments().getString("codigo","")+"-clt";
            codigoser = getArguments().getString("codigo","")+"-srv";
            device = getArguments().getString("device","");
            jsonEmit = new JSONObject();
            try {
                jsonEmit.put("name",getArguments().getString("name",""));
                jsonEmit.put("device",getArguments().getString("device",""));
                jsonEmit.put("connect",true);
                jsonEmit.put("isadd",true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.on(codigoser, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                if(data.getBoolean("begin")) {
                                    begin=true;
                                }
                                if(data.getBoolean("isnext")) {
                                    nextQuestion();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            progressDialog = ProgressDialog.show(getActivity(), "", "Searching..");
            progressDialog.setCancelable(true);
            btnnextlayout.setVisibility(View.INVISIBLE);
            /** Obtenemos el servicio App42 */
            if(!contentManager.isContentTemp()) {
                asyncService = AsyncApp42ServiceApi.instance(getActivity());
                asyncService.findDocByDocId(Constants.App42DBName, "diagnosticos", docId, this);
            }else{
                try {
                    JSONObject jsonObject = new JSONObject(contentManager.getContentTemp());
                    jsonArray = new JSONArray(jsonObject.getString("preguntas"));
                    id_creator = jsonObject.getString("id_creator");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } else {
            progressDialog = ProgressDialog.show(getActivity(), "", "Searching..");
            progressDialog.setCancelable(true);
            /** Obtenemos el servicio App42 */
            asyncService = AsyncApp42ServiceApi.instance(getActivity());
            asyncService.findDocByDocId(Constants.App42DBName, "diagnosticos", docId, this);
        }
        /** definimos la primera pregunta de la prueba **/
        itempregunta.setText("Q"+String.valueOf((next + 1)));
        /** Json para el resultado final **/
        jsonArrayResult = new JSONArray();
        return view;
    }

    public void TestType(String opt,String texto,String uri){


        linearLayout.removeAllViews();
        switch (opt){
            case "TEXTO":
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                        ((int) LinearLayout.LayoutParams.MATCH_PARENT,(int) LinearLayout.LayoutParams.MATCH_PARENT);
                params.leftMargin = 10;
                params.topMargin  = 10;
                textquestion = new TextView(getActivity());
                textquestion.setGravity(Gravity.CENTER);
                textquestion.setTextColor(Color.RED);
                textquestion.setTextSize(15);
                textquestion.setShadowLayer(1, 2, 2, Color.GRAY);
                textquestion.setLayoutParams(params);
                textquestion.setText(texto);
                linearLayout.addView(textquestion);
                break;
            case "AUDIO":
                mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(uri));

                LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams
                        ((int) LinearLayout.LayoutParams.MATCH_PARENT,(int) LinearLayout.LayoutParams.WRAP_CONTENT);
                params1.leftMargin = 5;
                params1.topMargin  = 5;
                textquestion = new TextView(getActivity());
                textquestion.setGravity(Gravity.CENTER);
                textquestion.setTextColor(Color.RED);
                textquestion.setTextSize(15);
                textquestion.setShadowLayer(1, 2, 2, Color.GRAY);
                textquestion.setLayoutParams(params1);



                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                LinearLayout.LayoutParams paramsbtn=new LinearLayout.LayoutParams
                        ((int) LinearLayout.LayoutParams.WRAP_CONTENT,(int) LinearLayout.LayoutParams.WRAP_CONTENT);
                final ImageButton play = new ImageButton(getActivity());
                paramsbtn.gravity=Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                play.setImageResource(R.drawable.speaker);
                play.setLayoutParams(paramsbtn);
                play.setBackgroundColor(Color.TRANSPARENT);
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.start();

                        }
                    }
                });

                linearLayout.addView(textquestion);

                linearLayout.addView(play);
                break;
            case "IMAGE":
                LinearLayout.LayoutParams paramimg=new LinearLayout.LayoutParams
                        ((int) LinearLayout.LayoutParams.MATCH_PARENT,(int) LinearLayout.LayoutParams.MATCH_PARENT);
                paramimg.leftMargin = 5;
                paramimg.topMargin  = 5;
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageBitmap(getImageBitmap(uri));
                imageView.setEnabled(true);
                imageView.setLayoutParams(paramimg);
                linearLayout.addView(imageView);
                break;
        }
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(null, "Error getting bitmap", e);
        }
        return bm;
    }

    public void sincronizatedTest(){
        if(!grouptest) {
            countDownTimer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    timeTest.setText(String.valueOf((millisUntilFinished / 1000)));
                }

                public void onFinish() {
                    txtnextlayout.setText("Incorrect!");
                    imgnextlayout.setImageResource(R.drawable.incorrect);
                    nextlayout.setBackgroundColor(Color.parseColor("#ae2929"));
                    viewAnimator.showNext();
                }
            }.start();
        }
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
            TestType(tipo, jsonObject1.getString("descripcion"), jsonObject1.getString("uri"));

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

    public void sendAnswer(String answer){
        RequestResult requestBody = new RequestResult();
        requestBody.setDescripcion(answer);
        requestBody.setEstado(false);
        requestBody.setGroupTest(codigoser);
        requestBody.setDevice(device);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOSTSERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<RequestResult> call = request.sendAnswer(requestBody);
        call.enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                viewAnimator.showNext();
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                Toast toast=Toast.makeText(getActivity(), t.getMessage(),Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /** pasa a la siguientee pregunta */
    public  void nextQuestion(){
        if(next<(jsonArray.length()-1)) {
            next++;
            itempregunta.setText("Q" + String.valueOf((next + 1)));
            jsonArrayResult.put(jsonObject1);
            assignQuestion();
            viewAnimator.showNext();

        }else{
            try {
                /*if(countDownTimer!=null) {
                    countDownTimer.cancel();
                    countDownTimer= null;
                }*/

                jsonArrayResult.put(jsonObject1);
                jsonDiagnostico.put("preguntas", jsonArrayResult);
                jsonDiagnostico.put("id_usuario","");
                jsonDiagnostico.put("id_creator",id_creator);

                progressDialog = ProgressDialog.show(getActivity(), "", "Input..");
                progressDialog.setCancelable(true);
                /** Obtenemos el servicio App42 */
                if(!dowload_service.equals("D")) {
                    asyncService.insertJSONDoc(Constants.App42DBName, "historial", jsonDiagnostico, this);
                }else {
                    progressDialog.dismiss();
                    contentManager.closeContentTemp();
                    Intent intent = new Intent(getActivity(),RsultActivity.class);
                    intent.putExtra("result",jsonDiagnostico.toString());
                    intent.putExtra("diagnostico", diagnostico);
                    intent.putExtra("id", Id);
                    intent.putExtra("dowload", dowload_service);
                    startActivity(intent);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /** selecciona repuesta opcion 1 */
    public void Answer1(){
        if(begin) {
            try {
                JSONObject jsonObject = new JSONObject(jsonObject1.getString("respuesta1"));
                jsonObject.put("selected", true);
                if (jsonObject.getBoolean("flag")) {
                    txtnextlayout.setText("Correct!");
                    imgnextlayout.setImageResource(R.drawable.correct);
                    nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
                } else {
                    txtnextlayout.setText("Incorrect!");
                    imgnextlayout.setImageResource(R.drawable.incorrect);
                    nextlayout.setBackgroundColor(Color.parseColor("#ae2929"));
                }
                jsonObject1.put("respuesta1", jsonObject);
                if (!grouptest) {
                    countDownTimer.cancel();
                }
                sendAnswer("1");

                //sendSelected();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /** selecciona repuesta opcion 2 */
    public void Answer2(){
        if(begin) {
            try {
                JSONObject jsonObject = new JSONObject(jsonObject1.getString("respuesta2"));
                jsonObject.put("selected", true);
                if (jsonObject.getBoolean("flag")) {
                    txtnextlayout.setText("Correct!");
                    imgnextlayout.setImageResource(R.drawable.correct);
                    nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
                } else {
                    txtnextlayout.setText("Incorrect!");
                    imgnextlayout.setImageResource(R.drawable.incorrect);
                    nextlayout.setBackgroundColor(Color.parseColor("#ae2929"));
                }
                jsonObject1.put("respuesta2", jsonObject);
                if (!grouptest) {
                    countDownTimer.cancel();
                }

                //sendSelected();
                sendAnswer("2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /** selecciona repuesta opcion 3 */
    public void Answer3(){
        if(begin) {
            try {
                JSONObject jsonObject = new JSONObject(jsonObject1.getString("respuesta3"));
                jsonObject.put("selected", true);
                if (jsonObject.getBoolean("flag")) {
                    txtnextlayout.setText("Correct!");
                    imgnextlayout.setImageResource(R.drawable.correct);
                    nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
                } else {
                    txtnextlayout.setText("Incorrect!");
                    imgnextlayout.setImageResource(R.drawable.incorrect);
                    nextlayout.setBackgroundColor(Color.parseColor("#ae2929"));
                }
                jsonObject1.put("respuesta3", jsonObject);
                if (!grouptest) {
                    countDownTimer.cancel();
                }

                //sendSelected();
                sendAnswer("3");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /** selecciona repuesta opcion 4 */
    public void Answer4(){
        if(begin) {
            try {
                JSONObject jsonObject = new JSONObject(jsonObject1.getString("respuesta4"));
                jsonObject.put("selected", true);
                if (jsonObject.getBoolean("flag")) {
                    txtnextlayout.setText("Correct!");
                    imgnextlayout.setImageResource(R.drawable.correct);
                    nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
                } else {
                    txtnextlayout.setText("Incorrect!");
                    imgnextlayout.setImageResource(R.drawable.incorrect);
                    nextlayout.setBackgroundColor(Color.parseColor("#ae2929"));
                }
                jsonObject1.put("respuesta4", jsonObject);
                if (!grouptest) {
                    countDownTimer.cancel();
                }
                viewAnimator.showNext();
                //sendSelected();
                sendAnswer("4");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDocumentInserted(Storage response) {

            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(),RsultActivity.class);
            intent.putExtra("result",jsonDiagnostico.toString());
            intent.putExtra("diagnostico", diagnostico);
            intent.putExtra("id", Id);
            intent.putExtra("dowload", dowload_service);
            startActivity(intent);

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
                contentManager.createContentTestTemp(docDocument);
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
    public void onDestroy() {
        super.onDestroy();
        contentManager.closeContentTemp();
    }

    @Override
    public void onPause() {
        super.onPause();
        contentManager.closeContentTemp();
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
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
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


