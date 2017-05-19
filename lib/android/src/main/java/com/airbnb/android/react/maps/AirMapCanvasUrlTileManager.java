package com.airbnb.android.react.maps;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

public class AirMapCanvasUrlTileManager extends ViewGroupManager<AirMapCanvasUrlTile> {
    private DisplayMetrics metrics;

    public AirMapCanvasUrlTileManager(ReactApplicationContext reactContext) {
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
        Log.d("Map", "Get name");
        return "AIRMapCanvasUrlTile";
    }

    @Override
    public AirMapCanvasUrlTile createViewInstance(ThemedReactContext context) {
        return new AirMapCanvasUrlTile(context);
    }

    @ReactProp(name = "urlTemplate")
    public void setUrlTemplate(AirMapCanvasUrlTile view, String urlTemplate) {
        view.setUrlTemplate(urlTemplate);
    }

    @ReactProp(name = "maxZoom", defaultInt = 12)
    public void setMaxZoom(AirMapCanvasUrlTile view, int maxZoom) {
        view.setMaxZoom(maxZoom);
    }

    @ReactProp(name = "alertType")
    public void setAlertType(AirMapCanvasUrlTile view, String alertType) {
        view.setAlertType(alertType);
    }

    @ReactProp(name = "areaId")
    public void setAreaId(AirMapCanvasUrlTile view, String areaId) {
        view.setAreaId(areaId);
    }

    @ReactProp(name = "isConnected", defaultBoolean = true)
    public void setIsConnected(AirMapCanvasUrlTile view, boolean isConnected) {
        view.setIsConnected(isConnected);
    }

    @ReactProp(name = "minDate")
    public void setMinDate(AirMapCanvasUrlTile view, String minDate) {
        view.setMinDate(minDate);
    }

    @ReactProp(name = "maxDate")
    public void setMaxDate(AirMapCanvasUrlTile view, String maxDate) {
        view.setMaxDate(maxDate);
    }

    @ReactProp(name = "zIndex", defaultFloat = -1.0f)
    public void setZIndex(AirMapCanvasUrlTile view, float zIndex) {
        view.setZIndex(zIndex);
    }

}
