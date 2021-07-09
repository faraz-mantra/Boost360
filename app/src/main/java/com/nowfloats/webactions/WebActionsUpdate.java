package com.nowfloats.webactions;

import com.nowfloats.webactions.models.WebActionUpdateValue;
import com.nowfloats.webactions.webactioninterfaces.IUpdate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by NowFloats on 12-04-2018.
 */

public class WebActionsUpdate implements IUpdate {

    private Map<String, WebActionUpdateValue> updateMap = new HashMap<>();

    @Override
    public <T> IUpdate addToSet(String key, T value) {
        updateMap.put(key, new WebActionUpdateValue<T>(value));
        return this;
    }

    @Override
    public <T> IUpdate removeFromSet(String key, T value) {
        updateMap.remove(key);
        return this;
    }

    @Override
    public IUpdate clearSet() {
        updateMap.clear();
        return this;
    }

    @Override
    public CharSequence toUpdateString() {
        StringBuilder updateBuilder = new StringBuilder();
        for (Map.Entry<String, WebActionUpdateValue> entry: updateMap.entrySet()) {
            if(entry.getValue().getClass().getName().equals(String.class.getName())) {
                updateBuilder.append(String.format("%s: '%s',", entry.getKey(), entry.getValue() + ""));
            } else {
                updateBuilder.append(String.format("%s: %s,", entry.getKey(), entry.getValue() + ""));
            }
        }
        updateBuilder.delete(updateBuilder.length()-2, updateBuilder.length()-1);
        return String.format("{$set:{%s}}", updateBuilder.toString());
    }
}
