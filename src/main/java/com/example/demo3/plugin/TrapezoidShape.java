package com.example.demo3.plugin;

import com.example.demo3.shapes.ShapePaint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.Map;

public class TrapezoidShape {
    public static class Trapezoid extends ShapePaint {
        private double startX, startY;
        private double endX, endY;
        private double topWidthRatio = 0.7;
        private Polygon polygon;

        @Override
        protected String getShapeType() {
            return "Trapezoid";
        }
        @Override
        public Map<String, Object> toMap() {
            Map<String, Object> map = super.toMap();
            map.put("startX", startX);
            map.put("startY", startY);
            map.put("endX", endX);
            map.put("endY", endY);
            map.put("topWidthRatio", topWidthRatio);
            return map;
        }

        @Override
        public void fromMap(Map<String, Object> map) {
            super.fromMap(map);
            this.startX = ((Number) map.get("startX")).doubleValue();
            this.startY = ((Number) map.get("startY")).doubleValue();
            this.endX = ((Number) map.get("endX")).doubleValue();
            this.endY = ((Number) map.get("endY")).doubleValue();
            this.topWidthRatio = ((Number) map.get("topWidthRatio")).doubleValue();

            draw();
            updatePolygonPoints();
        }

        @Override
        public Shape draw() {
            System.out.println("Trapezoid.draw() called");
            if (polygon == null) {
                polygon = new Polygon();
                polygon.setFill(Color.RED);
                polygon.setStroke(Color.BLUE);
                polygon.setStrokeWidth(2.0);
                setJavaFXShape(polygon);
            }
            updatePolygonPoints();
            return polygon;
        }
        @Override
        public boolean isPluginShape() {
            return true;
        }
        private void updatePolygonPoints() {
            if (polygon == null) return;

            double width = Math.abs(endX - startX);
            double height = Math.abs(endY - startY);
            double topWidth = width * topWidthRatio;
            double topOffset = (width - topWidth) / 2;

            double left = Math.min(startX, endX);
            double top = Math.min(startY, endY);

            System.out.printf("Coordinates: left=%.1f top=%.1f width=%.1f height=%.1f%n",
                    left, top, width, height);

            polygon.getPoints().clear();
            polygon.getPoints().addAll(
                    left, top + height,
                    left + width, top + height,
                    left + width - topOffset, top,
                    left + topOffset, top
            );
        }

        @Override
        public void updateShape(double x, double y) {
            this.endX = x;
            this.endY = y;
            updatePolygonPoints();
        }

        @Override
        public void finalizeShape(double x, double y) {
            updateShape(x, y);
        }

        @Override
        public void setStart(double x, double y) {
            this.startX = x;
            this.startY = y;
            this.endX = x;
            this.endY = y;
        }

        @Override
        public void reset() {
            polygon = null;
        }

        @Override
        public void finishCreation() {
        }
    }
}