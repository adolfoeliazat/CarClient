package hakito.carclient;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hakito.carclient.views.BigSeekBar;

public class SeekBarsFragment extends BaseSteeringFragment {

    private static final double MAX_SEEK_BAR_VALUE = 100;

    BigSeekBar steeringSeekBar;
    BigSeekBar throttleSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seek_bars, container, false);

        throttleSeekBar = (BigSeekBar) view.findViewById(R.id.seekThrottle);
        steeringSeekBar = (BigSeekBar) view.findViewById(R.id.seekSteer);

        steeringSeekBar.setHorizontal(true);
        throttleSeekBar.setOnTouchListener(new SeekResetter((int) (MAX_SEEK_BAR_VALUE / 2)));
        steeringSeekBar.setOnTouchListener(new SeekResetter((int) (MAX_SEEK_BAR_VALUE / 2)));

        return view;
    }

    private double normalize(int value) {
        return value / MAX_SEEK_BAR_VALUE * 2 - 1;
    }

    @Override
    double getSteer() {
        return normalize(steeringSeekBar.getProgress());
    }

    @Override
    double getThrottle() {
        return normalize(throttleSeekBar.getProgress());
    }
}
