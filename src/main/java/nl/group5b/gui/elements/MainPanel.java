package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.gui.Element;
import nl.group5b.util.Settings;

public class MainPanel extends Element {

    private int leftSensorImage;
    private int rightSensorImage;

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Settings.PANEL_WIDTH_MAIN, DisplayBuilder.getHeight());
        ImGui.begin("Main", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        float contentWidth = ImGui.getContentRegionAvailX();
        ImGui.text("Left sensor");
        ImGui.image(leftSensorImage, contentWidth, contentWidth, 1, 0, 0, 1);

        ImGui.text("Right sensor");
        ImGui.image(rightSensorImage, contentWidth, contentWidth, 1, 0, 0, 1);
        ImGui.end();
    }

    public void setImages(int leftSensorImage, int rightSensorImage) {
        this.leftSensorImage = leftSensorImage;
        this.rightSensorImage = rightSensorImage;
    }
}
