package Entity;

import TileMap.Tile;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Malak extends Enemy {

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;

    private int kunais;
    private int maxKunai;

    // kunai
    private boolean throwing;
    private int Cost;
    private int KunaiDamage;
    private ArrayList<Kunai> Kunais;

    // sword
    private boolean attacking;
    private int swordDamage;
    private int swordRange;

    private boolean deadDone;

    // animations
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            3, 6, 3, 2, 7, 8, 6
    };

    // animation actions
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int KUNAI = 4;
    private static final int SWORD = 5;
    private static final int DYING = 6;


    public Malak(TileMap tm, Player p){
        super(tm);
        player = p;

        width = 30;
        height = 30;
        cwidth = 15;
        cheight = 20;

        health = maxHealth = 50;
        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;

        swordDamage = 2;
        swordRange = 20;
        KunaiDamage = 1;


        Kunais = new ArrayList<Kunai>();

        try {

            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/Sprites/Enemies/Malak.png"
                    )
            );

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
            e.printStackTrace();
        }

        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);


    }

    public void stop() {

        left = right = up = down = flinching = attacking = jumping = attacking = throwing = false;
    }

    //health
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public void setHealth(int i) { health = i; }

    //kunai
    public int getKunai() { return kunais; }
    public int getMaxKunai() { return maxKunai; }
    public void setKunai(int i) { kunais = i;}

    // DEAD
    public boolean getDeadDone(){ return deadDone;}
    public void setDying() {
        dead = true;
        //stop();
    }

    public void setDead(){
        health = 0;
        stop();
    }

    //ATAC

    public void setThrowing() {
        throwing = true;
    }
    public void setAtacking() {
        attacking = true;
    }

    public int getDamage() { return swordDamage;}

    public void checkAttack(Player player){

            //sword attack
            if (attacking) {
                if (facingRight) {
                    if ((player.getx() > x) && (player.getx() < x + swordRange) && (player.gety() > y - height / 2) && (player.gety() < y + height / 2)) {
                        player.hit(swordDamage);

                    }
                }
                else{
                    if((player.getx()<x) && (player.getx() > x - swordRange) && (player.gety() > y - height/2) && (player.gety() < y + height /2)) {
                        player.hit(swordDamage);
                    }
                }
            }

            //kunais

            for(int j=0;j<Kunais.size();j++) {

                if (Kunais.get(j).intersects(player)) {
                    player.hit(KunaiDamage);
                    Kunais.get(j).setHit();
                    break;
                }

            }
    }

    public void hit(int damage){
        if(flinching) return;
        health -= damage;
        if(health <0) health =0;
        if(health==0) dead = true;
        flinching = true;
        flinchTimer = System.nanoTime();
    }

    private void getNextPosition() {

        // movement

        if(player.getx() > getx())
            facingRight = true;
        else
            facingRight = false;


        if(player.getx() < getx() - 60 )
            left = true;
        else
            left = false;

        if(player.getx() > getx() + 60)
            right = true;
        else
            right = false;



        if(player.gety() < gety())
            jumping = true;

        if((player.getx() + swordRange == getx() || player.getx() - swordRange == getx()) && player.gety() == gety() && throwing == false && player.currentAction!=SWORD && isDead()==false) {
            setAtacking();
        }



        if(player.gety() == gety() && animation.hasPlayedOnce() && isDead()==false)
            setThrowing();

        if(left ) {
            dx -= moveSpeed;
            if (dx < -maxSpeed) {
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

        // jumping
        if(jumping && !falling) {
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
        if (currentAction == SWORD) {
            if (animation.hasPlayedOnce())
                attacking = false;
        }

        if (currentAction == KUNAI) {
            if (animation.hasPlayedOnce())
                throwing = false;
        }

        // kunai attack

        if (throwing && currentAction != KUNAI) {
                //kunais -= Cost;
                Kunai kn = new Kunai(tileMap, facingRight);
                kn.setPosition(x, y);
                Kunais.add(kn);
        }

        //update kunais

        for (int i = 0; i < Kunais.size(); i++) {
            Kunais.get(i).update();
            if (Kunais.get(i).shouldRemove()) {
                Kunais.remove(i);
                i--;
            }
        }

        // check done flinching
        if (flinching) {
            long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
            if (elapsed > 1000) {
                flinching = false;
            }
        }

        // set animation
        if (attacking) {
            if (currentAction != SWORD) {
                currentAction = SWORD;
                animation.setFrames(sprites.get(SWORD));
                animation.setDelay(80);
                width = 30;
            }
        } else if (throwing) {
            if (currentAction != KUNAI) {
                currentAction = KUNAI;
                animation.setFrames(sprites.get(KUNAI));
                animation.setDelay(80);
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

        else if (dy > 0) {
            if (currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
                width = 30;
            }
        } else if (dy < 0) {
            if (currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 30;
            }
        } else if (left || right) {
            if (currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(40);
                width = 30;
            }
        } else {
            if (currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 30;
            }
        }

        animation.update();

        if(right) facingRight = true;
        if(left) facingRight = false;

        // set direction
        if (currentAction != SWORD && currentAction != KUNAI) {
            if (right) facingRight = true;
            if (left) facingRight = false;
        }
    }

    public void draw(Graphics2D g) {

        setMapPosition();

        // draw kunais
        for(int i = 0;i<Kunais.size();i++){
            Kunais.get(i).draw(g);
        }

        super.draw(g);
    }
}
