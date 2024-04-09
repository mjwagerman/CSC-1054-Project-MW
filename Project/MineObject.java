import javafx.scene.paint.*;
import javafx.scene.canvas.*;

//this is an example object
public class MineObject extends DrawableObject
{
	//takes in its position
   public MineObject(float x, float y)
   {
      super(x,y);
   }
   //draws itself at the passed in x and y.
   public void drawMe(float x, float y, GraphicsContext gc)
   {
      double value = Math.random();
      gc.setFill(Color.WHITE.interpolate(Color.RED,value));       
      gc.fillOval(x-3, y-3, 10, 10);
   }
   
}
