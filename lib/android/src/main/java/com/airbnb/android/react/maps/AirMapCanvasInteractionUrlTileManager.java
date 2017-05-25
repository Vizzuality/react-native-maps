package com.airbnb.android.react.maps;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

public class AirMapCanvasInteractionUrlTileManager extends ViewGroupManager<AirMapCanvasInteractionUrlTile> {
    private DisplayMetrics metrics;

    public AirMapCanvasInteractionUrlTileManager(ReactApplicationContext reactContext) {
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
        return "AIRMapCanvasInteractionUrlTile";
    }

    @Override
    public AirMapCanvasInteractionUrlTile createViewInstance(ThemedReactContext context) {
        return new AirMapCanvasInteractionUrlTile(context);
    }

    @ReactProp(name = "urlTemplate")
    public void setUrlTemplate(AirMapCanvasInteractionUrlTile view, String urlTemplate) {
        view.setUrlTemplate(urlTemplate);
    }

    @ReactProp(name = "maxZoom", defaultInt = 12)
    public void setMaxZoom(AirMapCanvasInteractionUrlTile view, int maxZoom) {
        view.setMaxZoom(maxZoom);
    }

    @ReactProp(name = "areaId")
    public void setAreaId(AirMapCanvasInteractionUrlTile view, String areaId) {
        view.setAreaId(areaId);
    }

    @ReactProp(name = "isConnected", defaultBoolean = true)
    public void setIsConnected(AirMapCanvasInteractionUrlTile view, boolean isConnected) {
        view.setIsConnected(isConnected);
    }

    @ReactProp(name = "minDate")
    public void setMinDate(AirMapCanvasInteractionUrlTile view, String minDate) {
        view.setMinDate(minDate);
    }

    @ReactProp(name = "maxDate")
    public void setMaxDate(AirMapCanvasInteractionUrlTile view, String maxDate) {
        view.setMaxDate(maxDate);
    }

    @ReactProp(name = "zIndex", defaultFloat = -1.0f)
    public void setZIndex(AirMapCanvasInteractionUrlTile view, float zIndex) {
        view.setZIndex(zIndex);
    }

    @ReactProp(name = "coordinates")
    public void setCoordinates(AirMapCanvasInteractionUrlTile view, ReadableMap coordinates) {
        if (coordinates != null) {
            int[] tile = new int[]{coordinates.getArray("tile").getInt(0), coordinates.getArray("tile").getInt(1), coordinates.getArray("tile").getInt(2)};
            double[] precision = new double[]{coordinates.getArray("precision").getDouble(0), coordinates.getArray("precision").getDouble(1)};
            view.setCoordinates(new Coordinates(tile, precision));
        }
    }

}
