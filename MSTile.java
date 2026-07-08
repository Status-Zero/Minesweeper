import javafx.application.Application;
import javafx.stage.Stage; 
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.transform.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;
import java.io.*;
import java.lang.*;

public class MSTile {
    Rectangle tile = new Rectangle();
    Text text = new Text("");
    
    boolean hasMine = false;
    boolean isRevealed = false;
    boolean isFlagged = false;
    
    public MSTile(int x, int y, int sideLength) {
        tile.setWidth(sideLength);
        tile.setHeight(sideLength);
        tile.setLayoutX(x);
        tile.setLayoutY(y);
        tile.setStroke(Color.BLACK);
        tile.setStrokeWidth(2);
        tile.setFill(Color.GRAY);
        
        text.setX(x + sideLength * 0.3);
        text.setY(y + sideLength * 0.7);
        text.setFont(Font.font(sideLength * 0.6));
        text.setVisible(false);
    }
    
    public void reset() {
        hasMine = false;
        isRevealed = false;
        isFlagged = false;
        tile.setFill(Color.GRAY);
        text.setVisible(false);
    }
    
    public void initialize(int surround) {
        if (!hasMine) {
            switch (surround) {
                case 1: text.setText("1"); text.setFill(Color.BLUE); break;
                case 2: text.setText("2"); text.setFill(Color.GREEN); break;
                case 3: text.setText("3"); text.setFill(Color.RED); break;
                case 4: text.setText("4"); text.setFill(Color.DARKBLUE); break;
                case 5: text.setText("5"); text.setFill(Color.ORANGE); break;
                case 6: text.setText("6"); text.setFill(Color.CYAN); break;
                case 7: text.setText("7"); text.setFill(Color.BLACK); break;
                case 8: text.setText("8"); text.setFill(Color.GRAY); break;
                default: text.setText("0"); text.setFill(Color.GRAY);
            }
        } else {
            text.setText("X");
            text.setFill(Color.DARKGRAY);
        }
    }
    
    public Rectangle getTile() {
        return tile;
    }
    public Text getText() {
        return text;
    }
    
    public void giveMine() {
        hasMine = true;
    }
    public boolean hasMine() {
        return hasMine;
    }
    
    public boolean isRevealed() {
        return isRevealed;
    }
    public void reveal() {
        if (!isFlagged && !isRevealed) {
            isRevealed = true;
            tile.setFill(Color.LIGHTGRAY);
            text.setVisible(true);
        }
    }
    
    public boolean isFlagged() {
        return isFlagged;
    }
    public void setFlagged() {
        if (!isRevealed) {
            isFlagged = true;
            tile.setFill(Color.RED);
        }
    }
    public void setUnflagged() {
        if (!isRevealed && isFlagged) {
            isFlagged = false;
            tile.setFill(Color.GRAY);
        }
    }
    
    public void disableClick() {
        tile.setOnMousePressed(null);
        tile.setOnMouseReleased(null);
    }
}
