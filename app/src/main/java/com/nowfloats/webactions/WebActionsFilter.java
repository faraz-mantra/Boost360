package com.nowfloats.webactions;

import com.nowfloats.webactions.webactioninterfaces.IFilter;

import java.util.List;

/**
 * Created by NowFloats on 11-04-2018.
 */

public class WebActionsFilter implements IFilter {

    private CharSequence query;

    public WebActionsFilter() {

    }

    private WebActionsFilter(CharSequence query) {
        this.query = query;
    }

    @Override
    public <T> IFilter eq(String key, T value) {
        if(value.getClass().getName().equals(String.class.getName())) {
            return new WebActionsFilter(String.format("{'%s': '%s'}", key, value.toString()));
        }
        return new WebActionsFilter(String.format("{'%s': %s}", key, value+""));
    }

    @Override
    public IFilter and(IFilter filter1, IFilter filter2) {
        return new WebActionsFilter(String.format("{$and:[%s, %s]}", filter1.toQuery(), filter2.toQuery()));
    }

    @Override
    public IFilter or(IFilter filter1, IFilter filter2) {
        return new WebActionsFilter(String.format("{$or:[%s, %s]}", filter1.toQuery(), filter2.toQuery()));
    }

    @Override
    public IFilter and(List<IFilter> filterList) {
        StringBuilder builder = new StringBuilder();
        for (IFilter filter: filterList) {
            builder.append(filter.toQuery() + ",");
        }
        builder.delete(builder.length() - 2, builder.length() -1);
        return new WebActionsFilter(String.format("{$and:[%s]}", builder.toString()));
    }

    @Override
    public IFilter or(List<IFilter> filterList) {
        StringBuilder builder = new StringBuilder();
        for (IFilter filter: filterList) {
            builder.append(filter.toQuery() + ",");
        }
        builder.delete(builder.length() - 2, builder.length() -1);
        return new WebActionsFilter(String.format("{$or:[%s]}", builder.toString()));
    }

    @Override
    public <T> IFilter gt(String key, T value) {
        return new WebActionsFilter(String.format("{'%s':{$gt:%s}}", key, value+""));
    }

    @Override
    public <T> IFilter lt(String key, T value) {
        return new WebActionsFilter(String.format("{'%s':{$lt:%s}}", key, value+""));
    }

    @Override
    public CharSequence toQuery() {
        return query;
    }
}
