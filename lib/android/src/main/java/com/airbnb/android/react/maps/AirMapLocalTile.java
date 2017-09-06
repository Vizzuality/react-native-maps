package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class AirMapLocalTile extends AirMapFeature {

    class AIRMapLocalTileProvider implements TileProvider {
        private int width;
        private int height;
        private String localTemplate;
        private int maxZoom;

        private readTileFromFile(String pathUrl) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File dir = getContext().getFilesDir();
            File myFile = new File(dir + "/" + pathUrl);
            Bitmap image = null;

            try {
                image = BitmapFactory.decodeFile(myFile.getAbsolutePath() + ".png", options);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            if (image == null) {
                try {
                    image = BitmapFactory.decodeFile(myFile.getAbsolutePath() + ".jpg", options);
                } else (Exception e) {
                    //e.printStackTrace();
                }
            }
            return image;
        }

        private getRescaledTileBitmap(Bitmap image, int x, int y, int z, int tileSize) {
            int zSteps = z - this.maxZoom;
            int relation = (int) Math.pow(2, zsteps) ;
            int size = (int) (tileSize / relation);
            // we scale the map to keep the tiles sharp
            int scaleSize = (int) (tileSize * 2);
            srcX = (int) size  * (x % relation);
            srcY = (int) size  * (y % relation);
            srcW = (int) size;
            srcH = (int) size;

            Bitmap croppedBitmap = Bitmap.createBitmap(image, srcX, srcY, srcW, srcH);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, scaleSize, scaleSize, false);
        }

        public AIRMapLocalTileProvider(int width, int height, String localTemplate, int maxZoom) {
            this.width = width;
            this.height = height;
            this.localTemplate = localTemplate;
            this.maxZoom = maxZoom;
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] bitmapData = stream.toByteArray();
            Bitmap image;
            int xCoord = x;
            int yCoord = y;
            int zCoord = zoom;
            boolean shouldRescaleTile = (boolean)(zoom > this.maxZoom);

            if (shouldRescaleTile) {
                xCoord = (int)(x / (Math.pow(2, zoom - this.maxZoom)));
                yCoord = (int)(y / (Math.pow(2, zoom - this.maxZoom)));
                zCoord = this.maxZoom;
            }

            String pathUrl = this.localTemplate
                    .replace("{x}", Integer.toString(xCoord))
                    .replace("{y}", Integer.toString(yCoord))
                    .replace("{z}", Integer.toString(zCoord));

            Log.i('hehehe', pathUrl);

            image = this.readTileFromFile(pathUrl);
            if (image == null) {
                return NO_TILE;
            }

            Bitmap finalBitmap = null;
            if (shouldRescaleTile) {
                finalBitmap = this.getRescaledTileBitmap(image, xCoord, yCoord, zCoord, this.width);
            } else {
                finalBitmap = Bitmap.createBitmap(image , 0 , 0, this.width, this.height);
            }
            stream = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            bitmapData = stream.toByteArray();
            return new Tile(this.width, this.height, bitmapData);
        }

        public void setLocalTemplate(String localTemplate) {
            this.localTemplate = localTemplate;
        }
    }

    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;
    private AIRMapLocalTileProvider tileProvider;

    private String localTemplate;
    private float zIndex;

    public AirMapLocalTile(Context context) {
        super(context);
    }

    public void setLocalTemplate(String localTemplate) {
        this.localTemplate = localTemplate;
        if (tileProvider != null) {
            tileProvider.setLocalTemplate(localTemplate);
        }
        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
        if (tileOverlay != null) {
            tileOverlay.setZIndex(zIndex);
        }
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
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
        this.tileProvider = new AIRMapLocalTileProvider(256, 256, this.localTemplate);
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
