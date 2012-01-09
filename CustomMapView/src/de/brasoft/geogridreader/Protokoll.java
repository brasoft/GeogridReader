/*
 * Protokoll.java
 *
 * Created on 29. Januar 2005, 14:58
 */

package de.brasoft.geogridreader;

import java.io.FileWriter;

/**
 *
 * @author Administrator
 */
public class Protokoll {
    public static String file;
    private static boolean console;
    
    /** Creates a new instance of Protokoll */
    public Protokoll(String pfile) {
        if (pfile.equalsIgnoreCase("stdout")) console=true; else console=false;
        file = pfile;
    }
    
    public static void Prot(String txt) {
        if (console) System.out.println(txt);
        else {
            try {
                FileWriter protfile = new FileWriter(file,true);
                protfile.write(txt+"\n");
                protfile.close();
            } catch (Exception e){}
        }
    }
    
    public static void changeProtFile(String pfile) {
        if (pfile.equalsIgnoreCase("stdout")) console=true; else console=false;
        file = pfile;
    }
    
}
