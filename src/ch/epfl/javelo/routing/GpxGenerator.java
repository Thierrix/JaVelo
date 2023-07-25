package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ListIterator;
/**
 * Classe représentant un générateur d'itinéraire au format GPX
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */
public class GpxGenerator {

    private GpxGenerator(){}

    /**
     * Méthode créant un fichier gpx
     * @param itineraire
     *      l'itinéraire donné
     * @param profil
     *      le profil donné
     * @return
     *      Document GPX correspondant à l'élévation et l'itinéraire
     */
    public static Document createGpx(Route itineraire, ElevationProfile profil){
        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element route = doc.createElement("rte");
        root.appendChild(route);
        Iterator<Edge> iterator = itineraire.edges().iterator();
        int position = 0;

        for(PointCh pt : itineraire.points()){

            Element ptRoute = doc.createElement("rtept");
            ptRoute.setAttribute("lat", Double.toString(pt.lat())); // to cast it into a String
            ptRoute.setAttribute("lon", Double.toString(pt.lon())); // to cast it into a String
            route.appendChild(ptRoute);

            Element elev = doc.createElement("ele");
            elev.setTextContent(String.valueOf(profil.elevationAt(position)));
            ptRoute.appendChild(elev);
            if(iterator.hasNext()){
                position += iterator.next().length();
            }
        }

        return doc;
    }

    /**
     * Méthode s'occupant de l'écriture du fichier GPX correspondant dans le fichier donné
     *
     * @param gpxName
     *      Nom du fichier GPX
     * @param itineraire
     *      itinéraire donné
     * @param profil
     *      profil donné
     * @throws IOException
     *      Erreur d'entrée/sortie
     */
    public static void writeGpx(String gpxName, Route itineraire, ElevationProfile profil) throws IOException {
        Document doc = createGpx(itineraire, profil);
        Writer w  = Files.newBufferedWriter(Path.of("src/" + gpxName));
        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        }catch(TransformerConfigurationException e){
            throw new Error(e);
        }
        catch (TransformerException e){
            throw new Error(e);
        }
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}