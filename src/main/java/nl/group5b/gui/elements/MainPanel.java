package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.gui.Element;
import nl.group5b.util.Settings;

public class MainPanel extends Element {

        @Override
        public void render() {
            ImGui.setNextWindowPos(0, 0);
            ImGui.setNextWindowSize(Settings.PANEL_WIDTH_MAIN, DisplayBuilder.getHeight());
            ImGui.begin("Main", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);
            ImGui.end();
        }
}
