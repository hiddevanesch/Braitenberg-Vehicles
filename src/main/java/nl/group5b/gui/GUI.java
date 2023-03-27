package nl.group5b.gui;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class GUI {

    private final ImGuiImplGlfw glfwGui = new ImGuiImplGlfw();
    private final ImGuiImplGl3 gl3Gui = new ImGuiImplGl3();

    private final Element[] elements;

    public GUI(long window, Element[] elements) {
        ImGui.createContext();
        glfwGui.init(window, true);
        gl3Gui.init("#version 460");
        this.elements = elements;
    }

    public void render() {
        glfwGui.newFrame();
        ImGui.newFrame();

        for (Element element : elements) {
            element.render();
        }

        ImGui.render();
        gl3Gui.renderDrawData(ImGui.getDrawData());
    }

    public void cleanUp() {
        gl3Gui.dispose();
        glfwGui.dispose();
        ImGui.destroyContext();
    }
}
