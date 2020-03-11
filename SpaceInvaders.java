import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Random;
import java.util.Vector;

public class SpaceInvaders extends Application {

    public static final int space_x_bw_aliens = 50;
    public static final int space_y_bw_aliens = 45;
    public static final int DIM_X = 1200;
    public static final int DIM_Y = 800;
    public static final int ABS_OFFSET_Y = 50;
    public static final int SIZE_ALIENS = 40;
    public static final int GRID_X = 10;
    public static final int GRID_Y = 5;
    public static  int DIR = 1;
    public static  double X_LEFT_BORDER = 0;
    public static  double X_RIGHT_BORDER = 0;
    public static  double Y_BORDER = 0;
    public static final int BULLET_SIZE = 20;
    public static  boolean KEYPRESSED = false;
    public static final double LVL1_FACTOR = 0.050;
    public static final double LVL2_FACTOR = 0.110;
    public static final double LVL3_FACTOR = 0.210;
    public static double LVL_FACTOR = 0.06;
    public static final double LVL_INC = 0.004;
    public static Vector<ImageView> ENEMY_BULLETS = new Vector<>();
    public static Vector<ImageView> PLAYER_BULLETS = new Vector<>();
    public static int SCORE = 0;
    public static int LIVES = 3;
    public static int LEVEL = 1;
    public static boolean UPDATE_ROOT = false;
    public static boolean GAME_WON = false;
    public static final int LVL_1_BUFFER = 10;
    public static boolean GAME_OVER = false;
    public static boolean LEVEL_CHANGE = false;
    public static double Probability = 20;


    AnimationTimer timer;

    @Override
    public void start(Stage stage) throws Exception {

        Scene scene = intro_scene();

        stage.setTitle("Space Invader");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(keyEvent -> {
            KeyCode key = keyEvent.getCode();
            if(key.equals(KeyCode.ENTER)){
                level(stage);
            } else if (key.equals(KeyCode.DIGIT1)){
                LEVEL = 1;
                level(stage);
            } else if (key.equals(KeyCode.DIGIT2)){
                LEVEL = 2;
                LVL_FACTOR = LVL2_FACTOR;
                Probability += 10;
                level(stage);
            } else if (key.equals(KeyCode.DIGIT3)){
                LEVEL = 3;
                LVL_FACTOR = LVL3_FACTOR;
                Probability += 2 * 10;
                level(stage);
            } else if (key.equals(KeyCode.Q)){
                System.exit(0);
            }
        });
    }

    @Override
    public void stop() {
        // stop the timer when the program is terminated
        timer.stop();
    }


    public Scene intro_scene(){
        //  Importing the logo image
        Image image = new Image("logo.png", 500, 500, true, true);
        ImageView imageView = new ImageView(image);

        // Making all the labels for the Instructions
        Label inst_title = new Label("Instructions");
        inst_title.setFont(Font.font("Arial", 40));
        Label start =  new Label("ENTER - Start Game");
        start.setFont(Font.font("Arial", 20));
        Label moves =  new Label("A, D - Move Ship Left or Right");
        moves.setFont(Font.font("Arial", 20));
        Label fire =  new Label("SPACE - Fire");
        fire.setFont(Font.font("Arial", 20));
        Label levels =  new Label("1/2/3 - Corresponding Level");
        levels.setFont(Font.font("Arial", 20));
        Label quit =  new Label("Q - Quit Game");
        quit.setFont(Font.font("Arial", 20));
        Label Intro = new Label("Implemented By Jasmeet Singh Gill");
        Label Intro2 = new Label("Student ID# 20804397");

        // Making all the VBoxes
        VBox instructions = new VBox(start,moves,fire,levels,quit);
        instructions.setAlignment(Pos.CENTER);
        instructions.setSpacing(5);
        VBox inst_with_title = new VBox(inst_title, instructions);
        inst_with_title.setAlignment(Pos.CENTER);
        inst_with_title.setSpacing(20);
        VBox menu = new VBox(imageView, inst_with_title);
        menu.setSpacing(40);
        VBox.setMargin(imageView, new Insets(100,0,0,0));
        menu.setAlignment(Pos.TOP_CENTER);
        VBox intros = new VBox(Intro, Intro2);
        intros.setAlignment(Pos.BOTTOM_CENTER);
        VBox root = new VBox(menu,intros);
        root.setBackground(Background.EMPTY);
        VBox.setMargin(intros, new Insets(200,0,0,0));
        // Setting the scenes
        Scene scene = new Scene(root, DIM_X , DIM_Y, Color.LIGHTGRAY);
        return scene;
    }

