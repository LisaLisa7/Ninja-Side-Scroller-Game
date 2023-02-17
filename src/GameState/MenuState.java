package GameState;

import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuState extends GameState{

    private Background bg;

    private int currentChoice = 0;
    private String[] options = { "Start","Load","Score","Help","Quit"};

    private Color titleColor;
    private Font titleFont;

    private Font font;


    public MenuState(GameStateManager gsm){
        this.gsm = gsm; // toate game stateurile au nevoie de referinta la game state manager

        try{
            bg = new Background("/Backgrounds/meniu.png",1);
            //bg.setVector(-0.1,0);

            titleColor = new Color(250,128,114);
            titleFont = new Font("Ink free",Font.BOLD,50);
            font = new Font("Arial",Font.BOLD,12);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void init() {}
    public void update() {
        bg.update();
    }
    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        // draw title
        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("Unbound",60,80);

        // draw menu options
        g.setFont(font);
        for(int i = 0;i<options.length;i++)
        {
            if(i == currentChoice){
                g.setColor(new Color(250,128,114));
            } else{
                g.setColor(Color.WHITE);
            }
            g.drawString(options[i],145,140 + i * 15 );
        }

    }

    private void select(){
        if(currentChoice == 0){
            gsm.setState(GameStateManager.LEVEL1STATE);
        }
        if(currentChoice ==1){
            gsm.setState(GameStateManager.LOADSTATE);
        }
        if(currentChoice == 2){
            gsm.setState(GameStateManager.SCORESTATE);
        }
        if(currentChoice == 3){
            gsm.setState(GameStateManager.HELPSTATE);
        }
        if(currentChoice == 4){
            System.exit(0);
        }
    }

    public void keyPressed(int k) {
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
