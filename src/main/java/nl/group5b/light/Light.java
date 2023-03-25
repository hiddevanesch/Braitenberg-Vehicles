package nl.group5b.light;


import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light {

    private Vector4f position; // w = 0 -> directional light, w = 1 -> point light
    private Vector3f colour;
    private Vector3f attenuation = new Vector3f(1, 0, 0);

    public Light(Vector4f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Light(Vector4f position, Vector3f colour, Vector3f attenuation) {
        this.position = position;
        this.colour = colour;
        this.attenuation = attenuation;
    }

    public Vector4f getPosition() {
        return position;
    }

    public void setPosition(Vector4f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }
}
