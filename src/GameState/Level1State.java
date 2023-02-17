package GameState;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.Mercenar;
import Exceptions.InvalidAudioException;
import Main.GamePanel;
import TileMap.TileMap;
import TileMap.Background;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import com.neet.Entity.PlayerSave;
import com.neet.Entity.Teleport;

public class Level1State extends GameState {

    private TileMap tileMap;
    private Background bg;

    private Player player;

    private ArrayList<Enemy> enemies;
    private ArrayList<Kunai> randomkunais;
    private ArrayList<Explosion> explosions;

    private HUD hud;

    private Teleport teleport;

    private AudioPlayer bgMusic;

    // events
    private boolean eventFinish;
    private boolean eventDead;
    private int eventCount = 0;


    public Level1State(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }

    /**
     *  initializare obiecte
     */
    public void init() {

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/level1.gif");
        tileMap.loadMap("/Maps/level1.map");
        tileMap.setPosition(0,0);
        tileMap.setTween(1);

        bg = new Background("/Backgrounds/level1.png",0.1);

        player = player.GetInstance(tileMap);
        player.setPosition(150,300);

        populateEnemies();
        populateRandomKunais();

        explosions = new ArrayList<Explosion>();

        hud = new HUD(player);

        teleport = new Teleport(tileMap);
        teleport.setPosition(1843,220);


        bgMusic = new AudioPlayer("/Music/VanishingGrace.mp3");
        //bgMusic.play();


    }
    private void populateEnemies(){

        enemies = new ArrayList<Enemy>();

        Mercenar m;
        Point[] points = new Point[] {
                new Point(400,100),
                new Point(1000,200),
                new Point(1680,200),
                new Point(1800,200)
        };

        for(int i=0;i< points.length;i++){
            m = new Mercenar(tileMap);
            m.setPosition(points[i].x,points[i].y);
            enemies.add(m);
        }
    }

    private void populateRandomKunais(){
        randomkunais = new ArrayList<Kunai>();
        Kunai k;
        Point[] points = new Point[]{
                new Point(500,130),
                new Point(1350,285),
                new Point(1350,165),
                new Point(1740,220),
        };
        for(int i=0;i< points.length;i++){
            k = new Kunai(tileMap,true);
            k.setPosition(points[i].x,points[i].y);
            randomkunais.add(k);
        }
    }

    private void colectedKunai(){
        for(int i=0;i<randomkunais.size();i++) {
            if (player.intersects(randomkunais.get(i)) && randomkunais.get(i).shouldRemove() != true) {
                randomkunais.get(i).Remove();
                player.colectKunai(1);
            }
        }
    }

    public void update() {

        if(teleport.contains(player)) {
            eventFinish = true;
        }

        if(player.gety() > tileMap.getHeight()) {
            reset();
        }
        if(player.getHealth() == 0) {
            player.setDead();
            eventDead = true;
        }

        // update player
        player.update();
        tileMap.setPosition(GamePanel.width / 2 - player.getx(), GamePanel.height / 2 - player.gety());

        if(eventDead) eventDead();
        if(eventFinish) eventFinish();

        //attack enemies

        player.checkAttack(enemies);

        //update all enemies
        for(int i=0;i<enemies.size();i++){
            Enemy e = enemies.get(i);
            e.update();
            if(e.isDead()){
                enemies.remove(i);
                player.scoreKill();
                i--;
                explosions.add(new Explosion(e.getx(),e.gety()));
            }
        }

        //update explosions
        for(int i=0;i< explosions.size();i++){
            explosions.get(i).update();
            if(explosions.get(i).shouldRemove())
            {
                explosions.remove(i);
                i--;
            }
        }

        teleport.update();
        colectedKunai();

    }
    public void draw(Graphics2D g) {
        //draw bg
        bg.draw(g);


        // draw tilemap
        tileMap.draw(g);

        for(int i=0;i<randomkunais.size();i++) {
            if (randomkunais.get(i).shouldRemove() == false)
                randomkunais.get(i).draw(g);
        }

        // draw player
        player.draw(g);

        // draw enemies
        for(int i=0;i< enemies.size();i++){
            enemies.get(i).draw(g);
        }

        //draw explosions
        for(int i=0;i<explosions.size();i++){
            explosions.get(i).setMapPosition((int)tileMap.getx(),(int)tileMap.gety());
            explosions.get(i).draw(g);
        }

        //draw hud

        hud.draw(g);

        //draw teleport

        teleport.draw(g);

    }
    public void keyPressed(int k) {
        if(k == KeyEvent.VK_LEFT) player.setLeft(true);
        if(k == KeyEvent.VK_RIGHT) player.setRight(true);
        if(k == KeyEvent.VK_SPACE) player.setJumping(true);
        if(k == KeyEvent.VK_R) player.setAtacking();
        if(k == KeyEvent.VK_F) player.setThrowing();
        if(k == KeyEvent.VK_ESCAPE) {
            bgMusic.stop();
            player.resetInstance();
            gsm.setState(GameStateManager.MENUSTATE);
        }
    }
    public void keyReleased(int k) {
        if(k == KeyEvent.VK_LEFT) player.setLeft(false);
        if(k == KeyEvent.VK_RIGHT) player.setRight(false);
        if(k == KeyEvent.VK_SPACE) player.setJumping(false);

    }

    ///////////////////////////////////////////////////////
//////////////////// EVENTS
///////////////////////////////////////////////////////

    /**
     * respawn player at the start
     */
    private void reset() {

        player.setPosition(150, 300);
        if(player.getHealth() >0)
            player.setHealth(player.getHealth() - 1);
    }


    /**
     * player has died
     */
    private void eventDead() {

        if (player.getHealth() == 0) {
            player.setDying();
            if(player.getDeadDone()) {
                bgMusic.stop();
                setScore();
                player.resetInstance();
                gsm.setState(GameStateManager.GAMEOVERSTATE);
            }
        }
    }


    /**
     * the level is finished
     */
    private void eventFinish() {

        player.stop();
        bgMusic.stop();
        PlayerSave.setHealth(player.getHealth());
        PlayerSave.setKunai(player.getKunai());
        PlayerSave.setScore(player.getScore());
        player.resetInstance();
        gsm.setState(GameStateManager.LEVEL2STATE);

    }

    /**
     *  add the data in the database
     */
    public void setScore()
    {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:score.db");
            c.setAutoCommit(false);

            int health = player.getHealth();
            int score = player.getScore();

            String sql = "INSERT INTO SCORE (Points,Lives,Kunais,Level) " +
                    "VALUES (?,?,?,?)";

            PreparedStatement update = c.prepareStatement(sql);
            update.setInt(1,score);
            update.setInt(2,health);
            update.setString(3,String.valueOf(player.getCollectedKunais()));
            update.setString(4,"Level 1");

            update.executeUpdate();
            update.close();
            c.commit();
            c.close();
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }

}
