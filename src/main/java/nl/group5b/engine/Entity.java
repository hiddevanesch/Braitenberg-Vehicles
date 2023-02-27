package nl.group5b.engine;

import nl.group5b.models.Model;
import org.lwjgl.util.vector.Vector3f;


public class Entity {

    private Model model;
    private Vector3f position;
    private float rx, ry, rz;
    private float scale;

    public Entity(Model model, Vector3f position, float rx, float ry, float rz, float scale) {
        this.model = model;
        this.position = position;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.scale = scale;
    }

    public void move(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void rotate(float dx, float dy, float dz) {
        this.rx += dx;
        this.ry += dy;
        this.rz += dz;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    public float getRz() {
        return rz;
    }

    public void setRz(float rz) {
        this.rz = rz;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
