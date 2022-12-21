package snake2;

import java.io.*; 
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class Snake2 extends JFrame implements Runnable {

    static final int numRows = 21;
    static final int numColumns = 21;
    static final int XBORDER = 40;
    static final int YBORDER = 60;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + numColumns*30;
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + numRows*30;
    
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    final int EMPTY = 0;
    final int SNAKE = 1;
    final int BAD_BOX = 2;
    final int BLUE_BOX = 3;
    final int YELLOW_BOX = 4;
    int board[][];

    int currentRow;
    int currentColumn;
    int columnDir;
    int rowDir;
    boolean inProgress;
    boolean gameOver;
    int timeCount;
    int score;
    int highScore;
    int blueboxRow = 3;
    int blueboxColumn = 4;
    int yellowboxRow;
    int yellowboxColumn;
    boolean snakeyellow;
    
    static Snake2 frame;
    public static void main(String[] args) {
        frame = new Snake2();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Snake2() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    inProgress = true;
                    columnDir = 0;
                    rowDir = -1;                    
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    inProgress = true;
                    columnDir = 0;
                    rowDir = 1;                                       
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    inProgress = true;
                    columnDir = -1;
                    rowDir = 0;                                        
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    inProgress = true;
                    columnDir = 1;
                    rowDir = 0;                                        
                }

                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }

 

////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.green);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.black);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        
        g.setColor(Color.blue);
//horizontal lines
        for (int zi=1;zi<numRows;zi++)
        {
            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
        }
//vertical lines
        for (int zi=1;zi<numColumns;zi++)
        {
            g.drawLine(getX(0)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(0)+zi*getWidth2()/numColumns,getY(getHeight2())  );
        }
        
    //     board[blueboxRow][blueboxColumn] == BLUE_BOX
                
                    g.setColor(Color.blue);
                    g.fillRect(getX(0)+blueboxRow*getWidth2()/numColumns,
                    getY(0)+blueboxColumn*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                  
                    g.setColor(Color.yellow);
                    g.fillRect(getX(0)+yellowboxRow*getWidth2()/numColumns,
                    getY(0)+yellowboxColumn*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
        
        
        
        
//Display the objects of the board
        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
                if (board[zrow][zcolumn] == SNAKE)
                {
                    g.setColor(Color.black);
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                }  
                else if (board[zrow][zcolumn] == BAD_BOX)
                {
                    g.setColor(Color.red);
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                }  
            }
        }
                 g.setColor(Color.black);
        g.setFont (new Font ("Boulder",Font.PLAIN, 25));             
        g.drawString("Score= "+score, 140, 85 );
            
        g.setColor(Color.black);
        g.setFont (new Font ("Boulder",Font.PLAIN, 25));             
        g.drawString("High Score= "+highScore, 400, 85 );
        
        
        
        if (gameOver)
        {
            g.setColor(Color.black);
            g.setFont (new Font ("Boulder",Font.PLAIN, 80));             
            g.drawString("Game Over", 170, 400 );
        }
        gOld.drawImage(image, 0, 0, null);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            
            
            double seconds = .2;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        timeCount = 0;
        gameOver = false;
        inProgress = false;
//Allocate memory for the 2D array that represents the board.
        board = new int[numRows][numColumns];
//Initialize the board to be empty.
        for (int zrow = 0;zrow < numRows;zrow++)
        {
            for (int zcolumn = 0;zcolumn < numColumns;zcolumn++)
                board[zrow][zcolumn] = EMPTY;
        }
 
        currentRow = numRows/2;
        currentColumn = numColumns/2;
        board[currentRow][currentColumn] = SNAKE;

        columnDir = 0;
        rowDir = 0;
        score = 0;
        blueboxRow = (int)(Math.random()*numRows);
        blueboxColumn = (int)(Math.random()*numColumns); 
        yellowboxRow = (int)(Math.random()*numRows);
        yellowboxColumn = (int)(Math.random()*numColumns); 
       
        
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            reset();
        }
        
        if (!inProgress)
            return;
        if (gameOver)
            return;
        snakeyellow = false;
                
        if (timeCount % 15 == 14)
        {
            int zrow = (int)(Math.random()*numRows);
            int zcol = (int)(Math.random()*numRows);
            board[zrow][zcol] = BAD_BOX;
            
          //  blueboxRow = (int)(Math.random()*numRows);
          //  blueboxColumn = (int)(Math.random()*numColumns);                                                 
        }
        if (timeCount % 30 == 29)
        {                       
            blueboxRow = (int)(Math.random()*numRows);
            blueboxColumn = (int)(Math.random()*numColumns);  
            yellowboxRow = (int)(Math.random()*numRows);
            yellowboxColumn = (int)(Math.random()*numColumns); 
        }
        
        
         board[blueboxRow][blueboxColumn] = BLUE_BOX;  
        
        if (currentColumn + columnDir < 0)
        {
          currentColumn = numColumns;
        }
        else if (currentColumn + columnDir > numColumns-1)
        {
            currentColumn = -1;
        }
        else if (currentRow + rowDir < 0)
        {
           currentRow = numRows;
        }
        else if (currentRow + rowDir > numRows-1)
        {
            currentRow = -1;
        }
        
        
        if (board[currentRow+rowDir][currentColumn+columnDir] == SNAKE)
        {
            gameOver = true;
        }
        else if (board[currentRow+rowDir][currentColumn+columnDir] == BAD_BOX)
        {
            gameOver = true;
        }
        if (currentRow == blueboxRow && currentColumn == blueboxColumn)
        {
            score+=10;
            blueboxRow = (int)(Math.random()*numRows);
            blueboxColumn = (int)(Math.random()*numColumns); 
         
        }
        if (board[currentRow+rowDir][currentColumn+columnDir] == YELLOW_BOX)
        {
           board[numRows][numColumns] = EMPTY;
         
        }
        else
        {
            currentRow += rowDir;
            currentColumn += columnDir;        
            board[currentRow][currentColumn] = SNAKE;
            score++;
        }
        
         if(score >= highScore)
            highScore = score;
    if (currentRow == yellowboxRow && currentColumn == yellowboxColumn)  
    {
        for(int z=0;z<numRows;z++)
        {
            for(int i=0;i<numColumns;i++)
            {
                
                if (board[z][i] == SNAKE)   
                {
                   board[z][i] = EMPTY;
                }   
                
            }
     
            
        }
        
    }  
        
        
        timeCount++;
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }


/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE );
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
}
