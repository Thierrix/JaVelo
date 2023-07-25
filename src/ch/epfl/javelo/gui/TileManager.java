package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

/**
 * Classe gérant les tuiles OSM, obtenant les tuiles, les stockant soit dans un cache mémoire soit dans un cache disque
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */
public final class TileManager {
    private final Path cacheDique;
    private final String tuileServeur;
    private final LinkedHashMap<TileId, Image> cacheMemoire;

    private final static int CAPACITE_INIT = 100;
    private final static float LOAD_FACTOR = 0.75F;
    private final static int INTERVALLE_MIN = 0;
    private final static int ZOOM_LEVEL_MIN = 0;
    private final static int INTERVALLE_MAX = 1;
    private final static int ZOOM_LEVEL_NORMAL = 8;

    public TileManager(Path cacheDisque, String tuileServeur){
        this.cacheDique = cacheDisque;
        this.tuileServeur = tuileServeur;
        this.cacheMemoire = new LinkedHashMap(CAPACITE_INIT, LOAD_FACTOR, true);
    }

    /**
     * Méthode retournant une image avec l'identité de tuile donné
     * @param tileId
     *      identité d'une tuile
     * @return
     * @throws IOException
     *      si l'URL n'est pas valide
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        int zoom = tileId.zoomLevel;
        int xTile = tileId.xTile;
        int yTile = tileId.yTile;

        if(cacheMemoire.containsKey(tileId)){
            return cacheMemoire.get(tileId);
        }

        String finalStringEnd = yTile + ".png";
        StringJoiner fileForDL = new StringJoiner("/");
        fileForDL.add(String.valueOf(zoom)).add(String.valueOf(xTile)).add(finalStringEnd);
        Path newTileIdStringPath = cacheDique.resolve(String.valueOf(zoom)).resolve(String.valueOf(xTile));
        String newTileIdString = String.valueOf(newTileIdStringPath.resolve(yTile + ".png"));

        boolean tileIdExists = Files.exists(Path.of(newTileIdString));

        if(!tileIdExists) {

            URL u = new URL("https", tuileServeur, fileForDL.toString());
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            Files.createDirectories(newTileIdStringPath);

            try (InputStream internetTileId = c.getInputStream();
                 OutputStream outputStream = new FileOutputStream(newTileIdString)
            ) {
                internetTileId.transferTo(outputStream);
            }
        }

        try(InputStream i = new FileInputStream(newTileIdString)){
            cacheMemoire.put(tileId,new Image(i));
        }

        return cacheMemoire.get(tileId);

    }

    /**
     * Enregistrement représentant l'identité d'une tuile OSM
     */
    public record TileId(int zoomLevel, int xTile, int yTile){
        //Constructeur compact
        public TileId{
            Preconditions.checkArgument(zoomLevel>= ZOOM_LEVEL_MIN);
            Preconditions.checkArgument(isValid(zoomLevel, xTile, yTile));
        }
        /**
         * Méthode certifiant la validité d'une tuile
         *
         * @param zoomLevel
         *      le niveau de zoom
         * @param xTile
         *      l'index x de la tuile
         * @param yTile
         *      l'index y de la tuile
         * @return
         *      true si cela est valide, false sinon
         */
        public static boolean isValid(int zoomLevel, int xTile, int yTile){

            if(INTERVALLE_MIN <= yTile && yTile < (INTERVALLE_MAX<< ZOOM_LEVEL_NORMAL + zoomLevel)
                    && INTERVALLE_MIN<=xTile && xTile<(INTERVALLE_MAX<< ZOOM_LEVEL_NORMAL + zoomLevel)){
                return true;
            }
            return false;
        }
    }
}
