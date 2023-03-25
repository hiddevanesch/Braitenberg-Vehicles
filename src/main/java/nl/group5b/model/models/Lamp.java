package nl.group5b.model.models;

import nl.group5b.light.Light;
import nl.group5b.model.*;
import nl.group5b.model.interfaces.PositionHandler;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileNotFoundException;

public class Lamp extends Body implements PositionHandler {

    private Light light;

    public Lamp(ModelLoader modelLoader, Vector3f position, Vector3f colour,
                Vector3f attenuation) throws FileNotFoundException {
        Model lamp = OBJLoader.loadOBJ("lamp", modelLoader);
        Model lampBase = OBJLoader.loadOBJ("lampbase", modelLoader);

        Material lampMaterial = new Material(colour.x, colour.y, colour.z, true);
        Material metalMaterial = new Material(0.5f, 0.5f, 0.5f, 2, 0.5f);

        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {lamp, lampBase};
        Material[] materialSets = {lampMaterial, metalMaterial};
        Vector3f[] startingPositions = {position, position};
        Vector3f[] startingRotations = {defaultRotation, defaultRotation};
        float[] scales = {1, 1};

        this.light = new Light(new Vector4f(position, 1), colour, attenuation);

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
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
