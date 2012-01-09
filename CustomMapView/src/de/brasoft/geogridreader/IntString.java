/*
 * IntString.java
 *
 * Created on 29. Januar 2005, 20:51
 */

package de.brasoft.geogridreader;

/**
 *
 * @author Administrator
 */
public class IntString {
    
    private int ary[];
    private int len;
    private int AryLen;
    
    /** Creates a new instance of IntString */
    //---------------------------------------------------------------------------
    public IntString() {
        ary = new int[2];  // Standardl�nge
        len = 0;           // Leerer String
        AryLen = 2;
    }
    
    //---------------------------------------------------------------------------
    public IntString(int elem) {
        ary = new int[2];  // Standardl�nge
        len = 1;
        AryLen = 2;
        ary[0] = elem;     // Datenelement
    }
    
    //---------------------------------------------------------------------------
    public void Add(int elem) {
        int tmp[];
        int i;
        
        if (AryLen-len >= 1) {
            ary[len] = elem;    // Datenelement eintragen
            len++;
        } else {
            tmp = new int[2*AryLen];                  // Erweitertes Array anlegen
            for (i=0; i<len; i++) tmp[i]=ary[i];      // Daten in neues Array �bertragen
            tmp[i]=elem;                              // Neues Element hinzuf�gen
            len++;
            AryLen=AryLen*2;
            ary=tmp;               // Verweis auf neues Array
        }
    }
    
    //---------------------------------------------------------------------------
    public int Length() {
        return len;
    }
    
    //---------------------------------------------------------------------------
    public void Serialize(int buf[], int offset) {
        for (int i=0; i<len; i++) buf[i+offset]=ary[i];
    }
    
    //---------------------------------------------------------------------------
    public void Add(IntString istr) {
        int i;
        int tmp[];
        int istrLen=istr.Length();
        
        if (AryLen-len >= istrLen) {
            istr.Serialize(ary,len);           // Datenelemente eintragen
            len=len+istrLen;
        } else {
            tmp = new int [2*(istrLen+len)];      // Neues Array mit doppeltem Speicherplatz
            for (i=0; i<len; i++) tmp[i]=ary[i];  // Daten in neues Array �bertragen
            istr.Serialize(tmp,i);                // Neue Datenelemente eintragen
            AryLen=2*(istrLen+len);
            len=len+istrLen;
            ary=tmp;               // Verweis auf neues Array
        }
    }
    
    //---------------------------------------------------------------------------
    public void Ist(int elem) {
        len = 1;
        ary[0] = elem;     // Datenelement
    }
    
    //---------------------------------------------------------------------------
    public void Ist(IntString istr) {
        len=0;
        Add(istr);
    }
    
    //---------------------------------------------------------------------------
    public int Get(int idx) {
        return ary[idx];
    }
    
    //---------------------------------------------------------------------------
    public int[] GetAryPtr() {
        return ary;
    }
    
    //---------------------------------------------------------------------------
    public String toString() {
        String retw=new String();
        for (int i=0; i<len; i++) {
            retw = retw + new Integer(ary[i]).toString() + " ";
            if (i%10 == 9) retw = retw + "\n";
        }
        return retw;
    }
    
}
