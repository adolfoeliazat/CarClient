package hakito.carclient;

import android.support.v4.app.Fragment;

import hakito.carclient.entity.SteeringData;

public abstract class BaseSteeringFragment extends Fragment {
    abstract double getSteer();
    abstract double getThrottle();
}
