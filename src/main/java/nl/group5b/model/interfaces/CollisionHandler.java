package nl.group5b.model.interfaces;
import nl.group5b.model.Body;
import nl.group5b.model.HitBox;
import org.joml.Vector3f;

import java.util.List;

public interface CollisionHandler {
    // Function that checks if the body of the object collides with another body
    // @param hitBox: The hitbox of the object
    // @param bodies: The bodies that the object can potentially collide with (possible implementing the CollisionHandler interface)
    // @return: True if the object collides with another body, false if not
    public boolean isColliding(HitBox hitBox, List<Body> bodies);

    // Function that checks if the hitbox of the object collides with the object itself
    public boolean isInHitBox(HitBox hitBox);
}
