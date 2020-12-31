package com.framework.analytics

import android.os.Bundle


class FirebaseEvent(private var name: String, private var bundle: Bundle = Bundle()) {

    fun getBundle(): Bundle {
        return bundle
    }

    fun setBundle(bundle: Bundle) {
        this.bundle = bundle
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    /**
     * Utility method to apply Firebase's restrictions on String key and String value
     * before putting them in [.bundle].
     *
     * @param key   The key.
     * @param value The value.
     */
    fun putString(key: String, value: String?) {
        bundle.putString(key, value)
    }

    /**
     * Utility method to apply String key and Float value
     * before putting them in [.bundle].
     *
     * @param key   The key.
     * @param value The value.
     */
    fun putFloat(key: String, value: Float) {
        bundle.putFloat(key, value)
    }

    /**
     * Utility method to apply String key and Double value
     * before putting them in [.bundle].
     *
     * @param key   The key.
     * @param value The value.
     */
    fun putDouble(key: String, value: Double) {
        bundle.putDouble(key, value)
    }

    /**
     * Utility method to apply String key and Long value
     * before putting them in [.bundle].
     *
     * @param key   The key.
     * @param value The value.
     */
    fun putLong(key: String, value: Long) {
        bundle.putLong(key, value)
    }

    /**
     * Utility method to apply String key and Int value
     * before putting them in [.bundle].
     *
     * @param key   The key.
     * @param value The value.
     */
    fun putInt(key: String, value: Int) {
        bundle.putInt(key, value)
    }

    /**
     * Utility method to apply String key and Boolean value
     * before putting them in [.bundle].
     *
     * @param key   The key.
     * @param value The value.
     */
    fun putBoolean(key: String, value: Boolean) {
        bundle.putBoolean(key, value)
    }

    /**
     * Utility method to apply String key and String value
     * before putting them in [.bundle].
     *
     * @param map The map containing keys and values.
     */
    fun putMap(map: Map<String, String>) {
        for ((key, value) in map) {
            bundle.putString(key, value)
        }
    }

    /**
     * Utility method to apply String key and Double value
     * before putting them in [.bundle].
     *
     * @param map The map containing keys and values.
     */
    fun putDoubleMap(map: Map<String, Double>) {
        for ((key, value) in map) {
            bundle.putDouble(key, value)
        }
    }

    /**
     * Utility method to apply String key and Int value
     * before putting them in [.bundle].
     *
     * @param map The map containing keys and values.
     */
    fun putIntMap(map: Map<String, Int>) {
        for ((key, value) in map) {
            bundle.putInt(key, value)
        }
    }


}