package se.chalmers.ocuclass.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.ui.UnityActivity;

/**
 * Created by richard on 14/10/15.
 */
public class ConnectedUserHudView extends View {




    private static final String TAG = ConnectedUserHudView.class.getSimpleName();

    private static final String[] FB_IDS = new String[]{"758885031","760789472","538773215","557448129","64344580964"};


    private float dp;
    private float sp;

    private List<User> connectedUsers;



    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap deviceCat;
    private Bitmap deviceDog;
    private Bitmap deviceSnail;

    private BitmapFactory.Options options = new BitmapFactory.Options();
    private Paint testPaint = new Paint();


    private ArrayMap<String,Bitmap> bitmaps = new ArrayMap<>(10);
    private ArrayMap<String,ValueAnimator> bitmapAnimations =  new ArrayMap<>(10);
    private boolean loading = true;
    private boolean error = false;
    private String loadingText;
    private String errorText;


    public void setConnectedUsers(List<User> connectedUsers) {
        this.connectedUsers = connectedUsers;
        loading = false;

        for(final User user : connectedUsers){



            if(user.getAccountType().equals("facebook")|| true){

                String fbId = fbId();

                Picasso.with(getContext()).load("http://graph.facebook.com/"+fbId+"/picture?type=square").into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmaps.put(user.getId(), bitmap);

                        ValueAnimator animator = ObjectAnimator.ofInt(0, 255);
                        animator.setDuration(400);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.start();

                        bitmapAnimations.put(user.getId(), animator);
                        postInvalidate();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }

        }

        postInvalidate();
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

    private String fbId() {

        Random rn = new Random();
        int index = rn.nextInt(FB_IDS.length);

        return FB_IDS[index];

    }

    public ConnectedUserHudView(Context context) {
        super(context);
        init(context);
    }

    public ConnectedUserHudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ConnectedUserHudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ConnectedUserHudView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {

        setWillNotDraw(false);

        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        loadingText = context.getString(R.string.loading);
        errorText = context.getString(R.string.err_connected_user);

        //options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //options.inDither = false;

        testPaint.setColor(Color.CYAN);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        dp = metrics.density;
        sp = metrics.scaledDensity;

        deviceCat = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_device_cat, options);
        deviceDog = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_device_dog,options);
        deviceSnail = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_device_snail,options);




        solidPaint.setColor(Color.BLACK);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(8 * sp);

    }

    /*
    RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
    clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
    canvas.clipPath(clipPath);*/

    /*private Bitmap getRoundBitmap(Context context, int resource) {

        return getCroppedBitmap(BitmapFactory.decodeResource(context.getResources(),resource,options), (int)(10*dp));
    }*/


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

        if(!loading) {

            int index = 0;
            for (User user : connectedUsers) {
                renderUser(canvas, user, index);
                index++;
            }

        }else {

            int width = (int) textPaint.measureText(loadingText);

            canvas.drawText(error?errorText:loadingText,getWidth()/4-width/2,getHeight()/2,textPaint);
        }

    }

    private void renderUser(Canvas canvas, User user, int index) {

        int offset = (int) ((28 * dp) * index);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int radius = (int) (10 * dp);

        //canvas.drawRect(paddingLeft,offset+paddingTop,200,offset+paddingTop+2,textPaint);

        Bitmap icon = deviceCat;
        if(user.getDeviceId().equals(UnityActivity.MARKER_DOG)){
            icon = deviceDog;
        }
        if(user.getDeviceId().equals(UnityActivity.MARKER_SNAIL)){
            icon = deviceSnail;
        }

        drawRoundedBitmap(canvas, icon, (int)((8 * dp) + paddingLeft + radius), (int)((4 * dp) + radius + paddingTop + offset), radius,255);

        float right = getWidth() / 2 - (8 * dp) - getPaddingRight();
        float top = (4 * dp) + paddingTop + offset;





        canvas.drawText(user.getName(), paddingLeft + (dp * 36), paddingTop + offset + (28 * dp / 2) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);

        Bitmap userBitmap = bitmaps.get(user.getId());
        if(userBitmap!=null&&bitmapAnimations.get(user.getId()).getAnimatedFraction()<1){

            drawRoundedBitmap(canvas, userBitmap, (int) (right - radius), (int) (top + radius), radius, (int) bitmapAnimations.get(user.getId()).getAnimatedValue());
            //postInvalidateOnAnimation();
        }



    }

    private static void drawRoundedBitmap(Canvas canvas, Bitmap bmp, int cx, int cy, int radius, int alpha) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius*2 || bmp.getHeight() != radius*2) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / (radius*2);
            sbmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() / factor), (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        final Paint paint = new Paint();
        final Rect rect = new Rect(cx-radius, cy-radius, cx+radius, cy+radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        //paint.setAlpha(255);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx ,
                cy, radius , paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp,new Rect(0,0,sbmp.getWidth(),sbmp.getHeight()),rect,paint);


    }

    public void error() {
        this.error = true;
    }
}
