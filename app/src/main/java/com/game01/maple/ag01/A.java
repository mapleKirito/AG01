package com.game01.maple.ag01;

import android.app.Application;

import com.game01.maple.ag01.config.SystemParams;


/**
 * Created by Song on 2016/11/2.
 */
public class A extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SystemParams.init(this);
    }
}
