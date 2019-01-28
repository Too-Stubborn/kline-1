package com.icechao.klinelib.draw;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.icechao.klinelib.base.BaseKLineChartView;
import com.icechao.klinelib.base.IChartDraw;
import com.icechao.klinelib.base.IValueFormatter;
import com.icechao.klinelib.entity.ICandle;
import com.icechao.klinelib.formatter.ValueFormatter;
import com.icechao.klinelib.utils.ViewUtil;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : MainDraw.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class MainDraw implements IChartDraw<ICandle> {

    private int itemCount;
    private final float padding;
    private final float margin;
    private String[] strings = new String[8];

    public void setItemCount(int mItemCount) {
        itemCount = mItemCount;
    }

    private float candleWidth = 0;
    private Paint lineAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint upPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint upLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint downPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint downLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private Paint indexPaintOne = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint indexPaintTwo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint indexPaintThree = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint selectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String[] marketInfoText = new String[8];

    public MainDraw(Context context) {
        selectorBorderPaint.setStyle(Paint.Style.STROKE);
        upPaint.setStyle(Paint.Style.FILL);
        upLinePaint.setStyle(Paint.Style.STROKE);
        upLinePaint.setAntiAlias(true);
        downPaint.setStyle(Paint.Style.FILL);
        downLinePaint.setStyle(Paint.Style.STROKE);
        downLinePaint.setAntiAlias(true);
        padding = ViewUtil.Dp2Px(context, 5);
        margin = ViewUtil.Dp2Px(context, 5);
        marketInfoText[0] = ("时间   ");
        marketInfoText[1] = ("开     ");
        marketInfoText[2] = ("高     ");
        marketInfoText[3] = ("低     ");
        marketInfoText[4] = ("收     ");
        marketInfoText[5] = ("涨跌额  ");
        marketInfoText[6] = ("涨跌幅  ");
        marketInfoText[7] = ("成交量  ");
    }


    @Override
    @SuppressWarnings("all")
    public void drawTranslated(@Nullable ICandle lastPoint, @NonNull ICandle curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {

        if (view.isLine()) {
            if (position == itemCount - 1) {
                float lastClosePrice = lastPoint.getClosePrice();
                view.drawEndMinutsLine(canvas, linePaint, lastX, lastClosePrice, curX);
                view.drawEndMinutsLineArea(canvas, lineAreaPaint, lastX, lastClosePrice, curX);

            } else {
                float lastClosePrice = lastPoint.getClosePrice();
                float closePrice = curPoint.getClosePrice();
                view.drawLine(canvas, linePaint, lastX, lastClosePrice, curX, closePrice);
                view.drawMinutsLineArea(canvas, lineAreaPaint, lastX, lastClosePrice, curX, closePrice);
            }

        } else {
            drawCandle(view, canvas, curX, curPoint.getHighPrice(), curPoint.getLowPrice(), curPoint.getOpenPrice(), curPoint.getClosePrice(), position);
            Status status = view.getStatus();
            if (status == Status.MA) {
                //画第一根ma
                if (0 != lastPoint.getMaOne()) {
                    if (itemCount - 1 == position && 0 != this.maOne) {
                        view.drawLine(canvas, indexPaintOne, lastX, lastPoint.getMaOne(), curX, this.maOne);
                    } else {
                        view.drawLine(canvas, indexPaintOne, lastX, lastPoint.getMaOne(), curX, curPoint.getMaOne());
                    }
                }
                //画第二根ma
                if (0 != lastPoint.getMaTwo()) {
                    if (itemCount - 1 == position && 0 != this.maTwo) {
                        view.drawLine(canvas, indexPaintTwo, lastX, lastPoint.getMaTwo(), curX, this.maTwo);
                    } else {
                        view.drawLine(canvas, indexPaintTwo, lastX, lastPoint.getMaTwo(), curX, curPoint.getMaTwo());
                    }
                }
                //画第三根ma
                if (0 != lastPoint.getMaThree()) {
                    if (itemCount - 1 == position && 0 != this.maThree) {
                        view.drawLine(canvas, indexPaintThree, lastX, lastPoint.getMaThree(), curX, this.maThree);
                    } else {
                        view.drawLine(canvas, indexPaintThree, lastX, lastPoint.getMaThree(), curX, curPoint.getMaThree());
                    }
                }
            } else if (status == Status.BOLL) {
                //画boll
                if (0 != lastPoint.getUp()) {
                    if (itemCount - 1 == position && 0 != bollUp) {
                        view.drawLine(canvas, indexPaintOne, lastX, lastPoint.getUp(), curX, bollUp);
                    } else {
                        view.drawLine(canvas, indexPaintOne, lastX, lastPoint.getUp(), curX, curPoint.getUp());
                    }
                }
                if (0 != lastPoint.getMb()) {
                    if (itemCount - 1 == position && 0 != bollMb) {
                        view.drawLine(canvas, indexPaintOne, lastX, lastPoint.getMb(), curX, bollMb);
                    } else {
                        view.drawLine(canvas, indexPaintTwo, lastX, lastPoint.getMb(), curX, curPoint.getMb());
                    }
                }
                if (0 != lastPoint.getDn()) {
                    if (itemCount - 1 == position && 0 != bollDn) {
                        view.drawLine(canvas, indexPaintOne, lastX, lastPoint.getDn(), curX, bollDn);
                    } else {
                        view.drawLine(canvas, indexPaintThree, lastX, lastPoint.getDn(), curX, curPoint.getDn());
                    }
                }
            }
        }
    }


    private float maOne;
    private float maTwo;
    private float maThree;

    private float bollUp;
    private float bollMb;
    private float bollDn;


    @Override
    @SuppressWarnings("all")
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {

        //修改头文字显示在顶部
        y = maTextHeight;
        if (view.isLine()) {

        } else {
            y += 10;
            Status status = view.getStatus();
            ICandle point = view.getItem(position);
            if (status == Status.MA) {
                String text;
                if (0 != point.getMaOne()) {
                    text = "MA5:" + view.formatValue(point.getMaOne()) + "  ";
                    canvas.drawText(text, x, y, indexPaintOne);
                    x += indexPaintOne.measureText(text);
                }
                if (0 != point.getMaTwo()) {
                    text = "MA10:" + view.formatValue(point.getMaTwo()) + "  ";
                    canvas.drawText(text, x, y, indexPaintTwo);
                    x += indexPaintTwo.measureText(text);
                }
                if (0 != point.getMaThree()) {
                    text = "MA30:" + view.formatValue(point.getBollMa());
                    canvas.drawText(text, x, y, indexPaintThree);
                }
            } else if (status == Status.BOLL) {
                if (0 != point.getMb()) {
                    String text = "BOLL:" + view.formatValue(point.getMb()) + "  ";
                    canvas.drawText(text, x, y, indexPaintTwo);
                    x += indexPaintOne.measureText(text);
                    text = "UB:" + view.formatValue(point.getUp()) + "  ";
                    canvas.drawText(text, x, y, indexPaintOne);
                    x += indexPaintTwo.measureText(text);
                    text = "LB:" + view.formatValue(point.getDn());
                    canvas.drawText(text, x, y, indexPaintThree);
                }
            }
        }
        if (view.isLongPress()) {
            drawSelector(view, canvas);
        }
    }

    @Override
    public float getMaxValue(ICandle point, Status status) {
        if (status != Status.BOLL) {
            return Math.max(point.getHighPrice(), point.getBollMa());
        }
        if (Float.isNaN(point.getUp())) {
            if (point.getMb() == 0) {
                return point.getHighPrice();
            } else {
                return point.getMb();
            }
        } else if (point.getUp() == 0) {
            return point.getHighPrice();
        } else {
            return point.getUp();
        }
    }

    @Override
    public float getMinValue(ICandle point, Status status) {
        if (status == Status.BOLL) {
            if (point.getDn() == 0) {
                return point.getLowPrice();
            } else {
                return point.getDn();
            }
        } else {
            if (point.getBollMa() == 0f) {
                return point.getLowPrice();
            } else {
                return Math.min(point.getBollMa(), point.getLowPrice());
            }
        }
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 画Candle
     *
     * @param canvas canvas
     * @param x      x轴坐标
     * @param high   最高价
     * @param low    最低价
     * @param open   开盘价
     * @param close  收盘价
     */
    private void drawCandle(BaseKLineChartView view, Canvas canvas, float x, float high, float low, float open, float close, int position) {
        high = view.getMainY(high);
        low = view.getMainY(low);
        open = view.getMainY(open);
        if (position == itemCount - 1) {
            close = view.getMainY(view.getLastPrice());
        } else {
            close = view.getMainY(close);
        }
        float r = candleWidth / 2 * view.getScaleX();
        float cancleLeft = x - r;
        float candleright = x + r;
        Path path = new Path();

        if (open > close) {
            path.moveTo(x, high);
            path.lineTo(x, open);
            path.moveTo(x, close);
            path.lineTo(x, low);
            canvas.drawRect(cancleLeft, close, candleright, open, upPaint);
            canvas.drawPath(path, upLinePaint);
        } else if (open < close) {
            path.moveTo(x, high);
            path.lineTo(x, close);
            path.moveTo(x, open);
            path.lineTo(x, low);
            canvas.drawRect(cancleLeft, open, candleright, close, downPaint);
            canvas.drawPath(path, downLinePaint);
        } else {

            path.moveTo(x, high);
            path.lineTo(x, low);
            canvas.drawRect(cancleLeft, open, candleright, close + 1, upPaint);
            canvas.drawPath(path, upLinePaint);
        }
    }

    /**
     * draw选择器
     *
     * @param view   view
     * @param canvas canvas
     */
    @SuppressLint("DefaultLocale")
    private void drawSelector(BaseKLineChartView view, Canvas canvas) {

        int index = view.getSelectedIndex();

        ICandle point = view.getItem(index);
        strings[0] = String.valueOf(view.getAdapter().getDate(index));
        strings[1] = (String.valueOf(point.getOpenPrice()));
        strings[2] = (String.valueOf(point.getHighPrice()));
        strings[3] = (String.valueOf(point.getLowPrice()));
        strings[4] = (String.valueOf(point.getClosePrice()));
        float tempDiffPrice = point.getOpenPrice() - point.getClosePrice();
        strings[5] = (String.valueOf(tempDiffPrice));
        strings[6] = (String.format("%.2f", (tempDiffPrice) / point.getOpenPrice()) + "%");
        strings[7] = (String.valueOf(point.getVolume()));

        float width = 0, left, top = margin + view.getTopPadding();
        //上下多加两个padding值的间隙
        int length = strings.length;
        float height = padding * ((length - 1) + 4) + selectedTextHeight * length;
        for (int i = 0; i < length; i++) {
            String tempString = marketInfoText[i] + strings[i];
            width = Math.max(width, selectorTextPaint.measureText(tempString));
        }
        width += padding * 2;

        float x = view.translateXtoX(view.getX(index));
        if (x > view.getChartWidth() / 2) {
            left = margin;
        } else {
            left = view.getChartWidth() - width - margin;
        }

        float right = left + width;
        RectF r = new RectF(left, top, right, top + height);
        canvas.drawRoundRect(r, padding, padding, selectorBackgroundPaint);
        canvas.drawRoundRect(r, padding, padding, selectorBorderPaint);
        float y = top + padding * 2 + selectedTextBaseLine;
        float tempX = right - padding;
        for (int i = 0; i < length; i++) {
            String s = strings[i];
            canvas.drawText(marketInfoText[i], left + padding, y, selectorTextPaint);
            if (i == 5 || i == 6) {
                if (tempDiffPrice >= 0) {
                    canvas.drawText(s, tempX - selectorTextPaint.measureText(s), y, upPaint);
                } else {
                    canvas.drawText(s, tempX - selectorTextPaint.measureText(s), y, downPaint);
                }
            } else {
                canvas.drawText(s, tempX - selectorTextPaint.measureText(s), y, selectorTextPaint);
            }
            y += selectedTextHeight + padding;
        }

    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth candle width
     */
    public void setCandleWidth(float candleWidth) {
        this.candleWidth = candleWidth;
    }

    /**
     * 设置蜡烛线宽度
     *
     * @param candleLineWidth lineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        downLinePaint.setStrokeWidth(candleLineWidth);
        upLinePaint.setStrokeWidth(candleLineWidth);
    }

    /**
     * 设置ma1颜色
     *
     * @param color color
     */
    public void setMaOneColor(int color) {
        this.indexPaintOne.setColor(color);
    }

    /**
     * 设置ma2颜色
     *
     * @param color color
     */
    public void setMaTwoColor(int color) {
        this.indexPaintTwo.setColor(color);
    }

    /**
     * 设置ma3颜色
     *
     * @param color color
     */
    public void setMaThreeColor(int color) {
        this.indexPaintThree.setColor(color);
    }

    /**
     * 设置选择器文字颜色
     *
     * @param color color
     */
    public void setSelectorTextColor(int color) {
        selectorTextPaint.setColor(color);
        selectorBorderPaint.setColor(color);
    }

    private float selectedTextHeight;
    private float selectedTextBaseLine;

    /**
     * 设置选择器文字大小
     *
     * @param textSize textsize
     */
    public void setSelectorTextSize(float textSize) {
        selectorTextPaint.setTextSize(textSize);
        downPaint.setTextSize(textSize);
        upPaint.setTextSize(textSize);
        Paint.FontMetrics metrics = selectorTextPaint.getFontMetrics();
        selectedTextHeight = metrics.descent - metrics.ascent;
        selectedTextBaseLine = (selectedTextHeight - metrics.bottom - metrics.top) / 2;

    }

    /**
     * 设置选择器背景
     *
     * @param color color
     */
    public void setSelectorBackgroundColor(int color) {
        selectorBackgroundPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        indexPaintThree.setStrokeWidth(width);
        indexPaintTwo.setStrokeWidth(width);
        indexPaintOne.setStrokeWidth(width);
        linePaint.setStrokeWidth(width);
        selectorBorderPaint.setStrokeWidth(width);

    }

    private float maTextHeight;

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        indexPaintThree.setTextSize(textSize);
        indexPaintTwo.setTextSize(textSize);
        indexPaintOne.setTextSize(textSize);
        Paint.FontMetrics metrics = indexPaintOne.getFontMetrics();
        maTextHeight = metrics.descent - metrics.ascent;
    }


    @Override
    public void startAnim(ICandle item, BaseKLineChartView view) {
        Status status = view.getStatus();
        if (maOne == 0) {
            maOne = item.getMaOne();
            maTwo = item.getMaTwo();
            maThree = item.getMaThree();
            bollUp = item.getUp();
            bollDn = item.getDn();
            bollMb = item.getMb();
            return;
        }
        if (status == Status.MA) {
            view.generaterAnimator(maOne, item.getMaOne(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    maOne = (float) animation.getAnimatedValue();
                }
            });
            view.generaterAnimator(maTwo, item.getMaTwo(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    maTwo = (float) animation.getAnimatedValue();
                }
            });
            view.generaterAnimator(maThree, item.getMaThree(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    maThree = (float) animation.getAnimatedValue();
                }
            });
        } else if (status == Status.BOLL) {
            view.generaterAnimator(bollMb, item.getMb(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bollMb = (float) animation.getAnimatedValue();
                }
            });
            view.generaterAnimator(bollDn, item.getDn(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bollDn = (float) animation.getAnimatedValue();
                }
            });
            view.generaterAnimator(bollUp, item.getUp(), new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bollUp = (float) animation.getAnimatedValue();
                }
            });
        }
    }

    public void setMarketInfoText(String[] marketInfoText) {
        this.marketInfoText = marketInfoText;
    }

    public void setStroke(boolean isStroke) {
        if (isStroke) {
            upPaint.setStyle(Paint.Style.STROKE);
            downPaint.setStyle(Paint.Style.STROKE);
        } else {
            upPaint.setStyle(Paint.Style.FILL);
            downPaint.setStyle(Paint.Style.FILL);
        }
    }


    public void setUpColor(int color) {
        upPaint.setColor(color);
        upLinePaint.setColor(color);

    }

    public void setDownColor(int color) {
        downPaint.setColor(color);
        downLinePaint.setColor(color);
    }

    public void setMinuteLineColor(int color) {
        linePaint.setColor(color);
    }
}
