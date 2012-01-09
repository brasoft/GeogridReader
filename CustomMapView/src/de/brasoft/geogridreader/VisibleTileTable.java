/*
 * VisibleTileTable.java
 *
 * Created on 15. Februar 2005, 11:51
 */

package de.brasoft.geogridreader;

/**
 *
 * @author peter
 */
public class VisibleTileTable {
    
    public int TileNr;
    public int SizeX, SizeY;
    public int SrcPosX, SrcPosY;
    //public int DstPosX, DstPosY;
    
    /** Creates a new instance of VisibleTileTable */
    public VisibleTileTable(int ptilenr, int psizex, int psizey) {
        TileNr = ptilenr;
        SizeX = psizex;
        SizeY = psizey;
        SrcPosX = 0;
        SrcPosY = 0;
    }

    public VisibleTileTable(int pt0, int i, int k, MPHData mph) {
        int Grenzkachel = (pt0 / mph.getXTiles() + i + 1) * mph.getXTiles();
        TileNr = pt0 + i*mph.getXTiles() + k;
        if (TileNr >= Grenzkachel) TileNr = mph.maxTiles;
        SizeX = mph.getXTileSize();
        SizeY = mph.getYTileSize();
        SrcPosX = 0;
        SrcPosY = 0;
    }
    
}
