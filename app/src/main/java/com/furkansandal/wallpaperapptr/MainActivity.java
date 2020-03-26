package com.furkansandal.wallpaperapptr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private GridView lv;
    final ArrayList<WpUrl> wallpapers = new ArrayList<WpUrl>();
    ArrayList<String> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
        //progressDialog.show();

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Duvar Kagitlari Yukleniyor...");
        pDialog.setCancelable(false);
        pDialog.show();

        contactList = new ArrayList<String>();
        lv = (GridView) findViewById(R.id.simpleGridView);

        new GetContacts().execute();
        pDialog.dismissWithAnimation();
        haveStoragePermission();



        /*
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,"Veriler geliyor...",Toast.LENGTH_LONG).show();

        }

         */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.baslik))
                        .setContentText("Isterseniz galerinize kayit edebilir, isterseniz duvar kagidi olarak ayarlayabilirsiniz.")
                        .setConfirmText("Duvar Kagidi Yap")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("Telefona Indir", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String url = wallpapers.get(position).getUrl();
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                request.setDescription("God of War");
                                request.setTitle("KRATOOOS");

                                //burada çok çok eski bir sürüm için gerekli bir denetleme yapıyoruz. bu kısmı kaldırabilirsiniz.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                }

                                //dosyayı yazacağımız yer ve dosyanın ismine karar verebiliyoruz.
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "duvarkagidi"+position+".png");

                                //download servisini çalıştırma ve kuyruğa alması
                                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                                Toast.makeText(getApplicationContext(), "Indirilenler klasorunuze kayit edildi.", Toast.LENGTH_LONG).show();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
        });
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://furkansandal.com/wallpaperapp/showjson.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("data");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("url");
                        wallpapers.add(new WpUrl(c.getString("url")));
                        /*
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        // adding contact to contact list
                        contactList.add(contact);

                         */
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();

                         */
                        Toast.makeText(getApplicationContext(),
                                "Internet erisiminizi kontrol ediniz...!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            //ResimAdapter adapterx = new ResimAdapter(getApplicationContext(), contactList, inflater);
            super.onPostExecute(result);
            ResimAdapter res = new ResimAdapter(MainActivity.this,MainActivity.this, wallpapers);
            //ListAdapter adapter = new SimpleAdapter(MainActivity.this,
            //                                                contactList,
              //                                              R.layout.item_list,
                //                                            new String[]{"url"},
                  //                                          new int[]{R.id.email});
            lv.setAdapter(res);
        }
    }
    public  boolean haveStoragePermission() {

        //izin alma işlemi api level 23'den sonra geldiği için onu kontrol ediyoruz.
        if (Build.VERSION.SDK_INT >= 23) {

            //manifestteki izin kontrol ediliyor.
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.e("Permission error","You have permission");
                return true;
            } else {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Izniniz Gerekli!")
                        .setContentText("Duvar kagitlarini cihaziniza kayit edebilmek icin dosya erisim izni vermeniz gerekmekte.")
                        .setConfirmText("Tamam")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                sweetAlertDialog.dismissWithAnimation();
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        })
                        .show();
                Log.e("Permission error","You have asked for permission");

                return false;
            }
        }

        // kullanıcı 23'den eski bir sistem kullanıyorsa izin alma işlemi yapılmadan devam ediliyor.
        else {
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }
}