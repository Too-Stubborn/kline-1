package com.icechao.klinelib.base;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.icechao.klinelib.R;
import com.icechao.klinelib.draw.MainDraw;
import com.icechao.klinelib.draw.Status;
import com.icechao.klinelib.draw.VolumeDraw;
import com.icechao.klinelib.entity.ICandle;
import com.icechao.klinelib.entity.IKLine;
import com.icechao.klinelib.formatter.TimeFormatter;
import com.icechao.klinelib.formatter.ValueFormatter;
import com.icechao.klinelib.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*************************************************************************
 * Description   : 去掉所有的Invalidate调用,只在价格发生变化时动画变化,或者显示分时线时分一直刷新页面
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : ViewUtil.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public abstract class BaseKLineChartView extends ScrollAndScaleView {

    private long duration = 500;

    private float priceBoxMarginRight = 120;

    private int childDrawPosition = -1;

    private float canvasTranslateX = 1f;

    private int width = 0;

    private int topPadding;

    private int childPadding;

    private int bottomPadding;

    private float mainScaleY = 1;

    private float volScaleY = 1;

    private float childScaleY = 1;

    private float mainMaxValue = Float.MAX_VALUE;

    private float mainMinValue = Float.MIN_VALUE;

    private float mainHighMaxValue = 0;

    private float mainLowMinValue = 0;

    private int mainMaxIndex = 0;

    private int mainMinIndex = 0;

    private Float volMaxValue = Float.MAX_VALUE;

    private Float childMaxValue = Float.MAX_VALUE;

    private int screenLeftIndex = 0;

    private int screenRightIndex = 0;

    private float chartItemWidth = 6;

    private int gridRows = 5;

    private int gridColumns = 5;

    private Paint lineEndPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint lineEndFillPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectedCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectedbigCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint maxMinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectedXLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectedYLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectedPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectorFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint priceLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint priceLineBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint priceLineBoxBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint priceLineBoxRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int selectedIndex;

    private int priceLineBoxHeight = 40;

    private IChartDraw<ICandle> mainDraw;
    private IChartDraw volDraw;
    private IChartDraw<ICandle> childDraw;

    private float lastPrice, lastVol, lastHigh, lastLow;
    private IAdapter<ICandle> dataAdapter;

    private boolean isLine;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            post(updataRunnable);
        }
    };
    private float selectedPointRadius = 5;
    private Runnable updataRunnable;


    /**
     * 执行动画渐变
     *
     * @param item
     * @param closePrice
     * @param vol
     */
    private void excuteAnimChange(ICandle item, float closePrice, float vol) {
        generaterAnimator(lastVol, vol, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lastVol = (Float) animation.getAnimatedValue();
            }
        });

        generaterAnimator(lastPrice, closePrice, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lastPrice = (Float) animation.getAnimatedValue();
                if (isLine) {
                    return;
                }
                animValidate();
            }
        });
        mainDraw.startAnim(item, BaseKLineChartView.this);
        volDraw.startAnim(item, BaseKLineChartView.this);
    }

    //当前点的个数
    private int itemsCount;
    @SuppressWarnings("unchecked")
    private List<IChartDraw> mChildDraws = new ArrayList<>();

    private IValueFormatter mValueFormatter;
    private IDateTimeFormatter mDateTimeFormatter;

    private ValueAnimator mAnimator;

    private float mOverScrollRange = 0;

    private OnSelectedChangedListener mOnSelectedChangedListener = null;

    private Rect mainRect;

    private Rect volRect;

    private Rect childRect;

    private float lineEndPointWidth;
    private ValueAnimator valueAnimator;
    private int areaTopColor;
    private int areaBottomColor;
    private float selectedWidth;
    private int selectedYColor;
    private int backGroundTopColor;
    private int backGroundBottomColor;

    public BaseKLineChartView(Context context) {
        super(context);
        init();
    }

    public BaseKLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseKLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        gestureDetector = new GestureDetectorCompat(getContext(), this);
        scaleDetector = new ScaleGestureDetector(getContext(), this);
        topPadding = (int) getResources().getDimension(R.dimen.chart_top_padding);
        childPadding = (int) getResources().getDimension(R.dimen.child_top_padding);
        bottomPadding = (int) getResources().getDimension(R.dimen.chart_bottom_padding);

        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        selectorFramePaint.setStrokeWidth(ViewUtil.Dp2Px(getContext(), 0.6f));
        selectorFramePaint.setStyle(Paint.Style.STROKE);
        selectorFramePaint.setColor(Color.WHITE);
        priceLinePaint.setAntiAlias(true);
        priceLineBoxRightPaint.setStyle(Paint.Style.STROKE);

        priceLineBoxPaint.setColor(Color.WHITE);
        priceLineBoxBgPaint.setColor(Color.BLACK);

        priceLineBoxPaint.setStyle(Paint.Style.STROKE);
        priceLineBoxPaint.setStrokeWidth(1);


        updataRunnable = new Runnable() {
            @Override
            public void run() {
                if (0 == width) {
                    postDelayed(updataRunnable, 500);
                } else {
                    int count = getAdapter().getCount();
                    ICandle item = getAdapter().getItem(count - 1);
                    float closePrice = item.getClosePrice();
                    float vol = item.getVolume();
                    float high = item.getHighPrice();
                    float low = item.getLowPrice();
                    if (itemsCount < count) {
                        setItemCount(count);
                        lastPrice = closePrice;
                        lastVol = vol;
                        lastHigh = high;
                        lastLow = low;
                        if (screenRightIndex == itemsCount - 2) {
                            setTranslatedX(canvasTranslateX - chartItemWidth * getScaleX());
                        }
                    } else if (count == itemsCount) {
                        if (lastPrice != closePrice || lastVol != item.getClosePrice() ||
                                lastHigh != high || low != low) {
                            excuteAnimChange(item, closePrice, vol);
                        }
                    }
                    invalidate();
                }
            }
        };

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        displayHeight = h - topPadding - bottomPadding;
        initRect();
    }

    int displayHeight = 0;

    private void initRect() {
        if (null != childDraw) {
            int mMainHeight = (int) (displayHeight * 0.6f);
            int mVolHeight = (int) (displayHeight * 0.2f);
            int mChildHeight = (int) (displayHeight * 0.2f);
            mainRect = new Rect(0, topPadding, width, topPadding + mMainHeight);
            volRect = new Rect(0, mainRect.bottom + childPadding, width, mainRect.bottom + mVolHeight);
            childRect = new Rect(0, volRect.bottom + childPadding, width, volRect.bottom + mChildHeight);
        } else {
            int mMainHeight = (int) (displayHeight * 0.8f);
            int mVolHeight = (int) (displayHeight * 0.2f);
            mainRect = new Rect(0, topPadding, width, topPadding + mMainHeight);
            volRect = new Rect(0, mainRect.bottom + childPadding, width, mainRect.bottom + mVolHeight);
        }
    }


    private long time;

    public void animValidate() {
        if (System.currentTimeMillis() - time > 16) {
            invalidate();
            time = System.currentTimeMillis();
        }

    }

    @Override
    public void onDraw(Canvas canvas) {

        if (width == 0 || mainRect.height() == 0 || itemsCount == 0) {
            return;
        }
        calculateValue();

        drawBackground(canvas);

        drawGird(canvas);

        drawK(canvas);

        drawText(canvas);

        drawPriceLine(canvas);

        drawValue(canvas, isLongPress ? selectedIndex : screenRightIndex);


    }

    private void drawBackground(Canvas canvas) {
        int mid = width / 2;
        canvas.drawColor(getContext().getResources().getColor(R.color.chart_background));
        backgroundPaint.setAlpha(18);
        backgroundPaint.setShader(new LinearGradient(mid, 0, mid, mainRect.bottom, backGroundTopColor, backGroundBottomColor, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, mainRect.bottom, backgroundPaint);
        backgroundPaint.setShader(new LinearGradient(mid, volRect.top - topPadding, mid, volRect.bottom, backGroundTopColor, backGroundBottomColor, Shader.TileMode.CLAMP));
        canvas.drawRect(0, mainRect.bottom, width, volRect.bottom, backgroundPaint);
        if (null != childDraw) {
            backgroundPaint.setShader(new LinearGradient(mid, childRect.top - topPadding, mid, childRect.bottom, backGroundTopColor, backGroundBottomColor, Shader.TileMode.CLAMP));
            canvas.drawRect(0, childRect.top, width, childRect.bottom, backgroundPaint);
        }
    }

    public float getMainY(float value) {
        return (mainMaxValue - value) * mainScaleY + mainRect.top;
    }

    public void setItemCount(int itemCount) {

        if (0 == this.itemsCount) {
            this.itemsCount = itemCount;
        } else {
            this.itemsCount = itemCount;
        }
        if (0 == itemCount) {
            setTranslatedX(0);
        }
        mainDraw.setItemCount(itemsCount);
        volDraw.setItemCount(itemsCount);
        if (1f == canvasTranslateX) {
            setTranslatedX(getMinTranslate());
        }
    }

    public float getVolY(float value) {
        return (volMaxValue - value) * volScaleY + volRect.top;
    }

    public float getChildY(float value) {
        return (childMaxValue - value) * childScaleY + childRect.top;
    }

    /**
     * 解决text居中的问题
     */
    public float fixTextYBaseBottom(float y) {
        return y + (textHeight) / 2 - textDecent;
    }

    /**
     * 画表格
     *
     * @param canvas canvas
     */
    private void drawGird(Canvas canvas) {
        canvas.drawLine(0, 0, width, 0, gridPaint);
        //横向的grid
        float rowSpace = displayHeight / gridRows;
        for (int i = 0; i <= gridRows; i++) {
            float y = rowSpace * i;
            canvas.drawLine(0, y + mainRect.top, width, y + mainRect.top, gridPaint);
        }
        canvas.drawLine(0, volRect.bottom, width, volRect.bottom, gridPaint);
        float columnSpace = width / gridColumns;
        for (int i = 1; i < gridColumns; i++) {
            float stopX = columnSpace * i;
            canvas.drawLine(stopX, 0, stopX, mainRect.bottom, gridPaint);
            canvas.drawLine(stopX, mainRect.bottom, stopX, volRect.bottom, gridPaint);
            if (null != childDraw) {
                canvas.drawLine(stopX, volRect.bottom, stopX, childRect.bottom, gridPaint);
            }
        }
    }

//    public float getTranslationX() {
//        float dataLength = getDataLength();
//        if (dataLength < width) {
//            return width - dataLength;
//        } else {
//            return canvasTranslateX;
//        }
//    }


    /**
     * 画k线图
     *
     * @param canvas canvas
     */
    private void drawK(Canvas canvas) {

        //保存之前的平移，缩放
        canvas.save();
        canvas.translate(canvasTranslateX, 0);
        for (int i = screenLeftIndex; i <= screenRightIndex; i++) {
            ICandle lastPoint, currentPoint = getItem(i);
            float lastX, currentPointX = getX(i);
            if (0 < i) {
                lastPoint = getItem(i - 1);
                lastX = getX(i - 1);
            } else {
                lastPoint = currentPoint;
                lastX = 0;
            }
            mainDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            volDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            if (null != childDraw) {
                childDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            }

        }
        drawSelectedCross(canvas);

        //还原 平移缩放

        drawMaxAndMin(canvas);
        canvas.restore();
    }

    private void drawSelectedCross(Canvas canvas) {
        //画选择线
        if (isLongPress) {
            IKLine point = (IKLine) getItem(selectedIndex);
            float x = getX(selectedIndex);
            float y = getMainY(point.getClosePrice());
            // k线图竖线
            float halfWidth = selectedWidth / 2 * scaleX;
            float left = x - halfWidth;
            float right = x + halfWidth;
            float bottom = displayHeight + topPadding;
            Path path = new Path();
            path.moveTo(left, topPadding);
            path.lineTo(right, topPadding);
            path.lineTo(right, bottom);
            path.lineTo(left, bottom);
            path.close();
            LinearGradient linearGradient = new LinearGradient(x, topPadding, x, bottom,
                    new int[]{Color.TRANSPARENT, selectedYColor, selectedYColor, Color.TRANSPARENT},
                    new float[]{0f, 0.2f, 0.8f, 1f}, Shader.TileMode.CLAMP);
            selectedYLinePaint.setShader(linearGradient);
            canvas.drawPath(path, selectedYLinePaint);
            canvas.drawLine(-canvasTranslateX, y, -canvasTranslateX + width - 5, y, selectedXLinePaint);
            canvas.drawCircle(x, y, chartItemWidth, selectedbigCrossPaint);
            canvas.drawCircle(x, y, selectedPointRadius, selectedCrossPaint);
        }
    }


    /**
     * 画文字
     *
     * @param canvas canvas
     */
    private void drawText(Canvas canvas) {

        int textY = mainRect.top - 5;
        float rowValue;
        int gridRowCount;
        float rowSpace = displayHeight / gridRows;
        //当显示子视图时,y轴label减少显示一个
        if (null != childDraw) {
            gridRowCount = gridRows - 2;
            rowValue = (mainMaxValue - mainMinValue) / gridRowCount;
        } else {
            gridRowCount = gridRows - 1;
            rowValue = (mainMaxValue - mainMinValue) / gridRowCount;
        }

        //Y轴上网络的值
        for (int i = 0; i <= gridRowCount; i++) {
            String text = formatValue(mainMaxValue - i * rowValue);
            canvas.drawText(text, width - textPaint.measureText(text), rowSpace * i + textY, textPaint);
        }

        //交易量图的Y轴label
        String maxVol = volDraw.getValueFormatter().format(volMaxValue);
        canvas.drawText(maxVol, width - textPaint.measureText(maxVol), mainRect.bottom + baseLine, textPaint);

        //子图Y轴label
        if (null != childDraw) {
            String childLable = childDraw.getValueFormatter().format(childMaxValue);
            canvas.drawText(childLable, width - textPaint.measureText(childLable), volRect.bottom + baseLine, textPaint);
        }
        //画时间
        float columnSpace = width / gridColumns;
        float y;
        if (null != childDraw) {
            y = childRect.bottom + baseLine + 5;
        } else {
            y = volRect.bottom + baseLine + 5;
        }

        float halfWidth = chartItemWidth / 2 * getScaleX();
        float startX = getX(screenLeftIndex) - halfWidth;
        float stopX = getX(screenRightIndex) + halfWidth;

        for (int i = 1; i < gridColumns; i++) {
            float tempX = columnSpace * i;
            float translateX = xToTranslateX(tempX);
            if (translateX >= startX && translateX <= stopX) {
                int index = indexOfTranslateX(translateX);
                String text = dataAdapter.getDate(index);
                canvas.drawText(text, tempX - textPaint.measureText(text) / 2, y, textPaint);
            }
        }
        //X轴最左侧的值
        float translateX = xToTranslateX(0);
        if (translateX >= startX && translateX <= stopX) {
            canvas.drawText(getAdapter().getDate(screenLeftIndex), 0, y, textPaint);
        }
        //X轴最右侧的值
        translateX = xToTranslateX(width);
        if (translateX >= startX && translateX <= stopX) {
            String text = getAdapter().getDate(screenRightIndex);
            canvas.drawText(text, width - textPaint.measureText(text), y, textPaint);
        }
        if (isLongPress) {
            // 选中状态下的Y值
            IKLine point = (IKLine) getItem(selectedIndex);
            float textHorizentalPadding = ViewUtil.Dp2Px(getContext(), 5);
            float textVerticalPadding = ViewUtil.Dp2Px(getContext(), 3);
            float r = textHeight / 2 + textVerticalPadding;
            y = getMainY(point.getClosePrice());
            float x;
            String text = formatValue(point.getClosePrice());
            float textWidth = textPaint.measureText(text);
            float tempX = textWidth + 2 * textHorizentalPadding;
            //左侧框
            float boxTop = y - r;
            float boXBottom = y + r;
            if (translateXtoX(getX(selectedIndex)) < getChartWidth() / 2) {
                x = 1;
                Path path = new Path();
                path.moveTo(x, boxTop);
                path.lineTo(x, boXBottom);
                path.lineTo(tempX, boXBottom);
                path.lineTo(tempX + textVerticalPadding * 2, y);
                path.lineTo(tempX, boxTop);
                path.close();
                canvas.drawPath(path, selectedPointPaint);
                canvas.drawPath(path, selectorFramePaint);
                canvas.drawText(text, x + textHorizentalPadding, fixTextYBaseBottom(y), textPaint);
            } else {//右侧框
                x = width - textWidth - 1 - 2 * textHorizentalPadding - textVerticalPadding;
                Path path = new Path();
                path.moveTo(x, y);
                path.lineTo(x + textVerticalPadding * 2, boXBottom);
                path.lineTo(width - 2, boXBottom);
                path.lineTo(width - 2, boxTop);
                path.lineTo(x + textVerticalPadding * 2, boxTop);
                path.close();
                canvas.drawPath(path, selectedPointPaint);
                canvas.drawPath(path, selectorFramePaint);
                canvas.drawText(text, x + textVerticalPadding + textHorizentalPadding, fixTextYBaseBottom(y), textPaint);
            }

            // 画X值
            String date = dataAdapter.getDate(selectedIndex);
            textWidth = textPaint.measureText(date);
            r = textHeight / 2;
            x = translateXtoX(getX(selectedIndex));
            if (null != childDraw) {
                y = childRect.bottom;
            } else {
                y = volRect.bottom;
            }

            float halfTextWidth = textWidth / 2;
            if (x < tempX) {
                x = 1 + halfTextWidth + textHorizentalPadding;
            } else if (width - x < tempX) {
                x = width - 1 - halfTextWidth - textHorizentalPadding;
            }

            float tempLeft = x - halfTextWidth;
            float left = tempLeft - textHorizentalPadding;
            float right = x + halfTextWidth + textHorizentalPadding;
            float bottom = y + baseLine + r;
            canvas.drawRect(left, y, right, bottom, selectedPointPaint);
            canvas.drawRect(left, y, right, bottom, selectorFramePaint);
            canvas.drawText(date, tempLeft, y + baseLine + 5, textPaint);
        }
    }

    /**
     * 绘制最后一个呼吸灯效果
     *
     * @param canvas canvas
     * @param stopX  x
     */
    public void drawEndPoint(Canvas canvas, float stopX) {
        RadialGradient radialGradient = new RadialGradient(stopX, getMainY(lastPrice), endShadowLayerWidth, lineEndPointPaint.getColor(), Color.TRANSPARENT, Shader.TileMode.CLAMP);
        lineEndPointPaint.setShader(radialGradient);
        canvas.drawCircle(stopX, getMainY(lastPrice), lineEndPointWidth * 4, lineEndPointPaint);

    }


    private void drawPriceLine(Canvas canvas) {
        float y = getMainY(lastPrice);
        @SuppressLint("DefaultLocale") String priceString = String.format("%.2f", lastPrice);
        //多加2个像素防止文字宽度有小的变化
        float textWidth = textPaint.measureText(priceString);
        float textLeft = width - textWidth;
        float endLineRight = getX(screenRightIndex) + canvasTranslateX;
        if (screenRightIndex == itemsCount - 1 && endLineRight < textLeft) {
            //两个价格图层在点之间所以放在价格线中绘制
            if (isLine) {
                drawEndPoint(canvas, endLineRight);
            }
            //8 : 4 虚线
            Path path = new Path();
            for (float i = endLineRight; i < textLeft - 2; i += 12) {
                path.moveTo(i, y);
                path.lineTo(i + 8, y);
            }
            canvas.drawPath(path, priceLineBoxRightPaint);
            canvas.drawText(priceString, textLeft, fixTextYBaseBottom(y), priceLineBoxRightPaint);
            //绘制价格圆点
            if (isLine) {
                canvas.drawCircle(endLineRight, y, lineEndPointWidth, lineEndFillPointPaint);
            }
        } else {
            float halfPriceBoxHeight = priceLineBoxHeight / 2;
            //修改价格信息框Y轴计算保证,只会绘制在主区域中
            if (lastPrice > mainMaxValue) {
                y = mainRect.top + halfPriceBoxHeight;
            } else if (lastPrice < mainMinValue) {
                y = mainRect.bottom - halfPriceBoxHeight;
            }

            Path path = new Path();
            for (int i = 0; i < width; i += 12) {
                path.moveTo(i, y);
                path.lineTo(i + 8, y);
            }

            canvas.drawPath(path, priceLinePaint);
            //绘制价格框
            float halfHeight = textHeight / 2;
            float boxRight = width - priceBoxMarginRight;
            float boxLeft = boxRight - textWidth - priceLineBoxHeight - halfHeight;
            float boxTop = y - halfPriceBoxHeight;
            float boxBottom = y + halfPriceBoxHeight;
            canvas.drawRoundRect(new RectF(boxLeft, boxTop, boxRight, boxBottom), halfPriceBoxHeight, halfPriceBoxHeight, priceLineBoxBgPaint);
            canvas.drawRoundRect(new RectF(boxLeft, boxTop, boxRight, boxBottom), halfPriceBoxHeight, halfPriceBoxHeight, priceLineBoxPaint);

            //价格框三角形
            float top = y - halfHeight / 2;
            float bottom = y + halfHeight / 2;
            float shapeLeft = boxRight - halfPriceBoxHeight;
            Path shape = new Path();
            shape.moveTo(shapeLeft, top);
            shape.lineTo(shapeLeft, bottom);
            shape.lineTo(shapeLeft + halfHeight / 2, y);
            shape.close();
            canvas.drawPath(shape, textPaint);

            canvas.drawText(priceString, shapeLeft - textWidth - halfHeight, (y + (halfHeight - textDecent)), textPaint);
        }
    }

    protected float getDataLength() {
        return chartItemWidth * getScaleX() * (itemsCount - 1) + getmOverScrollRange();
    }

    public void startFreshPage() {
        if (null != valueAnimator && valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofFloat(lineEndPointWidth, lineEndPointWidth * 4);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(10000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                endShadowLayerWidth = (Float) animation.getAnimatedValue();
                animValidate();
            }
        });
        valueAnimator.start();
    }

    public void stopFreshPage() {
        if (null != valueAnimator && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = null;
    }


    /**
     * 画文字
     *
     * @param canvas canvas
     */
    private void drawMaxAndMin(Canvas canvas) {
        if (!isLine) {
            //绘制最大值和最小值
            float x = (getX(mainMinIndex));
            float y = getMainY(mainLowMinValue);

            //计算显示位置

            y = y + maxMinTextHeight / 2 - maxMinTextDecent;
            String LowString;
            float stringWidth, screenMid = getX((screenRightIndex + screenLeftIndex) / 2);

            if (x < screenMid) {
                LowString = "── " + Float.toString(mainLowMinValue);
            } else {
                LowString = Float.toString(mainLowMinValue) + " ──";
                stringWidth = maxMinPaint.measureText(LowString);
                x -= stringWidth;
            }
            canvas.drawText(LowString, x, y, maxMinPaint);

            x = getX(mainMaxIndex);
            y = getMainY(mainHighMaxValue);
            String highString;
            y = y + maxMinTextHeight / 2 - maxMinTextDecent;
            if (x < screenMid) {
                highString = "── " + Float.toString(mainHighMaxValue);
            } else {
                highString = Float.toString(mainHighMaxValue) + " ──";
                stringWidth = maxMinPaint.measureText(highString);
                x -= stringWidth;
            }
            canvas.drawText(highString, x, y, maxMinPaint);

        }
    }

    /**
     * 画值
     *
     * @param canvas   canvas
     * @param position 显示某个点的值
     */
    private void drawValue(Canvas canvas, int position) {
        float x = 10;
        mainDraw.drawText(canvas, this, position, x, mainRect.top + baseLine - textHeight / 2);
        volDraw.drawText(canvas, this, position, x, mainRect.bottom + baseLine);
        if (null != childDraw) {
            childDraw.drawText(canvas, this, position, x, volRect.bottom + baseLine);
        }
    }

    /**
     * 格式化值
     */
    public String formatValue(float value) {
        if (null == getValueFormatter()) {
            setValueFormatter(new ValueFormatter());
        }
        return getValueFormatter().format(value);
    }

    /**
     * MA/BOLL切换及隐藏
     *
     * @param status MA/BOLL/NONE
     */
    public void changeMainDrawType(Status status) {
        if (this.status != status) {
            this.status = status;
            invalidate();
        }
    }

    public Status getStatus() {
        return this.status;
    }

    private Status status = Status.MA;

    private void calculateSelectedX(float x) {
        selectedIndex = indexOfTranslateX(xToTranslateX(x));
        if (selectedIndex > screenRightIndex) {
            selectedIndex = screenRightIndex;
        }
        if (selectedIndex < screenLeftIndex) {
            selectedIndex = screenLeftIndex;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        int lastIndex = selectedIndex;
        calculateSelectedX(e.getX());
        if (lastIndex != selectedIndex) {
            onSelectedChanged(this, getItem(selectedIndex), selectedIndex);
        }
        invalidate();
    }

    private float getMinTranslate() {
        float dataLength = getDataLength();
        if (dataLength >= width) {
            return -(dataLength - width);
        }
        return width - dataLength;
    }


    /**
     * 获取平移的最大值
     *
     * @return 最大值
     */
    private float getMaxTranslateX() {
        float dataLength = getDataLength();
        if (dataLength >= width) {
            return isLine ? 0 : chartItemWidth * getScaleX() / 2;
        }
        return width - dataLength + getmOverScrollRange() - (isLine ? 0 : chartItemWidth * getScaleX() / 2);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        setTranslatedX(canvasTranslateX + (l - oldl));
        if (isLine && getX(screenRightIndex) + canvasTranslateX <= width) {
            startFreshPage();
        } else {
            stopFreshPage();
        }
    }

    @Override
    protected void onScaleChanged(float scale, float oldScale) {
        //通过 放大和左右左右个数设置左移
        float tempWidth = chartItemWidth * scale;
        float newCount = (width / tempWidth);
        float oldCount = (width / chartItemWidth / oldScale);
        float difCount = (newCount - oldCount) / 2;
        setTranslatedX(canvasTranslateX / oldScale * scale + difCount * tempWidth);
        super.onScaleChanged(scale, oldScale);
    }


    public void setTranslatedX(float mTranslateX) {
        if (mTranslateX < getMinTranslate()) {
            mTranslateX = getMinTranslate();
        } else if (mTranslateX > getMaxTranslateX()) {
            mTranslateX = getMaxTranslateX();
        }
        this.canvasTranslateX = mTranslateX;
    }

    private void calculateValue() {
        if (!isLongPress()) {
            selectedIndex = -1;
        }
        float scaleWidth = chartItemWidth * scaleX;

        if (canvasTranslateX <= scaleWidth / 2) {
            screenLeftIndex = (int) ((-canvasTranslateX) / scaleWidth);
            screenRightIndex = (int) (screenLeftIndex + width / scaleWidth + 0.5) + 1;

        } else {
            screenLeftIndex = 0;
            screenRightIndex = itemsCount - 1;
        }
        if (screenRightIndex > itemsCount - 1) {
            screenRightIndex = itemsCount - 1;
        }
        mainMaxValue = Float.MIN_VALUE;
        mainMinValue = Float.MAX_VALUE;
        volMaxValue = Float.MIN_VALUE;
        Float mVolMinValue = Float.MAX_VALUE;
        childMaxValue = Float.MIN_VALUE;
        Float mChildMinValue = Float.MAX_VALUE;
        mainMaxIndex = screenLeftIndex;
        mainMinIndex = screenLeftIndex;
        mainHighMaxValue = Float.MIN_VALUE;
        mainLowMinValue = Float.MAX_VALUE;
        //为保证页面效果正常,右侧取值时取值时多取一个,在计算最大值时需要抛出

        int tempLeft = screenLeftIndex > 0 ? screenLeftIndex + 1 : 0;
//        int tempRight = screenRightIndex == (itemsCount - 1) ? itemsCount - 1 : screenRightIndex - 1;
        for (int i = tempLeft; i <= screenRightIndex; i++) {
            IKLine point = (IKLine) getItem(i);
            if (null == point) {
                continue;
            }

            mainMaxValue = Math.max(mainMaxValue, mainDraw.getMaxValue(point, status));
            mainMinValue = Math.min(mainMinValue, mainDraw.getMinValue(point, status));
            if (mainHighMaxValue != Math.max(mainHighMaxValue, point.getHighPrice())) {
                mainHighMaxValue = point.getHighPrice();
                mainMaxIndex = i;
            }
            if (mainLowMinValue != Math.min(mainLowMinValue, point.getLowPrice())) {
                mainLowMinValue = point.getLowPrice();
                mainMinIndex = i;
            }

            volMaxValue = Math.max(volMaxValue, volDraw.getMaxValue(point, status));
            mVolMinValue = Math.min(mVolMinValue, volDraw.getMinValue(point, status));

            if (null != childDraw) {
                childMaxValue = Math.max(childMaxValue, childDraw.getMaxValue(point, status));
                mChildMinValue = Math.min(mChildMinValue, childDraw.getMinValue(point, status));
            }
        }
        if (mainMaxValue != mainMinValue) {
            float padding = (mainMaxValue - mainMinValue) * 0.05f;
            mainMaxValue += padding;
            mainMinValue -= padding;
        } else {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mainMaxValue += Math.abs(mainMaxValue * 0.05f);
            mainMinValue -= Math.abs(mainMinValue * 0.05f);
            if (mainMaxValue == 0) {
                mainMaxValue = 1;
            }
        }

        if (Math.abs(volMaxValue) < 0.01) {
            volMaxValue = 15.00f;
        }

        if (Math.abs(childMaxValue) < 0.01 && Math.abs(mChildMinValue) < 0.01) {
            childMaxValue = 1f;
        } else if (childMaxValue.equals(mChildMinValue)) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            childMaxValue += Math.abs(childMaxValue * 0.05f);
            mChildMinValue -= Math.abs(mChildMinValue * 0.05f);
            if (childMaxValue == 0) {
                childMaxValue = 1f;
            }
        }

        if (5 == childDrawPosition) {
            childMaxValue = 0f;
            if (Math.abs(mChildMinValue) < 0.01)
                mChildMinValue = -10.00f;
        }
        mainScaleY = mainRect.height() * 1f / (mainMaxValue - mainMinValue);
        volScaleY = volRect.height() * 1f / (volMaxValue - mVolMinValue);
        if (null != childRect)
            childScaleY = childRect.height() * 1f / (childMaxValue - mChildMinValue);
        if (mAnimator.isRunning()) {
            float value = (float) mAnimator.getAnimatedValue();
            this.screenRightIndex = screenLeftIndex + Math.round(value * (this.screenRightIndex - screenLeftIndex));
        }
    }

    public int indexOfTranslateX(float translateX) {
        return (int) (translateX / chartItemWidth / getScaleX());
    }


    /**
     * 在主区域画线
     *
     * @param startX    开始点的横坐标
     * @param stopX     开始点的值
     * @param stopValue 结束点的值
     */
    public void drawLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(stopValue), paint);
    }


    public void drawEndMinutsLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX) {
        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(lastPrice), paint);
    }


    public void drawEndMinutsLineArea(Canvas canvas, Paint paint, float startX, float startValue, float stopX) {
        int y = displayHeight + topPadding + bottomPadding;
        LinearGradient linearGradient = new LinearGradient(startX, topPadding,
                stopX, y, areaTopColor, areaBottomColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        Path path = new Path();
        path.moveTo(startX, y);
        path.lineTo(startX, getMainY(startValue));
        path.lineTo(stopX, getMainY(lastPrice));
        path.lineTo(stopX, y);
        path.close();
        canvas.drawPath(path, paint);


    }

    private float endShadowLayerWidth = 20;

    /**
     * 在主区域画分时线
     *
     * @param startX    开始点的横坐标
     * @param stopX     开始点的值
     * @param stopValue 结束点的值
     */
    public void drawMinutsLineArea(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {

        int y = displayHeight + topPadding + bottomPadding;
        LinearGradient linearGradient = new LinearGradient(startX, topPadding,
                stopX, y, areaTopColor, areaBottomColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        float mainY = getMainY(stopValue);
        Path path = new Path();
        path.moveTo(startX, y);
        path.lineTo(startX, getMainY(startValue));
        path.lineTo(stopX, mainY);
        path.lineTo(stopX, y);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 在子区域画线
     *
     * @param startX     开始点的横坐标
     * @param startValue 开始点的值
     * @param stopX      结束点的横坐标
     * @param stopValue  结束点的值
     */
    public void drawChildLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getChildY(startValue), stopX, getChildY(stopValue), paint);
    }

    /**
     * 在子区域画线
     *
     * @param startX     开始点的横坐标
     * @param startValue 开始点的值
     * @param stopX      结束点的横坐标
     * @param stopValue  结束点的值
     */
    public void drawVolLine(Canvas canvas, Paint paint, float startX, float startValue, float stopX, float stopValue) {
        canvas.drawLine(startX, getVolY(startValue), stopX, getVolY(stopValue), paint);
    }

    /**
     * 根据索引获取实体
     *
     * @param position 索引值
     * @return 实体
     */
    public ICandle getItem(int position) {
        if (null != dataAdapter) {
            return dataAdapter.getItem(position);
        } else {
            return null;
        }
    }

    /**
     * 画板上的坐标点
     *
     * @param position 索引值
     * @return x坐标
     */
    public float getX(int position) {
        return position * chartItemWidth * scaleX;
    }

    /**
     * 获取适配器
     *
     * @return 获取适配器
     */
    public IAdapter<ICandle> getAdapter() {
        return dataAdapter;
    }

    /**
     * 设置当前子图
     *
     * @param position position
     */
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public void setChildDraw(int position) {
        if (childDrawPosition != position) {
            childDrawPosition = position;
            childDraw = mChildDraws.get(position);
            initRect();
            invalidate();
        }
    }

    /**
     * 隐藏子图
     */
    public void hideChildDraw() {
        childDrawPosition = -1;
        childDraw = null;
        initRect();
        invalidate();
    }

    /**
     * 给子区域添加画图方法
     *
     * @param childDraw IChartDraw
     */
    public void addChildDraw(IChartDraw childDraw) {
        mChildDraws.add(childDraw);
    }

    /**
     * 获取ValueFormatter
     *
     * @return IValueFormatter
     */
    public IValueFormatter getValueFormatter() {
        return mValueFormatter;
    }

    /**
     * 设置ValueFormatter
     *
     * @param valueFormatter value格式化器
     */
    public void setValueFormatter(IValueFormatter valueFormatter) {
        this.mValueFormatter = valueFormatter;
    }

    /**
     * 获取DatetimeFormatter
     *
     * @return 时间格式化器
     */
    public IDateTimeFormatter getDateTimeFormatter() {
        return mDateTimeFormatter;
    }

    /**
     * 设置dateTimeFormatter
     *
     * @param dateTimeFormatter 时间格式化器
     */
    public void setDateTimeFormatter(IDateTimeFormatter dateTimeFormatter) {
        mDateTimeFormatter = dateTimeFormatter;
    }

    /**
     * 格式化时间
     *
     * @param date date
     */
    @SuppressWarnings("unused")
    public String formatDateTime(Date date) {
        if (null == getDateTimeFormatter()) {
            setDateTimeFormatter(new TimeFormatter());
        }
        return getDateTimeFormatter().format(date);
    }

    /**
     * 获取主区域的 IChartDraw
     *
     * @return IChartDraw
     */
    @SuppressWarnings("unused")
    public IChartDraw<ICandle> getMainDraw() {
        return mainDraw;
    }

    /**
     * 设置主区域的 IChartDraw
     *
     * @param mainDraw IChartDraw
     */
    public void setMainDraw(IChartDraw<ICandle> mainDraw) {
        this.mainDraw = mainDraw;
    }

    @SuppressWarnings("unused")
    public IChartDraw<ICandle> getVolDraw() {
        return volDraw;
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public void setVolDraw(IChartDraw mVolDraw) {
        this.volDraw = mVolDraw;
    }


    /**
     * 设置数据适配器
     */
    public void setAdapter(IAdapter<ICandle> adapter) {
        if (null != dataAdapter && null != dataSetObserver) {
            dataAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        dataAdapter = adapter;
        if (null == dataAdapter || null == dataSetObserver) {
            itemsCount = 0;
            return;

        }
        dataAdapter.registerDataSetObserver(dataSetObserver);
        if (dataAdapter.getCount() > 0) {
            dataSetObserver.onChanged();
        }
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (null != mAnimator) {
            mAnimator.start();
        }
    }

    /**
     * 设置动画时间
     */
    @SuppressWarnings("unused")
    public void setAnimationDuration(long duration) {
        if (null != mAnimator) {
            mAnimator.setDuration(duration);
        }
    }

    /**
     * 设置表格行数
     */
    public void setGridRows(int gridRows) {
        if (gridRows < 1) {
            gridRows = 1;
        }
        this.gridRows = gridRows;
    }

    /**
     * 设置表格列数
     */
    public void setGridColumns(int gridColumns) {
        if (gridColumns < 1) {
            gridColumns = 1;
        }
        this.gridColumns = gridColumns;
    }

    /**
     * view中的x转化为TranslateX
     *
     * @param x x
     * @return translateX
     */
    public float xToTranslateX(float x) {
        return -canvasTranslateX + x;
    }

    /**
     * translateX转化为view中的x
     *
     * @param translateX translateX
     * @return x
     */
    public float translateXtoX(float translateX) {
        return translateX + canvasTranslateX;
    }

    /**
     * 获取上方padding
     */
    public float getTopPadding() {
        return topPadding;
    }

    /**
     * 获取上方padding
     */
    @SuppressWarnings("unused")
    public float getChildPadding() {
        return childPadding;
    }

    /**
     * 获取子试图上方padding
     */
    @SuppressWarnings("unused")
    public float getmChildScaleYPadding() {
        return childPadding;
    }

    /**
     * 获取图的宽度
     *
     * @return 宽度
     */
    public int getChartWidth() {
        return width;
    }

    /**
     * 是否长按
     */
    public boolean isLongPress() {
        return isLongPress;
    }

    /**
     * 获取选择索引
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }


    public Rect getVolRect() {
        return volRect;
    }

    /**
     * 设置选择监听
     */
    @SuppressWarnings("unused")
    public void setOnSelectedChangedListener(OnSelectedChangedListener l) {
        this.mOnSelectedChangedListener = l;
    }

    public void onSelectedChanged(BaseKLineChartView view, Object point, int index) {
        if (null != this.mOnSelectedChangedListener) {
            mOnSelectedChangedListener.onSelectedChanged(view, point, index);
        }
    }

    /**
     * 设置超出右方后可滑动的范围
     */
    public void setOverScrollRange(float overScrollRange) {
        if (overScrollRange < 0) {
            overScrollRange = 0;
        }
        mOverScrollRange = overScrollRange;
        setScrollX((int) -overScrollRange);
    }


    /**
     * 设置上方padding
     *
     * @param topPadding topPadding
     */
    @SuppressWarnings("unused")
    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    /**
     * 设置下方padding
     *
     * @param bottomPadding bottomPadding
     */
    @SuppressWarnings("unused")
    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    /**
     * 设置表格线宽度
     */
    public void setGridLineWidth(float width) {
        gridPaint.setStrokeWidth(width);
    }

    /**
     * 设置表格线颜色
     */
    public void setGridLineColor(int color) {
        gridPaint.setColor(color);
    }

    /**
     * 设置选择器横线宽度
     */
    public void setSelectedXLineWidth(float width) {
        selectedXLinePaint.setStrokeWidth(width);
    }

    /**
     * 设置选择器横线颜色
     */
    public void setSelectedXLineColor(int color) {
        selectedXLinePaint.setColor(color);
    }

    /**
     * 设置选择器竖线宽度
     */
    public void setSelectedYLineWidth(float width) {
        selectedWidth = width;
    }

    /**
     * 设置选择器竖线颜色
     */
    public void setSelectedYLineColor(int color) {
        selectedYLinePaint.setColor(color);
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
        selectedYColor = color;
    }

    float textHeight;
    float baseLine;
    float textDecent;

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        textPaint.setTextSize(textSize);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        textHeight = fm.descent - fm.ascent;
        textDecent = fm.descent;
        baseLine = (textHeight - fm.bottom - fm.top) / 2;

        priceLineBoxRightPaint.setTextSize(textSize);
    }

    public void setPriceLineColor(int color) {
        priceLinePaint.setColor(color);
    }

    /**
     * 设置最大值/最小值文字颜色
     */
    public void setMTextColor(int color) {
        maxMinPaint.setColor(color);
    }

    private float maxMinTextHeight;
    private float maxMinTextDecent;

    /**
     * 设置最大值/最小值文字大小
     */
    public void setMTextSize(float textSize) {
        maxMinPaint.setTextSize(textSize);
        Paint.FontMetrics fm = maxMinPaint.getFontMetrics();
        maxMinTextHeight = fm.descent - fm.ascent;
        maxMinTextDecent = fm.descent;

    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        this.backgroundPaint.setColor(color);
    }

    /**
     * 设置选中point 值显示背景
     */
    public void setSelectPointColor(int color) {
        selectedPointPaint.setColor(color);
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public float getLastVol() {
        return lastVol;
    }

    /**
     * 选中点变化时的监听
     */
    public interface OnSelectedChangedListener {
        /**
         * 当选点中变化时
         *
         * @param view  当前view
         * @param point 选中的点
         * @param index 选中点的索引
         */
        void onSelectedChanged(BaseKLineChartView view, Object point, int index);
    }

    public void setPriceLineWidth(float lineWidth) {
        priceLinePaint.setStrokeWidth(lineWidth);
        priceLinePaint.setStyle(Paint.Style.STROKE);
    }

    public void setPriceLineRightColor(int color) {
        priceLineBoxRightPaint.setColor(color);
    }

    public float getmOverScrollRange() {
        return mOverScrollRange;
    }

    /**
     * 设置每个点的宽度
     */
    public void setChartItemWidth(float pointWidth) {
        chartItemWidth = pointWidth;
    }

    public float getChartItemWidth() {
        return chartItemWidth;
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setEndPointColor(int color) {
        lineEndPointPaint.setColor(color);
        lineEndFillPointPaint.setColor(color);
    }

    public void setLineEndPointWidth(float width) {
        this.lineEndPointWidth = width;
    }


    /**
     * 执行一个动画变换
     *
     * @param start    start
     * @param end      end
     * @param listener listener
     * @return ValueAnimator
     */
    @SuppressWarnings("all")
    public ValueAnimator generaterAnimator(Float start, float end, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(listener);
        animator.start();
        return animator;
    }

    public void setShowLine(boolean isLine) {
        if (isLine && getX(screenRightIndex) + canvasTranslateX <= width) {
            startFreshPage();
        } else {
            stopFreshPage();
        }
        this.isLine = isLine;
        setTranslatedX(getMinTranslate());
        invalidate();
    }

    public boolean isLine() {
        return isLine;
    }

    /**
     * 设置价格框离右边的距离
     *
     * @param priceBoxMarginRight priceBoxMarginRight
     */
    @SuppressWarnings("unused")
    public void setPriceBoxMarginRight(float priceBoxMarginRight) {
        this.priceBoxMarginRight = priceBoxMarginRight;
    }

    /**
     * 设置价格框高度
     *
     * @param priceLineBoxHeight priceLineBoxHeight
     */
    @SuppressWarnings("unused")
    public void setPriceLineBoxHeight(int priceLineBoxHeight) {
        this.priceLineBoxHeight = priceLineBoxHeight;
    }

    /**
     * 设置选中框前面的文本
     *
     * @param marketInfoText 默认中文
     */
    @SuppressWarnings("unused")
    public void setMarketInfoText(String[] marketInfoText) {
        ((MainDraw) mainDraw).setMarketInfoText(marketInfoText);
    }

    /**
     * 设置价格框背景色
     *
     * @param color default black
     */
    @SuppressWarnings("unused")
    public void setPriceBoxBgColor(int color) {
        priceLineBoxBgPaint.setColor(color);
    }

    /**
     * 设置选中点的颜色
     *
     * @param color default wihte
     */
    @SuppressWarnings("unused")
    public void setSelectCrossColor(int color) {
        selectedCrossPaint.setColor(color);
    }

    /**
     * 设置选中点外圆颜色
     *
     * @param color default wihte
     */
    @SuppressWarnings("unused")
    public void setSelectCrossBigColor(int color) {
        selectedbigCrossPaint.setColor(color);
    }

    /**
     * 设置价格框边框颜色
     *
     * @param color default wihte
     */
    @SuppressWarnings("unused")
    public void setPriceBoxBorderColor(int color) {
        priceLineBoxPaint.setColor(color);
    }

    /**
     * 设置价格框边框宽度
     *
     * @param width default 1
     */
    @SuppressWarnings("unused")
    public void setPriceBoxBorderWidth(int width) {
        priceLineBoxPaint.setStrokeWidth(width);
    }

    /**
     * 设置圆点半径
     *
     * @param radius
     */
    public void setSelectedPointRadius(float radius) {
        selectedPointRadius = radius;
    }


    public void setSelectedYColor(int color) {
        this.selectedYColor = color;
    }

    public void setBackgroundStartColor(int color) {
        this.backGroundTopColor = color;
    }

    public void setBackgroundEmdColor(int color) {
        this.backGroundBottomColor = color;
    }

    public void setUpColor(int color) {
        ((MainDraw) mainDraw).setUpColor(color);

    }

    public void setDownColor(int color) {
        ((MainDraw) mainDraw).setDownColor(color);
    }

    public void setMinuteLineColor(int color) {
        ((MainDraw) mainDraw).setMinuteLineColor(color);
        ((VolumeDraw) volDraw).setMinuteColor(color);
    }

    public void setAreaTopColor(int areaTopColor) {
        this.areaTopColor = areaTopColor;
    }

    public void setAreaBottomColor(int areaBottomColor) {
        this.areaBottomColor = areaBottomColor;
    }
}
