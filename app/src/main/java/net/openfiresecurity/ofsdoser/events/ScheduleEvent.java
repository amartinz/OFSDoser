package net.openfiresecurity.ofsdoser.events;

public class ScheduleEvent {

    private boolean mEnabled;

    public ScheduleEvent(final boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

}
