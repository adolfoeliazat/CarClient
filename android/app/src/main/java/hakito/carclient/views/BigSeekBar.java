package hakito.carclient.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ProgressBar;

/**
 * Created by Oleg on 13.06.2016.
 */
public class BigSeekBar extends ProgressBar {
    public interface OnProgressChangedListener {
        void changed();
    }

    private static final int size = 32, centerSize = 8;

    private static final int backColor = Color.parseColor("#E6E6E6"),
            color = Color.parseColor("#3F4051"),
            centerColor = Color.RED;

    private OnProgressChangedListener changedListener;

    Paint paint, centerPaint;

    private Rect rect, centerRect;

    private boolean horizontal;
    int progress;

    public BigSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public BigSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public BigSeekBar(Context context) {
        super(context);
        init();
    }

    public void setChangedListener(OnProgressChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    void init() {
        centerPaint = new Paint();
        centerPaint.setColor(centerColor);

        paint = new Paint();
        paint.setColor(color);

    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
        initRect();

    }

    void initRect() {
        if (horizontal) {
            rect = new Rect(-size / 2, 0, size / 2, getHeight());
        } else//vertical
        {
            rect = new Rect(0, getHeight() - size / 2, getWidth(), getHeight() + size / 2);
        }
        setProgress(getProgress());
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (horizontal) {
            centerRect = new Rect(getWidth() / 2 - centerSize / 2, 0, getWidth() / 2 + centerSize / 2, getHeight());
        } else {
            centerRect = new Rect(0, getHeight() / 2 - centerSize / 2, getWidth(), getHeight() / 2 + centerSize / 2);
        }
        initRect();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int p;
        if (horizontal) {
            p = (int) (getMax() * ((event.getX()) / getWidth()));
        } else {
            p = (int) (getMax() * ((getHeight() - event.getY()) / getHeight()));
        }
        setProgress(p);
        //Log.d("qaz", ""+p);
        return true;
    }

    @Override
    public synchronized int getProgress() {
        return progress;
    }

    @Override
    public synchronized void setProgress(int progress) {
        // super.setProgress(progress);

        if (progress < 0) progress = 0;
        if (progress > getMax()) progress = getMax();
        this.progress = progress;
        float v = (float) progress / getMax();

        if (rect != null) {
            if (horizontal) {
                rect.offsetTo((int) (v * getWidth() - size / 2), 0);
            } else {
                rect.offsetTo(0, getHeight() - (int) (v * getHeight() + size / 2));
            }

        }
        invalidate();
        if (changedListener != null) {
            changedListener.changed();
        }
        Log.d("qaz", "" + getProgress());
    }

    @Override
    public synchronized void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(indeterminate);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.drawColor(backColor);
        canvas.drawRect(centerRect, centerPaint);
        canvas.drawRect(rect, paint);

    }
}
