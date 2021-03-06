package com.example.projectteam23mobiledev;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectteam23mobiledev.Models.Challenge;
import com.example.projectteam23mobiledev.Models.RunModel;
import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;
import com.example.projectteam23mobiledev.ViewModels.BottomNavViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class RunFragment extends Fragment implements SensorEventListener, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private Chronometer chronometer;
    private int sec = 0;
    TextView steps;
    int stepCount = 0;
    double speed = 0;
    double elapsedHours = 0;
    long elapsedSeconds = 0;
    double distanceValue = 0;
    TextView calories;
    double caloriesValue = 0;
    long tStart;
    TextView distance;
    TextView time;
    TextView pace;
    boolean isRunning = true;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    SensorManager sensorManager;
    Sensor countSensor;
    Button stop;
    Button pause;
    FirebaseAuth mAuth;
    Handler hd;
    Challenge challenge;
    String currEmail;
    String challengeId;

    BottomNavViewModel bottomNavViewModel;
    private BottomNavigationView bottomNavigationView;

    public RunFragment(BottomNavViewModel bottomNavViewModel) {
        this.bottomNavViewModel = bottomNavViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View runView = inflater.inflate(R.layout.fragment_run, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            challengeId  = bundle.getString("challengeId", null);
            challenge  = (Challenge) bundle.getSerializable("challenge");
//            Toast.makeText(getContext(), challengeId, Toast.LENGTH_SHORT).show();
        } else {

            // no challenege is asscoiated
            challengeId = "0";
            challenge = null;
        }

        bottomNavViewModel.setIsVisible(true);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Quit");
                alert.setMessage("Are you sure you want to stop the run?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onStop();
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        hd = new Handler();
        mAuth = FirebaseAuth.getInstance();
        currEmail  = mAuth.getCurrentUser().getEmail();
        steps = (TextView) runView.findViewById(R.id.steps_value);
        distance = (TextView) runView.findViewById(R.id.distance_value);
        time = (TextView) runView.findViewById(R.id.time_value);
        pace = (TextView) runView.findViewById(R.id.pace_value);
        calories = (TextView) runView.findViewById(R.id.calories_value);
        sensorManager = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        tStart = System.currentTimeMillis();
        startTimer();
        fetchLocation();

        pause = (Button) runView.findViewById(R.id.btn_pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning) {
                    //pauseTimer();
                    onPause();
                }

                else
                    onResume();
            }
        });

        stop = (Button) runView.findViewById(R.id.btn_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStop();
            }
        });
        return runView;

    }

    public void stuff() {

    }

    public void openNewActivity(RunModel runModel){
        Intent intent = new Intent(getActivity(), RunStatistics.class);
        intent.putExtra("run", runModel);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        isRunning = true;
        super.onResume();

        sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {

        super.onStop();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date d = new Date();
        d.getTime();



        RunModel run = new RunModel(currEmail, distanceValue,
                stepCount, speed, sec, challengeId ,caloriesValue);

        // get run model of the other user
        final RunModel[] opp_run = new RunModel[1];

        Query q = db.collection("runstats")
                .whereEqualTo("challengeId", challengeId);
//                        .whereNotEqualTo("user", currEmail);

//                q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                        opp_run[0] = (RunModel) document.toObject(RunModel.class);
//
//                                Log.d(TAG," user: "+(String) document.getData().get("user"));
//
//                                String usr = (String) document.getData().get("user");
//
//                                if (!usr.equals(currEmail)) {
//                                    // the other user exists
//                                }
//                            }
//                            Log.d(TAG," Bingo");
//                        } else {
//                            Log.d(TAG, "the query doesn't work");
//                        }
//                    }
//                });


        if(challengeId == "0") {
            // if no challenge and just a normal
            db.collection("runstats")
                    .add(run)
                    .addOnSuccessListener(documentReference -> {
//

                        run.setId(documentReference.getId());

                        Fragment fragment = new RunStatsFragment(bottomNavViewModel);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("runStats", run);
                        fragment.setArguments(bundle);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    })
                    .addOnFailureListener(exception -> {
//                                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {

            // there is a challenge
            db.collection("runstats")
                    .whereEqualTo("challengeId", challengeId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean isEmptyDoc = true;
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String usr = (String) document.getData().get("user");
                                    isEmptyDoc = false;

                                    if (!usr.equals(currEmail)) {
                                        // the other user exists
                                        opp_run[0] = (RunModel) document.toObject(RunModel.class);

                                        if (opp_run[0] != null && challenge != null) {

                                            // compare run models
                                            boolean res = false;
                                            if (challenge.getType().equals("distance")) {

                                                if (opp_run[0].getSeconds() > run.getSeconds()) {
                                                    res = true;
                                                }
                                            } else {
                                                if (opp_run[0].getDistance() < run.getDistance()) {
                                                    res = true;
                                                }
                                            }

                                            String vicEmail = currEmail;

                                            // add result in challenge
                                            if (res) {
                                                // he won
                                                if (challenge.getSender().equals(currEmail)) {
                                                    challenge.setStatus("snd");
                                                    challenge.setSendStatus(StatusEnum.COMPLETED);
                                                } else {
                                                    challenge.setStatus("rcv");
                                                    challenge.setReceiverStatus(StatusEnum.COMPLETED);
                                                }

                                            } else {
                                                // he lost
                                                if (challenge.getSender().equals(currEmail)) {
                                                    challenge.setStatus("rcv");
                                                    vicEmail = challenge.getReceiver();
                                                    challenge.setSendStatus(StatusEnum.COMPLETED);

                                                } else {

                                                    challenge.setStatus("snd");
                                                    vicEmail = challenge.getSender();
                                                    challenge.setReceiverStatus(StatusEnum.COMPLETED);
                                                }
                                            }

                                            int vicPoints = challenge.getTotalCredit();
                                            challenge.setTotalCredit(0);

                                            // add money to victor in wallet
                                            final String[] usrDocId = {""};
                                            final long[] bal = new long[1];
                                            db.collection("users")
                                                    .whereEqualTo("email", vicEmail)
                                                    .limit(1)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    usrDocId[0] = document.getId();
                                                                    bal[0] = (long) document.getData().get("wallet");

                                                                    if (usrDocId[0].equals("")) {
                                                                        // didn;t get the user return
                                                                        return;
                                                                    } else {
                                                                        Long newBal = bal[0] + vicPoints;

                                                                        //add money to user db wallet
                                                                        db.collection("users")
                                                                                .document(usrDocId[0])
                                                                                .update(
                                                                                        "wallet", newBal
                                                                                )
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                        // now update the challenge
                                                                                        ObjectMapper oMapper = new ObjectMapper();
                                                                                        Map<String, Object> map = oMapper.convertValue(challenge, Map.class);
                                                                                        db.collection("challenges")
                                                                                                .document(challengeId)
                                                                                                .update(map)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void unused) {

                                                                                                        db.collection("runstats")
                                                                                                                .add(run)
                                                                                                                .addOnSuccessListener(documentReference -> {
                                                                                                                    Log.d(TAG, "run here in 410: " + run.toString());
                                                                                                                    run.setId(documentReference.getId());

                                                                                                                    Fragment fragment = new RunStatsFragment(bottomNavViewModel);

                                                                                                                    Bundle bundle = new Bundle();
                                                                                                                    bundle.putSerializable("runStats", run);
                                                                                                                    fragment.setArguments(bundle);

                                                                                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                                                                    fragmentTransaction.replace(R.id.container, fragment);
                                                                                                                    fragmentTransaction.addToBackStack(null);
                                                                                                                    fragmentTransaction.commit();

                                                                                                                })
                                                                                                                .addOnFailureListener(exception -> {
//                                                                                                                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                });

                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });
                                                                    }
                                                                    break;
                                                                }
                                                            } else {
                                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                                            }
                                                        }
                                                    });


                                        }
                                    }
                                }

                                if (isEmptyDoc) {
                                    // if no opposite runner but its a challenge
                                    if (opp_run[0] == null && challenge!=null) {
                                        if (currEmail.equals(challenge.getSender())) {
                                            challenge.setSendStatus(StatusEnum.COMPLETED);
                                        } else {
                                            challenge.setReceiverStatus(StatusEnum.COMPLETED);
                                        }

                                        ObjectMapper oMapper = new ObjectMapper();
                                        // object -> Map
                                        Map<String, Object> map = oMapper.convertValue(challenge, Map.class);
                                        db.collection("challenges")
                                                .document(challengeId)
                                                .update(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        db.collection("runstats")
                                                                .add(run)
                                                                .addOnSuccessListener(documentReference -> {

                                                                    Log.d(TAG, "run here in 470: distance is " + run.getDistance() + ", time is " + run.getSeconds() + ", calories is " + run.getCalories() + ", speed is " + run.getSpeed() + ", steps is " + run.getSteps());
                                                                    run.setId(documentReference.getId());

                                                                    Fragment fragment = new RunStatsFragment(bottomNavViewModel);

                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putSerializable("runStats", run);
                                                                    fragment.setArguments(bundle);

                                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                    fragmentTransaction.replace(R.id.container, fragment);
                                                                    fragmentTransaction.addToBackStack(null);
                                                                    fragmentTransaction.commit();

                                                                })
                                                                .addOnFailureListener(exception -> {
//                                                                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                                });
                                                    }
                                                });
                                    }
                                }
                            } else {
                                Log.d(TAG, "empty runner : "+opp_run[0]);





                            }
                        }
                    });
        }

        isRunning = false;
        sensorManager.unregisterListener(this, countSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(isRunning) {

            Sensor sensor = sensorEvent.sensor;
            float[] values = sensorEvent.values;
            int value = -1;

            if (values.length > 0) {
                value = (int) values[0];
            }

            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                stepCount++;
            }
            //calculate Distance
            distanceValue = (float)(stepCount * 2.2) / (float)5280;
            String roundOffDistance = String.format("%.2f", distanceValue);
            distance.setText(String.valueOf(roundOffDistance));

            if (challenge != null) {
                if ("distance".equals(challenge.getType())) {
                    if (Integer.valueOf(roundOffDistance).equals(challenge.getDistance())) {
                        onStop();
                    }
                }
            }

            //calculate steps
            steps.setText(String.valueOf(stepCount));

            //calculate pace
            speed = (elapsedHours != 0) ? (distanceValue/elapsedHours) : 0;
            String roundSpeed = String.format("%.2f", speed);
            pace.setText(String.valueOf(roundSpeed));

            //calculate calories
            caloriesValue = 0.05 * stepCount;
            String roundCalories = String.format("%.2f", caloriesValue);
            calories.setText(roundCalories);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(RunFragment.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: "+location.getLatitude()+" "+ location.getLongitude());
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(50));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        PolylineOptions polyLine = new PolylineOptions();

        polyLine.visible(true);
        Log.d(TAG, "startCap:" + polyLine.getStartCap());
        polyLine.add(latLng);
        polyLine.width(10f);
        polyLine.color(Color.BLUE);
        polyLine.geodesic(true);
        mMap.addPolyline(polyLine);

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onConnected: ");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    private void pauseTimer() {
//        hd.removeCallbacks(runnable);
//        reset.setEnabled(true);
//    }

    private void startTimer()
    {

            hd.post(new Runnable() {
                @Override

                public void run()
                {

                    if (isRunning)
                    {
                        sec++;

                        //calculate time
                        long tEnd = System.currentTimeMillis();
                        long tDelta = tEnd - tStart;
                        elapsedSeconds = (tDelta / 1000);
                        elapsedHours = (elapsedSeconds/(60.0 * 60.0));

                        //stop run after challenge
                        if (challenge != null) {
                            if ("time".equals(challenge.getType())) {
                                if (challenge.getTime() * 60 == sec) {

                                    onStop();
                                }
                            }
                        }
                    }

                    int hours_var = sec / 3600;
                    int minutes_var = (sec % 3600) / 60;
                    int secs_var = sec % 60;

                    String time_value = String.format(Locale.getDefault(),
                            "%d:%02d:%02d", hours_var, minutes_var, secs_var);

                    time.setText(time_value);

                    hd.postDelayed(this, 1000);
                }
            });


    }
}