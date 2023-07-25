package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    void sampledToThrowExceptions(){
        assertThrows(IllegalArgumentException.class, () -> {
            float[] a = {1};
            var actual1 = Functions.sampled(a, 1.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            float[] a = {2, 4, 6};
            var actual1 = Functions.sampled(a, -1.0);
        });
    }

    @Test
    void constantToWorkOnValidValues(){
        var actual1 = Functions.constant(1);
        var expected1 = 1;
        assertEquals(expected1, actual1.applyAsDouble(2));
    }

    @Test
    void sampledToWorkOnValidValues(){
        float[] a = {0, 2, 4, 6, 8, 10};
        var actual1 = Functions.sampled(a, 3.5);
        var expected1 = 0;
        assertEquals(expected1, actual1.applyAsDouble(0));

    }


    @Test
    void functionsSampledInterpolatesBetweenSamples() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 20);
            var samples = new float[sampleCount];
            for (int j = 0; j < sampleCount; j += 1)
                samples[j] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(50, 100);
            var f = Functions.sampled(samples, xMax);
            var interSampleDistance = xMax / (sampleCount - 1);
            var minDeltaX = interSampleDistance / 4;
            for (int j = 1; j < sampleCount; j += 1) {
                var xL = (j - 1) * interSampleDistance;
                var yL = samples[j - 1];
                var xR = j * interSampleDistance;
                var yR = samples[j];
                var x = rng.nextDouble(xL + minDeltaX, xR - minDeltaX);
                var y = f.applyAsDouble(x);
                var expectedSlope = (yR - yL) / interSampleDistance;
                var actualSlope = (y - yL) / (x - xL);
                assertEquals(expectedSlope, actualSlope, 1e-3);
            }
        }
    }

    @Test
    void functionsSampledIsConstantLeftAndRightOfSamples() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 20);
            var samples = new float[sampleCount];
            for (int j = 0; j < sampleCount; j += 1)
                samples[j] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(Math.nextUp(0), 100);
            var f = Functions.sampled(samples, xMax);
            assertEquals(samples[0], f.applyAsDouble(Math.nextDown(0)));
            assertEquals(samples[0], f.applyAsDouble(-1000));
            assertEquals(samples[sampleCount - 1], f.applyAsDouble(Math.nextUp(xMax)));
            assertEquals(samples[sampleCount - 1], f.applyAsDouble(xMax + 1000));
        }
    }


}
