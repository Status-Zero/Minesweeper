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
import java.util.concurrent.*;
import java.io.*;
import java.lang.*;

public class MineSweeper extends Application {
    private MSTile[][] tiles;
    private boolean firstClick = true;
    @Override
    public void start(Stage stage) {
        MSStartScreen start = new MSStartScreen();
        
        Scene scene = new Scene(start.getRoot());
        stage.setScene(scene);
        stage.setTitle("Mine Sweeper");
        stage.setResizable(false);
        stage.show();
        
        Pane root = new Pane();
        
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && !firstClick) {
                reset(scene, start, stage);
            }
        });
        
        start.getButton().setOnMouseClicked(e -> {
            int paneDimensions = 0;
            switch (start.getDifficulties().getValue()) {
                case "Easy": paneDimensions = 10; break;
                case "Medium": paneDimensions = 15; break;
                case "Hard": paneDimensions = 20; break;
                case "Expert": paneDimensions = 28; break;
            }
            root.setPrefSize(700, 700);
            
            tiles = new MSTile[paneDimensions][paneDimensions];
            for (int x = 0; x < tiles.length; x++) {
                for (int y = 0; y < tiles[x].length; y++) {
                    final int fx = x;
                    final int fy = y;
                    
                    int sideLength = 700 / paneDimensions;
                    tiles[x][y] = new MSTile(x * sideLength, y * sideLength, sideLength);
                    
                    tiles[x][y].getTile().setOnMouseReleased(ex -> {
                        boolean shift = ex.isShiftDown();
                        if (shift) {
                            if (!tiles[fx][fy].isFlagged()) {
                            tiles[fx][fy].setFlagged();
                            } else {
                            tiles[fx][fy].setUnflagged();
                            }
                            return;
                        }
                        if (!tiles[fx][fy].isFlagged()) {
                            if (firstClick) {
                                List<MSTile> revealTiles = new ArrayList<>();
                                int[][] offsets = {
                                    {0, 0},
                                    {-1, 0},
                                    {1, 0},
                                    {0, -1},
                                    {0, 1}
                                };
                                for (int[] o : offsets) {
                                    int nx = fx + o[0];
                                    int ny = fy + o[1];
                                    if (nx < 0 || ny < 0 || nx >= tiles.length || ny >= tiles[0].length) {
                                        continue;
                                    }
                                    revealTiles.add(tiles[nx][ny]);
                                }
                                for (MSTile t : revealTiles) {
                                    t.reveal();
                                    t.disableClick();
                                }
                                startGame();
                                firstClick = false;
                                if (tiles[fx][fy].getText().getText().equals("0")) {
                                    floodFill(fx, fy);
                                }
                            } else if (!tiles[fx][fy].hasMine()) {
                                MSTile t = tiles[fx][fy];
                                if (t.getText().getText().equals("0")) {
                                    floodFill(fx, fy);
                                } else {
                                    t.reveal();
                                    t.disableClick();
                                }
                            } else {
                                reset(scene, start, stage);
                            }
                        }
                    });
                    root.getChildren().addAll(tiles[x][y].getTile(), tiles[x][y].getText());
                }
            }
            scene.setRoot(root);
            stage.sizeToScene();
        });
    }
    private void startGame() {
        int bombs = (int)(tiles.length * tiles[0].length * 0.17);
        List<MSTile> availableTiles = new ArrayList<>();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (!tiles[x][y].isRevealed()) {
                    availableTiles.add(tiles[x][y]);
                }
            }
        }
        for (int x = 0; x < bombs; x++) {
            int index = ThreadLocalRandom.current().nextInt(0, availableTiles.size());
            availableTiles.get(index).giveMine();
            availableTiles.get(index).initialize(0);
            availableTiles.remove(index);
        }
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                int bombCounter = 0;
                MSTile t = tiles[x][y];
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) {
                            continue;
                        }
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx < 0 || ny < 0 || nx >= tiles.length || ny >= tiles[0].length) {
                            continue;
                        }
                        if (tiles[nx][ny].hasMine()) {
                            bombCounter++;
                        }
                    }
                }
                t.initialize(bombCounter);
            }
        }  
    }
    private void floodFill(int x, int y) {
        floodFillInternal(x, y, true);
    }
    private void floodFillInternal(int x, int y, boolean isRoot) {
        if (x < 0 || y < 0 || x >= tiles.length || y >= tiles[0].length) {
            return;
        }
        MSTile t = tiles[x][y];
        if (!isRoot && t.isRevealed()) {
            return;
        }
        if (t.isFlagged() || t.hasMine()) {
            return;
        }
        t.reveal();
        t.disableClick();
        if (!t.getText().getText().equals("0")) {
            return;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                floodFillInternal(x + dx, y + dy, false);
            }
        }
    }
    private void reset(Scene scene, MSStartScreen start, Stage stage) {
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                tiles[x][y].reset();
            }
        }
        firstClick = true;
        scene.setRoot(start.getRoot());
        stage.sizeToScene();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
