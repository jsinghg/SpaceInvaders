import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Alien extends ImageView {
    // Curr position on the screen
    double x;
    int y;

    // True if the Alien is alive or not
    boolean alive;

    // Constructor
    public Alien(double x, int y, boolean alive, Image source) {
        this.x = x;
        this.y = y;
        this.alive = alive;
        setImage(source);
    }

    // Getters and setters
    public double getx(){
        return x;
    }

    public void setx(double x){
        this.x = x;
    }

    public int gety(){
        return y;
    }

    public void sety(int y){
        this.y = y;
    }

}
