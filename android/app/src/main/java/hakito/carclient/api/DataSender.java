package hakito.carclient.api;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import hakito.carclient.sensors.SensorProvider;

public class DataSender extends AsyncTask<Void, Void, Void> {

    TextView debugView;
    int interval = 100;
    private SensorProvider sensorProvider;
    private int led;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private SensorsCallback sensorsCallback;

    public DataSender(String address, SensorProvider sensorProvider, SensorsCallback sensorsCallback) throws IOException {
        this.sensorProvider = sensorProvider;
        this.sensorsCallback = sensorsCallback;
        String[] tokens = address.split(":");
        String host = tokens[0];
        int port = Integer.valueOf(tokens[1]);

        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        socket.setTrafficClass(0x10);
        socket.setPerformancePreferences(-1, 1, 1);
        socket.setSoTimeout(10);

        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    public int getLed() {
        return led;
    }

    public void setLed(int led) {
        this.led = led;
    }

    public void stop() throws IOException {
        socket.close();
        socket = null;
    }

    private void debug(final String s) {
        if (debugView != null) {
            debugView.post(new Runnable() {
                @Override
                public void run() {
                    debugView.setText(s);
                }
            });
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setDebugView(TextView debugView) {
        this.debugView = debugView;
    }

    private byte[] getCommand() {
        byte[] res = new byte[5];
        res[0] = '$';
        res[1] = (byte) sensorProvider.getThrottle();
        res[2] = (byte) sensorProvider.getSteering();
        res[3] = (byte) led;
        res[4] = (byte) (res[1] + res[2] + res[3]);
        return res;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            while (true) {
                if (socket == null) {
                    return null;
                }
                byte[] command = getCommand();
                String strCommand = Arrays.toString(command);
                Log.d("qaz", strCommand);
                outputStream.write(command);
                outputStream.flush();


                if (inputStream.available() > 0 && inputStream.read() == '$') {
                    sensorsCallback.onVoltageChanged(inputStream.read());
                }

                debug(strCommand);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            debug(e.getMessage());
        }
        return null;
    }

    public interface SensorsCallback {
        void onVoltageChanged(int voltage);
    }
}
