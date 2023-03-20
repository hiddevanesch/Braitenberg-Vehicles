package nl.group5b.model.models;

import nl.group5b.light.Light;
import nl.group5b.model.*;
import nl.group5b.model.interfaces.PositionHandler;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileNotFoundException;

public class Lamp extends Body implements PositionHandler {

    private Light light;

    public Lamp(ModelLoader modelLoader, Vector3f position, Vector3f rotation) throws FileNotFoundException {
        Model lamp = OBJLoader.loadOBJ("lamp", modelLoader);

        Material yellowMaterial = new Material(0.75f, 0.75f, 0, 10, 0.5f);

        Model[] loadedModels = {lamp};
        Material[] materialSets = {yellowMaterial};
        Vector3f[] startingPositions = {position};
        Vector3f[] startingRotations = {rotation};
        float[] scales = {1};

        this.light = new Light(new Vector4f(position, 1), new Vector3f(1, 1, 1));

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }

    public Lamp(ModelLoader modelLoader, Vector3f position, Vector3f rotation,
                Vector3f colour, Vector3f attenuation) throws FileNotFoundException {
        this(modelLoader, position, rotation);
        this.light.setColour(colour);
        this.light.setAttenuation(attenuation);
    }

    @Override
    public void setPosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().setPosition(position);
        this.light.setPosition(new Vector4f(position, 1));
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
    }

    @Override
    public void movePosition(Vector3f position) {
        // TODO implement
    }

    @Override
    public void moveRotation(Vector3f rotation) {
        // TODO implement
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
