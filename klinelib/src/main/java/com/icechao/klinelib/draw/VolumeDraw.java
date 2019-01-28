package com.icechao.klinelib.draw;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.icechao.klinelib.R;
import com.icechao.klinelib.base.BaseKLineChartView;
import com.icechao.klinelib.base.IChartDraw;
import com.icechao.klinelib.base.IValueFormatter;
import com.icechao.klinelib.entity.ICandle;
import com.icechao.klinelib.entity.IVolume;
import com.icechao.klinelib.formatter.BigValueFormatter;
import com.icechao.klinelib.utils.ViewUtil;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : ViewUtil.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class VolumeDraw implements IChartDraw<IVolume> {

    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint volLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float pillarWidth = 0;
    //当前显示的是不是分钟线
    //分钟线下,量的柱状图宽度/2
    private float lineVolWidth;
    private int mItemCount;

    public VolumeDraw(Context context) {
        mRedPaint.setColor(ContextCompat.getColor(context, R.color.chart_up));
        mGreenPaint.setColor(ContextCompat.getColor(context, R.color.chart_down));
        pillarWidth = ViewUtil.Dp2Px(context, 4);
    }

    private float endMa5;
    private float endMa10;

    @Override
    @SuppressWarnings("all")
    public void drawTranslated(
            @Nullable IVolume lastPoint, @NonNull IVolume curPoint, float lastX, float curX,
            @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {

        drawHistogram(canvas, curPoint, lastPoint, curX, view, position);
        if (0f != lastPoint.getMA5Volume()) {
            if (position == mItemCount - 1 && 0 != endMa5) {
                view.drawVolLine(canvas, ma5Paint, lastX, lastPoint.getMA5Volume(), curX, endMa5);
            } else {
                view.drawVolLine(canvas, ma5Paint, lastX, lastPoint.getMA5Volume(), curX, curPoint.getMA5Volume());
            }
        }
        if (0f != lastPoint.getMA10Volume()) {
            if (position == mItemCount - 1 && 0 != endMa10) {
                view.drawVolLine(canvas, ma10Paint, lastX, lastPoint.getMA10Volume(), curX, endMa10);
            } else {
                view.drawVolLine(canvas, ma10Paint, lastX, lastPoint.getMA10Volume(), curX, curPoint.getMA10Volume());
            }
        }
    }

    private void drawHistogram(
            Canvas canvas, IVolume curPoint, IVolume lastPoint, float curX,
            BaseKLineChartView view, int position) {

        float r = pillarWidth / 2 * view.getScaleX();
        float top;
        if (position == mItemCount - 1) {
            top = view.getVolY(view.getLastVol());
        } else {
            top = view.getVolY(curPoint.getVolume());
        }
        int bottom = view.getVolRect().bottom;
        if (view.isLine()) {
            canvas.drawRect(curX - lineVolWidth, top, curX + lineVolWidth, bottom, linePaint);
        } else if (curPoint.getClosePrice() > curPoint.getOpenPrice()) {//涨
            canvas.drawRect(curX - r, top, curX + r, bottom, mRedPaint);
        } else {
            canvas.drawRect(curX - r, top, curX + r, bottom, mGreenPaint);
        }

    }

    @Override
    public void drawText(
            @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IVolume point = (IVolume) view.getItem(position);
        //当没被选中时使用动画效果
        String text;
        if (position == mItemCount - 1) {
            text = "VOL:" + getValueFormatter().format(view.getLastVol()) + "  ";
        } else {
            text = "VOL:" + getValueFormatter().format(point.getVolume()) + "  ";
        }
        canvas.drawText(text, x, y, volLeftPaint);
        x += view.getTextPaint().measureText(text);
        text = "MA5:" + getValueFormatter().format(point.getMA5Volume()) + "  ";
        canvas.drawText(text, x, y, ma5Paint);
        x += ma5Paint.measureText(text);
        text = "MA10:" + getValueFormatter().format(point.getMA10Volume());
        canvas.drawText(text, x, y, ma10Paint);
    }

    @Override
    public float getMaxValue(IVolume point, Status status) {
        return Math.max(point.getVolume(), Math.max(point.getMA5Volume(), point.getMA10Volume()));
    }

    @Override
    public float getMinValue(IVolume point, Status status) {
        return Math.min(point.getVolume(), Math.min(point.getMA5Volume(), point.getMA10Volume()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new BigValueFormatter();
    }

    @Override
    public void setItemCount(int mItemCount) {
        this.mItemCount = mItemCount;
    }

    /**
     * 设置 MA5 线的颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置 vol图左上角文字 线的颜色
     *
     * @param color
     */
    public void setVolLeftColor(int color) {
        this.volLeftPaint.setColor(color);
    }


    /**
     * 设置 MA10 线的颜色
     *
     * @param color
     */
    public void setMa10Color(int color) {
        this.ma10Paint.setColor(color);
    }


    public void setLineWidth(float width) {
        this.linePaint.setStrokeWidth(width);
        this.ma5Paint.setStrokeWidth(width);
        this.ma10Paint.setStrokeWidth(width);
        this.lineVolWidth = width / 2;
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.ma5Paint.setTextSize(textSize);
        this.ma10Paint.setTextSize(textSize);
        this.volLeftPaint.setTextSize(textSize);
    }

    public void setBarWidth(float candleWidth) {
        pillarWidth = candleWidth;
    }


    public void startAnim(ICandle item, BaseKLineChartView view) {
        IVolume volume = (IVolume) item;
        if (0 == endMa5) {
            endMa5 = volume.getMA5Volume();
            endMa10 = volume.getMA10Volume();
        } else {
            view.generaterAnimator(endMa5, volume.getMA5Volume(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    endMa5 = (float) animation.getAnimatedValue();
                }
            });
            view.generaterAnimator(endMa10, volume.getMA10Volume(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    endMa10 = (float) animation.getAnimatedValue();
                }
            });
        }
    }

    public void setMinuteColor(int color) {
        linePaint.setColor(color);
    }
}
