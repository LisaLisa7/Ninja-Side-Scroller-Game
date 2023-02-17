package Entity.Enemies;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class  Mercenar extends Enemy {

    private BufferedImage[] sprites;

    public Mercenar(TileMap tm){
        super(tm);
        moveSpeed = 0.3;
        maxSpeed = 0.8;
        fallSpeed = 0.2;
        maxFallSpeed = 10.0;

        width = 30; // pt tiles
        height = 30;
        cwidth = 20; // real width
        cheight = 20;

        health = maxHealth = 2;
        damage = 1;

        // load sprites

        try{

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/mercenar.png"));


            sprites = new BufferedImage[3];
            for(int i=0; i<sprites.length;i++){
                sprites[i] = spritesheet.getSubimage(i*width,0,width,height);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        animation = new Animation();
        animation.setFrames(sprites);
        animation.setDelay(300);

        left = true;
        facingRight = false;

    }

    private void getNextPosition(){

        if(left) dx = -moveSpeed;
        else if(right) dx = moveSpeed;
        else dx = 0;
        if(falling) {
            dy += fallSpeed;
            if(dy > maxFallSpeed) dy = maxFallSpeed;
        }
        if(jumping && !falling) {
            dy = jumpStart;
        }
    }

    public void update() {

        //update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp,ytemp);

        // check flinching
        if(flinching){
            long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 400)
                flinching = false;
        }

        getNextPosition();
        checkTileMapCollision();
        calculateCorners(x, ydest + 1);
        if(!bottomLeft) {
            left = false;
            right = facingRight = true;
        }
        if(!bottomRight) {
            left = true;
            right = facingRight = false;
        }
        setPosition(xtemp, ytemp);

        if(right && dx == 0) {
            right = false;
            left = true;
            facingRight = false;
        }
        else if(left && dx == 0) {
            right = true;
            left = false;
            facingRight = true;
        }



        // update animation
        animation.update();
    }

    public void draw(Graphics2D g){

        //if(notOnScreen()) return;

        setMapPosition();

        super.draw(g);
    }

}
