package nl.group5b.model;

import nl.group5b.engine.Entity;

public class BodyElement {

    private Entity entity;
    private Material material;

    public BodyElement(Entity entity, Material material) {
        this.entity = entity;
        this.material = material;
    }

    public Entity getEntity() {
        return entity;
    }

    public Material getMaterial() {
        return material;
    }
}