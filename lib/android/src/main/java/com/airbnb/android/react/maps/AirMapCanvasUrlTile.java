package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class AirMapCanvasUrlTile extends AirMapFeature {

    class AIRMapCanvasUrlTileProvider implements TileProvider {
        public AIRMapCanvasUrlTileProvider(int width, int height) {
            super();
        }
        @Override
        public Tile getTile(int x, int y, int zoom) {
            int w = 256, h = 256;

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap coordTile = Bitmap.createBitmap(w, h, conf);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //coordTile.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("wri-tiles.s3.amazonaws.com")
                    .appendPath("glad_prod")
                    .appendPath("tiles")
                    .appendPath(String.valueOf(zoom))
                    .appendPath(String.valueOf(x))
                    .appendPath(String.valueOf(y));
            String providerUrl = builder.build().toString() + ".png";

            Log.d("Tiles", providerUrl);


            //new DownloadTile(bitmapData)
            //       .execute(providerUrl);

            URL url = null;
            try {
                url = new URL(providerUrl);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                int width, height, r,g, b, c;
                height = image.getHeight();
                width = image.getWidth();

                Bitmap bmpSephia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                /*Canvas canvas = new Canvas(bmpSephia);
                Paint paint = new Paint();
                paint.setAntiAlias(true);

                canvas.drawBitmap(image, 0, 0, paint);*/
                /*
                for(int e=0; e < width; e++) {
                    for(int f=0; f < height; f++) {
                        c = image.getPixel(e, f);
                        int day = Color.red(c) * 255 + Color.green(c);

                        if (day > 0 ) {
                            r = 220;
                            g = 102;
                            b = 153;

                            bmpSephia.setPixel(e, f, Color.rgb(r, g, b));
                        } else {
                            bmpSephia.setPixel(e, f, Color.argb(0, 0, 0 ,0));
                        }
                    }
                }*/


                stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 0, stream);
                bitmapData = stream.toByteArray();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new Tile(256, 256, bitmapData);
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
