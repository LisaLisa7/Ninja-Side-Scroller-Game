package Entity;

import Audio.AudioPlayer;
import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Player extends MapObject {

    protected static Player instance = null;

    //player stuff
    private int health;
    private int maxHealth;
    private int kunais;
    private int maxKunai;
    private static int collectedKunais;
    private boolean dead;
    private boolean deadDone = false;
    private boolean flinching;
    private long flinchTimer;
    private static int score;
    private boolean knockback;

    // kunai
    private boolean throwing;
    private int Cost;
    private int KunaiDamage;
    private ArrayList<Kunai> Kunais;

    // sword
    private boolean attacking;
    private int swordDamage;
    private int swordRange;


    // animations
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            2, 6, 3, 2, 9, 8, 5
    };


    // animations actions
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int KUNAI = 4;
    private static final int SWORD = 5;
    private static final int DYING = 6;


    // audio
    private HashMap<String, AudioPlayer> sfx;

    // constructor
    protected Player(TileMap tm) {

        super(tm);

        width = 30;
        height = 30;
        cwidth = 15;
        cheight = 20;

        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;

        facingRight = true;

        health = maxHealth = 5;
        kunais = maxKunai = 5;

        score = 0;

        Cost = 1;
        KunaiDamage = 5;
        Kunais = new ArrayList<Kunai>();

        swordDamage = 10;
        swordRange = 30;

        //load sprites

        try {

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/Sagiri.png"));

            sprites = new ArrayList<BufferedImage[]>();
            for(int i = 0; i < 7; i++) {

                BufferedImage[] bi = new BufferedImage[numFrames[i]];

                for(int j = 0; j < numFrames[i]; j++) {
                        bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
                }

                sprites.add(bi);

            }

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);


        // sound effects
        sfx = new HashMap<String, AudioPlayer>();
        sfx.put("jump", new AudioPlayer("/SFX/jump.mp3"));
        sfx.put("sword", new AudioPlayer("/SFX/sword.mp3"));
        sfx.put("throw", new AudioPlayer("/SFX/throw.mp3"));

    }

    public static Player GetInstance(TileMap tm){
        if(instance == null){
            instance = new Player(tm);
        }
        return instance;
    }

    public static void resetInstance(){
        instance = null;
    }


    /**
     * Stop all of the player's movement
     */
    public void stop() {

        left = right = up = down = flinching = attacking = jumping = throwing = false;
    }



    //health
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public void setHealth(int i) { health = i; }

    //scor
    /**
     * gain points for mercenaries kills
     */
    public void scoreKill() { score=score+5;}
    public int getScore(){return score;}
    public void setScore(int i){ score = i;}

    //kunai
    public int getKunai() { return kunais; }
    public int getMaxKunai() { return maxKunai; }
    public int getCollectedKunais(){return collectedKunais;}
    public void setKunai(int i) { kunais = i;}
    public void colectKunai(int i)
    {
        if(kunais < getMaxKunai())
            kunais = kunais + 1;
        collectedKunais++;
    }

    /**
     * @return if the death animation has played or not
     */
    // DEAD
    public boolean getDeadDone(){ return deadDone;}
    public void setDying() {
        dead = true;
    }

    public void setDead(){
        stop();
    }

    //ATAC

    public void setThrowing() {
        throwing = true;
    }
    public void setAtacking() {
        attacking = true;
    }


    public void checkAttack(ArrayList<Enemy> enemies){

        //loop through enemies
        for(int i=0;i<enemies.size();i++) {
            Enemy e = enemies.get(i);

            //sword attack
            if (attacking) {
                if (facingRight) {
                    if ((e.getx() > x) && (e.getx() < x + swordRange) && (e.gety() > y - height / 2) && (e.gety() < y + height / 2)) {
                        e.hit(swordDamage);
                    }
                }
                else{
                    if((e.getx()<x) && (e.getx() > x - swordRange) && (e.gety() > y - height/2) && (e.gety() < y + height /2)) {
                        e.hit(swordDamage);
                    }
                }
            }

            //kunais

            for(int j=0;j<Kunais.size();j++){

                if(Kunais.get(j).intersects(e)){
                    e.hit(KunaiDamage);
                    Kunais.get(j).setHit();
                    break;
                }

            }

            // check for enemy collision
            if(intersects(e)){
                if(e.getDamage() == 1)
                    hit(e.getDamage());
            }

        }

    }

    /**
     * when player gets hit by enemies
     */
    public void hit(int damage){
        if(flinching) return;
        health -= damage;
        if(health <0) health =0;
        if(health==0) dead = true;
        flinching = true;
        flinchTimer = System.nanoTime();

        if(facingRight) dx = -2;
        else dx = 2;
        dy = -3;
        knockback = true;
        falling = true;
        jumping = false;
    }

    private void getNextPosition() {

        // movement
        if(knockback) {
            dy += fallSpeed * 2;
            if(!falling) knockback = false;
            return;
        }

        if(left) {
            dx -= moveSpeed;
            if(dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        }
        else if(right) {
            dx += moveSpeed;
            if(dx > maxSpeed) {
                dx = maxSpeed;
            }
        }
        else {
            if(dx > 0) {
                dx -= stopSpeed;
                if(dx < 0) {
                    dx = 0;
                }
            }
            else if(dx < 0) {
                dx += stopSpeed;
                if(dx > 0) {
                    dx = 0;
                }
            }
        }

        // se poate misca cand ataca doar cand e in aer
        if(
                (currentAction == SWORD || currentAction == KUNAI) &&
                        !(jumping || falling)) {
            dx = 0;
        }

        // jumping
        if(jumping && !falling) {
            sfx.get("jump").play();
            dy = jumpStart;
            falling = true;
        }

        // falling
        if(falling) {

            dy += fallSpeed;

            if(dy > 0) jumping = false;
            if(dy < 0 && !jumping) dy += stopJumpSpeed;

            if(dy > maxFallSpeed) dy = maxFallSpeed;

        }


    }

    public void update() {

        // update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        // check attack has stopped
        if(currentAction == SWORD){
            if(animation.hasPlayedOnce())
                attacking = false;
        }

        if(currentAction == KUNAI){
            if(animation.hasPlayedOnce())
                throwing = false;
        }


        // kunai attack

        if(kunais > maxKunai)  kunais = maxKunai;
        if(throwing && currentAction != KUNAI){
            if(kunais >= Cost){
                kunais -= Cost;
                Kunai kn = new Kunai(tileMap,facingRight);
                kn.setPosition(x,y);
                Kunais.add(kn);
            }
        }

        //update kunais

        for(int i = 0; i<Kunais.size();i++){
            Kunais.get(i).update();
            if(Kunais.get(i).shouldRemove()){
                Kunais.remove(i);
                i--;
            }
        }

        // check done flinching
        if(flinching){
            long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 1000){
                flinching = false;
            }
        }

        // set animation
        if(attacking) {
            if(currentAction != SWORD) {
                sfx.get("sword").play();
                currentAction = SWORD;
                animation.setFrames(sprites.get(SWORD));
                animation.setDelay(60);
                width = 30;
            }
        }
        else if(throwing) {
            if (currentAction != KUNAI) {
                if (kunais > 0)
                    sfx.get("throw").play();

                currentAction = KUNAI;
                animation.setFrames(sprites.get(KUNAI));
                animation.setDelay(60);
                width = 30;

            }
        }
        else if(dead)
        {
            if (currentAction != DYING) {
                currentAction = DYING;
                animation.setFrames(sprites.get(DYING));
                animation.setDelay(200);
                width = 30;
            }
            if(animation.hasPlayedOnce())
                deadDone = true;
        }
        else if(dy > 0) {
            if(currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
                width = 30;
            }
        }
        else if(dy < 0) {
            if(currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 30;
            }
        }
        else if(left || right) {
            if(currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(40);
                width = 30;
            }
        }
        else {
            if(currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 30;
            }
        }

        animation.update();


        // set direction
        if(currentAction != SWORD && currentAction != KUNAI) {
            if(right) facingRight = true;
            if(left) facingRight = false;
        }

    }

    public void draw(Graphics2D g) {

        setMapPosition();

        // draw kunais

        for(int i = 0;i<Kunais.size();i++){
            Kunais.get(i).draw(g);
        }

        // draw player

        if(flinching) {
            if(getHealth() != 0) {
                long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
                if (elapsed / 100 % 2 == 0) {
                    return;
                }
            }
        }

        super.draw(g);

    }

}
