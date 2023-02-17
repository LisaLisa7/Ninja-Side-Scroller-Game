package GameState;

import Audio.AudioPlayer;
import Exceptions.InvalidAudioException;
import Exceptions.InvalidDataBaseExtraction;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class ScoreState extends GameState{

    private Background bg;
    private Font font;
    private Color color;
    private Color color2;
    private static ArrayList<Integer> l = new ArrayList<>();
    private static ArrayList<String> l2 = new ArrayList<>();
    private int limit = 5;


    public ScoreState(GameStateManager gsm){

        this.gsm = gsm;

        try {
            bg = new Background("/Backgrounds/meniu.png", 1);
            color = new Color(250,250,250);
            color2 = new Color(250,128,114);
            font = new Font("Arial",Font.BOLD,12);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        getdata();

    }

    public void init() {
    }

    public void update() {

        init();
        bg.update();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);
        // draw title
        g.setColor(color2);
        g.setFont(font);
        g.drawString("Score",140,20);
        g.drawString("Points",15,50);
        g.drawString("Lives left",70,50);
        g.drawString("Kunai collected", 140,50);
        g.drawString("Level", 260,50);

        g.setColor(color);

        try {
            if(l.size() == 0 || l2.size() ==0)
                throw new InvalidDataBaseExtraction("Nu au fost extrase datele");

            for (int i = 0; i < l.size(); i++) {
                if (i % 2 == 0) {
                    g.drawString(String.valueOf(l.get(i)), 20, 80 + i * 15);
                } else {
                    g.drawString(String.valueOf(l.get(i)), 95, 80 + (i - 1) * 15);
                }
            }

            for (int i = 0; i < l2.size(); i++) {
                if (l2.get(i) != null) {
                    if (i % 2 == 0) {
                        g.drawString(l2.get(i), 180, 80 + i * 15);
                    } else {
                        g.drawString(l2.get(i), 245, 80 + (i - 1) * 15);
                    }
                }

            }
        }
        catch (InvalidDataBaseExtraction e){
            System.out.println(e.getMessage());
        }


    }

    /**
     * get the data from the database
     */
    public void getdata(){
        Connection c = null;
        Statement s = null;
        String q = "SELECT * FROM score";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:score.db");
            c.setAutoCommit(false);
            s = c.createStatement();


            int health;
            int score;

            ResultSet rs = s.executeQuery("SELECT * FROM score;");
            ResultSet ord = s.executeQuery(q + " ORDER BY Points DESC");
            while(rs.next() && l.size() < 2*limit)
            {
                int unu = rs.getInt("Points");
                l.add(unu);
                int doi = rs.getInt("Lives");
                l.add(doi);
                String trei = rs.getString("Kunais");
                l2.add(trei);
                String patru = rs.getString("Level");
                l2.add(patru);

            }
            ord.close();
            rs.close();
            s.close();

            c.commit();
            c.close();
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfullyyyy");
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ESCAPE){
            l.clear();
            l2.clear();
            gsm.setState(GameStateManager.MENUSTATE);
        }

    }
    public void keyReleased(int k) {}
}
