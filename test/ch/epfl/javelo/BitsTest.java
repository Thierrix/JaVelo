package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitsTest {

    @Test
    void extractSignedToThrowOnInvalidValues(){
        assertThrows(IllegalArgumentException.class, () ->{
            int actual1 = Bits.extractSigned(5, -1, 4);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual2 = Bits.extractSigned(5, 1, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual3 = Bits.extractSigned(5, 33, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual4 = Bits.extractSigned(5, 1, 0);
        });
    }

    @Test
    void extractUnsignedToThrowOnInvalidValues(){
        assertThrows(IllegalArgumentException.class, () ->{
            int actual1 = Bits.extractUnsigned(5, -1, 4);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual2 = Bits.extractUnsigned(5, 1, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual3 = Bits.extractUnsigned(5, 33, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual4 = Bits.extractUnsigned(5, 1, -1);
        });
    }

    @Test
    void extractSignedToWorkOnValidValues(){
        assertEquals(0b11111111111111111111111111111010, Bits.extractSigned(0b11001010111111101011101010111110, 8, 4));
    }

    @Test
    void extractUnsignedToWorkOnValidValues(){
        assertEquals(0b00000000000000000000000000001010, Bits.extractUnsigned(0b11001010111111101011101010111110, 8, 4));
    }


    @Test
    void bitsExtractWorksOnFullLength() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v = rng.nextInt();
            assertEquals(v, Bits.extractSigned(v, 0, Integer.SIZE));
        }
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v = 1 + rng.nextInt(-1, Integer.MAX_VALUE);
            assertEquals(v, Bits.extractUnsigned(v, 0, Integer.SIZE - 1));
        }
    }

}
