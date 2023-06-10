package com.example.astarproj;
// Note undirected graph

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Driver extends Application {
    final static Font font3 = Font.font("Times New Roman", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 20);
    final static Font font4 = Font.font("Times New Roman", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 12);
    private static final BorderPane border = new BorderPane();
    private static TextArea txtArea_result = new TextArea();
    private static TextArea txtArea_path = new TextArea();
    private static byte selected = 0;
    static ComboBox<String> cmb_start;
    static ComboBox<String> cmb_target;
    private static Map<String, City> citiesMap = new HashMap<>();
    private static Map<Pair<String, String>, Double> hueristicMap = new HashMap<>();
    static TableEntry[] table;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.DECORATED);
        InputStream photoStream = new FileInputStream("Palestine.png");
        table = new TableEntry[citiesMap.size() + 1];
        initializeTable();
        Image worldMap = new Image(photoStream);
        ImageView view = new ImageView();
        view.setImage(worldMap);
        view.setFitWidth(650);
        view.setFitHeight(750);
        border.getChildren().add(view);
        Label lbl_start = new Label("Start: ");
        lbl_start.setFont(font3);
        cmb_start = new ComboBox<>();
        cmb_start.setPromptText("Select your Start");
        HBox hstart = new HBox(lbl_start, cmb_start);
        hstart.setSpacing(20);
        Label lbl_target = new Label("Target:");
        lbl_target.setFont(font3);
        cmb_target = new ComboBox<>();
        cmb_target.setPromptText("Select Your target");
        for (Map.Entry<String, City> c : citiesMap.entrySet()) {
            cmb_start.getItems().add(c.getValue().getName());
            cmb_target.getItems().add(c.getValue().getName());
            c.getValue().c.setOnMouseClicked(e -> {
                if (selected == 0) {
                    cmb_start.setValue(c.getValue().getName());
                    selected++;
                } else {
                    cmb_target.setValue(c.getValue().getName());
                    selected = 0;
                }
            });
        }
        HBox htarget = new HBox(lbl_target, cmb_target);
        htarget.setSpacing(20);
        Button btnStart = new Button("Start");
        Button btnClear = new Button("Clear");
        btnStart.setFont(font3);
        btnClear.setFont(font3);
        HBox btn_box = new HBox(btnStart, btnClear);
        btn_box.setSpacing(20);
        btn_box.setAlignment(Pos.CENTER);
        Label lbl_result = new Label("Result");
        lbl_result.setFont(font3);
        txtArea_result.setPrefRowCount(10);
        txtArea_result.setPrefColumnCount(20);
        txtArea_result.setFont(font4);
        HBox hArea = new HBox(lbl_result, txtArea_result);
        hArea.setSpacing(5);
        Label lbl_path = new Label("Path:");
        lbl_path.setFont(font3);
        txtArea_path.setPrefRowCount(5);
        txtArea_path.setPrefColumnCount(20);
        txtArea_path.setFont(font4);
        HBox hPath = new HBox(lbl_path, txtArea_path);
        hPath.setSpacing(5);
        VBox v = new VBox(hstart, htarget, btn_box, hArea, hPath);
        v.setSpacing(50);
        border.setRight(v);
        border.setPadding(new Insets(40, 20, 20, 20));
        for (Map.Entry<String, City> c : citiesMap.entrySet()) {
            border.getChildren().add(c.getValue().c);
            border.getChildren().add(c.getValue().line);
            c.getValue().line.setVisible(false);
        }
        border.setStyle("-fx-background-color:SkyBlue;");
        Scene scene = new Scene(border, 1020, 750);
//         Get x and y position of a scene
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println(event.getSceneX());
				System.out.println(event.getSceneY());
			}
		});
