package GameState;

import Audio.AudioPlayer;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

public class HelpState extends GameState{

    private Background bg;
    private AudioPlayer bgMusic;

    private Font font;
    private Color titleColor;
    private Font titleFont;

    public HelpState(GameStateManager gsm){

        this.gsm = gsm;

        try {

            bg = new Background("/Backgrounds/meniu.png", 1);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {
    }

    public void update() {
        bg.update();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        g.drawString("ESC - back to menu",100,40);
        g.drawString("gameplay", 130,100);
        g.drawString("SPACE - jump", 120,150);
        g.drawString("R - sword attack",110, 170);
        g.drawString("F - kunai attack",115, 190);
        g.drawString("Arrow keys - moving",100, 210);
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ESCAPE){
            gsm.setState(GameStateManager.MENUSTATE);
        }

    }
    public void keyReleased(int k) {}
}
