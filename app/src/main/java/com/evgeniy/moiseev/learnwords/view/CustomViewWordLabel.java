package com.evgeniy.moiseev.learnwords.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.evgeniy.moiseev.learnwords.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class CustomViewWordLabel extends AppCompatTextView {
    private Path pathShape;
    private Path pathTransparentLine;
    private Paint paintShape;
    private TextPaint textPaint;
    private Paint paintTransparentLine;
    private String text;
    private int measureSpecHeight;

    public CustomViewWordLabel(Context context) {
        this(context, null);
    }

    public CustomViewWordLabel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        pathShape = new Path();
        pathTransparentLine = new Path();

        paintShape = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShape.setColor(getResources().getColor(R.color.colorAccent));
        paintShape.setStyle(Paint.Style.FILL);

        paintTransparentLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTransparentLine.setColor(getResources().getColor(R.color.colorCardViewWord));
        paintTransparentLine.setStyle(Paint.Style.STROKE);
        paintTransparentLine.setStrokeWidth(2);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.colorTextOnPrimary));


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewWordLabel, 0, 0);
        try {
            text = typedArray.getString(R.styleable.CustomViewWordLabel_text);
            float textSize = typedArray.getDimensionPixelSize(R.styleable.CustomViewWordLabel_textSize, 0);
            textPaint.setTextSize(textSize);
        } finally {
            typedArray.recycle();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureSpecHeight = MeasureSpec.getMode(heightMeasureSpec);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        int desiredHeight = measureSpecHeight == MeasureSpec.EXACTLY ?
                MeasureSpec.getSize(heightMeasureSpec) : Math.round(textBounds.height() + getPaddingTop() + getPaddingBottom());
        int desiredWidth = Math.round(textBounds.width() + getPaddingLeft() + getPaddingRight() + (desiredHeight * 2f));

        int measuredHeight = resolveSize(desiredHeight, heightMeasureSpec);
        int measuredWidth = resolveSize(desiredWidth, widthMeasureSpec);

        if (measuredWidth < measuredHeight * 4) {
            measuredWidth = measuredHeight * 4;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = canvas.getHeight();

        canvas.drawColor(Color.TRANSPARENT);
        pathShape.reset();
        pathShape.moveTo(0, 0);
        pathShape.lineTo(canvasHeight, canvasHeight);
        pathShape.lineTo(canvasWidth - canvasHeight, canvasHeight);
        pathShape.lineTo(canvasWidth, 0);
        pathShape.lineTo(0, 0);
        canvas.drawPath(pathShape, paintShape);

        pathTransparentLine.reset();
        pathTransparentLine.moveTo(0, canvasHeight);
        pathTransparentLine.lineTo(canvasWidth, canvasHeight);
        canvas.drawPath(pathTransparentLine, paintTransparentLine);

        pathTransparentLine.reset();
        pathTransparentLine.moveTo(0, canvasHeight - 1);
        pathTransparentLine.lineTo(canvasWidth, canvasHeight - 1);
        canvas.drawPath(pathTransparentLine, paintTransparentLine);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        float y = measureSpecHeight == MeasureSpec.EXACTLY ? canvasHeight / 2 + textBounds.height() / 2 - textPaint.descent() / 2 :
                textBounds.height() / 2 + textPaint.descent() + getPaddingTop();

        canvas.drawText(
                text,
                canvasWidth / 2 - textBounds.width() / 2,
                y,
                textPaint);
    }
}
