package GameState;

import java.util.ArrayList;

public class GameStateManager {

    private GameState[] gameStates;
    private int currentState;

    public static final int NUMGAMESTATES = 9;
    public static final int MENUSTATE = 0;
    public static final int LEVEL1STATE = 1;
    public static final int LEVEL2STATE = 2;
    public static final int BOSSSTATE = 3;
    public static final int GAMEOVERSTATE = 4;
    public static final int GAMEFINISHEDSTATE = 5;
    public static final int HELPSTATE = 6;
    public static final int SCORESTATE = 7;
    public static final int LOADSTATE = 8;

    public GameStateManager() {

        gameStates = new GameState[NUMGAMESTATES];

        currentState = MENUSTATE;
        loadState(currentState);

    }

    private void loadState(int state) {
        if(state == MENUSTATE)
            gameStates[state] = new MenuState(this);
        else if(state == LEVEL1STATE)
            gameStates[state] = new Level1State(this);
        else if(state == LEVEL2STATE)
            gameStates[state] = new Level2State(this);
        else if(state == BOSSSTATE)
            gameStates[state] = new BossState(this);
        else if(state == GAMEOVERSTATE)
            gameStates[state] = new GameOverState(this);
        else if(state == GAMEFINISHEDSTATE)
            gameStates[state] = new GameFinishedState(this);
        else if(state == HELPSTATE)
            gameStates[state] = new HelpState(this);
        else if(state == SCORESTATE)
            gameStates[state] = new ScoreState(this);
        else if(state == LOADSTATE)
            gameStates[state] = new LoadState(this);
    }

    private void unloadState(int state) {

        gameStates[state] = null;
    }

    public void setState(int state) {
        unloadState(currentState);
        currentState = state;
        loadState(currentState);

    }

    public void update() {
        if(gameStates[currentState] != null) {
            gameStates[currentState].update();
        }
    }

    public void draw(java.awt.Graphics2D g) {
        if(gameStates[currentState] != null) {
            gameStates[currentState].draw(g);
        }
    }

    public void keyPressed(int k) {

        if(gameStates[currentState] != null)
            gameStates[currentState].keyPressed(k);
    }

    public void keyReleased(int k) {

        if(gameStates[currentState] != null)
            gameStates[currentState].keyReleased(k);
    }

}
