package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Arena extends Body {
    public Arena(ModelLoader modelLoader) throws FileNotFoundException {
        Model arena = OBJLoader.loadOBJ("arena", modelLoader);

        Material greenMaterial = new Material(0.1f, 0.6f, 0.2f, 10, 0.5f);

        Vector3f nullPosition = new Vector3f(0, 0, 0);

        Model[] loadedModels = {arena};
        Material[] materialSets = {greenMaterial};
        Vector3f[] startingPositions = {nullPosition};
        float[] scales = {1};

        super.setBody(loadedModels, materialSets, startingPositions, scales);
    }

    @Override
    public void setPosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().setPosition(position);
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
    }

    @Override
    public Vector3f getPosition() {
        return super.getBodyElements()[0].getEntity().getPosition();
    }

    @Override
    public Vector3f getRotation() {
        return super.getBodyElements()[0].getEntity().getRotation();
    }
}
