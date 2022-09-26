package pg.grpc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import pg.grpc.data.UserController;

public class App extends Application {

    public static App getInstance() {
        return instance;
    }

    private static App instance;
    private SharedPreferences preferences;
    public static final String USERS = "users";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = getSharedPreferences("grpc", Context.MODE_PRIVATE);
        UserController.init();
    }

    public String getPrefString(String key) {
        return instance.preferences.getString(key, null);
    }

    public void putPrefString(String key, String value) {
        instance.preferences.edit().putString(key, value).apply();
    }
}
