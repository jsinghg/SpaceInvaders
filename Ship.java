import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ship extends ImageView {
    // Curr position on the screen
    int x;
    int y;

    // True if the Alien is alive or not
    boolean alive;

    // Constructor
    public Ship(int x, int y, boolean alive, Image source) {
        this.x = x;
        this.y = y;
        this.alive = alive;
        setImage(source);
    }

    // Getters and setters
    public int getx(){
        return x;
    }

    public void setx(int x){
        this.x = x;
    }

    public int gety(){
        return y;
    }

    public void sety(int y){
        this.y = y;
    }
}
