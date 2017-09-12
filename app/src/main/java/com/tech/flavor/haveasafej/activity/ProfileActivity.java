package com.tech.flavor.haveasafej.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tech.flavor.haveasafej.R;
import com.tech.flavor.haveasafej.util.HsjConstant;
import com.tech.flavor.haveasafej.util.HsjHandler;
import com.tech.flavor.haveasafej.util.HsjValidator;

import java.util.HashMap;


public class ProfileActivity extends Activity {

    private static final String mTag = "HaveASafeJ";
    Boolean isProfileCreated = Boolean.FALSE;
    TextView tv_activity_header;
    Button mSubmitProfile;
    EditText mEtProfileName;
    EditText mEtProfileEmail;
    EditText mEtProfileEmEmail;
    EditText mEtProfileMobile;
    EditText mEtProfileEmMobile;
    EditText mEtProfileAddress;
    EditText mEtPassword;
    HashMap hmProfileData;
    Boolean isDataValid;
    Boolean isPasswordEnable= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout ll = (LinearLayout) findViewById(R.id.title_ll);
        HsjHandler.getTitle(ll, this, HsjConstant.APP_NAME, 20);

        Log.i(mTag, "In-side onCreate");

        mSubmitProfile = (Button)findViewById(R.id.btn_submit);
        mEtProfileName = (EditText)findViewById(R.id.et_name);
        mEtProfileEmail = (EditText)findViewById(R.id.et_mail);
        mEtPassword = (EditText)findViewById(R.id.et_newpassword);
        mEtProfileEmEmail = (EditText)findViewById(R.id.et_emeemail);
        mEtProfileMobile = (EditText)findViewById(R.id.et_mobile);
        mEtProfileEmMobile = (EditText)findViewById(R.id.et_ememobno);
        mEtProfileAddress = (EditText)findViewById(R.id.et_address);

