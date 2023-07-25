package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void funcLoadFromWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void funcLoadFromRaisesException(){
        assertThrows(IOException.class, () -> {Graph actual1 = Graph.loadFrom(Path.of("lauzanne"));});
    }

    @Test
    void nodeCountWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(212679, actual1.nodeCount());
    }

    @Test
    void classWorksForSpecificNode() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        PointCh pointTest = actual1.nodePoint(2345);
        assertEquals(new PointCh(2539500.0, 1165122.3125), pointTest);
        assertEquals(2, actual1.nodeOutDegree(2345));
        assertEquals(4751, actual1.nodeOutEdgeId(2345, 1));
        assertEquals(2345, actual1.nodeClosestTo(new PointCh(2539500, 1165120), 10));
        assertEquals(-1, actual1.nodeClosestTo(new PointCh(2539500, 1165120), 2));
        assertEquals(1155, actual1.edgeTargetNodeId(2345));
        assertTrue(actual1.edgeIsInverted(1155));
        AttributeSet l = AttributeSet.of(Attribute.HIGHWAY_TRACK);
        assertEquals(l, actual1.edgeAttributes(218));
        assertEquals(42.5625, actual1.edgeLength(2345));
        assertEquals(3.375, actual1.edgeElevationGain(2345));
        //assertEquals(Functions.sampled(), actual1.edgeProfile(2345));
    }


    private Graph getGraph() throws IOException {
        return Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void loadFrom() throws IOException {

    }

    @Test
    void nodeCountWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        System.out.println(graph.nodeCount());
    }

    @Test
    void nodePointWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        //LongBuffer nodeOSMId = readNodeOSMId();
        //System.out.println(nodeOSMId.get(0)); // 1684019323

        PointCh actual = graph.nodePoint(0);
        double lat = Math.toRadians(46.6455770);
        double lon = Math.toRadians(6.7761194);

        assertEquals(lat, actual.lat(), 10e-7);
        assertEquals(lon, actual.lon(), 10e-7);
    }

    @Test
    void nodeOutDegree() throws IOException {
        Graph graph = getGraph();

        int actual1 = graph.nodeOutDegree(0); //1684019323
        assertEquals(1, actual1);
        int actual2 = graph.nodeOutDegree(1); //1684019310
        assertEquals(2, actual2);
        int actual3 = graph.nodeOutDegree(100_000); //2101684853
        assertEquals(3, actual3);
    }

    @Test
    void nodeOutEdgeId() throws IOException {
        Graph graph = getGraph();
        int actual1 = graph.nodeOutEdgeId(0, 0); //1684019323
        assertEquals(0, actual1);
    }

    @Test
    void nodeClosestTo() throws IOException {
        Graph graph = getGraph();

        double e = Ch1903.e(Math.toRadians(6.77653), Math.toRadians(46.64608)); //osmid: 1684019323
        double n = Ch1903.n(Math.toRadians(6.77653), Math.toRadians(46.64608));
        PointCh point = new PointCh(e, n);
        int actual1 = graph.nodeClosestTo(point, 100);
        int actual2 = graph.nodeClosestTo(point, 0);

        assertEquals(0, actual1);
        assertEquals(-1, actual2);
    }

    @Test
    void edgeTargetNodeId() throws IOException {
        Graph graph = getGraph();

        int actual1 = graph.edgeTargetNodeId(0);
        assertEquals(1, actual1);
    }

    @Test
    void edgeIsInverted() throws IOException {
        Graph graph = getGraph();

        assertFalse(graph.edgeIsInverted(0));
        assertTrue(graph.edgeIsInverted(334630));
    }

    @Test
    void edgeAttributes() throws IOException {
        Graph graph = getGraph();

        AttributeSet actual1 = graph.edgeAttributes(0);
        AttributeSet expected1 = AttributeSet.of(Attribute.HIGHWAY_TRACK, Attribute.TRACKTYPE_GRADE1);
        assertEquals(expected1.bits(), actual1.bits());

        AttributeSet expected2 = AttributeSet.of(Attribute.BICYCLE_USE_SIDEPATH, Attribute.HIGHWAY_TERTIARY, Attribute.SURFACE_ASPHALT);
        AttributeSet actual2 = graph.edgeAttributes(362164);
        assertEquals(expected2.bits(), actual2.bits());
    }

    @Test
    void edgeLength() throws IOException {
        Graph graph = getGraph();
        double actual = graph.edgeLength(335275);
        assertEquals(24, actual, 10e-1); //  /!\ expected -> lack of precision
    }

    @Test
    void edgeElevationGain() throws IOException {
        Graph graph = getGraph();
        double actual1 = graph.edgeElevationGain(335275);
        assertEquals(1, actual1, 10e-2); //  /!\ expected -> lack of precision

        double actual2 = graph.edgeElevationGain(293069); // edge entre 289087937 (osm) et 570300687 (osm)
        assertEquals(8, actual2, 10e-1); //  /!\ expected -> lack of precision
    }

    @Test
    void edgeProfile() throws IOException {
        Graph graph = getGraph();
        DoubleUnaryOperator func = graph.edgeProfile(335275);
        assertEquals(390,func.applyAsDouble(0),1);
        DoubleUnaryOperator func2 = graph.edgeProfile(293912);
        assertEquals(490,func2.applyAsDouble(0),4);
        assertEquals(496,func2.applyAsDouble(34),1);

    }






    @Test
    void nodeCountWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
//            (short) 0x180C, (short) 0xFEFF,
//            (short) 0xFFFE, (short) 0xF000 //TypeIn3
                (short) 0x180C, (short) 0x1212 //Type2
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 1;
        int actual0 = graph.nodeCount();
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 2;
        int actual1 = graph1.nodeCount();
        assertEquals(expected1, actual1);
    }

    @Test
    void nodePointWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        PointCh expected0 = new PointCh(2_600_000, 1_200_000);
        PointCh actual0 = graph.nodePoint(0);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        PointCh expected1 = new PointCh(2_536_263, 1_215_736);
        PointCh actual1 = graph1.nodePoint(0);
        assertEquals(expected1, actual1);
    }

    @Test
    void nodeOutDegreeWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 2;
        int actual0 = graph.nodeOutDegree(0);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 1;
        int actual1 = graph1.nodeOutDegree(1);
        assertEquals(expected1, actual1);
    }

    @Test
    void nodeOutEdgeIdWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 9;
        int actual0 = graph.nodeOutEdgeId(0, 2);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 31 + 5;
        int actual1 = graph1.nodeOutEdgeId(1, 5);
        assertEquals(expected1, actual1);
    }

    //A FAIRE!
    @Test
    void nodeClosestToWorksOnGivenValue() throws IOException {

    }

    @Test
    void edgeTargetNodeIdWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 12;
        int actual0 = graph.edgeTargetNodeId(0);
        assertEquals(expected0, actual0);
    }

    @Test
    void edgeIsInvertedWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        boolean expected0 = true;
        boolean actual0 = graph.edgeIsInverted(0);
        assertEquals(expected0, actual0);
    }

    //ASSISTANT HELP!
    @Test
    void edgeAttributesWorksOnGivenValue() {}

    @Test
    void edgeLengthWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        double expected0 = 16.6875;
        double actual0 = graph.edgeLength(0);
        assertEquals(expected0, actual0);
    }

    @Test
    void edgeElevationGainWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        double expected0 = 16.0;
        double actual0 = graph.edgeElevationGain(0);
        assertEquals(expected0, actual0);
    }

}
