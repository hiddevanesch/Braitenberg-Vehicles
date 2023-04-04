package nl.group5b.engine;

import nl.group5b.camera.BodyCamera;
import nl.group5b.camera.Camera;
import nl.group5b.camera.Sensor;
import nl.group5b.gui.GUI;
import nl.group5b.light.Light;
import nl.group5b.light.DirectionalShadowMR;
import nl.group5b.model.Body;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.model.interfaces.DriveHandler;
import nl.group5b.model.models.vehicle.BraitenbergVehicle;
import nl.group5b.shaders.RealShader;
import nl.group5b.util.Settings;
import org.lwjgl.opengl.GL46;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class MasterRenderer {

    private final RealShader shader;

    private final Renderer renderer;
    private final DirectionalShadowMR directionalShadowRenderer;

    private final List<Body> bodies;
    private final List<Light> lights;
    private final GUI gui;
    private final long window;

    private final Map<Model, List<BodyElement>> renderMap = new java.util.HashMap<>();

    public MasterRenderer(List<Body> bodies, List<Light> lights, GUI gui, long window, RealShader shader) {
        this.renderer = new Renderer(shader);
        this.directionalShadowRenderer = new DirectionalShadowMR(lights.get(0)); // At build time, sun should be at index 0
        this.bodies = bodies;
        this.lights = lights;
        this.gui = gui;
        this.window = window;
        this.shader = shader;
    }

    private void prepareShader() {
        // Load the shadow map
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, directionalShadowRenderer.getShadowMap());

        // Load lights (should be done every frame, because the lights can move)
        shader.start();
        shader.loadLights(lights);
        shader.loadAmbientLight(Settings.AMBIENT_LIGHT);
        shader.loadToShadowMapSpaceMatrix(directionalShadowRenderer.getToShadowMapSpaceMatrix());
        shader.stop();
    }

    private void renderShadows() {
        directionalShadowRenderer.render(renderMap);
    }

    private void renderSensors() {
        shader.start();
        shader.loadGammaCorrection(Settings.SENSOR_GAMMA_CORRECTION);
        shader.stop();
        for (Body body : bodies) {
            if (body instanceof BraitenbergVehicle braitenbergVehicle) {
                renderSensor(braitenbergVehicle.getLeftSensor());
                renderSensor(braitenbergVehicle.getRightSensor());
            }
        }
    }

    private void renderSensor(Sensor sensor) {
        renderer.prepareSensor(sensor);
        shader.start();
        shader.loadViewMatrix(sensor.getCamera());
        renderer.render(renderMap);
        shader.stop();
        renderer.completeSensor(sensor);
    }

    private void renderViewport() {
        shader.start();
        shader.loadGammaCorrection(Settings.VIEWPORT_GAMMA_CORRECTION);
        shader.loadViewMatrix(gui.getCamera());

        // Render all BodyElements in the map
        renderer.render(renderMap);

        shader.stop();
    }

    private void renderGUI() throws FileNotFoundException {
        gui.render();
    }

    public void render() throws FileNotFoundException {
        // Process all the bodies
        for (Body body : bodies) {
            processBody(body);
        }

        // Compute shadow maps
        renderShadows();

        // Prepare the RealShader
        prepareShader();

        // Render all the sensors
        renderSensors();

        // Enable multisampling
        GL46.glEnable(GL_MULTISAMPLE);

        // Render the viewport
        renderer.prepareViewport();
        renderViewport();
        renderGUI();
        renderer.completeViewport(window);

        // Disable multisampling
        GL46.glDisable(GL_MULTISAMPLE);

        // Clear the render map
        renderMap.clear();
    }

    public void move() {
        // If body is "movable", move it using 'updateWheelSpeeds'
        for (Body body : bodies) {
            if (body instanceof ControlHandler) {
                ((ControlHandler) body).move(window, renderer);
            } else if (body instanceof DriveHandler) {
                ((DriveHandler) body).move(renderer);
            }
        }

        Camera camera = gui.getCamera();

        // If camera is "movable", move it
        if (camera instanceof BodyCamera) {
            ((BodyCamera) camera).move(window);
        }
    }

    public void cleanUp() {
        shader.cleanUp();
        gui.cleanUp();
        directionalShadowRenderer.cleanUp();
    }

    private void processBody(Body body) {
        for (BodyElement bodyElement : body.getBodyElements()) {
            processBodyElement(bodyElement);
        }
    }

    private void processBodyElement(BodyElement bodyElement) {
        Model model = bodyElement.getEntity().getModel();
        List<BodyElement> bodyElements = renderMap.get(model);

        if (bodyElements != null) {
            bodyElements.add(bodyElement);
        } else {
            List<BodyElement> newBodyElements = new java.util.ArrayList<>();
            newBodyElements.add(bodyElement);
            renderMap.put(model, newBodyElements);
        }
    }
}