        Bundle b = getIntent().getExtras();
        if ( b != null) {
            boolean isForgetPassword = b.getBoolean("FORGETPASSWORD");
            isPasswordEnable = true;
            if (isForgetPassword)
                mEtPassword.setVisibility(View.VISIBLE);
        }
        // Checking profile is already created, if not asking to fill the data.
        if ( (HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), this)) != null ) {
            isProfileCreated = true;
            mSubmitProfile.setText(R.string.updatebtntitle);
            HashMap hmProfileData = HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), ProfileActivity.this);

            mEtProfileName.setText(hmProfileData.get(HsjConstant.ProfileName).toString());
            mEtProfileEmail.setText(hmProfileData.get(HsjConstant.ProfileMail).toString());
            mEtProfileEmEmail.setText(hmProfileData.get(HsjConstant.ProfileEmMail).toString());
            mEtProfileMobile.setText(hmProfileData.get(HsjConstant.ProfileMobile).toString());
            mEtProfileEmMobile.setText(hmProfileData.get(HsjConstant.ProfileEmMobile).toString());
            mEtProfileAddress.setText(hmProfileData.get(HsjConstant.ProfileAddress).toString());

            HsjHandler.sendEmail(this,mEtProfileEmail.getText().toString(), "AlertMe profile updated","Thanks "+mEtProfileName.getText()+" for updating your profile.", false, null);
        }else{
            mSubmitProfile.setText(R.string.savebtntitle);
            HsjHandler.sendEmail(this, mEtProfileEmail.getText().toString(), "AlertMe profile created", "Thanks " + mEtProfileName.getText() + " for creating your profile.", false, null);
        }

        mEtProfileMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // pick it from contact
            }

        });

        mEtProfileEmMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pick it from contact

            }

        });


        mSubmitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(mTag, "onButton submit");
                if ( ! isProfileCreated) {
                    Log.i(mTag, "Updating your profile ");
                        setProfileData(false);
                } else {
                    Log.i(mTag, "Creating your profile ");
                    // Create a hash map
                    setProfileData(true);
                }
            }
        });
    }

    private void pickContect(String pEditType){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra("EditType", pEditType);

        startActivityForResult(intent, HsjConstant.PICK_CONTACT);
    }

    private void setProfileData( Boolean pIsNew ){
        hmProfileData = new HashMap();
        isDataValid = Boolean.FALSE;
        // Put elements to the map

        if (TextUtils.isEmpty(mEtProfileName.getText().toString())){
            mEtProfileName.setError("Name must not empty");
            return;
        }else{
            hmProfileData.put(HsjConstant.ProfileName, mEtProfileName.getText());
        }

        if ( isPasswordEnable ){
            if (TextUtils.isEmpty(mEtPassword.getText().toString())){
                mEtPassword.setError("Password must not empty");
                return;
            }else{
                if (mEtPassword.getText().toString().length() > 5 ) {
                    hmProfileData.put(HsjConstant.ProfilePass, mEtPassword.getText());
                }else{
                    mEtPassword.setError("Password is not valid, must greater than 5 char");
                    return;
                }
            }
        }

        if (TextUtils.isEmpty(mEtProfileEmail.getText().toString())){
            mEtProfileEmail.setError("Email must not empty");
            return;
        }else{
            if (HsjValidator.isValidEmail(mEtProfileEmail.getText())) {
                hmProfileData.put(HsjConstant.ProfileMail, mEtProfileEmail.getText());
            }else{
                mEtProfileEmail.setError("Email is not valid");
                return;
            }
        }

        if (TextUtils.isEmpty(mEtProfileEmEmail.getText().toString())){
            mEtProfileEmEmail.setError("Emergency Mail must not empty");
            return;
        }else{
            if (HsjValidator.isValidEmail( mEtProfileEmEmail.getText())) {
                hmProfileData.put(HsjConstant.ProfileEmMail, mEtProfileEmEmail.getText());
            }else {
                mEtProfileEmEmail.setError("Emergency Mail is not valid");
                return;
            }
        }

        if (TextUtils.isEmpty(mEtProfileMobile.getText().toString())){
            mEtProfileMobile.setError("Mobile number must not empty");
            return;
        }else{
            if (HsjValidator.isValidPhoneNumber(mEtProfileMobile.getText())) {
                hmProfileData.put(HsjConstant.ProfileMobile, mEtProfileMobile.getText());
            }else{
                mEtProfileMobile.setError("Mobile is not valid");
                return;
            }
        }

        if (TextUtils.isEmpty(mEtProfileEmMobile.getText().toString())){
            mEtProfileEmMobile.setError("Emergency mobile number not empty");
            return;
        }else{
            if ( HsjValidator.isValidPhoneNumber(mEtProfileEmMobile.getText())){
                hmProfileData.put(HsjConstant.ProfileEmMobile, mEtProfileEmMobile.getText());
            }else{
                mEtProfileEmMobile.setError("Emergency mobile number is not valid");
                return;
            }
        }

        if (TextUtils.isEmpty(mEtProfileAddress.getText().toString())){
            mEtProfileAddress.setError("Address must not empty");
            return;
        }else{

                hmProfileData.put(HsjConstant.ProfileAddress, mEtProfileAddress.getText());

        }

        HsjHandler.setSharedPreferences(ProfileActivity.class.getName(), hmProfileData, ProfileActivity.this, pIsNew);
        isDataValid = Boolean.TRUE;
        Log.i(mTag, "Profile data is set into.............");
        Log.i(mTag, "profile data is saved.");
        isProfileCreated = true;
        mSubmitProfile.setText(R.string.updatebtntitle);
        ProfileActivity.this.finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (HsjConstant.PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    String mobileno;
                    Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                    try {
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                        int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                        int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
                        String phoneNumber;

                        cursor.moveToFirst();
                        do {
//                            String name = cursor.getString(nameIdx);
//                            String idContact = cursor.getString(contactIdIdx);
                            phoneNumber = cursor.getString(phoneNumberIdx);

                        } while (cursor.moveToNext());

                        if ( data.getStringExtra("EditType").equalsIgnoreCase("EM")){
                            mEtProfileEmMobile.setText(phoneNumber);
                        }else{
                            mEtProfileMobile.setText(phoneNumber);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;
        }
    }
}