    //////////////////////////////////////////////////////

    public void level(Stage stage){
        // Making a container of all the aliens
        Group root = new Group();
        Vector<Vector<Alien>> Grid = new Vector<>();
        // Initialisation of the vector
        initialise_Grid(Grid, root);

        // Labels for printing of the current level
        Label info =  new Label(" Lives: " + LIVES + "  Level: " + LEVEL);
        info.setFont(Font.font("Arial", 20));
        info.setTextFill(Color.ANTIQUEWHITE);
        info.setLayoutX(1000);

        // Label for printing score
        Label info_score =  new Label(" Score: " + SCORE);
        info_score.setFont(Font.font("Arial", 20));
        info_score.setTextFill(Color.ANTIQUEWHITE);
        info_score.setLayoutX(100);

        // Making a ship for the player
        Image player_img = new Image("player.png", SIZE_ALIENS, SIZE_ALIENS,true, true);
        Ship player = new Ship(0,0,true,player_img);
        player.setX(DIM_X/2);
        player.setY(DIM_Y-SIZE_ALIENS);

        // Adding the label to the Group node
        root.getChildren().add(info);
        root.getChildren().add(info_score);
        root.getChildren().add(player);

        // timer ticks every time we want to advance a frame
        // scheduled to run every 1000/FPS ms
        timer = new AnimationTimer() {
            private long lastUpdate = 0 ;

            @Override
            public void handle(long l) {
                if (l - lastUpdate >= 10_000_000) {
                    handle_animation(Grid, player, info_score, root, info, stage);
                    lastUpdate = l ;
                }

            }
        };
        timer.start();
        Scene lvl1 = new Scene(root, DIM_X, DIM_Y, Color.BLACK);

        // Handling Input from keyboard
        lvl1.setOnKeyPressed(keyEvent -> {
            KeyCode key = keyEvent.getCode();
            if (key.equals(KeyCode.A)) {
                // To move left
                player.setx(player.getx() - 1);
            } else if (key.equals(KeyCode.D)){
                // To move right
                player.setx(player.getx() + 1);
            } else if (key.equals((KeyCode.SPACE))){
                // To fire bullets
                Image bullet =  new Image("player_bullet.png", BULLET_SIZE, BULLET_SIZE,true,true);
                ImageView temp = new ImageView(bullet);
                temp.setX(player.getX() + (SIZE_ALIENS/2));
                temp.setY(player.getY() - (SIZE_ALIENS/2));
                root.getChildren().add(temp);
                PLAYER_BULLETS.add(temp);
                String sound = getClass().getClassLoader().getResource("shoot.wav").toString();
                AudioClip clip = new AudioClip(sound);
                clip.play();
            } else if (key.equals(KeyCode.Q)) {
                System.exit(0);
            } else if (key.equals(KeyCode.R)){
                if (GAME_WON || GAME_OVER) {
                    if (GAME_OVER)
                        GAME_OVER = false;
                    if (GAME_WON)
                        GAME_WON = false;
                    root.getChildren().clear();
                    LEVEL = 1;
                    LVL_FACTOR = LVL1_FACTOR;
                    LEVEL_CHANGE = false;
                    PLAYER_BULLETS.clear();
                    ENEMY_BULLETS.clear();
                    LIVES = 3;
                    level(stage);
                }
            }
            KEYPRESSED = true;
            keyEvent.consume();
        });

        // Setting the scene to display
        stage.setScene(lvl1);
        stage.show();
    }

    ///////////////////////////////

