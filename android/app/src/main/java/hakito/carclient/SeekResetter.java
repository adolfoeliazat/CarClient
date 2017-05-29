package hakito.carclient;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by Oleg on 06.06.2016.
 */
public class SeekResetter implements ProgressBar.OnTouchListener {

    int defaultProgress;

    public SeekResetter(int defaultProgress) {
        this.defaultProgress = defaultProgress;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ProgressBar pb = (ProgressBar)v;
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            pb.setProgress(defaultProgress);
            return true;
        }
        return false;

    }
}
