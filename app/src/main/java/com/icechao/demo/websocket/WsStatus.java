package com.icechao.demo.websocket;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.demo
 * @FileName     : WsStatus.java
 * @Author       : chao
 * @Date         : 2019/1/22
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class WsStatus {

    public final static int CONNECTED = 1;
    public final static int CONNECTING = 0;
    public final static int RECONNECT = 2;
    public final static int DISCONNECTED = -1;

    class CODE {
        public final static int NORMAL_CLOSE = 1000;
        public final static int ABNORMAL_CLOSE = 1001;
    }

    class TIP {
        public final static String NORMAL_CLOSE = "normal close";
        public final static String ABNORMAL_CLOSE = "abnormal close";
    }
}