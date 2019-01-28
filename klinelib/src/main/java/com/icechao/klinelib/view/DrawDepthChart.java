package com.icechao.klinelib.view;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.view
 * @FileName     : DepthChart.java
 * @Author       : chao
 * @Date         : 2019/1/23
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.icechao.klinelib.R;
import com.icechao.klinelib.entity.MarketDepthPercentItem;
import com.icechao.klinelib.utils.NumberUtil;
import com.icechao.klinelib.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class DrawDepthChart extends View implements GestureDetector.OnGestureListener {

    private int length;
    private int viewWidth;
    private int viewHeight;
    private Paint borderLinePaint;
    private Paint textPaint;
    private Paint brokenLineBuyPaint;

    private Paint brokenLineFillBuyPaint;

    private Paint brokenLineSellPaint;

    private Paint brokenLineFillSellPaint;

    private Paint touchBgPaint;


    private Paint horizontalLinePaint;

    private float brokenLineMarginLeft;

    private float brokenLineMarginTop;

    private float brokenLineMarginBottom;

    private float brokenLinerMarginRight;

    private float needDrawWidth;

    private float needDrawHeight;

    private List<MarketDepthPercentItem> buyDataList = new ArrayList<>();

    private List<MarketDepthPercentItem> sellDataList = new ArrayList<>();

    private double maxYVlaue;

    private double minYValue;

    private double maxXVlaue;

    private double minXValue;

    private double calculateXValue;

    private double averageXValue;

    private double calculateYValue;

    private double averageYValue;

    private int numberLine;

    private int horinzontalNumberLine;

    private int mBorderLineColor;

    private int mBorderWidth;

    private int mBorderTextColor = Color.GRAY;

    private float mBorderTextSize;

    private float mBorderLineTextSize;

    private int mBorderTransverseLineColor;

    private float mBorderTransverseLineWidth;

    private int mBrokenLineBuyColor;

    private int mBrokenLineSellColor;

    private float mBrokenLineWidth;

    private int mBrokenLineFillBuyColor;

    private int mBrokenLineFillSellColor;

    private float mBrokenLineFillWidth;

    public PointF[] pointBuys;

    public PointF[] pointSells;


    private int horizontalLabelMarginTop;
    private int verticaLabelMarginRight;
    private int horizontalLabelMarginBottom;

    private GestureDetector detector;


    private int axisTextColor = Color.parseColor("#6D87A8");
    private int axisTouchTextColor = Color.parseColor("#CFD3E9");
    private int axisTouchRectBgColor = Color.parseColor("#E6081724");
    private int axisTouchRectBoundColor = Color.parseColor("#6D87A8");
    private int touchRingBgColor = Color.parseColor("#33081724");
    private Context context;

    public DrawDepthChart(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawDepthChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void initPaint() {

        /**初始化边框文本画笔*/
        if (textPaint == null) {
            textPaint = new Paint();
            initPaint(textPaint);
        }
        textPaint.setTextSize(mBorderTextSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.market_info_depth_chart_coordinate_color));
        /**初始化边框线画笔*/
        if (borderLinePaint == null) {
            borderLinePaint = new Paint();
            initPaint(borderLinePaint);
        }

        borderLinePaint.setTextSize(mBorderLineTextSize);
        borderLinePaint.setStrokeWidth(mBorderWidth);
        borderLinePaint.setColor(mBorderLineColor);

        /**初始化折线画笔*/
        if (brokenLineBuyPaint == null) {
            brokenLineBuyPaint = new Paint();
            initPaint(brokenLineBuyPaint);
            brokenLineBuyPaint.setStrokeWidth(mBrokenLineWidth);
            mBrokenLineBuyColor = ContextCompat.getColor(getContext(), R.color.chart_up);
            brokenLineBuyPaint.setColor(mBrokenLineBuyColor);
        }

        if (brokenLineFillBuyPaint == null) {
            brokenLineFillBuyPaint = new Paint();
            brokenLineFillBuyPaint.setAntiAlias(true);
            brokenLineFillBuyPaint.setStyle(Paint.Style.FILL);
            brokenLineFillBuyPaint.setStrokeWidth(mBrokenLineFillWidth);
            mBrokenLineFillBuyColor = ContextCompat.getColor(getContext(), R.color.chart_up_alpha);
            brokenLineFillBuyPaint.setColor(mBrokenLineFillBuyColor);
        }

        if (brokenLineSellPaint == null) {
            brokenLineSellPaint = new Paint();
            initPaint(brokenLineSellPaint);
            brokenLineSellPaint.setStrokeWidth(mBrokenLineWidth);
            mBrokenLineSellColor = ContextCompat.getColor(getContext(), R.color.chart_down);
            brokenLineSellPaint.setColor(mBrokenLineSellColor);
        }

        if (brokenLineFillSellPaint == null) {
            brokenLineFillSellPaint = new Paint();
            brokenLineFillSellPaint.setAntiAlias(true);
            brokenLineFillSellPaint.setStyle(Paint.Style.FILL);
            brokenLineFillSellPaint.setStrokeWidth(mBrokenLineFillWidth);
            mBrokenLineFillSellColor = ContextCompat.getColor(getContext(), R.color.chart_down_alpha);
            brokenLineFillSellPaint.setColor(mBrokenLineFillSellColor);
        }

        if (touchBgPaint == null) {
            touchBgPaint = new Paint();
            touchBgPaint.setAntiAlias(true);
            touchBgPaint.setStyle(Paint.Style.FILL);
            touchBgPaint.setColor(touchRingBgColor);
        }

        /**横线画笔*/
        if (horizontalLinePaint == null) {
            horizontalLinePaint = new Paint();
            initPaint(horizontalLinePaint);
        }

        horizontalLinePaint.setStrokeWidth(mBorderTransverseLineWidth);
        horizontalLinePaint.setColor(mBorderTransverseLineColor);

    }

    private void init() {
        numberLine = 5;
        horinzontalNumberLine = 3;
        mBorderWidth = 2;
        mBorderTextSize = ViewUtil.Dp2Px(context, 10);
        mBorderLineTextSize = 2;
        mBorderTransverseLineColor = Color.GRAY;
        mBorderTransverseLineWidth = 2;
        mBrokenLineWidth = 4;
        mBrokenLineFillWidth = 2;
        mBorderLineColor = Color.BLACK;

        brokenLineMarginLeft = ViewUtil.Dp2Px(context, 0);
        brokenLineMarginTop = ViewUtil.Dp2Px(context, 20);
        brokenLineMarginBottom = ViewUtil.Dp2Px(context, 20);
        brokenLinerMarginRight = ViewUtil.Dp2Px(context, 0);
        horizontalLabelMarginTop = ViewUtil.Dp2Px(context, 5);
        horizontalLabelMarginBottom = ViewUtil.Dp2Px(context, 5);
        verticaLabelMarginRight = ViewUtil.Dp2Px(context, 5);

        initPaint();

        detector = new GestureDetector(getContext(), this);
    }


    /**
     * 初始化画笔默认属性
     */
    private void initPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();


        initNeedDrawWidthAndHeight();
    }

    /**
     * 初始化绘制折线图的宽高
     */
    private void initNeedDrawWidthAndHeight() {
        needDrawWidth = viewWidth - brokenLineMarginLeft - brokenLinerMarginRight;
        needDrawHeight = viewHeight - brokenLineMarginTop - brokenLineMarginBottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateMaxMin();
        calculateAverage();
        pointBuys = getPoints(buyDataList, needDrawHeight, needDrawWidth, brokenLineMarginLeft, brokenLineMarginTop);
        pointSells = getPoints(sellDataList, needDrawHeight, needDrawWidth, brokenLineMarginLeft, brokenLineMarginTop);

        /**根据数据绘制线*/
        DrawBuyLine(canvas);
        DrawSellLine(canvas);

        /**绘制边框线和边框文本*/
        DrawBorderLineAndText(canvas);


        if (touchMode) {
            boolean safePoint = false;
            safePoint = showBuy ? pointBuys != null && (showIndex < pointBuys.length && showIndex > 0) :
                    pointSells != null && (showIndex < pointSells.length && showIndex > 0);
            if (!safePoint) return;
            RectF center = new RectF(0, 0, 8, 8);
            RectF ring = new RectF(0, 0, ViewUtil.Dp2Px(context, 15), ViewUtil.Dp2Px(context, 15));
            center.offset(-center.width() / 2, -center.height() / 2);
            ring.offset(-ring.width() / 2, -ring.height() / 2);
            PointF touchPoint = showBuy ? pointBuys[showIndex] : pointSells[showIndex];

            MarketDepthPercentItem marketDepthPercentItem = showBuy ? buyDataList.get(showIndex) : sellDataList.get(showIndex);


            String touchVol = verticalCoordinatePlace(marketDepthPercentItem.getAmount());
            String touchValue = NumberUtil.roundFormatDown(String.valueOf(marketDepthPercentItem.getPrice()), length);

            float y = brokenLineMarginTop + needDrawHeight;
            float h = getHeight() - y - horizontalLabelMarginBottom;

            Rect valueRect = new Rect();
            RectF valueRect2 = new RectF();
            textPaint.getTextBounds(touchVol, 0, touchVol.length(), valueRect);
            valueRect2.set(0, touchPoint.y, valueRect.width() + 2 * ViewUtil.Dp2Px(context, 5), touchPoint.y + h);
            valueRect2.offset(getWidth() - valueRect2.width(), -valueRect2.height() / 2);
            if (valueRect2.bottom > y) {
                valueRect2.offset(0, y - valueRect2.bottom);
            } else if (valueRect2.top < brokenLineMarginTop) {
                valueRect2.offset(0, brokenLineMarginTop - valueRect2.bottom);
            }


            Rect volRect = new Rect();
            RectF volRect2 = new RectF();
            textPaint.getTextBounds(touchValue, 0, touchValue.length(), volRect);
            float baseLine = y + h / 2 - volRect.exactCenterY();

            volRect2.set(0, y, volRect.width() + 2 * ViewUtil.Dp2Px(context, 5), y + h);
            float offsetX = Math.min(Math.max(0, touchPoint.x - volRect2.width() / 2), getWidth() - volRect2.width());
            volRect2.offset(offsetX, 0);

            center.offset(touchPoint.x, touchPoint.y);
            ring.offset(touchPoint.x, touchPoint.y);

            canvas.drawArc(ring, 0, 360, true, touchBgPaint);
            canvas.drawArc(ring, 0, 360, false, showBuy ? brokenLineBuyPaint : brokenLineSellPaint);
            canvas.drawArc(center, 0, 360, true, showBuy ? brokenLineBuyPaint : brokenLineSellPaint);

            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.save();
            canvas.clipRect(valueRect2);
            canvas.drawColor(axisTouchRectBgColor);
            canvas.restore();
            textPaint.setColor(axisTouchRectBoundColor);
            canvas.drawRect(valueRect2, textPaint);
            textPaint.setColor(axisTouchTextColor);
            canvas.drawText(touchVol, getWidth() - verticaLabelMarginRight, valueRect2.centerY() - valueRect.centerY(), textPaint);

            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.save();
            canvas.clipRect(volRect2);
            canvas.drawColor(axisTouchRectBgColor);
            canvas.restore();
            textPaint.setColor(axisTouchRectBoundColor);
            canvas.drawRect(volRect2, textPaint);
            textPaint.setColor(axisTouchTextColor);
            canvas.drawText(touchValue, volRect2.centerX(), baseLine, textPaint);

        }
    }

    private void calculateMaxMin() {
        if (sellDataList != null && sellDataList.size() > 0) {
            maxXVlaue = sellDataList.get(sellDataList.size() - 1).getPrice();
            maxYVlaue = sellDataList.get(sellDataList.size() - 1).getAmount();
        }
        if (buyDataList != null && buyDataList.size() > 0) {
            minXValue = buyDataList.get(0).getPrice();
            double max = buyDataList.get(0).getAmount();
            maxYVlaue = max > maxYVlaue ? max : maxYVlaue;
        }
        minYValue = 0;
    }

    private void calculateAverage() {
        /**计算总值*/
        calculateYValue = maxYVlaue - minYValue;
        /**计算框线横线间隔的数据平均值*/
        averageYValue = calculateYValue / (numberLine - 0.5);
        calculateXValue = maxXVlaue - minXValue;
        averageXValue = calculateXValue / (horinzontalNumberLine - 1);
    }


    /**
     * 根据值绘制折线
     */
    public void DrawBuyLine(Canvas canvas) {
        Path path = new Path();
        Path mFillPath = new Path();

        for (int j = 0; j < pointBuys.length; j++) {
            PointF startp = pointBuys[j];
            PointF endp;
            if (j != pointBuys.length - 1) {
                endp = pointBuys[j + 1];
                float wt = (startp.x + endp.x) / 2;
                PointF p3 = new PointF();
                PointF p4 = new PointF();
                p3.y = startp.y;
                p3.x = wt;
                p4.y = endp.y;
                p4.x = wt;
                if (j == 0) {
                    path.moveTo(startp.x, startp.y);
                    mFillPath.moveTo(startp.x, startp.y);
                }

                path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
                mFillPath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            } else {
                float y = needDrawHeight + brokenLineMarginTop;
                path.lineTo(startp.x, y);
                mFillPath.lineTo(startp.x, y);
                mFillPath.lineTo(brokenLineMarginLeft, y);
                mFillPath.lineTo(brokenLineMarginLeft, pointBuys[0].y);
            }
        }
        canvas.drawPath(mFillPath, brokenLineFillBuyPaint);
        canvas.drawPath(path, brokenLineBuyPaint);

    }

    public void DrawSellLine(Canvas canvas) {
        Path mPath = new Path();
        Path mFillPath = new Path();

        for (int j = 0; j < pointSells.length; j++) {
            PointF startp = pointSells[j];
            PointF endp;
            if (j != pointSells.length - 1) {
                endp = pointSells[j + 1];
                float wt = (startp.x + endp.x) / 2;
                PointF p3 = new PointF();
                PointF p4 = new PointF();
                p3.y = startp.y;
                p3.x = wt;
                p4.y = endp.y;
                p4.x = wt;
                if (j == 0) {
                    mPath.moveTo(startp.x, startp.y);

                    mFillPath.moveTo(startp.x, startp.y);
                }

                mPath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
                mFillPath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            } else {
                float x = brokenLineMarginLeft + needDrawWidth;
                mPath.lineTo(x, startp.y);
                mFillPath.lineTo(x, startp.y);
                float y = needDrawHeight + brokenLineMarginTop;
                mFillPath.lineTo(x, y);
                mFillPath.lineTo(pointSells[0].x, y);
            }
        }
        canvas.drawPath(mFillPath, brokenLineFillSellPaint);
        canvas.drawPath(mPath, brokenLineSellPaint);

    }

    /**
     * 绘制边框坐标
     */
    private void DrawBorderLineAndText(Canvas canvas) {

        //纵向的坐标
        float averageHeight = needDrawHeight / numberLine;
        textPaint.setTextAlign(TextPaint.Align.RIGHT);
        textPaint.setColor(axisTextColor);
        for (int i = 0; i < numberLine; i++) {
            float nowadayHeight = averageHeight * i;
            double v = averageYValue * (numberLine - i) + minYValue;
            canvas.drawText(verticalCoordinatePlace(v) + "", getWidth() - verticaLabelMarginRight, nowadayHeight + brokenLineMarginTop, textPaint);
        }
        //横向的坐标
        float averageWidth = needDrawWidth / (horinzontalNumberLine - 1);
        textPaint.setTextAlign(TextPaint.Align.LEFT);
        for (int i = 0; i < horinzontalNumberLine; i++) {
            float nowadayWidth = averageWidth * i;
            double v = averageXValue * i + minXValue;
            String text = NumberUtil.roundFormatDown(String.valueOf(v), length);
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);

            float y = brokenLineMarginTop + needDrawHeight + bounds.height() + horizontalLabelMarginTop;

            if (i == 0) {
                canvas.drawText(text + "", brokenLineMarginLeft + nowadayWidth, y, textPaint);
            } else if (i == horinzontalNumberLine - 1) {
                canvas.drawText(text + "", brokenLineMarginLeft + nowadayWidth - bounds.width(), y, textPaint);
            } else {
                canvas.drawText(text + "", brokenLineMarginLeft + nowadayWidth - bounds.width() / 2, y, textPaint);
            }
        }
    }

    /**
     *
     */
    private String verticalCoordinatePlace(double f) {
        return NumberUtil.getTradeMarketAmount(NumberUtil.roundFormat(f, length));
    }


    /**
     * 根据值计算在该值的 x，y坐标
     */
    public PointF[] getPoints(List<MarketDepthPercentItem> values, double height, double width, double left, double top) {
        int size = values.size();
        PointF[] points = new PointF[size];
        double maxY = averageYValue * numberLine;
        for (int i = 0; i < size; i++) {
            double valueY = values.get(i).getAmount() - minYValue;
            //计算每点高度所以对应的值
            double meanY = (maxY - minYValue) / height;
            //获取要绘制的高度
            float drawHeight = (float) (valueY / meanY);
            int pointY = (int) (height + top - drawHeight);

            double valueX = values.get(i).getPrice() - minXValue;
            double meanX = (maxXVlaue - minXValue) / width;
            float drawWidth = (float) (valueX / meanX);

            int pointX = (int) (drawWidth + left);
            PointF point = new PointF(pointX, pointY);
            points[i] = point;
        }
        return points;
    }


    /**
     * 设置边框左上右下边距
     */
    public void setBrokenLineLTRB(float l, float t, float r, float b) {
        brokenLineMarginLeft = ViewUtil.Dp2Px(context, l);
        brokenLineMarginTop = ViewUtil.Dp2Px(context, t);
        brokenLinerMarginRight = ViewUtil.Dp2Px(context, r);
        brokenLineMarginBottom = ViewUtil.Dp2Px(context, b);
    }

    /**
     * 数据data
     */
    public void setBuyDataListValue(List<MarketDepthPercentItem> value) {
        buyDataList.clear();
        buyDataList.addAll(value);
    }

    /**
     * 数据data
     */
    public void setSellDataListValue(List<MarketDepthPercentItem> value) {
        sellDataList.clear();
        sellDataList.addAll(value);
    }

    /**
     * 图表显示最大值
     */
    @SuppressWarnings("all")
    public void setMaxYVlaue(float maxYVlaue) {
        this.maxYVlaue = maxYVlaue;
    }

    /**
     * 图表显示最小值
     */
    @SuppressWarnings("all")
    public void setMinYValue(float minYValue) {
        this.minYValue = minYValue;
    }

    /**
     * 图表横线数量
     */
    @SuppressWarnings("all")
    public void setNumberLine(int numberLine) {
        this.numberLine = numberLine;
    }

    /**
     * 边框线颜色
     */
    @SuppressWarnings("all")
    public void setBorderLineColor(int borderLineColor) {
        mBorderLineColor = borderLineColor;
    }

    /**
     * 边框文本颜色
     */
    @SuppressWarnings("all")
    public void setBorderTextColor(int borderTextColor) {
        mBorderTextColor = borderTextColor;
    }

    /**
     * 边框文本大小
     */
    @SuppressWarnings("all")
    public void setBorderTextSize(float borderTextSize) {
        mBorderTextSize = ViewUtil.Dp2Px(context, borderTextSize);
    }

    /**
     * 框线横线 颜色
     */
    @SuppressWarnings("all")
    public void setBorderTransverseLineColor(int borderTransverseLineColor) {
        mBorderTransverseLineColor = borderTransverseLineColor;
    }

    /**
     * 边框内折线颜色
     */
    @SuppressWarnings("all")
    public void setBrokenLineColor(int brokenLineColor) {
        mBrokenLineBuyColor = brokenLineColor;
    }

    /**
     * 边框线宽度
     */
    @SuppressWarnings("all")
    public void setBorderWidth(float borderWidth) {
        mBorderWidth = ViewUtil.Dp2Px(context, borderWidth);
    }

    /**
     * 边框横线宽度
     */
    @SuppressWarnings("all")
    public void setBorderTransverseLineWidth(float borderTransverseLineWidth) {
        mBorderTransverseLineWidth = ViewUtil.Dp2Px(context, borderTransverseLineWidth);
    }

    /**
     * 折线宽度
     */
    @SuppressWarnings("all")
    public void setBrokenLineWidth(float brokenLineWidth) {
        mBrokenLineWidth = ViewUtil.Dp2Px(context, brokenLineWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchMode) {
                    calcTouchPoint(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                (getParent()).requestDisallowInterceptTouchEvent(false);
                break;

        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        touchMode = false;
        invalidate();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    private boolean touchMode = false;
    private boolean showBuy;
    private int showIndex;

    @Override
    public void onLongPress(MotionEvent e) {
        calcTouchPoint(e);
        (getParent()).requestDisallowInterceptTouchEvent(touchMode);
    }

    private void calcTouchPoint(MotionEvent e) {
        if (pointSells != null && pointSells.length > 0 &&
                pointBuys != null && pointBuys.length > 0) {
            touchMode = true;
            float x = e.getX();
            float minDistanceSell = Integer.MAX_VALUE;
            float minDistanceBuy = Integer.MAX_VALUE;
            int indexSell = 0;
            int indexBuy = 0;

            for (int i = 0; i < pointSells.length; i++) {
                float temp = Math.abs(pointSells[i].x - x);
                if (temp < minDistanceSell) {
                    minDistanceSell = temp;
                    indexSell = i;
                }
            }
            for (int i = 0; i < pointBuys.length; i++) {
                float temp = Math.abs(pointBuys[i].x - x);
                if (temp < minDistanceBuy) {
                    minDistanceBuy = temp;
                    indexBuy = i;
                }
            }

            showBuy = minDistanceBuy < minDistanceSell;
            showIndex = showBuy ? indexBuy : indexSell;
            invalidate();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
