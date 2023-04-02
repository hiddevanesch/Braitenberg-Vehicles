package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.gui.Element;
import nl.group5b.light.Light;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

public class SettingsPanel extends Element {

    private final Light sun;

    private final float[] sunBrightness = {Settings.SUN_BRIGHTNESS};
    private final float[] ambientLight = {Settings.AMBIENT_LIGHT};
    private final float[] sunPosition = {
            Settings.SUN_DEFAULT_POSITION.x(),
            Settings.SUN_DEFAULT_POSITION.y(),
            Settings.SUN_DEFAULT_POSITION.z()
    };
    private final float[] gamma = {Settings.VIEWPORT_GAMMA_CORRECTION};
    private final float[] fov = {Settings.VIEWPORT_FOV};

    public SettingsPanel(Light sun) {
        this.sun = sun;
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(DisplayBuilder.getWidth() - Settings.PANEL_WIDTH_SETTINGS, 0);
        ImGui.setNextWindowSize(Settings.PANEL_WIDTH_SETTINGS, DisplayBuilder.getHeight());
        ImGui.begin("Settings", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        float contentWidth = ImGui.getContentRegionAvailX();

        ImGui.text("Sun brightness");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat("##slider_sun_brightness", sunBrightness, 0, 1, "%.2f")) {
            sun.setColour(new Vector3f(sunBrightness[0], sunBrightness[0], sunBrightness[0]));
        }

        ImGui.text("Ambient light");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat("##slider_ambient_light", ambientLight, 0, 1, "%.2f")) {
            Settings.AMBIENT_LIGHT = ambientLight[0];
        }

//        ImGui.text("Sun position");
//        ImGui.setNextItemWidth(contentWidth);
//        if (ImGui.sliderInt3("##slider_sun_position", sunPosition, -20, 20)) {
//            sun.setPosition(new Vector4f(sunPosition[0], sunPosition[1], sunPosition[2], 0));
//        }

        ImGui.text("Gamma");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat("##slider_gamma", gamma, 0, 2.2f, "%.1f")) {
            Settings.VIEWPORT_GAMMA_CORRECTION = gamma[0];
        }

        ImGui.text("FOV");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat("##slider_fov", fov, 30, 90, "%.0f")) {
            Settings.VIEWPORT_FOV = fov[0];
        }

        ImGui.end();
    }
}
