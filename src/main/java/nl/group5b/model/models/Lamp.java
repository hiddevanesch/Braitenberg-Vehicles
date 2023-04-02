package nl.group5b.model.models;

import nl.group5b.light.Light;
import nl.group5b.model.*;
import nl.group5b.model.interfaces.PositionHandler;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileNotFoundException;

public class Lamp extends Body implements PositionHandler {

    private final Light light;
    private boolean isEnabled = false;

    public Lamp(ModelLoader modelLoader, Vector3f position, Vector3f colour,
                Vector3f attenuation) throws FileNotFoundException {
        Model lamp = OBJLoader.loadOBJ("lamp", modelLoader);
        Model lampBase = OBJLoader.loadOBJ("lampbase", modelLoader);

        Material lampMaterial = new Material(colour, true);
        Material metalMaterial = new Material(new Vector3f(0.5f, 0.5f, 0.5f), 2, 0.5f);

        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {lamp, lampBase};
        Material[] materialSets = {lampMaterial, metalMaterial};
        Vector3f[] startingPositions = {position, position};
        Vector3f[] startingRotations = {defaultRotation, defaultRotation};
        float[] scales = {1, 1};

        this.light = new Light(new Vector4f(position, 1), colour, attenuation, isEnabled);

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }

    @Override
    public void setPosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().setPosition(new Vector3f(position));
        super.getBodyElements()[1].getEntity().setPosition(new Vector3f(position));
        this.light.setPosition(new Vector4f(position, 1));
    }

    @Override
    public void setRotation(Vector3f rotation) {
        // Lamp does not have to rotate
    }

    @Override
    public void movePosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().move(position);
        super.getBodyElements()[1].getEntity().move(position);
        this.light.getPosition().add(new Vector4f(position, 1));
    }

    @Override
    public void moveRotation(Vector3f rotation) {
        // Lamp does not have to rotate
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

    // Get the name of the lamp
    public String getName() {
        return this.getClass().getSimpleName() + " " + System.identityHashCode(this);
    }

    public Vector3f getColour() {
        return this.light.getColour();
    }

    public void setColour(Vector3f colour) {
        this.light.setColour(colour);
        this.getBodyElements()[0].getMaterial().setColour(colour);
    }

    public Vector3f getAttenuation() {
        return this.light.getAttenuation();
    }

    public void enable() {
        this.isEnabled = true;
        this.light.enable();
    }

    public void disable() {
        this.isEnabled = false;
        this.light.disable();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }
}
