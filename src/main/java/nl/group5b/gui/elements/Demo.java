package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class Demo extends Element {

    int[] demoDate = {16, 3, 2023};

    private  ImBoolean spawnSecondCar = new ImBoolean(false);

    @Override
    public void render() {
        ImGui.begin("Demo GUI");
        ImGui.text("Welcome to our Demo! This is our ImGUI.");
        ImGui.sliderInt3("The date in sliders!", demoDate, 0, 3000);
        ImGui.separator();
        ImGui.text("Spawn second car:");
        ImGui.checkbox("Spawn second car", spawnSecondCar);
        ImGui.end();
    }

    public ImBoolean getSpawnSecondCar() {
        return spawnSecondCar;
    }

}
