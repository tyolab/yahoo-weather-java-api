package com.github.fedy2.weather.binding;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 11/10/17.
 */

public interface Parser<T> {
    T parse(String txt) throws Exception;
}
