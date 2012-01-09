/*
 * TIntStrings.java
 *
 * Created on 29. Januar 2005, 21:42
 */

package de.brasoft.geogridreader;

/**
 *
 * @author Administrator
 */
public class TIntStrings {
    
    private IntString List[];
    private int Count;
    //private int max;
    
    /** Creates a new instance of TIntStrings */
    public TIntStrings(int elem) {
        List = new IntString[elem];
        Count=0;
        //max=elem;
    }
    
    public void Add(IntString elem) {
        List[Count] = new IntString();
        List[Count].Ist(elem);
        Count++;
    }
    
    public IntString Get(int idx) {
        return List[idx];
    }
}
