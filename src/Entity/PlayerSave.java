package com.neet.Entity;

/**
 *   Used for saving player's stats
 */
public class PlayerSave {

    private static int lives = 3;
    private static int health = 5;
    private static long time = 0;
    private static int kunais = 5;
    private static int score = 0;

    public static void init() {
        lives = 3;
        health = 5;
        kunais = 5;
        time = 0;
        score = 0;
    }

    public static int getLives() { return lives; }
    public static void setLives(int i) { lives = i; }

    public static int getKunais(){ return kunais;}
    public static void setKunai(int i) { kunais = i;}


    public static int getHealth() { return health; }
    public static void setHealth(int i) { health = i; }

    public static int getScore(){return score;}
    public static void setScore(int i) { score = i;}


}
