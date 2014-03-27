package net.openfiresecurity.ofsdoser.events;

public class VisibilityEvent {

    private boolean mVisible;

    public VisibilityEvent(final boolean visible) {
        mVisible = visible;
    }

    public boolean getVisibility() {
        return mVisible;
    }

}
