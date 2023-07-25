package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {

    @Test
    void withPositionShiftedByWorks(){
        RoutePoint routePoint = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        RoutePoint expectedPoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500 + 100, 450);
        RoutePoint expectedPoint2 = new RoutePoint(new PointCh(2607098, 1107654), 13500 - 100, 450);
        assertEquals(expectedPoint1, routePoint.withPositionShiftedBy(100));
        assertEquals(expectedPoint2, routePoint.withPositionShiftedBy(-100));
    }

    @Test
    void min1Works(){
        RoutePoint routePoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        RoutePoint routePoint2  = new RoutePoint(new PointCh(2601098, 1101654), 4500, 150);
        assertEquals(routePoint2, routePoint1.min(routePoint2));
        assertEquals(routePoint2, routePoint2.min(routePoint1));
    }

    @Test
    void min2Works(){
        RoutePoint routePoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        assertEquals(new RoutePoint(new PointCh(2601098, 1101654), 4500, 150),routePoint1.min(new PointCh(2601098, 1101654),4500,150));
        assertEquals(routePoint1,routePoint1.min(new PointCh(2601098, 1101654),4500,850));
    }





    @Test
    void withPositionShiftedByWorksOnGivenValue(){
        //Shift by 2000
        RoutePoint point0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual0 = point0.withPositionShiftedBy(2000);
        RoutePoint expected0 = new RoutePoint(new PointCh(2800000, 1080000), 14839, 1000);
        assertEquals(expected0, actual0);

        //Shift by -2000
        RoutePoint point1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = point1.withPositionShiftedBy(-2000);
        RoutePoint expected1 = new RoutePoint(new PointCh(2800000, 1080000), 10839, 1000);
        assertEquals(expected1, actual1);

        //Shift by 0
        RoutePoint point2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = point2.withPositionShiftedBy(0);
        RoutePoint expected2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        assertEquals(expected2, actual2);
    }

    @Test
    void minWorksOnGivenValue(){
        //That plus petit
        RoutePoint pointThis0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint pointThat0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint actual0 = pointThis0.min(pointThat0);
        RoutePoint expected0 = pointThat0;
        assertEquals(expected0, actual0);

        //Egalité
        RoutePoint pointThis1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint pointThat1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = pointThis1.min(pointThat1);
        RoutePoint expected1 = pointThis1;
        assertEquals(expected1, actual1);

        //This plus petit
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint pointThat2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = pointThis2.min(pointThat2);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }

    @Test
    void min2WorksOnGivenValue(){
        //ThatDistanceToReference plus petit
        RoutePoint pointThis0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual0 = pointThis0.min(new PointCh(2800000, 1080000), 12839, 999);
        RoutePoint expected0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 999);
        assertEquals(expected0, actual0);

        //Egalité
        RoutePoint pointThis1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = pointThis1.min(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint expected1 = pointThis1;
        assertEquals(expected1, actual1);

        //This plus petit
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint actual2 = pointThis2.min(new PointCh(2800000, 1080000), 12839, 5002);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }
}
