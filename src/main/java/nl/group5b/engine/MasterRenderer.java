package nl.group5b.engine;

import nl.group5b.camera.Camera;
import nl.group5b.gui.GUI;
import nl.group5b.light.Light;
import nl.group5b.light.ShadowMasterRenderer;
import nl.group5b.model.Body;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.model.models.Arena;
import nl.group5b.shaders.viewport.ViewportShader;

import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private ViewportShader shader;

    private Renderer renderer;
    private ShadowMasterRenderer shadowRenderer;

    private final Light[] lights;
    private Camera camera;
    private long window;
    private GUI gui;

    private Map<Model, List<BodyElement>> renderMap = new java.util.HashMap<>();

    public MasterRenderer(Light[] lights, Camera camera, long window, GUI gui) {
        this.shader = new ViewportShader(lights.length);
        this.renderer = new Renderer(shader);
        this.shadowRenderer = new ShadowMasterRenderer(lights[0], camera);
        this.lights = lights;
        this.camera = camera;
        this.window = window;
        this.gui = gui;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        shadowRenderer.setCamera(camera);
    }

    public void render() {
        renderer.prepare();

        // Load the shadow map into the shader
        renderer.loadTexture(shadowRenderer.getShadowMap());

        shader.start();
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        shader.loadToShadowMapSpaceMatrix(shadowRenderer.getToShadowMapSpaceMatrix());

        // Render all BodyElements in the map
        renderer.render(renderMap);

        shader.stop();

        // Render the GUI
        gui.render();

        renderer.complete(window);

        // Clear the map of BodyElements to render
        renderMap.clear();
    }

    public void computeShadows() {
        shadowRenderer.render(renderMap);
    }

    public int getShadowMapTexture() {
        return shadowRenderer.getShadowMap();
    }

    public void cleanUp() {
        shader.cleanUp();
        shadowRenderer.cleanUp();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void processBodies(List<Body> bodies) {
        for (Body body : bodies) {
            processBody(body);
        }
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

    public void processArena(Arena arena) {
        processBody(arena);
    }
}
