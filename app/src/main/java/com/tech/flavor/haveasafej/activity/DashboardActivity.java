package com.tech.flavor.haveasafej.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tech.flavor.haveasafej.R;
import com.tech.flavor.haveasafej.util.HsjConstant;
import com.tech.flavor.haveasafej.util.HsjHandler;
import com.tech.flavor.haveasafej.util.LocationAddress;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends Activity implements LocationListener {

    static int hour, min;
    private static File mFile;
    private static File mDir;
    private static Bitmap mBitmap;
    private static int TAKE_PHOTO_CODE = 0;
    private static int count = 0;
    LocationManager locationManager;
    String mprovider;
    HashMap mHmTripData;
    SimpleDateFormat format;
    java.sql.Time timeValue;
    private String mTag = "HaveASafeJ";
    private ImageButton mSetting;
    private EditText mCurLoc;
    private EditText mDropLoc;
    private EditText mEstTimeHH;
    private EditText mEstTimeMM;
    private Button mPickTime;
    private ImageButton mGetCurLoc;
    private ImageButton mStart;
    private ImageView mImgView;
    private TextView mTextViewHH;
//    private TextView mTextViewMM;
    private ToggleButton mIsMailAllowed;
    private ToggleButton mIsSmsAllowed;
    private Uri outputFileUri;
    private Boolean mTripStarted = false;
    private Boolean isPhotoClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Setting application name to the header
        LinearLayout ll = (LinearLayout) findViewById(R.id.title_ll);
        HsjHandler.getTitle(ll, this, HsjConstant.APP_NAME, 20);

        // Getting resources
        mSetting = (ImageButton) findViewById(R.id.setting);
        mImgView = (ImageView) findViewById(R.id.cam_image);
        mGetCurLoc = (ImageButton) findViewById(R.id.get_cur_loc);
        mCurLoc = (EditText) findViewById(R.id.current_loc);
        mCurLoc.setEnabled(false);

        mDropLoc = (EditText) findViewById(R.id.drop_loc);

        mEstTimeHH = (EditText) findViewById(R.id.ed_approx_time_hh);
        mEstTimeHH.setEnabled(false);
        mEstTimeMM = (EditText) findViewById(R.id.ed_approx_time_mm);
        mEstTimeMM.setEnabled(false);
        mPickTime = (Button) findViewById(R.id.btn_getAproxTime);
        mStart = (ImageButton) findViewById(R.id.start_btn);
        mIsMailAllowed = (ToggleButton) findViewById(R.id.t_btn_GnMailNotify);
        mIsSmsAllowed = (ToggleButton) findViewById(R.id.t_btn_GnSMSNotify);
        mTextViewHH = (TextView) findViewById(R.id.tv_approx_time_hh);
        //mTextViewMM = (TextView) findViewById(R.id.tv_approx_time_mm);

        outputFileUri = Uri.parse("android.resource://drawable/hsjlogo.png");

        try {
            //Checking for profile is created or not to save setting required data.
            if ((HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), this)) == null) {
                Log.i(mTag, "Profile not found, Going to create.");
                Intent pMainActivity = new Intent(getBaseContext(),
                        ProfileActivity.class);
                pMainActivity.putExtra("FORGETPASSWORD", false);
                startActivity(pMainActivity);
            }
            //Checking for trip is still going or not, to reset old data.
            if ((HsjHandler.getSharedPreferences(DashboardActivity.class.getName(), this)) == null) {
                Log.i(mTag, "No Prev trip found");
                isPhotoClicked = false;
            } else {
                HashMap hmDashboadData = HsjHandler.getSharedPreferences(DashboardActivity.class.getName(), this);
                String endtime = hmDashboadData.get(HsjConstant.TRIP_END_TIME).toString();
                if (HsjHandler.isTripEnd(endtime)) {
                    mTripStarted = true;
                    isPhotoClicked = true;
                    setImage(hmDashboadData.get(HsjConstant.TRIP_IMG_URI).toString());
                    mCurLoc.setText(hmDashboadData.get(HsjConstant.TRIP_PICKUP).toString());
                    mDropLoc.setText(hmDashboadData.get(HsjConstant.TRIP_DROP).toString());
                    mTextViewHH.setText("Trip End time: ");
                    mEstTimeHH.setText(hmDashboadData.get(HsjConstant.TRIP_END_TIME).toString());
                    //mTextViewMM.setVisibility(View.GONE);
                    mEstTimeMM.setVisibility(View.GONE);
                    mStart.setImageResource(R.drawable.stop);
                }
            }

        } catch (Exception ex) {
            Log.e(mTag, ex.getMessage());
        }


        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pMainActivity = new Intent(getBaseContext(),
                        ProfileActivity.class);
                startActivity(pMainActivity);
            }
        });

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/AlertMe/";
        Log.d(mTag, "Create dir for pict: " + dir);

        mDir = new File(dir);
        mDir.mkdirs();

        mImgView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isPhotoClicked = true;

                if (ContextCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                5);
                    }
                }

                // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
                count++;
                String file = dir + count + ".jpg";
                Log.d(mTag, "File: " + file);
                mFile = new File(file);
                try {
                    mFile.createNewFile();
                } catch (IOException e) {
                }

                outputFileUri = Uri.fromFile(mFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });

        mGetCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                55);
                    }
                } else {
                    //Toast.makeText(DashboardActivity.this, "permission issue while accessing.", Toast.LENGTH_LONG).show();
                    Log.i(mTag, "permission granted for access location");
                }

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                mprovider = locationManager.getBestProvider(criteria, false);
                if (mprovider != null && !mprovider.equals("")) {
                    Location location = locationManager.getLastKnownLocation(mprovider);
                    locationManager.requestLocationUpdates(mprovider, 15000, 1, DashboardActivity.this);

                    if (location != null)
                        onLocationChanged(location);
                    else
                        Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "No Location Provider enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(mTag, "date picker clicked: ");
                TimePickerDialog td = new TimePickerDialog(DashboardActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                try {
                                    String dtStart = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                                    format = new SimpleDateFormat("HH:MM");

                                    timeValue = new java.sql.Time(format.parse(dtStart).getTime());
                                    mEstTimeHH.setText(String.valueOf(hourOfDay));
                                    mEstTimeMM.setText(String.valueOf(minute));
                                    Log.d(mTag, "date picked HH:MM - " + hourOfDay + ":" + minute);
                                } catch (Exception ex) {
                                    Log.e(mTag, ex.getMessage().toString());
                                }
                            }
                        },
                        hour, min,
                        true
                );
                td.show();
            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEntryBeforeStart()) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            switch (choice) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialog.dismiss();
                                    break;
                                //                                case DialogInterface.BUTTON_NEGATIVE:
                                //                                    Toast.makeText(getApplicationContext(),"Enter current location",Toast.LENGTH_SHORT);
                                //                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Please enter valid data before start.")
                            .setPositiveButton("Ok", dialogClickListener).show();
                } else {
                    if (!mTripStarted) {
                        mTripStarted = true;
                        isPhotoClicked = true;
                        mStart.setImageResource(R.drawable.stop);
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        mHmTripData = new HashMap();
                        // Get and save all data into preferences, required to check in case of resume
                        mHmTripData.put(HsjConstant.TRIP_TITLE, mCurLoc.getText() + HsjHandler.getCurrentTime());
                        Log.i(mTag, " getCurrentTime: " + HsjHandler.getCurrentTime());
                        //Get image
                        if (outputFileUri == null)
                            mHmTripData.put(HsjConstant.TRIP_IMG_URI, "");
                        else {
                            mHmTripData.put(HsjConstant.TRIP_IMG_URI, outputFileUri.toString());
                            Log.i(mTag, " outputFileUri: " + outputFileUri.toString());
                        }
                        //Get pickup/current location
                        mHmTripData.put(HsjConstant.TRIP_PICKUP, mCurLoc.getText());
                        Log.i(mTag, "mCurLoc: " + mCurLoc.getText());
                        //Get drop location
                        mHmTripData.put(HsjConstant.TRIP_DROP, mDropLoc.getText());
                        Log.i(mTag, "mDropLoc: " + mDropLoc.getText());
                        //Get estimate time
                        // mHmTripData.put(HsjConstant.TRIP_EST_TIME, mEstTimeHH.getText()+ ":" +mEstTimeMM.getText());

                        Log.i(mTag, "Estimate Time: " + mEstTimeHH.getText() + ":" + mEstTimeMM.getText());
                        //save current/start time and end time
                        mHmTripData.put(HsjConstant.TRIP_START_TIME, HsjHandler.getCurrentTime());
                        mHmTripData.put(HsjConstant.TRIP_END_TIME, HsjHandler.getEndTime(Integer.parseInt(mEstTimeHH.getText().toString()), Integer.parseInt(mEstTimeMM.getText().toString())));

                        // saving data into preferences
                        HsjHandler.setSharedPreferences(mTag, mHmTripData, DashboardActivity.this, true);

                        HashMap hmProfileData = HsjHandler.getSharedPreferences(ProfileActivity.class.getName(), DashboardActivity.this);
                        String message = "Hi " + hmProfileData.get(HsjConstant.ProfileName) + ", \n \n Your trip has been stated From: \n" + mCurLoc.getText() + "\n To:\n " + mDropLoc.getText() + " \n on Date: " + HsjHandler.getCurrentTime() + ". \n\n Thanks \n Team HaSJ.";
                        //Check is sendMail enable, if yes!, get mail id from setting and send a mail
                        if (mIsMailAllowed.isChecked()) {
                            HsjHandler.sendEmail(DashboardActivity.this, hmProfileData.get(HsjConstant.ProfileEmMail).toString(), "Trip Started", message, true, outputFileUri.toString());
                        }
                        //Check is sendSMS enable, if yes!, get number from setting and send a sms
                        if (mIsSmsAllowed.isChecked()) {
                            HsjHandler.sendSMS(DashboardActivity.this, hmProfileData.get(HsjConstant.ProfileMobile).toString(), message);
                        }
                        //Start the timer
                        //Check if alarm is enable start alarm


                    } else {
                        mTripStarted = false;
                        HsjHandler.deleteSharedPreferences(DashboardActivity.class.getName(), DashboardActivity.this);

                        //Clear data from preferences and Input boxes
                        setImage("");
                        mCurLoc.setText("");
                        mDropLoc.setText("");
                        mEstTimeHH.setText("");
                        mStart.setImageResource(R.drawable.start);

                    }
                }
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                getApplicationContext(), new GeocoderHandler());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d(mTag, "Pic saved");
            setImage(mFile.getPath());
        }
    }

    public Bitmap LoadAndResizeBitmap(String fileName, int width, int height) {
        // First we get the the dimensions of the file on disk

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(fileName, options);

        // Next we calculate the ratio that we need to resize the image by
        // in order to fit the requested dimensions.
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int inSampleSize = 1;

        if (outHeight > height || outWidth > width) {
            inSampleSize = outWidth > outHeight
                    ? outHeight / height
                    : outWidth / width;
        }

        // Now we will load the image and have BitmapFactory resize it for us.
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap resizedBitmap = BitmapFactory.decodeFile(fileName, options);

        return resizedBitmap;
    }

    private void setImage(String filename) {
        Log.i(mTag, "set Image to the view: " + filename);
        int height = mImgView.getHeight();
        int width = mImgView.getWidth();
        mBitmap = LoadAndResizeBitmap(filename, width, height);
        if (mBitmap != null) {
            mImgView.setImageBitmap(mBitmap);
            mBitmap = null;
        }
    }

    private boolean validateEntryBeforeStart() {
        //Check image resource
        if (!isPhotoClicked && outputFileUri != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int choice) {
                    switch (choice) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Click to add image first.")
                    .setPositiveButton("Ok", dialogClickListener).show();
            //return false;
        }

        //Check current location
        if (mCurLoc.getText().equals("")) {
            mCurLoc.setError("Must enter pickup location");
            return false;
        }

        if (mDropLoc.getText().equals("")) {
            mDropLoc.setError("Must enter drop location");
            return false;
        }

        if (mEstTimeHH.getText().equals("")) {
            mEstTimeHH.setError("Must enter approx HH (Hours)");
            return false;
        }

        if (mEstTimeMM.getText().equals("")) {
            mEstTimeMM.setError("Must enter approx MM (Minutes)");
            return false;
        }
        //Check drop location
        //Check aprox HH & MM
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Log.i(mTag, "permission granted");
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Log.i(mTag, "permission not granted");
            }
        }

        if (requestCode == 55) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Log.i(mTag, "location permission granted");
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Log.i(mTag, "location permission not granted");
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            //tvAddress.setText(locationAddress);
            if (locationAddress.contains("Enter manually")){
                mCurLoc.setText(locationAddress);
                mCurLoc.setEnabled(true);
            }else {
                mCurLoc.setText(locationAddress);
            }
            Log.i(mTag, locationAddress);
        }
    }
}
