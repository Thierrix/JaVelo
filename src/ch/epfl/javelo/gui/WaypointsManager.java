package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe gérant la création et l'affichage des points de passages ainsi que les événements qui leurs sont liés
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public class WaypointsManager {

    private Graph graph;
    private MapViewParameters ParametresCarte;
    private ObservableList<Waypoint> pointsPassageListe;
    private ErrorManager erreur;

    private Pane pane = new Pane();

    private List<Group> groups = new ArrayList<>();

    // vraie pendant qu'on déplace un groupe
    private AtomicBoolean groupeEnDeplacement = new AtomicBoolean(false);

    // l'index du dernier groupe en déplacement dans la liste
    private AtomicInteger groupeIndex = new AtomicInteger(0);

    // pointCh de départ d'un déplacement, dans le cas où l'on remet le point à sa place car aucune route à proximité
    private PointCh pointChDebut;

    // noeud le plus proche de pointChDebut
    private int noeudDeDepart;

    ObjectProperty<Double> posRelativeX = new SimpleObjectProperty<>();
    ObjectProperty<Double> posRelativeY = new SimpleObjectProperty<>();

    /**
     * Constructeur de la classe, permet d'initialiser les attributs puis d'associer aux attributs ayant besoin d'événements ou
     * d'observateur, les événements/observateurs nécessaires
     * @param graph
     * @param MapParameterProperty
     * @param pointsPassageListe
     * @param erreur
     */

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> MapParameterProperty,
                            ObservableList<Waypoint> pointsPassageListe, ErrorManager erreur) {

        pane.setPickOnBounds(false);
        this.graph = graph;
        this.ParametresCarte = MapParameterProperty.get();
        this.pointsPassageListe = pointsPassageListe;
        this.erreur = erreur;
        cheminPointsDePassage();

        pane.getChildren().add(erreur.pane());

        pointsPassageListe.addListener((InvalidationListener) o -> {
            cheminPointsDePassage();
            initEvGroupes();
        });

        MapParameterProperty.addListener(o -> {
            ParametresCarte = MapParameterProperty.get();
            positionnerPointsDePassage();
        });

        initEvGroupes();

        pane.setOnMouseDragged(d -> {
            if (groupeEnDeplacement.get()) { //si en deplacement d'un waypoint alors...
                groups.get(groupeIndex.get()).setLayoutX(d.getX());
                groups.get(groupeIndex.get()).setLayoutY(d.getY());
                initEvGroupes();
            }
        });

        pane.setOnMouseReleased(r -> {
            if (groupeEnDeplacement.get() && !r.isStillSincePress()) { //si en deplacement d'un waypoint alors...
                PointWebMercator point = ParametresCarte.pointAt(r.getX(), r.getY());
                PointCh pointCh = point.toPointCh();

                caseOfError(pointCh);
                if (pointCh != null) {
                    int noeudProche = graph.nodeClosestTo(pointCh, 500);
                    pointsPassageListe.set(groupeIndex.get(), noeudProche == -1
                            ? new Waypoint(pointChDebut, noeudDeDepart)
                            : new Waypoint(pointCh, noeudProche));
                } else
                    pointsPassageListe.set(groupeIndex.get(), new Waypoint(pointChDebut, noeudDeDepart));

                groupeEnDeplacement.set(false); //je marque que je ne déplace plus de point pour l'instant
            }
        });


    }

    /**
     * Méthode contenant les événements liés à la suppression d'un point de passage et à l'activation du déplacement
     * d'un point de passage, qu'on associe à chaque groupe à chaque fois que la liste de groupes est modifiée
     */

    private void initEvGroupes() {
        for (int i = 0; i<groups.size(); ++i) {
            int index = i;
            groups.get(i).setOnMouseClicked(g -> {
                if (g.isStillSincePress()) pointsPassageListe.remove(index);
            });
            groups.get(i).setOnMousePressed(p -> {
                posRelativeX.set(p.getX());
                posRelativeY.set(p.getY());
                pointChDebut = pointsPassageListe.get(index).pointCh();
                noeudDeDepart = pointsPassageListe.get(index).noeudProcheId();
                groupeEnDeplacement.set(true);
                groupeIndex.set(index);
            });
        }
    }


    /**
     * Méthode qui retourne le groupe correspondant à la position donnée et à la place donnée
     * @param place le point de passage est soit le premier, le dernier ou entre les deux
     * @return
     */

    private Group creationGroupe(String place, Waypoint waypoint) {
        SVGPath pathExt = new SVGPath();
        pathExt.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        pathExt.getStyleClass().add("pin_outside");

        SVGPath pathInt = new SVGPath();
        pathInt.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        pathInt.getStyleClass().add("pin_inside");

        Group group = new Group();
        group.getChildren().setAll(pathExt, pathInt);
        group.getStyleClass().add("pin");
        group.getStyleClass().add(place);

        group.setLayoutX(ParametresCarte.viewX(PointWebMercator.ofPointCh(waypoint.pointCh())));
        group.setLayoutY(ParametresCarte.viewY(PointWebMercator.ofPointCh(waypoint.pointCh())));

        return group;
    }

    /**
     * Méthode permettant d'attribuer à chque groupe la position du point de passage correspondant
     */

    private void positionnerPointsDePassage() {
        for (int i = 0; i < pointsPassageListe.size(); ++i) {
            groups.get(i).setLayoutX(ParametresCarte.viewX(PointWebMercator.ofPointCh(pointsPassageListe.get(i).pointCh())));
            groups.get(i).setLayoutY(ParametresCarte.viewY(PointWebMercator.ofPointCh(pointsPassageListe.get(i).pointCh())));

            pane.getChildren().clear();
            pane.getChildren().setAll(groups);
        }
    }

    /**
     * Méthode permettant de créer le chemin passant par les points de passages de la liste pointsPassageListe
     * et de les assigner aux enfants du pane
     */

    private void cheminPointsDePassage() {
        groups.clear();
        if (!pointsPassageListe.isEmpty()) {
            groups.add(creationGroupe("first", pointsPassageListe.get(0)));
            for (int i = 1; i < pointsPassageListe.size()-1; ++i) {
                groups.add(creationGroupe("middle", pointsPassageListe.get(i)));
            }
            if (pointsPassageListe.size()!=1) {
                groups.add(creationGroupe("last", pointsPassageListe.get(pointsPassageListe.size()-1)));
            }
            positionnerPointsDePassage();
        }
        pane.getChildren().clear();
        pane.getChildren().setAll(groups);
    }

    /**
     * Méthode qui retourne le panneau contenant les points de passage
     * @return le panneau contenant les points de passage
     */

    public Pane pane() {
        return this.pane;
    }

    /**
     * Méthode qui prend en arguments les coordonnées x et y d'un point et ajoute
     * un nouveau point de passage au nœud du graphe qui en est le plus proche
     * @param x coordonnée x du point voulu
     * @param y coordonnée y du point voulu
     */

    public void addWaypoint(double x, double y) {
        PointWebMercator point = ParametresCarte.pointAt(x,y);
        PointCh pointCh = point.toPointCh();
        int node = graph.nodeClosestTo(pointCh, 500);
        caseOfError(pointCh);
        if (node!=-1) {
            pointsPassageListe.add(new Waypoint(pointCh, node));
        }
    }

    /**
     * Méthode permettant de tester si un point est proche d'un noeud (donc d'une route) ou pas
     * @param pointCh point pour lequel on teste s'il y a un noeud proche
     */

    private void caseOfError(PointCh pointCh) {
        if (pointCh == null || graph.nodeClosestTo(pointCh, 500)==-1) {
            erreur.displayError("Aucune route à proximité !");
        }
    }

}
