package com.example.dell.firebasephoneauthexample.smsmodule;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dell.firebasephoneauthexample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SMSActivity extends AppCompatActivity implements MessageTranslateListener, TextToSpeech.OnInitListener {

    private String TAG = SMSActivity.class.getSimpleName();
    private int permsRequestCode = 200;
    private int speechRequestCode = 201;
    private RecyclerView messagesRecyclerView;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        setLanguageSpinner();
        requestForPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    private void setLanguageSpinner() {
        spinner = findViewById(R.id.language_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.langs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void requestForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            gotPermission();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, TextToSpeech.Engine.ACTION_CHECK_TTS_DATA}, permsRequestCode);
        }
    }

    private void checkLanguageResourceFile() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, speechRequestCode);
    }

    private void gotPermission() {
        setMessagesAdapter();
    }

    private void onPermissionDenied() {
        Toast.makeText(this, "Need all permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permsRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gotPermission();
        } else {
            onPermissionDenied();
        }
    }


    private TextToSpeech textToSpeech;

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == speechRequestCode) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech = new TextToSpeech(SMSActivity.this, this);
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    private void setMessagesAdapter() {
        messagesRecyclerView = findViewById(R.id.messages_list);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(new MessageAdapter(getAllSms(), this));
    }


    public List<SMSData> getAllSms() {
        List<SMSData> lstSms = new ArrayList();
        SMSData objSms;

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor != null) {
            int totalSMS = cursor.getCount();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new SMSData();
                    objSms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(cursor.getString(cursor
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                    objSms.setReadState(cursor.getString(cursor.getColumnIndex("read")));
                    objSms.setTime(Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("date"))));

                    if (objSms.getMsg().contains("credited") || objSms.getMsg().contains("debited") || objSms.getMsg().contains("withdrawn")) {
                        lstSms.add(objSms);
                    }

                    cursor.moveToNext();
                }
            }
            cursor.close();
        }


        return lstSms;
    }

    private String messageBody;

    @Override
    public void onMessageTranslateRequest(int position) {
        messageBody = ((MessageAdapter) messagesRecyclerView.getAdapter()).data.get(position).getMsg();
        Log.d(TAG, "Message Body " + messageBody);
        checkLanguageResourceFile();
    }


    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");
        if (spinner.getSelectedItem().equals("English")) {
            textToSpeech.setLanguage(Locale.US);
            textToSpeech.speak(messageBody, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            String msg = getParsedMessage(messageBody);
            textToSpeech.setLanguage(new Locale(getLocal(), "IND"));
            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private String getLocal() {
        switch ((String) spinner.getSelectedItem()) {
            case "Custom English":
                return "en";
            case "Hindi":
                return "hi";
            case "Telugu":
                return "hi";
            default:
                return "hi";
        }

    }


    private void testDummy() {
        Log.d(TAG, "Size : " + messagesArray.length);
        for (int i = 0; i < messagesArray.length; i++) {
            Log.d(TAG, "Message" + i + " : " + getParsedMessage(messagesArray[i]));
        }
    }

    private String getParsedMessage(String msg) {
        String xxxValue = null;
        String yyyValue = null;
        String transectionType = null;
        if (msg.contains("debited") || msg.contains("Debit Card") || msg.contains("withdrawn") || msg.contains("withdrawal") || msg.contains("Debited") || msg.contains("debit card") || msg.contains("Withdrawn") || msg.contains("Withdrawal")) {
            transectionType = "debit";
            if (msg.contains("INR ")) {
                Pair<String, String> value = parseMessageAmount(msg, "INR ");
                xxxValue = value.first;
                yyyValue = value.second;
            } else {
                Pair<String, String> value = parseMessageAmount(msg, "Rs.");
                xxxValue = value.first;
                yyyValue = value.second;
            }

        } else {
            transectionType = "credit";
            if (msg.contains("INR ")) {
                Pair<String, String> value = parseMessageAmount(msg, "INR ");
                xxxValue = value.first;
                yyyValue = value.second;
            } else {
                Pair<String, String> value = parseMessageAmount(msg, "Rs.");
                xxxValue = value.first;
                yyyValue = value.second;
            }
        }
        if (transectionType.equals("debit")) {
            if (yyyValue == null) {
                return getDebitSpeakingMessage(xxxValue);
            } else {
                return getDebitSpeakingMessageWithAVL(xxxValue, yyyValue);
            }
        } else {
            if (yyyValue == null) {
                return getCreditSpeakingMessage(xxxValue);
            } else {
                return getCreditSpeakingMessageWithAVL(xxxValue, yyyValue);
            }
        }
    }


    private Pair<String, String> parseMessageAmount(String msg, String predicate) {

        msg = "xyz " + msg.replace("Rs ", "Rs.");
        String[] dummy;
        String xxxD = null;
        String yyyD = null;
        if (msg.contains(predicate)) {
            String[] messageParts = msg.split(predicate);
            String debitAmount = messageParts[1].trim().split(" ")[0];
            dummy = debitAmount.split("\\.");
            if (dummy.length > 2) {
                debitAmount = debitAmount.substring(0, debitAmount.length() - debitAmount.indexOf(dummy[dummy.length - 1]));
            }
            xxxD = debitAmount;
            if (messageParts.length > 2) {
                String ailBalance = messageParts[messageParts.length - 1].trim().split(" ")[0];
                dummy = ailBalance.split("\\.");
                if (dummy.length > 2) {
                    ailBalance = ailBalance.substring(0, ailBalance.length() - ailBalance.indexOf(dummy[dummy.length - 1]));
                }
                yyyD = ailBalance;
            }

        }
        return new Pair<>(xxxD, yyyD);
    }


    private String getDebitSpeakingMessageWithAVL(String xxx, String yyy) {
        switch ((String) spinner.getSelectedItem()) {
            case "Custom English":
                return MessagesStrings.englishMessageForDebitOne.replace("xxx", xxx).replace("yyy", yyy);
            case "Hindi":
                return MessagesStrings.hindiMessageForDebitOne.replace("xxx", xxx).replace("yyy", yyy);
            case "Telugu":
                return MessagesStrings.teluguMessageForDebitOne.replace("xxx", xxx).replace("yyy", yyy);
            default:
                return MessagesStrings.kannadaMessageForDebitOne.replace("xxx", xxx).replace("yyy", yyy);
        }
    }

    private String getDebitSpeakingMessage(String xxx) {
        switch ((String) spinner.getSelectedItem()) {
            case "Custom English":
                return MessagesStrings.englishMessageForDebitTwo.replace("xxx", xxx);
            case "Hindi":
                return MessagesStrings.hindiMessageForDebitTwo.replace("xxx", xxx);
            case "Telugu":
                return MessagesStrings.teluguMessageForDebitTwo.replace("xxx", xxx);
            default:
                return MessagesStrings.kannadaMessageForDebitTwo.replace("xxx", xxx);
        }
    }


    private String getCreditSpeakingMessageWithAVL(String xxx, String yyy) {
        switch ((String) spinner.getSelectedItem()) {
            case "Custom English":
                return MessagesStrings.englishMessageForCreditOne.replace("xxx", xxx).replace("yyy", yyy);
            case "Hindi":
                return MessagesStrings.hindiMessageForCreditOne.replace("xxx", xxx).replace("yyy", yyy);
            case "Telugu":
                return MessagesStrings.teluguMessageForCreditOne.replace("xxx", xxx).replace("yyy", yyy);
            default:
                return MessagesStrings.kannadaMessageForCreditOne.replace("xxx", xxx).replace("yyy", yyy);
        }
    }

    private String getCreditSpeakingMessage(String xxx) {
        switch ((String) spinner.getSelectedItem()) {
            case "Custom English":
                return MessagesStrings.englishMessageForCreditTwo.replace("xxx", xxx);
            case "Hindi":
                return MessagesStrings.hindiMessageForCreditTwo.replace("xxx", xxx);
            case "Telugu":
                return MessagesStrings.teluguMessageForCreditTwo.replace("xxx", xxx);
            default:
                return MessagesStrings.kannadaMessageForCreditTwo.replace("xxx", xxx);
        }
    }


    private String[] messagesArray = new String[]{
            "INR 53000.00 is credited to your A/c XXXX6096 on 26-02-2019 on account" +
                    " of CASH DEPOSIT AT 691 BY SELF.Combined Available Balance is INR 53001.77. Chq" +
                    " deposit subject to clearance.",
            "Your a/c no. XXXX716096 is debited Rs.300.00 on 25-01-19 and a/c linked to virtual address" +
                    " bmqadir8@okicici is credited (UPI Ref no 902516565451).",
            "Thank you for using your Debit Card XX5767 for Rs.33.3 at 89050036 on 20-12-2018 23:52:51." +
                    " Available balance in A/C XX6096 is Rs.12342.92",
            "Cash withdrawal of Rs.200 made on Kotak Debit card XX5767 on 21-11-2018 23:28:33 at " +
                    "SCVDL342.Combined balance in A/c XX6096 is Rs. 12433.46.",
            "Dear SBI UPI User, your account is debited INR 40.0 on Date 2019-04-04 01:42:06 PM by UPI Ref No 909437902581.Download YONO ¡ www.yonosbi.com",
            "Dear SBI UPI User, your account is credited INR 13.00 on Date 2019-04-01 12:25:25 AM by UPI Ref No 909100760213",
            "Your A/c 101895 is debited with Rs 40.00 on 08-04-2019 13:45:43 A/c Bal is Rs 27998.49 Info: PUR/HOUSE OF FOOD/RANGAREDDY/0000000000000000000000000/Seq No 909813458750. Call 18605005555 (if in India) if you have not done this transaction.",
            "Hello! Your A/c no. 101895 has been debited by Rs. 7500 on 04Feb19. The A/c balance is Rs. 10500.00.Info: UPI/P2A/903413722301/50100216180912. Call 18605005555 (if in India) if you have not done this transaction.",
            "Your a/c 67724658 is credited Rs 2000 on 2016-12-28 A/c balance is Rs 5855.85 Info: NEFT/SBHY916363167451/Shri MOHD  JAMEEL",
            "Rs 200.00 debited from a/c **1998 on 10-04-19 to VPA darshansarje07@okhdfcbank(UPI Ref No 910010019239). Not you? Call on 18002586161 to report",
            "Rs. 60.00 credited to a/c XXXXXX1998 on 03-04-19 by a/c linked to VPA 9423834093@ybl (UPI Ref No  909351768528).",
            "ALERT:You've withdrawn Rs.3000.00 via Debit Card xx4664 at +MD LINES TOLICHOWKI on 2019-04-09:10:35:42.Avl Bal Rs.224910.91.Not you?Call 18002586161.",
            "ALERT:You've spent Rs.92.50 via Debit Card xx4664 at Zaak Epayment Services on 2019-03-18:13:09:17.Avl Bal Rs.231608.91.Not you?Call 18002586161.",
            "ALERT:You've spent Rs.121.00 via Debit Card xx4664 at Zaak Epayment Services on 2019-03-08:14:45:08.Avl Bal Rs.232108.41.Not you?Call 18002586161.",
            "Rs 200.00 debited from a/c **1998 on 10-04-19 to VPA darshansarje07@okhdfcbank(UPI Ref No 910010019239). Not you? Call on 18002586161 to report",
            "Your a/c xxxx1639 has been credited by Rs. 1.50 on 08-APR-2019 by VISA-HPCL 0.75 310319 AC:227239 ARN:913355671547. A/c Bal is Rs. 24,338.39 CR and AVL Bal is Rs. 24,338.39",
            "Your A/c No xxxx1639 has been debited by Rs. 500.00 on 07-APR-2019 via SPCNF390/471287XXXXXX3452/909720002355. A/c No xxxx1639 Bal is Rs. 24,336.89 CR and AVL Bal is Rs. 24,336.89"


    };
}
