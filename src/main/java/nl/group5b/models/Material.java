package nl.group5b.models;

import org.lwjgl.util.vector.Vector3f;

public class Material {
    private Vector3f ambient;
    private Vector3f diffuse;
    private float damping;
    private float shininess;

    public Material(Vector3f ambient, Vector3f diffuse, float damping, float shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.damping = damping;
        this.shininess = shininess;
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public float getDamping() {
        return damping;
    }

    public float getShininess() {
        return shininess;
    }
}
