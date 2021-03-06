package se.chalmers.ocuclass.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.BaseResponse;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.ui.fragment.OptionsDialogFragment;
import se.chalmers.ocuclass.ui.fragment.ProgressDialogFragment;
import se.chalmers.ocuclass.util.Utils;

/**
 * Created by richard on 08/10/15.
 */
public class NfcDetectActivity extends RxAppCompatActivity implements OptionsDialogFragment.Callback {

    private static final String EXTRA_USER = "extra_user";
    private static final String TAG = NfcDetectActivity.class.getSimpleName();
    /*private static final String CARD_ID_ONE = "404FCDF7"; //Richards kort
    private static final String CARD_ID_TWO = "F6D33280"; //paks kort*/
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
    private static final String DIALOG_TAG_OPTIONS_DIALOG = "dialog_tag_options_dialog";
    private static final String PREF_KEY_PERMISSIONS_DIALOG_SHOWN = "pref_key_permissions_dialog_shown";
    private static final int REQ_PERMISSIONS = 99;
    private static final String PREF_KEY_PERMISSIONS_RESULT_COUNT = "pref_key_permissions_result_count";

    private ArrayMap<String,String> deviceOptions = new ArrayMap<>(5);



    private User user;
    private TextView txtUsername;
    private Presentation presentation;
    private OptionsDialogFragment optionsDialogFragment;
    private SharedPreferences prefs;

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


        prefs = PreferenceManager.getDefaultSharedPreferences(this);





        deviceOptions.put(getString(R.string.device_cat), UnityActivity.MARKER_CAT);
        deviceOptions.put(getString(R.string.device_dog), UnityActivity.MARKER_DOG);
        deviceOptions.put(getString(R.string.device_snail), UnityActivity.MARKER_SNAIL);

        optionsDialogFragment = OptionsDialogFragment.newInstance(deviceOptions.keySet().toArray(new String[]{}));

        txtUsername = (TextView) findViewById(R.id.txt_username);

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);
        presentation = (Presentation) getIntent().getExtras().getSerializable(EXTRA_PRESENTATION);


        txtUsername.setText(getString(R.string.welcome) + "\n" + user.getName());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(user.getName());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.nfc);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_log_out:
                        RestClient.getInstance().setUser(null, null);
                        finish();
                        break;
                    case R.id.action_join:
                        optionsDialogFragment.show(getSupportFragmentManager(), DIALOG_TAG_OPTIONS_DIALOG);
                        break;

                }
                return false;
            }
        });

        requestPermissionsInit();


    }

    private void requestPermissionsInit() {
        requestPermissions(Manifest.permission.CAMERA);
    }

    private void requestPermissions(final String... permissions) {

        boolean permissionDenied = false;
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                permissionDenied = true;
                break;
            }
        }

        if(permissionDenied){


            final boolean dialogShown = prefs.getBoolean(PREF_KEY_PERMISSIONS_DIALOG_SHOWN,false);
            String text = dialogShown?getString(R.string.change_permissions):getString(R.string.accept_pemissions);
            new AlertDialog.Builder(
                    this).setTitle(getString(R.string.permissions)).setMessage(text).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                    if(dialogShown){
                        Utils.showInstalledAppDetails(NfcDetectActivity.this);
                    }else{
                        ActivityCompat.requestPermissions(NfcDetectActivity.this,
                                permissions,
                                REQ_PERMISSIONS);
                    }

                    prefs.edit().putBoolean(PREF_KEY_PERMISSIONS_DIALOG_SHOWN, true).commit();

                }
            }).create().show();

        } else{
            hasAllPermissions();
        }
    }

    private void hasAllPermissions() {

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


            startPresentation(tag);


            //


        }
    }

    private void startPresentation(String tag) {





        RestClient.service().presentationJoin(presentation.getId(), user.getId(), tag).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<BaseResponse>bindToLifecycle()).subscribe(new Action1<BaseResponse>() {


            @Override
            public void call(BaseResponse joinResponse) {
                UnityActivity.startActivity(NfcDetectActivity.this, user, presentation);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(NfcDetectActivity.this, "Unable to join, try again!", Toast.LENGTH_LONG).show();
            }
        });
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        int count = prefs.getInt(PREF_KEY_PERMISSIONS_RESULT_COUNT,0);
        count++;

        prefs.edit().putInt(PREF_KEY_PERMISSIONS_RESULT_COUNT,count);




        requestPermissionsInit();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {




        if(requestCode == Utils.REQ_CODE_SETTINGS){
            requestPermissionsInit();
        }
    }

    @Override
    public void onOptionSelected(int index) {
        startPresentation(deviceOptions.get(deviceOptions.keyAt(index)));
    }

}
