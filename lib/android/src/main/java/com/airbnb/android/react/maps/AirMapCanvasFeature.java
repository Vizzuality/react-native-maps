package com.airbnb.android.react.maps;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

/**
 * Created by joseangel.parreno@vizzuality.com on 25/05/2017.
 */

public abstract class AirMapCanvasFeature extends AirMapFeature {
    protected TileOverlayOptions tileOverlayOptions;
    protected TileOverlay tileOverlay;
    protected AirMapCanvasTileProvider tileProvider;

    protected String urlTemplate;
    protected int maxZoom;
    protected String areaId;
    protected String alertType;
    protected String minDate;
    protected String maxDate;
    protected boolean isConnected;
    protected float zIndex;
    protected Coordinates coordinates;

    public AirMapCanvasFeature(Context context) {
        super(context);
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
        if (tileProvider != null) {
            tileProvider.setUrlTemplate(urlTemplate);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
        if (tileProvider != null) {
            tileProvider.setMaxZoom(maxZoom);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
        if (tileProvider != null) {
            tileProvider.setAreaId(areaId);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
        if (tileProvider != null) {
            tileProvider.setIsConnected(isConnected);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
        if (tileProvider != null) {
            tileProvider.setAlertType(alertType);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
        if (tileOverlay != null) {
            tileOverlay.setZIndex(zIndex);
        }
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        if (tileProvider != null) {
            tileProvider.setCoordinates(coordinates);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public TileOverlayOptions getTileOverlayOptions() {
        if (tileOverlayOptions == null) {
            tileOverlayOptions = createTileOverlayOptions();
        }
        return tileOverlayOptions;
    }

    protected abstract TileOverlayOptions createTileOverlayOptions();


    @Override
    public Object getFeature() {
        return tileOverlay;
    }

    @Override
    public void addToMap(GoogleMap map) {
        this.tileOverlay = map.addTileOverlay(getTileOverlayOptions());
    }

    @Override
    public void removeFromMap(GoogleMap map) {
        tileOverlay.remove();
    }
}
