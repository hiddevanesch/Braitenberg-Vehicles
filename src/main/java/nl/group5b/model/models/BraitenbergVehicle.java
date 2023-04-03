package nl.group5b.model.models;

import nl.group5b.camera.Sensor;
import nl.group5b.engine.Renderer;
import nl.group5b.model.*;
import nl.group5b.model.interfaces.CollisionHandler;
import nl.group5b.model.interfaces.PositionHandler;
import nl.group5b.util.Algebra;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

public abstract class BraitenbergVehicle extends Body implements PositionHandler, CollisionHandler {

    static final Vector3f carBodyRelativePosition = new Vector3f(0, 0.3f, 0);
    static final Vector3f carLeftWheelRelativePosition = new Vector3f(0.72f, 0.3f, 0);
    static final Vector3f carRightWheelRelativePosition = new Vector3f(-0.72f, 0.3f, 0);
    static final Vector3f carLeftSensorRelativePosition = new Vector3f(0.6f, 0.5f, 1.75f);
    static final Vector3f carRightSensorRelativePosition = new Vector3f(-0.6f, 0.5f, 1.75f);
    static final Vector3f carLampRelativePosition = new Vector3f(0, 0.79f, 0);
    static final Vector3f carCameraBarsRelativePosition = new Vector3f(0, 0, 0);

    protected float leftWheelRotation = 0;
    protected float rightWheelRotation = 0;

    protected float leftWheelSpeed = 0;
    protected float rightWheelSpeed = 0;

    protected HitBox hitBox;
    protected List<Body> collisionBodies;

    private final Sensor leftSensor;
    private final Sensor rightSensor;
    private float sensorFov = Settings.SENSOR_FOV;

    private AttachableLamp lamp = null;

    public BraitenbergVehicle(ModelLoader modelLoader, Material bodyMaterial) throws FileNotFoundException {
        Model carBody = OBJLoader.loadOBJ("carbody", modelLoader);
        Model carWheel = OBJLoader.loadOBJ("carwheel", modelLoader);
        Model sensorCamera = OBJLoader.loadOBJ("camera", modelLoader);
        Model cameraBars = OBJLoader.loadOBJ("cameraBars", modelLoader);


        Material blackMaterial = new Material(new Vector3f(0.02f, 0.02f, 0.02f), 10, 0.1f);
        Material greyMaterial = new Material(new Vector3f(0.2f, 0.2f, 0.2f), 10, 0.1f);
        Material shinyMetalMaterial = new Material(new Vector3f(0.666f, 0.662f, 0.678f), 5, 1f);

        Vector3f defaultRotation = new Vector3f(0, 0, 0);

        Model[] loadedModels = {carBody, carWheel, carWheel, sensorCamera, sensorCamera, cameraBars};
        Material[] materialSets = {bodyMaterial, blackMaterial, blackMaterial, greyMaterial, greyMaterial, shinyMetalMaterial};
        Vector3f[] startingPositions = {carBodyRelativePosition, carLeftWheelRelativePosition,
                carRightWheelRelativePosition, carLeftSensorRelativePosition, carRightSensorRelativePosition, carCameraBarsRelativePosition};
        Vector3f[] startingRotations = {defaultRotation, defaultRotation, defaultRotation, defaultRotation, defaultRotation, defaultRotation};
        float[] scales = {1, 1, 1, 1, 1, 1};

        this.leftSensor = new Sensor(carLeftSensorRelativePosition, defaultRotation, Settings.SENSOR_RESOLUTION);
        this.rightSensor = new Sensor(carRightSensorRelativePosition, defaultRotation, Settings.SENSOR_RESOLUTION);

        super.setBody(loadedModels, materialSets, startingPositions, startingRotations, scales);
    }

    public BraitenbergVehicle(ModelLoader modelLoader, Material bodyMaterial, Vector3f position,
                              Vector3f rotation) throws FileNotFoundException {
        this(modelLoader, bodyMaterial);
        this.setPosition(position);
        this.setRotation(rotation);

        this.hitBox = new HitBox(
                new Vector3f(-0.67f, 0, 1.7f),
                new Vector3f(0.67f, 0, 1.7f),
                new Vector3f(-0.67f, 0, -0.3f),
                new Vector3f(0.67f, 0, -0.3f),
                this.getBodyElements()[0].getEntity());
    }

