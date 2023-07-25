package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {

    @Test
    void classRaisesIllegalArgument() {
        float[] l = {3, 4, 5};
        float[] j = {2};
        float[] er = {};
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(-1, l);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(0, l);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(1, j);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(2, er);
        });
    }

    @Test
    void classWorks() {
        float[] l = {2.25F, 1.67F, 7.1F, 4.1F};
        ElevationProfile el = new ElevationProfile(2, l);

        assertEquals(2, el.length());
        assertEquals(1.67F, el.minElevation());
        assertEquals(7.1F, el.maxElevation());
        assertEquals(5.429999947547913, el.totalAscent());
        assertEquals(3.5800000429153442, el.totalDescent());

        assertEquals(4.384999930858613, el.elevationAt(1));
        assertEquals(2.25F, el.elevationAt(-5));
        assertEquals(4.1F, el.elevationAt(200));
    }


        @Test
        void exceptionLength () {
            assertThrows(IllegalArgumentException.class, () -> {
                new ElevationProfile(0, null);
            });
        }

        @Test
        void exceptionArrayLength () {
            float[] testArray = new float[0];
            assertThrows(IllegalArgumentException.class, () -> {
                new ElevationProfile(1, testArray);
            });
        }

        @Test
        void length () {
            float[] testArray = new float[5];
            ElevationProfile testValues = new ElevationProfile(6, testArray);
            assertEquals(6, testValues.length());
        }

        @Test
        void minElevation () {
            float[] testArray = new float[]{3, 6, 1, 7};
            ElevationProfile testValues = new ElevationProfile(5, testArray);
            assertEquals(1, testValues.minElevation());
        }

        @Test
        void maxElevation () {
            float[] testArray = new float[]{3, 6, 1, 7};
            ElevationProfile testValues = new ElevationProfile(5, testArray);
            assertEquals(7, testValues.maxElevation());
        }

        @Test
        void nullAscentTest () {
            float[] testArray = new float[]{3, 2, 1, 0};
            ElevationProfile testValues = new ElevationProfile(5, testArray);
            assertEquals(0, testValues.totalAscent());
        }

        @Test
        void totalAscent () {
            float[] testArray = new float[]{2, 0, 1, 0};
            ElevationProfile testValues = new ElevationProfile(5, testArray);
            assertEquals(1, testValues.totalAscent());
        }

        @Test
        void nullDescentTest () {
            float[] testArray = new float[]{2, 3, 4, 5};
            ElevationProfile testValues = new ElevationProfile(5, testArray);
            assertEquals(0, testValues.totalDescent());
        }

        @Test
        void totalDescent () {
            float[] testArray = new float[]{2, 6, 4, 7};
            ElevationProfile testValues = new ElevationProfile(5, testArray);
            assertEquals(2, testValues.totalDescent());
        }

        @Test
        void elevationAtNegativePosition () {
            float[] testArray = new float[]{2, 6, 4, 7};
            ElevationProfile testValues = new ElevationProfile(3, testArray);
            assertEquals(2, testValues.elevationAt(-1));
        }

        @Test
        void elevationAtOverLength () {
            float[] testArray = new float[]{2, 6, 4, 7};
            ElevationProfile testValues = new ElevationProfile(3, testArray);
            assertEquals(7, testValues.elevationAt(5));
        }

        @Test
        void elevationAt () {
            float[] testArray = new float[]{2, 6, 4, 7};
            ElevationProfile testValues = new ElevationProfile(3, testArray);
            assertEquals(2, testValues.elevationAt(0));
            assertEquals(7, testValues.elevationAt(3));
            assertEquals(4, testValues.elevationAt(2));
            assertEquals(6, testValues.elevationAt(1));
            assertEquals(5, testValues.elevationAt(1.5));
            assertEquals(5.5, testValues.elevationAt(2.5));
            assertEquals(4, testValues.elevationAt(0.5));
        }
}
