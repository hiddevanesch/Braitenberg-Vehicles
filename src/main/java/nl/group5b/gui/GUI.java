package nl.group5b.gui;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class GUI {

    private final ImGuiImplGlfw glfwGui = new ImGuiImplGlfw();
    private final ImGuiImplGl3 gl3Gui = new ImGuiImplGl3();

    public void init(long window) {
        ImGui.createContext();
        ImGui.setCurrentContext(ImGui.getCurrentContext());
        glfwGui.init(window, true);
        gl3Gui.init("#version 460");
    }

    public void render() {
        glfwGui.newFrame();
        ImGui.newFrame();

        ImGui.begin("Hello, world!");
        ImGui.text("This is some useful text.");
        ImGui.end();

        ImGui.render();
        gl3Gui.renderDrawData(ImGui.getDrawData());
    }

    public void cleanUp() {
        gl3Gui.dispose();
        glfwGui.dispose();
        ImGui.destroyContext();
    }
}
