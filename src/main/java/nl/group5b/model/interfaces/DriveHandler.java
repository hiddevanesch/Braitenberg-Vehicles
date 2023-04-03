package nl.group5b.model.interfaces;

import nl.group5b.engine.Renderer;

public interface DriveHandler {
    // Function that moves the vehicle by first updating the wheel speeds
    // and then updating the position
    void move(Renderer renderer);
}
