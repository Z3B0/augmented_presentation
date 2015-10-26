package se.chalmers.ocuclass.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;

/**
 * Created by richard on 14/10/15.
 */
public class InfoHudView extends View {


    private static final String TAG = InfoHudView.class.getSimpleName();
    private float dp;
    private float sp;


    private int textPadding;

    private String time = "";
    private String elapsedTime = "";
    private String presentationName = "";

    public void setTime(String time) {
        this.time = time;
        postInvalidate();
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
        postInvalidate();
    }

    public InfoHudView(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {

        setWillNotDraw(false);

        DisplayMetrics metrics =  context.getResources().getDisplayMetrics();

        dp = metrics.density;
        sp = metrics.scaledDensity;

        textPadding = (int) (2*dp);

        backgroundPaint.setColor(0x8A000000);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(12 * sp);




    }



    @Override
    protected void onDraw(Canvas canvas) {

        render(canvas);
        canvas.save();
        canvas.translate(getWidth() / 2, 0);
        render(canvas);
        canvas.restore();

        super.onDraw(canvas);
    }

    private void render(Canvas canvas) {
        drawTextWithBg(canvas, time, Gravity.LEFT);
        drawTextWithBg(canvas, elapsedTime, Gravity.RIGHT);
        drawTextWithBg(canvas, presentationName, Gravity.CENTER);
    }

    private void drawTextWithBg(Canvas canvas, String text, int gravity) {

        int width = textPadding+(int)textPaint.measureText(text)+textPadding;

        int left = 0;
        int right = 0;

        switch (gravity){
            case Gravity.LEFT:
                left = getPaddingLeft();
                right = width+getPaddingLeft();
                break;
            case Gravity.RIGHT:
                left = getWidth()/2-width-getPaddingRight();
                right = getWidth()/2-getPaddingRight();
                break;
            case Gravity.CENTER:
                left = getWidth()/4-(width/2);
                right = left+width;
        }

        canvas.drawRect(left,getPaddingTop(),right,((int)textPaint.getTextSize())+(textPadding*2)+getPaddingTop(),backgroundPaint);

        canvas.drawText(text,left+textPadding,getPaddingTop()+(((textPadding*2+textPaint.getTextSize())/2)-((textPaint.descent() + textPaint.ascent()) / 2)),textPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

// Set the dimension to the smaller of the 2 measures
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


// Create the new specs
        int widthSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, heightMode);

// Call the super onMeasure
        super.onMeasure(widthSpec, heightSpec);
    }

    private Paint backgroundPaint = new Paint();
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public InfoHudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InfoHudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoHudView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

}
