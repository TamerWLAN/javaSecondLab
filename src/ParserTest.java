import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @org.junit.jupiter.api.Test
    void computeWithSpace() {
        Parser parse = new Parser("   (79-  ((5^4)/(5  -1)+8-5+4) )");
        assertEquals(-84.25, parse.compute());
    }
    @org.junit.jupiter.api.Test
    void computeSimple() {
        Parser parse = new Parser("5+6");
        assertEquals(11, parse.compute());
    }
    @org.junit.jupiter.api.Test
    void checkBracketsOrder() {
        Parser parse = new Parser("5-())(6+1)");
        Exception ex = assertThrows(RuntimeException.class, ()->{
            parse.compute();
        });

        assertEquals("Bracket order error", ex.getMessage());
    }
    @org.junit.jupiter.api.Test
    void checkLogicalStringStart() {
        Parser parse = new Parser("-(5+6)");
        Exception ex = assertThrows(RuntimeException.class, ()->{
            parse.compute();
        });

        assertEquals("String must be started with number/variables or bracket", ex.getMessage());
    }
    @org.junit.jupiter.api.Test
    void checkLogicalStringEnd() {
        Parser parse = new Parser("(5+6)+");
        Exception ex = assertThrows(RuntimeException.class, ()->{
            parse.compute();
        });

        assertEquals("String must not ended on sign", ex.getMessage());
    }
    @org.junit.jupiter.api.Test
    void checkLogicalVarAfterNumber() {
        Parser parse = new Parser("(5a+6)");
        Exception ex = assertThrows(RuntimeException.class, ()->{
            parse.compute();
        });

        assertEquals("After number, must be a sign or another number", ex.getMessage());
    }
    @org.junit.jupiter.api.Test
    void checkLogicalTwoSignsInARow() {
        Parser parse = new Parser("(5++6)");
        Exception ex = assertThrows(RuntimeException.class, ()->{
            parse.compute();
        });

        assertEquals("There must not be two signs in a row", ex.getMessage());
    }

    @org.junit.jupiter.api.Test
    void checkLogicalTwoVarsInARow() {
        Parser parse = new Parser("(aa+6)");
        Exception ex = assertThrows(RuntimeException.class, ()->{
            parse.compute();
        });

        assertEquals("Variable must be called single letter", ex.getMessage());
    }
}