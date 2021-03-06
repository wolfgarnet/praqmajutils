package net.praqma.util.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

public class Printer {

    public static <T1> void arrayPrinter( T1[] array ) {
        arrayPrinter( array, null );
    }

    public static <T1> void arrayPrinter( T1[] array, Integer max ) {
        Integer c = 1;
        for( T1 t : array ) {
            if( max != null && c > max )
                break;
            System.out.println( "(" + t.toString() + ") " );
            c++;
        }
    }

    public static <T1, T2> void mapPrinter( Map<T1, T2> hm ) {
        Iterator<Entry<T1, T2>> it = hm.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<T1, T2> entry = (Map.Entry<T1, T2>) it.next();
            System.out.print( "(" + entry.getKey().toString() + ", " + entry.getValue().toString() + ")" );
            if( it.hasNext() )
                System.out.print( ", " );
        }

        System.out.println( "" );
    }
    
    public static <T1, T2> void listMapPrinter( List<Map<T1, T2>> lm ) {
        for(Map<T1, T2> m : lm) {
            mapPrinter(m);
        }
    }

    public static <T1, T2> String mapPrinterToString( Map<T1, T2> hm ) {
        StringBuffer sb = new StringBuffer();
        Iterator<Entry<T1, T2>> it = hm.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<T1, T2> entry = (Map.Entry<T1, T2>) it.next();
            sb.append( "(" + entry.getKey().toString() + ", " + entry.getValue().toString() + ")" );
            if( it.hasNext() )
                System.out.print( ", " );
        }

        sb.append( "" );

        return sb.toString();
    }

    public static <T> void listPrinter( List<T> list ) {
        for( T t : list ) {
            System.out.print( "(" + t.toString() + ") " );
        }

        System.out.println( "" );
    }

    public static <T> String listPrinterToString( List<T> list ) {
        StringBuffer sb = new StringBuffer();
        for( T t : list ) {
            sb.append( "(" + t.toString() + ") " );
        }

        return sb.toString();
    }

    public static void matchPrinter( Matcher m ) {
        for( int i = 0; i < m.groupCount(); i++ ) {
            System.out.println( "[" + i + "]" + m.group( i ) + " " );
        }
    }
}
