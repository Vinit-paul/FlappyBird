import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;            
    int boardHeight = 640;

    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird class  -> settings of height and width
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 34;
    int birdHeight = 24;                 

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //pipe class -> setting of height and width
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;
    
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed 
    int velocityY = 0; //move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;       //to store the random height of pipes
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;    //for status of game
    double score = 0;   //for the start of game score is set

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));  //size of game window
        // setBackground(Color.blue);
        setFocusable(true);   //function to keep focus over the action from keyboard
        addKeyListener(this);

        //load images to a variable
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        
        bird = new Bird(birdImg);  //bird image object
        pipes = new ArrayList<Pipe>();   //pipe array object

        //place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {    //printing of pipes image in every sec            @Override
            public void actionPerformed(ActionEvent e) {
              // Code to be executed
              placePipes();
            }
        });
        placePipeTimer.start();    //print of pipes
        
		//game timer
		gameLoop = new Timer(1000/60, this); //print of background image in a loop of 60 times per seconds 
        gameLoop.start();
	}
    
    void placePipes() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;   //opening space for the bird to pass
    
        Pipe topPipe = new Pipe(topPipeImg);   //top pipe random function
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
    
        Pipe bottomPipe = new Pipe(bottomPipeImg);        //bottom pipe random function
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    //call of grapics function
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
     //giving each image its grapics (height ,width,color,styles)
	public void draw(Graphics g) {
         //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

         //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        
	}
    //movements of pipes faster with the increase of loop round
    public void move() {
        //bird
        velocityY += gravity;  
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //maximum limit of bird to fly

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;                       //setting score system (+0.5 -> at  time it will cross 2 pipe for 1 point -> 2*0.5)
                pipe.passed = true;   
            }

            if (collision(bird, pipe)) {      //type-1 -to game over
                gameOver = true;             //if collision happens -> game over
            }
        }

        if (bird.y > boardHeight) {         //type-2 - to game over
            gameOver = true;                //restrictions for bird's height to fly
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        move();
        repaint();
        if (gameOver) {                   //as soon as game over ..the loop of pipe stops
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {     //action key space_bar is assigned to play
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            
            velocityY = -9;        //downfall of bird image 

            if (gameOver) {
                               //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
