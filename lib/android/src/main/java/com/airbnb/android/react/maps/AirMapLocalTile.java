package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class AirMapLocalTile extends AirMapFeature {

    class AIRMapLocalTileProvider implements TileProvider {
        private int width;
        private int height;
        private String localTemplate;
        private int maxZoom;

        public AIRMapLocalTileProvider(int width, int height, String localTemplate, int maxZoom) {
            this.width = width;
            this.height = height;
            this.localTemplate = localTemplate;
            this.maxZoom = maxZoom;
        }

        private Bitmap readTileFromFile(String pathUrl) {
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
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            return image;
        }

        private Bitmap getRescaledTileBitmap(Bitmap image, int x, int y, int z, int tileSize) {
            int zSteps = z - this.maxZoom;
            int relation = (int) Math.pow(2, zSteps);
            int cropSize = (tileSize / relation);
            int cropX = (x % relation) * (tileSize / relation);
            int cropY = (y % relation) * (tileSize / relation);

            Log.i("*************", Integer.toString(cropX));
            Log.i("*************", Integer.toString(cropY));
            Bitmap croppedBitmap = Bitmap.createBitmap(image, cropX, cropY, cropSize, cropSize);
            return Bitmap.createScaledBitmap(croppedBitmap, tileSize * relation, tileSize * relation, false);
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            Bitmap image;
            int xCoord = x;
            int yCoord = y;
            int zCoord = zoom;
            boolean shouldRescaleTile = (zoom > this.maxZoom);

            if (shouldRescaleTile) {
                int zSteps = zCoord - this.maxZoom;
                int relation = (int) Math.pow(2, zSteps) ;
                xCoord = (int)(x / relation);
                yCoord = (int)(y / relation);
                zCoord = this.maxZoom;
            }

            String pathUrl = this.localTemplate
                    .replace("{x}", Integer.toString(xCoord))
                    .replace("{y}", Integer.toString(yCoord))
                    .replace("{z}", Integer.toString(zCoord));
            Log.i("::::::::::::", pathUrl);
            image = this.readTileFromFile(pathUrl);
            if (image == null) {
                return NO_TILE;
            }

            Bitmap finalBitmap = null;
            if (shouldRescaleTile) {
                finalBitmap = this.getRescaledTileBitmap(image, x, y, zoom, this.width);
            } else {
                finalBitmap = Bitmap.createBitmap(image , 0 , 0, this.width, this.height);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] bitmapData = stream.toByteArray();
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
    private int maxZoom;

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

    public void setMaxZoom(int maxZoom) {
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
        this.tileProvider = new AIRMapLocalTileProvider(256, 256, this.localTemplate, this.maxZoom);
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
