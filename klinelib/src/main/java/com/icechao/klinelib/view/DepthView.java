package com.icechao.klinelib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.icechao.klinelib.R;
import com.icechao.klinelib.base.BaseDepthView;
import com.icechao.klinelib.utils.ViewUtil;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.view
 * @FileName     : DepthView.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class DepthView extends BaseDepthView {

    public DepthView(Context context) {
        super(context);
        onCreated(context);
    }

    public DepthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreated(context);
    }

    public DepthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreated(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DepthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreated(context);

    }

    private void onCreated(Context context) {
        setBackGroundColor(context.getResources().getColor(R.color.background));
        setShowLeftLabel(false);
        setLegnetTextColorSameAsLegent(true);
        setLabelColor(context.getResources().getColor(R.color.chart_text));
        setSelectedLabelColor(Color.WHITE);
        setSelectedBoxBorderColor(getResources().getColor(R.color.chart_text));
        setSelectedBoxColor(getResources().getColor(R.color.background));
        setLabelHeight(40);
        setLeftColor(getResources().getColor(R.color.chart_up));
        setRightColor(getResources().getColor(R.color.chart_down));
        setLeftAreaColor(getResources().getColor(R.color.chart_up_alpha));
        setRightAreaColor(getResources().getColor(R.color.chart_down_alpha));
        setLeftLegentText("买");
        setRightLegentText("卖");
        setSelectedCricleRadiusWidth(ViewUtil.Dp2Px(context,1));
        setSelectedCircleRadius(ViewUtil.Dp2Px(context,8));
        setSelectedPointRadius(ViewUtil.Dp2Px(context,2));
        setDepthLineWidth(ViewUtil.Dp2Px(context,1));
        setLegentHeight(ViewUtil.Dp2Px(context,8));

        setTextLabelTextSize(25);


        setSelectedBorderWitdh(ViewUtil.Dp2Px(context,3));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
