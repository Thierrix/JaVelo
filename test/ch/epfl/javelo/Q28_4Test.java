package ch.epfl.javelo;

import ch.epfl.javelo.data.GraphEdges;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofIntTestOnValidValues() {
        var actual1 = Q28_4.ofInt(28);
        var expected1 = 448;
        assertEquals(expected1, actual1);

        var actual2  = Q28_4.ofInt(0);
        var expected2 = 0;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.ofInt(-1);
        var expected3 = -16;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.ofInt(2);
        var expected4 = 32;
        assertEquals(expected4, actual4);

        var actual5 = Q28_4.ofInt(0b111);
        var expected5 = 112;
        assertEquals(expected5, actual5);
    }

    @Test
    void asDouble() {
        var actual1 = Q28_4.asDouble(12345);
        var expected1 = 771.5625;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.asDouble(0);
        var expected2 = 0;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.asDouble(-1);
        var expected3 = -0.0625;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.asDouble(0b111);
        var expected4 = 0.4375;
        assertEquals(expected4, actual4);
    }

    @Test
    void asFloat() {
        var actual1 = Q28_4.asFloat(12345);
        var expected1 = 771.5625;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.asFloat(0);
        var expected2 = 0;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.asFloat(-1);
        var expected3 = -0.0625;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.asFloat(0b111);
        var expected4 = 0.4375;
        assertEquals(expected4, actual4);
    }


}

