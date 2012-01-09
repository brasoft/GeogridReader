/*
 * TCALPTS.java
 *
 * Created on 28. Januar 2005, 18:59
 */

package de.brasoft.geogridreader;

/**
 *
 * @author Administrator
 */
public class TCALPTS {
    public  int xr;
    public  int yr;
    public  int p3;
    public  int p4;
    public  double Lat;
    public  double Lon;
    private double CenterMeridian;   //Bezugsmeridian der Karte
    private double Hr;      // Referenz des Hochwerts (Northing)
    private double Rr;      // Referenz des Rechtswerts (Easting)
    private GeodeticPoint Pt_Potsdam;
    private GeodeticPoint Pt_WGS84;
   
    /** Creates a new instance of TCALPTS */
    public TCALPTS() {
    }
    
    //-------------------------------------------------------------------------
    public TCALPTS(int pxr, int pyr, int pp3, int pp4, double pLat, double pLon, double pCM) {
        xr = pxr;
        yr = pyr;
        p3 = pp3;
        p4 = pp4;
        Lat = pLat;
        Lon = pLon;
        CenterMeridian = pCM * Math.PI / 180.0;
        Pt_Potsdam = new GeodeticPoint();
        Pt_WGS84 = new GeodeticPoint();
        TransMercPoint outPt = new TransMercPoint();
        EllipsoidPar epar = new EllipsoidPar();
        GeoParameter.Bessel1841_Parameters(epar);

        if (Lon>360) {
            // Berechnung von Hr/Rr auf Grundlage des Potsdam-Datums und Hoch-/Rechts-Wert
            int tmp = (int)Lat / 1000000;
            double urCM = (tmp*3) * Math.PI / 180.0;
            Rr = Lat - (tmp*1000000+500000);

	        GeodeticPoint Pt = new GeodeticPoint();
            TransverseMercator.Set_Parameters(epar.a, epar.f,0,urCM,0,0,1.0);
            TransverseMercator.Convert_To_Geodetic(Rr,Lon, Pt);
            TransverseMercator.Set_Parameters(epar.a, epar.f,0,CenterMeridian,0,0,1.0);
            TransverseMercator.Convert_From_Geodetic(Pt.GeoLat, Pt.GeoLon, outPt);
            Hr = outPt.Northing;
            Rr = outPt.Easting;
        }
        else {
            // Berechnung von Hr/Rr auf Grundlage des Potsdam-Datums
            GeodeticPoint Pt = new GeodeticPoint(Lat, Lon, 0);
            TransverseMercator.Set_Parameters(epar.a, epar.f,0,CenterMeridian,0,0,1.0);
            TransverseMercator.Convert_From_Geodetic(Pt.GeoLat, Pt.GeoLon, outPt);
            Hr = outPt.Northing;
            Rr = outPt.Easting; 
        }
    }
    
    //-------------------------------------------------------------------------
    public String toString() {
        String retw=new String();
        retw = new Integer(xr).toString() + " " + new Integer(yr).toString() + " " +
                new Integer(p3).toString() + " " + new Integer(p4).toString() + " " +
                new Double(Lat).toString() + " " + new Double(Lon).toString() + " " +
                new Double(Hr).toString() + " " + new Double(Rr).toString();
        return retw;
    }
    
    //-------------------------------------------------------------------------
    public double GetPixelDist(int x, int y) {
        return Math.sqrt(Math.pow(x-xr,2) + Math.pow(y-yr,2));
    }
    
    //-------------------------------------------------------------------------
    public GeodeticPoint getGeoKoordinate(int x, int y, int Masstab, double PixelMM) {
        // Rechts- und Hochwert des Pixelpunktes (x,y) bestimmen.
        double R = (x-xr)*(double)Masstab/(PixelMM*1000) + Rr;
        double H = (yr-y)*(double)Masstab/(PixelMM*1000) + Hr;
        
        // Parameter f�r Transverse Mercator sind schon gesetzt (Konstruktor TCALPTS)
        // Gilt nur, wenn nicht an verschiedenen Stellen unterschiedliche Parameter
        // eingestellt werden.
        
        TransverseMercator.Convert_To_Geodetic(R, H, Pt_Potsdam);
        Pt_Potsdam.Geodetic_Shift_Potsdam_To_WGS84(Pt_WGS84);
        return Pt_WGS84;
    }
    
    //-------------------------------------------------------------------------
    public int getMapPixelX(GeodeticPoint geoPt, int Masstab, double PixelMM) {
    	GeodeticPoint Pt_P = new GeodeticPoint();
        // Umwandeln in Potsdam_Datum
    	geoPt.Geodetic_Shift_WGS84_To_Potsdam(Pt_P);

        // Mercatorprojektion
    	TransMercPoint tmPt = new TransMercPoint(); 
    	TransverseMercator.Convert_From_Geodetic(Pt_P.GeoLat, Pt_P.GeoLon, tmPt);

        // Pixel (x und y) aus Rechts- und Hochwert bestimmen
    	return (int)Math.round((tmPt.Easting - Rr)*PixelMM*1000/Masstab) + xr;
    }

    //-------------------------------------------------------------------------
    public int getMapPixelY(GeodeticPoint geoPt, int Masstab, double PixelMM) {
    	GeodeticPoint Pt_P = new GeodeticPoint();
        // Umwandeln in Potsdam_Datum
    	geoPt.Geodetic_Shift_WGS84_To_Potsdam(Pt_P);

        // Mercatorprojektion
    	TransMercPoint tmPt = new TransMercPoint(); 
    	TransverseMercator.Convert_From_Geodetic(Pt_P.GeoLat, Pt_P.GeoLon, tmPt);

        // Pixel (x und y) aus Rechts- und Hochwert bestimmen
        return (int) (yr - Math.round((tmPt.Northing - Hr)*PixelMM*1000/Masstab));
    }

    //-------------------------------------------------------------------------
    public int getMapTile(GeodeticPoint geoPt, int Masstab, double PixelMM,
    int xTiles, int yTiles, int xTileSize, int yTileSize) {

    	int x = getMapPixelX(geoPt, Masstab, PixelMM);
    	int y = getMapPixelY(geoPt, Masstab, PixelMM);
    	return (y / yTileSize) * xTiles + (x / xTileSize);
    }
    
    
}
