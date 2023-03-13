package nl.group5b.model.models;

import nl.group5b.model.*;
import nl.group5b.model.interfaces.MoveHandler;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

public abstract class BraitenbergVehicle extends Body implements MoveHandler {

    Vector3f carBodyRelativePosition = new Vector3f(0, 0.3f, 0);
    Vector3f carLeftWheelRelativePosition = new Vector3f(0.72f, 0.3f, -0.75f);
    Vector3f carRightWheelRelativePosition = new Vector3f(-0.72f, 0.3f, -0.75f);

    public BraitenbergVehicle(ModelLoader modelLoader) throws FileNotFoundException {
        Model carBody = OBJLoader.loadOBJ("carbody", modelLoader);
        Model carWheel = OBJLoader.loadOBJ("carwheel", modelLoader);

        Material brownMaterial = new Material(0.45f, 0.30f, 0.2f, 10, 0.5f);
        Material blackMaterial = new Material(0.2f, 0.2f, 0.2f, 10, 0.5f);

        // TODO remove
        Material testMaterial = new Material(1f, 0, 0, 10, 0.5f);

        Model[] loadedModels = {carBody, carWheel, carWheel};
        Material[] materialSets = {brownMaterial, blackMaterial, testMaterial};
        Vector3f[] startingPositions = {carBodyRelativePosition, carLeftWheelRelativePosition,
                carRightWheelRelativePosition};
        float[] scales = {1, 1, 1};

        super.setBody(loadedModels, materialSets, startingPositions, scales);
    }

    @Override
    public void setPosition(Vector3f position) {
        Vector3f bodyPosition = new Vector3f(position.x, position.y + 0.3f, position.z);
        // Compute leftWheelPosition and rightWheelPosition based on the y-rotation of the carBody
        Vector3f leftWheelPosition = new Vector3f(position.x + 0.72f, position.y + 0.3f, position.z - 0.75f);
        Vector3f rightWheelPosition = new Vector3f(position.x - 0.72f, position.y + 0.3f, position.z - 0.75f);
        super.getBodyElements()[0].getEntity().setPosition(bodyPosition);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
        super.getBodyElements()[1].getEntity().setRotation(rotation);
        super.getBodyElements()[2].getEntity().setRotation(rotation);

        // Get the vehicles pivot point
        Vector3f bodyPosition = super.getBodyElements()[0].getEntity().getPosition();

        // rotate the wheels around the pivot point
        Vector3f leftWheelPosition = rotatePointAroundPivot(carLeftWheelRelativePosition, bodyPosition, rotation.y);
        Vector3f rightWheelPosition = rotatePointAroundPivot(carRightWheelRelativePosition, bodyPosition, rotation.y);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
    }

    @Override
    public Vector3f getPosition() {
        return null;
    }

    @Override
    public Vector3f getRotation() {
        return null;
    }

    private Vector3f rotatePointAroundPivot(Vector3f point, Vector3f pivot, float angle) {
        float s = (float) Math.sin(Math.toRadians(-angle));
        float c = (float) Math.cos(Math.toRadians(-angle));

        // rotate point
        float xnew = point.x * c - point.z * s;
        float znew = point.x * s + point.z * c;

        // translate point back:
        point.x = xnew + pivot.x;
        point.y = pivot.y;
        point.z = znew + pivot.z;
        return point;
    }
}
