package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

        public AIRMapLocalTileProvider(int width, int height, String localTemplate) {
            this.width = width;
            this.height = height;
            this.localTemplate = localTemplate;
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] bitmapData = stream.toByteArray();

            Bitmap image;

            String pathUrl = this.localTemplate
                    .replace("{x}", Integer.toString(x))
                    .replace("{y}", Integer.toString(y))
                    .replace("{z}", Integer.toString(zoom));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File dir = getContext().getFilesDir();
            File myFile = new File(dir + "/" + pathUrl);

            try {
                image = BitmapFactory.decodeFile(myFile.getAbsolutePath() + ".png", options);
            } catch (Exception e) {
                e.printStackTrace();
                return NO_TILE;
            }

            if (image == null) {
                try {
                    image = BitmapFactory.decodeFile(myFile.getAbsolutePath() + ".jpg", options);
                } catch (Exception e) {
                    e.printStackTrace();
                    return NO_TILE;
                }
            }

            if (image != null) {
                Bitmap finalBitmap = Bitmap.createBitmap(image , 0 , 0, this.width, this.height);
                stream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                bitmapData = stream.toByteArray();
                return new Tile(this.width, this.height, bitmapData);
            } else {
                return NO_TILE;
            }
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
