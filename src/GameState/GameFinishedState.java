package GameState;

import Audio.AudioPlayer;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameFinishedState extends GameState {

    private Background bg;
    private AudioPlayer bgMusic;

    private int currentChoice = 0;
    private String[] options = {
            "play again",
            "main menu",
            "exit"
    };
    private Font font;
    private Color titleColor;
    private Font titleFont;

    public GameFinishedState(GameStateManager gsm){

        this.gsm = gsm;
        init();

        try {

            bg = new Background("/Backgrounds/finish.jpg", 1);
            titleColor = new Color(255,255,255);
            titleFont = new Font("Ink free",Font.BOLD,30);
            font = new Font("Ink free", Font.PLAIN, 18);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {

        bgMusic = new AudioPlayer("/Music/credits.mp3");
        bgMusic.play();
    }

    public void update() {
        bg.update();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);


        // draw title
        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("Thank you for playing<3",0,80);

        // draw menu options
        g.setFont(font);

        for(int i = 0; i < options.length; i++) {
            if(i == currentChoice) {
                g.setColor(Color.WHITE);
            }
            else {
                g.setColor(Color.GREEN);
            }
            g.drawString(options[i], 5, 125 + i * 20);
        }


    }

    private void select() {
        if(currentChoice == 0) {
            bgMusic.stop();
            gsm.setState(GameStateManager.LEVEL1STATE);
        }
        if(currentChoice == 1) {
            bgMusic.stop();
            gsm.setState(GameStateManager.MENUSTATE);
        }
        if(currentChoice == 2) {
            System.exit(0);
        }
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ENTER){
            select();
        }
        if(k == KeyEvent.VK_UP) {
            currentChoice--;
            if(currentChoice == -1) {
                currentChoice = options.length - 1;
            }
        }
        if(k == KeyEvent.VK_DOWN) {
            currentChoice++;
            if(currentChoice == options.length) {
                currentChoice = 0;
            }
        }
    }
    public void keyReleased(int k) {}

}
