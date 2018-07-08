package com.here.iam.nagy.mohamed.imhere.ui.cutom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.here.iam.nagy.mohamed.imhere.R;

/**
 * Created by Mohamed Nagy on 7/1/2018 .
 * Project IM_Here
 * Time    2:07 PM
 */
public class CircleImage extends AppCompatImageView {

    private static final int DEFAULT_RADIUS = 40;
    private static final int DEFAULT_STROKE_COLOR = 0;
    private static final int DEFAULT_STROKE_WIDTH = 0;
    private static final int ERROR_FACTOR = 7;

    private Integer mRadius = null;
    private Integer mStrokeColor = null;
    private Integer mStrokeWidth = null;
    private Paint mPaint = null;

    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context, attrs);
    }

    public CircleImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mRadius*2 + mStrokeWidth*2 + ERROR_FACTOR * 2 , mRadius*2 + mStrokeWidth * 2 + ERROR_FACTOR * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if(drawable == null)
            return;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();


        // true: mutable pixels
        Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        setDefaults();

        Bitmap resizedBitmap = resizeBitmap(bitmapCopy);
        Bitmap circleBitmap = getCroppedBitmap(resizedBitmap);

        canvas.drawBitmap(circleBitmap, ERROR_FACTOR, ERROR_FACTOR, null);

        drawCircularImageViewBorder(canvas);
    }

    private void drawCircularImageViewBorder(Canvas canvas){

        mPaint.setStyle(Paint.Style.STROKE);


        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);

        canvas.drawCircle(mRadius + ERROR_FACTOR, mRadius + ERROR_FACTOR, mRadius , mPaint);
    }

    public Bitmap getCroppedBitmap(Bitmap resizedBitmap){
        int diameter = mRadius * 2;

        Bitmap circleBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);
        mPaint = new Paint();

        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(resizedBitmap, 0, 0, mPaint);

        return circleBitmap;
    }

    private void setDefaults(){
        if(mRadius == null)
            mRadius = DEFAULT_RADIUS;
        if(mStrokeWidth == null)
            mStrokeWidth = DEFAULT_STROKE_WIDTH;
        if(mStrokeColor == null)
            mStrokeColor = DEFAULT_STROKE_COLOR;
    }

    private Bitmap resizeBitmap(Bitmap bitmap){
         int height = bitmap.getHeight();
         int width = bitmap.getWidth();

         if(width == height)
             return resizeBitmapSquare(bitmap);
         else
             return resizeBitmapRectangle(bitmap);
    }

    private Bitmap resizeBitmapSquare(Bitmap bitmap){
        int width = bitmap.getWidth();
        int diameter = mRadius *2 + mStrokeWidth * 2;
        int newSideLength;
        int diff = Math.abs(width - diameter);

        if(width < diameter){
            newSideLength = diff + width;
        }else{
            newSideLength = width - diff;
        }

        return Bitmap.createScaledBitmap(bitmap, newSideLength, newSideLength, true);
    }


    private Bitmap resizeBitmapRectangle(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.e("redu test", " ra " +  mRadius + " stro " + mStrokeWidth);
        int diameter = mRadius * 2 + mStrokeWidth * 2;
        int newWidth, newHeight;
        int newSideLength = Math.abs(diameter - Math.min(width, height));

        if(width < diameter || height < diameter){
            newWidth = width + newSideLength;
            newHeight = height + newSideLength;
        }else{
            newWidth = width - newSideLength;
            newHeight = height - newSideLength;
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Get radius value of cicle image view from xml
     * @param context
     * @param attributeSet
     */
    private void setAttributes(Context context, AttributeSet attributeSet){
        mRadius = context.getTheme()
                .obtainStyledAttributes(
                        attributeSet,
                        R.styleable.CircleImage,
                        0,
                        0)
                .getDimensionPixelSize(R.styleable.CircleImage_radius, DEFAULT_RADIUS);

        mStrokeColor = context.getTheme()
                .obtainStyledAttributes(
                        attributeSet,
                        R.styleable.CircleImage,
                        0,
                        0)
                .getColor(R.styleable.CircleImage_strokeColor, DEFAULT_STROKE_COLOR);

        mStrokeWidth = context.getTheme()
                .obtainStyledAttributes(
                        attributeSet,
                        R.styleable.CircleImage,
                        0,
                        0)
                .getDimensionPixelSize(R.styleable.CircleImage_strokeWidth, DEFAULT_STROKE_WIDTH);

    }
}
