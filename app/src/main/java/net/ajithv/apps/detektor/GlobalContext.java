package net.ajithv.apps.detektor;

import org.json.JSONArray;
import android.content.Context;

public class GlobalContext {
    private static GlobalContext instance;

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private Context ctx;
    public static GlobalContext getInstance() {
        if(instance == null) {
            instance = new GlobalContext();
        }
        return instance;
    }
    public static JSONArray readings;
}
