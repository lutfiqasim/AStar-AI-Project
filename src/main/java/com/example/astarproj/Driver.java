package com.example.astarproj;
// Note undirected graph
// comment
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.awt.Desktop;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Driver extends Application {
    private static final Group mapGroup = new Group();
    private static TextArea txtArea_result = new TextArea();
    private static TextArea txtArea_path = new TextArea();
    private static byte selected = 0;
    static ComboBox<String> cmb_start;
    static ComboBox<String> cmb_target;

    Label distanceLbVal;
    private static Map<String, City> citiesMap = new HashMap<>();
    private static Map<Pair<String, String>, Double> hueristicMap = new HashMap<>();
    TableView<AlgorithimEntry> aStarVbfsTb = new TableView<>();

    static TableEntry[] AStarTable;

    static GraphPrinter gp = new GraphPrinter("citiesGraph");

    static DiGraphPainter aStartPathGraph = new DiGraphPainter("AStar");

    static DiGraphPainter bfsPathGraph = new DiGraphPainter("BFS");

    static ArrayList<Double> aStarPathLabels = new ArrayList<>();
    Image aStarGraphImg;

    Image bfsGraphImg;
    ImageView aStarGraphImgView;

    ImageView bfsGraphImgView;
    Button btnStart;
    Button btnClear;

    Label aStartPathLb ;
    Label bfsPathLb;

    static long aStarTime;
    static long bfsTime;

    static int aStarDistance;
    static int bfsDistance;


    AlgorithimEntry aStarEntry;
    AlgorithimEntry bfsEntry;




    @Override
    public void start(Stage primaryStage) throws Exception {
        cmb_target = new ComboBox<>();
        cmb_start = new ComboBox<>();
        Scene scene = initGUI();
        scene.getStylesheets().add(Driver.class.getResource("primer-light.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setTitle("Route Finder");
        btnStart.setOnAction(e -> {
            try {
                OnStart();
                btnClear.setDisable(false);
            } catch (Exception nullExc) {
                nullExc.printStackTrace();
                errorMsg("Enter starting point and end point");
            }
        });
        btnClear.setOnAction(e -> {
            OnClear();
        });
    }


    private void handleSelection(){
        for (Map.Entry<String, City> c : citiesMap.entrySet()) {
            cmb_start.getItems().add(c.getValue().getName());
            cmb_target.getItems().add(c.getValue().getName());
            c.getValue().c.setOnMouseClicked(e -> {
                if (selected == 0) {
                    cmb_start.setValue(c.getValue().getName());
                    selected++;
                    c.getValue().c.setFill(Color.GREEN);

                } else if(selected==1) {
                    cmb_target.setValue(c.getValue().getName());
                    selected ++;
                    btnStart.setDisable(false);
                    c.getValue().c.setFill(Color.ORANGE);
                }else{
                    OnClear();
                    cmb_start.setValue(c.getValue().getName());
                    selected=0;
                }
            });
        }
    }

    private Scene initGUI() {
        HBox mainPane = new HBox();
        mainPane.setPadding(new Insets(10, 20, 0, 20));
        VBox leftPane = new VBox();
        HBox fileBrowsePane = new HBox();
        fileBrowsePane.setAlignment(Pos.TOP_CENTER);
        HBox calcButtonPane = new HBox();
//        calcButtonPane.setMinHeight(300);
        calcButtonPane.setAlignment(Pos.TOP_CENTER);
        HBox routePane = new HBox();
        routePane.setAlignment(Pos.CENTER);
//        routePane.setSpacing(20);
        HBox distanceLbPane = new HBox();
        distanceLbPane.setAlignment(Pos.CENTER);
        StackPane tablePane = new StackPane();
        tablePane.setAlignment(Pos.BOTTOM_CENTER);
        tablePane.setMinHeight(300);
        StackPane mapPane = new StackPane();


        Label browseLabel = new Label("Data File: ");
        browseLabel.setDisable(true);
        TextField browseField = new TextField();
        browseField.setEditable(false);
        Button browseButton = new Button("Browse");
        fileBrowsePane.getChildren().addAll(browseLabel, browseField, browseButton);
        fileBrowsePane.setSpacing(10);

        btnStart = new Button("Calculate Route");
        btnClear = new Button("Clear");
        btnStart.setDisable(true);
        btnClear.setDisable(true);
        calcButtonPane.getChildren().addAll(btnStart, btnClear);
        calcButtonPane.setSpacing(10);

//        aStartPathLb = new Label();
//        aStartPathLb.setTextFill(Color.BLACK);
//        bfsPathLb = new Label();
//        bfsPathLb.setTextFill(Color.ORANGE);
//        routePane.getChildren().addAll(aStartPathLb, bfsPathLb);


        Label distanceLb = new Label("Total distance (km): ");
        distanceLbVal = new Label();
        distanceLbVal.setTextFill(Color.GREEN);
        distanceLbPane.getChildren().addAll(distanceLb, distanceLbVal);

        aStarVbfsTb.setMaxSize(400, 150);
        TableColumn<AlgorithimEntry, String> algorithmCol = new TableColumn<>("Algorithm");
        TableColumn<AlgorithimEntry, Long> timeCol = new TableColumn<>("Time (nano)");
        TableColumn<AlgorithimEntry, String> theoreticalCol = new TableColumn<>("Time Complexity");
        TableColumn<AlgorithimEntry, Integer> distanceCol = new TableColumn<>("Distance");
        aStarVbfsTb.getColumns().addAll(algorithmCol, timeCol, theoreticalCol, distanceCol);

        algorithmCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("actualTime"));
        theoreticalCol.setCellValueFactory(new PropertyValueFactory<>("timeComp"));
        distanceCol.setCellValueFactory(new PropertyValueFactory<>("distance"));

        aStarEntry = new AlgorithimEntry("A*", "O(b^d)", 0,0);
        bfsEntry = new AlgorithimEntry("BFS", "O(b^d)", 0,0);

        aStarVbfsTb.getItems().add(aStarEntry);
        aStarVbfsTb.getItems().add(bfsEntry);

//        aStarVbfsTb.getItems().add("BFS", bfsTime, "O(b^d)", "O(b^d)");

        tablePane.getChildren().add(aStarVbfsTb);
        aStarGraphImg = new Image("file: AStar.png");
        double aStartImgHeight = aStarGraphImg.getHeight();
        aStarGraphImgView = new ImageView(aStarGraphImg);
        aStarGraphImgView.setImage(aStarGraphImg);
        aStarGraphImgView.setFitHeight(300);
        aStarGraphImgView.setPreserveRatio(true);

//        StackPane aStarGraphPane = new StackPane();
//        aStarGraphPane.getChildren().add(aStarGraphImgView);
        bfsGraphImg = new Image("file: BFS.png");
        double bfsImgHeight = bfsGraphImg.getHeight();
        bfsGraphImgView = new ImageView(bfsGraphImg);
        bfsGraphImgView.setImage(bfsGraphImg);
        bfsGraphImgView.setFitHeight(300);
        bfsGraphImgView.setPreserveRatio(true);
//
//        StackPane bfsGraphPane = new StackPane();
//        bfsGraphPane.getChildren().add(bfsGraphImgView);


        routePane.getChildren().addAll(bfsGraphImgView, aStarGraphImgView);
        routePane.setSpacing(20);
//        routePane.setMaxHeight(300);

        Button showGraphBt = new Button("Show Graph");

        leftPane.getChildren().addAll(fileBrowsePane, calcButtonPane, routePane, distanceLbPane, showGraphBt, tablePane);
        leftPane.setSpacing(10);
        leftPane.setAlignment(Pos.CENTER);

        InputStream imgStream = null;
        try {
            imgStream = new FileInputStream("Palestine.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Image psImg = new Image(imgStream);
        ImageView psImgView = new ImageView(psImg);

        psImgView.setFitHeight(900);
        psImgView.setPreserveRatio(true);



        mainPane.setSpacing(10);
        mainPane.getChildren().addAll(leftPane, mapPane);

        mapGroup.getChildren().add(psImgView);
//        mapGroup.getChildren().add(testCircle);
//        addCitiesToMap(mapGroup, calcRouteBt);


        mapPane.getChildren().add(mapGroup);


        mapPane.setOnMouseClicked(e -> {
            System.out.println(e.getX() + " " + e.getY());
        });


//        calcRouteBt.setOnAction(e -> {
//            try {
//                Driver.onStart();
//                calcRouteBt.setDisable(true);
//                clearBt.setDisable(false);
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
//        });
//
//        clearBt.setOnAction(e -> {
//            Driver.onClear2();
//            clearBt.setDisable(true);
//            calcRouteBt.setDisable(true);
//            txtArea_result.setText("");
//            txtArea_path.setText("");
//        });
        browseButton.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select Resource Directory");
            // get stage of browse button
            Stage stage = (Stage) browseButton.getScene().getWindow();
            File selectedFile = dirChooser.showDialog(stage);
            if (selectedFile != null) {
                browseField.setText(selectedFile.getAbsolutePath());
                btnStart.setDisable(false);
                String path = selectedFile.getAbsolutePath();
                String citiesFilePath = (path + "/cities.csv");
                String huresticsFilePath = (path + "/AirDistances.csv");
                String roadsFilePath = (path + "/roads.csv");

                readCities(citiesFilePath);
                readHeuristicTable(huresticsFilePath);
                readRoads(roadsFilePath);
                AStarTable = new TableEntry[citiesMap.size() + 1];
                initializeTable();
                handleSelection();
                addPointsToMap();

            }
            else {
                btnStart.setDisable(true);
                browseField.setText("");
            }

        });
        showGraphBt.setOnAction(e -> {
            File file = new File("citiesGraph.png");
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        mainPane.setAlignment(Pos.CENTER);
        double windWidth = (900 / psImg.getHeight()) * psImg.getWidth() + 400; // 450 is the width of the left pane
        return new Scene(mainPane, windWidth, 900);

    }

    private void addPointsToMap(){
        for (Map.Entry<String, City> c : citiesMap.entrySet()) {
            mapGroup.getChildren().add(c.getValue().c);
            mapGroup.getChildren().add(c.getValue().AstarLine);
            mapGroup.getChildren().add(c.getValue().bfsLine);
            mapGroup.getChildren().add(c.getValue().distAstarLb);
            c.getValue().AstarLine.setVisible(false);
            c.getValue().bfsLine.setVisible(false);
        }
    }

    private void OnStart() throws Exception {
        if (AStarTable.length != 0) {
            OnClear();
        }
        try {
            initializeTable();
            String start = cmb_start.getValue();
            String end = cmb_target.getValue();
            Astar search = new Astar(hueristicMap);
            BFS  bfsSearch = new BFS();
            if (start != null && end != null) {
                AStarTable = search.findPath(citiesMap.get(start), citiesMap.get(end), AStarTable);
                aStarTime = search.timeTaken();
                aStarEntry.actualTime = aStarTime;
                aStarDistance= (int)(AStarTable[citiesMap.get(end).cityEntry].distance);
                aStarEntry.distance = aStarDistance;
                distanceLbVal.setText(String.valueOf(aStarDistance));
//                System.out.println("Astar Time: " + aStarTime);
                ArrayList<City> bfsroute= bfsSearch.findPath(citiesMap.get(start),citiesMap.get(end), citiesMap.size());
                bfsTime = bfsSearch.getTime();
                bfsEntry.actualTime = bfsTime;
                bfsDistance = bfsroute.size()-1;
                bfsEntry.distance = bfsDistance;
//                aStarVbfsTb.getItems().clear();
                aStarVbfsTb.getItems().add(aStarEntry);
                aStarVbfsTb.getItems().add(bfsEntry);

//                System.out.println("BFS Time: " + bfsTime);
                makeBFSGraph(bfsroute);

//                Printing A star path
                aStarPathLabels.clear(); // clear the labels from the previous path
                StringBuilder path = new StringBuilder("");
                printPath(citiesMap.get(end), path);
//                -----------------------------------------------------------------------------
//                BFS call and path returning and printing on consule
//                System.out.println(bfsSearch.getTime());
//                for (City c: bfsroute)
//                    System.out.println(c);
//                printBFSPath(bfsroute);
//                System.out.println("BFS EDGE COUNT = "+(bfsroute.size()-1));
//                -----------------------------------------------------------

//                txtArea_path.setText(path.toString());
//                txtArea_result.setText("Distance to go from " + start + " to " + end + "\n="
//                        + AStarTable[citiesMap.get(end).cityEntry].getDistance() + "km\n"+
//                        "Time taken to find using A*=:"+search.timeTaken());
//                String formatedAstartPath = getPathFormated(path.toString());
//                aStartPathLb.setText("A* Path: " + formatedAstartPath);
                makeAstarGraph(path.toString());
                Thread.sleep(1000);
                aStarGraphImg = new Image("file:AStar.png");
                aStarGraphImgView.setImage(aStarGraphImg);
                bfsGraphImg = new Image("file:BFS.png");
                bfsGraphImgView.setImage(bfsGraphImg);
            }else{
                errorMsg("Enter starting point and end point");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void makeBFSGraph(ArrayList<City> bfsroute) {
        bfsPathGraph.clear();
        for (int i = 0 ; i < bfsroute.size() - 1; i++){
            bfsPathGraph.addln("\"" + bfsroute.get(i).getName() + "\"" + " -> "  + "\"" + bfsroute.get(i+1).getName() + "\"" +"\n");
        }
        bfsPathGraph.print();
    }

    private void makeAstarGraph(String rawPath) {
        aStartPathGraph.clear();
        double num = 0;
        String [] cities = rawPath.split(":");
        for (int i = 0 ; i < cities.length - 1; i++){
            num = aStarPathLabels.get(i+1);
            double num_before = aStarPathLabels.get(i);
            aStartPathGraph.addln("\"" + cities[i] + "\"" + " -> "  + "\"" + cities[i+1] + "\"" + "[label=" + "\"" + "  " + String.format("%.1f",(num - num_before)) + "\"]" +"\n");
        }
        aStartPathGraph.print();
    }

    private String getPathFormated(String rawPath){
        StringBuilder formatedPath = new StringBuilder("");
        String formatedPathArr [] = rawPath.split(":");
        for (int i =0; i < formatedPathArr.length; i++){
            if (i == formatedPathArr.length -1 ){
                formatedPath.append(formatedPathArr[i]);
                break;
            }
            formatedPath.append(formatedPathArr[i]).append(" -> ");
        }
        return formatedPath.toString();
    }

    private void OnClear() {
        txtArea_path.clear();
        txtArea_result.clear();
        aStarVbfsTb.getItems().clear();
        distanceLbVal.setText("");
        aStarGraphImgView.setImage(null);
        bfsGraphImgView.setImage(null);
//        aStartPathLb.setText("A* Path: ");
//        bfsPathLb.setText("BFS Path: ");
        removeDistAstarLb();
        for (int i = 0; i < AStarTable.length; i++) {
            AStarTable[i].known = false;
            if (AStarTable[i].path != null) {
                AStarTable[i].path.AstarLine.setStroke(Color.BLACK);
//                AStarTable[i].path.distAstarLb.setVisible(false);
//                AStarTable[i].path.distAstarLb.setText("");
            }
            AStarTable[i].path = null;
            AStarTable[i].distance = Double.MAX_VALUE;
            initializeTable();
        }
        for(Map.Entry<String, City> c:citiesMap.entrySet()){
            c.getValue().c.setFill(Color.RED);
            c.getValue().bfsLine.setVisible(false);
            c.getValue().AstarLine.setVisible(false);
            c.getValue().AstarLine.setStroke(Color.BLACK);
        }

    }

    private void printPath(City start, StringBuilder s) {//Note to bebo start her is end
        if (AStarTable[start.cityEntry].path != null) {
            AStarTable[start.cityEntry].path.AstarLine.setEndX(start.c.getTranslateX());
            AStarTable[start.cityEntry].path.AstarLine.setEndY(start.c.getTranslateY());
            AStarTable[start.cityEntry].path.AstarLine.setVisible(true);
            AStarTable[start.cityEntry].path.c.setFill(Color.BLUE);
            printPath(AStarTable[start.cityEntry].path, s);
            double label_pointX = (start.c.getTranslateX() + AStarTable[start.cityEntry].path.c.getTranslateX()) / 2;
            double label_pointY = (start.c.getTranslateY() + AStarTable[start.cityEntry].path.c.getTranslateY()) / 2;
//            System.out.println(label_pointX + " " + label_pointY);
            AStarTable[start.cityEntry].path.distAstarLb.setTranslateX(label_pointX);
            AStarTable[start.cityEntry].path.distAstarLb.setTranslateY(label_pointY);
            AStarTable[start.cityEntry].path.distAstarLb.setVisible(true);
            AStarTable[start.cityEntry].path.distAstarLb.setText(AStarTable[start.cityEntry].getDistance() + " km");
        }
        s.append(start.getName() + ":");
        aStarPathLabels.add(AStarTable[start.cityEntry].getDistance());
    }

    private void removeDistAstarLb(){
        for (Map.Entry<String, City> c : citiesMap.entrySet()) {
            c.getValue().distAstarLb.setVisible(false);
        }
    }
    private void printBFSPath(ArrayList<City>route){
        for (int i =0;i<route.size()-1;i++){
            if(route.get(i+1).c.getTranslateX() == route.get(i).AstarLine.getEndX() && route.get(i+1).c.getTranslateY() == route.get(i).AstarLine.getEndY()){
            // then it's a common path
                route.get(i).AstarLine.setStroke(Color.GREEN);
//                    route.get(i).AstarLine.setStrokeWidth(4);
            }else{
                route.get(i).bfsLine.setEndX(route.get(i+1).c.getTranslateX());
                route.get(i).bfsLine.setEndY(route.get(i+1).c.getTranslateY());
                route.get(i).bfsLine.setVisible(true);
            }
        }
    }
    private void initializeTable() {
        for (int i = 0; i < AStarTable.length; i++) {
            AStarTable[i] = new TableEntry();
            AStarTable[i].known = false;
            AStarTable[i].path = null;
            AStarTable[i].distance = Double.MAX_VALUE;
        }
    }

    private static void readHeuristicTable(String heuristicsFilename) {
        File hFile = new File(heuristicsFilename);
        if (!hFile.exists()) {
            errorMsg("Heuristics file not found!");
        }
        try (Scanner input = new Scanner(hFile)) {
            while (input.hasNext()) {
                String data = input.nextLine();
                String[] tok = data.split(",");//0: first city, 1 second city, 2 is air distance
                Pair<String, String> pair = new Pair<>(tok[0], tok[1]);
                hueristicMap.put(pair, Double.parseDouble(tok[2]));
            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            errorMsg("Error Reading Heuristics File!");
        }
    }

    private static void readCities(String cities) {
        File stdFile = new File(cities);
        if (!stdFile.exists()) {
            errorMsg("Cities file not found!");
        }
        try (Scanner input = new Scanner(stdFile)) {
            String numOfData = input.nextLine();
//            String[] str = numOfData.split(" ");
            int citiesNo = 0;
            try {
                citiesNo = Integer.parseInt(numOfData);
            }
            catch (NumberFormatException e){
                errorMsg("Please enter a valid number of cities!");
            }
//            int edges = Integer.parseInt(str[1]);
            int citiesRead = 0;
//            int edgesRead =0;
            while (input.hasNext()) {
                if (citiesRead < citiesNo) {
                    String countryData = input.nextLine().strip();
                    if (countryData.isEmpty()) { // skip empty lines
                        continue;
                    }
                    String[] tok = countryData.split(",");
                    City city = new City(tok[0].strip(), Double.parseDouble(tok[1].strip()),
                            Double.parseDouble(tok[2].strip()));
                    citiesMap.put(city.getName(), city);
                    citiesRead++;
                } /*else {
                    String edgesData = input.nextLine();
                    String[] tok = edgesData.split(" ");
                    System.out.println(tok[0]+"-->"+tok[1]);
                    citiesMap.get((tok[0])).adjacent.add(new Adjacent(citiesMap.get(tok[1]), Float.parseFloat(tok[2])));
                    citiesMap.get(tok[1]).adjacent.add(new Adjacent(citiesMap.get(tok[0]), Float.parseFloat(tok[2])));
                    gp.addln("\"" + tok[0]+ "\"" + "--" + "\"" +tok[1]+ "\"");
                    //gp.addln("\"" + tok[1]+ "\"" + "->" + "\"" +tok[0]+ "\"");
                }*/
            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            errorMsg("Error Reading Cities File!");
        }
    }


    private static void readRoads(String roadsFilename){
        File stdFile = new File(roadsFilename);
        if (!stdFile.exists()) {
            errorMsg("Roads file not found!");
        }
        try (Scanner input = new Scanner(stdFile)) {
//            int edgesRead =0;
            while (input.hasNext()) {
                String edgesData = input.nextLine().strip();
                if (edgesData.isEmpty()) continue;
                String[] tok = edgesData.split(",");
                System.out.println(tok[0]+"-->"+tok[1]);
                citiesMap.get((tok[0])).adjacent.add(new Adjacent(citiesMap.get(tok[1]), Float.parseFloat(tok[2])));
                citiesMap.get(tok[1]).adjacent.add(new Adjacent(citiesMap.get(tok[0]), Float.parseFloat(tok[2])));
                // drawing the graph
                gp.addln("\"" + tok[0]+ "\"" + "--" + "\"" +tok[1]+ "\""); // Quotes are necessary for city names with illegal characters like "-"

            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            errorMsg("Error Reading Roads File!");
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
            errorMsg(e.toString());
        }
    }

    public static void main(String[] args) {
        try {
//            readData("Cities.csv");
//            readHuersticTable("AirDistances.csv");
//            setLattitudeAndLongtiude("CitiesLongLat.txt");
//            gp.print();
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
//			warning_Message(e.toString());
        }
    }

    public static void errorMsg(String x) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(x);
        alert.show();
    }
}
