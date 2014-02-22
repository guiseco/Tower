package org.droidplanner.helpers.geoTools;

import java.util.List;

import org.droidplanner.helpers.coordinates.Coord2D;
import org.droidplanner.helpers.units.Area;
import org.droidplanner.helpers.units.Length;
import org.droidplanner.polygon.Polygon;

import com.google.android.gms.maps.model.LatLng;

public class GeoTools {
	private static final double RADIUS_OF_EARTH = 6372797.560856d;
	public List<LatLng> waypoints;

	public GeoTools() {
	}

	/**
	 * Returns the distance between two points
	 * 
	 * @return distance between the points in degrees
	 */
	public static Double getAproximatedDistance(Coord2D p1, Coord2D p2) {
		return (Math.hypot((p1.getX() - p2.getX()),
				(p1.getY() - p2.getY())));
	}

	public static Double metersTolat(double meters) {
		double radius_of_earth = 6378100.0;// # in meters
		return Math.toDegrees(meters / radius_of_earth);
	}

	public static Double latToMeters(double lat) {
		double radius_of_earth = 6378100.0;// # in meters
		return Math.toRadians(lat) * radius_of_earth;
	}

	/**
	 * Extrapolate latitude/longitude given a heading and distance thanks to
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * @param origin
	 *            Point of origin
	 * @param bearing
	 *            bearing to navigate
	 * @param distance
	 *            distance to be added
	 * @return New point with the added distance
	 */
	public static LatLng newCoordFromBearingAndDistance(LatLng origin,
			double bearing, double distance) {

		double lat = origin.latitude;
		double lon = origin.longitude;
		double lat1 = Math.toRadians(lat);
		double lon1 = Math.toRadians(lon);
		double brng = Math.toRadians(bearing);
		double dr = distance / RADIUS_OF_EARTH;

		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dr) + Math.cos(lat1)
				* Math.sin(dr) * Math.cos(brng));
		double lon2 = lon1
				+ Math.atan2(Math.sin(brng) * Math.sin(dr) * Math.cos(lat1),
						Math.cos(dr) - Math.sin(lat1) * Math.sin(lat2));

		return (new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2)));
	}

	/**
	 * Calculates the arc between two points
	 * http://en.wikipedia.org/wiki/Haversine_formula
	 * 
	 * @return the arc in degrees
	 */
	static double getArcInRadians(LatLng from, LatLng to) {

		double latitudeArc = Math.toRadians(from.latitude - to.latitude);
		double longitudeArc = Math.toRadians(from.longitude - to.longitude);

		double latitudeH = Math.sin(latitudeArc * 0.5);
		latitudeH *= latitudeH;
		double lontitudeH = Math.sin(longitudeArc * 0.5);
		lontitudeH *= lontitudeH;

		double tmp = Math.cos(Math.toRadians(from.latitude))
				* Math.cos(Math.toRadians(to.latitude));
		return Math.toDegrees(2.0 * Math.asin(Math.sqrt(latitudeH + tmp
				* lontitudeH)));
	}

	/**
	 * Computes the distance between two coordinates
	 * 
	 * @return distance in meters
	 */
	public static Length getDistance(LatLng from, LatLng to) {
		return new Length(RADIUS_OF_EARTH * Math.toRadians(getArcInRadians(from, to)));
	}

	/**
	 * Computes the heading between two coordinates
	 * 
	 * @return heading in degrees
	 */
	static double getHeadingFromCoordinates(LatLng fromLoc, LatLng toLoc) {
		double fLat = Math.toRadians(fromLoc.latitude);
		double fLng = Math.toRadians(fromLoc.longitude);
		double tLat = Math.toRadians(toLoc.latitude);
		double tLng = Math.toRadians(toLoc.longitude);

		double degree = Math.toDegrees(Math.atan2(
				Math.sin(tLng - fLng) * Math.cos(tLat),
				Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat)
						* Math.cos(tLat) * Math.cos(tLng - fLng)));

		if (degree >= 0) {
			return degree;
		} else {
			return 360 + degree;
		}
	}

	/**
	 * Experimental Function, needs testing! Calculate the area of the polygon
	 * 
	 * @return area in m�
	 */
	// TODO test and fix this function
	public static Area getArea(Polygon poly) {
		double sum = 0.0;
		int length = poly.getPoints().size();
		for (int i = 0; i < length - 1; i++) {
			sum = sum
					+ (latToMeters(poly.getPoints().get(i).getX()) * latToMeters(poly
							.getPoints().get(i + 1).getY()))
					- (latToMeters(poly.getPoints().get(i).getY()) * latToMeters(poly
							.getPoints().get(i + 1).getX()));
		}
		sum = sum 
				+ (latToMeters(poly.getPoints().get(length-1).getX()) * latToMeters(poly
				.getPoints().get(0).getY()))
				- (latToMeters(poly.getPoints().get(length-1).getY()) * latToMeters(poly
						.getPoints().get(0).getX()));
		return new Area(Math.abs(0.5 * sum));
		//return new Area(0);
	}


}