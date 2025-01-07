import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
       int boardWidth = 500;
       int boardHeight = 800;

       JFrame frame=new JFrame("Flappy Bird");
       //frame.setVisible(true);
       frame.setSize(boardWidth,boardHeight);
       frame.setLocationRelativeTo(null);
       frame.setResizable(true);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       FlappyBird flappybird=new FlappyBird();
       frame.add(flappybird);
       frame.pack();
       flappybird.requestFocus();
       frame.setVisible(true);
    }
}
