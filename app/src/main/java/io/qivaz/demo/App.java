package io.qivaz.demo;

import android.app.Application;

import io.qivaz.anime.Anime;


/**
 * @author Qinghua Zhang @create 2017/3/21.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        Anime.start(this, true);
        super.onCreate();
    }
}
