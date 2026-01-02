import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

public class Main extends JPanel implements MouseListener, KeyListener, Runnable {
    public static JFrame frame;
    public static ArrayList<Ball> balls = new ArrayList<Ball>();
    public static int gameState = -1;
    public static long timer;
    public static int frames = 0;
    public static double physicsFreq = 1.0/240.0;
    public Main() {
        this.setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setPreferredSize(new Dimension(1400, 700));
        setBackground(Color.WHITE);
        Thread thread = new Thread(this);
        thread.start();
        timer = System.currentTimeMillis();
    }
    public static void main(String[] args) {
        //Line l = new Line (1, 4, 4,2);
        //System.out.println(l.pointDistance(7,5));
        JFrame frame = new JFrame("8 Ball");
        Main panel = new Main();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if (gameState == -1){
            init();
            temp();
            gameState+=1;
        }
        drawBalls(g);
    }
    public void init(){
        balls.add(new Ball(1,600,360, true));
        balls.add(new Ball(2,600,340, true));
        balls.add(new Ball(3, 618, 350, true));
        balls.add(new Ball(4, 1400,350,true));
    }
    public void temp(){
        balls.get(0).setVelocity(new Vectorio(0,Math.PI/2,true));
        balls.get(1).setVelocity(new Vectorio(0, Math.PI, true));
        balls.get(3).setVelocity(new Vectorio(-1500,0));//Max speed 3000 pixels/second
    }
    public void drawBalls(Graphics g){
        for (int i = 0; i < balls.size(); i++){
            g.drawOval((int) balls.get(i).getX(), (int) balls.get(i).getY(), 20,20);
        }
    }
    public void move(){
        for (int i = 0; i < balls.size(); i++)
            balls.get(i).move();
    }
    public void checkCol(){
        for (int i = 0; i < balls.size(); i++){
            for (int j = 0; j < balls.size(); j++){
                balls.get(i).touching(balls.get(j));
            }
        }
        //System.out.println(Ball.collisions);
        Iterator<Collision> iter = Ball.collisions.iterator();
        for (int i = 0; i < Ball.collisions.size(); i++){
            Collision c = iter.next();
            if (c.getFrames() == 0) c.handle();//Only handle each collision once
            c.iterateFrame();//Keep collision in the hashset so slow moving balls don't get counted twice
            if (c.getFrames() == 4) iter.remove();
        }
    }
    public void physics (){
        move();
        checkCol();
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void run() {
        long previous = System.nanoTime();
        double accumulator = 0.0;
        double lastFrameTime;
        long current;
        long timer = System.currentTimeMillis();
        int counter = 0;
        boolean physicsRan;
        while (true) {
            physicsRan = false;
            current = System.nanoTime();
            lastFrameTime = (current - previous)/1000000000.0;//divide by 1 billion to convert to seconds
            previous = current;
            lastFrameTime = Math.min(lastFrameTime, 0.25);//"spiral of death" prevention
            accumulator += lastFrameTime;
            while (accumulator >= physicsFreq){//Run physics as many times as necessary given how much real time has elapsed since the last frame
                physics();
                //counter++;
                accumulator -= physicsFreq;
                physicsRan = true;
            }
            Thread.yield();//This entire process does not involve any waiting. Thread.sleep is inaccurate <15 ms, so this is the best I can do
            if (physicsRan){
                repaint();
                counter++;
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer = System.currentTimeMillis();
                System.out.println("One second! Graphics ran: " + counter + " times.");
                counter = 0;
            }
            /*
            if (System.currentTimeMillis() - timer > 1000){
                timer = System.currentTimeMillis();
                System.out.println("One second! Physics ran: " + counter + " times.");
                counter = 0;
            }

             */
        }
    }
}