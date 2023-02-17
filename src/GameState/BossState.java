package GameState;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.Mercenar;
import Main.GamePanel;
import TileMap.TileMap;
import TileMap.Background;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import com.neet.Entity.Teleport;

public class BossState extends GameState {

    private TileMap tileMap;
    private Background bg;

    private Player player;
    private Malak boss;

    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private ArrayList<Kunai> randomkunais;

    private HUD hud;
    private HUDboss hud2;

    private Teleport teleport;

    private AudioPlayer bgMusic;

    // events
    private boolean eventFinish;
    private boolean eventDead;


    public BossState(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    public void init() {

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/boss.png");
        tileMap.loadMap("/Maps/boss.map");
        tileMap.setPosition(0, 0);
        tileMap.setTween(1);

        bg = new Background("/Backgrounds/meniu.png", 0.1);

        player = player.GetInstance(tileMap);
        player.setPosition(280, 200);

        // get previous stats
        player.setHealth(com.neet.Entity.PlayerSave.getHealth());
        player.setKunai(com.neet.Entity.PlayerSave.getKunais());
        player.setScore(com.neet.Entity.PlayerSave.getScore());


        boss = new Malak(tileMap,player);
        boss.setPosition(320,200);

        enemies = new ArrayList<Enemy>();
        enemies.add(boss);

        populateEnemies();
        explosions = new ArrayList<Explosion>();

        populateRandomKunais();

        hud = new HUD(player);
        hud2 = new HUDboss(boss);

        bgMusic = new AudioPlayer("/Music/boss.mp3");
        //bgMusic.play();

    }

    private void populateEnemies() {

        Mercenar m;
        Point[] points = new Point[]{
                new Point(250, 70),
                new Point(400, 70),
        };

        for (int i = 0; i < points.length; i++) {
            m = new Mercenar(tileMap);
            m.setPosition(points[i].x, points[i].y);
            enemies.add(m);
        }

    }

    private void populateRandomKunais(){
        randomkunais = new ArrayList<Kunai>();
        Kunai k;
        Point[] points = new Point[]{
                new Point(200,130),
                new Point(400,130),
                new Point(50,190),
                new Point(550,190),
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

        if(player.getHealth() == 0) {
            player.setDead();
            eventDead = true;
        }

        if(boss.getHealth() == 0){
            boss.setDead();
            eventFinish = true;
        }

        // update player
        player.update();
        boss.update();

        tileMap.setPosition(GamePanel.width / 2 - player.getx(), GamePanel.height / 2 - player.gety());

        if(eventDead) eventDead();
        if(eventFinish) eventFinish();

        boss.checkAttack(player);
        player.checkAttack(enemies);


        //update all enemies
        for(int i=1;i<enemies.size();i++){
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
            if(explosions.get(i).shouldRemove()){
                explosions.remove(i);
                i--;
            }
        }

        colectedKunai();

    }

    public void draw(Graphics2D g) {
        //draw bg
        bg.draw(g);

        // draw tilemap
        tileMap.draw(g);

        // draw player
        player.draw(g);

        boss.draw(g);

        for(int i=0;i<randomkunais.size();i++) {
            if (randomkunais.get(i).shouldRemove() == false)
                randomkunais.get(i).draw(g);
            if(player.getKunai() == 0)
                randomkunais.get(i).Respawn();
        }

        // draw enemies
        for (int i = 1; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }


        //draw explosions
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).setMapPosition((int) tileMap.getx(), (int) tileMap.gety());
            explosions.get(i).draw(g);
        }

        //draw hud

        hud.draw(g);
        hud2.draw(g);

    }

    public void keyPressed(int k) {
        if (k == KeyEvent.VK_LEFT) player.setLeft(true);
        if (k == KeyEvent.VK_RIGHT) player.setRight(true);
        if (k == KeyEvent.VK_SPACE) player.setJumping(true);
        if (k == KeyEvent.VK_R) player.setAtacking();
        if (k == KeyEvent.VK_F) player.setThrowing();
        if(k == KeyEvent.VK_ESCAPE) {
            bgMusic.stop();
            player.resetInstance();
            gsm.setState(GameStateManager.MENUSTATE);
        }
    }

    public void keyReleased(int k) {
        if (k == KeyEvent.VK_LEFT) player.setLeft(false);
        if (k == KeyEvent.VK_RIGHT) player.setRight(false);
        if (k == KeyEvent.VK_SPACE) player.setJumping(false);

    }

    ///////////////////////////////////////////////////////
//////////////////// EVENTS
///////////////////////////////////////////////////////


    /**
     * player has died
     */
    private void eventDead() {
        player.setDying();
        if(player.getDeadDone()) {
            bgMusic.stop();
            player.resetInstance();
            gsm.setState(GameStateManager.GAMEOVERSTATE);
        }
    }

    /**
     *  the level is finished
     */
    private void eventFinish() {

        bgMusic.stop();
        boss.setDying();
        com.neet.Entity.PlayerSave.setHealth(player.getHealth());

        if(boss.getDeadDone())
        {
            player.setScore(player.getScore() + 50);
            com.neet.Entity.PlayerSave.setScore(player.getScore());
            setScore();

            player.resetInstance();
            gsm.setState(GameStateManager.GAMEFINISHEDSTATE);
        }
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

            int health = com.neet.Entity.PlayerSave.getHealth();
            int score = com.neet.Entity.PlayerSave.getScore();

            String sql = "INSERT INTO SCORE (Points,Lives,Kunais,Level) " +
                    "VALUES (?,?,?,?)";

            PreparedStatement update = c.prepareStatement(sql);
            update.setInt(1,score);
            update.setInt(2,health);
            update.setString(3,String.valueOf(player.getCollectedKunais()));
            update.setString(4,"Boss Level");

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
