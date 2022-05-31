package mineSweeper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class World {
    private static int width = 20;
    private static int height = 20;

    private final int AMOUNT_OF_BOMBS = 40;

    private boolean finish;
    private boolean dead;

    private Random random;

    private Tile[] [] tiles;

    private BufferedImage bomb = ImageLoader.scale(ImageLoader.loadImage("images/bomb.png"), Tile.getWidth(), Tile.getHeight());
    private BufferedImage flag = ImageLoader.scale(ImageLoader.loadImage("images/flag.png"), Tile.getWidth(), Tile.getHeight());
    private BufferedImage normal = ImageLoader.scale(ImageLoader.loadImage("images/normal.png"), Tile.getWidth(), Tile.getHeight());
    private BufferedImage pressed = ImageLoader.scale(ImageLoader.loadImage("images/pressed.png"), Tile.getWidth(), Tile.getHeight());

    public World() {
        random = new Random();

        tiles = new Tile[width] [height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i] [j] = new Tile(i, j, normal, bomb, pressed, flag);
            }
        }

        reset();
    }

    private void placeBombs(){
        for (int i = 0; i < AMOUNT_OF_BOMBS; i++) {
            placeBomb();
        }
    }

    private void placeBomb() {
        int x = random.nextInt(width);
        int y = random.nextInt(height);

        if (!tiles[x] [y].isBomb()) tiles[x] [y].setBomb(true);
        else placeBomb();
    }

    private  void setNumbers(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int mi = i - 1;
                int gi = i + 1;
                int mj = j - 1;
                int gj = j + 1;

                int amountOfBombs = 0;
                if (mi >= 0 && mj >= 0 && tiles[mi] [mj].isBomb()) amountOfBombs++;
                if (mi >= 0 && tiles[mi] [j].isBomb()) amountOfBombs++;
                if (mi >= 0 && gj < height && tiles[mi] [gj].isBomb()) amountOfBombs++;
                if (mj >= 0 && tiles[i] [mj].isBomb()) amountOfBombs++;
                if (gj < height && tiles[i] [gj].isBomb()) amountOfBombs++;
                if (gi < width && mj >= 0 && tiles[gi] [mj].isBomb()) amountOfBombs++;
                if (gi < width && tiles[gi] [j].isBomb()) amountOfBombs++;
                if (gi < width && gj < height && tiles[gi] [gj].isBomb()) amountOfBombs++;

                tiles[i] [j].setAmountOfNearBombs(amountOfBombs);
            }
        }
    }

    public void clickedLeft(int x, int y) {
        if (!dead && !finish) {
            int tileX = x / width;
            int tileY = y / height;

            if (!tiles[tileX] [tileY].isFlag()) {
                tiles[tileX] [tileY].setOpened(true);

                if (tiles[tileX] [tileY].isBomb()) dead = true;
                else {
                    if (tiles[tileX] [tileY].getAmountOfNearBombs() == 0) {
                        open(tileX,tileY);
                    }
                }
                checkFinish();
            }
        }
    }

    public void clickedRight(int x, int y){
        if (!dead && !finish) {
            int tileX = x / width;
            int tileY = y / height;
            tiles[tileX] [tileY].placeFlag();

            checkFinish();
        }
    }

    public void clickedMiddle(int x, int y) {
        reset();
    }

    private void open(int x, int y) {

        tiles[x] [y].setOpened(true);
        if (tiles[x] [y].getAmountOfNearBombs() == 0){
            int mx = x - 1;
            int gx = x + 1;
            int my = y - 1;
            int gy = y + 1;

            if (mx >= 0 && my >= 0 && tiles[mx] [my].canOpen()) open(mx,my);
            if (mx >= 0 && tiles[mx] [y].canOpen()) open(mx,y);
            if (mx >= 0 && gy < height && tiles[mx] [gy].canOpen()) open(mx,gy);
            if (my >= 0 && tiles[x] [my].canOpen()) open(x,my);
            if (gy < height && tiles[x] [gy].canOpen()) open(x,gy);
            if (gx < width && my >= 0 && tiles[gx] [my].canOpen()) open(gx,my);
            if (gx < width && tiles[gx] [y].canOpen()) open(gx,y);
            if (gx < width && gy < height && tiles[gx] [gy].canOpen()) open(gx,gy);
        }
    }

    private void checkFinish() {
        finish = true;
        outer: for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!(tiles[i] [j].isOpened() || (tiles[i] [j].isBomb() && tiles[i] [j].isFlag()))) {
                    finish = false;
                    break outer;
                }
            }
        }
    }

    public void reset() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i] [j].reset();
            }
        }

        dead = false;
        finish = false;

        placeBombs();
        setNumbers();
    }

    public void draw(Graphics g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i] [j].draw(g);
            }
        }

        if (dead) {
            g.setColor(Color.RED);
            g.drawString("You lost!", 45, 37);
            g.setColor(Color.BLACK);
            g.drawString("If you want to try one more time, press the middle button on the mouse.", 13, 57);
        } else if (finish) {
            g.setColor(Color.BLUE);
            g.drawString("You win!", 45, 37);
            g.setColor(Color.BLACK);
            g.drawString("If you want to play again, press the middle button on the mouse.", 22, 57);
        }
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }


}
