package com.bnaze.smartmouse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bnaze.smartmouse.networkutils.ConnectionCondition;
import com.bnaze.smartmouse.networkutils.Connector;
import com.bnaze.smartmouse.networkutils.Message;
import com.bnaze.smartmouse.networkutils.MessageQueue;
import com.bnaze.smartmouse.networkutils.MessageSender;
import com.bnaze.smartmouse.networkutils.MessageType;
import com.bnaze.smartmouse.networkutils.Settings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener, FragmentListener, ConnectionCondition {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private AirMouseFragment airMouseFragment;
    private AccelerometerMouseFragment accelerometerMouseFragment;
    private TouchScreenMouseFragment touchScreenMouseFragment;

    private EditText hostEditText;
    private EditText portEditText;

    private boolean connected;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        MessageSender.getInstance().init();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        Connector.getInstance().addConnectionCondition(this);

        airMouseFragment = new AirMouseFragment();
        touchScreenMouseFragment = new TouchScreenMouseFragment();
        accelerometerMouseFragment = new AccelerometerMouseFragment();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(airMouseFragment,"Air");
        viewPagerAdapter.addFragment(touchScreenMouseFragment,"Touch");
        viewPagerAdapter.addFragment(accelerometerMouseFragment,"Accelero");

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_airmouse);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_touch_app);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_mouse);
    }

    public void connectionSettings(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        hostEditText = dialogView.findViewById(R.id.button_host);
        portEditText = dialogView.findViewById(R.id.button_port);

        if (Settings.getInstanceOf().getHost() != null && !Settings.getInstanceOf().getHost().isEmpty()) {
            hostEditText.setText(Settings.getInstanceOf().getHost());
        }
        if (Settings.getInstanceOf().getPort() != 0) {
            portEditText.setText(String.format("%s", Settings.getInstanceOf().getPort()));
        }

        if (connected) {
            builder.setNegativeButton("Disconnect", this);
        } else {
            builder.setPositiveButton("Connect", this);
        }
        builder.setNeutralButton("Cancel", this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.show();
    }

    public void toggleKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (connected) {
            MessageQueue.getInstance().push(Message.newMessage(MessageType.KEYBOARD_INPUT, ReturnKey.key(event).toString()));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1: {
                // positive
                String host = hostEditText.getText().toString();
                String portString = portEditText.getText().toString();
                if (host.isEmpty() || portString.isEmpty()) {
                    return;
                }
                int port = Integer.parseInt(portString);
                Settings.getInstanceOf().setHost(host);
                Settings.getInstanceOf().setPort(port);

                //Attempting connection here
                Connector.getInstance().connect();
            }
            break;
            case -2: {
                // negative
                //If the user clicked disconnect, attempt to disconnect
                Connector.getInstance().disconnect();
            }
            break;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof AirMouseFragment) {
            AirMouseFragment mouseFragment = (AirMouseFragment) fragment;
            mouseFragment.setAirMouseSelectedListener(this);
        }

        if (fragment instanceof TouchScreenMouseFragment) {
            TouchScreenMouseFragment mouseFragment = (TouchScreenMouseFragment) fragment;
            mouseFragment.setTouchScreenMouseListener(this);
        }

        if (fragment instanceof AccelerometerMouseFragment) {
            AccelerometerMouseFragment mouseFragment = (AccelerometerMouseFragment) fragment;
            mouseFragment.setAccelerometerMouseListener(this);
        }
    }

    @Override
    public boolean ConnectedValue() {
        return connected;
    }

    @Override
    public void onConnected() {
        connected = true;
        final String msg = "Connection established with host machine";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDisconnected() {
        connected = false;
        final String msg = "Connection disrupted";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        connected = false;
        final String msg = "Could not establish connection with host machine";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        //Need this in order to set the titles for the tabs
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
