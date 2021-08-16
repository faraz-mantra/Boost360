package com.nowfloats.webactions.webactioninterfaces;

/**
 * Created by NowFloats on 12-04-2018.
 */

public interface IUpdate {
    public <T> IUpdate addToSet(String key, T value);

    public <T> IUpdate removeFromSet(String key, T value);

    public IUpdate clearSet();

    public CharSequence toUpdateString();
}
