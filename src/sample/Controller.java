package sample;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.text.TabSet;

public class Controller implements Initializable {
    @FXML
    private MenuItem saveButton;
    @FXML
    private MenuItem undoButton;
    @FXML
    private ColorPicker fillPicker;
    @FXML
    private CheckBox transparentCheck;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ToggleButton tglBtnPoly;
    @FXML
    private ToggleButton tglBtnRect;
    @FXML
    private ToggleButton tglBtnCircle;
    @FXML
    private Label labelCords;
    @FXML
    private TextField textCmd;
    @FXML
    private Button btnDeleteAll;
    @FXML
    private ToggleButton tglBtnLine;
    @FXML
    private Pane pane;

    private File chooseFile;
    private Stage fileStage;
    private FileChooser fileChooser;


    private char currentDraw = ' ';
    private char shapeOfObject = ' ';

    private boolean isDrawing = false;
    private boolean firstClick = true;
    private boolean selected;

    private double startX, startY, endX, endY, definiteStartX, definiteStartY;

    private List<Shape> shapes = new ArrayList<>();
    private List<ToggleButton> tglGroup = new ArrayList<>();


    @FXML
    void handleBtnDeleteAll(ActionEvent e) {
        shapes.clear();
        pane.getChildren().clear();
    }
    @FXML
    void handleButtonUndo(ActionEvent e) {
        undo();
    }
    @FXML
    void handleButtonSave(ActionEvent e) throws Exception{
        System.out.println("not implemented");
    }

    private void undo(){
        if(shapes.size() != 0) {
            shapes.remove(shapes.size() - 1);
            pane.getChildren().remove(pane.getChildren().size() - 1);
        }
    }

    @FXML
    void handlePaneMouseClick(MouseEvent e) {
        //Determine if drawing has just started by First click
        startX = e.getX();
        startY = e.getY();
        if (firstClick) {
            isDrawing = true;
            firstClick = false;
            definiteStartX = startX;
            definiteStartY = startY;
        }
        //logic for NOT first clicks
        else {
            //if drawing lines dedicated for making shapes double-click will finish the drawing
            if (e.getButton() == MouseButton.PRIMARY && isDrawing && e.getClickCount() == 2) {
                drawShape(definiteStartX, definiteStartY, e.getX(), e.getY(), currentDraw);
                endDraw();
            }
            //if drawing a square/rectangle, circle, polygon mouse click will finish the drawing
            else if(e.getButton() == MouseButton.PRIMARY && isDrawing && currentDraw != 'L') {
                drawShape(definiteStartX, definiteStartY, e.getX(), e.getY(), currentDraw);
                endDraw();
            }

        }
        if (currentDraw == 'L') {
            shapes.add(new Line());
        } else if (currentDraw == 'C') {
            shapes.add(new Circle());
        } else if(currentDraw == 'R'){
            shapes.add(new Rectangle());
        } else if(currentDraw == 'P'){
            shapes.add(new Polygon());
        }
    }

    private void endDraw(){
        isDrawing = false;
        definiteStartX = 0;
        definiteStartY = 0;
        firstClick = true;
    }

