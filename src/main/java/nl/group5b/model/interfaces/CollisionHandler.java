package nl.group5b.model.interfaces;
import nl.group5b.model.HitBox;

public interface CollisionHandler {

    // Function that checks if front of the object collides with another body
    // @return: True if the object collides with another body, false if not
    boolean isColliding();

    // Function that returns the hitbox of the object
    HitBox getHitBox();
}
