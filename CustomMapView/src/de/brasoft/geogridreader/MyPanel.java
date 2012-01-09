/*
 * MyPanel.java
 *
 * Created on 31. Januar 2005, 16:31
 */

package de.brasoft.geogridreader;

/**
 *
 * @author  Administrator
 */
public class MyPanel {
    /*
	private static final long serialVersionUID = 1L;
	private MPHData mph;
    private MPRData mpr;
    private VisibleTileTable vtt[];
    private VisibleTileTable vtt_old[];
    
    private int getWidth() {
    	return 480;
    }
    
    private int getHeight() {
    	return 600;
    }
    
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        MapPaint(mph.X0, mph.Y0,g2);
    }
    
    public void ConnectGR(MPHData pmph, MPRData pmpr) {
        mph=pmph;
        mpr=pmpr;
    }
    
    private void MapPaint(int x0, int y0, Graphics2D g2) {
        int TileNr0;     // Erste dargestellte Kachel
        int x0t, y0t;    // Koordinaten in dieser Kachel
        
        int x_Si, y_Si;  // Sichtbare Kacheln in x- und y-Richtung
        int AnzSi;       // Anzahl sichtbarer Kacheln
        
        // Aus x0,y0 (Die Koordinate in der Karte die im Ursprung (0,0)
        // des Anzeigefenster angezeigt wird) die zugeh�rige TileNr0
        // berechnen und auch x0t und y0t (Koordinaten auf TileNr0 bezogen)
        
        TileNr0 = (y0 / mph.getYTileSize()) * mph.getXTiles() + (x0 / mph.getXTileSize());
        x0t = x0 % mph.getXTileSize();
        y0t = y0 % mph.getYTileSize();
        
        // Anzahl der dargestellten Kacheln Berechnen
        x_Si = (getWidth() + mph.getXTileSize() + x0t - 1) / mph.getXTileSize();
        y_Si = (getHeight() + mph.getYTileSize() + y0t - 1) / mph.getYTileSize();
        
        // Kachel-Tabelle (visible tile table) erstellen, in der alle
        // relevanten Informationen zum Kopieren der einzelnen Kacheln
        // auf die Anzeigefl�che enthalten sind.
        AnzSi = x_Si * y_Si;
        vtt_old = vtt;
        // Sichtbarkeitsattribut zur�cksetzen
        int tile;
        if (vtt_old != null) {
            for (int i=0; i<vtt_old.length; i++) {
                tile = vtt_old[i].TileNr;
                if (tile<mph.maxTiles) {
                    mpr.ref[tile].isVisible = false;
                }
            }
        }
//        VisibleTileTable vtt[] = new VisibleTileTable[AnzSi];
        vtt = new VisibleTileTable[AnzSi];
        
        //Schleife �ber alle sichtbaren Kacheln
        int idx=0;
        for (int i=0; i<y_Si; i++) {
            for (int k=0; k<x_Si; k++) {
                //vtt[idx] = new VisibleTileTable(TileNr0+i*mph.xTiles+k,mph.xTileSize,mph.yTileSize);  // Mit Vorbelegung
                vtt[idx] = new VisibleTileTable(TileNr0,i,k,mph);  // Mit Vorbelegung
                // SizeX,Y berechnen
                if (k==0) vtt[idx].SizeX =((x0t+mph.getXTileSize())/mph.getXTileSize())*mph.getXTileSize() - x0t;
                if (i==0) vtt[idx].SizeY =((y0t+mph.getYTileSize())/mph.getYTileSize())*mph.getYTileSize() - y0t;
                if (k==x_Si-1) vtt[idx].SizeX =x0t+getWidth()-((x0t+k*mph.getXTileSize())/mph.getXTileSize())*mph.getXTileSize();
                if (i==y_Si-1) vtt[idx].SizeY =y0t+getHeight()-((y0t+i*mph.getYTileSize())/mph.getYTileSize())*mph.getYTileSize();
                
                // Koordinaten in den Source-Kacheln
                if (k==0) vtt[idx].SrcPosX = x0t;
                if (i==0) vtt[idx].SrcPosY = y0t;
                
                // Index hochz�hlen nicht vergessen
                idx++;
            }
        }
        
        // Und jetzt alle Kacheln auf die Zeichenfl�che kopieren
        idx=0;
        int DestPosY=0;  // Destination Position
        int DestPosX=0;
        Image TileToDraw;
        for (int i=0; i<y_Si; i++) {
            DestPosX=0;
            for (int k=0; k<x_Si; k++) {
                if (vtt[idx].TileNr < mph.maxTiles) TileToDraw = mpr.GetImage(vtt[idx].TileNr);
                else TileToDraw = mpr.GetWhiteImage();
                g2.drawImage(TileToDraw,
                        // Destination
                        DestPosX, DestPosY,       // 1. Ecke
                        DestPosX + vtt[idx].SizeX, DestPosY + vtt[idx].SizeY,   // 2. Ecke
                        // Source
                        vtt[idx].SrcPosX, vtt[idx].SrcPosY,  // 1. Ecke
                        vtt[idx].SrcPosX + vtt[idx].SizeX, vtt[idx].SrcPosY + vtt[idx].SizeY,  // 2. Ecke
                        this);
                DestPosX = DestPosX + vtt[idx].SizeX;
                idx++;
            }
            DestPosY = DestPosY + vtt[idx-1].SizeY;
        }
        
        // Nach dem Zeichnen die nicht mehr gezeichneten Kacheln vom letzen Aufruf freigeben
        if (vtt_old != null) {
            for (int i=0; i<vtt_old.length; i++) {
                // Die gerade gezeichneten Kacheln �berpr�fen
                tile = vtt_old[i].TileNr;
                if (tile<mph.maxTiles) {
                    if (mpr.ref[tile].isVisible == false) mpr.FreeTile(tile);
                }
            }
        }
    }*/
    
}
