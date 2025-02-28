package com.project.ilearncentral.CustomBehavior;

import com.project.ilearncentral.CustomInterface.OnIntegerChangeListener;

public class ObservableInteger {
    private OnIntegerChangeListener listener;

    private int value;

    public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
    {
        this.listener = listener;
    }

    public int get()
    {
        return value;
    }

    public void set(int value)
    {
        this.value = value;

        if(listener != null)
        {
            listener.onIntegerChanged(value);
        }
    }
}
