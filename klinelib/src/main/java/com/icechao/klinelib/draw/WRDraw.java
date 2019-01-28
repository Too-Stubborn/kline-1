package com.icechao.klinelib.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.icechao.klinelib.base.BaseKLineChartView;
import com.icechao.klinelib.base.IChartDraw;
import com.icechao.klinelib.base.IValueFormatter;
import com.icechao.klinelib.entity.ICandle;
import com.icechao.klinelib.entity.IWR;
import com.icechao.klinelib.formatter.ValueFormatter;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : WRDraw.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class WRDraw implements IChartDraw<IWR> {

    private Paint r1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint r2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint r3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WRDraw(Context view) {
    }

    @Override
    public void drawTranslated(@Nullable IWR lastPoint, @NonNull IWR curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        if (!Float.isNaN(lastPoint.getrOne())) {
            view.drawChildLine(canvas, r1Paint, lastX, lastPoint.getrOne(), curX, curPoint.getrOne());
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IWR point = (IWR) view.getItem(position);
        if (!Float.isNaN(point.getrOne())) {
            String text = "WR(14):";
            canvas.drawText(text, x, y, view.getTextPaint());
            x += view.getTextPaint().measureText(text);
            text = view.formatValue(point.getrOne()) + " ";
            canvas.drawText(text, x, y, r1Paint);
        }
    }

    @Override
    public float getMaxValue(IWR point, Status status) {
        return point.getrOne();
    }

    @Override
    public float getMinValue(IWR point, Status status) {
        return point.getrOne();
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    @Override
    public void setItemCount(int mItemCount) {

    }

    @Override
    public void startAnim(ICandle item, BaseKLineChartView view) {

    }

    /**
     * 设置%R颜色
     */
    public void setR1Color(int color) {
        r1Paint.setColor(color);
    }

    /**
     * 设置%R颜色
     */
    public void setR2Color(int color) {
        r2Paint.setColor(color);
    }

    /**
     * 设置%R颜色
     */
    public void setR3Color(int color) {
        r3Paint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        r1Paint.setStrokeWidth(width);
        r2Paint.setStrokeWidth(width);
        r3Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        r1Paint.setTextSize(textSize);
        r2Paint.setTextSize(textSize);
        r3Paint.setTextSize(textSize);
    }
}
