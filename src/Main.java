import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
//TODO: White ball placement,
public class Main extends JPanel implements MouseListener, KeyListener, Runnable, MouseMotionListener {
    public static JFrame frame;
    public static Sounds sounds;
    public static ArrayList<Ball> balls = new ArrayList<Ball>();
    public static ArrayList<Line> lines = new ArrayList<Line>();
    public static Ball cueBall, ghostCueBall, ghostObjectBall;
    public static Cue cue;
    public static Line initPath, cuePath, objectPath;
    public static int gameState = -1;
    public static int shotState = 1;
    //shotStates: 0: hitting, 1: aiming, 2: cue moving forward, 3: cue strikes ball, 4: moving cue ball
    public static String turn;
    public static int startPull;
    public static long timer;
    public static int frames = 0;
    public static double physicsFreq = 1.0/300.0;
    public static int offsetX = 75;
    public static int offsetY = 25;
    public static BufferedImage table;
    public static BufferedImage cueImg;
    public static BufferedImage bgcolorImg;
    public static HashMap <String, BufferedImage> images = new HashMap<String, BufferedImage> ();
    public static int cueImgW;
    public static int cueImgL;
    public static boolean breaking, breakShot;
    public static boolean redHit, blueHit, blackHit;
    public static String firstHit, winState;
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
        images.put("blue", ImageIO.read(new File ("blueball.png")));
        images.put("red", ImageIO.read(new File ("redball.png")));
        images.put("blue1", ImageIO.read(new File("blue1.png")));
        images.put("red1", ImageIO.read(new File("red1.png")));
        images.put("blue2", ImageIO.read(new File("blue2.png")));
        images.put("red2", ImageIO.read(new File("red2.png")));
        images.put("grey1", ImageIO.read(new File("grey1.png")));
        images.put("grey2", ImageIO.read(new File("grey2.png")));
        images.put("black1", ImageIO.read(new File("black1.png")));
        images.put("black2", ImageIO.read(new File("black2.png")));
        sounds = new Sounds();
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
            gameState+=1;
        }
        if (gameState == 0) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(table, offsetX, offsetY, null);
            g2d.drawImage(images.get(turn), 1425, 50, null);
            drawBalls(g2d);
            //drawLines(g);
            drawCue(g2d);
        }
        if (gameState == 1){
            g.drawImage(images.get(winState), 0, 0, null);
        }
    }
    public void init(){
        turn = "grey1";
        firstHit = "none";
        winState = "none";
        ArrayList<Integer> IDs = new ArrayList<>();
        for (int i = 1; i <= 15; i++){
            if (i == 8) continue;
            IDs.add(i);
        }
        breakShot = true;
        Collections.shuffle(IDs);//Shuffle IDs so the colours are randomized
        balls.add(new Ball(IDs.get(0),356,359));
        balls.add(new Ball(IDs.get(1),332,373));
        balls.add(new Ball(IDs.get(2), 332, 345));
        balls.add(new Ball(8, 308,359));//8-ball
        balls.add(new Ball(IDs.get(3), 308,387));
        balls.add(new Ball(IDs.get(4), 308,331));
        balls.add(new Ball(IDs.get(5), 284,373));
        balls.add(new Ball(IDs.get(6), 284,345));
        balls.add(new Ball(IDs.get(7), 284,317));
        balls.add(new Ball(IDs.get(8), 284,401));
        balls.add(new Ball(IDs.get(9), 260,359));
        balls.add(new Ball(IDs.get(10), 260,387));
        balls.add(new Ball(IDs.get(11), 260,331));
        balls.add(new Ball(IDs.get(12), 260,415));
        balls.add(new Ball(IDs.get(13), 260,303));
        cueBall = new Ball (16, 942,359);
        balls.add(cueBall);
        cue = new Cue();
        breaking = true;
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
        lines.add(new Line(115, 69, 80, 32));
        lines.add(new Line(115, 69, 617, 69));
        lines.add(new Line(617, 69, 625, 38));
        lines.add(new Line(675, 38, 683, 69));
        lines.add(new Line(683, 69, 1185, 69));
        lines.add(new Line(1185, 69, 1221, 32));
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
    public void drawBalls(Graphics2D g2d){
        for (int i = 0; i < balls.size(); i++){
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.RED);
            if (balls.get(i).getID() == 16) {
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int) balls.get(i).getX() - 14 + offsetX, (int) balls.get(i).getY() - 14 + offsetY, 28, 28);
            }
            else if (balls.get(i).getID() < 8) {
                AffineTransform tx = AffineTransform.getTranslateInstance(balls.get(i).getX() - 14 + offsetX, balls.get(i).getY() - 14 + offsetY);
                g2d.drawImage(images.get("blue"), tx, null);
            }
            else if (balls.get(i).getID() > 8) {
                AffineTransform tx = AffineTransform.getTranslateInstance(balls.get(i).getX() - 14 + offsetX, balls.get(i).getY() - 14 + offsetY);
                g2d.drawImage(images.get("red"), tx, null);
            }
            else{
                g2d.setColor(Color.BLACK);
                g2d.fillOval((int) balls.get(i).getX() - 14 + offsetX, (int) balls.get(i).getY() - 14 + offsetY, 28, 28);
            }

            //g2d.drawImage(ballImages.get("blue"), (int) balls.get(i).getX() - 14 + offsetX, (int) balls.get(i).getY() - 14 + offsetY, null);
        }
        //g.drawImage(cueImg, (int )balls.get(6).getX() + offsetX, (int) balls.get(6).getY() + offsetY, null);
    }
    public void drawLines(Graphics g){
        for (int i = 0; i < lines.size(); i++){
            Line l = lines.get(i);
            g.drawLine((int) l.getX1() + offsetX, (int) l.getY1() + offsetY, (int) l.getX2() + offsetX, (int) l.getY2() + offsetY);
        }
    }
    public void drawLine(Graphics g, Line l){
        if (l == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawLine((int) l.getX1() + offsetX, (int) l.getY1() + offsetY, (int) l.getX2() + offsetX, (int) l.getY2() + offsetY);
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
        drawLine(g, initPath);
        drawLine(g, cuePath);
        drawLine(g, objectPath);
        g.setColor(Color.WHITE);
        if (initPath != null) {
            g.drawOval((int) (initPath.getX2() - 14 + offsetX), (int) (initPath.getY2() - 14 + offsetY), 28, 28);
        }
    }
    public void changePlayer(){
        boolean foul = false;
        if (Collections.binarySearch(balls, cueBall) < 0){
            balls.add(cueBall);
            foul = true;
        }
        else cue.setShow(true);
        if (!turn.contains("grey") && !turn.contains(firstHit)) foul = true;
        boolean redLeft = false;
        boolean blueLeft = false;
        for (int i = 0; i < balls.size(); i++){
            if (balls.get(i).getColour().equals("red")) redLeft = true;
            if (balls.get(i).getColour().equals("blue")) blueLeft = true;
        }
        if (turn.contains("blue") && blackHit){
            if (foul || blueLeft) winState = "redWin";
            else winState = "blueWin";
            gameState = 1;
        }
        else if (turn.contains("red") && blackHit){
            if (foul || redLeft) winState = "blueWin";
            else winState = "redWin";
            gameState = 1;
        }
        else if (foul){
            shotState = 4;
            cueBall.setPosition(650, 359);
            cue.setShow(false);
            if (turn.equals("grey1")) turn = "grey2";
            else if (turn.equals("grey2")) turn = "grey1";
            else if (redLeft && turn.contains("1") && !turn.contains("red")) turn = "red2";
            else if (redLeft && turn.contains("2") && !turn.contains("red")) turn = "red1";
            else if (blueLeft && turn.contains("1") && !turn.contains("blue")) turn = "blue2";
            else if (blueLeft && turn.contains("2") && !turn.contains("blue")) turn = "blue1";
            else if (turn.contains("1")) turn = "black2";
            else if (turn.contains("2")) turn = "black1";
        }
        else if (!redLeft && turn.contains("red")){//red turn and all red hit in, no foul
            if (turn.contains("1")) turn = "black1";
            else turn = "black2";
        }
        else if (!blueLeft && turn.contains("blue")){//blue turn and all blue hit in, no foul
            if (turn.contains("1")) turn = "black1";
            else turn = "black2";
        }
        else if (!breakShot && turn.contains("grey")){//Initially setting colours. Cannot set colours if foul, hence foul checked first
            if (redHit && turn.contains("1")) turn = "red1";
            else if (redHit && turn.contains("2")) turn = "red2";
            else if (blueHit && turn.contains("1")) turn = "blue1";
            else if (blueHit && turn.contains("2")) turn = "blue2";
            else if (turn.equals("grey1")) turn = "grey2";
            else turn = "grey1";
        }
        else if (breakShot){//Breaking
            breakShot = false;
            if (balls.size() < 16 && balls.contains(cueBall)){
                turn = "grey1";
            }
            else turn = "grey2";
        }
        else if (turn.contains("red")){//Normal red
            if (!redHit && turn.contains("1")) turn = "blue2";
            else if (!redHit && turn.contains("2")) turn = "blue1";
        }
        else if (turn.contains("blue")){//Normal blue
            if (!blueHit && turn.contains("1")) turn = "red2";
            else if (!blueHit && turn.contains("2")) turn = "red1";
        }
        blueHit = false;
        redHit = false;
        firstHit = "none";
    }
    public void move(){
        boolean allStop = true;
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).move();
            if (balls.get(i).getVelocity().getMagnitude() != 0) allStop = false;
        }
        if (allStop && shotState == 0){
            shotState = 1;
            Collections.sort(balls);//Sort balls not only for binary search, but to determine the order of precedence
            // for projected collisions (only the closest one counts)
            changePlayer();
        }
        //System.out.println(shotState);
    }
    public void checkBallCol(){
        for (int i = 0; i < balls.size(); i++){
            for (int j = 0; j < balls.size(); j++){
                if(balls.get(i).checkCollision(balls.get(j)) && (balls.get(i).getID() == 16 || balls.get(j).getID() == 16)){
                    if (!firstHit.equals("none")) continue;//First hit can only be set once
                    if (balls.get(i).getColour().equals("red") || balls.get(j).getColour().equals("red")) firstHit = "red";
                    else if (balls.get(i).getColour().equals("blue") || balls.get(j).getColour().equals("blue")) firstHit = "blue";
                }
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
                if (b.getID() > 8) redHit = true;
                else if (b.getID() < 8) blueHit = true;
                else if (b.getID() == 8) blackHit = true;
                balls.remove(i);
                //System.out.println("here!");
            }
        }
    }
    public void physics (){
        if (shotState == 3 && gameState == 0){
            cueBall.setVelocity(new Vectorio(2500*cue.getPowerPercent(), cue.getAngle() - Math.PI, true));
            //System.out.println(3000*cue.getPowerPercent());
            shotState = 0;
            cue.setShow(false);
            if (breaking) {
                Collections.shuffle(balls);//Shuffle the list of balls to ensure a random break every time (15! possibilities)
                breaking = false;
            }
        }
        if (shotState == 1 && gameState == 0) {
            boolean ballCol = false;
            ghostCueBall = new Ball(17, cueBall.getX(), cueBall.getY());
            ghostCueBall.setVelocity(new Vectorio(1, cue.getAngle() - Math.PI, true));
            for (int i = 0; i < balls.size(); i++) {
                if (balls.get(i).getID() == 16) continue;
                if (cue.getLine().pointDistance(balls.get(i).getX(), balls.get(i).getY(), false) < 28) {
                    ballCol = true;
                    //ghostCueBall = new Ball(0, cueBall.getX(), cueBall.getY(), -1);
                    ghostObjectBall = new Ball(18, balls.get(i).getX(), balls.get(i).getY());
                    int counter = 0;
                    while (!ghostCueBall.checkCollision(balls.get(i)) && counter < 300) {//March ball forward until collides with ball
                        ghostCueBall.changeX(cue.getDirection().getX() * -5.5);
                        ghostCueBall.changeY(cue.getDirection().getY() * -5.5);
                        counter++;
                    }
                    if (counter > 295){
                        ballCol = false;
                        ghostCueBall = new Ball(17, cueBall.getX(), cueBall.getY());
                        ghostCueBall.setVelocity(new Vectorio(1, cue.getAngle() - Math.PI, true));
                        continue;
                    }
                    initPath = new Line((int) cueBall.getX(), (int) cueBall.getY(), (int) ghostCueBall.getX(), (int) ghostCueBall.getY());
                    Ball.collisions.clear();
                    Collision calculator = new Collision(ghostCueBall, ghostObjectBall);
                    calculator.handle();
                    cuePath = new Line((int) ghostCueBall.getX(), (int) ghostCueBall.getY(),
                            (int) (ghostCueBall.getX() + ghostCueBall.getVelocity().getX() * 100),
                            (int) (ghostCueBall.getY() + ghostCueBall.getVelocity().getY() * 100));
                    objectPath = new Line((int) ghostObjectBall.getX(), (int) ghostObjectBall.getY(),
                            (int) (ghostObjectBall.getX() + ghostObjectBall.getVelocity().getX() * 100),
                            (int) (ghostObjectBall.getY() + ghostObjectBall.getVelocity().getY() * 100));
                    break;
                }
            }
            if (!ballCol) {
                /*
                while (!ballCol){
                    ghostCueBall.changeX(cue.getDirection().getX() * -5.5);
                    ghostCueBall.changeY(cue.getDirection().getY() * -5.5);
                    for (int i = 0; i < lines.size(); i++){
                        if(lines.get(i).collision(ghostCueBall)){
                            ballCol = true;
                            initPath = new Line((int) cueBall.getX(), (int) cueBall.getY(), (int) ghostCueBall.getX(), (int) ghostCueBall.getY());
                        }
                    }
                }

                 */
                cuePath = null;
                objectPath = null;
                //while(!)
                initPath = new Line((int) cueBall.getX(), (int) cueBall.getY(),
                        (int) (cueBall.getX() + cue.getDirection().getX() * -500), (int) (cueBall.getY() + cue.getDirection().getY() * -500));
            }
        }
        if (shotState != 0) return;
        move();
        checkBallCol();
        checkLineCol();
        checkOB();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 's' && balls.size() >= 2){
            if (!balls.getFirst().getColour().equals("cue") && !balls.getFirst().getColour().equals("eight"))
                balls.removeFirst();
            else
                balls.add(balls.removeFirst());
        }
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
        if (e.getButton() == (MouseEvent.BUTTON1)){
            cue.calcAngle(cueBall, e.getX(), e.getY());
        }
        if (SwingUtilities.isRightMouseButton(e) && shotState == 1){
            startPull = e.getY();
        }
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 4 && cueBall.squaredDistance(e.getX() - offsetX, e.getY() - offsetX) < 196){
            cueBall.setPosition(e.getX() - offsetX, e.getY() - offsetY);
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 1){
            cue.calcAngle(cueBall, e.getX(), e.getY());
            //System.out.println("herE!");
        }
        if (SwingUtilities.isRightMouseButton(e) && shotState == 1){
            cue.setPullBack(e.getY() - startPull);
        }
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 4){
            cueBall.setPosition(e.getX() - offsetX, e.getY() - offsetY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {;
        if (SwingUtilities.isRightMouseButton(e) && shotState == 1){
            if (e.getY() - startPull > 10)
                shotState = 2;
            else
                cue.setPullBack(0);
        }
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 4){
            shotState = 1;
            cue.setShow(true);
        }
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