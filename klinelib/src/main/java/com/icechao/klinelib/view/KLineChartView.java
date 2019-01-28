package com.icechao.klinelib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.icechao.klinelib.R;
import com.icechao.klinelib.base.BaseKLineChartView;
import com.icechao.klinelib.draw.KDJDraw;
import com.icechao.klinelib.draw.MACDDraw;
import com.icechao.klinelib.draw.MainDraw;
import com.icechao.klinelib.draw.RSIDraw;
import com.icechao.klinelib.draw.VolumeDraw;
import com.icechao.klinelib.draw.WRDraw;
import com.icechao.klinelib.utils.ViewUtil;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : KLineChartView.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class KLineChartView extends BaseKLineChartView {

    ProgressBar mProgressBar;
    private boolean isRefreshing = false;
    private boolean isLoadMoreEnd = false;
    private boolean mLastScrollEnable;
    private boolean mLastScaleEnable;

    private KChartRefreshListener mRefreshListener;

    private MACDDraw mMACDDraw;
    private RSIDraw mRSIDraw;
    private MainDraw mMainDraw;
    private KDJDraw mKDJDraw;
    private WRDraw mWRDraw;
    private VolumeDraw mVolumeDraw;


    public KLineChartView(Context context) {
        this(context, null);
        initView(context);
    }

    public KLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
        initAttrs(attrs);
    }

    public KLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(attrs);
    }

    private void initView(Context context) {

        mVolumeDraw = new VolumeDraw(context);
        mMACDDraw = new MACDDraw(context, getResources().getColor(R.color.chart_up), getResources().getColor(R.color.chart_down));
        mWRDraw = new WRDraw(context);
        mKDJDraw = new KDJDraw(context);
        mRSIDraw = new RSIDraw(context);
        mMainDraw = new MainDraw(context);
        addChildDraw(mMACDDraw);
        addChildDraw(mKDJDraw);
        addChildDraw(mRSIDraw);
        addChildDraw(mWRDraw);
        setVolDraw(mVolumeDraw);
        setMainDraw(mMainDraw);


        mVolumeDraw.setVolLeftColor(getResources().getColor(R.color.chart_text));
        setPriceLineColor(getResources().getColor(R.color.chart_text));
        setPriceLineWidth(ViewUtil.Dp2Px(context, 1));
        setPriceLineRightColor(getResources().getColor(R.color.right_index));
        setSelectCrossBigColor(getResources().getColor(R.color.cross_big_color));
        setSelectCrossColor(getResources().getColor(R.color.cross_color));
        setSelectedYColor(getResources().getColor(R.color.cross_color));

        setUpColor(getResources().getColor(R.color.chart_up));
        setDownColor(getResources().getColor(R.color.chart_down));
        setMinuteLineColor(getResources().getColor(R.color.chart_minute_line));


        setAreaTopColor(getResources().getColor(R.color.chart_line_start));
        setAreaBottomColor(getResources().getColor(R.color.chart_line_end));


        //背景添加渐变色
        setBackgroundStartColor(getResources().getColor(R.color.kline_bg_start));
        setBackgroundEmdColor(getResources().getColor(R.color.info_kline_bg_end));

        setEndPointColor(Color.WHITE);
        setLineEndPointWidth(ViewUtil.Dp2Px(context, 4));

        mProgressBar = new ProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewUtil.Dp2Px(context, 50), ViewUtil.Dp2Px(context, 50));
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(mProgressBar, layoutParams);
        mProgressBar.setVisibility(GONE);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.KLineChartView);
        if (null != array) {
            try {
                //public
                setChartItemWidth(array.getDimension(R.styleable.KLineChartView_kc_point_width, getDimension(R.dimen.chart_point_width)));
                setTextSize(array.getDimension(R.styleable.KLineChartView_kc_text_size, getDimension(R.dimen.chart_text_size)));
                setTextColor(array.getColor(R.styleable.KLineChartView_kc_text_color, getColor(R.color.chart_text)));
                setMTextSize(array.getDimension(R.styleable.KLineChartView_kc_text_size, getDimension(R.dimen.chart_text_size)));
                setMTextColor(array.getColor(R.styleable.KLineChartView_kc_text_color, getColor(R.color.chart_text)));
                setLineWidth(array.getDimension(R.styleable.KLineChartView_kc_line_width, getDimension(R.dimen.chart_line_width)));
//                setBackgroundColor(array.getColor(R.styleable.KLineChartView_kc_background_color, getColor(R.color.chart_bac)));
                setSelectPointColor(array.getColor(R.styleable.KLineChartView_kc_background_color, getColor(R.color.chart_point_selected_price_bg)));

                setSelectedXLineColor(getResources().getColor(R.color.cross_color));
                setSelectedXLineWidth(getDimension(R.dimen.chart_line_width));

                setSelectedYLineColor(getResources().getColor(R.color.cross_vertical_color));
                setSelectedYLineWidth(getDimension(R.dimen.chart_point_width));

                setGridLineWidth(array.getDimension(R.styleable.KLineChartView_kc_grid_line_width, getDimension(R.dimen.chart_grid_line_width)));
                setGridLineColor(array.getColor(R.styleable.KLineChartView_kc_grid_line_color, getColor(R.color.chart_grid_line)));
                //macd
                setMACDWidth(array.getDimension(R.styleable.KLineChartView_kc_macd_width, getDimension(R.dimen.chart_candle_width)));
                setDIFColor(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setDEAColor(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setMACDColor(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                //kdj
                setKColor(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setDColor(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setJColor(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                //wr
                setR1Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setR2Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma10)));
                setR3Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma30)));
                //rsi
                setRSI1Color(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setRSI2Color(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setRSI3Color(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                //main
                setMaOneColor(array.getColor(R.styleable.KLineChartView_kc_dif_color, getColor(R.color.chart_ma5)));
                setMaTwoColor(array.getColor(R.styleable.KLineChartView_kc_dea_color, getColor(R.color.chart_ma10)));
                setMaThreeColor(array.getColor(R.styleable.KLineChartView_kc_macd_color, getColor(R.color.chart_ma30)));
                setCandleWidth(array.getDimension(R.styleable.KLineChartView_kc_candle_width, getDimension(R.dimen.chart_candle_width)));
                setCandleLineWidth(array.getDimension(R.styleable.KLineChartView_kc_candle_line_width, getDimension(R.dimen.chart_candle_line_width)));
                setSelectorBackgroundColor(array.getColor(R.styleable.KLineChartView_kc_selector_background_color, getColor(R.color.chart_selector)));
                setSelectorTextSize(array.getDimension(R.styleable.KLineChartView_kc_selector_text_size, getDimension(R.dimen.chart_selector_text_size)));
                setCandleSolid(array.getBoolean(R.styleable.KLineChartView_kc_candle_solid, false));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                array.recycle();
            }
        }
    }

    private float getDimension(@DimenRes int resId) {
        return getResources().getDimension(resId);
    }

    private int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    @Override
    public void onLeftSide() {
        showLoading();
    }

    @Override
    public void onRightSide() {
    }

    public void showLoading() {
        if (!isLoadMoreEnd && !isRefreshing) {
            isRefreshing = true;
            if (null != mProgressBar) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (null != mRefreshListener) {
                mRefreshListener.onLoadMoreBegin(this);
            }
            mLastScaleEnable = isScaleEnable();
            mLastScrollEnable = isScrollEnable();
            super.setScrollEnable(false);
            super.setScaleEnable(false);
        }
    }

    public void justShowLoading() {
        if (!isRefreshing) {
            isLongPress = false;
            isRefreshing = true;
            if (null != mProgressBar) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (null != mRefreshListener) {
                mRefreshListener.onLoadMoreBegin(this);
            }
            mLastScaleEnable = isScaleEnable();
            mLastScrollEnable = isScrollEnable();
            super.setScrollEnable(false);
            super.setScaleEnable(false);
        }
    }

    private void hideLoading() {
        if (null != mProgressBar) {
            mProgressBar.setVisibility(View.GONE);
        }
        super.setScrollEnable(mLastScrollEnable);
        super.setScaleEnable(mLastScaleEnable);
    }

    /**
     * 隐藏选择器内容
     */
    public void hideSelectData() {
        isLongPress = false;
//        invalidate();
    }

    /**
     * 刷新完成
     */
    @SuppressWarnings("unused")
    public void refreshComplete() {
        isRefreshing = false;
        hideLoading();
    }

    /**
     * 刷新完成，没有数据
     */
    public void refreshEnd() {
        isLoadMoreEnd = true;
        isRefreshing = false;
        hideLoading();
    }

    /**
     * 重置加载更多
     */
    @SuppressWarnings("unused")
    public void resetLoadMoreEnd() {
        isLoadMoreEnd = false;
    }

    @SuppressWarnings("unused")
    public void setLoadMoreEnd() {
        isLoadMoreEnd = true;
    }

    public interface KChartRefreshListener {
        /**
         * 加载更多
         *
         * @param chart chart
         */
        void onLoadMoreBegin(KLineChartView chart);
    }

    @Override
    public void setScaleEnable(boolean scaleEnable) {
        if (isRefreshing) {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScaleEnable(scaleEnable);

    }

    @Override
    public void setScrollEnable(boolean scrollEnable) {
        if (isRefreshing) {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScrollEnable(scrollEnable);
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        mMACDDraw.setDIFColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        mMACDDraw.setDEAColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        mMACDDraw.setMACDColor(color);
    }

    /**
     * 设置MACD的宽度
     *
     * @param MACDWidth width
     */
    public void setMACDWidth(float MACDWidth) {
        mMACDDraw.setMACDWidth(MACDWidth);
    }

    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        mKDJDraw.setKColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
        mKDJDraw.setDColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        mKDJDraw.setJColor(color);
    }

    /**
     * 设置R颜色
     */
    public void setR1Color(int color) {
        mWRDraw.setR1Color(color);
    }

    /**
     * 设置R颜色
     */
    public void setR2Color(int color) {
        mWRDraw.setR2Color(color);
    }

    /**
     * 设置R颜色
     */
    public void setR3Color(int color) {
        mWRDraw.setR3Color(color);
    }

    /**
     * 设置ma5颜色
     *
     * @param color color
     */
    public void setMaOneColor(int color) {
        mMainDraw.setMaOneColor(color);
        mVolumeDraw.setMa5Color(color);
    }

    /**
     * 设置ma10颜色
     *
     * @param color color
     */
    public void setMaTwoColor(int color) {
        mMainDraw.setMaTwoColor(color);
        mVolumeDraw.setMa10Color(color);
    }

    /**
     * 设置ma20颜色
     *
     * @param color color
     */
    public void setMaThreeColor(int color) {
        mMainDraw.setMaThreeColor(color);
    }

    /**
     * 设置选择器文字大小
     *
     * @param textSize textsize
     */
    public void setSelectorTextSize(float textSize) {
        mMainDraw.setSelectorTextSize(textSize);
    }

    /**
     * 设置选择器背景
     *
     * @param color Color
     */
    public void setSelectorBackgroundColor(int color) {
        mMainDraw.setSelectorBackgroundColor(color);
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mMainDraw.setCandleWidth(candleWidth);
        //量的柱状图与蜡烛图同宽
        mVolumeDraw.setBarWidth(candleWidth);
    }

    /**
     * 设置蜡烛线宽度
     *
     * @param candleLineWidth candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mMainDraw.setCandleLineWidth(candleLineWidth);
    }

    /**
     * 蜡烛是否空心
     */
    public void setCandleSolid(boolean candleSolid) {
        mMainDraw.setStroke(candleSolid);
    }

    public void setRSI1Color(int color) {
        mRSIDraw.setRSI1Color(color);
    }

    public void setRSI2Color(int color) {
        mRSIDraw.setRSI2Color(color);
    }

    public void setRSI3Color(int color) {
        mRSIDraw.setRSI3Color(color);
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
        mMainDraw.setTextSize(textSize);
        mRSIDraw.setTextSize(textSize);
        mMACDDraw.setTextSize(textSize);
        mKDJDraw.setTextSize(textSize);
        mWRDraw.setTextSize(textSize);
        mVolumeDraw.setTextSize(textSize);
    }

    public void setLineWidth(float lineWidth) {
        mMainDraw.setLineWidth(lineWidth);
        mRSIDraw.setLineWidth(lineWidth);
        mMACDDraw.setLineWidth(lineWidth);
        mKDJDraw.setLineWidth(lineWidth);
        mWRDraw.setLineWidth(lineWidth);
        mVolumeDraw.setLineWidth(lineWidth);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mMainDraw.setSelectorTextColor(color);
    }

    /**
     * 设置刷新监听
     */
    @SuppressWarnings("all")
    public void setRefreshListener(KChartRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    public void setMainDrawLine(boolean isLine) {
        setShowLine(isLine);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!isRefreshing) {
            super.onLongPress(e);
        }
    }
}
