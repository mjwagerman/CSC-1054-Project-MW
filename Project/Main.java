import java.net.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.scene.image.*;

import java.io.*;

import java.util.*;
import java.text.*;
import java.io.*;
import java.lang.*;
import javafx.application.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.net.*;
import javafx.geometry.*;



public class Main extends Application
{
   private void saveHighScore(double newHighScore) {
        String fileName = "highscore.txt";
        File file = new File(fileName);
        try {
            if (file.exists()) {
               file.delete();
            }
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(String.valueOf(newHighScore));
            fileWriter.close();
            System.out.println("saved");
        } catch (IOException e) {
            System.err.println("unable to save, error: " + e.getMessage());
        }
    }
    private void loadHighScore() {
      String fileName = "highscore.txt";
      File file = new File(fileName);
      if (file.exists()) {
         try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextDouble()) {
                highScore = scanner.nextDouble();
            }
         } 
         catch (FileNotFoundException e) {
            System.err.println("file not found: " + e.getMessage());
         }
      }
   }
    
   FlowPane fp;
   
   Canvas theCanvas = new Canvas(600,600);
   
   PlayerObject thePlayer = new PlayerObject(300, 300);//create player
   
   ArrayList<MineObject> mineList = new ArrayList<MineObject>();
   
   GraphicsContext gc;
   public void start(Stage stage)
   {
      loadHighScore();
      fp = new FlowPane();
      fp.getChildren().add(theCanvas);
      gc = theCanvas.getGraphicsContext2D();
      drawBackground(300,300,gc);
            
      fp.setOnKeyPressed(new KeyListenerDown());
      fp.setOnKeyReleased(new KeyListenerUp());
      
      AnimationHandler ta = new AnimationHandler();
      ta.start();
 
      Scene scene = new Scene(fp, 600, 600);
      stage.setScene(scene);
      stage.setTitle("Project :)");
      stage.show();
           
      fp.requestFocus();
   }
   
   
   Image background = new Image("stars.png");
   Image overlay = new Image("starsoverlay.png");
   Random backgroundRand = new Random();
      //this piece of code doesn't need to be modified
   public void drawBackground(float playerx, float playery, GraphicsContext gc)
   {
	  //re-scale player position to make the background move slower. 
      playerx*=.1;
      playery*=.1;
   
	//figuring out the tile's position.
      float x = (playerx) / 400;
      float y = (playery) / 400;
      
      int xi = (int) x;
      int yi = (int) y;
      
	  //draw a certain amount of the tiled images
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(background,-playerx+i*400,-playery+j*400);
         }
      }
      
	  //below repeats with an overlay image
      playerx*=2f;
      playery*=2f;
   
      x = (playerx) / 400;
      y = (playery) / 400;
      
      xi = (int) x;
      yi = (int) y;
      
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(overlay,-playerx+i*400,-playery+j*400);
         }
      }
   }
   
   boolean left, right, down, up;
   
   public class KeyListenerUp implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.A) 
         {
            left = false;
         }
         if (event.getCode() == KeyCode.W)  
         {
            up = false;
         }
         if (event.getCode() == KeyCode.S)  
         {
            down = false;
         }
         if (event.getCode() == KeyCode.D)  
         {
            right = false;
         }
      }
   }
   
   public class KeyListenerDown implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.A) 
         {
            left = true;
         }
         if (event.getCode() == KeyCode.W)  
         {
            up = true;
         }
         if (event.getCode() == KeyCode.S)  
         {
            down = true;
         }
         if (event.getCode() == KeyCode.D)  
         {
            right = true;
         }
         

      }
   }


   float x=300, y=300, startX = 300, startY = 300;
   double score = 0, highScore = 0;
   
   float forceY = 0, forceX = 0;
   boolean movingLeft = false, movingRight = false, movingUp = false, movingDown = false;
   
   int prevGridX = -1;
   int prevGridY = -1;
   
   boolean gameRunning = true;
   
   int diviser = 1000;
   
   public class AnimationHandler extends AnimationTimer
   {
      public void handle(long currentTimeInNanoSeconds) 
      { 
         if (!gameRunning) {
            return;
         }
      
         gc.clearRect(0,0,600,600);      
        
         //USE THIS CALL ONCE YOU HAVE A PLAYER
         drawBackground(thePlayer.getX(), thePlayer.getY(),gc); 
         
         
	      //example calls of draw - this should be the player's call for draw
         thePlayer.draw(300,300,gc,true); //all other objects will use false in the parameter.

         //example call of a draw where m is a non-player object. Note that you are passing the player's position in and not m's position.
         //m.draw(thePlayer.getX(),thePlayer.getY(),gc,false);
         
         score = Math.sqrt(Math.pow(thePlayer.getX() - startX, 2) + Math.pow(thePlayer.getY() - startY, 2));
            if (score > highScore) {
                highScore = score; // Update high score if current score is greater
                saveHighScore(highScore);
         }
         
//--------start of garbage for force calculation----------
         if (left || right)
         {
            if (forceX < 5){
               forceX += 0.1;
            }
            if (forceX > 5) {
               forceX = 5;
            }
            movingLeft = left;
            movingRight = right;
         }
         if (!left && !right)
         {
            if (forceX >= 0.025)
            {
               forceX -= 0.025;
            }
            if (forceX < 0.25) {
               forceX = 0;
            }
            
         }
         
         if (up || down)
         {
            if (forceY < 5){
               forceY += 0.1;
            }
            if (forceY > 5) {
               forceY = 5;
            }
            movingUp = up;
            movingDown = down;
         }
         if (!up && !down)
         {
            if (forceY >= 0.025)
            {
               forceY -= 0.025;
            }
            if (forceY < 0.25) {
               forceY = 0;
            }
            
         }
                 
         if(movingLeft)
            thePlayer.setX(thePlayer.getX() - forceX);
         if(movingRight)
            thePlayer.setX(thePlayer.getX() + forceX);
         if(movingDown)
            thePlayer.setY(thePlayer.getY() + forceY);
         if(movingUp)
            thePlayer.setY(thePlayer.getY() - forceY);
       
         //System.out.println("X: " + forceX + "Y: " + forceY);  
         
//----------end of garbage for force calculation
         
         
         int cgridx = ((int)thePlayer.getX())/100;
         int cgridy = ((int)thePlayer.getY())/100;
         //System.out.println(cgridx + " " + cgridy);
    
         //Display the score and high score
         gc.setFill(Color.WHITE);
         gc.setFont(new Font("Arial", 15));
         gc.fillText("Score: " + (int) score, 10, 20);
         gc.fillText("High Score: " + (int) highScore, 10, 50);
         
         
         int chance = (int)(Math.sqrt(Math.pow((cgridx * 100) - 300, 2) + Math.pow((cgridy * 100) - 300, 2))) / diviser;
         ///System.out.println("Chance: " + chance);
         //if grid changes
         if (cgridx > 10 && cgridy > 10 ){
            diviser = 100;
         }
         if (cgridx != prevGridX || cgridy != prevGridY) {
            Random random = new Random();
            int randomX;
            int randomY;
            
            for (int i = -5; i < 5; i ++) //for above
            {
               randomX = random.nextInt(100);
               randomY = random.nextInt(100) + 300;
               int randomChance = random.nextInt(100);
               if (randomChance <= chance * 10) {
                  mineList.add(new MineObject(thePlayer.getX() + (i * randomX) , thePlayer.getY() - randomY));
               }
            }
            for (int i = -5; i < 5; i ++) //for below
            {
               randomX = random.nextInt(100);
               randomY = random.nextInt(100) + 300;
               int randomChance = random.nextInt(100);
               if (randomChance <= chance) {
                  mineList.add(new MineObject(thePlayer.getX() + (i * randomX) , thePlayer.getY() + randomY));
               }
            }
            for (int i = -5; i < 5; i ++) //for left
            {
               randomY = random.nextInt(100);
               randomX = random.nextInt(100) + 300;
               int randomChance = random.nextInt(100);
               if (randomChance <= chance) {
                  mineList.add(new MineObject(thePlayer.getX() - randomX, thePlayer.getY() + (i * randomY)));
               }
            }
            for (int i = -5; i < 5; i ++) //for right
            {
               randomY = random.nextInt(100);
               randomX = random.nextInt(100) + 300;
               int randomChance = random.nextInt(100);
               if (randomChance <= chance) {
                  mineList.add(new MineObject(thePlayer.getX() + randomX, thePlayer.getY() + (i * randomY)));
               }
            }

            
            //update the previous grid position
            prevGridX = cgridx;
            prevGridY = cgridy;
         }
         
         
         //remove mines if 800 away
         Iterator<MineObject> iterator = mineList.iterator();
         while (iterator.hasNext()) {
            MineObject mine = iterator.next();
            double distance = Math.sqrt(Math.pow(mine.getX() - thePlayer.getX(), 2) + Math.pow(mine.getY() - thePlayer.getY(), 2));
            if (distance >= 800) {
               iterator.remove();
            }
         }
         
         //draw mines
         for (MineObject mine : mineList) {
             mine.draw(thePlayer.getX(), thePlayer.getY(), gc, false);
         }
         //for collision
         for (MineObject mine : mineList) {
            double distance = Math.sqrt(Math.pow(mine.getX() - thePlayer.getX(), 2) + Math.pow(mine.getY() - thePlayer.getY(), 2));
            if (distance <= 20) {
               gameRunning = false;
            }
         }
         if (!gameRunning) {
            gc.clearRect(0,0, 600,600);
            drawBackground(thePlayer.getX(), thePlayer.getY(),gc);
           
            
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 40));
            gc.fillText("Game Over!", 200, 300);

            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 20));
            gc.fillText("Score: " + (int) score, 220, 340);
            gc.fillText("High Score: " + (int) highScore, 220, 370);

            
         }

      }
   }
   

   public static void main(String[] args)
   {
      launch(args);
   }
}

