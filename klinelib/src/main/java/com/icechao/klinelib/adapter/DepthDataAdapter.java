package com.icechao.klinelib.adapter;

import com.icechao.klinelib.base.BaseDepthAdapter;
import com.icechao.klinelib.entity.IDepth;

import java.util.Collections;
import java.util.List;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.adapter
 * @FileName     : DepthDataAdapter.java
 * @Author       : chao
 * @Date         : 2019/1/9
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class DepthDataAdapter extends BaseDepthAdapter {


    public void setNewData(List<List<Double>> leftDatas, List<List<Double>> rightDatas) {
        notifyDataWillChanged();
        iDepthsLeft.clear();
        Collections.reverse(leftDatas);
        iDepthsLeft.addAll(leftDatas);
        iDepthsRight.clear();
        iDepthsRight.addAll(rightDatas);
        tempLeftDatas = parseData(leftDatas, true);
        tempRightDatas = parseData(rightDatas, false);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return iDepthsLeft.size() + iDepthsRight.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public String getDate(int position) {
        return null;
    }

}
