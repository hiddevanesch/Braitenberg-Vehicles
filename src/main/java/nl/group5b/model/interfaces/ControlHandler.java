package nl.group5b.model.interfaces;

import nl.group5b.engine.Renderer;

public interface ControlHandler {
    // This function should "move" a body based on user input
    void move(long window, Renderer renderer);
}
