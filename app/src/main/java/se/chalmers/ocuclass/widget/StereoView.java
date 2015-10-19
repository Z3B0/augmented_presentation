package se.chalmers.ocuclass.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by richard on 14/10/15.
 */
public class StereoView extends FrameLayout {


    private static final String TAG = StereoView.class.getSimpleName();

    public StereoView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        testPaint.setColor(Color.RED);
    }

    private Paint testPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right/2, bottom);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();
        canvas.translate(getWidth() / 2, 0);

        super.dispatchDraw(canvas);

        canvas.restore();
        super.dispatchDraw(canvas);

    }

    public StereoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StereoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StereoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
}
