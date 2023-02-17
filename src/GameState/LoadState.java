package GameState;


import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LoadState extends GameState {

    private Background bg;

    private int currentChoice = 0;
    private String[] options = { "Level 1","Level 2","Boss Level"};

    private Font font;
    private Color titleColor;
    private Font titleFont;

    public LoadState(GameStateManager gsm){

        this.gsm = gsm;
        init();

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

        for(int i = 0;i<options.length;i++)
        {
            if(i == currentChoice){
                g.setColor(new Color(250,128,114));
            } else{
                g.setColor(Color.WHITE);
            }
            g.drawString(options[i],135,120 + i * 25 );
        }
    }

    private void select(){
        if(currentChoice == 0){
            gsm.setState(GameStateManager.LEVEL1STATE);
        }
        if(currentChoice ==1){
            com.neet.Entity.PlayerSave.setHealth(5);
            com.neet.Entity.PlayerSave.setKunai(5);
            com.neet.Entity.PlayerSave.setScore(0);
            gsm.setState(GameStateManager.LEVEL2STATE);
        }
        if(currentChoice == 2) {
            com.neet.Entity.PlayerSave.setHealth(5);
            com.neet.Entity.PlayerSave.setKunai(5);
            com.neet.Entity.PlayerSave.setScore(0);
            gsm.setState(GameStateManager.BOSSSTATE);
        }

    }


    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ESCAPE){
            gsm.setState(GameStateManager.MENUSTATE);
        }

        if(k== KeyEvent.VK_ENTER){
            select();
        }
        if(k == KeyEvent.VK_UP){
            currentChoice--;
            if(currentChoice == -1){
                currentChoice = options.length -1;
            }
        }
        if(k == KeyEvent.VK_DOWN){
            currentChoice++;
            if(currentChoice == options.length){
                currentChoice = 0;
            }
        }

    }
    public void keyReleased(int k) {}
}
