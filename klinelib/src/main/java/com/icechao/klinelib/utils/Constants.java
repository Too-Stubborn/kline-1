package com.icechao.klinelib.utils;


/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.klinelib.utils
 * @FileName     : Constants.java
 * @Author       : chao
 * @Date         : 2019/1/10
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class Constants {

    public static final long LONG_PRESS_TIME = 1000L;
    //market.$symbol.kline.$period
    public static final String KLINE_INFO = "market.%s.kline.%s";
    //market.$symbol.detail
    public static final String DETAIL_INFO = "market.%s.%s";
    //market.$symbol.trade.detail
    public static final String TRADE_DETAIL_INFO = "market.%s.%s";
    //market.$symbol.depth.$type
    public static final String DEPTH_INFO = "market.%s.depth.%s";
    //market.tickers
    public static final String ALL_TICKER = "market.tickers";


    public static final int MARKET_DEPTH_BUY_SELL_NUM = 20;
}
