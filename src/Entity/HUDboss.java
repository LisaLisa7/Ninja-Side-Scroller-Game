package Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HUDboss {

    private Malak boss;
    private BufferedImage image;
    private Font font;

    public HUDboss(Malak b){
        boss = b;
        try{

            image = ImageIO.read(getClass().getResourceAsStream("/HUD/hudBoss.png"));
            font = new Font("Arial",Font.PLAIN,10);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g){

        g.drawImage(image,230,10,null);
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString(boss.getHealth() + "/" + boss.getMaxHealth(),260,30);
    }
}
