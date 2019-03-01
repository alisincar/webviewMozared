package com.mozared.gcm;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mozared.app.Anasayfa;
import com.mozared.app.SplashScreen;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class RegisterApp extends AsyncTask<Void, Void, String> {


    private static final String TAG = "Mozared GCM";
    Context ctx;
    GoogleCloudMessaging gcm;
    final String PROJECT_ID = "938307354288";
    String regid = null;
    private int appVersion;

    public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion){
        this.ctx = ctx;
        this.gcm = gcm;
        this.appVersion = appVersion;
    }



        @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Void... arg0) { //
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);//GCM objesi oluşturduk ve gcm referansına bağladık
            }
            regid = gcm.register(PROJECT_ID);//gcm objesine PROJECT_ID mizi göndererek regid değerimizi aldık.Bu değerimizi hem sunucularımıza göndereceğiz Hemde Androidde saklıyacağız
            msg = "Registration ID=" + regid;

            sendRegistrationIdToBackend();//Sunuculara regid gönderme işlemini yapacak method

            storeRegistrationId(ctx, regid);//Androidde regid saklı tutacak method

        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();

        }
        return msg;
    }

    private void storeRegistrationId(Context ctx, String regid) {//Androidde regid ve appversion saklı tutacak method
        //Burada SharedPreferences kullanarak kayıt yapmaktadır
        //SharedPreferences hakkında ayrıntılı dersi Bloğumuzda bulabilirsiniz.
        final SharedPreferences prefs = ctx.getSharedPreferences(SplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", regid);
        editor.putInt("appVersion", appVersion);
        editor.commit();

    }









    private void sendRegistrationIdToBackend() {
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
        }*/
        URI url = null;
        try {
            url = new URI("http://www.mozared.com/nat/register.php?regId=" + regid+"&kadi="+Anasayfa.cookie);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);
        try {
            httpclient.execute(request);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    @Override
    protected void onPostExecute(String result) {
        //doInBackground işlemi bittikten sonra çalışır
        super.onPostExecute(result);
        //Toast.makeText(ctx, "Registration "+ Anasayfa.cookie, Toast.LENGTH_SHORT).show();
        Log.v(TAG, result);
        Intent i = new Intent(ctx,Anasayfa.class);//Anasayfaya Yönlendir
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ctx.startActivity(i);

    }
}