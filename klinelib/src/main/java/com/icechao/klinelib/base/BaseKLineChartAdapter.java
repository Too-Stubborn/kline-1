package com.icechao.klinelib.base;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Looper;


/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : BaseKLineChartAdapter.java
 * @Author       : chao
 * @Date         : 2019/1/8
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/

public abstract class BaseKLineChartAdapter implements IAdapter {

    Handler handler = new Handler(Looper.getMainLooper());
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    Runnable notifyDataChangeRunable = new Runnable() {
        @Override
        public void run() {
            mDataSetObservable.notifyChanged();
        }
    };
    Runnable notifyDataWillChangeRunnable = new Runnable() {
        @Override
        public void run() {
            mDataSetObservable.notifyInvalidated();
        }
    };

    @Override
    public void notifyDataSetChanged() {
        if (getCount() > 0) {
            handler.post(notifyDataChangeRunable);
        }
    }

    @Override
    public void notifyDataWillChanged() {
        handler.post(notifyDataWillChangeRunnable);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }
}
