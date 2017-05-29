package hakito.carclient.sensors;

import android.support.annotation.FloatRange;

public interface SensorProvider {
    @FloatRange(from = -1, to = 1)
    double getThrottle();

    @FloatRange(from = -1, to = 1)
    double getSteering();
}
