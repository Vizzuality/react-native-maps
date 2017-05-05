package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.AsyncTask;
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


public class AirMapCanvasUrlTile extends AirMapFeature {

    class AIRMapCanvasUrlTileProvider implements TileProvider {
        private String urlTemplate;
        private int width;
        private int height;
        private int maxZoom;
        private String areaName;
        private boolean isConnected;
        public AIRMapCanvasUrlTileProvider(int width, int height, String urlTemplate, int maxZoom, String areaName, boolean isConnected) {
            super();
            this.width = width;
            this.height = height;
            this.urlTemplate = urlTemplate;
            this.maxZoom = maxZoom;
            this.areaName = areaName;
            this.isConnected = isConnected;
        }
        @Override
        public Tile getTile(int x, int y, int zoom) {            
            int TILE_SIZE = this.width;
            try{
                byte[] bitmapData = new BackgroundTask().execute(String.valueOf(this.isConnected), Integer.toString(x), Integer.toString(y), 
                                                                 Integer.toString(zoom), Integer.toString(width), Integer.toString(height),
                                                                 this.areaName, this.urlTemplate).get();
                if (bitmapData == null){
                    return null;
                } else {
                    return new Tile(TILE_SIZE, TILE_SIZE, bitmapData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void setUrlTemplate(String urlTemplate) {
            this.urlTemplate = urlTemplate;
        }

        public void setMaxZoom(int maxZoom) {
            this.maxZoom = maxZoom;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public void setIsConnected(boolean isConnected) {
            this.isConnected = isConnected;
        }
    }
    private class BackgroundTask extends AsyncTask<String, Integer, byte[]> {
        @Override
        protected byte[] doInBackground(String... response) {
            Boolean isConnected =  Boolean.parseBoolean(response[0]);
            int x = Integer.valueOf(response[1]);
            int y = Integer.valueOf(response[2]);
            int zoom = Integer.valueOf(response[3]);
            int width = Integer.valueOf(response[4]);
            int height = Integer.valueOf(response[5]);
            String areaName = response[6];
            String urlTemplate = response[7];
            
            int TILE_SIZE = width;
            int maxZoom = 12;
            int xCord = x;
            int yCord = y;
            int zoomCord = zoom;
            int srcX = 0;
            int srcY = 0;
            int srcW = width;
            int srcH = height;
            int scaleSize = 1;
                Log.e("x", String.valueOf(x));
                Log.e("y", String.valueOf(y));

            if (zoom > maxZoom) {
                xCord = (int)(x / (Math.pow(2, zoom - maxZoom)));
                yCord = (int)(y / (Math.pow(2, zoom - maxZoom)));
                zoomCord = maxZoom;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] bitmapData = stream.toByteArray();
            Uri.Builder builder = new Uri.Builder();
            Bitmap image = null;
            byte[] finalBitmapData = null;



            if (isConnected) {
                String providerUrl = urlTemplate
                  .replace("{x}", String.valueOf(xCord))
                  .replace("{y}", String.valueOf(yCord))
                  .replace("{z}", String.valueOf(zoomCord));

                URL url;
                try {
                    url = new URL(providerUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                File dir = Environment.getExternalStorageDirectory();
                File myFile = new File(dir, areaName + "/" + zoomCord + "x" + xCord + "x" + yCord + ".png");

                try {
                    image = BitmapFactory.decodeFile(myFile.getAbsolutePath(), options);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }


            if (zoom > maxZoom) {
                int zsteps = zoom - maxZoom;
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

                int w, h, r, g, b, c;
                h = scaledBitmap.getHeight();
                w = scaledBitmap.getWidth();

                Bitmap finalBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                int red, green, blue, pixel, alpha;
                int[] pixels = new int[w * h];
                scaledBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
                for (int i = 0; i < pixels.length; i++) {
                    pixel = pixels[i];

                    red = (pixel >> 16) & 0xFF;
                    green = (pixel >> 8) & 0xFF;
                    blue = pixel & 0xFF;

                    int day = red * 255 + green;

                    if (red > 255)
                        red = 255;
                    if (green > 255)
                        green = 255;

                    if (day > 0) {
                        red = 220;
                        green = 102;
                        blue = 153;
                        alpha = 255;
                    } else {
                        alpha = 0;
                    }

                    pixels[i] = Color.argb(alpha, red, green, blue);
                }
                finalBitmap.setPixels(pixels, 0, w, 0, 0, w, h);

                stream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                bitmapData = stream.toByteArray();
                finalBitmapData = bitmapData;
            } else {
                finalBitmapData = null;
            }
            return finalBitmapData;
        }
    }
    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;
    private AIRMapCanvasUrlTileProvider tileProvider;

    private String urlTemplate;
    private int maxZoom;
    private String areaName;
    private String minDate;
    private String maxDate;
    private boolean isConnected;
    private float zIndex;

    public AirMapCanvasUrlTile(Context context) {
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

    public void setAreaName(String areaName) {
        this.areaName = areaName;
        if (tileProvider != null) {
            tileProvider.setAreaName(areaName);
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

    public TileOverlayOptions getTileOverlayOptions() {
        if (tileOverlayOptions == null) {
            tileOverlayOptions = createTileOverlayOptions();
        }
        return tileOverlayOptions;
    }

    private TileOverlayOptions createTileOverlayOptions() {
        TileOverlayOptions options = new TileOverlayOptions();
        options.zIndex(zIndex);
        this.tileProvider = new AIRMapCanvasUrlTileProvider(256, 256, this.urlTemplate, this.maxZoom, this.areaName, this.isConnected);
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
