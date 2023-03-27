package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import nl.group5b.camera.BodyCamera;
import nl.group5b.camera.Camera;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.gui.Element;
import nl.group5b.light.Light;
import nl.group5b.util.Settings;
import nl.group5b.util.cameraType;
import org.joml.Vector3f;

public class SettingsPanel extends Element {

    private Camera camera;
    private BodyCamera thirdPersonCamera;
    private Camera topDownCamera;
    private Light sun;

    private float[] sunBrightness = {Settings.SUN_BRIGHTNESS};
    private float[] fov = {Settings.VIEWPORT_FOV};
    private int currentCamera;
    private ImInt selectedCamera;

    public SettingsPanel(Camera topDownCamera, BodyCamera thirdPersonCamera, Light sun) {
        if (Settings.DEFAULT_CAMERA == cameraType.TOP_DOWN) {
            this.camera = topDownCamera;
            this.currentCamera = 1;
            this.selectedCamera = new ImInt(1);
        } else if (Settings.DEFAULT_CAMERA == cameraType.THIRD_PERSON) {
            this.camera = thirdPersonCamera;
            this.currentCamera = 0;
            this.selectedCamera = new ImInt(0);
        }

        this.topDownCamera = topDownCamera;
        this.thirdPersonCamera = thirdPersonCamera;
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
        ImGui.sliderFloat("##slider_sun_brightness", sunBrightness, 0, 1, "%.2f");
        // If the slider value has changed, update the sun brightness
        if (sunBrightness[0] != sun.getColour().x) {
            sun.setColour(new Vector3f(sunBrightness[0], sunBrightness[0], sunBrightness[0]));
        }

        ImGui.text("FOV");
        ImGui.setNextItemWidth(contentWidth);
        ImGui.sliderFloat("##slider_fov", fov, 30, 90, "%.0f");
        // If the slider value has changed, update the FOV
        if (fov[0] != Settings.VIEWPORT_FOV) {
            Settings.VIEWPORT_FOV = fov[0];
        }

        ImGui.text("Camera");
        ImGui.setNextItemWidth(contentWidth);
        ImGui.combo("##combo_camera", selectedCamera, new String[]{"Third Person", "Top Down"});
        // If the selected camera has changed, update the camera
        if (selectedCamera.get() != currentCamera) {
            currentCamera = selectedCamera.get();
            if (currentCamera == 0) {
                thirdPersonCamera.setIsActivate(true);
                camera = thirdPersonCamera;
            } else {
                thirdPersonCamera.setIsActivate(false);
                camera = topDownCamera;
            }
        }

        ImGui.end();
    }

    public Camera getCamera() {
        return camera;
    }
}
