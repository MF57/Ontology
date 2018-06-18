package org.agh.ontology.reason;

public enum AmbientLight {


    LowestAmbientLight(10.0f),
    LowAmbientLight(50.0f),
    MediumAmbientLight(100.0f),
    HighAmbientLight(200.0f),
    HighestAmbientLight(600.0f);

    public float getLimit() {
        return limit;
    }

    float limit;

    AmbientLight(float limit) {
        this.limit = limit;
    }
}
