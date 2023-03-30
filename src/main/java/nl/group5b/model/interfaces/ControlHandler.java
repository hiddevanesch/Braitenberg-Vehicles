package nl.group5b.model.interfaces;

import nl.group5b.engine.Renderer;

public interface ControlHandler {
    // Function that moves the vehicle by first updating the wheel speeds
    // and then updating the position
    void move(long window, Renderer renderer);
}
