package nl.group5b.model.models;

import nl.group5b.engine.Light;
import nl.group5b.model.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public class Lamp extends Body {

    private Light light;

    public Lamp(ModelLoader modelLoader) throws FileNotFoundException {
        Model lamp = OBJLoader.loadOBJ("lamp", modelLoader);

        Material yellowMaterial = new Material(0.75f, 0.75f, 0, 10, 0.5f);

        Vector3f defaultPosition = new Vector3f(0, 0, 0);

        Model[] loadedModels = {lamp};
        Material[] materialSets = {yellowMaterial};
        Vector3f[] startingPositions = {defaultPosition};
        float[] scales = {1};

        this.light = new Light(defaultPosition, new Vector3f(1, 1, 1));

        super.setBody(loadedModels, materialSets, startingPositions, scales);
    }

    @Override
    public void setPosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().setPosition(position);
        this.light.setPosition(position);
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

    public Light getLight() {
        return light;
    }
}
