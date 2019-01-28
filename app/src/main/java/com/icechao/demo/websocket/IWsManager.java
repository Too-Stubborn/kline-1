package com.icechao.demo.websocket;


import okhttp3.WebSocket;
import okio.ByteString;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.demo
 * @FileName     : IWsManager.java
 * @Author       : chao
 * @Date         : 2019/1/22
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
interface IWsManager {

    WebSocket getWebSocket();

    void startConnect();

    void stopConnect();

    boolean isWsConnected();

    int getCurrentStatus();

    void setCurrentStatus(int currentStatus);

//    boolean sendStickMessage(RequestBean msg, WsManager.IWsLisenter lisntener);

//    boolean sendOnceMessage(RequestBean msg, WsManager.IWsLisenter lisntener);

    boolean sendMessage(ByteString byteString);
}