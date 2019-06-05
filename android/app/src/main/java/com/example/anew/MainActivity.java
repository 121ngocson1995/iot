package com.example.anew;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    DatabaseReference doorStatus = database.child("opened");
    DatabaseReference lockStatus = database.child("locked");
    DatabaseReference alarmStatus = database.child("ringingstatus");

    String locked = "";
    String alarm = "";
    String opened = "";

    TextView textView;
    Button lockBtn;
    Button disableAlarmBtn;

    ColorStateList oldTextViewColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        lockBtn = (Button) findViewById(R.id.button_lock);
        disableAlarmBtn = (Button) findViewById(R.id.button_disableAlarm);

        oldTextViewColor =  textView.getTextColors();

        FirebaseMessaging.getInstance().subscribeToTopic("Alarm").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d("subscription", "subscription failed");
                }
            }
        });

        doorStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                opened = dataSnapshot.getValue(String.class);

                if (alarm.equals("true") == false) {
                    if (opened.equals("true")) {
                        textView.setText("OPENED");
                        textView.setTextColor(Color.rgb(255,140,0));
                    } else {
                        textView.setText("CLOSED");
                        textView.setTextColor(Color.GREEN);
                    }
                }

                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("file", "Failed to read value.", error.toException());
            }
        });

        lockStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locked = dataSnapshot.getValue(String.class);

                if (locked.equals("true")) {
                    lockBtn.setText("Unlock the door");
                } else {
                    lockBtn.setText("Lock the door");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("file", "Failed to read value.", error.toException());
            }
        });

        alarmStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alarm = dataSnapshot.getValue(String.class);

                if (alarm.equals("true")) {
                    disableAlarmBtn.setEnabled(true);

                    textView.setText("BREACHED");
                    textView.setTextColor(Color.RED);

                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    textView.startAnimation(anim);
                } else {
                    disableAlarmBtn.setEnabled(false);

                    textView.clearAnimation();

                    textView.setText("Fetching status...");
                    textView.setTextColor(oldTextViewColor );
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

                    new CountDownTimer(3000, 3000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            // do nothing
                        }

                        @Override
                        public void onFinish() {
                            if (opened.equals("true")) {
                                textView.setText("OPENED");
                                textView.setTextColor(Color.rgb(255,140,0));
                            } else {
                                textView.setText("CLOSED");
                                textView.setTextColor(Color.GREEN);
                            }

                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("file", "Failed to read value.", error.toException());
            }
        });
    }

    public void toggleLock(View v) {
        if (locked.equals("true")) {
            lockStatus.setValue("false");
        } else {
            lockStatus.setValue("true");
        }
    }

    public void toggleAlarm(View v) {
        if (alarm.equals("true")) {
            alarmStatus.setValue("false");
        } else {
            alarmStatus.setValue("true");
        }
    }
}
