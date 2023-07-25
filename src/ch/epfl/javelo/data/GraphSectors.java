package ch.epfl.javelo.data;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Enregistrement représentant le tableau concernant les 16384 secteurs de JaVelo
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_NODENBER = Integer.BYTES;
    private static final int OFFSET_SECTORS = OFFSET_NODENBER + Short.BYTES;
    private static final int SECTORS_NUMBER = 128;

    /**
     * Nouvel enregistrement pour simplifier la création de secteurs
     */
    public record Sector(int startNodeId, int endNodeId) {

    }

    /**
     * Méthode donnant la liste de secteurs contenus dans un carré de centre center et de coté 2*distance
     *
     * @param center
     *      centre du carré
     * @param distance
     *      Moitié de coté du carré
     * @return
     *      la listes des secteurs contenus dans le carré
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        ArrayList<Sector> sectorsInArea = new ArrayList<>();

        double eastAgain = center.e() - SwissBounds.MIN_E;
        double northAgain = center.n() - SwissBounds.MIN_N;

        double xMin, xMax, yMin, yMax;

        if(eastAgain - distance <0){
            xMin = 0;
        }else xMin = eastAgain - distance;

        if(northAgain - distance <0){
            yMin = 0;
        }else yMin = northAgain - distance;

        if(eastAgain + distance >= SwissBounds.WIDTH){
            xMax = SwissBounds.WIDTH -1;
        }else xMax = eastAgain + distance;

        if(northAgain + distance >= SwissBounds.HEIGHT){
            yMax = SwissBounds.HEIGHT -1 ;
        }else yMax = northAgain + distance;

        double largeurSector = SwissBounds.WIDTH/SECTORS_NUMBER;
        double longueurSector = SwissBounds.HEIGHT/SECTORS_NUMBER;

        int xMinLowerSector = (int)Math.floor(xMin / largeurSector);
        int xMaxSupSector = (int)Math.floor(xMax/largeurSector);
        int yMinLowerSector = (int)Math.floor(yMin/longueurSector);
        int yMaxSupSector = (int)Math.floor(yMax/longueurSector);

        int downAndLeftSector = xMinLowerSector + SECTORS_NUMBER * yMinLowerSector;
        int downAndRightSector = xMaxSupSector + SECTORS_NUMBER * yMinLowerSector;
        int upAndRightSector = xMaxSupSector + SECTORS_NUMBER * yMaxSupSector;
        int upAndLeftSector = xMinLowerSector + SECTORS_NUMBER * yMaxSupSector;


        for (int y = downAndLeftSector; y <= upAndLeftSector; y+=SECTORS_NUMBER) {
            for (int x = y; x <= downAndRightSector + y - downAndLeftSector; x++) {
                int debutNode = buffer.getInt(x*OFFSET_SECTORS);
                int endNode = debutNode + Short.toUnsignedInt(buffer.getShort(x*OFFSET_SECTORS +OFFSET_NODENBER));
                sectorsInArea.add(new Sector(debutNode, endNode));
            }
        }
        return sectorsInArea;
    }
}

