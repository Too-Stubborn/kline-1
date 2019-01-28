package com.icechao.klinelib.adapter;

import com.icechao.klinelib.base.BaseKLineChartAdapter;
import com.icechao.klinelib.entity.KLineEntity;
import com.icechao.klinelib.utils.DataHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据适配器
 * Created by tifezh on 2016/6/18.
 */
public class KLineChartAdapter extends BaseKLineChartAdapter {

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("mm/dd");

    public void setDateFormatter(SimpleDateFormat dateFormatter) {
        this.dateFormatter = dateFormatter;
    }


    private List<KLineEntity> datas = new ArrayList<>();

    public KLineChartAdapter() {

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        if (datas.size() == 0 || position < 0) {
            return null;
        }
        return datas.get(position);
    }

    @Override
    public String getDate(int position) {
        return dateFormatter.format(new Date(datas.get(position).getDate()));
    }

    /**
     * 向头部添加数据
     */
    public void addHeaderData(List<KLineEntity> data) {
        if (null != data && !data.isEmpty()) {
            notifyDataWillChanged();
            datas.clear();
            datas.addAll(data);
        }
    }

    /**
     * 向尾部添加数据
     */
    public void resetData(List<KLineEntity> data) {
        notifyDataWillChanged();
        datas = data;
        DataHelper.calculate(datas);
    }

    public void addLast(KLineEntity entity) {

        if (null != entity) {
            datas.add(datas.size(), entity);
            DataHelper.calculate(datas);
        }
    }


    /**
     * 获取当前K线最后一个数据
     *
     * @return 最后一根线的bean
     */
    public KLineEntity getLastData() {
        if (null != datas) {
            return datas.get(datas.size() - 1);
        }
        return null;
    }

    /**
     * 改变某个点的值
     *
     * @param position 索引值
     */
    public void changeItem(int position, KLineEntity data) {
        notifyDataWillChanged();
        datas.set(position, data);
        DataHelper.calculate(datas);
        notifyDataSetChanged();
    }

    /**
     * 数据清除
     */
    public void clearData() {
        notifyDataWillChanged();
        datas.clear();
        notifyDataSetChanged();
    }
}