    void handle_animation( Vector<Vector<Alien>> Grid, Ship player, Label score, Group root, Label info, Stage stage) {
        // if we hit the edge of the window, change direction
        if ((LIVES <= 0) || (Y_BORDER >= DIM_Y - SIZE_ALIENS)){
            GAME_OVER = true;
            Label over = new Label(" Game Over ");
            over.setFont(Font.font("Arial", 40));
            over.setTextFill(Color.BLACK);
            Label lost = new Label("You Lost");
            lost.setFont(Font.font("Arial", 30));
            lost.setTextFill(Color.BLACK);
            Label l = new Label("Your Score: " + SCORE);
            l.setFont(Font.font("Arial", 20));
            l.setTextFill(Color.BLACK);
            Label inst = new Label("Press R to Restart");
            inst.setFont(Font.font("Arial", 20));
            inst.setTextFill(Color.BLACK);
            Label quit = new Label("Press Q to Quit");
            quit.setFont(Font.font("Arial", 20));
            quit.setTextFill(Color.BLACK);
            VBox vbox = new VBox(over,lost,l,inst,quit);
            vbox.setAlignment(Pos.CENTER);
            vbox.setLayoutX(DIM_X/2 - 80);
            vbox.setLayoutY(DIM_Y/2 - 80);
            vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0.001,0.001,0.001,0.001))));
            root.getChildren().add(vbox);
            timer.stop();

        }

        if (stage_clear(Grid)){
            ++LEVEL;
            Probability += 10;
            LEVEL_CHANGE = true;
            if (LEVEL == 2)
                LVL_FACTOR = LVL2_FACTOR;
            if (LEVEL == 3)
                LVL_FACTOR = LVL3_FACTOR;
        }

        if (LEVEL > 3 ){
            GAME_WON = true;
            Label over = new Label(" Game Over ");
            over.setFont(Font.font("Arial", 40));
            over.setTextFill(Color.BLACK);
            Label lost = new Label("You Won");
            lost.setFont(Font.font("Arial", 30));
            lost.setTextFill(Color.BLACK);
            Label l = new Label("Your Score: " + SCORE);
            l.setFont(Font.font("Arial", 20));
            l.setTextFill(Color.BLACK);
            Label inst = new Label("Press R to Restart");
            inst.setFont(Font.font("Arial", 20));
            inst.setTextFill(Color.BLACK);
            Label quit = new Label("Press Q to Quit");
            quit.setFont(Font.font("Arial", 20));
            quit.setTextFill(Color.BLACK);
            VBox vbox = new VBox(over,lost,l,inst,quit);
            vbox.setAlignment(Pos.CENTER);
            vbox.setLayoutX(DIM_X/2 - 80);
            vbox.setLayoutY(DIM_Y/2 - 80);
            vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0.001,0.001,0.001,0.001))));
            root.getChildren().add(vbox);
            timer.stop();
        }

        Random rand = new Random();
        int random = rand.nextInt(5000);
        // Modifying the right border according to the requirements
        if(!LEVEL_CHANGE && !GAME_OVER && !GAME_WON) {
           right_border_empty(Grid);
           left_border_empty(Grid);
           lower_border_empty(Grid);

           if (X_LEFT_BORDER < 0 || X_RIGHT_BORDER > DIM_X || (random < Probability)) {
               // Update Y
               if (X_LEFT_BORDER < 0 || X_RIGHT_BORDER > DIM_X) {
                   DIR = -1 * DIR;
                   update_y(Grid);
                   Y_BORDER += SIZE_ALIENS;
               }
               // Firing from the alien ships
               boolean fired = false;

               while (!fired) {
                   Random rand2 = new Random();
                   int random2 = rand2.nextInt(Grid.elementAt(Grid.size() - 1).size());
                   int random_bullet = (rand2.nextInt(3)) + 1;
                   String bullet_name = "bullet" + random_bullet + ".png";
                   if (Grid.elementAt(Grid.size() - 1).elementAt(random2).alive) {
                       double x_offset = Grid.elementAt(Grid.size() - 1).elementAt(random2).getX() + (SIZE_ALIENS / 2);
                       double y_offset = Grid.elementAt(Grid.size() - 1).elementAt(random2).getY() + (SIZE_ALIENS);
                       Image temp = new Image(bullet_name, 30, 30, true, true);
                       ImageView b_temp = new ImageView(temp);
                       b_temp.setX(x_offset);
                       b_temp.setY(y_offset);
                       ENEMY_BULLETS.add(b_temp);
                       fired = true;
                       UPDATE_ROOT = true;
                   }
               }
           } else {
               // Update X
               update_x(Grid);
               UPDATE_ROOT = false;
           }
       }
        // Updating border of the grid of aliens
        X_LEFT_BORDER += DIR * LVL_FACTOR * SIZE_ALIENS;
        X_RIGHT_BORDER += DIR * LVL_FACTOR * SIZE_ALIENS;

            // Checking if the game is over and changing the scene accordingly
            if (GAME_OVER || GAME_WON){
                timer.stop();
                return;
            }

            if (LEVEL_CHANGE){
                LEVEL_CHANGE = false;
                initialise_Grid(Grid,root);
                info.setText(" Lives: " + LIVES + "  Level: " + LEVEL);
                update_images(Grid);
                return;
            }
            // Reset X and Y for the images
            update_images(Grid);

            // Updating if any key has been pressed
            if (KEYPRESSED) {
                double new_pos = (player.getx() * SIZE_ALIENS) + player.getX();
                if (new_pos >= 0  && new_pos <= (DIM_X - SIZE_ALIENS)){
                    player.setX((player.getx() * SIZE_ALIENS) + player.getX());
                }
                player.setx(0);
                KEYPRESSED = false;
            }

            // updating the root group node if any new bullet has been added to the enemy bullets
            if (UPDATE_ROOT){
                root.getChildren().add(ENEMY_BULLETS.elementAt(ENEMY_BULLETS.size()-1));
                UPDATE_ROOT = false;
            }

            // Update Player Bullets if they exist
            // Checking if the bullet hits anything
            if (! PLAYER_BULLETS.isEmpty()) {
                for (int i = 0; i < PLAYER_BULLETS.size(); ++i) {
                    PLAYER_BULLETS.elementAt(i).setY(PLAYER_BULLETS.elementAt(i).getY() - (LVL2_FACTOR * 2.5 * SIZE_ALIENS) );
                    for (int j = 0; j < Grid.size(); ++j) {
                        if (detect_all(Grid.elementAt(j), PLAYER_BULLETS.elementAt(i))) {
                            PLAYER_BULLETS.removeElementAt(i);
                            SCORE += LEVEL * 10;
                            LVL_FACTOR += LVL_INC;
                            score.setText(" Score: " + SCORE);
                            String sound = getClass().getClassLoader().getResource("explosion.wav").toString();
                            AudioClip clip = new AudioClip(sound);
                            clip.play();
                            break;
                        }
                    }
                }
            }

            // Updating the enemy bullets
            if (! ENEMY_BULLETS.isEmpty()){
                for (int i = 0; i < ENEMY_BULLETS.size(); ++i) {
                    ENEMY_BULLETS.elementAt(i).setY(ENEMY_BULLETS.elementAt(i).getY() + (LVL2_FACTOR * 2.5 * SIZE_ALIENS));
                    if (detect_intersection(ENEMY_BULLETS.elementAt(i), player)){
                        ENEMY_BULLETS.elementAt(i).setImage(null);
                        ENEMY_BULLETS.removeElementAt(i);
                        LIVES = LIVES - 1;
                        String sound = getClass().getClassLoader().getResource("invaderkilled.wav").toString();
                        AudioClip clip = new AudioClip(sound);
                        clip.play();
                        player.setX(DIM_X/2);
                        player.setY(DIM_Y - SIZE_ALIENS);
                        info.setText(" Lives: " + LIVES + "  Level: " + LEVEL);
                        break;
                    }
                }
            }
    }


    void initialise_Grid(Vector<Vector<Alien>> Grid, Group root){
        Grid.clear();
        for(int i = 0; i < 5; ++i){
            Vector <Alien> aliens = new Vector<>();
            for(int j = 0; j < 10; ++j){
                Image img;
                // Calculating the offset for the postions of the aliens
                int x_offset = (j * space_x_bw_aliens);
                int y_offset = (i * space_y_bw_aliens) + ABS_OFFSET_Y;

                // Initialising images for the first row
                if (i == 0){
                    img = new Image("enemy1.png", SIZE_ALIENS, SIZE_ALIENS,true, true);
                } else if (i == 1 || i == 2 ){
                    // Initialising images for the 2nd and 3rd row
                    img = new Image("enemy2.png", SIZE_ALIENS, SIZE_ALIENS,true, true);
                } else {
                    // Initialising images for the last 2 rows
                    img = new Image("enemy3.png", SIZE_ALIENS, SIZE_ALIENS,true, true);
                }

                // Creating a temp object for insertion into the vector
                Alien temp = new Alien(j, i ,true, img);
                // Setting the position of the aliens
                temp.setX(x_offset);
                temp.setY(y_offset);

                // Adding the alien node to the root group node
                root.getChildren().add(temp);

                // Storing the alien into the array
                aliens.add(temp);
            }
            Grid.add(aliens);
        }
        // Current borders of the grid of the aliens
        X_RIGHT_BORDER = ((GRID_X - 1) * space_x_bw_aliens) + ((GRID_X - 5) * SIZE_ALIENS) - 10;
        X_LEFT_BORDER = 0;
        Y_BORDER = ((GRID_Y + 2) * (SIZE_ALIENS ));


    }


    void update_y(Vector<Vector<Alien>> Grid){
        for(int i = 0; i < Grid.size(); ++i){
            for (int j = 0; j < Grid.elementAt(i).size(); ++j){
                int prev_value = Grid.elementAt(i).elementAt(j).gety();
                Grid.elementAt(i).elementAt(j).sety(prev_value + 1);
            }
        }
    }


    void update_x(Vector<Vector<Alien>> Grid){
        for(int i = 0; i < Grid.size(); ++i){
            for (int j = 0; j < Grid.elementAt(i).size(); ++j){
                double prev_value = Grid.elementAt(i).elementAt(j).getx();
                Grid.elementAt(i).elementAt(j).setx(prev_value + (DIR * LVL_FACTOR));
            }
        }
    }


    // update_images updates the position of the images held in Grid
    void update_images(Vector<Vector<Alien>> Grid){
        for(int i = 0; i < Grid.size(); ++i){
            for (int j = 0; j < Grid.elementAt(i).size(); ++j){
                double x_value = Grid.elementAt(i).elementAt(j).getx();
                int y_value = Grid.elementAt(i).elementAt(j).gety();
                double x_offset = (x_value *  space_x_bw_aliens);
                int y_offset = (y_value * space_y_bw_aliens) + ABS_OFFSET_Y;
                Grid.elementAt(i).elementAt(j).setX(x_offset);
                Grid.elementAt(i).elementAt(j).setY(y_offset);

            }
        }
        String sound = getClass().getClassLoader().getResource("fastinvader1.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }


    boolean detect_intersection(ImageView img1, ImageView img2){
        if(img1.getBoundsInParent().intersects(img2.getBoundsInParent())){
            return true;
        }
        return false;
    }


    boolean detect_all(Vector<Alien> aliens, ImageView bullet ){
        for (int i = 0; i < aliens.size(); ++i){
            if(aliens.elementAt(i).alive) {
                if (detect_intersection(aliens.elementAt(i), bullet)) {
                    aliens.elementAt(i).alive = false;
                    aliens.elementAt(i).setImage(null);
                    bullet.setImage(null);
                    return true;
                }
            }
        }
        return false;
    }


    void right_border_empty(Vector<Vector<Alien>> Grid){
        for (int i = 0;  i < Grid.size(); ++i){
            if((Grid.elementAt(i).elementAt(Grid.elementAt(i).size() - 1)).alive){
                return;
            }
        }
        for (int i = 0;  i < Grid.size(); ++i){
            Grid.elementAt(i).removeElementAt(Grid.elementAt(i).size() - 1);
        }
        X_RIGHT_BORDER -= SIZE_ALIENS;
    }


    void left_border_empty(Vector<Vector<Alien>> Grid){
        for (int i = 0;  i < Grid.size(); ++i){
            if((Grid.elementAt(i).elementAt(0)).alive){
                return;
            }
        }
        for (int i = 0;  i < Grid.size(); ++i){
            Grid.elementAt(i).removeElementAt(0);
        }
        X_LEFT_BORDER += SIZE_ALIENS - LVL_1_BUFFER;
    }


    void lower_border_empty(Vector<Vector<Alien>> Grid){
        for (int i = 0;  i < Grid.elementAt(Grid.size() - 1).size(); ++i){
            if((Grid.elementAt(Grid.size() - 1).elementAt(i)).alive){
                return;
            }
        }
        Grid.removeElementAt(Grid.size() - 1);
        Y_BORDER -= SIZE_ALIENS;
    }


    boolean stage_clear(Vector<Vector<Alien>> Grid){
        // Checking if no alien is left in the Grid
        if(Grid.isEmpty()){
            return true;
        }
        // Checking if no alien is left in the vectors in the Grid
        boolean all_empty = true;
        for (int i = 0; i < Grid.size(); ++i){
            if (! Grid.elementAt(i).isEmpty()){
                all_empty = false;
            }
        }
        if (all_empty){
            return true;
        }
        // Checking if all the aliens are dead
        for (int i = 0; i < Grid.size(); ++i){
            for (int j = 0; j < Grid.elementAt(i).size(); ++j){
                if (Grid.elementAt(i).elementAt(j).alive)
                    return false;
            }
        }
        return true;

    }

}
