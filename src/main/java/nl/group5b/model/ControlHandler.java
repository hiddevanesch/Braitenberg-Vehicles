package nl.group5b.model;

import nl.group5b.engine.Renderer;

public interface ControlHandler {
    // This function should "move" a body based on user input
    public void move(long window, Renderer renderer);
}