    private void drawShape(double startX, double startY, double endX, double endY, char type) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        Color fillColor;
        if(transparentCheck.isSelected()) fillColor = null;
        else fillColor = fillPicker.getValue();
        //Handle Line/Path drawing
        if (type == 'L') {
            ((Line) shapes.get(shapes.size() - 1)).setStartX(startX);
            ((Line) shapes.get(shapes.size() - 1)).setStartY(startY);
            ((Line) shapes.get(shapes.size() - 1)).setEndX(endX);
            ((Line) shapes.get(shapes.size() - 1)).setEndY(endY);
            ((Line) shapes.get(shapes.size() - 1)).setStroke(colorPicker.getValue());
        }
        //Handling Circle drawing
        else if (type == 'C') {
            ((Circle) shapes.get(shapes.size() - 1)).setCenterX(startX);
            ((Circle) shapes.get(shapes.size() - 1)).setCenterY(startY);
            ((Circle) shapes.get(shapes.size() - 1)).setRadius(Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)));
            ((Circle) shapes.get(shapes.size() - 1)).setStroke(colorPicker.getValue());

            ((Circle) shapes.get(shapes.size() - 1)).setFill(fillColor);
        }
        //Handling Rectangle drawing
        else if(type == 'R'){
            //Calculation of the distance: Distance = sqrt((endCord - startCord)^2)
            ((Rectangle) shapes.get(shapes.size() - 1)).setX(startX);
            ((Rectangle) shapes.get(shapes.size() - 1)).setY(startY);
            //Handling negative and positive coords
            if(endX - startX < 0 && endY - startY < 0){
                ((Rectangle) shapes.get(shapes.size() - 1)).setTranslateX(-width);
                ((Rectangle) shapes.get(shapes.size() - 1)).setTranslateY(-height);
            }
            if(endX - startX < 0 ){
                ((Rectangle) shapes.get(shapes.size() - 1)).setTranslateX(-width);
            }
            else if(endY - startY < 0){
                ((Rectangle) shapes.get(shapes.size() - 1)).setTranslateY(-height);
            }
            else {
                ((Rectangle) shapes.get(shapes.size() - 1)).setTranslateX(0);
                ((Rectangle) shapes.get(shapes.size() - 1)).setTranslateY(0);
            }
            ((Rectangle) shapes.get(shapes.size() - 1)).setWidth(width);
            ((Rectangle) shapes.get(shapes.size() - 1)).setHeight(height);
            ((Rectangle) shapes.get(shapes.size() - 1)).setStroke(colorPicker.getValue());
            ((Rectangle) shapes.get(shapes.size() - 1)).setFill(fillColor);
        }
        else if(type == 'P'){
            ((Polygon)shapes.get(shapes.size() - 1)).getPoints().clear();
            if(endX - startX < 0){
                width = -width;
            }
            Double[] polyCords = {
                    startX, startY,
                    endX, startY,
                    width/2 + startX, endY
            };
            ((Polygon)shapes.get(shapes.size() - 1)).getPoints().addAll(polyCords);
            ((Polygon) shapes.get(shapes.size() - 1)).setStroke(colorPicker.getValue());
            ((Polygon) shapes.get(shapes.size() - 1)).setFill(fillColor);
        }
        //loop trough each object in ArrayList, draw on screen if not drawn already
        shapes.forEach(x -> { if (!pane.getChildren().contains(x)) pane.getChildren().add(x); });
    }

    //displaying coords of mouse
    //realtime drawing what the object would look like
    @FXML
    void handlePaneMouseMove(MouseEvent e) {
        if (isDrawing) {
            endX = e.getX();
            endY = e.getY();
            drawShape(startX, startY, endX, endY, currentDraw);
        }
        labelCords.setText("X: " + (int) e.getX() + " Y: " + (int) e.getY());

    }
    //Toggle Line draw
    @FXML
    void handleTglBtnLine(ActionEvent e) {
        if (tglBtnLine.isSelected()) {
            currentDraw = 'L';
            firstClick = true;
            tglSet(tglBtnLine);
        } else {
            currentDraw = ' ';
            firstClick = false;
        }
    }
    //Toggle Circle draw
    @FXML
    void handleTglBtnCircle(ActionEvent e) {
        if (tglBtnCircle.isSelected()) {
            currentDraw = 'C';
            firstClick = true;
            tglSet(tglBtnCircle);
        } else {
            currentDraw = ' ';
            firstClick = false;
        }
    }
    //Toggle Rectangle draw
    @FXML
    void handleTglBtnRect(ActionEvent e) {
        if (tglBtnRect.isSelected()) {
            currentDraw = 'R';
            firstClick = true;
            tglSet(tglBtnRect);
        } else {
            currentDraw = ' ';
            firstClick = false;
        }
    }
    //Toggle triangle draw
    @FXML
    void handleTglBtnPoly(ActionEvent e){
        if (tglBtnPoly.isSelected()) {
            currentDraw = 'P';
            firstClick = true;
            tglSet(tglBtnPoly);
        } else {
            currentDraw = ' ';
            firstClick = false;
        }
    }

    @FXML
    void handleTxtCmdKeyPressed(KeyEvent e) {
    }
    @FXML
    void handleAppKeyPress(KeyEvent e) {
        if(e.getCode() == KeyCode.ESCAPE){
            if(isDrawing){
                if(currentDraw == 'L') {
                    drawShape(startX, startY, startX, startY, currentDraw);
                    undo();
                }
                else drawShape(startX, startY, endX, endY, currentDraw);
                endDraw();
            }
        }
        if(e.getCode() == KeyCode.Z && e.isControlDown()){
            undo();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tglGroup.add(tglBtnLine);
        tglGroup.add(tglBtnCircle);
        tglGroup.add(tglBtnRect);
        tglGroup.add(tglBtnPoly);
        colorPicker.setValue(Color.BLACK);
    }
    private void tglSet(ToggleButton tgl){
        for(ToggleButton button : tglGroup){
            if(button != tgl){
                button.setSelected(false);
            }
        }
    }
}

