package nl.group5b.gui.elements;

import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImBoolean;
import nl.group5b.camera.BodyCamera;
import nl.group5b.camera.Camera;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.gui.Element;
import nl.group5b.light.Light;
import nl.group5b.model.Body;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.models.AttachableLamp;
import nl.group5b.model.models.BraitenbergVehicle;
import nl.group5b.model.models.StaticLamp;
import nl.group5b.shaders.real.RealShader;
import nl.group5b.util.Settings;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends Element {

    private final ModelLoader modelLoader;

    private final List<Body> bodies;
    private final List<Light> lights;
    private final RealShader shader;
    List<Class<? extends BraitenbergVehicle>> vehicleClasses = new ArrayList<>();
    List<Class<? extends StaticLamp>> lampClasses = new ArrayList<>();

    private Camera camera;
    private final BodyCamera thirdPersonCamera = new BodyCamera();
    private final Camera topDownCamera = new Camera(new Vector3f(0, 20, 0), new Vector3f(90, 0, 0));

    private BraitenbergVehicle selectedVehicle = null;
    private Class<? extends BraitenbergVehicle> selectedVehicleClass = null;
    private final float[] currentVehiclePosition = {0, 0};
    private final float[] currentVehicleRotation = {0};
    private final float[] vehicleSpeedsLeft = new float[Settings.GUI_GRAPH_HISTORY_SIZE];
    private final float[] vehicleSpeedsRight = new float[Settings.GUI_GRAPH_HISTORY_SIZE];
    private final float[] speeds = new float[Settings.GUI_GRAPH_HISTORY_SIZE];
    private int vehicleSpeedsIndex = 0;
    private final float[] currentSensorFov = {Settings.SENSOR_FOV};
    private float[] vehicleSpawnPosition = {0, 0};
    private float[] vehicleSpawnRotation = {0};

    private StaticLamp selectedLamp = null;
    private Class<? extends StaticLamp> selectedLampClass = null;
    private final float[] currentLampPosition = {0, 0, 0};
    private final float[] currentLampColour = {0, 0, 0};
    private final float[] currentLampAttenuation = {0, 0, 0};

    public MainPanel(long window, ModelLoader modelLoader, RealShader shader, List<Body> bodies, List<Light> lights) {
        this.modelLoader = modelLoader;
        this.bodies = bodies;
        this.shader = shader;
        this.lights = lights;

        thirdPersonCamera.enableZoom(window);
        thirdPersonCamera.enableMouseTracking(window);

        camera = topDownCamera;

        // Add vehicle classes
        getVehicleTypes();

        // Add lamp classes
        getLampTypes();
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

    private void getLampTypes() {
        // Get lamp package
        String lampPackage = StaticLamp.class.getPackageName();

        // Get the URL of the package directory
        URL packageUrl = Thread.currentThread().getContextClassLoader().getResource(
                lampPackage.replace(".", "/")
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
                            String className = lampPackage + "." +
                                    file.getName().substring(0, file.getName().length() - 6);
                            Class<?> cls = Class.forName(className);

                            // Check if the class is a subtype of Lamp (or Lamp itself), but not AttachableLamp
                            if (StaticLamp.class.isAssignableFrom(cls) && !AttachableLamp.class.isAssignableFrom(cls)) {
                                lampClasses.add(cls.asSubclass(StaticLamp.class));
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
    public void render() throws FileNotFoundException {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Settings.PANEL_WIDTH_MAIN, DisplayBuilder.getHeight());
        ImGui.begin("Main", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        float contentWidth = ImGui.getContentRegionAvailX();

        ImGui.pushStyleVar(ImGuiStyleVar.TabRounding, 0);

        ImGui.beginTabBar("##tab_bar_main");

        if (ImGui.beginTabItem("Vehicles")) {
            renderVehiclesTab(contentWidth);
            ImGui.endTabItem();
        }

        if (ImGui.beginTabItem("Lights")) {
            renderLightsTab(contentWidth);
            ImGui.endTabItem();
        }

        ImGui.endTabBar();

        ImGui.popStyleVar();

        ImGui.end();
    }

    private void renderVehiclesTab(float contentWidth) throws FileNotFoundException {
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
        ImGui.setNextItemWidth(selectorWidth);
        if (ImGui.beginCombo("##combo_vehicles", selectedVehicleName)){
            for (Body body : bodies) {
                if (body instanceof BraitenbergVehicle braitenbergVehicle) {
                    if (ImGui.selectable(braitenbergVehicle.getName(), selectedVehicle == braitenbergVehicle)) {
                        selectedVehicle = braitenbergVehicle;
                        clearVehicleSpeeds();
                        if (camera == thirdPersonCamera) {
                            thirdPersonCamera.resetView();
                            thirdPersonCamera.setBody(selectedVehicle);
                        }
                    }
                }
            }
            ImGui.endCombo();
        }

        // Render vehicle remove button
        ImGui.sameLine();
        if (ImGui.button("-", buttonWidth, 0)) {
            if (selectedVehicle != null) {
                AttachableLamp attachableLamp = selectedVehicle.getLamp();
                if (attachableLamp != null) {
                    if (selectedLamp == attachableLamp) {
                        selectedLamp = null;
                    }
                    bodies.remove(attachableLamp);
                    lights.remove(attachableLamp.getLight());
                    shader.recompile(lights.size());
                }

                if (camera == thirdPersonCamera) {
                    camera = topDownCamera;
                    thirdPersonCamera.unbind();
                }

                bodies.remove(selectedVehicle);
            }
        }

        // Render vehicle add button
        ImGui.sameLine();
        if (ImGui.button("+", buttonWidth, 0)) {
            ImGui.openPopup("Add vehicle");
        }

        renderAddVehiclePopup();

        ImGui.separator(); // =======================================================================================

        if (selectedVehicle != null) {
            updateVehicleSpeed();

            renderVehicleData(contentWidth);
        }
    }

    private void renderVehicleData(float contentWidth) throws FileNotFoundException {
        ImGui.beginChild("##child_vehicle");

        ImGui.dummy(0, 5);

        updateVehiclePosition();

        ImGui.text("Position (x, z)");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat2("##slider_vehicle_position", currentVehiclePosition,
                -Settings.ARENA_SPAWN_RADIUS, Settings.ARENA_SPAWN_RADIUS, "%.1f")) {
            selectedVehicle.setPosition(new Vector3f(
                    currentVehiclePosition[0],
                    0,
                    currentVehiclePosition[1]
            ));
        }

        updateVehicleRotation();

        ImGui.text("Rotation");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat("##slider_vehicle_rotation", currentVehicleRotation,
                0, 360, "%.0f")) {
            selectedVehicle.setRotation(new Vector3f(0, currentVehicleRotation[0], 0));
        }

        ImGui.dummy(0, 10);

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
        ImGui.text("Current\nwheel speed\n\u00A0");
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
        ImGui.text("Brightness\nvalues\n\u00A0");
        ImGui.tableSetColumnIndex(1);
        String leftBrightness = String.format("%.2f", selectedVehicle.getLeftSensor().calculateSensorBrightness());
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (ImGui.getContentRegionAvailX() - ImGui.calcTextSize(leftBrightness).x) / 2.0f);
        ImGui.text(leftBrightness);
        ImGui.tableSetColumnIndex(2);
        String rightBrightness = String.format("%.2f", selectedVehicle.getRightSensor().calculateSensorBrightness());
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (ImGui.getContentRegionAvailX() - ImGui.calcTextSize(rightBrightness).x) / 2.0f);
        ImGui.text(rightBrightness);

        ImGui.endTable();

        updateVehicleSensorFov();

        ImGui.text("Sensor FOV");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat("##slider_sensor_fov", currentSensorFov,
                10, 90, "%.0f")) {
            selectedVehicle.getLeftSensor().setFov(currentSensorFov[0]);
            selectedVehicle.getRightSensor().setFov(currentSensorFov[0]);
        }

        ImGui.dummy(0, 10);

        if (ImGui.checkbox("Third person camera", camera == thirdPersonCamera)) {
            if (camera == topDownCamera) {
                thirdPersonCamera.resetView();
                thirdPersonCamera.setBody(selectedVehicle);
                camera = thirdPersonCamera;
            } else {
                camera = topDownCamera;
                thirdPersonCamera.unbind();
            }
        }

        ImGui.dummy(0, 10);

        boolean selectedVehicleHasLamp = selectedVehicle.hasLamp();

        String lampText = "Attach lamp\n" + (selectedVehicleHasLamp ? "(" + selectedVehicle.getLamp().getName() + ")" : "\u00A0");

        if (ImGui.checkbox(lampText, selectedVehicleHasLamp)) {
            if (selectedVehicleHasLamp) {
                StaticLamp attachableLamp = selectedVehicle.getLamp();
                selectedVehicle.removeLamp();
                bodies.remove(attachableLamp);
                lights.remove(attachableLamp.getLight());
                shader.recompile(lights.size());
            } else {
                AttachableLamp attachableLamp = new AttachableLamp(
                        modelLoader, new Vector3f(Settings.LAMP_DEFAULT_POSITION),
                        new Vector3f(Settings.LAMP_DEFAULT_COLOUR), new Vector3f(Settings.LAMP_DEFAULT_ATTENUATION));
                attachableLamp.setVehicle(selectedVehicle);
                selectedVehicle.attachLamp(attachableLamp);
                bodies.add(attachableLamp);
                lights.add(attachableLamp.getLight());
                shader.recompile(lights.size());
            }
        }

        ImGui.endChild();
    }

    private void updateVehicleSensorFov() {
        currentSensorFov[0] = selectedVehicle.getSensorFov();
    }

    private void renderAddVehiclePopup() {
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
            if (ImGui.beginCombo("##combo_vehicle_type", selectedVehicleClassName)) {
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
            ImGui.sliderFloat2("##slider_vehicle_spawn_coordinates", vehicleSpawnPosition,
                    -Settings.ARENA_SPAWN_RADIUS, Settings.ARENA_SPAWN_RADIUS, "%.0f");

            // Render rotation slider
            ImGui.text("Rotation (degrees)");
            ImGui.setNextItemWidth(contentWidth);
            ImGui.sliderFloat("##slider_vehicle_spawn_rotation", vehicleSpawnRotation, 0, 360, "%.0f");

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
                        Vector3f positionVector = new Vector3f(vehicleSpawnPosition[0], 0, vehicleSpawnPosition[1]);
                        Vector3f rotationVector = new Vector3f(0, vehicleSpawnRotation[0], 0);
                        BraitenbergVehicle vehicle = selectedVehicleClass.
                                getConstructor(ModelLoader.class, Vector3f.class, Vector3f.class).
                                newInstance(modelLoader, positionVector, rotationVector);
                        bodies.add(vehicle);
                        vehicle.setBodies(bodies);
                        selectedVehicle = vehicle;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    selectedVehicleClass = null;
                    vehicleSpawnPosition = new float[]{0, 0};
                    vehicleSpawnRotation = new float[]{0};

            }

            if (disabled) {
                ImGui.endDisabled();
            }

            ImGui.endPopup();
        }
    }

    private void renderLightsTab(float contentWidth) {
        float buttonWidth = 30;
        float selectorWidth = contentWidth - 2 * buttonWidth - 2 * ImGui.getStyle().getItemSpacingX();

        // Get selected lamp name
        String selectedLampName = selectedLamp != null ? selectedLamp.getName() : "Select lamp";

        // Render lamp selector
        ImGui.setNextItemWidth(selectorWidth);
        if (ImGui.beginCombo("##combo_lamps", selectedLampName)){
            for (Body body : bodies) {
                if (body instanceof StaticLamp lamp) {
                    if (ImGui.selectable(lamp.getName(), selectedLamp == lamp)) {
                        setSelectedLamp(lamp);

                    }
                }
            }
            ImGui.endCombo();
        }

        // Render lamp remove button
        ImGui.sameLine();
        if (ImGui.button("-", buttonWidth, 0)) {
            if (selectedLamp != null) {
                bodies.remove(selectedLamp);
                lights.remove(selectedLamp.getLight());
                shader.recompile(lights.size());
                if (selectedLamp instanceof AttachableLamp attachableLamp) {
                    if (attachableLamp.getVehicle() != null) {
                        attachableLamp.getVehicle().removeLamp();
                    }
                }
                selectedLamp = null;
            }
        }

        // Render lamp add button
        ImGui.sameLine();
        if (ImGui.button("+", buttonWidth, 0)) {
            ImGui.openPopup("Add lamp");
        }

        renderAddLampPopup();

        ImGui.separator(); // =======================================================================================

        if (selectedLamp != null) {
            renderLampData(contentWidth);
        }
    }

    private void renderAddLampPopup() {
        // Add vehicle popup
        ImGui.setNextWindowPos(
                (float) DisplayBuilder.getWidth() / 2,
                (float) DisplayBuilder.getHeight() / 2,
                ImGuiCond.None, 0.5f, 0.5f);
        ImGui.setNextWindowSize(Settings.POPUP_WIDTH, 0);
        if (ImGui.beginPopupModal("Add lamp", new ImBoolean(true), ImGuiWindowFlags.NoResize)) {

            // Get available window width
            float contentWidth = ImGui.getContentRegionAvailX();

            // Get selected vehicle class name
            String selectedLampClassName = selectedLampClass != null ?
                    selectedLampClass.getSimpleName() : "Select lamp type";

            // Render vehicle type selector
            ImGui.text("Lamp type");
            ImGui.setNextItemWidth(contentWidth);
            if (ImGui.beginCombo("##combo_lamp_type", selectedLampClassName)) {
                for (Class<? extends StaticLamp> lampClass : lampClasses) {
                    if (ImGui.selectable(lampClass.getSimpleName(), selectedLampClass == lampClass)) {
                        selectedLampClass = lampClass;
                    }
                }
                ImGui.endCombo();
            }

            ImGui.separator();

            boolean disabled = selectedLampClass == null;

            // Disable add button if no vehicle type is selected
            if (disabled) {
                ImGui.beginDisabled();
            }

            // Render add button
            if (ImGui.button("Add", contentWidth, 0)) {

                ImGui.closeCurrentPopup();
                try {
                    StaticLamp lamp = selectedLampClass.
                            getConstructor(ModelLoader.class, Vector3f.class, Vector3f.class, Vector3f.class).
                            newInstance(
                                    modelLoader,
                                    new Vector3f(Settings.LAMP_DEFAULT_POSITION),
                                    new Vector3f(Settings.LAMP_DEFAULT_COLOUR),
                                    new Vector3f(Settings.LAMP_DEFAULT_ATTENUATION)
                            );
                    bodies.add(lamp);
                    lights.add(lamp.getLight());
                    shader.recompile(lights.size());
                    setSelectedLamp(lamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                selectedLampClass = null;
            }

            if (disabled) {
                ImGui.endDisabled();
            }

            ImGui.endPopup();
        }
    }

    private void renderLampData(float contentWidth) {
        ImGui.dummy(0, 5);

        if (!(selectedLamp instanceof AttachableLamp)) {
            Vector3f lampPosition = selectedLamp.getPosition();
            currentLampPosition[0] = lampPosition.x();
            currentLampPosition[1] = lampPosition.y();
            currentLampPosition[2] = lampPosition.z();

            ImGui.text("Position (x, y, z)");
            ImGui.setNextItemWidth(contentWidth);
            if (ImGui.sliderFloat3("##slider_position", currentLampPosition,
                    -Settings.ARENA_SPAWN_RADIUS, Settings.ARENA_SPAWN_RADIUS, "%.1f")) {
                selectedLamp.setPosition(
                        new Vector3f(currentLampPosition[0], currentLampPosition[1], currentLampPosition[2])
                );
            }
        }

        ImGui.text("Color (r, g, b)");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.colorEdit3("##color_button", currentLampColour)) {
            selectedLamp.setColour(new Vector3f(currentLampColour[0], currentLampColour[1], currentLampColour[2]));
        }

        ImGui.text("Attenuation (c, l, q)");
        ImGui.setNextItemWidth(contentWidth);
        if (ImGui.sliderFloat3("##slider_attenuation", currentLampAttenuation, 0.2f, 1, "%.1f")) {
            selectedLamp.getLight().setAttenuation(
                    new Vector3f(currentLampAttenuation[0], currentLampAttenuation[1], currentLampAttenuation[2])
            );
        }

    }

    private void updateVehiclePosition() {
        currentVehiclePosition[0] = selectedVehicle.getPosition().x();
        currentVehiclePosition[1] = selectedVehicle.getPosition().z();
    }

    private void updateVehicleRotation() {
        float rotation = selectedVehicle.getRotation().y() % 360;
        if (rotation < 0) {
            rotation += 360;
        }
        currentVehicleRotation[0] = rotation;
    }

    private void updateVehicleSpeed() {
        vehicleSpeedsLeft[vehicleSpeedsIndex] = selectedVehicle.getSpeedLeft();
        vehicleSpeedsRight[vehicleSpeedsIndex] = selectedVehicle.getSpeedRight();
        vehicleSpeedsIndex = (vehicleSpeedsIndex + 1) % Settings.GUI_GRAPH_HISTORY_SIZE;
    }

    private float[] getVehicleSpeedsLeft() {
        // Copy the contents of the buffer into the float array
        for (int i = 0; i < Settings.GUI_GRAPH_HISTORY_SIZE; i++) {
            speeds[i] = vehicleSpeedsLeft[(vehicleSpeedsIndex + i) % Settings.GUI_GRAPH_HISTORY_SIZE];
        }
        return speeds;
    }

    private float[] getVehicleSpeedsRight() {
        // Copy the contents of the buffer into the float array
        for (int i = 0; i < Settings.GUI_GRAPH_HISTORY_SIZE; i++) {
            speeds[i] = vehicleSpeedsRight[(vehicleSpeedsIndex + i) % Settings.GUI_GRAPH_HISTORY_SIZE];
        }
        return speeds;
    }

    private void clearVehicleSpeeds() {
        for (int i = 0; i < Settings.GUI_GRAPH_HISTORY_SIZE; i++) {
            vehicleSpeedsLeft[i] = 0;
            vehicleSpeedsRight[i] = 0;
        }
        vehicleSpeedsIndex = 0;
    }

    private void setSelectedLamp(StaticLamp lamp) {
        selectedLamp = lamp;

        // The position is updated every frame (because the ControllableLamp can move)

        Vector3f lampColour = lamp.getColour();
        currentLampColour[0] = lampColour.x();
        currentLampColour[1] = lampColour.y();
        currentLampColour[2] = lampColour.z();

        Vector3f lampAttenuation = lamp.getAttenuation();
        currentLampAttenuation[0] = lampAttenuation.x();
        currentLampAttenuation[1] = lampAttenuation.y();
        currentLampAttenuation[2] = lampAttenuation.z();
    }

    public Camera getCamera() {
        return camera;
    }
}
