package com.nowfloats.Analytics_Screen;

/**
 * Created by Kamal on 17-02-2015.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DataMap implements Map<String, Object>{


    Map map = null;
    public DataMap()
    {
        map  = new LinkedHashMap<String,Object>();
    }
    @Override
    public void clear() {
        // TODO Auto-generated method stub
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {

        boolean flag = false;
        if(map.containsKey(key))
        {
            flag = true;
        }

        return flag;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean flag = false;
        if(map.containsValue(value))
        {
            flag = true;
        }

        return flag;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        // TODO Auto-generated method stub
        return map.entrySet();

    }

    @Override
    public Object get(Object key) {
        // TODO Auto-generated method stub
        Object tmp = null;
        if(map.containsKey(key))
        {
            tmp = map.get(key);
        }
        return tmp;

    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        boolean flag = false;
        if(map.size()==0)
        {
            flag = true;
        }
        return false;
    }

    @Override
    public Set<String> keySet() {
        // TODO Auto-generated method stub
        return map.keySet();
    }

    @Override
    public Object put(String key, Object value) {
        // TODO Auto-generated method stub
        if(!map.containsKey(key))
        {
            map.put(key, value);
        }

        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> arg0) {
        // TODO Auto-generated method stub
        map.putAll(arg0);
    }

    @Override
    public Object remove(Object key) {
        // TODO Auto-generated method stub
        map.remove(key);
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return map.size();
    }

    @Override
    public Collection<Object> values() {
        // TODO Auto-generated method stub
        return map.values();
    }

    public Object get(int index)
    {
        return (new ArrayList<String>(map.values())).get(index);
    }

}
