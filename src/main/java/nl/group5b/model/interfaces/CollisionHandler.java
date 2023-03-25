package nl.group5b.model.interfaces;
import nl.group5b.model.Body;
import nl.group5b.model.HitBox;
import org.joml.Vector3f;

import java.util.List;

public interface CollisionHandler {
    // Function that checks if the body of the object collides with another body
    // @param nextCoordinates: The coordinates the object will have after the next update, and that is used to check for collision
    // @param bodies: The bodies that the object can potentially collide with (possible implementing the CollisionHandler interface)
    // @return: True if the object collides with another body, false if not
    public boolean isColliding(Vector3f[] nextCoordinates, List<Body> bodies);

    // Function that checks if the next coordinates of the object are in the hitbox of the target
    public boolean isInHitBox(Vector3f[] nextCoordinates ,HitBox hitBoxTarget);

    // Function that returns the hitbox of the object
    public HitBox getHitBox();
}
