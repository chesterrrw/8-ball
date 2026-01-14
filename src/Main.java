import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JPanel implements MouseListener, KeyListener, Runnable, MouseMotionListener {
    public static JFrame frame;
    public static ArrayList<Ball> balls = new ArrayList<Ball>();
    public static ArrayList<Line> lines = new ArrayList<Line>();
    public static Ball cueBall;
    public static Cue cue;
    public static Line initPath;
    public static int gameState = -1;
    public static int shotState = 0;
    public static int startPull;
    public static long timer;
    public static int frames = 0;
    public static double physicsFreq = 1.0/240.0;
    public static int offsetX = 50;
    public static int offsetY = 75;
    public static BufferedImage table;
    public static BufferedImage cueImg;
    public static BufferedImage bgcolorImg;
    public static int cueImgW;
    public static int cueImgL;
    public Main() throws IOException {
        this.setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setPreferredSize(new Dimension(1600, 900));
        //setBackground(new Color(6,42,74,100));
        Thread thread = new Thread(this);
        thread.start();
        timer = System.currentTimeMillis();
        table = ImageIO.read(new File("table2.png"));
        cueImg = ImageIO.read(new File("cue.png"));
        cueImgW = cueImg.getWidth();
        cueImgL = cueImg.getHeight();
        bgcolorImg = ImageIO.read(new File("bgcolor.png"));
    }
    public static void main(String[] args) throws IOException {
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
        g.drawImage(bgcolorImg,0,0,null);
        if (gameState == -1){
            init();
            temp();
            gameState+=1;
        }
        g.drawImage(table, offsetX, offsetY, null);
        drawBalls(g);
        //drawLines(g);
        drawCue(g);
        if (gameState == 0 && shotState == 1){
            //g.drawRect
        }
    }
    public void init(){
        cueBall = new Ball (0, 1000,500, 0);
        balls.add(new Ball(1,600,374, 1));
        balls.add(new Ball(2,600,336, 1));
        balls.add(new Ball(3, 618, 350, 1));
        balls.add(new Ball(4, 1000,350,1));
        balls.add(new Ball(5, 582,350,1));
        balls.add(new Ball(6, 600,300,1));
        balls.add(new Ball(7, 200,200,1));
        balls.add(cueBall);
        cue = new Cue();
        //lines.add(new Line(100, 600,1200,600));
        //lines.add(new Line(100, 100,100,600));
        //lines.add(new Line(1200, 100,1200,600));
        //lines.add(new Line(100, 100,1200,100));
        //Square
        /*
        lines.add(new Line(400,300,500,400));
        lines.add(new Line(400,500,500,400));
        lines.add(new Line(400,300,300,400));
        lines.add(new Line(300,400,400,500));

         */


        lines.add(new Line(115, 68, 80, 32));
        lines.add(new Line(115, 68, 617, 68));
        lines.add(new Line(617, 68, 625, 38));
        lines.add(new Line(675, 38, 683, 68));
        lines.add(new Line(683, 68, 1185, 68));
        lines.add(new Line(1185, 68, 1221, 32));
        lines.add(new Line(1231,114,1267,75));
        lines.add(new Line(1231, 114, 1231, 603));
        lines.add(new Line(1231, 603, 1268, 646));
        lines.add(new Line(1185, 649, 1230, 686));
        lines.add(new Line(1185, 649, 683, 649));
        lines.add(new Line(683, 649, 675, 678));
        lines.add(new Line(627, 678, 617, 649));
        lines.add(new Line(617, 649, 116, 649));
        lines.add(new Line(116, 649, 71, 687));
        lines.add(new Line(31, 646, 69, 603));
        lines.add(new Line(69, 603, 69, 114));
        lines.add(new Line(69, 114, 33, 77));
    }
    public void temp(){
        //balls.get(0).setVelocity(new Vectorio(500,34,true));
        //balls.get(1).setVelocity(new Vectorio(0, Math.PI, true));
        //balls.get(3).setVelocity(new Vectorio(1500, Math.PI, true));//Max speed 3000 pixels/second
        //balls.get(4).setVelocity(new Vectorio(100,0, true));
        //balls.get(6).setVelocity(new Vectorio(-100,-100));

    }
    public void drawBalls(Graphics g){
        for (int i = 0; i < balls.size(); i++){
            g.setColor(Color.RED);
            if (balls.get(i).getID() == 0) g.setColor(Color.WHITE);
            g.fillOval((int) balls.get(i).getX() - 14 + offsetX, (int) balls.get(i).getY() - 14 + offsetY, 28,28);
        }
        //g.drawImage(cueImg, (int )balls.get(6).getX() + offsetX, (int) balls.get(6).getY() + offsetY, null);
    }
    public void drawLines(Graphics g){
        for (int i = 0; i < lines.size(); i++){
            Line l = lines.get(i);
            g.drawLine((int) l.getX1() + offsetX, (int) l.getY1() + offsetY, (int) l.getX2() + offsetX, (int) l.getY2() + offsetY);
        }
    }
    public void drawCue(Graphics g){
        if (!cue.getShow()) return;
        Graphics2D g2d = (Graphics2D) g.create();
        //System.out.println(cueBall.getX());
        g2d.translate(cueBall.getX() + offsetX, cueBall.getY() + offsetY);
        g2d.rotate(cue.getAngle());
        g2d.translate(cue.getPullBack(), 0);
        if (shotState == 2){
            cue.striking();
        }
        g2d.drawImage(cueImg, -cueImgW / 2, -cueImgL / 2, null);
    }
    public void move(){
        boolean allStop = true;
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).move();
            if (balls.get(i).getVelocity().getMagnitude() != 0) allStop = false;
        }
        if (allStop && shotState == 0){
            shotState = 1;
            cue.setShow(true);
            Collections.sort(balls);
        }
        //System.out.println(shotState);
    }
    public void checkBallCol(){
        for (int i = 0; i < balls.size(); i++){
            for (int j = 0; j < balls.size(); j++){
                balls.get(i).checkCollision(balls.get(j));
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
    public void checkLineCol(){
        for (int i = 0; i < balls.size(); i++){
            for (int j = 0; j < lines.size(); j++){
                if (lines.get(j).collision(balls.get(i))) break;
            }
        }
    }
    public void checkOB(){
        //System.out.println(balls.get(0).getX());
        for (int i = balls.size()-1; i >= 0; i--){
            Ball b = balls.get(i);
            //Pocketed balls check
            if (b.getX() < 64 || b.getX() > 1235 || b.getY() < 64 || b.getY() > 653){
                balls.remove(i);
                //System.out.println("here!");
            }
        }
    }
    public void physics (){
        move();
        checkBallCol();
        checkLineCol();
        checkOB();
        if (shotState == 3){
            cueBall.setVelocity(new Vectorio(3000*cue.getPowerPercent(), cue.getDirection(), true));
            System.out.println(3000*cue.getPowerPercent());
            shotState = 0;
            cue.setShow(false);
        }
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
        if (e.getButton() == (MouseEvent.BUTTON3)){
            cue.calcAngle(cueBall, e.getX(), e.getY());

        }
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 1){
            startPull = e.getY();
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && shotState == 1){
            cue.calcAngle(cueBall, e.getX(), e.getY());
        }
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 1){
            cue.setPullBack(e.getY() - startPull);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 1 && e.getY() - startPull > 10){
            shotState = 2;
        }
        else cue.setPullBack(0);
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
        boolean physicsRan = false;
        while (true) {
            if (gameState == 0) {
                physicsRan = false;
                current = System.nanoTime();
                lastFrameTime = (current - previous) / 1000000000.0;//divide by 1 billion to convert to seconds
                previous = current;
                lastFrameTime = Math.min(lastFrameTime, 0.25);//"spiral of death" prevention
                accumulator += lastFrameTime;
                while (accumulator >= physicsFreq) {//Run physics as many times as necessary given how much real time has elapsed since the last frame
                    physics();
                    //counter++;
                    accumulator -= physicsFreq;
                    physicsRan = true;
                }
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
                //System.out.println(balls.get(0).getVelocity());
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