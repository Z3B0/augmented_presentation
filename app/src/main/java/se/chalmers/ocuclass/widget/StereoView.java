package se.chalmers.ocuclass.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by richard on 14/10/15.
 */
public class StereoView extends FrameLayout {


    private static final String TAG = StereoView.class.getSimpleName();
    private int width;

    public StereoView(Context context) {
        super(context);
        init(context);
    }

    private String debugText = "SSS";

    private void init(Context context) {

        width = context.getResources().getDisplayMetrics().widthPixels;
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(context.getResources().getDisplayMetrics().scaledDensity*22);

        backgroundPaint.setColor(0x8A000000);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {






        //params.width = getWidth()/2;
        super.addView(child, index, params);
    }

    private Paint backgroundPaint = new Paint();
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        for(int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            //debugText = layoutParams.width+"";
                debugText = layoutParams.width+"";
                layoutParams.width = right/2;

                debugText = layoutParams.width+"";
            //}

            child.setLayoutParams(layoutParams);

        }



        super.onLayout(changed, left, top, right/2, bottom);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.drawRect(0,0,getWidth(),getHeight(),backgroundPaint);
        canvas.drawText(debugText,200,200,textPaint);

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
