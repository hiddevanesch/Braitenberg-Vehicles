package nl.group5b.model.models;

import nl.group5b.model.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

//public class Multipart extends Body {
//    public Multipart(ModelLoader modelLoader) throws FileNotFoundException {
//        Model dragon = OBJLoader.loadOBJ("dragon", modelLoader);
//        Model arena = OBJLoader.loadOBJ("arena", modelLoader);
//
//        Material blueMaterial = new Material(0.1f, 0.2f, 0.7f, 10, 0.5f);
//        Material redMaterial = new Material(0.7f, 0.2f, 0.1f, 5, 0.5f);
//
//        Vector3f nullPosition = new Vector3f(0, 0, 0);
//        Vector3f lowArenaPosition = new Vector3f(0, -2, 0);
//
//        Model[] loadedModels = {dragon, arena};
//        Material[] materialSets = {blueMaterial, redMaterial};
//        Vector3f[] startingPositions = {nullPosition, lowArenaPosition};
//        float[] scales = {1, 1};
//
//        super.setBody(loadedModels, materialSets, startingPositions, scales);
//    }
//}
