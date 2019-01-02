package com.example.bright.RescueHelper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

final class MapHelper {
    static final LatLng LA_LOCATION = new LatLng(34.052235, -118.243683);

    /**
     * In kilometers.
     */
    private static final int EARTH_RADIUS = 6371;

    public MapHelper() {
        //no instance
    }

    static PolygonOptions createPolygonWithCircle(Context context, LatLng center, float radius) {

        return new PolygonOptions()
                .fillColor(ContextCompat.getColor(context, R.color.grey_500_transparent))
                .addAll(createOuterBounds())
                //.addAll(HOLE_COORDINATES)
                .strokeWidth(0);
    }

    public static List<LatLng> createOuterBounds() {
       final float delta = 0.01f;

        return new ArrayList<LatLng>() {{
            add(new LatLng(90 - delta, -180 + delta));
            add(new LatLng(0, -180 + delta));
            add(new LatLng(-90 + delta, -180 + delta));
            add(new LatLng(-90 + delta, 0));
            add(new LatLng(-90 + delta, 180 - delta));
            add(new LatLng(0, 180 - delta));
            add(new LatLng(90 - delta, 180 - delta));
            add(new LatLng(90 - delta, 0));
            add(new LatLng(90 - delta, -180 + delta));
        }};
    }

    public static Iterable<LatLng> createHole(LatLng center, float radius) {
        int points = 50; // number of corners of inscribed polygon

        double radiusLatitude = Math.toDegrees(radius / (float) EARTH_RADIUS);
        double radiusLongitude = radiusLatitude / Math.cos(Math.toRadians(center.latitude));

        List<LatLng> result = new ArrayList<>(points);

        double anglePerCircleRegion = 2 * Math.PI / points;

        for (int i = 0; i < points; i++) {
            double theta = i * anglePerCircleRegion;
            double latitude = center.latitude + (radiusLatitude * Math.sin(theta));
            double longitude = center.longitude + (radiusLongitude * Math.cos(theta));

            result.add(new LatLng(latitude, longitude));
        }

        return result;
    }

    static final List<List<LatLng>> HOLE_COORDINATES = new ArrayList<List<LatLng>>() {
        {
            add(new ArrayList<>(new ArrayList<LatLng>() {
                {
                    add(new LatLng(25.256531695820797, 55.30084858315658));
                    add(new LatLng(25.252243254705405, 55.298280197635705));
                    add(new LatLng(25.250501032248863, 55.30163885563897));
                    add(new LatLng(25.254700192612702, 55.304059065092645));
                    add(new LatLng(25.256531695820797, 55.30084858315658));
                }
            }));
            add(new ArrayList<>(new ArrayList<LatLng>() {
                {
                    add(new LatLng(25.262517391695198, 55.30173763969924));
                    add(new LatLng(25.26122200491396, 55.301095543307355));
                    add(new LatLng(25.259479911263526, 55.30396028103232));
                    add(new LatLng(25.261132667394975, 55.30489872958182));
                    add(new LatLng(25.262517391695198, 55.30173763969924));
                }
            }));
        }
    };
}