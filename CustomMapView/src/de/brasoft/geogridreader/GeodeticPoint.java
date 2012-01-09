/*
 * GeodeticPoint.java
 *
 * Created on 22. Februar 2005, 19:02
 */

package de.brasoft.geogridreader;

import java.util.Locale;


/**
 *
 * @author peter
 */
public class GeodeticPoint {
    
    double GeoLat;
    double GeoLon;
    double GeoHgt;
    
    /** Creates a new instance of GeodeticPoint */
    public GeodeticPoint() {
    }
    
    public GeodeticPoint(double pLat, double pLon, double pHgt) {
        GeoLat = pLat * Math.PI / 180.0;
        GeoLon = pLon * Math.PI / 180.0;
        GeoHgt = pHgt;
    }

    //-------------------------------------------------------------------------
    public String toString() {
        String retw = new String();
        retw = String.format("%.5f:%.5f",Math.toDegrees(GeoLon), Math.toDegrees(GeoLat));
        return retw;
    }
    
    //-------------------------------------------------------------------------
    public String toStrLon() {
        String retw = new String();
        retw = String.format(Locale.US, "%.5f O", Math.toDegrees(GeoLon));
        return retw;
    }
            
    //-------------------------------------------------------------------------
    public String toStrLonMin() {
        String retw = new String();
        int grad = (int)(Math.floor(Math.toDegrees(GeoLon)));
        retw = String.format(Locale.US, "%d� %.3f' O", grad,
                (Math.toDegrees(GeoLon)-grad)*60);
        return retw;
    }
            
    //-------------------------------------------------------------------------
    public String toStrLonSek() {
        String retw = new String();
        int grad = (int)(Math.floor(Math.toDegrees(GeoLon)));
        int min = (int) (Math.floor((Math.toDegrees(GeoLon)-grad)*60));
        int sek = (int) Math.floor((Math.toDegrees(GeoLon)*60-(grad*60+min))*60);
        retw = String.format(Locale.US, "%d� %d' %d'' O", grad, min, sek);
        return retw;
    }
            
    //-------------------------------------------------------------------------
    public String toStrLat() {
        String retw = new String();
        retw = String.format(Locale.US, "%.5f N", Math.toDegrees(GeoLat));
        return retw;
    }
            
    //-------------------------------------------------------------------------
    public String toStrLatMin() {
        String retw = new String();
        int grad = (int)(Math.floor(Math.toDegrees(GeoLat)));
        retw = String.format(Locale.US, "%d� %.3f' N", grad,
                (Math.toDegrees(GeoLat)-grad)*60);
        return retw;
    }
            
    //-------------------------------------------------------------------------
    public String toStrLatSek() {
        String retw = new String();
        int grad = (int)(Math.floor(Math.toDegrees(GeoLat)));
        int min = (int) (Math.floor((Math.toDegrees(GeoLat)-grad)*60));
        int sek = (int) Math.floor((Math.toDegrees(GeoLat)*60-(grad*60+min))*60);
        retw = String.format(Locale.US, "%d� %d' %d'' N", grad, min, sek);
        return retw;
    }
            
    //-------------------------------------------------------------------------
    private void Molodensky_Shift( double a,
            double da,
            double f,
            double df,
            double dx,
            double dy,
            double dz,
            double Lat_in,
            double Lon_in,
            double Hgt_in,
            GeodeticPoint out)
            
    { /* Begin Molodensky_Shift */
  /*
   *  This function shifts geodetic coordinates using the Molodensky method.
   *
   *    a         : Semi-major axis of source ellipsoid in meters  (input)
   *    da        : Destination a minus source a                   (input)
   *    f         : Flattening of source ellipsoid                 (input)
   *    df        : Destination f minus source f                   (input)
   *    dx        : X coordinate shift in meters                   (input)
   *    dy        : Y coordinate shift in meters                   (input)
   *    dz        : Z coordinate shift in meters                   (input)
   *    Lat_in    : Latitude in radians.                           (input)
   *    Lon_in    : Longitude in radians.                          (input)
   *    Hgt_in    : Height in meters.                              (input)
   *    Lat_out   : Calculated latitude in radians.                (output)
   *    Lon_out   : Calculated longitude in radians.               (output)
   *    Hgt_out   : Calculated height in meters.                   (output)
   */
        double tLon_in;   /* temp longitude                                   */
        double e2;        /* Intermediate calculations for dp, dl               */
        double ep2;       /* Intermediate calculations for dp, dl               */
        double sin_Lat;   /* sin(Latitude_1)                                    */
        double sin2_Lat;  /* (sin(Latitude_1))^2                                */
        double sin_Lon;   /* sin(Longitude_1)                                   */
        double cos_Lat;   /* cos(Latitude_1)                                    */
        double cos_Lon;   /* cos(Longitude_1)                                   */
        double w2;        /* Intermediate calculations for dp, dl               */
        double w;         /* Intermediate calculations for dp, dl               */
        double w3;        /* Intermediate calculations for dp, dl               */
        double m;         /* Intermediate calculations for dp, dl               */
        double n;         /* Intermediate calculations for dp, dl               */
        double dp;        /* Delta phi                                          */
        double dp1;       /* Delta phi calculations                             */
        double dp2;       /* Delta phi calculations                             */
        double dp3;       /* Delta phi calculations                             */
        double dl;        /* Delta lambda                                       */
        double dh;        /* Delta height                                       */
        double dh1;       /* Delta height calculations                          */
        double dh2;       /* Delta height calculations                          */
        
        if (Lon_in > Math.PI)
            tLon_in = Lon_in - (2*Math.PI);
        else
            tLon_in = Lon_in;
        e2 = 2 * f - f * f;
        ep2 = e2 / (1 - e2);
        sin_Lat = Math.sin(Lat_in);
        cos_Lat = Math.cos(Lat_in);
        sin_Lon = Math.sin(tLon_in);
        cos_Lon = Math.cos(tLon_in);
        sin2_Lat = sin_Lat * sin_Lat;
        w2 = 1.0 - e2 * sin2_Lat;
        w = Math.sqrt(w2);
        w3 = w * w2;
        m = (a * (1.0 - e2)) / w3;
        n = a / w;
        dp1 = cos_Lat * dz - sin_Lat * cos_Lon * dx - sin_Lat * sin_Lon * dy;
        dp2 = ((e2 * sin_Lat * cos_Lat) / w) * da;
        dp3 = sin_Lat * cos_Lat * (2.0 * n + ep2 * m * sin2_Lat) * (1.0 - f) * df;
        dp = (dp1 + dp2 + dp3) / (m + Hgt_in);
        dl = (-sin_Lon * dx + cos_Lon * dy) / ((n + Hgt_in) * cos_Lat);
        dh1 = (cos_Lat * cos_Lon * dx) + (cos_Lat * sin_Lon * dy) + (sin_Lat * dz);
        dh2 = -(w * da) + ((a * (1 - f)) / w) * sin2_Lat * df;
        dh = dh1 + dh2;
        out.GeoLat = Lat_in + dp;
        out.GeoLon = Lon_in + dl;
        out.GeoHgt = Hgt_in + dh;
        if (out.GeoLon > (Math.PI * 2))
            out.GeoLon -= 2*Math.PI;
        if (out.GeoLon < (- Math.PI))
            out.GeoLon += 2*Math.PI;
    } /* End Molodensky_Shift */
    

