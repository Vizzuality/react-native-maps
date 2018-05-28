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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AirMapLocalTile extends AirMapFeature {

    class AIRMapLocalTileProvider implements TileProvider {
        private int maxZoom;
        private static final int MEM_MAX_SIZE = 8;
        private static final int BUFFER_SIZE = 16 * 1024;
        private int tileSize;
        private String pathTemplate;


        public AIRMapLocalTileProvider(int tileSizet, String pathTemplate, int maxZoom) {
            this.tileSize = tileSizet;
            this.pathTemplate = pathTemplate;
            this.maxZoom = maxZoom;
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            byte[] image = readTileImage(x, y, zoom);
            boolean shouldRescaleTile = (zoom > this.maxZoom);

            if (image == null) {
                return TileProvider.NO_TILE;
            }

            if (shouldRescaleTile) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                Bitmap finalBitmap = this.getRescaledTileBitmap(bitmap, x, y, zoom);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] bitmapData = stream.toByteArray();
                return new Tile(this.tileSize, this.tileSize, bitmapData);
            }
            return new Tile(this.tileSize, this.tileSize, image);
        }

        public void setPathTemplate(String pathTemplate) {
            this.pathTemplate = pathTemplate;
        }

        public void setTileSize(int tileSize) {
            this.tileSize = tileSize;
        }

        private byte[] readTileImage(int x, int y, int zoom) {
            int xCoord = x;
            int yCoord = y;
            int zCoord = zoom;
            boolean shouldRescaleTile = (zoom > this.maxZoom);
            if (shouldRescaleTile) {
                int zSteps = zCoord - this.maxZoom;
                int relation = (int) Math.pow(2, zSteps) ;
                xCoord = (x / relation);
                yCoord = (y / relation);
                zCoord = this.maxZoom;
            }

            InputStream in = null;
            ByteArrayOutputStream buffer = null;
            File dir = getContext().getFilesDir();
            File file = new File(dir + "/" + getTileFilename(xCoord, yCoord, zCoord));

            try {
                in = new FileInputStream(file);
                buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[BUFFER_SIZE];

                while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            } finally {
                if (in != null) try { in.close(); } catch (Exception ignored) {}
                if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
            }
        }

        private String getTileFilename(int x, int y, int zoom) {
            String s = this.pathTemplate
                    .replace("{x}", Integer.toString(x))
                    .replace("{y}", Integer.toString(y))
                    .replace("{z}", Integer.toString(zoom));
            return s;
        }

        private Bitmap getRescaledTileBitmap(Bitmap image, int x, int y, int z) {
            int zSteps = z - this.maxZoom;
            int relation = (int) Math.pow(2, zSteps);
            int cropSize = (this.tileSize / relation);
            int cropX = (x % relation) * (this.tileSize / relation);
            int cropY = (y % relation) * (this.tileSize / relation);
            int scaleSize = (relation <= MEM_MAX_SIZE) ? tileSize * relation : tileSize * MEM_MAX_SIZE;
            Bitmap croppedBitmap = Bitmap.createBitmap(image, cropX, cropY, cropSize, cropSize);
            return Bitmap.createScaledBitmap(croppedBitmap, scaleSize, scaleSize, false);
        }
    }

    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;
    private AirMapLocalTile.AIRMapLocalTileProvider tileProvider;

    private String pathTemplate;
    private float tileSize;
    private float zIndex;
    private int maxZoom;

    public AirMapLocalTile(Context context) {
        super(context);
    }

    public void setPathTemplate(String pathTemplate) {
        this.pathTemplate = pathTemplate;
        if (tileProvider != null) {
            tileProvider.setPathTemplate(pathTemplate);
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

    public void setTileSize(float tileSize) {
        this.tileSize = tileSize;
        if (tileProvider != null) {
            tileProvider.setTileSize((int)tileSize);
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
        this.tileProvider = new AirMapLocalTile.AIRMapLocalTileProvider((int)this.tileSize, this.pathTemplate, this.maxZoom);
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
