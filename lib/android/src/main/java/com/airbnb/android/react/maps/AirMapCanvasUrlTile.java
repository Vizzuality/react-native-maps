package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.android.gms.maps.model.TileOverlayOptions;


public class AirMapCanvasUrlTile extends AirMapCanvasFeature {

    public AirMapCanvasUrlTile(Context context){ super(context); }

    class AIRMapCanvasUrlTileProvider extends AirMapCanvasTileProvider {

        public AIRMapCanvasUrlTileProvider(int width, int height, String urlTemplate, int maxZoom, String areaId, boolean isConnected, String minDate, String maxDate, String alertType, Coordinates coordinates)  {
            super(width, height, urlTemplate, maxZoom, areaId, isConnected, minDate, maxDate, alertType, coordinates);
        }

        protected Context getParentContext() {
            return getContext();
        }

        protected Bitmap paintTile(Bitmap scaledBitmap, int width, int height, int zoom, int zsteps, int minDate, int maxDate) {
            Bitmap finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int red, green, blue, pixel, alpha;
            int[] pixels = new int[width * height];
            scaledBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            for (int i = 0; i < pixels.length; i++) {
                pixel = pixels[i];

                red = (pixel >> 16) & 0xFF;
                green = (pixel >> 8) & 0xFF;
                blue = pixel & 0xFF;

                if (red > 255)
                    red = 255;
                if (green > 255)
                    green = 255;

                int day;
                if (this.alertType != null && this.alertType.equals("viirs")) {
                    day = blue;
                } else {
                    day = red * 255 + green;
                }

                if(this.alertType != null && this.alertType.equals("viirs")) {
                    if (day > 0) {
                        red = 244;
                        green = 66;
                        blue = 66;
                        alpha = 255;
                    } else {
                        alpha = 0;
                    }
                } else if (day > 0 && day >= minDate && day <= maxDate) {
                    red = 220;
                    green = 102;
                    blue = 153;
                    alpha = 255;
                } else {
                    alpha = 0;
                }

                pixels[i] = Color.argb(alpha, red, green, blue);
            }
            finalBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return finalBitmap;
        }


    }


    protected TileOverlayOptions createTileOverlayOptions() {
        TileOverlayOptions options = new TileOverlayOptions();
        options.zIndex(zIndex);
        this.tileProvider = new AIRMapCanvasUrlTileProvider(256, 256, this.urlTemplate, this.maxZoom, this.areaId,  this.isConnected, this.minDate, this.maxDate, this.alertType, null);
        options.tileProvider(this.tileProvider);
        return options;
    }

}
