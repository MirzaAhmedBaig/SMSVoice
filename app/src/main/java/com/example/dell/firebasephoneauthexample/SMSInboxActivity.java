package com.example.dell.firebasephoneauthexample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class SMSInboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsinbox);

        ListView lViewSMS = (ListView) findViewById(R.id.listViewSMS);


        //PERMISSIONS
        final int MY_PERMISSIONS_REQUEST_SMS = 1;
        final String[] sms_permissions =
                {
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS
                };
        if ((int) Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                        builder.setMessage("To get storage access you have to allow us access to your sd card content.");
                        builder.setTitle("Storage");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SMSInboxActivity.this, sms_permissions, 0);

                            }
                        });

                        builder.show();
                    } else {
                        ActivityCompat.requestPermissions(this, sms_permissions, 0);

                    }
                } else {
                    ActivityCompat.requestPermissions(SMSInboxActivity.this,
                            sms_permissions,
                            MY_PERMISSIONS_REQUEST_SMS);

                }

            }
        }



        if (fetchInbox() != null) {
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fetchInbox());

            lViewSMS.setAdapter(adapter);
            if (ContextCompat.checkSelfPermission(getBaseContext(),"android.permision.READ_SMS")== getPackageManager().PERMISSION_GRANTED){
                fetchInbox();

            }else {
                final  int REQUEST_CODE_ASK_PERMISSION =123;
                ActivityCompat.requestPermissions(SMSInboxActivity.this,new String[]{"android.permission.READ_SMS"},REQUEST_CODE_ASK_PERMISSION);

            }
        }
    }
    public ArrayList fetchInbox () {

        ArrayList sms = new ArrayList();

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String address = cursor.getString(1);
            String body = cursor.getString(3);

            System.out.println("======&gt; Mobile number =&gt; " + address);
            System.out.println("=====&gt; SMS Text =&gt; " + body);

            sms.add("Address=&gt; " + address + "n SMS =&gt; " + body);
        }
        return sms;
    }
}
