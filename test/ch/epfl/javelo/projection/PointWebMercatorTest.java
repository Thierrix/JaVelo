package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class PointWebMercatorTest {

    public static final double DELTA = 1e-11;
    public static final double DELTA1 = 2;
    public static final double DELTA2 = 3;





    @Test
    void PointWebMercatorConstructorConstructorThrowsOnInvalidCoordinates(){

        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(2, 1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(-1, 1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(1,  2);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(1, -1);
        });

    }

    @Test
    void PointWebMercatorWorksOnValidCoordinates(){
        var rng = newRandom();

        for(int i = 0; i<RANDOM_ITERATIONS; i++){
            var x  = rng.nextDouble(0, 1);
            var y = rng.nextDouble(0, 1);
            new PointWebMercator(x,y);
        }
    }

    @Test
    void ofToWorksOnValidValues(){

        var actual1 = PointWebMercator.of(3, 0, 0);
        var expected1 = new PointWebMercator(0, 0);
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.of(5, 256, 125);
        var expected2 = new PointWebMercator(0.03125, 0.0152587890625);
        assertEquals(expected2, actual2);

        var actual3 = PointWebMercator.of(8, 356, 678);
        var expected3 = new PointWebMercator(0.00543212890625, 0.010345458984375);
        assertEquals(expected3, actual3);

        var actual4 = PointWebMercator.of(6, 2056, 1028);
        var expected4 = new PointWebMercator(0.12548828125, 0.062744140625);
        assertEquals(expected4, actual4);

    }

    @Test
    void ofThrowsOnInvalidValues(){

        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator.of(1, 513, 512);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator.of(2, 312, 1026);
        });
    }

    @Test
    void ofPointChWorksOnValidValues(){
        var actual1 = PointWebMercator.ofPointCh(new PointCh(2485001, 1075001));
        var expected1 = new PointWebMercator(0.5165531653847774,0.3564927149520306);
        assertEquals(expected1, actual1);

        // à faire d'autres plus tard
    }

    @Test
    void xAtZoomLevelWorksOnValidValues(){
        var actual1 = new PointWebMercator(0.4, 0.6);
        var zoomTest1 = actual1.xAtZoomLevel(4);
        var expected1 = 1638.4;
        assertEquals(expected1, zoomTest1);

        var actual2 = new PointWebMercator(0.5, 0.8);
        var zoomTest2 = actual2.xAtZoomLevel(8);
        var expected2 = 32768;
        assertEquals(expected2, zoomTest2);

        var actual3 = new PointWebMercator(0.75, 0);
        var zoomTest3 = actual3.xAtZoomLevel(0);
        var expected3 = 192;
        assertEquals(expected3, zoomTest3);
    }

    @Test
    void yAtZoomLevelWorksOnValidValues(){
        var actual1 = new PointWebMercator(0.4, 0.6);
        var zoomTest1 = actual1.yAtZoomLevel(4);
        var expected1 = 2457.6;
        assertEquals(expected1, zoomTest1);

        var actual2 = new PointWebMercator(0.5, 0.8);
        var zoomTest2 = actual2.yAtZoomLevel(8);
        var expected2 = 52428.8;
        assertEquals(expected2, zoomTest2);

        var actual3 = new PointWebMercator(0.75, 0);
        var zoomTest3 = actual3.yAtZoomLevel(0);
        var expected3 = 0;
        assertEquals(expected3, zoomTest3);

    }

    @Test
    void lonWorksWithValidValues(){
        var rng = newRandom();

        for(int i = 0; i < RANDOM_ITERATIONS; i++){
            var x = rng.nextDouble(0,1);
            var y = rng.nextDouble(0,1);
            var testPoint = new PointWebMercator(x,y);
            var actual = testPoint.lon();
            var expected = 2*Math.PI*x - Math.PI;
            assertEquals(expected, actual);
        }
    }

    @Test
    void latWorksWithValidValues(){
        var rng = newRandom();

        for(int i = 0; i < RANDOM_ITERATIONS; i++){
            var x = rng.nextDouble(0,1);
            var y = rng.nextDouble(0,1);
            var testPoint = new PointWebMercator(x,y);
            var actual = testPoint.lat();
            var expected = Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
            assertEquals(expected, actual);
        }
    }

    @Test
    void toPointChToWorkWithValues(){

        var testPoint1 = new PointWebMercator(0, 0);
        var actual1 = testPoint1.toPointCh();
        assertEquals(null, actual1);

        var testPoint2 = new PointWebMercator(0.518275, 0.353664);
        var actual2 = testPoint2.toPointCh();
        var expected2 = new PointCh(2534037.590653985, 1152675.5325669462);
        assertEquals(expected2, actual2);

    }
}

