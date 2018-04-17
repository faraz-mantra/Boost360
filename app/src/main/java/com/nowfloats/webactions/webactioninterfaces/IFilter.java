package com.nowfloats.webactions.webactioninterfaces;

import java.util.List;

/**
 * Created by NowFloats on 11-04-2018.
 */

public interface IFilter {
    public <T> IFilter eq(String key, T value);
    public IFilter and(IFilter filter1, IFilter filter2);
    public IFilter or(IFilter filter1, IFilter filter2);
    public IFilter and(List<IFilter> filterList);
    public IFilter or(List<IFilter> filterList);
    public <T> IFilter gt(String key, T value);
    public <T> IFilter lt(String key, T value);
    public CharSequence toQuery();
}
