package com.example.androidthings.myproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.nio.charset.Charset;

import static com.example.androidthings.myproject.R.layout;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Strategy.TTL_SECONDS_INFINITE).build();

    private Gson mGson;
    private MessageListener mMessageListener;
    private int mCurrentOccupancy;
    private int mMaximumOccupancy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(layout.activity_main);

        mGson = new Gson();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference()
                .child(RoomGlobals.BUILDING)
                .child(RoomGlobals.ROOM)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Room room = dataSnapshot.getValue(Room.class);
                                mCurrentOccupancy = Integer.parseInt(dataSnapshot.child("current_occupancy").getValue().toString());
                                mMaximumOccupancy = Integer.parseInt(dataSnapshot.child("maximum_occupancy").getValue().toString());

                                TextView mNumberOccupantsText = (TextView) findViewById(R.id.numberOccupants);
                                TextView mOccupantsTitle = (TextView) findViewById(R.id.occupants);

                                if(mCurrentOccupancy < mMaximumOccupancy) {
                                    String occupancyText = mCurrentOccupancy + "/" + mMaximumOccupancy;
                                    mOccupantsTitle.setText("Current Occupancy");
                                    mNumberOccupantsText.setText(occupancyText);
                                }
                                else{
                                    mNumberOccupantsText.setText(null);
                                    mOccupantsTitle.setText("Room is at Max Capacity");
                                }
                                Log.d(TAG, "Current occupancy: " + mCurrentOccupancy + "/" + mMaximumOccupancy);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.toString());
                            }
                        }
                );
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                if (mCurrentOccupancy < mMaximumOccupancy) {
                    Student newStudent = getStudentFromNearbyMessage(message);
                    database.getReference(RoomGlobals.BUILDING)
                            .child(RoomGlobals.ROOM)
                            .child("students")
                            .push()
                            .setValue(newStudent);
                    database.getReference()
                            .child(RoomGlobals.BUILDING)
                            .child(RoomGlobals.ROOM)
                            .setValue("maximum_occupancy", mMaximumOccupancy + 1);
                }

            }

            @Override
            public void onLost(final Message message) {
                
            }
        };

        buildGoogleApiClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            unsubscribe();
            mGoogleApiClient.disconnect();
        }
    }

    private Student getStudentFromNearbyMessage(Message message) {
        String nearbyMessageString = new String(message.getContent()).trim();
        return mGson.fromJson(
                (new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
                Student.class);
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        logAndShowSnackbar("Exception while connecting to Google Play services: " +
                connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {
        logAndShowSnackbar("Connection suspended. Error code: " + i);
        unsubscribe();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        subscribe();
    }

    /**
     * Subscribes to messages from nearby devices and updates the UI if the subscription either
     * fails or TTLs.
     */
    private void subscribe() {
        Log.i(TAG, "Subscribing");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "No longer subscribing");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                }).build();

        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Subscribed successfully.");
                        } else {
                            logAndShowSnackbar("Could not subscribe, status = " + status);
                        }
                    }
                });
    }

    /**
     * Stops subscribing to messages from nearby devices.
     */
    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    /**
     * Logs a message and shows a {@link Snackbar} using {@code text};
     *
     * @param text The text used in the Log message and the SnackBar.
     */
    private void logAndShowSnackbar(final String text) {
        Log.w(TAG, text);
    }
}
