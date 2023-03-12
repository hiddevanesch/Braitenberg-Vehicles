package nl.group5b.gui.elements;

import imgui.ImGui;
import nl.group5b.gui.elements.Element;

public class Test extends Element {

    @Override
    public void render() {
        ImGui.begin("Hello, world!");
        ImGui.text("This is some useful text.");
        ImGui.end();
    }
}
