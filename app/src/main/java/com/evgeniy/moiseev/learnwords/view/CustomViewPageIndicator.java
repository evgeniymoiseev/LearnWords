package com.evgeniy.moiseev.learnwords.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.TrainingWordsActivity;

import java.util.ArrayList;
import java.util.List;

public class CustomViewPageIndicator extends View {

    public static final int TYPE_SINGLE_CHOICE = 0;
    public static final int TYPE_SEQUENCE = 1;

    private int mType;

    private DisplayMetrics metrics;
    private Path mPath;
    private Paint mPaint;
    private Paint paintShape;
    private Path pathShape;
    private List<Path> mPaths;
    private List<Paint> mPaints;
    private float mStep;
    private int mCount;
    private boolean isCountChanged;


    public CustomViewPageIndicator(Context context) {
        this(context, null);
    }

    public CustomViewPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCount = TrainingWordsActivity.DEFAULT_TRAINING_COUNT;

        mPaths = new ArrayList<>();
        mPaints = new ArrayList<>();

        paintShape = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShape.setColor(getResources().getColor(R.color.colorNotSelectedTab));
        paintShape.setStyle(Paint.Style.FILL);
        pathShape = new Path();

        metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomViewPageIndicator, 0, 0);
        try {
            mType = typedArray.getInteger(R.styleable.CustomViewPageIndicator_type, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (5 * metrics.density);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        pathShape.reset();
        pathShape.moveTo(0, 0);
        pathShape.lineTo(getWidth(), 0);
        pathShape.lineTo(getWidth(), getHeight());
        pathShape.lineTo(0, getHeight());
        pathShape.lineTo(0, 0);
        canvas.drawPath(pathShape, paintShape);

        mStep = metrics.widthPixels / (mCount * 2 + 1);

        float corner = getHeight() / 6f;
        float top = getHeight() / 4f;
        float bottom = 3 * getHeight() / 4f;

        if (mPaths.isEmpty() || isCountChanged) {
            mPaths.clear();
            mPaints.clear();

            for (int i = 0; i < mCount; i++) {
                mPath = new Path();
                mPath.reset();
                mPath.moveTo(mStep * (2 * i + 1), top + corner);
                mPath.lineTo(mStep * (2 * i + 1) + corner, top);
                mPath.lineTo(mStep * (2 * i + 2) - corner, top);
                mPath.lineTo(mStep * (2 * i + 2), top + corner);
                mPath.lineTo(mStep * (2 * i + 2), bottom - corner);
                mPath.lineTo(mStep * (2 * i + 2) - corner, bottom);
                mPath.lineTo(mStep * (2 * i + 1) + corner, bottom);
                mPath.lineTo(mStep * (2 * i + 1), bottom - corner);

                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(getResources().getColor(android.R.color.transparent));

                mPaths.add(mPath);
                mPaints.add(mPaint);
                canvas.drawPath(mPath, mPaint);
            }
            isCountChanged = false;
        } else {
            for (int i = 0; i < mPaths.size(); i++) {
                canvas.drawPath(mPaths.get(i), mPaints.get(i));
            }
        }
    }

    public void setIndicator(int position, boolean guessed) {
        if (mType == TYPE_SINGLE_CHOICE) {
            clear();
            mPaints.get(position).setColor(getResources().getColor(guessed ? R.color.colorGuessed : R.color.colorError));
        } else if (mType == TYPE_SEQUENCE) {
            mPaints.get(position).setColor(getResources().getColor(guessed ? R.color.colorGuessed : R.color.colorError));
        }
        invalidate();
    }

    public void setType(int type) {
        mType = type;
    }

    public void setIndicatorCount(int count) {
        mCount = count;
        isCountChanged = true;
        invalidate();
    }

    private void clear() {
        for (Paint p : mPaints) {
            p.setColor(getResources().getColor(android.R.color.transparent));
        }
    }

    public void clearIndicator() {
        clear();
        invalidate();
    }

}
