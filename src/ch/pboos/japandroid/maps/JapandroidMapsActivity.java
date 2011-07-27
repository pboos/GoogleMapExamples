
package ch.pboos.japandroid.maps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class JapandroidMapsActivity extends MapActivity implements OnClickListener {
    private MapView mMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.button_map_url).setOnClickListener(this);
        findViewById(R.id.button_map_loc).setOnClickListener(this);
        findViewById(R.id.button_show_image).setOnClickListener(this);
        findViewById(R.id.button_show_mapview).setOnClickListener(this);

        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);
    }

    @Override
    public void onClick(View v) {
        String latitude = "35.689479";
        String longitude = "139.707411";

        findViewById(R.id.image_map).setVisibility(View.GONE);
        findViewById(R.id.mapview).setVisibility(View.GONE);

        switch (v.getId()) {
            case R.id.button_map_url:
                openMapUrl(latitude, longitude);
                break;
            case R.id.button_map_loc:
                openMapGeo(latitude, longitude);
                break;
            case R.id.button_show_image:
                showImage(latitude, longitude);
                break;
            case R.id.button_show_mapview:
                showMap(latitude, longitude);
                break;
            default:
                break;
        }
    }

    private void showMap(String latitude, String longitude) {
        mMapView.setVisibility(View.VISIBLE);

        GeoPoint point = new GeoPoint((int) (Double.parseDouble(latitude) * 1E6),
                (int) (Double.parseDouble(longitude) * 1E6));

        // set up map with correct center and zoom
        MapController controller = mMapView.getController();
        controller.setCenter(point);
        controller.setZoom(16);

        // show marker
        List<Overlay> mapOverlays = mMapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.map_pin);
        MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(drawable);
        OverlayItem overlayitem = new OverlayItem(point, "", "");
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.clear(); // delete all overlays added before
        mapOverlays.add(itemizedOverlay);
    }

    private void showImage(String latitude, String longitude) {
        String loc = latitude + "," + longitude;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + loc
                + "&zoom=17&size=400x300&sensor=true&markers=" + loc;
        new DownloadImageTask(url).execute();
    }

    private void openMapGeo(String latitude, String longitude) {
        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + latitude + ","
                + longitude + "?z=17&q=" + latitude + "," + longitude));
        // replace the q with a search string works too. Example:
        // intent2 = new Intent(Intent.ACTION_VIEW,
        // Uri.parse("geo:0,0?q=Tonchidot"));
        startActivity(intent2);
    }

    private void openMapUrl(String latitude, String longitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?q="
                + latitude + "," + longitude));
        startActivity(intent);
    }

    // //////////////////
    // DownloadImageTask
    // //////////////////
    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
        private String mUrl;

        public DownloadImageTask(String url) {
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return BitmapFactory.decodeStream(new URL(mUrl).openStream());
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            findViewById(R.id.progress).setVisibility(View.GONE);
            if (result != null) {
                ImageView image = (ImageView) findViewById(R.id.image_map);
                image.setImageBitmap(result);
                image.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(JapandroidMapsActivity.this, "Could not download image",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
