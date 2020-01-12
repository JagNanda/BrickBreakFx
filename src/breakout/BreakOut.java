/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breakout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Jag
 */
public class BreakOut extends Application
{
    // for Y, 0 = top, HEIGHT = bottom
    private final int HEIGHT = 600;
    private final int WIDTH = 800;    
    private Rectangle platform = new Rectangle(100, 20, Color.RED);
    private Rectangle ball = new Rectangle(10,10);
    private Timeline timeLine = new Timeline();
    private int directionY = 10;
    private int directionX = 10;
    private boolean launchBall = false;
    private boolean movePlatformLeft = false;
    private boolean movePlatformRight = false;
    private int lives = 3;
    Label lblLives = new Label();
    private int score = 0;
    Label lblScore = new Label();
    private static int level = 1; 
            
    public Pane generateGame()
    {
       
        Pane pane = new Pane();
        //creating platform and setting its position
        pane.setPrefSize(WIDTH, HEIGHT);
        platform.setX((WIDTH - platform.getWidth())/2);
        platform.setY(HEIGHT - platform.getHeight());
        
        //creating blocks of rectangles and setting position of each block
        Rectangle[][] rectArr = new Rectangle[5][5];
        for(int i = 0; i < rectArr.length; i++)
        {
            for(int j = 0; j < rectArr[i].length; j++)
            {                
                rectArr[i][j] = new Rectangle(100,20);
                //set color of brick by row
                switch(i)
                {
                    case 0:
                        rectArr[i][j].setFill(Color.RED);
                        break;
                    case 1: 
                        rectArr[i][j].setFill(Color.ORANGE);
                        break;
                    case 2:
                        rectArr[i][j].setFill(Color.YELLOW);
                        break;
                    case 3:
                        rectArr[i][j].setFill(Color.GREEN);
                        break;    
                    case 4:
                        rectArr[i][j].setFill(Color.BLUE);
                        break;
                }
                rectArr[i][j].setX(j * (100 + 20) + 110);
                rectArr[i][j].setY(i * (20 + 20) + 10);
                rectArr[i][j].setStroke(Color.BLACK);
                rectArr[i][j].setStrokeWidth(2);
                pane.getChildren().add(rectArr[i][j]);
            }
        }
        //Animating platform and ball movement        
        KeyFrame animation = new KeyFrame(Duration.millis(30), e ->
        {
            lblScore.setText("" + score);
            lblLives.setText("" + lives);
            //move ball right
            if(movePlatformRight == true && platform.getX() < WIDTH - platform.getWidth())
            {
                platform.setX(platform.getX() + 20);
                movePlatformRight = false;
            }
            //move ball left
            if(movePlatformLeft == true && platform.getX() > 0)
            {
                platform.setX(platform.getX() - 20);
                movePlatformLeft = false;                
            }
            //if spacebar is pressed launchball
            if(launchBall == true)
            {
                
                if(ball.getX() >= platform.getX() && ball.getX() + ball.getWidth() <= platform.getX() + platform.getWidth() 
                        && ball.getY() - ball.getHeight() == platform.getY() - platform.getHeight())
                { 
                    directionY *= -1;
                }
                //bounce off right wall
                if(ball.getX() >= WIDTH - ball.getWidth())
                {
                    directionX *= -1;
                }
                //bounce off top wall
                if(ball.getY() <= 0)
                {
                    directionY *= -1;
                }
                //bounce off left wall
                if(ball.getX() <= 0)
                {
                    directionX *= -1;
                }   
                //if platform misses ball
                if(ball.getY() >= HEIGHT)
                {
                    launchBall = false;
                    lives -= 1;
                }
                //move the ball. Put after collision check so it bounces off platform when game starts
                ball.setY(ball.getY() + directionY);
                ball.setX(ball.getX() + directionX);
            }
            //This is to move the ball with the platform before its launched
            else
            {
                ball.setX(platform.getX() + platform.getWidth()/2 - ball.getWidth()/2);
                ball.setY(platform.getY() - platform.getHeight() + ball.getHeight());
            }
            //checking too see if bricks are hit by ball
            for(int i = 0; i < rectArr.length; i++)
            {
                for(int j = 0; j < rectArr[i].length; j++)
                {   
                    //if bricks are not visible, no collision detection
                    if(rectArr[i][j].isVisible())
                    {   
                        //if bottom of ball hits top of a block
                        if(ball.getX() >= rectArr[i][j].getX() && ball.getX() + ball.getWidth() <= rectArr[i][j].getX() + rectArr[i][j].getWidth() 
                            && ball.getY() - ball.getHeight() == rectArr[i][j].getY() - rectArr[i][j].getHeight())
                        { 
                            directionY *= -1;
                            rectArr[i][j].setVisible(false);
                            score += 100;
                        }
                        //if top of ball hits bottom of a block
                        if(ball.getX() >= rectArr[i][j].getX() && ball.getX() + ball.getWidth() <= rectArr[i][j].getX() + rectArr[i][j].getWidth()
                                && ball.getY() == rectArr[i][j].getY() + rectArr[i][j].getHeight())
                        {
                            directionY *= -1;
                            rectArr[i][j].setVisible(false);
                            score += 100;
                        }

                        //bounce off right of brick
                        if(ball.getBoundsInLocal().intersects(
                                rectArr[i][j].getX() + 100, rectArr[i][j].getY() - 20, 1, 21))
                        {
                            directionX *= -1;
                            rectArr[i][j].setVisible(false);
                            score += 100;
                        }
                        //bounce off left of brick
                        if(ball.getBoundsInLocal().intersects(
                                rectArr[i][j].getX(), rectArr[i][j].getY(), 1, 21))
                        {
                            directionX *= -1;
                            rectArr[i][j].setVisible(false);
                            score += 100;
                        }
                    }
                }
            }
            if(score == 2500)
            {
                lblScore.setText("" + score);
                timeLine.stop();
                levelWon(pane);
            }
            if(lives == 0)
            {
                lblLives.setText("" + lives);
                timeLine.stop();
                gameOver(pane);
            }
        });
        pane.getChildren().addAll(platform,ball);
        timeLine.getKeyFrames().add(animation);
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
        return pane;
        
    }
    
