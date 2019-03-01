package com.mozared.app;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mozared.gcm.RegisterApp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Toast;

public class SplashScreen extends Activity {

 private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
 public static final String PROPERTY_REG_ID = "registration_id";
 private static final String PROPERTY_APP_VERSION = "appVersion";
 private static final String TAG = "Mozared GCM";
 GoogleCloudMessaging gcm;
 String regid;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.splash);



  if (checkPlayServices()) {//GOOGLE PLAY SERV�CE APK Y�KL�M�
		gcm = GoogleCloudMessaging.getInstance(getApplicationContext());//GoogleCloudMessaging objesi olu�turduk
		regid = getRegistrationId(getApplicationContext()); //registration_id olup olmad���n� kontrol ediyoruz


      if(regid.isEmpty()){//YEN� KAYIT
			  //regid de�erimiz bo� gelmi�se uygulama ya ilk kez ac�l�yor yada g�ncellenmi� demektir.Registration i�lemleri tekrardan yap�lacak.
	          new RegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute(); //RegisterApp clas�n� �al��t�r�yoruz ve de�erleri g�nderiyoruz
		}else{
           new RegisterApp( getApplicationContext(), gcm, getAppVersion( getApplicationContext() ) ).execute(); //RegisterApp clas�n� �al��t�r�yoruz ve de�erleri g�nderiyoruz

          //regid de�erimiz bo� gelmemi�se �nceden registration i�lemleri tamamlanm�� ve g�ncelleme olmam�� demektir.Yani uygulama direk a��lacak
			  //Arkada�lar e�er splash ekran�n�n g�z�kmesini istiyorsan�z thread kullan�p 2 3 sn bekletebilirsiniz.Daha sonra a�a��daki i�lemlere ba�layabilirsiniz
             // Toast.makeText(getApplicationContext(), "Bu cihaz önceden kaydedilmiş "+Anasayfa.cookie, Toast.LENGTH_SHORT).show();
          /*try {
              Thread.sleep(2000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }*/
          Intent i = new Intent(getApplicationContext(),Anasayfa.class);//Anasayfaya Y�nlendir
     		   startActivity(i);
      		   finish();
		   }

  }

 }


    private boolean checkPlayServices() {
	//Google Play Servis APK y�kl�m�
	 //Y�kl� De�ilse Log bas�p kapat�cak uygulamay�
	 //Siz kullan�c�ya uyar� verdirip Google Play Apk Kurmas�n� isteyebilirsiniz

     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
     if (resultCode != ConnectionResult.SUCCESS) {
         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
             GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                     PLAY_SERVICES_RESOLUTION_REQUEST).show();
         } else {
             Log.i(TAG, "Google Play Servis Yükleyin.");
             finish();
         }
         return false;
     }
     return true;
 }
 private String getRegistrationId(Context context) { //registration_id geri d�ner
	 //Bu method registration id ye bakar.
	 //Bu uygulamada registration id nin �nceden olabilmesi i�in uygulaman�n �nceden a��lm�� ve registration i�lemlerini yapm�� olmas� laz�m
	 //Uygulama �nceden ac�ld�ysa registration_id SharedPreferences yard�m� ile kaydedilir.

     final SharedPreferences prefs = getGCMPreferences(context);
     String registrationId = prefs.getString(PROPERTY_REG_ID, "");//registration_id de�eri al�nd�
     if (registrationId.isEmpty()) {//e�er bo�sa �nceden kaydedilmemi� yani uygulama ilk kez �al���yor.
         Log.i(TAG, "Registration id bulunamadı.");
         return "";
     }

     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
     int currentVersion = getAppVersion(getApplicationContext());//yine SharedPreferences a kaydedilmi� version de�erini ald�k
     if (registeredVersion != currentVersion) {//versionlar uyu�muyorsa g�ncelleme olmu� demektir. Yani tekrardan registration i�lemleri yap�lcak
         Log.i(TAG, "App version değişmiş.");
         return "";
     }
     return registrationId;
 }

 private SharedPreferences getGCMPreferences(Context context) {
     return getSharedPreferences(SplashScreen.class.getSimpleName(),
             Context.MODE_PRIVATE);
 }

 private static int getAppVersion(Context context) { //Versiyonu geri d�ner
     try {
         PackageInfo packageInfo = context.getPackageManager()
                 .getPackageInfo(context.getPackageName(), 0);
         return packageInfo.versionCode;
     } catch (NameNotFoundException e) {
         // should never happen
         throw new RuntimeException("Paket versiyonu bulunamadı: " + e);
     }
 }
}