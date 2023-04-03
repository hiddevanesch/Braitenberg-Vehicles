package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.gui.Element;
import nl.group5b.model.Body;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.models.BraitenbergVehicle;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends Element {

    private final ModelLoader modelLoader;

    private final List<Body> bodies;
    List<Class<? extends BraitenbergVehicle>> vehicleClasses = new ArrayList<>();

    private BraitenbergVehicle selectedVehicle = null;
    private Class<? extends BraitenbergVehicle> selectedVehicleClass = null;
    private final FloatBuffer vehicleSpeedsLeft = FloatBuffer.allocate(Settings.GUI_GRAPH_HISTORY_SIZE);
    private final FloatBuffer vehicleSpeedsRight = FloatBuffer.allocate(Settings.GUI_GRAPH_HISTORY_SIZE);
    private final float[] speeds = new float[Settings.GUI_GRAPH_HISTORY_SIZE];
    private int vehicleSpeedsIndex = 0;
    private float[] position = {0, 0};
    private float[] rotation = {0};

    public MainPanel(ModelLoader modelLoader, List<Body> bodies) {
        this.modelLoader = modelLoader;
        this.bodies = bodies;

        // Add vehicle classes
        getVehicleTypes();
    }

    private void getVehicleTypes() {
        // Get vehicle package
        String vehiclePackage = BraitenbergVehicle.class.getPackageName();

        // Get the URL of the package directory
        URL packageUrl = Thread.currentThread().getContextClassLoader().getResource(
                vehiclePackage.replace(".", "/")
        );

        if (packageUrl != null && packageUrl.getProtocol().equals("file")) {
            try {
                // Convert the URL to a file path and get the package directory
                File packageDir = new File(packageUrl.toURI());

                // Get all the files in the package directory
                File[] files = packageDir.listFiles();

                // Iterate over all the files in the package directory
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            // Convert the file name to a class name and load the class
                            String className = vehiclePackage + "." +
                                    file.getName().substring(0, file.getName().length() - 6);
                            Class<?> cls = Class.forName(className);

                            // Check if the class is a subtype of BraitenbergVehicle
                            if (BraitenbergVehicle.class.isAssignableFrom(cls) &&
                                    !Modifier.isAbstract(cls.getModifiers())) {
                                vehicleClasses.add(cls.asSubclass(BraitenbergVehicle.class));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Settings.PANEL_WIDTH_MAIN, DisplayBuilder.getHeight());
        ImGui.begin("Main", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        float contentWidth = ImGui.getContentRegionAvailX();

        float buttonWidth = 30;
        float selectorWidth = contentWidth - 2 * buttonWidth - 2 * ImGui.getStyle().getItemSpacingX();

        if (selectedVehicle != null) {
            // Check if the selected vehicle is still in the list
            if (!bodies.contains(selectedVehicle)) {
                selectedVehicle = null;
            }
        }

        // Get selected vehicle name
        String selectedVehicleName = selectedVehicle != null ? selectedVehicle.getName() : "Select vehicle";

        // Render vehicle selector
        ImGui.text("Vehicles");
        ImGui.setNextItemWidth(selectorWidth);
        if (ImGui.beginCombo("##combo_vehicles", selectedVehicleName)){
            for (Body body : bodies) {
                if (body instanceof BraitenbergVehicle braitenbergVehicle) {
                    if (ImGui.selectable(braitenbergVehicle.getName(), selectedVehicle == braitenbergVehicle)) {
                        selectedVehicle = braitenbergVehicle;
                        vehicleSpeedsLeft.clear();
                        vehicleSpeedsRight.clear();
                    }
                }
            }
            ImGui.endCombo();
        }

        // Render vehicle remove button
        ImGui.sameLine();
        if (ImGui.button("-", buttonWidth, 0)) {
            bodies.remove(selectedVehicle);
        }

        // Render vehicle add button
        ImGui.sameLine();
        if (ImGui.button("+", buttonWidth, 0)) {
            ImGui.openPopup("Add vehicle");
        }

        renderAddPopup();

        ImGui.separator(); // =======================================================================================

        if (selectedVehicle != null) {
            updateVehicleSpeed();

            renderVehicleData();
        }

        ImGui.end();
    }

    private void renderVehicleData() {
        ImGui.beginChild("##child_vehicle");

        ImGui.beginTable("##table_vehicle", 3, ImGuiTableFlags.SizingStretchSame);

        float columnWidth = (ImGui.getColumnWidth() - ImGui.getStyle().getWindowPaddingX()) / 3;
        float graphHeight = columnWidth / 2;

        ImGui.tableSetupColumn("");
        ImGui.tableSetupColumn("Left");
        ImGui.tableSetupColumn("Right");

        ImGui.tableHeadersRow();

        ImGui.tableNextRow();
        ImGui.tableSetColumnIndex(0);
        ImGui.text("Wheel speed\nhistory");
        ImGui.tableSetColumnIndex(1);
        ImGui.plotLines("##plot_left_wheel_speed", getVehicleSpeedsLeft(), getVehicleSpeedsLeft().length,
                0, "", 0, Settings.VEHICLE_SPEED, columnWidth, graphHeight);
        ImGui.tableSetColumnIndex(2);
        ImGui.plotLines("##plot_right_wheel_speed", getVehicleSpeedsRight(), getVehicleSpeedsRight().length,
                0, "", 0, Settings.VEHICLE_SPEED, columnWidth, graphHeight);

        ImGui.tableNextRow();
        ImGui.tableSetColumnIndex(0);
        ImGui.text("Current\nwheel speed");
        ImGui.tableSetColumnIndex(1);
        String leftSpeed = String.format("%.2f", selectedVehicle.getSpeedLeft());
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (ImGui.getContentRegionAvailX() - ImGui.calcTextSize(leftSpeed).x) / 2.0f);
        ImGui.text(leftSpeed);
        ImGui.tableSetColumnIndex(2);
        String rightSpeed = String.format("%.2f", selectedVehicle.getSpeedRight());
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (ImGui.getContentRegionAvailX() - ImGui.calcTextSize(rightSpeed).x) / 2.0f);
        ImGui.text(rightSpeed);

        ImGui.tableNextRow();
        ImGui.tableSetColumnIndex(0);
        ImGui.text("Sensor views");
        ImGui.tableSetColumnIndex(1);
        ImGui.image(selectedVehicle.getLeftSensor().getTextureID(),
                columnWidth, columnWidth, 1, 0, 0, 1);
        ImGui.tableSetColumnIndex(2);
        ImGui.image(selectedVehicle.getRightSensor().getTextureID(),
                columnWidth, columnWidth, 1, 0, 0, 1);

        ImGui.tableNextRow();
        ImGui.tableSetColumnIndex(0);
        ImGui.text("Brightness\nvalues");
        ImGui.tableSetColumnIndex(1);
        String leftBrightness = String.format("%.2f", selectedVehicle.getLeftSensor().calculateSensorBrightness());
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (ImGui.getContentRegionAvailX() - ImGui.calcTextSize(leftBrightness).x) / 2.0f);
        ImGui.text(leftBrightness);
        ImGui.tableSetColumnIndex(2);
        String rightBrightness = String.format("%.2f", selectedVehicle.getRightSensor().calculateSensorBrightness());
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (ImGui.getContentRegionAvailX() - ImGui.calcTextSize(rightBrightness).x) / 2.0f);
        ImGui.text(rightBrightness);

        ImGui.endTable();

        ImGui.endChild();
    }

    private void renderAddPopup() {
        // Add vehicle popup
        ImGui.setNextWindowPos(
                (float) DisplayBuilder.getWidth() / 2,
                (float) DisplayBuilder.getHeight() / 2,
                ImGuiCond.None, 0.5f, 0.5f);
        ImGui.setNextWindowSize(Settings.POPUP_WIDTH, 0);
        if (ImGui.beginPopupModal("Add vehicle", new ImBoolean(true), ImGuiWindowFlags.NoResize)) {

            // Get available window width
            float contentWidth = ImGui.getContentRegionAvailX();

            // Get selected vehicle class name
            String selectedVehicleClassName = selectedVehicleClass != null ?
                    selectedVehicleClass.getSimpleName() : "Select vehicle type";

            // Render vehicle type selector
            ImGui.text("Vehicle type");
            ImGui.setNextItemWidth(contentWidth);
            if (ImGui.beginCombo("##combo_vehicle_type", selectedVehicleClassName, ImGuiComboFlags.NoArrowButton)) {
                for (Class<? extends BraitenbergVehicle> vehicleClass : vehicleClasses) {
                    if (ImGui.selectable(vehicleClass.getSimpleName(), selectedVehicleClass == vehicleClass)) {
                        selectedVehicleClass = vehicleClass;
                    }
                }
                ImGui.endCombo();
            }

            // Render position sliders
            ImGui.text("Position (x, z)");
            ImGui.setNextItemWidth(contentWidth);
            ImGui.sliderFloat2("##slider_coordinates", position,
                    -Settings.ARENA_RADIUS, Settings.ARENA_RADIUS, "%.0f");

            // Render rotation slider
            ImGui.text("Rotation (degrees)");
            ImGui.setNextItemWidth(contentWidth);
            ImGui.sliderFloat("##slider_rotation", rotation, 0, 360, "%.0f");

            ImGui.separator();

            boolean disabled = selectedVehicleClass == null;

            // Disable add button if no vehicle type is selected
            if (disabled) {
                ImGui.beginDisabled();
            }

            // Render add button
            if (ImGui.button("Add", contentWidth, 0)) {

                    ImGui.closeCurrentPopup();
                    try {
                        Vector3f positionVector = new Vector3f(position[0], 0, position[1]);
                        Vector3f rotationVector = new Vector3f(0, rotation[0], 0);
                        BraitenbergVehicle vehicle = selectedVehicleClass.
                                getConstructor(ModelLoader.class, Vector3f.class, Vector3f.class).
                                newInstance(modelLoader, positionVector, rotationVector);
                        bodies.add(vehicle);
                        vehicle.setCollisionBodies(bodies);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    selectedVehicleClass = null;
                    position = new float[]{0, 0};
                    rotation = new float[]{0};

            }

            if (disabled) {
                ImGui.endDisabled();
            }

            ImGui.endPopup();
        }
    }

    private void updateVehicleSpeed() {
        vehicleSpeedsLeft.put(vehicleSpeedsIndex, selectedVehicle.getSpeedLeft());
        vehicleSpeedsRight.put(vehicleSpeedsIndex, selectedVehicle.getSpeedRight());
        vehicleSpeedsIndex = (vehicleSpeedsIndex + 1) % Settings.GUI_GRAPH_HISTORY_SIZE;
    }

    private float[] getVehicleSpeedsLeft() {
        // Copy the contents of the buffer into the float array
        for (int i = 0; i < Settings.GUI_GRAPH_HISTORY_SIZE; i++) {
            speeds[i] = vehicleSpeedsLeft.get((vehicleSpeedsIndex + i) % Settings.GUI_GRAPH_HISTORY_SIZE);
        }
        return speeds;
    }

    private float[] getVehicleSpeedsRight() {
        // Copy the contents of the buffer into the float array
        for (int i = 0; i < Settings.GUI_GRAPH_HISTORY_SIZE; i++) {
            speeds[i] = vehicleSpeedsRight.get((vehicleSpeedsIndex + i) % Settings.GUI_GRAPH_HISTORY_SIZE);
        }
        return speeds;
    }
}
