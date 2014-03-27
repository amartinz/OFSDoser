package net.openfiresecurity.ofsdoser.events;

public class ToastEvent {

    private boolean mIsString;
    private int     mMsgId;
    private String  mMsg;

    public ToastEvent(final int msgId) {
        this(false, msgId, null);
    }

    public ToastEvent(final String msg) {
        this(true, 0, msg);
    }

    public ToastEvent(final boolean isString, final int msgId, final String msg) {
        mIsString = isString;
        mMsgId = msgId;
        mMsg = msg;
    }

    public boolean getIsString() {
        return mIsString;
    }

    public int getMsgId() {
        return mMsgId;
    }

    public String getMsg() {
        return mMsg;
    }

}