    protected void rotateWheels(float frameTime) {
        leftWheelRotation += leftWheelSpeed * Settings.WHEEL_ROTATION_SPEED * frameTime;
        rightWheelRotation += rightWheelSpeed * Settings.WHEEL_ROTATION_SPEED * frameTime;

        // Get y-axis rotation
        float yRotation = getRotation().y;

        // Rotate the wheels in the axis direction of yDirection
        Vector3f newLeftWheelRotation = Algebra.rotateWheelGivenCurrentAngle(yRotation, leftWheelRotation);
        Vector3f newRightWheelRotation = Algebra.rotateWheelGivenCurrentAngle(yRotation, rightWheelRotation);
        super.getBodyElements()[1].getEntity().setRotation(newLeftWheelRotation);
        super.getBodyElements()[2].getEntity().setRotation(newRightWheelRotation);
    }

    // Function that updates the position of the vehicle based on the wheel speeds
    public void updatePosition(Renderer renderer) {
        float frameTime = renderer.getFrameTimeSeconds();

        // TODO maybe find another way
        // Frametime should be lower than 0.5
        if (frameTime > 0.5f) {
            return;
        }

        // Compute rotation angle based on wheel speeds
        float rotationAngle = (rightWheelSpeed - leftWheelSpeed) * frameTime * 180;
        Vector3f deltaRotation = new Vector3f(0, rotationAngle, 0);
        moveRotation(deltaRotation);

        // Compute position based on wheel speeds
        float distance = (leftWheelSpeed + rightWheelSpeed) * frameTime;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotation().y)));
        Vector3f deltaPosition = new Vector3f(dx, 0, dz);

        // Check if the front of the car is colliding with a body
        if (isColliding()) {
            // If collision is detected, set wheel speeds to 0
            leftWheelSpeed = 0;
            rightWheelSpeed = 0;
        }
        else {
            movePosition(deltaPosition);
            rotateWheels(frameTime);
        }
    }

    @Override
    public void setPosition(Vector3f position) {
        Vector3f bodyPosition = new Vector3f(position).add(carBodyRelativePosition);
        Vector3f leftWheelPosition = new Vector3f(position).add(carLeftWheelRelativePosition);
        Vector3f rightWheelPosition = new Vector3f(position).add(carRightWheelRelativePosition);
        Vector3f leftSensorPosition = new Vector3f(position).add(carLeftSensorRelativePosition);
        Vector3f rightSensorPosition = new Vector3f(position).add(carRightSensorRelativePosition);
        Vector3f cameraBarsPosition = new Vector3f(position).add(carCameraBarsRelativePosition);

        super.getBodyElements()[0].getEntity().setPosition(bodyPosition);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
        super.getBodyElements()[3].getEntity().setPosition(leftSensorPosition);
        super.getBodyElements()[4].getEntity().setPosition(rightSensorPosition);
        super.getBodyElements()[5].getEntity().setPosition(cameraBarsPosition);
        this.leftSensor.setPosition(leftSensorPosition);
        this.rightSensor.setPosition(rightSensorPosition);

        if (lamp != null) {
            Vector3f lampPosition = new Vector3f(position).add(carLampRelativePosition);
            lamp.setPosition(lampPosition);
        }
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
        super.getBodyElements()[1].getEntity().setRotation(rotation);
        super.getBodyElements()[2].getEntity().setRotation(rotation);
        super.getBodyElements()[3].getEntity().setRotation(rotation);
        super.getBodyElements()[4].getEntity().setRotation(rotation);
        super.getBodyElements()[5].getEntity().setRotation(rotation);

        Vector3f sensorRotation = new Vector3f(rotation).add(0, 180, 180); /////////

        this.leftSensor.setRotation(sensorRotation);
        this.rightSensor.setRotation(sensorRotation);

        // Get the vehicles pivot point
        Vector3f bodyPosition = super.getBodyElements()[0].getEntity().getPosition();

        // Rotate the wheels and sensors around the pivot point
        Vector3f leftWheelPosition = Algebra.rotatePointAroundPivot(carLeftWheelRelativePosition,
                bodyPosition, rotation.y);
        Vector3f rightWheelPosition = Algebra.rotatePointAroundPivot(carRightWheelRelativePosition,
                bodyPosition, rotation.y);
        Vector3f leftSensorPosition = Algebra.rotatePointAroundPivot(carLeftSensorRelativePosition,
                bodyPosition, rotation.y);
        Vector3f rightSensorPosition = Algebra.rotatePointAroundPivot(carRightSensorRelativePosition,
                bodyPosition, rotation.y);
        Vector3f cameraBarsPosition = Algebra.rotatePointAroundPivot(carCameraBarsRelativePosition,
                bodyPosition, rotation.y);
        super.getBodyElements()[1].getEntity().setPosition(leftWheelPosition);
        super.getBodyElements()[2].getEntity().setPosition(rightWheelPosition);
        super.getBodyElements()[3].getEntity().setPosition(leftSensorPosition);
        super.getBodyElements()[4].getEntity().setPosition(rightSensorPosition);
        super.getBodyElements()[5].getEntity().setPosition(cameraBarsPosition);
        this.leftSensor.setPosition(leftSensorPosition);
        this.rightSensor.setPosition(rightSensorPosition);
    }

    @Override
    public void movePosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().move(position);
        super.getBodyElements()[1].getEntity().move(position);
        super.getBodyElements()[2].getEntity().move(position);
        super.getBodyElements()[3].getEntity().move(position);
        super.getBodyElements()[4].getEntity().move(position);
        super.getBodyElements()[5].getEntity().move(position);

        // We do not want to move the sensors here, this causes rubber banding
        //this.leftSensor.getPosition().add(position);
        //this.rightSensor.getPosition().add(position);

        if (this.lamp != null) {
            this.lamp.movePosition(position);
        }
    }

    @Override
    public void moveRotation(Vector3f rotation) {
        Vector3f bodyRotation = new Vector3f(super.getBodyElements()[0].getEntity().getRotation()).add(rotation);

        setRotation(bodyRotation);
    }

    @Override
    public Vector3f getPosition() {
        Vector3f position = super.getBodyElements()[0].getEntity().getPosition();
        return new Vector3f(position.x(), 0, position.z());
    }

    @Override
    public Vector3f getRotation() {
        return super.getBodyElements()[0].getEntity().getRotation();
    }

    public float getSpeedLeft() {
        return leftWheelSpeed;
    }

    public float getSpeedRight() {
        return rightWheelSpeed;
    }

    public Sensor getLeftSensor() {
        return leftSensor;
    }

    public Sensor getRightSensor() {
        return rightSensor;
    }

    // Get the name of the vehicle
    public String getName() {
        return this.getClass().getSimpleName() + " " + System.identityHashCode(this);
    }

    public void setCollisionBodies(List<Body> bodies) {
        this.collisionBodies = bodies;
    }

    // Function that goes through all the bodies and checks if the front of the vehicle is colliding with any of them
    public boolean isColliding() {
        Vector3f[] hitBoxCoordinates = hitBox.getCoordinates();
        // Loop over all bodies in the list, excluding the vehicle itself
        for (Body body : collisionBodies) {
            if (body != this && body instanceof CollisionHandler) {
                // Get the hitbox of the target
                HitBox hitBoxTarget = ((CollisionHandler) body).getHitBox();
                if (Algebra.hitboxOverlap(hitBoxCoordinates, hitBoxTarget)) {
                    return true;
                }
            }
        }

        // Check if the vehicle is outside of the arena boundaries
        for (Vector3f coordinate : hitBoxCoordinates) {
            if (coordinate.x <= -Settings.ARENA_RADIUS + Settings.ARENA_WALL_OFFSET ||
                    coordinate.x >= Settings.ARENA_RADIUS - Settings.ARENA_WALL_OFFSET ||
                    coordinate.z <= -Settings.ARENA_RADIUS + Settings.ARENA_WALL_OFFSET ||
                    coordinate.z >= Settings.ARENA_RADIUS - Settings.ARENA_WALL_OFFSET) {
                return true;
            }
        }

        return false;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public float getSensorFov() {
        return sensorFov;
    }

    public void setSensorsFov(float sensorFieldOfView) {
        this.sensorFov = sensorFieldOfView;
        leftSensor.setFov(sensorFieldOfView);
        rightSensor.setFov(sensorFieldOfView);
    }

    public void attachLamp(AttachableLamp lamp) {
        this.lamp = lamp;
        this.lamp.setPosition(new Vector3f(getPosition()).add(carLampRelativePosition));
    }

    public boolean hasLamp() {
        return lamp != null;
    }

    public void removeLamp() {
        lamp = null;
    }

    public AttachableLamp getLamp() {
        return lamp;
    }
}
