package nl.group5b.engine;

import nl.group5b.camera.Camera;
import nl.group5b.camera.Sensor;
import nl.group5b.gui.GUI;
import nl.group5b.light.Light;
import nl.group5b.light.DirectionalShadowMR;
import nl.group5b.model.Body;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.model.models.BraitenbergVehicle;
import nl.group5b.shaders.real.RealShader;
import nl.group5b.util.Settings;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private final RealShader shader;

    private final Renderer renderer;
    private final DirectionalShadowMR directionalShadowRenderer;

    private final Light[] lights;
    private final long window;
    private final GUI gui;

    private final Map<Model, List<BodyElement>> renderMap = new java.util.HashMap<>();

    public MasterRenderer(Light[] lights, long window, GUI gui) {
        this.shader = new RealShader(lights.length);
        this.renderer = new Renderer(shader);
        this.directionalShadowRenderer = new DirectionalShadowMR(lights[0]);
        this.lights = lights;
        this.window = window;
        this.gui = gui;
    }

    private void prepareShader() {
        // Load the shadow map
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, directionalShadowRenderer.getShadowMap());

        // Load lights (should be done every frame, because the lights can move)
        shader.start();
        shader.loadLights(lights);
        shader.loadToShadowMapSpaceMatrix(directionalShadowRenderer.getToShadowMapSpaceMatrix());
        shader.stop();
    }

    private void renderShadows() {
        directionalShadowRenderer.render(renderMap);
    }

    private void renderSensors(List<Body> bodies) {
        for (Body body : bodies) {
            if (body instanceof BraitenbergVehicle braitenbergVehicle) {
                //
            }
        }
    }

    private void renderSensors(Sensor[] sensors) {
        shader.start();
        shader.loadGammaCorrection(1.0f);
        shader.stop();
        for (Sensor sensor : sensors) {
            renderer.prepareSensor(sensor);
            shader.start();
            shader.loadViewMatrix(sensor.getCamera());
            renderer.render(renderMap);
            shader.stop();
            renderer.completeSensor(sensor);
        }
    }

    private void renderViewport() {
        shader.start();
        shader.loadGammaCorrection(Settings.GAMMA_CORRECTION);
        shader.loadViewMatrix(gui.getCamera());

        // Render all BodyElements in the map
        renderer.render(renderMap);

        shader.stop();
    }

    private void renderGUI() {
        gui.render();
    }

    public void render(List<Body> bodies, Sensor[] sensors) {
        // Process all the bodies
        for (Body body : bodies) {
            processBody(body);
        }

        // Compute shadow maps
        renderShadows();

        // Prepare the RealShader
        prepareShader();

        // Render all the sensors
        renderSensors(sensors);

        // Render the viewport
        renderer.prepareViewport();
        renderViewport();
        renderGUI();
        renderer.completeViewport(window);

        // Clear the render map
        renderMap.clear();
    }

    public void cleanUp() {
        shader.cleanUp();
        gui.cleanUp();
        directionalShadowRenderer.cleanUp();
    }

    public Renderer getRenderer() {
        return renderer;
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
