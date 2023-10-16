package org.litesoft.utils;

import org.litesoft.annotations.Significant;
import org.litesoft.pragmatics.Context;

/**
 * Parse SemVer string (dropping the +- branch stuff) and either validate (normalize) it or convert it to  an 'int'.
 */
public class SemanticVersioning {
    public static final String SEM_VER_EXCEPTION_PREFIX = "Unacceptable SemVer ";

    public static class SemVerException extends IllegalStateException {
        public SemVerException( String semVer, String why ) {
            super( SEM_VER_EXCEPTION_PREFIX + why + " of: " + semVer );
        }
    }

    public static String validate( Context context, String semVer ) {
        int[] iSemVer = toParts( context, semVer );
        return iSemVer[0] + "." + iSemVer[1] + "." + iSemVer[2];
    }

    public static int asInt1000( Context context, String semVer ) {
        int[] iSemVer = toParts( context, semVer );
        return (iSemVer[0] * 1000000) +
               (iSemVer[1] * 1000) +
               iSemVer[2];
    }

    public static int[] toParts( Context context, String semVer ) {
        context = (context != null) ? context : new Context( "SemanticVersioning" );
        // <major> "." <minor> "." <patch> [<'-'/'+'> ...]
        String tsv = Significant.AssertState.contextValue( context, semVer );
        String[] parts = tsv.split( "\\." );
        switch ( parts.length ) {
            case 1:
                throw new SemVerException( semVer, "missing <minor> & <patch> sections" );
            case 2:
                throw new SemVerException( semVer, "missing <patch> section" );
        }
        return new int[]{
                parsePart( "<major>", semVer, parts[0] ),
                parsePart( "<minor>", semVer, parts[1] ),
                parsePart( "<patch>", semVer, cleanPart3( parts[2] ) ),
                };
    }

    private static String cleanPart3( String part ) {
        int atDash = part.indexOf( '-' );
        int atPlus = part.indexOf( '+' );
        if ( (atPlus == -1) && (atDash == -1) ) {
            return part;
        }
        int at = Math.min( atPlus, atDash );
        if ( at != -1 ) {
            return part.substring( 0, at );
        }
        return part.substring( 0, Math.max( atPlus, atDash ) );
    }

    private static int parsePart( String partID, String semVer, String part ) {
        try {
            return Integer.parseInt( part.trim() );
        }
        catch ( NumberFormatException e ) {
            throw new SemVerException( semVer, partID + " parse error for '" + part + "'" );
        }
    }
}