    public Pane generateRightPane()
    {
        Pane right = new Pane();
        
        Label lblScoreText = new Label("Score");
        lblScoreText.setPrefWidth(150);
        lblScoreText.setStyle("-fx-text-fill: white; -fx-font-size: 20pt;");
        lblScoreText.setTranslateY(50);
        lblScoreText.setAlignment(Pos.CENTER);        
        lblScore.setPrefWidth(150);
        lblScore.setAlignment(Pos.CENTER);
        lblScore.setTranslateY(90);
        lblScore.setStyle("-fx-text-fill: white; -fx-font-size: 20pt;");        
        
        Label lblLevelText = new Label("Level");
        lblLevelText.setPrefWidth(150);
        lblLevelText.setStyle("-fx-text-fill: white; -fx-font-size: 20pt;");
        lblLevelText.setTranslateY(200);
        lblLevelText.setAlignment(Pos.CENTER);
        Label lblLevel = new Label("" + level);
        lblLevel.setPrefWidth(150);
        lblLevel.setStyle("-fx-text-fill: white; -fx-font-size: 20pt;");
        lblLevel.setTranslateY(240);
        lblLevel.setAlignment(Pos.CENTER);
        
        Label lblLivesText = new Label("Lives");
        lblLivesText.setPrefWidth(150);
        lblLivesText.setStyle("-fx-text-fill: white; -fx-font-size: 20pt;");
        lblLivesText.setTranslateY(350);
        lblLivesText.setAlignment(Pos.CENTER);
        lblLives.setPrefWidth(150);
        lblLives.setAlignment(Pos.CENTER);
        lblLives.setTranslateY(390);
        lblLives.setStyle("-fx-text-fill: white; -fx-font-size: 20pt;");     
        
        right.setPrefWidth(150);
        right.setStyle("-fx-background-color: black;");
        right.getChildren().addAll(lblScoreText, lblScore, lblLevelText, lblLevel, lblLivesText, lblLives);
        return right;
        
    }
    
    @Override
    public void start(Stage primaryStage)
    {    
        
        BorderPane root = new BorderPane();
        //center the game in BorderPane
        Pane center = generateGame();
        root.setCenter(center);
        //right pane       
        Pane right = generateRightPane();
        root.setRight(right);
        
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(e ->
        {
            KeyCode keyPressed = e.getCode();
            if(keyPressed.equals(KeyCode.RIGHT))
            {
                movePlatformRight = true;
                movePlatformLeft = false;
            }
            else if(keyPressed.equals(KeyCode.LEFT))
            {
                movePlatformLeft = true;
                movePlatformRight = false;                      
            }
            if(keyPressed.equals(KeyCode.SPACE))
            {
                launchBall = true;
            }
        });
        
        primaryStage.setTitle("Break Out");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
     
    public void levelWon(Pane p)
    {
        Label lblWin = new Label("Next Level");
        lblWin.setStyle("-fx-font-weight: bold; -fx-font-size: 25pt; -fx-text-fill: white; -fx-background-color: black;");
        lblWin.setTranslateY(HEIGHT/2);
        lblWin.setMinWidth(WIDTH + 50);
        lblWin.setAlignment(Pos.CENTER);
        p.getChildren().add(lblWin);
    }
    
    public void gameOver(Pane p)
    {
        Label lblGameOver = new Label("Game Over");
        lblGameOver.setStyle("-fx-font-weight: bold; -fx-font-size: 25pt; -fx-text-fill: white; -fx-background-color: black;");
        lblGameOver.setTranslateY(HEIGHT/2);
        lblGameOver.setMinWidth(WIDTH + 50);
        lblGameOver.setAlignment(Pos.CENTER);
        p.getChildren().add(lblGameOver);
        int highScore = 0;
        try
        {
            File file = new File("Highscore.txt");
            if(file.exists())
            {
               BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
               String s = br.readLine();
               String[] parts = s.split(": ");
               highScore = Integer.parseInt(parts[1]);
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        if(score > highScore)
        {
            try
            {    
                File file = new File("Highscore.txt");
                if(!file.exists())
                {
                    file.createNewFile();
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                bw.write("Score: " + score);
                bw.newLine();
                bw.write("Level: " + level);
                bw.close();
            }
            catch(IOException e)
            {
                System.out.println(e);
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