//        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        btnStart.setOnAction(e -> {
            try {
                OnStart();
            } catch (Exception nullExc) {
                nullExc.printStackTrace();
                warning_Message("Enter starting point and end point");
            }
        });
        btnClear.setOnAction(e -> {
            OnClear();
        });
    }

    private void OnStart() throws Exception {
        if (table.length != 0) {
            OnClear();
        }
        try {
            initializeTable();
            String start = cmb_start.getValue();
            String end = cmb_target.getValue();
            Astar search = new Astar(hueristicMap);
            if (start != null && end != null) {
                table = search.findPath(citiesMap.get(start), citiesMap.get(end), table);
                StringBuilder path = new StringBuilder("");
                printPath(citiesMap.get(end), path);
                txtArea_path.setText(path.toString());
                txtArea_result.setText("Distance to go from " + start + " to " + end + "\n="
                        + table[citiesMap.get(end).cityEntry].getDistance() + "km");
            }else{
                warning_Message("Enter starting point and end point");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void OnClear() {
//        initializeTable();
        txtArea_path.clear();
        txtArea_result.clear();
        for (int i = 0; i < table.length; i++) {
            table[i].known = false;
            if (table[i].path != null) {
                System.out.println(table[i].path);
                table[i].path.line.setVisible(false);
                table[i].path.c.setFill(Color.RED);
            }
            table[i].path = null;
            table[i].distance = Double.MAX_VALUE;
        }
    }

    private void printPath(City start, StringBuilder s) {//Note to bebo start her is end
        if (table[start.cityEntry].path != null) {
            table[start.cityEntry].path.line.setEndX(start.c.getTranslateX());
            table[start.cityEntry].path.line.setEndY(start.c.getTranslateY());
            table[start.cityEntry].path.line.setVisible(true);
            table[start.cityEntry].path.c.setFill(Color.BLUE);
            printPath(table[start.cityEntry].path, s);
            s.append("to :");
        }
        s.append(start + " Distance: " + table[start.cityEntry].getDistance() + " km\n");
    }

    private void initializeTable() {
        for (int i = 0; i < table.length; i++) {
            table[i] = new TableEntry();
            table[i].known = false;
            table[i].path = null;
            table[i].distance = Double.MAX_VALUE;
        }
    }

    private static void readHuersticTable(String fileName) {
        File hFile = new File(fileName);
        try (Scanner input = new Scanner(hFile)) {
            while (input.hasNext()) {
                String data = input.nextLine();
                String[] tok = data.split(" ");//0: first city, 1 second city, 2 is air distance
                Pair<String, String> pair = new Pair<>(tok[0], tok[1]);
                hueristicMap.put(pair, Double.parseDouble(tok[2]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void readData(String fileName) {
        File stdFile = new File(fileName);
        try (Scanner input = new Scanner(stdFile)) {
            String numOfData = input.nextLine();
            String[] str = numOfData.split(" ");
            int countries = Integer.parseInt(str[0]);
            int edges = Integer.parseInt(str[1]);
            int countriesRead = 0;
//            int edgesRead =0;
            while (input.hasNext()) {
                if (countriesRead < countries) {
                    String countryData = input.nextLine();
                    String[] tok = countryData.split(",");
                    City city = new City(tok[0].strip(), Double.parseDouble(tok[1].strip()),
                            Double.parseDouble(tok[2].strip()));
                    citiesMap.put(city.getName(), city);
                    countriesRead++;
                } else {
                    String edgesData = input.nextLine();
                    String[] tok = edgesData.split(" ");
                    citiesMap.get((tok[0])).adjacent.add(new Adjacent(citiesMap.get(tok[1]), Float.parseFloat(tok[2])));
                    citiesMap.get(tok[1]).adjacent.add(new Adjacent(citiesMap.get(tok[0]), Float.parseFloat(tok[2])));
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            warning_Message(e.toString());
        }
    }

    private static void setLattitudeAndLongtiude(String file) {
        File stdFile = new File(file);
        try (Scanner input = new Scanner(stdFile)) {
            while (input.hasNext()) {
                String countryData = input.nextLine();
                String[] tok = countryData.split(",");

                citiesMap.get(tok[0]).lattitude = Double.parseDouble(tok[1]);
                citiesMap.get(tok[0]).longtidue = Double.parseDouble(tok[2]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            warning_Message(e.toString());
        }
    }

    public static void main(String[] args) {
        try {
            readData("cities.txt");
            readHuersticTable("Huerstic.txt");
            setLattitudeAndLongtiude("CitiesLongLat.txt");
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
//			warning_Message(e.toString());
        }
    }

    public static void warning_Message(String x) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(x);
        alert.show();
    }
}
