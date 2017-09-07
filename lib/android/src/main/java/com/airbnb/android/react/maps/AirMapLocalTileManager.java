package com.airbnb.android.react.maps;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

public class AirMapLocalTileManager extends ViewGroupManager<AirMapLocalTile> {
    private DisplayMetrics metrics;

    public AirMapLocalTileManager(ReactApplicationContext reactContext) {
        super();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            metrics = new DisplayMetrics();
            ((WindowManager) reactContext.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay()
                    .getRealMetrics(metrics);
        } else {
            metrics = reactContext.getResources().getDisplayMetrics();
        }
    }

    @Override
    public String getName() {
        return "AIRMapLocalTile";
    }

    @Override
    public AirMapLocalTile createViewInstance(ThemedReactContext context) {
        return new AirMapLocalTile(context);
    }

    @ReactProp(name = "localTemplate")
    public void setLocalTemplate(AirMapLocalTile view, String localTemplate) {
        view.setLocalTemplate(localTemplate);
    }

    @ReactProp(name = "zIndex", defaultFloat = -1.0f)
    public void setZIndex(AirMapLocalTile view, float zIndex) {
        view.setZIndex(zIndex);
    }


    @ReactProp(name = "maxZoom", defaultInt = 12)
    public void setMaxZoom(AirMapLocalTile view, int maxZoom) {
        view.setMaxZoom(maxZoom);
    }

}