    //-------------------------------------------------------------------------
    void Geodetic_Shift_Potsdam_To_WGS84(GeodeticPoint WGS84_out)
    
    { /* Begin Geodetic_Shift_To_WGS84 */
  /*
   *  This function shifts geodetic coordinates relative to a given source datum
   *  to geodetic coordinates relative to WGS84.
   *
   *    Lat_in    : Latitude in radians relative to source datum  (input)
   *    Lon_in    : Longitude in radians relative to source datum (input)
   *    Hgt_in    : Height in meters relative to source datum     (input)
   *    WGS84_Lat : Latitude in radians relative to WGS84         (output)
   *    WGS84_Lon : Longitude in radians relative to WGS84        (output)
   *    WGS84_Hgt : Height in meters relative to WGS84            (output)
   */
        EllipsoidPar WGS84 = new EllipsoidPar();   /* Semi-major axis of WGS84 ellipsoid in meters */
        /* Flattening of WGS84 ellisoid                 */
        EllipsoidPar local = new EllipsoidPar();   /* Semi-major axis of ellipsoid in meters       */
        /* Flattening of ellipsoid                      */
        double da;                        /* Difference in semi-major axes                */
        double df;                        /* Difference in flattening                     */
        Shift3Param Shift = new Shift3Param();
        
        /* Use Molodensky's method */
        GeoParameter.WGS84_Parameters(WGS84);
        GeoParameter.Bessel1841_Parameters(local);
        da = WGS84.a - local.a;
        df = WGS84.f - local.f;
        GeoParameter.Potsdam_3Param_Shift(Shift);
        Molodensky_Shift(local.a, da, local.f, df, Shift.dx, Shift.dy, Shift.dz,
                GeoLat, GeoLon, GeoHgt, WGS84_out);
    } /* End Geodetic_Shift_To_WGS84 */
    
    
    
    //-------------------------------------------------------------------------
    void Geodetic_Shift_WGS84_To_Potsdam(GeodeticPoint Potsdam_out)
            
    { /* Begin Geodetic_Shift_From_WGS84 */
  /*
   *  This function shifts geodetic coordinates relative to WGS84
   *  to geodetic coordinates relative to a given local datum.
   *
   *    WGS84_Lat : Latitude in radians relative to WGS84              (input)
   *    WGS84_Lon : Longitude in radians relative to WGS84             (input)
   *    WGS84_Hgt : Height in meters  relative to WGS84                (input)
   *    Index     : Index of destination datum                         (input)
   *    Lat_out   : Latitude in radians relative to destination datum  (output)
   *    Lon_out   : Longitude in radians relative to destination datum (output)
   *    Hgt_out   : Height in meters relative to destination datum     (output)
   *
   */
        EllipsoidPar WGS84 = new EllipsoidPar();   /* Semi-major axis of WGS84 ellipsoid in meters */
        /* Flattening of WGS84 ellisoid                 */
        EllipsoidPar local = new EllipsoidPar();   /* Semi-major axis of ellipsoid in meters       */
        /* Flattening of ellipsoid                      */
        double da;                        /* Difference in semi-major axes                */
        double df;                        /* Difference in flattening                     */
        Shift3Param Shift = new Shift3Param();
        
        /* Use Molodensky's method */
        GeoParameter.WGS84_Parameters(WGS84);
        GeoParameter.Bessel1841_Parameters(local);
        da = local.a - WGS84.a;
        df = local.f - WGS84.f;
        GeoParameter.Potsdam_3Param_Shift(Shift);
        Shift.dx = -Shift.dx;
        Shift.dy = -Shift.dy;
        Shift.dz = -Shift.dz;
        Molodensky_Shift(WGS84.a, da, WGS84.f, df, Shift.dx, Shift.dy, Shift.dz,
                GeoLat, GeoLon, GeoHgt, Potsdam_out);

    } /* End Geodetic_Shift_From_WGS84 */
    
}
