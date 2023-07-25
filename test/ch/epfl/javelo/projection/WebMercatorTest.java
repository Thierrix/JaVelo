package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebMercatorTest {
    public static final double DELTA = 1e-7;

    @Test
    void xWorksWithKnownValues(){
        var actual1 = WebMercator.x(12.25643);
        var expected1 = 2.4506714;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.x(45.98762);
        var expected2 = 7.8191570;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.x(0);
        var expected3 = 0.5;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.x(197.87764565);
        var expected4 = 31.9932054;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void yWorksWithKnownValues(){
        var actual1 = WebMercator.y(0);
        var expected1 = 0.5;
        assertEquals(actual1, expected1, DELTA);

        var actual2 = WebMercator.y(25.78209);
        var expected2 = 0.3885162;
        assertEquals(actual2, expected2, DELTA);

        var actual3 = WebMercator.y(76.99745);
        var expected3 = 1.1769481;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.y(50);
        var expected4 = 0.5427581;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void lonWorksWithKnownValues(){
        var actual1 = WebMercator.lon(0);
        var expected1 = -Math.PI;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lon(0.875);
        var expected2 = 2.35619449;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lon(0.5);
        var expected3 = 0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lon(1);
        var expected4 = 3.14159265;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void latWoksWithKnownValues(){

        var actual1 = WebMercator.lat(0);
        var expected1 = 1.4844222284;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lat(1);
        var expected2 = -1.4844222296;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lat(0.5);
        var expected3 = 0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lat(0.734);
        var expected4 = -1.1189169804;
        assertEquals(expected4, actual4, DELTA);
    }

}