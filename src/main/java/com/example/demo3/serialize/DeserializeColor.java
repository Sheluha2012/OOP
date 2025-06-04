package com.example.demo3.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javafx.scene.paint.Color;

import java.io.IOException;

public class DeserializeColor extends JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Color.web(p.getText());
    }
}