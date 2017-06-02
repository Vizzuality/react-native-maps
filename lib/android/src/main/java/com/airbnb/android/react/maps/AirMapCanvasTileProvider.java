package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

/**
 * Created by joseangel.parreno@vizzuality.com on 19/05/2017.
 */


public abstract class AirMapCanvasTileProvider implements TileProvider {
    protected String urlTemplate;
    protected int width;
    protected int height;
    protected int maxZoom;
    protected String areaId;
    protected String minDate;
    protected String maxDate;
    protected boolean isConnected;
    protected Coordinates coordinates;
    protected String alertType;

    public AirMapCanvasTileProvider(int width, int height, String urlTemplate, int maxZoom, String areaId, boolean isConnected, String minDate, String maxDate, String alertType, Coordinates coordinates) {
        super();
        this.width = width;
        this.height = height;
        this.urlTemplate = urlTemplate;
        this.maxZoom = maxZoom;
        this.areaId = areaId;
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.alertType = alertType;
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

        int minDate = Integer.valueOf(this.minDate);
        int maxDate = Integer.valueOf(this.maxDate);

        if (this.coordinates != null) {
            int[] tile = this.coordinates.getTile();
            if(!(tile[0] == x && tile[1] == y && tile[2] == zoom)) {
                return NO_TILE;
            }
        }

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
            File dir = getParentContext().getFilesDir();
            File myFile = new File(dir + "/tiles", this.areaId + "/" + this.alertType + "/"+ zoomCord + "x" + xCord + "x" + yCord + ".png");

            try {
                image = BitmapFactory.decodeFile(myFile.getAbsolutePath(), options);
            } catch (Exception e) {
                e.printStackTrace();
                return NO_TILE;
            }
        }
        int zsteps = 1;

        if (zoom > this.maxZoom) {
            zsteps = zoom - this.maxZoom;
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

            int width, height;
            height = scaledBitmap.getHeight();
            width = scaledBitmap.getWidth();

            //

            int[] pixels = new int[width * height];
            scaledBitmap.getPixels(pixels, 0, width, 0, 0, width, height);


            Bitmap finalBitmap = paintTile(scaledBitmap, width, height, zoom, zsteps, minDate, maxDate);



            stream = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            bitmapData = stream.toByteArray();
            return new Tile(TILE_SIZE, TILE_SIZE, bitmapData);
        } else {
            return NO_TILE;
        }
    }

    protected abstract Context getParentContext();

    protected abstract Bitmap paintTile(Bitmap scaledBitmap, int width, int height, int zoom, int zsteps, int minDate, int maxDate);

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
