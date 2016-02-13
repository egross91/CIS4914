package com.meetup.helpers;

import com.meetup.interfaces.Presentable;

public abstract class PresentationHelper implements Presentable {
    @Override
    public final void display(CharSequence dialog) {
        doDisplay(dialog);
    }

    @Override
    public final void display(CharSequence dialog, int duration) {
        doDisplay(dialog, duration);
    }

    protected abstract void doDisplay(CharSequence dialog);
    protected abstract void doDisplay(CharSequence dialog, int duration);
}
