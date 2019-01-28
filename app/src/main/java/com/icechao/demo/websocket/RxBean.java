package com.icechao.demo.websocket;

/*************************************************************************
 * Description   :
 *
 * @PackageName  : com.icechao.demo.websocket
 * @FileName     : RxBean.java
 * @Author       : chao
 * @Date         : 2019/1/23
 * @Email        : icechliu@gmail.com
 * @version      : V1
 *************************************************************************/
public class RxBean {

    public RxBean(EventType status) {
        this.status = status;
    }

    public enum EventType {
        TypeSocket
    }

    private EventType status;

    public EventType getStatus() {
        return status;
    }

    public void setStatus(EventType status) {
        this.status = status;
    }
}
