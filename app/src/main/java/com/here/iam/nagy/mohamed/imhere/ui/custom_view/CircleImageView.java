package com.here.iam.nagy.mohamed.imhere.ui.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.here.iam.nagy.mohamed.imhere.R;

/**
 * Created by mohamednagy on 4/5/2017.
 */

public class CircleImageView extends ImageView {

    private Integer radius;
    private int borderSize;
    private int borderColor;
    private int squareSideLength;

    private static final int CENTER_FRAME_X = 0;
    private static final int CENTER_FRAME_Y = 0;
    private static final int DEFAULT_RADIUS = 200;
    private static final int DEFAULT_BORDER_SIZE = 0;
    private static final int DEFAULT_BORDER_COLOR = 0;

    public CircleImageView(Context context) {
        super(context);
    }



    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttr(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Get Image As Bitmap.
        Drawable imageDrawable = getDrawable();
        if(imageDrawable == null)
            return;

        Bitmap imageBitmap = ((BitmapDrawable) imageDrawable).getBitmap();

        if(radius == 0)
            return;

        // Set Circular image.
        Bitmap circularBitmapImageFrame = getCircularBitmapImage(imageBitmap);

        // Set Image To Canvas.
        canvas.drawBitmap(circularBitmapImageFrame, getXCenter(), getYCenter(), null);

        // Draw Border.
        drawCircularImageViewBorder(canvas);
    }

    private float getXCenter(){
        return borderSize / 2;
    }

    private float getYCenter(){
        return borderSize / 2 ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(squareSideLength + borderSize , squareSideLength + borderSize );
    }

    private void drawCircularImageViewBorder(Canvas canvas){
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);

        // set ARGB.
        final int RED = Color.red(borderColor);
        final int GREEN = Color.green(borderColor);
        final int BLUE = Color.blue(borderColor);
        final int ALPHA = Color.alpha(borderColor);

        paint.setStrokeWidth(borderSize);

        paint.setARGB(ALPHA, RED, GREEN, BLUE);

        canvas.drawCircle(radius + getXCenter(), radius + getYCenter(), radius, paint);
    }

    private Bitmap getCircularBitmapImage(Bitmap imageBitmap){

        Bitmap imageBitmapScaled = scaleBitmapImage(imageBitmap);

        Bitmap newImageViewBitmapFrame = getSquareImageViewBitmapFrame();

        Canvas canvas = new Canvas(newImageViewBitmapFrame);
        Paint framePaint = new Paint();

        canvas.drawCircle(radius, radius, radius,framePaint);

        framePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(imageBitmapScaled, CENTER_FRAME_X, CENTER_FRAME_Y, framePaint);

        return newImageViewBitmapFrame;
    }

    private Bitmap getSquareImageViewBitmapFrame(){
        return Bitmap.createBitmap(squareSideLength, squareSideLength, Bitmap.Config.ARGB_8888);
    }

    private Bitmap scaleBitmapImage(Bitmap imageBitmap){
        final int IMAGE_WIDTH = imageBitmap.getWidth();
        final int IMAGE_HEIGHT = imageBitmap.getHeight();
        Bitmap imageBitmapScaled = imageBitmap;


        if(IMAGE_HEIGHT != squareSideLength || IMAGE_WIDTH != squareSideLength){
            imageBitmapScaled = Bitmap.createScaledBitmap(
                    imageBitmap,
                    squareSideLength,
                    squareSideLength,
                    false);
        }

        return imageBitmapScaled;
    }

    private void setAttr(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attributeSet,R.styleable.CircleImageView,0,0);

        try {
            radius = typedArray.getInteger(
                    R.styleable.CircleImageView_radius,
                    DEFAULT_RADIUS);
            borderSize = typedArray.getInteger(
                    R.styleable.CircleImageView_borderSize,
                    DEFAULT_BORDER_SIZE);
            borderColor = typedArray.getColor(
                    R.styleable.CircleImageView_borderColor,
                    DEFAULT_BORDER_COLOR);

            squareSideLength = radius * 2;
        }finally {
            typedArray.recycle();
        }
    }

}
