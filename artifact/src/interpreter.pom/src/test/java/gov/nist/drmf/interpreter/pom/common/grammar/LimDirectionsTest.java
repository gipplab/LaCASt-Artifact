package gov.nist.drmf.interpreter.pom.common.grammar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andre Greiner-Petter
 */
public class LimDirectionsTest {
    @Test
    void leftTest() {
        assertEquals(LimDirections.LEFT, LimDirections.getDirection("left"));
        assertEquals("left", LimDirections.LEFT.getKey());
    }

    @Test
    void rightTest() {
        assertEquals(LimDirections.RIGHT, LimDirections.getDirection("right"));
        assertEquals("right", LimDirections.RIGHT.getKey());
    }

    @Test
    void noneTest() {
        assertNull(LimDirections.getDirection("tmp"));
        assertEquals("", LimDirections.NONE.getKey());
    }
}
