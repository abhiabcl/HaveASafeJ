package com.tech.flavor.haveasafej.activity;

/**
 * Created by etbdefi on 9/10/2017.
 */
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.tech.flavor.haveasafej.R;
import com.tech.flavor.haveasafej.util.HsjConstant;
import com.tech.flavor.haveasafej.util.HsjHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class LogInForm extends Activity {
    private static final String mTag = "HaveASafeJ";
    Button mLoginBtn;
    TextView mForgetBtn;
    TextView mSignUpBtn;

    EditText mUsername;
    EditText mPassowrd;
    ImageButton mProfilePic;
    private static int RESULT_LOAD_IMAGE = 1;
    private final int RESULT_CROP = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_design_login_form);

        mUsername = (EditText)findViewById(R.id.et_username);
        mPassowrd = (EditText)findViewById(R.id.et_password);

        mForgetBtn = (TextView)findViewById(R.id.tv_forget);
        mSignUpBtn = (TextView)findViewById(R.id.tv_signup);
        mLoginBtn = (Button)findViewById(R.id.btn_signin);
        mProfilePic = (ImageButton)findViewById(R.id.user_profile_photo);

        SharedPreferences prfs = getSharedPreferences("PROFILE_PIC", MODE_PRIVATE);

        if( prfs != null ){
            mProfilePic.setImageURI(Uri.parse(prfs.getString("PrfilePicFname", "@drawable/hsjlogo")));
        }

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);


            }
        });

        mForgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the login SharedPreferences and add reset data
                if ( (HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), LogInForm.this))!= null ) {
                    Intent pMainActivity = new Intent(getBaseContext(),
                            ProfileActivity.class);
                    pMainActivity.putExtra("FORGETPASSWORD", true);
                    startActivity(pMainActivity);
                    //finish();
                }else{
                    Toast.makeText(getBaseContext(), "No user found, Do signup!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup profile activity to set data and save
                if ( (HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), LogInForm.this)) == null ) {
                    Intent pMainActivity = new Intent(getBaseContext(),
                            ProfileActivity.class);
                    pMainActivity.putExtra("FORGETPASSWORD", true);
                    startActivity(pMainActivity);
                    //finish();
                } else{
                    Toast.makeText(getBaseContext(), "Username is already created!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get username/email and validate password
                if ( (HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), LogInForm.this)) != null ) {
                    HashMap hmProfileData = HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), LogInForm.this);
                    if ( (mUsername.getText().toString().equalsIgnoreCase(hmProfileData.get(HsjConstant.ProfileMail).toString()))&& (mPassowrd.getText().toString().equalsIgnoreCase(hmProfileData.get(HsjConstant.ProfilePass).toString()))){
                        Log.i(mTag, "login success");
                        Intent pDashboardActivity = new Intent(getBaseContext(),
                                DashboardActivity.class);
                        startActivity(pDashboardActivity);
                        finish();
                    }else{
                        Log.i(mTag, "login failed");
                        Toast.makeText(getBaseContext(), "Username/password is not valid!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            performCrop(picturePath);
            //mProfilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }

        if (requestCode == RESULT_CROP ) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");

                // Set The Bitmap Data To ImageView
                mProfilePic.setImageBitmap(selectedBitmap);
                mProfilePic.setScaleType(ImageView.ScaleType.FIT_XY);
                saveImage(selectedBitmap, "HaSjProfile");
            }
        }
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "/Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            //Save it for next time
            SharedPreferences.Editor editor = getSharedPreferences("PROFILE_PIC", MODE_PRIVATE).edit();
            editor.putString("PrfilePicFname", root + fname);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
