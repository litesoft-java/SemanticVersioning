package org.litesoft.utils;

import org.junit.jupiter.api.Test;
import org.litesoft.pragmatics.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.litesoft.utils.SemanticVersioning.SEM_VER_EXCEPTION_PREFIX;

class SemanticVersioningTest {

    @Test
    void check_Validate_asInt1000() {
        check( "1.0+2.0", null, "<minor> parse error for" );
        check( "1.0", new Context( "svTest" ), "missing <patch> section of:" );
        check( 1000000, "1.0.0", "1.0.0+2" );
        check( 1002000, "1.2.0","1.2.0-4.trouble" );
        check( 13006005, "10.3006.5","10.3006.5" );
    }

    void check( String source, Context context, String errorText ) {
        try {
            int result = SemanticVersioning.asInt1000( context, source );
            fail( "Expected exception w/ '" + errorText + "', but got result of: " + result );
        }
        catch ( SemanticVersioning.SemVerException e ) {
            String expectedPrefix = SEM_VER_EXCEPTION_PREFIX + errorText;
            String actual = e.getMessage();
            if ( !actual.startsWith( expectedPrefix ) ) {
                fail( "Expected Exception Message to start:" +
                      "\n    with: " + expectedPrefix +
                      "\n  actual: " + actual );
            }
        }
    }

    void check( int expected, String validated, String source ) {
        assertEquals( validated, SemanticVersioning.validate( null, source ), source );
        int actual = SemanticVersioning.asInt1000( null, source );
        assertEquals( expected, actual, source );
    }
}