package com.neet.Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Entity.Animation;
import TileMap.TileMap;
import Entity.MapObject;

public class Teleport extends MapObject {

    private BufferedImage[] sprites;

    public Teleport(TileMap tm) {
        super(tm);
        facingRight = true;
        width = height = 40;
        cwidth = 20;
        cheight = 40;
        try {
            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/Teleport.gif"));
            sprites = new BufferedImage[9];
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
            }
            animation = new Animation();
            animation.setFrames(sprites);
            animation.setDelay(30);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        animation.update();
    }

    public void draw(Graphics2D g) {
        setMapPosition();
        super.draw(g);
    }

}
