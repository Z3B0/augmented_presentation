package se.chalmers.agumentedexploration.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

import se.chalmers.agumentedexploration.R;


/**
 * Created by admin on 2015-09-01.
 */
public class ShadedImageView extends ImageView {
    private float ratio;
    private Paint paint;
    private LinearGradient gradient;

    public ShadedImageView(Context context) {
        super(context);
        init(context,null);
    }

    public ShadedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ShadedImageView,
                0, 0);

        ratio = a.getFloat(R.styleable.ShadedImageView_ratio, 0.7f);

        a.recycle();

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(gradient == null){
            gradient = new LinearGradient(0,0,0,getHeight(),Color.TRANSPARENT,Color.BLACK, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
        }



        canvas.drawPaint(paint);
    }
}
