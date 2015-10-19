package se.chalmers.ocuclass.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.BaseResponse;
import se.chalmers.ocuclass.net.RestClient;

/**
 * Created by richard on 08/10/15.
 */
public class NfcDetectActivity extends RxAppCompatActivity {

    private static final String EXTRA_USER = "extra_user";
    private static final String TAG = NfcDetectActivity.class.getSimpleName();
    private static final String CARD_ID_ONE = "404FCDF7"; //Richards kort
    private static final String CARD_ID_TWO = "F6D33280"; //paks kort
    private static final String EXTRA_PRESENTATION = "extra_presentation";
    private static final String[][] NTC_TECH_LIST = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(),
                    Ndef.class.getName()
            }
    };



    private User user;
    private TextView txtUsername;
    private Presentation presentation;

    @Override
    protected void onResume() {

        super.onResume();


        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.NTC_TECH_LIST);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        txtUsername = (TextView) findViewById(R.id.txt_username);

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);
        presentation = (Presentation) getIntent().getExtras().getSerializable(EXTRA_PRESENTATION);


        txtUsername.setText(getString(R.string.welcome) + " "+user.getName());


    }



    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            String tag = byteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));

            Log.d(TAG,"======NFC TAG=====");
            Log.d(TAG,tag);
            Log.d(TAG,"==================");


            RestClient.service().presentationJoin(presentation.getId(), user.getUserId(), tag).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).compose(this.<BaseResponse>bindToLifecycle()).subscribe(new Action1<BaseResponse>() {


                @Override
                public void call(BaseResponse joinResponse) {
                    UnityActivity.startActivity(NfcDetectActivity.this, user,presentation);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(NfcDetectActivity.this, "Unable to join, try again!", Toast.LENGTH_LONG).show();
                }
            });





            //


        }
    }



    private String byteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    public static void startActivity(Context context, User user, Presentation presentation) {

        Intent intent = new Intent(context, NfcDetectActivity.class);
        intent.putExtra(EXTRA_USER,user);
        intent.putExtra(EXTRA_PRESENTATION, presentation);
        context.startActivity(intent);

    }
}
