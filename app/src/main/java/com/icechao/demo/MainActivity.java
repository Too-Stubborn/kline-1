package com.icechao.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.icechao.kline.R;
import com.icechao.klinelib.adapter.KLineChartAdapter;
import com.icechao.klinelib.draw.Status;
import com.icechao.klinelib.entity.KLineEntity;
import com.icechao.klinelib.entity.MarketBuySellItem;
import com.icechao.klinelib.entity.MarketDepthPercentItem;
import com.icechao.klinelib.formatter.DateFormatter;
import com.icechao.klinelib.utils.Constants;
import com.icechao.klinelib.view.KLineChartView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private List<KLineEntity> datas;

    private KLineChartAdapter adapter;

    private KLineChartView chartView;

    private Handler handler = new Handler();
    private TextView textViewPriceText;
    private TextView textViewRiseAndFallText;
    private TextView textViewCny;
    private TextView textViewHighPriceText;
    private TextView textViewLowPriceText;
    private TextView textViewVolumeSumText;
    private View attachedOperater;
    private View masterOperater;
    private View moreIndex;
    private View klineOperater;
//    private ReqBean klineReq;
    private DepthFullView depthFullView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chartView = findViewById(R.id.kLineChartView);

        depthFullView = findViewById(R.id.full_depth_view);

        attachedOperater = findViewById(R.id.linear_layout_attached_operater);
        masterOperater = findViewById(R.id.linear_layout_master_operater);
        moreIndex = findViewById(R.id.linear_layout_index_more);
        klineOperater = findViewById(R.id.kline_operater);


        findViewById(R.id.text_view_ma).setOnClickListener(this);
        findViewById(R.id.text_view_boll).setOnClickListener(this);
        findViewById(R.id.text_view_macd).setOnClickListener(this);
        findViewById(R.id.text_view_kdj).setOnClickListener(this);
        findViewById(R.id.text_view_rsi).setOnClickListener(this);
        findViewById(R.id.text_view_wr).setOnClickListener(this);

        findViewById(R.id.text_view_hide_sub).setOnClickListener(this);
        findViewById(R.id.text_view_hide_master).setOnClickListener(this);


        textViewPriceText = findViewById(R.id.text_view_price_text);
        textViewRiseAndFallText = findViewById(R.id.text_view_rise_and_fall_text);
        textViewCny = findViewById(R.id.text_view_cny);
        textViewHighPriceText = findViewById(R.id.high_price_text);
        textViewLowPriceText = findViewById(R.id.low_price_text);
        textViewVolumeSumText = findViewById(R.id.volume_sum_text);

        RadioGroup radioGroup = findViewById(R.id.radio_group_defalt_index);
        radioGroup.setOnCheckedChangeListener(this);

        initKline();
        initData();

    }

    private void initKline() {
        adapter = new KLineChartAdapter();
        chartView = findViewById(R.id.kLineChartView);
        chartView.setAdapter(adapter);
        chartView.setDateTimeFormatter(new DateFormatter());
        chartView.setGridColumns(5);
        chartView.setGridRows(5);
        chartView.setOverScrollRange(getWindowManager().getDefaultDisplay().getWidth() / 5);

    }

    private void initData() {
        List<KLineEntity> all = DataRequest.getALL(this);
        adapter.resetData(all.subList(0, 40));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {

        klineOperater.setVisibility(View.GONE);
        masterOperater.setVisibility(View.GONE);
        attachedOperater.setVisibility(View.GONE);
        moreIndex.setVisibility(View.GONE);

        switch (v.getId()) {
            case R.id.text_view_hide_master:
                chartView.hideSelectData();
                chartView.changeMainDrawType(Status.NONE);

                break;
            case R.id.text_view_hide_sub:
                chartView.hideSelectData();
                chartView.hideChildDraw();
                break;
            case R.id.text_view_ma:
                chartView.hideSelectData();
                chartView.changeMainDrawType(Status.MA);
                break;
            case R.id.text_view_boll:
                chartView.hideSelectData();
                chartView.changeMainDrawType(Status.BOLL);
                break;
            case R.id.text_view_macd:
                chartView.hideSelectData();
//                chartView.changeMainDrawType(Status.);
                chartView.setChildDraw(0);
                break;
            case R.id.text_view_kdj:
                chartView.hideSelectData();
//                chartView.changeMainDrawType(Status.);
                chartView.setChildDraw(1);
                break;
            case R.id.text_view_rsi:
                chartView.hideSelectData();
//                chartView.changeMainDrawType(Status.);
                chartView.setChildDraw(2);
                break;
            case R.id.text_view_wr:
                chartView.hideSelectData();
//                chartView.changeMainDrawType(Status.);
                chartView.setChildDraw(3);
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        klineOperater.setVisibility(View.GONE);
        masterOperater.setVisibility(View.GONE);
        attachedOperater.setVisibility(View.GONE);
        moreIndex.setVisibility(View.GONE);

        switch (checkedId) {
            case R.id.radio_button_time_line:
                chartView.hideSelectData();
                chartView.setMainDrawLine(true);
//                sendChangedReq(ReqBean.KlineTime.ONEMIN);
                break;
            case R.id.radio_button_fifteen:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.FIFTEENMIN);


                break;
            case R.id.radio_button_one_hour:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.ONEHOUR);


                break;
            case R.id.radio_button_four_hour:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);

//                sendChangedReq(ReqBean.KlineTime.FOURHOUR);

                break;
            case R.id.radio_button_one_day:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.ONEDAY);

                break;
            case R.id.radio_button_more:
                moreIndex.setVisibility(View.VISIBLE);
                klineOperater.setVisibility(View.VISIBLE);
                break;
            case R.id.radio_button_index_setting:
                masterOperater.setVisibility(View.VISIBLE);
                attachedOperater.setVisibility(View.VISIBLE);
                klineOperater.setVisibility(View.VISIBLE);
                break;
            case R.id.text_view_one_minute:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.ONEMIN);

                break;
            case R.id.text_view_five_minute:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.FIVEMIN);


                break;
            case R.id.text_view_half_hour:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.THIRTYMIN);

                break;
            case R.id.text_view_one_week:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.ONEWEEK);

                break;
            case R.id.text_view_one_mounth:
                chartView.hideSelectData();
                chartView.setMainDrawLine(false);
