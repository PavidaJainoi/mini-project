
package zomgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ZomWindow extends JFrame{
    
//  Ground Y = Setting ground position when zombie fall down 
    public static float GRAVITY = 0.4f;
    public static float GROUNDY = 500;
    
    private int maxJumps = 2;
    private int jumps = 0;

    private ZomScreen zomScreen = new ZomScreen();

    public ZomWindow() {
        add(zomScreen);
        addKeyListener(zomScreen);
    }
    
    public void startGame(){
        zomScreen.startGame();
    }
    
    public static void main(String[] args) {
        ZomWindow zm = new ZomWindow();
        
        zm.setTitle("! RUN Zombie RUN !");
        zm.setSize(1200, 588);
        zm.setLocation(50,50); //set location window when open it 
        zm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        zm.setVisible(true);
        zm.startGame();
    }
    
    /*------------------------------------------ Screen Zone ------------------------------------------*/
    
    public class ZomScreen extends JPanel implements Runnable, KeyListener{
//      Game State for change
        private static final int HOME_STATE = 0;
        private static final int FIRST_STATE = 1;
        private static final int SECOND_STATE = 2;
        private static final int OVER_STATE = 3;
        
//      Set default of game state
        private int GAME_STATE =  HOME_STATE; 
        
        private Thread thread = new Thread(this);
        private ZomCharacter zomacter = new ZomCharacter();
        private Land land = new Land();
        private Clouds clouds = new Clouds();
        private Bonusset bonusset = new Bonusset(zomacter, zomScreen);
        private Obstacleset obstacleset = new Obstacleset(zomacter, this);
        private EnemySet enemySet = new EnemySet(zomacter, this);
        
        private int score;
        private int brian;

        public ZomScreen(){
//          Zombie Location
            zomacter.setX(75); 
            zomacter.setY(398);
        }

        public void startGame(){
            thread.start();
        }
        
//      Change game state when brian = 10
        private void StateChange() {
        if (brian == 10) {
            switch (GAME_STATE) {
                case FIRST_STATE:
                    // Change to SECOND_STATE
                    GAME_STATE = SECOND_STATE;
                    brian = 0;
                    
                    break;
                case SECOND_STATE:
                    // Change back to FIRST_STATE
                    GAME_STATE = FIRST_STATE;
                    brian = 0;
                    rechange();
                    
                    break;
                }
            }
        }
        
        public void addScore(int score) {
            this.score += score;
        }
        
        public void addBrians(int brian) {
            this.brian += brian;
            
            StateChange();
        }
        
        @Override
        public void paintComponent(Graphics g){
//            super draw the backgroung again and mean clear the all background
//            Coordination x, y, width, height of the Rectangle
            super.paintComponent(g);
            
            Font fs14 = new Font("Arial", Font.BOLD, 14);
            Font fs36 = new Font("Arial", Font.BOLD, 36);
            Font fs100 = new Font("Arial", Font.BOLD, 100);
            
//            background color 
            g.setColor(Color.decode("#1C2833"));
            g.fillRect(0, 0, getWidth(), getHeight());

//            various elements based on the current game state
            switch(GAME_STATE) {
                case HOME_STATE:
                    clouds.paintComponent(g);
                    land.paintComponent(g);
                    zomacter.paintComponent(g);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs100);
                    g.drawString("ZOMBIE RUN !!", 230, 200);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("[SPACE] - START", 445, 250);
                    
                    break;
                case FIRST_STATE:
                    clouds.paintComponent(g);
                    bonusset.paintComponent(g);
                    land.paintComponent(g);
                    zomacter.paintComponent(g);
                    enemySet.paintComponent(g);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("SCORE : " + String.valueOf(score), 50, 50);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("BRAINS : " + String.valueOf(brian), 50, 100);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs14);
                    g.drawString("Note : ", 900, 30);
                    g.drawString("Press 'ESC' to End the Game", 900, 70);
                    g.drawString("Press 'SPACE' to Jump", 900, 50);
                    g.drawString("Press 'SPACE' x2 to Double Jump", 900, 90);
                    g.drawString("Collect 'Brians x10' to Change state", 900, 110);
                    
                    break;
                case SECOND_STATE:
                    clouds.paintComponent(g);
                    bonusset.paintComponent(g);
                    zomacter.paintComponent(g);
                    obstacleset.paintComponent(g);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("SCORE : " + String.valueOf(score), 50, 50);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("BRAINS : " + String.valueOf(brian), 50, 100);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs14);
                    g.drawString("Note : ", 900, 30);
                    g.drawString("You can jump Unlimitedly", 900, 50);
                    g.drawString("Collect 'Brians x10' to Change state", 900, 70);
                    
                    break;    
                case OVER_STATE:
                    clouds.paintComponent(g);
                    land.paintComponent(g);
                    enemySet.paintComponent(g);
                    obstacleset.paintComponent(g);
                    bonusset.paintComponent(g);
                    zomacter.paintComponent(g);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("YOUR SCORE : " + String.valueOf(score), 400, 250);
                    
                    //g.drawImage(GAMEOVERTEXT, 200, 225, null);
                    g.setColor(Color.WHITE);
                    g.setFont(fs100);
                    g.drawString("GAME  OVER", 250, 200);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("[R] RESTART", 400, 350);
                    
                    g.setColor(Color.WHITE);
                    g.setFont(fs36);
                    g.drawString("[ESC] BACK TO HOME", 400, 400);
                    
                    break;
            }

        }
        
        @Override
        public void run() {
            while (true) {                
                try {
                    update();
                    
                    repaint();
                    Thread.sleep(12);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public void update(){
            switch (GAME_STATE) {
                case FIRST_STATE:
                    zomacter.update();
                    land.update();
                    clouds.update();
                    bonusset.update();
                    enemySet.update();
                    
//                    if Zombie is not alive
                    if(zomacter.getAlive() == false){
                        GAME_STATE = OVER_STATE;
                    } 
                    
                    break;
                case SECOND_STATE:
                    zomacter.update();
                    clouds.update();
                    obstacleset.update();
                    bonusset.update();
                    
//                    if Zombie is not alive
                    if(zomacter.getAlive() == false) {
                        GAME_STATE = OVER_STATE;
                    }
                    
                    break;
            }
        }
        
//        when change state reset
        private void rechange() {
            zomacter.setLife(true);
            GRAVITY = 0.4f;
            GROUNDY = 500;
            
            //Zombie Location
            zomacter.setX(75); 
            zomacter.setY(398);
            
            bonusset.reset();
            enemySet.reset();
            obstacleset.reset();
        }
        
//        when gameover reset
        private void resetGame() {
            zomacter.setLife(true);
            score = 0;
            brian = 0; 
            GRAVITY = 0.3f;
            GROUNDY = 500;
            
            //Zombie Location
            zomacter.setX(75); 
            zomacter.setY(398);
            
            bonusset.reset();
            enemySet.reset();
            obstacleset.reset();
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                
                case KeyEvent.VK_SPACE:
//                    start game 
                    if(GAME_STATE == HOME_STATE){
                        GAME_STATE = FIRST_STATE;
//                    in first state     
                    }else if (GAME_STATE == FIRST_STATE){
                        if (jumps < maxJumps) {
                            zomacter.Jump();
                            jumps++;
                        }
                    }
//                    in second state
                    if (GAME_STATE == SECOND_STATE){
                            zomacter.Jump();
                            GRAVITY = 0.8f;
                            GROUNDY = 700;
                    } 
                    
                    break;
                case KeyEvent.VK_R:
//                    when game over want to restart ?
                    if (GAME_STATE == OVER_STATE) {
                        resetGame();
                        GAME_STATE = FIRST_STATE;
                    }
                
                    break;
                case KeyEvent.VK_ESCAPE:
//                    game start but need to exit game
                    if (GAME_STATE == FIRST_STATE) {
                        GAME_STATE = OVER_STATE;
                    }else if (GAME_STATE == SECOND_STATE) {
                        GAME_STATE = OVER_STATE;
                    } else if (GAME_STATE == OVER_STATE) {
                        GAME_STATE = HOME_STATE;
                    }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }
    
    /*------------------------------------------ Character Zone ------------------------------------------*/
    
    public class ZomCharacter {
        private float x = 0;
        private float y = 0;
        private float speedY = 0;
        private int distance;

        private Animation Zomination = new Animation(90); // Zombie Animation
        private Rectangle rect = new Rectangle();
        
        private boolean isLife = true;

        public ZomCharacter() {
            Zomination.addFrame(Resource.getResourceImage("data/Zombie_Nor.png"));
            Zomination.addFrame(Resource.getResourceImage("data/Zombie_L.png"));
            Zomination.addFrame(Resource.getResourceImage("data/Zombie_R.png"));
        }

        public void update() {
            Zomination.update();

//             Zombie is on the ground or below it
            if(y >= GROUNDY - Zomination.getFrame().getHeight()) {
                speedY = 0;
                y = GROUNDY - Zomination.getFrame().getHeight();
                jumps = 0;
            } else {    // Zombie is in the air
                speedY += GRAVITY;
                y += speedY;
            }
            
            rect.x = (int) x;
            rect.y = (int) y;
            rect.width = Zomination.getFrame().getWidth();
            rect.height = Zomination.getFrame().getHeight();
            
            distance ++;
            
            if(distance % 10 == 0) {
                zomScreen.addScore(1);
            }
        }

        public void paintComponent(Graphics g){
            g.drawImage(Zomination.getFrame(), (int) x, (int) y, null);
        }

        public void Jump() {
            speedY = -11;
            y += speedY;
        }
        
        public Rectangle getBound() {
//            Hit Box fixed
            rect = new Rectangle((int) x + 20, (int) y + 5, 70, 70);
            
            return rect;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getSpeedY() {
            return speedY;
        }

        public void setSpeedY(float speedY) {
            this.speedY = speedY;
        }
        
        public void setLife(boolean alive){
            isLife = alive;
        }
        
        public boolean getAlive() {
            return isLife;
        }
    }
    
    /*------------------------------------------ Obstacle Zone ------------------------------------------*/
    
    public class Obstacle extends Obstacles {
        private ZomCharacter zomacter;
        private BufferedImage image;
        
        private static List<Obstacle> obstacles = new ArrayList<>();
        private Rectangle rect = new Rectangle();
        private Random random = new Random();
        
        private int posX, posY;
        private int obspeed = 5;
       
        public Obstacle(ZomCharacter zomacter) {
            this.zomacter = zomacter;
            
            image = Resource.getResourceImage("data/Tower1.png");
            
            posX = 1200; 
            posY = 400;
        }
        
        public void update() {
            posX -= obspeed;
            obspeed += 0.5;
            
            rect.x = posX;
            rect.y = posY;
            rect.width = image.getWidth();
            rect.height = image.getHeight(); 
        }
        
        @Override
        public Rectangle getBound(){
            return rect;
        }
        
        @Override
        public void paintComponent(Graphics g){
            g.drawImage(image, posX, posY, null);
        }
        
        public void setX(int x){
            posX = x;
        }
        
        public void setY(int y){
            posY = y;
        }
        
        public void setImage(BufferedImage image){
            this.image= image;
        }
        
        @Override
        public boolean isOutofScreen() {
//            return the trouble out of screen for add new trouble in EnemySetting class
            return posX + image.getWidth() < 0;
        }
        
        @Override
        public boolean isOver() {
            return zomacter.getX() > posX;
        }
       
    }
    
    public abstract class Obstacles {
        public abstract Rectangle getBound();
        public abstract void paintComponent(Graphics g);
        public abstract void update();
        public abstract boolean isOutofScreen();
        public abstract boolean isOver(); 
    }
    
    public class Obstacleset {
        private List<Obstacles> obstacles = new ArrayList<Obstacles>();
        private Random random = new Random();
        private BufferedImage image1, image2;
        
        private ZomCharacter zomacter;
        private ZomScreen zomScreen;
        
        public Obstacleset(ZomCharacter zomacter, ZomScreen zomScreen) {
            this.zomacter = zomacter;
            this.zomScreen = zomScreen;
            
            image1 = Resource.getResourceImage("data/Tower1.png");
            image2 = Resource.getResourceImage("data/Tower2.png");
            
//             Add multiple obstacles 
            for (int i = 0; i < 2; i++) {
                obstacles.add(getRandom());
            }
            
        }
        
        public void update() {
            for(Obstacles o : obstacles) {
                o.update();
               
                if(o.getBound().intersects(zomacter.getBound())) {
                    zomacter.setLife(false);
                }
            }
            
            List<Obstacles> Remove = new ArrayList<>();
            for (Obstacles o : obstacles) {
                    if (o.isOutofScreen()) {
                        Remove.add(o);
                    }
                }

                obstacles.removeAll(Remove);

                while (obstacles.size() < 2) {
                    obstacles.add(getRandom());
                }
            }
        
        public void paintComponent(Graphics g){
            for(Obstacles o : obstacles) {
                o.paintComponent(g);
            }
        }
        
        public void reset(){
            obstacles.clear();

//             Add multiple obstacles 
            for (int i = 0; i < 2; i++) {
                obstacles.add(getRandom());
            }
        }
        
        private Obstacle getRandom() {
            Obstacle obstacle = new Obstacle(zomacter);
            int n = random.nextInt(-1000,400);
            int m = random.nextInt(1200,2500);
            
            if(n < 0 && n < -500 && n > -700) {
                obstacle.setY(n);
                obstacle.setX(m);
                
                if(random.nextBoolean()){
                    obstacle.setImage(image1);
                } else if (random.nextBoolean()){
                    obstacle.setImage(image2);
                }
            } else if (n > 0 && n > 200) {
                obstacle.setY(n);
                obstacle.setX(m);
                
                if(random.nextBoolean()){
                    obstacle.setImage(image1);
                } else if (random.nextBoolean()){
                    obstacle.setImage(image2);
                }
            }
            
            return obstacle;
        }
    }
    
    /*------------------------------------------ Trouble Zone ------------------------------------------*/
    
    public class Trouble extends Enemy{
        private BufferedImage image;
        private int posX, posY;
        private int troublespeed = 5;
        
        private Rectangle rect = new Rectangle();
        private ZomCharacter zomacter;
        
        public Trouble(ZomCharacter zomacter){
            this.zomacter = zomacter;
            
            image = Resource.getResourceImage("data/Trouble1.png");
            
            
            posX = 500; 
            posY = 500 - image.getHeight();
        }
        
        @Override
        public void update(){
//            can fix move speed of trouble
            posX -= troublespeed;
            troublespeed += 0.5;
            
            rect.x = posX;
            rect.y = posY;
            rect.width = image.getWidth();
            rect.height = image.getHeight();
        }
        
        @Override
        public Rectangle getBound(){
            return rect;
        }
        
        @Override
        public void paintComponent(Graphics g){
            g.drawImage(image, posX, posY, null);
        }
        
        public void setX(int x){
            posX = x;
        }
        
        public void setY(int y){
            posY = y;
        }
        
        public void setImage(BufferedImage image){
            this.image= image;
        }
        
        @Override
        public boolean isOutofScreen() {
//            return the trouble out of screen for add new trouble in EnemySetting class
            return posX + image.getWidth() < 0;
        }
        
        @Override
        public boolean isOver() {
            return zomacter.getX() > posX;
        }
        
    }
  
    public abstract class Enemy {
        public abstract Rectangle getBound();
        public abstract void paintComponent(Graphics g);
        public abstract void update();
        public abstract boolean isOutofScreen();
        public abstract boolean isOver(); 
    }
    
    public class EnemySet {
        private List<Enemy> enemies = new ArrayList<Enemy>();
        private Random random = new Random();
        private BufferedImage imageTrouble1, imageTrouble2, imageTrouble3;
        
        private ZomCharacter zomacter;
        private ZomScreen zomScreen;
        
        public EnemySet(ZomCharacter zomacter, ZomScreen zomScreen) {
            this.zomacter = zomacter;
            this.zomScreen = zomScreen;
            
            imageTrouble1 = Resource.getResourceImage("data/Trouble1.png");
            imageTrouble2 = Resource.getResourceImage("data/Trouble2.png");
            imageTrouble3 = Resource.getResourceImage("data/Trouble3.png");
            
//             Add multiple obstacles 
            for (int i = 0; i < 2; i++) {
                enemies.add(getRandom());
            }
        }
        
        public void update(){
            for(Enemy e : enemies) {
                e.update();
               
                if(e.getBound().intersects(zomacter.getBound())) {
                    zomacter.setLife(false);
//                    System.out.println("hit !!");
                }
            }
            
            List<Enemy> Remove = new ArrayList<>();
            for (Enemy e : enemies) {
                if (e.isOutofScreen()) {
                    Remove.add(e);
                }
            }

            enemies.removeAll(Remove);

            while (enemies.size() < 2) {
                enemies.add(getRandom());
            }
        }
        
        public void paintComponent(Graphics g){
            for(Enemy e : enemies) {
                e.paintComponent(g);
            }
        }
        
        public void reset(){
            enemies.clear();
            
//             Add multiple obstacles 
            for (int i = 0; i < 2; i++) {
                enemies.add(getRandom());
            }
        }
        
        private Trouble getRandom(){
            Trouble trouble = new Trouble(zomacter);
            trouble.setX(1200);
            
            if(random.nextBoolean()) {
                trouble.setImage(imageTrouble1);
                trouble.setX(1500);
                
            } else if (random.nextBoolean()){
                trouble.setImage(imageTrouble2);
                trouble.setX(1800);
            } else {
                trouble.setImage(imageTrouble3);
                trouble.setX(2100);
            }
            return trouble;
        }
    }
    
    /*------------------------------------------ Item Zone ------------------------------------------*/
    
    public class Bonus extends Bonuss{
        private Rectangle rect = new Rectangle();
        private BufferedImage tpimage;
        private ZomCharacter zomacter;
        
        private int speed = 5;
        private int posX, posY;
        Bonus(ZomCharacter zomacter) {
            this.zomacter = zomacter;
            
            tpimage = Resource.getResourceImage("data/Portal_brain.png");
            
            posX = 1200; 
            posY = 400;
        }
        
        public void paintComponent(Graphics g){
                g.drawImage(tpimage, posX, posY, null);
        }
        
        @Override
        public void update() {
            posX -= speed;
            speed += 0.5;
            
            rect.x = posX;
            rect.y = posY;
            rect.width = tpimage.getWidth();
            rect.height = tpimage.getHeight();
            
//             Award bonus points and remove the brain
            if (zomacter.getBound().intersects(rect)) {
            zomScreen.addBrians(1);
            posX = -tpimage.getWidth(); // Move the brain out of the screen
        }
        }
        
        @Override
        public Rectangle getBound(){
            return rect;
        }
        
        public void setX(int x){
            posX = x;
        }
        
        public void setY(int y){
            posY = y;
        }
        
        @Override
        public boolean isOutofScreen() {
            return posX + tpimage.getWidth() < 0;        
        }

        @Override
        public boolean isOver() {
            return zomacter.getX() > posX;
        }
        
    }
    
    public abstract class Bonuss {
        public abstract Rectangle getBound();
        public abstract void paintComponent(Graphics g);
        public abstract void update();
        public abstract boolean isOutofScreen();
        public abstract boolean isOver(); 
    }
    
    public class Bonusset {
        private List<Bonuss> bonuss = new ArrayList<Bonuss>();
        private Random random = new Random();
        private BufferedImage tpimage;
        
        private ZomCharacter zomacter;
        private ZomScreen zomScreen;
        
        public Bonusset(ZomCharacter zomacter, ZomScreen zomScreen) {
            this.zomacter = zomacter;
            this.zomScreen = zomScreen;
            
            tpimage = Resource.getResourceImage("data/Portal_brain.png");
            
            bonuss.add(getRandom());
        }
        
        public void update() {
            for(Bonuss p : bonuss) {
                p.update();
            }
            
             List<Bonuss> removeList = new ArrayList<>();
            for (Bonuss p : bonuss) {
                if (p.isOutofScreen()) {
                    removeList.add(p);
                }
            }
            
            bonuss.removeAll(removeList);

            while (bonuss.size() < 1) {
                bonuss.add(getRandom());
            }
        }
            
        public void paintComponent(Graphics g){
            for(Bonuss p : bonuss) {
                p.paintComponent(g);
            }
        }
        
        public void reset(){
            bonuss.clear();
            bonuss.add(getRandom());
        }
        
        private Bonuss getRandom() {
            Bonus bonus = new Bonus(zomacter);
            int n = random.nextInt(200,400);
            int m = random.nextInt(1200,1400);
            
            bonus.setY(n);
            bonus.setX(m);
            
            return bonus;
        }
    }
    
    /*------------------------------------------ Clouds Zone ------------------------------------------*/
    
    public class Clouds {
        private BufferedImage cloudImage;
        private List<Cloud> clouds = new ArrayList<Cloud>();
        private Random random = new Random();
        private int cloudspeed = 2;
        
        public Clouds() {
            cloudImage = Resource.getResourceImage("data/Cloud.png");
            
            Cloud cloud1 = new Cloud();
            
            cloud1.posX = 100;
            cloud1.posY = 50;
            clouds.add(cloud1);
            
            cloud1 = new Cloud();
            cloud1.posX = 300;
            cloud1.posY = 250;
            clouds.add(cloud1);
            
            cloud1 = new Cloud();
            cloud1.posX = 750;
            cloud1.posY = 75;
            clouds.add(cloud1);
            
            cloud1 = new Cloud();
            cloud1.posX = 1000;
            cloud1.posY = 150;
            clouds.add(cloud1);
            
            cloudspeed += 0.5;
        }
        
        public void update() {
//            move speed of clouds
            for(Cloud cloud : clouds){
                cloud.posX -= cloudspeed;
            }
            
//            when first image leave the screen add new image and remove first image
            Cloud firstCloud = clouds.get(0);
            if(firstCloud.posX + cloudImage.getWidth() < 0){
                firstCloud.posX = 1200;
                
                clouds.remove(firstCloud);
                clouds.add(firstCloud);
            }
        }
        
        public void paintComponent(Graphics g){
            for(Cloud cloud : clouds) {
                g.drawImage(cloudImage, (int) cloud.posX, (int) cloud.posY, null);
            }
        }
        
        private class Cloud {
            float posX, posY; // Position by X and Y 
            
        }
    }
    
    /*------------------------------------------ Land Zone ------------------------------------------*/
    
    public class Land {
        private List<LandImage> listImage = new ArrayList<LandImage>();
        private BufferedImage Nor_land1, Nor_land2, Nor_land3;
        private Random random = new Random();
        private int landspeed = 5;
        
        public  Land() {
//            Image list
            Nor_land1 = Resource.getResourceImage("data/land1.png");
            Nor_land2 = Resource.getResourceImage("data/land2.png");
            Nor_land3 = Resource.getResourceImage("data/land3.png");
            
//            num of image for loop in Screen
            int numofLand = 1200 / Nor_land1.getWidth() +2;
            
            for(int i = 0; i < numofLand; i++) {
                LandImage landImage = new LandImage();
                
                landImage.posX = (int) (i * Nor_land1.getWidth());
                landImage.image = getLandImage();
                
                listImage.add(landImage);
            }
        }
        public void update() {
//            move speed of land
            for(LandImage landImage : listImage){
                landImage.posX -=landspeed;
                landspeed += 0.5;
            }
            
//            when first image leave the screen add new image and remove first image
            LandImage firstImage = listImage.get(0);
            
            if(firstImage.posX + Nor_land1.getWidth() < 0) {
//                for last image
                firstImage.posX = listImage.get(listImage.size() -1).posX + Nor_land1.getWidth();
                
//                for first image 
                listImage.add(listImage.get(0));
                listImage.remove(0);
            }
        }

        public void paintComponent(Graphics g){
//            for-each loop image in list 
            for(LandImage landImage:listImage) {
                g.drawImage(landImage.image, landImage.posX, (int) GROUNDY -30, null);
            }
        }
        
        private BufferedImage getLandImage(){
//            random int from 0, 1, 2, 3, ... , 9
            int i = random.nextInt(10);
            switch (i) {
                case 0: return Nor_land1;
                case 1: return Nor_land3;
                
                default: 
                    return Nor_land2;
            }
        }
        
        private class LandImage {
            int posX;
            BufferedImage image;
        }
    }
    
    /*------------------------------------------ Animation Zone ------------------------------------------*/
    
    public class Animation {
        public List<BufferedImage> frames;

        private int framelist = 0;
        private int deltaTime;
        private long previousTime; 

        public  Animation(int deltaTime) {
            this.deltaTime = deltaTime;
            frames = new ArrayList<BufferedImage>();
        }

        public void update() {
            if(System.currentTimeMillis() - previousTime > deltaTime){

                framelist ++;
                if (framelist >= frames.size()){
                    framelist = 0;
                }

                previousTime = System.currentTimeMillis();
            }
        }

        public void addFrame(BufferedImage frame) {
             frames.add(frame);
        }

        public BufferedImage getFrame() {
            if (frames.size() > 0) {
                return frames.get(framelist);
            }
            return null;
        }
    }
    
    /*------------------------------------------ Resource Zone ------------------------------------------*/
    
    public class Resource {
//        Use to Draw in the Screen
        public static BufferedImage getResourceImage(String path){
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(path));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return img;
        }
    }

}
