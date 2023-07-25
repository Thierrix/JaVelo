package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {

    @Test
    void constructorToThrowException(){
        assertThrows(IllegalArgumentException.class, () ->{
            var actual1 = new AttributeSet(0b000011111111111111111111111111111111);
        });

    }

    @Test
    void ofToWorkOnValidValue(){
        var actual1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        var expected1 = new AttributeSet(0b00000000001);
        assertEquals(expected1, actual1);
    }

    @Test
    void containsToWork(){
        var test1 = AttributeSet.of(Attribute.HIGHWAY_CYCLEWAY);
        var actual1 = test1.contains(Attribute.HIGHWAY_CYCLEWAY);
        var actual2 = test1.contains(Attribute.ACCESS_NO);
        assertEquals(true, actual1);
        assertEquals(false, actual2);
    }

    @Test
    void toStringWorks(){
        var actual1 = AttributeSet.of(Attribute.ACCESS_PRIVATE, Attribute.BICYCLE_DESIGNATED);
        var expected1="{access=private,bicycle=designated}";
        assertEquals(expected1, actual1.toString());
    }

    @Test
    void intersectToWork(){
        var actual1 = AttributeSet.of(Attribute.HIGHWAY_CYCLEWAY, Attribute.BICYCLE_NO);
        var actual2 = AttributeSet.of(Attribute.ACCESS_NO, Attribute.CYCLEWAY_OPPOSITE);
        assertFalse(actual1.intersects(actual2));

        var actual3 = AttributeSet.of(Attribute.HIGHWAY_CYCLEWAY, Attribute.BICYCLE_YES);

        assertTrue(actual1.intersects(actual3));
    }

}