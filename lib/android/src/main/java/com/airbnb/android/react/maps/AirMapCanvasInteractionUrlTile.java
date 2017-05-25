package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.android.gms.maps.model.TileOverlayOptions;


public class AirMapCanvasInteractionUrlTile extends AirMapCanvasFeature {

    public AirMapCanvasInteractionUrlTile(Context context){ super(context); }

    class AIRMapCanvasInteractionTileProvider extends AirMapCanvasTileProvider {

        public AIRMapCanvasInteractionTileProvider(int width, int height, String urlTemplate, int maxZoom, String areaId, boolean isConnected, String minDate, String maxDate, String alertType, Coordinates coordinates)  {
            super(width, height, urlTemplate, maxZoom, areaId, isConnected, minDate, maxDate, alertType, coordinates);
        }

        protected Context getParentContext() {
            return getContext();
        }

        protected Bitmap paintTile(Bitmap scaledBitmap, int width, int height, int zoom, int zsteps, int minDate, int maxDate) {
            Bitmap finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int red, green, blue, alpha, c;
            double[] precision = coordinates.getPrecision();
            int xFilter = (int)(precision[0] * width);
            int yFilter = (int)(precision[1] * height);
            double THRESHOLD = zoom * 0.5 * zsteps;


            for(int xPoint=0; xPoint < width; xPoint++) {
                for(int yPoint=0; yPoint < height; yPoint++) {
                    c = scaledBitmap.getPixel(xPoint, yPoint);

                    red = Color.red(c);
                    green = Color.green(c);
                    blue = Color.blue(c);

                    if (red > 255)
                        red = 255;
                    if (green > 255)
                        green = 255;

                    int day = red * 255 + green;
                    boolean inDay = day > 0 && day >= minDate && day <= maxDate;
                    boolean inXPoint = xPoint >= (xFilter - THRESHOLD) && (xPoint <= xFilter + THRESHOLD);
                    boolean inYPoint = yPoint >= (yFilter - THRESHOLD) && (yPoint <= yFilter + THRESHOLD);

                    if (inDay && inXPoint && inYPoint) {
                        red = 255;
                        green = 255;
                        blue = 255;
                        alpha = 150;
                    } else {
                        alpha = 0;
                    }

                    finalBitmap.setPixel(xPoint, yPoint, Color.argb(alpha, red, green, blue));
                }
            }
            return finalBitmap;
        }


    }


    protected TileOverlayOptions createTileOverlayOptions() {
        TileOverlayOptions options = new TileOverlayOptions();
        options.zIndex(zIndex);
        this.tileProvider = new AIRMapCanvasInteractionTileProvider(256, 256, this.urlTemplate, this.maxZoom, this.areaId,  this.isConnected, this.minDate, this.maxDate, this.alertType, this.coordinates);
        options.tileProvider(this.tileProvider);
        return options;
    }

}
