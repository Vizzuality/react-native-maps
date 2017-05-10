package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.lang.Math;

public class AirMapCanvasInteractionUrlTile extends AirMapFeature {
    private class Coordinates {
        private double lat;
        private double lng;
        private int[] tile;

        public Coordinates(double lat, double lng, int[] tile) {
            this.lat = lat;
            this.lng = lng;
            this.tile = tile;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public int[] getTile() {
            return tile;
        }
    }


    class AIRMapCanvasInteractionUrlTileProvider implements TileProvider {
        private String urlTemplate;
        private int width;
        private int height;
        private int maxZoom;
        private String areaId;
        private boolean isConnected;
        private Coordinates coordinates;
        public AIRMapCanvasInteractionUrlTileProvider(int width, int height, String urlTemplate, int maxZoom, String areaId, boolean isConnected, Coordinates coordinates) {
            super();
            this.width = width;
            this.height = height;
            this.urlTemplate = urlTemplate;
            this.maxZoom = maxZoom;
            this.areaId = areaId;
            this.isConnected = isConnected;
            this.coordinates = coordinates;
        }
        @Override
        public Tile getTile(int x, int y, int zoom) {
            int TILE_SIZE = this.width;
            int maxZoom = 12;
            int xCord = x;
            int yCord = y;
            int zoomCord = zoom;
            int srcX = 0;
            int srcY = 0;
            int srcW = this.width;
            int srcH = this.height;
            int scaleSize = 1;

            int[] tile = coordinates.getTile();
            Log.d("tile Position: ", x + "-" + y + "-" + zoom);
            Log.d("tile given: ", tile[0] + "-" + tile[1] + "-" + tile[2]);

            if (tile[0] == x && tile[1] == y && tile[2] == zoom) {

                if (zoom > this.maxZoom) {
                    xCord = (int)(x / (Math.pow(2, zoom - this.maxZoom)));
                    yCord = (int)(y / (Math.pow(2, zoom - this.maxZoom)));
                    zoomCord = this.maxZoom;
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] bitmapData = stream.toByteArray();

                Bitmap image;

                if (this.isConnected) {
                    String providerUrl = this.urlTemplate
                      .replace("{x}", Integer.toString(xCord))
                      .replace("{y}", Integer.toString(yCord))
                      .replace("{z}", Integer.toString(zoomCord));

                    URL url;
                    try {
                        url = new URL(providerUrl);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return NO_TILE;
                    }

                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    File dir = getContext().getFilesDir();
                    File myFile = new File(dir + "/tiles", areaId + "/" + zoomCord + "x" + xCord + "x" + yCord + ".png");

                    try {
                        image = BitmapFactory.decodeFile(myFile.getAbsolutePath(), options);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return NO_TILE;
                    }
                }

                if (zoom > this.maxZoom) {
                    int zsteps = zoom - this.maxZoom;
                    int relation = (int) Math.pow(2, zsteps) ;
                    int size = (int) (TILE_SIZE / relation);
                    // we scale the map to keep the tiles sharp
                    scaleSize = (int) (TILE_SIZE * 2);
                    srcX = (int) size  * (x % relation);
                    srcY = (int) size  * (y % relation);
                    srcW = (int) size;
                    srcH = (int) size;
                }

                if (image != null) {
                    Bitmap croppedBitmap = Bitmap.createBitmap(image , srcX , srcY, srcW, srcH);
                    Bitmap scaledBitmap = croppedBitmap;
                    if (zoom > maxZoom) {
                        // The last false is for filter anti-aliasing
                        scaledBitmap = Bitmap.createScaledBitmap (croppedBitmap, scaleSize, scaleSize, false);
                    }

                    int width, height, r, g, b, c;
                    height = scaledBitmap.getHeight();
                    width = scaledBitmap.getWidth();

                    Bitmap finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    int red, green, blue, pixel, alpha;
                    int[] pixels = new int[width * height];
                    scaledBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    for (int i = 0; i < pixels.length; i++) {
                        pixel = pixels[i];

                        red = (pixel >> 16) & 0xFF;
                        green = (pixel >> 8) & 0xFF;
                        blue = pixel & 0xFF;

                        int day = red * 255 + green;

                        red = 255;
                        green = 255;
                        blue = 255;
                        alpha = 100;

                        pixels[i] = Color.argb(alpha, red, green, blue);
                    }
                    finalBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                    stream = new ByteArrayOutputStream();
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    bitmapData = stream.toByteArray();
                    return new Tile(TILE_SIZE, TILE_SIZE, bitmapData);
                } else {
                    return NO_TILE;
                }
            } else {
              return NO_TILE;
            }
        }

        public void setUrlTemplate(String urlTemplate) {
            this.urlTemplate = urlTemplate;
        }

        public void setMaxZoom(int maxZoom) {
            this.maxZoom = maxZoom;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public void setIsConnected(boolean isConnected) {
            this.isConnected = isConnected;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }
    }

    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;
    private AIRMapCanvasInteractionUrlTileProvider tileProvider;

    private String urlTemplate;
    private int maxZoom;
    private String areaId;
    private String minDate;
    private String maxDate;
    private boolean isConnected;
    private float zIndex;
    private Coordinates coordinates;

    public AirMapCanvasInteractionUrlTile(Context context) {
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

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(String maxDate) {
        this.minDate = maxDate;
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
        if (tileOverlay != null) {
            tileOverlay.setZIndex(zIndex);
        }
    }

    public void setCoordinates(double lat, double lng, int[] tile) {
        this.coordinates = new Coordinates(lat, lng, tile);
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

    private TileOverlayOptions createTileOverlayOptions() {
        TileOverlayOptions options = new TileOverlayOptions();
        options.zIndex(zIndex);
        this.tileProvider = new AIRMapCanvasInteractionUrlTileProvider(256, 256, this.urlTemplate, this.maxZoom, this.areaId, this.isConnected, this.coordinates);
        options.tileProvider(this.tileProvider);
        return options;
    }

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
