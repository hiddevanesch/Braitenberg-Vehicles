package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;

import java.util.ArrayList;
import java.util.List;

public class Demo extends Element {

    int[] demoDate = {16, 3, 2023};

    private static final int SPEED_HISTORY_SIZE = 500;

    private List<Float> vehicleSpeedsLeft = new ArrayList<>();
    private List<Float> vehicleSpeedsRight = new ArrayList<>();

    private ImBoolean spawnSecondCar = new ImBoolean(false);

    @Override
    public void render() {
        ImGui.begin("Demo GUI");
        ImGui.text("Welcome to our Demo! This is our ImGUI.");
        ImGui.sliderInt3("The date in sliders!", demoDate, 0, 3000);
        ImGui.separator();
        ImGui.plotLines("Left wheel speed", getVehicleSpeedsLeft(), getVehicleSpeedsLeft().length, 0, "", 0, 1.5f, 400, 50);
        ImGui.plotLines("Right wheel speed", getVehicleSpeedsRight(), getVehicleSpeedsRight().length, 0, "", 0, 1.5f, 400, 50);
        ImGui.separator();
        ImGui.text("Spawn second car:");
        ImGui.checkbox("Spawn second car", spawnSecondCar);
        ImGui.end();
    }

    public ImBoolean getSpawnSecondCar() {
        return spawnSecondCar;
    }

    public void addVehicleSpeed(float speedLeft, float speedRight) {
        if (vehicleSpeedsLeft.size() > SPEED_HISTORY_SIZE) {
            vehicleSpeedsLeft.remove(0);
        }
        vehicleSpeedsLeft.add(speedLeft);
        if (vehicleSpeedsRight.size() > SPEED_HISTORY_SIZE) {
            vehicleSpeedsRight.remove(0);
        }
        vehicleSpeedsRight.add(speedRight);
    }

    private float[] getVehicleSpeedsLeft() {
        float[] speeds = new float[vehicleSpeedsLeft.size()];
        for (int i = 0; i < vehicleSpeedsLeft.size(); i++) {
            speeds[i] = vehicleSpeedsLeft.get(i);
        }
        return speeds;
    }

    private float[] getVehicleSpeedsRight() {
        float[] speeds = new float[vehicleSpeedsRight.size()];
        for (int i = 0; i < vehicleSpeedsRight.size(); i++) {
            speeds[i] = vehicleSpeedsRight.get(i);
        }
        return speeds;
    }

}
