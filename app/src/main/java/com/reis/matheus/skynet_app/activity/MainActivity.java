package com.reis.matheus.skynet_app.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.reis.matheus.skynet_app.R;
import com.reis.matheus.skynet_app.repository.RepoConversation;
import com.reis.matheus.skynet_app.service.ConversationServices;
import com.reis.matheus.skynet_app.tools.Permission;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnDSListener{

    public final String TAG = "c";
    public String lastCommand = "";

    private DroidSpeech droidSpeech;
    private TextView finalSpeechResult;
    private String language = "pt-BR";

    private boolean can;

    // MARK: Activity Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Permission.verifyPermissions(1, this))
            config();

        // Setting the layout;
        setContentView(R.layout.activity_main);
        //config();
    }

    public void config(){
        // Initializing the droid speech and setting the listener
        droidSpeech = new DroidSpeech(this, getFragmentManager());
        droidSpeech.setOnDroidSpeechListener(this);
        droidSpeech.setShowRecognitionProgressView(true);
        //droidSpeech.setOneStepResultVerify(true);
        droidSpeech.setRecognitionProgressMsgColor(Color.WHITE);
        droidSpeech.setOneStepVerifyConfirmTextColor(Color.WHITE);
        droidSpeech.setOneStepVerifyRetryTextColor(Color.WHITE);

        droidSpeech.startDroidSpeechRecognition();
        can = true;
    }

    // MARK: DroidSpeechListener Methods

    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {
        Log.i(TAG, "Current speech language = " + currentSpeechLanguage);
        Log.i(TAG, "Supported speech languages = " + supportedSpeechLanguages.toString());

        droidSpeech.setPreferredLanguage(language);
    }


    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue) {
        // Log.i(TAG, "Rms change value = " + rmsChangedValue);
    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult) {
        Log.i(TAG, "Live speech result = " + liveSpeechResult);
    }

    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult) {
        // Setting the final speech result
        Toast.makeText(this, finalSpeechResult, Toast.LENGTH_SHORT).show();

        if (!lastCommand.equals(finalSpeechResult))
            chamaApiSkynet(finalSpeechResult);
        lastCommand = finalSpeechResult;

        if (droidSpeech.getContinuousSpeechRecognition()) {
            int[] colorPallets1 = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA};
            int[] colorPallets2 = new int[]{Color.YELLOW, Color.RED, Color.CYAN, Color.BLUE, Color.GREEN};

            // Setting random color pallets to the recognition progress view
            droidSpeech.setRecognitionProgressViewColors(new Random().nextInt(2) == 0 ? colorPallets1 : colorPallets2);
        }
    }

    @Override
    public void onDroidSpeechClosedByUser() {
        droidSpeech.startDroidSpeechRecognition();
    }

    @Override
    public void onDroidSpeechError(String errorMsg) {
        // Speech error
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    // MARK: DroidSpeechPermissionsListener Method
    private void chamaApiSkynet(final String text) {
        if(!can) return;
        try{
            can = false;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.43.86:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ConversationServices conversationServices = retrofit.create(ConversationServices.class);

            Call<RepoConversation> repos = conversationServices.postMessage(text);
            repos.enqueue(new Callback<RepoConversation>() {
                @Override
                public void onResponse(Call<RepoConversation> call, retrofit2.Response<RepoConversation> response) {
                    RepoConversation repoResponse = response.body();
                    System.out.println(repoResponse.getResponse());

                    if(repoResponse.getLanguage() != null){
                        System.out.println(repoResponse.getLanguage());
                        switch(repoResponse.getLanguage()){
                            case "alemao":
                                droidSpeech.setPreferredLanguage("de-DE");
                                language = "de-DE";
                                break;
                            case "arabe":
                                droidSpeech.setPreferredLanguage("ar-TN");
                                language = "ar-TN";
                                break;
                            case "espanhol":
                                droidSpeech.setPreferredLanguage("en-EN");
                                language = "en-EN";
                                break;
                            case "frances":
                                droidSpeech.setPreferredLanguage("fr-FR");
                                language = "fr-FR";
                                break;
                            case "grego":
                                droidSpeech.setPreferredLanguage("el-GR");
                                language = "el-GR";
                                break;
                            case "ingles":
                                droidSpeech.setPreferredLanguage("en-US");
                                language = "en-US";
                                break;
                            case "italiano":
                                droidSpeech.setPreferredLanguage("it-IT");
                                language = "it-IT";
                                break;
                            case "japones":
                                droidSpeech.setPreferredLanguage("ja-JP");
                                language = "ja-JP";
                                break;
                            case "portugues":
                                droidSpeech.setPreferredLanguage("pt-BR");
                                language = "pt-BR";
                                break;
                        }
                    }
                    can = true;
                }

                @Override
                public void onFailure(Call<RepoConversation> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            } else {
                config();
            }
        }
    }

    public void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar esse app é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
