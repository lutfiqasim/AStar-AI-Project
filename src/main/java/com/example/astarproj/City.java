package com.example.astarproj;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.*;

public class City {
    private double x;//on map
    private double y;//on map
    double lattitude;
    double longtidue;
    Tooltip toolTipTxt;
    private String name;
    private static int number = 0;
    int cityEntry;
    Circle c;
    Set<Adjacent> adjacent = new HashSet<>();
    Line line;

    public City(String name, double x, double y) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.line = new Line();
        createCircle();
        this.cityEntry = number++;
    }

    private void createCircle() {
        line = new Line();
        line.toFront();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        c = new Circle(3);
        c.setFill(Color.RED);
        c.setTranslateZ(4);
        setXAndYProperty();
        line.setStartX(c.getTranslateX());
        line.setStartY(c.getTranslateY());
        Tooltip toolTipTxt = new Tooltip(this.name);
        // Setting the tool tip to the text field
        Tooltip.install(c, toolTipTxt);
        c.setOnMouseEntered(e -> {
            c.setRadius(10);
        });
        c.setOnMouseExited(e -> {
            c.setRadius(3);
        });

    }

    private void setXAndYProperty() {
        double xPosition = x;
        double yPosition = y;
//        if (longtidue < 0 && lattitude > 0) {
//            xPosition = (650 / 2.0) + (this.longtidue * 3.3048);// Longtidue
//            yPosition = (750 / 2.0) - (this.lattitude * 2.8811);// Latitude
//        } else {
//            xPosition = (650/ 2.0) + (this.longtidue * 3.3048);// Longtidue : 3.3048 3.3048
//            yPosition = (750 / 2.0) - (this.lattitude * 2.3611);// Latitude 2.0104  2.3611
//        }
        c.setTranslateX(xPosition);
        c.setTranslateY(yPosition);
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "name=" + name;
    }
}
