package nl.group5b.engine;

import nl.group5b.camera.Camera;
import nl.group5b.gui.GUI;
import nl.group5b.light.Light;
import nl.group5b.model.Body;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.shaders.viewport.ViewportShader;

import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private ViewportShader shader;

    private Renderer renderer;

    private Map<Model, List<BodyElement>> renderMap = new java.util.HashMap<>();

    public MasterRenderer(int lightCount) {
        this.shader = new ViewportShader(lightCount);
        this.renderer = new Renderer(shader);
    }

    public void render(Light[] lights, Camera camera, long window, GUI gui, List<Body> bodies) {
        renderer.prepare();
        shader.start();
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);

        // Render all BodyElements in the map
        renderer.render(renderMap, bodies);

        shader.stop();

        // Render the GUI
        gui.render();

        renderer.complete(window);

        // Clear the map of BodyElements to render
        renderMap.clear();
    }

    public void cleanUp() {
        shader.cleanUp();
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

}
