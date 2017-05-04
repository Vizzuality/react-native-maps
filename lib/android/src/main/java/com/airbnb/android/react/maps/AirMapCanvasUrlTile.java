package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.lang.Math;


public class AirMapCanvasUrlTile extends AirMapFeature {

    class AIRMapCanvasUrlTileProvider implements TileProvider {
        public AIRMapCanvasUrlTileProvider(int width, int height) {
            super();
        }
        @Override
        public Tile getTile(int x, int y, int zoom) {
            int w = 256, h = 256;
            int maxZoom = 12;
            int xCord = x;
            int yCord = y;
            int zoomCord = zoom;

            if (zoom > maxZoom) {
                xCord = (int)(x / (Math.pow(2, zoom - maxZoom)));
                yCord = (int)(y / (Math.pow(2, zoom - maxZoom)));
                zoomCord = maxZoom;
            }

            boolean online = false;


            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap coordTile = Bitmap.createBitmap(w, h, conf);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //coordTile.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();
            Uri.Builder builder = new Uri.Builder();
            Bitmap image;

            if (online) {

                builder.scheme("http")
                .authority("wri-tiles.s3.amazonaws.com")
                .appendPath("glad_prod")
                .appendPath("tiles")
                .appendPath(String.valueOf(zoomCord))
                .appendPath(String.valueOf(xCord))
                .appendPath(String.valueOf(yCord));
                String providerUrl = builder.build().toString() + ".png";

                URL url = null;
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
                File dir = Environment.getExternalStorageDirectory();
                File yourFile = new File(dir, "Download/" + zoomCord + "x" + xCord + "x" + yCord + ".png");

                try {
                    image = BitmapFactory.decodeFile(yourFile.getAbsolutePath(), options);
                } catch (Exception e) {
                    e.printStackTrace();
                    return NO_TILE;
                }
            }

            int srcX = 0;
            int srcY = 0;
            int srcW = w;
            int srcH = h;

            if (zoom > maxZoom) {
                int zsteps = zoom - maxZoom;
                int relation = (int) Math.pow(2, zsteps) ;
                int size = (int) (256 / relation);
                srcX = (int) size  * (x % relation);
                srcY = (int) size  * (y % relation);
                srcW = (int) size;
                srcH = (int) size;
            }

            //new DownloadTile(bitmapData)
            //       .execute(providerUrl);

            if (image != null) {
              try {

                  Bitmap resizedBitmap = Bitmap.createBitmap(image , srcX , srcY, srcW, srcH);

                  int width, height, r, g, b, c;
                  height = resizedBitmap.getHeight();
                  width = resizedBitmap.getWidth();

                  Bitmap finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                  int red, green, blue, pixel, alpha;
                  int[] pixels = new int[width * height];
                  resizedBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
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
                  finalBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                  stream = new ByteArrayOutputStream();
                  finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

                  bitmapData = stream.toByteArray();
              } catch (Exception e) {
                  e.printStackTrace();
              }
              return new Tile(256, 256, bitmapData);
            } else {
                return NO_TILE;
            }
        }
    }

    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;

    private String urlTemplate;
    private float zIndex;

    public AirMapCanvasUrlTile(Context context) {
        super(context);
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
        if (tileOverlay != null) {
            tileOverlay.setZIndex(zIndex);
        }
    }

    private class DownloadTile extends AsyncTask<String, Void, Bitmap> {
        private byte[] tile;

        public DownloadTile(byte[] tile) {
            this.tile = tile;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            Log.d("Title", String.valueOf(result));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.PNG, 0, stream);
            this.tile = stream.toByteArray();
            Tile newTile = new Tile(256, 256, this.tile);
        }
    }

    @Override
    public Object getFeature() {
        return tileOverlay;
    }

    @Override
    public void addToMap(GoogleMap map) {
        Log.d("Map", "Add to map");
        this.tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(new AIRMapCanvasUrlTileProvider(256, 256)));
    }

    @Override
    public void removeFromMap(GoogleMap map) {
        tileOverlay.remove();
    }
}
