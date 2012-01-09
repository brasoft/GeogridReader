/*
 * GeoParameter.java
 *
 * Created on 22. Februar 2005, 17:35
 */

package de.brasoft.geogridreader;

/**
 *
 * @author peter
 */
public class GeoParameter {
    
    /* Ellipsoid Parameters, WGS 84  */
    private static double WGS84_a = 6378137.0;              /* Semi-major axis of ellipsoid i meters */
    private static double WGS84_f = 1 / 298.257223563;      /* Flattening of ellipsoid  */
    
    /* Ellipsoid Parameters, Bessel (1841)  */
    private static double BR_a = 6377397.155;              /* Semi-major axis of ellipsoid i meters */
    private static double BR_f = 1 / 299.1528128;          /* Flattening of ellipsoid  */
    
    /* Shift Parameters, Potsdam (zu WGS 84)  */
    private static double Potsdam_dx = 587.0;
    private static double Potsdam_dy =  16.0;
    private static double Potsdam_dz = 393.0;
    
    /* Shift Parameters, MGI/�sterreich (zu WGS 84)  */
    //private static double MGI_dx = 575.0;
    //private static double MGI_dy =  93.0;
    //private static double MGI_dz = 466.0;
    
    /** Creates a new instance of GeoParameter */
    public GeoParameter() {
    }
    
    static public void WGS84_Parameters(EllipsoidPar par) {
        par.a = WGS84_a;
        par.f = WGS84_f;
    }
    
    static public void Bessel1841_Parameters(EllipsoidPar par) {
        par.a = BR_a;
        par.f = BR_f;
    }
    
    static public void Potsdam_3Param_Shift(Shift3Param par) {
        par.dx = Potsdam_dx;
        par.dy = Potsdam_dy;
        par.dz = Potsdam_dz;
    }
    
}
