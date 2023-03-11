package nl.group5b.engine;

import nl.group5b.model.Body;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.shaders.StaticShader;

import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private StaticShader shader;

    private Renderer renderer;

    private Map<Model, List<BodyElement>> renderMap = new java.util.HashMap<>();

    public MasterRenderer(int lightCount) {
        this.shader = new StaticShader(lightCount);
        this.renderer = new Renderer(shader);
    }

    public void render(Light[] lights, Camera camera, long window) {
        renderer.prepare();
        shader.start();
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);

        // Render all BodyElements in the map
        renderer.render(renderMap);

        shader.stop();

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

    public void processBody(Body body) {
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
