package hakito.carclient;

import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import hakito.carclient.api.DataSender;
import hakito.carclient.sensors.SensorProvider;
import hakito.carclient.views.BigSeekBar;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity implements SensorProvider, DataSender.SensorsCallback {

    private static final int MAX_SEEK_BAR_VALUE = 100;

    @BindView(R.id.tWifiName)
    TextView wifi;
    @BindView(R.id.voltage_text)
    TextView voltageText;
    DataSender dataSender;
    SensorProvider sensorNormalizer;

    @BindView(R.id.tDebug)
    TextView debugText;

    @BindView(R.id.speedometer)
    CustomGauge speedometer;

    @BindView(R.id.speedometer_text)
    TextView speedomterText;

    @BindView(R.id.seekSteer)
    BigSeekBar steeringSeekBar;

    @BindView(R.id.seekThrottle)
    BigSeekBar throttleSeekBar;

    @BindView(R.id.brake_button)
    Button brateButton;
    private WifiManager wifiManager;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        steeringSeekBar.setHorizontal(true);
        throttleSeekBar.setOnTouchListener(new SeekResetter(MAX_SEEK_BAR_VALUE / 2));
        steeringSeekBar.setOnTouchListener(new SeekResetter(MAX_SEEK_BAR_VALUE / 2));

        preferenceHelper = new PreferenceHelper(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                wifi.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi.setText(wifiManager.getConnectionInfo().getSSID());
                    }
                });
            }
        }, 300, 5000);


        sensorNormalizer = new SensorNormalizer(this,
                new Normalizer(preferenceHelper.getLeft(), preferenceHelper.getRight()),
                new Normalizer(0, 255));
    }

    void showSpeed(double kmh) {
        speedometer.setValue((int) (kmh * 10));
        speedomterText.setText(String.format("%.1f\nkm/h", kmh));
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataSender = new DataSender(preferenceHelper.getAddress(), sensorNormalizer, MainActivity.this);
                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                dataSender.setDebugView(debugText);
                dataSender.setInterval(preferenceHelper.getInterval());
                dataSender.execute();
                dataSender.setLed(preferenceHelper.getLight());
            }
        }).start();
    }

    @Override
    protected void onPause() {
        if (dataSender != null) {
            try {
                dataSender.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mSettings:
                Intent intent = new Intent(this, PrefsActivity.class);
                startActivity(intent);
                break;
            case R.id.mAdditionalPanel:
                new AdditionalPanelDialog().show(getSupportFragmentManager(), "TAG");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private double normalize(int value) {
        return value / MAX_SEEK_BAR_VALUE * 2 - 1;
    }

    @Override
    public double getSteering() {
        return normalize(steeringSeekBar.getProgress());
    }

    @Override
    public double getThrottle() {
        return normalize(throttleSeekBar.getProgress());
    }

    @Override
    public boolean isBraking() {
        return brateButton.isPressed();
    }

    @Override
    public void onVoltageChanged(final int voltage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double value = 0.111 * voltage;
                voltageText.setText(String.format("%.2fV", value));
                if (value > 12.3) {
                    voltageText.setTextColor(Color.GREEN);
                } else if (value < 11.6) {
                    voltageText.setTextColor(Color.RED);
                } else {
                    voltageText.setTextColor(Color.GRAY);
                }
            }
        });
    }

    @Override
    public void onSpeedShanged(final int speed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSpeed(Math.max(speed, 100));
            }
        });

    }

    static class SensorNormalizer implements SensorProvider {

        private SensorProvider provider;
        private Normalizer steeringNormalizer;
        private Normalizer throttleNormalizer;

        public SensorNormalizer(SensorProvider provider, Normalizer steeringNormalizer, Normalizer throttleNormalizer) {
            this.provider = provider;
            this.steeringNormalizer = steeringNormalizer;
            this.throttleNormalizer = throttleNormalizer;
        }

        @Override
        public double getThrottle() {
            return throttleNormalizer.normalize(provider.getThrottle());
        }

        @Override
        public double getSteering() {
            return steeringNormalizer.normalize(provider.getSteering());
        }

        @Override
        public boolean isBraking() {
            return provider.isBraking();
        }
    }
}