//                sendChangedReq(ReqBean.KlineTime.ONEMON);

                break;

        }
    }

//    private ReqBean.KlineTime time = RequestBean.KlineTime.ONEMIN;
//
//    private void sendChangedReq(ReqBean.KlineTime onemon) {
//
//
//    }


    private List<MarketBuySellItem> askList = new ArrayList<>();

    private List<MarketBuySellItem> bidList = new ArrayList<>();


    private void updateBuySellList(String symbol, List<MarketBuySellItem> list, List<List<Double>> priceAndAmount, List<Double> totalList, int type, double totalAmount) {
        int loop = Math.min(Constants.MARKET_DEPTH_BUY_SELL_NUM, priceAndAmount.size());
        list.clear();
        MarketBuySellItem item;
        for (int i = 0; i < loop; i++) {
            item = new MarketBuySellItem();
            item.setTradeType(MarketBuySellItem.MARKET_TRADE);
            item.setNeedDraw(true);
            item.setSymbol(symbol);
            item.setPrice(priceAndAmount.get(i).get(0));
            item.setAmount(priceAndAmount.get(i).get(1));
            double amount = totalList.get(i);
            int progress = (int) (Double.compare(totalAmount, 0) == 0 ? 1 : amount * 100 / totalAmount);
            if (progress < 1)
                progress = 1;

            item.setProgress(progress);
            item.setType(type);
            list.add(item);
        }

    }

    private void updatePercentBuySellList(String symbol, List<MarketDepthPercentItem> list, List<List<Double>> priceAndAmount, List<Double> totalList, int type) {
        int loop = priceAndAmount.size();
        list.clear();
        MarketDepthPercentItem item;
        for (int i = 0; i < loop; i++) {
            item = new MarketDepthPercentItem();
            item.setSymbol(symbol);
            item.setPrice(priceAndAmount.get(i).get(0));
            double totalAmount = totalList.get(i);
            item.setAmount(totalAmount);
            list.add(item);
        }
        if (type == MarketBuySellItem.BUY_TYPE) {
            Collections.reverse(list);
        }

        LogUtil.e("updatePercentBuySellList");

    }

    private List<Double> getPercentTotalAmount(List<List<Double>> list) {
        List<Double> totalList = new ArrayList<>();
        double amount = 0;

        if (list == null)
            return totalList;

        int loop = list.size();

        for (int i = 0; i < loop; i++) {
            if (list.get(i).size() >= 2) {
                amount += list.get(i).get(1);
                totalList.add(amount);
            }
        }
        return totalList;
    }

    @NonNull
    private List<Double> getTotalAmount(List<List<Double>> list) {
        List<Double> totalList = new ArrayList<>();
        double amount = 0;

        if (list == null)
            return totalList;

        int loop = Math.min(Constants.MARKET_DEPTH_BUY_SELL_NUM, list.size());

        for (int i = 0; i < loop; i++) {
            if (list.get(i).size() >= 2) {
                amount += list.get(i).get(1);
                totalList.add(amount);
            }
        }
        return totalList;
    }

}
