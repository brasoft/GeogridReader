/*
 * LZWDEC.java
 *
 * Created on 29. Januar 2005, 21:31
 */

package de.brasoft.geogridreader;

/**
 *
 * @author Administrator
 */
public class LZWDEC {
    
    private int len;
    //private byte in[];               // Eingabe-Daten
    private int inpos;               // Position in Eingabe-Buffer
    private int next_code;
    private int new_code;
    private int old_code;
    private int character;
    
    private IntString string;         // Arbeitsstring
    private IntString OutString;      // Ausgabestring
    TIntStrings StringTable;          // Stringtabelle
    
    private int base_codes;           // Anzahl der Basis-Codes
    
    private int startbit;             // Bitposition im Bitstream
    private int bitlen;               // Bitl�nge des Codes - Startwert
    private int maxbitlen;            // Maximale Bitl�nge des Codes
    private SpecialData Get;

    // Fehlersuche
    private int startbit0;
    //private IntString debugInputCodes;
    
    /** Creates a new instance of LZWDEC */
    public LZWDEC(byte pbuf[], int offset, int plen, int pnext_code, int pbitlen, int pmaxbitlen) {
        //in=pbuf;
        len=plen;
        base_codes=pnext_code;
        next_code=pnext_code;
        inpos=offset;
        startbit=inpos*8;
        startbit0=startbit;    // F�r die Fehlersuche
        bitlen=pbitlen;
        maxbitlen=pmaxbitlen;
        StringTable = new TIntStrings((1<<maxbitlen)-base_codes);
        OutString = new IntString();
        string = new IntString();
        //debugInputCodes = new IntString();   // F�r Fehlersuche
        
        Get = new SpecialData(pbuf);
    }
    
    private int input_code() {
        
        int retw;
        
        retw = Get.GetBitVal(startbit, bitlen);
        startbit = startbit + bitlen;
        inpos=(startbit+bitlen-1)/8;  // n�chste Byte-Grenze
        //debugInputCodes.Add(retw);  // Zum Debuggen
        return retw;
    }
    
    //---------------------------------------------------------------------------
    private int decode_string(IntString str, int code) {
        
        if (code >= next_code) {
            String errbuf = String.format("Fehler beim Dekomprimieren LZW, code=%d, next_code=%d\nstartbit=%d,bitlen=%d",
                    code, next_code, startbit, bitlen);
            errbuf = errbuf + String.format("\nStartbit0=%d Base_Codes=%d\n", startbit0, base_codes);
            // Noch 20 Eingabewerte zus�tzlich lesen
            //for (int i=0; i<200; i++) input_code();
            //errbuf = errbuf + "Input-Codes\n" + debugInputCodes.toString();
            Protokoll.Prot(errbuf);
            return -1;
        }
        if (code<base_codes) str.Ist(code);
        else str.Ist(StringTable.Get(code-base_codes));
        return 0;
    }
    
    //---------------------------------------------------------------------------
    public IntString Decode() {
        
        IntString tmp=new IntString();
        
        
        old_code=input_code();       /* Read in the first code, initialize the */
        character=old_code;          /* character variable, and send the first */
        OutString.Add(old_code);     // Code ausgeben
/*
 **  This is the main expansion loop.  It reads in characters from the LZW file
 **  until it sees the special code used to inidicate the end of the data.
 */
        while (true) {
            if (inpos>=len) break;   // Der ganze Eingabebuffer ist abgearbeitet
            new_code=input_code();
            
/*
 ** This code checks for the special STRING+CHARACTER+STRING+CHARACTER+STRING
 ** case which generates an undefined code.  It handles it by decoding
 ** the last code, and adding a single character to the end of the decode string.
 */
            if (new_code>=next_code) {
                if (decode_string(string, old_code)!=0)
                    return null;  // Abbbruch bei Fehler
                string.Add(character);
            }
/*
 ** Otherwise we do a straight decode of the new code.
 */
            else
                if (decode_string(string, new_code)!=0)
                    return null;  // Abbbruch bei Fehler
/*
 ** Now we output the decoded string
 */
            OutString.Add(string);
            character=string.Get(0);
/*
 ** Finally, if possible, add a new code to the string table.
 */
            if (next_code < (1<<maxbitlen) /*MAX_CODE*/) {
                if (decode_string(tmp, old_code)!=0) return null;  // Abbbruch bei Fehler
                tmp.Add(character);
                StringTable.Add(tmp);
                if ((next_code == ((1<<bitlen))-1) && (bitlen != maxbitlen)) {
                    // N�chstes Startbit auf Bytegrenze ausrichten
                    startbit = ((startbit+7)/8)*8;
                    bitlen++;
                }
                next_code++;
            }
            old_code=new_code;
        }
        
        
        // Ausgabe (IntString) zur�ck geben
        return OutString;
    }
    
}
