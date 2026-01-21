import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
//problems
//For very slight collisions the projection may not detect it, but the actual physics might, or the other way around
//If the last of a colour's ball was scored by the opposite player in a legal shot, the colour doesn't change to black
//As a whole, due to difficulty in testing, the rules/winning/coloured number system may have other bugs
//The pocket "hit boxes" are such that some balls may appear to hover over the hole without scoring
//Slow moving balls can appear to clip into walls/corners, though this doesn't noticeably impact physics
public class Main extends JPanel implements MouseListener, KeyListener, Runnable, MouseMotionListener {
    public static JFrame frame;
    public static Sounds sounds;
    public static ArrayList<Ball> balls = new ArrayList<Ball>();
    public static ArrayList<Line> lines = new ArrayList<Line>();
    public static Ball cueBall, ghostCueBall, ghostObjectBall;
    public static Cue cue;
    public static Line initPath, cuePath, objectPath;
    public static int gameState = -1;
    //gameStates: 0: playing. -1: Title. -2.0: instructions. -2.1: instructions p2. -2.2: instrucitons p3. 1: win screen
    public static int shotState = 1;
    //shotStates: 0: hitting, 1: aiming, 2: cue moving forward, 3: cue strikes ball, 4: moving cue ball
    public static String turn;
    public static int startPull;
    public static long timer;
    public static double physicsFreq = 1.0/300.0;//300 hz physics/graphics
    public static int offsetX = 50;
    public static int offsetY = 45;
    public static BufferedImage table;
    public static BufferedImage cueImg;
    public static BufferedImage bgcolorImg;
    public static HashMap <String, BufferedImage> images = new HashMap<String, BufferedImage> ();
    public static int cueImgW;
    public static int cueImgL;
    public static boolean breaking, breakShot, calling;
    public static boolean redHit, blueHit, blackHit;
    public static String firstHit, winState;
    public static int calledPocket, hitPocket;
    //Initalizes images and other graphics related things. No parameters, no return
    public Main() throws IOException {
        this.setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setPreferredSize(new Dimension(1500, 800));
        //setBackground(new Color(6,42,74,100));
        Thread thread = new Thread(this);
        thread.start();
        timer = System.currentTimeMillis();
        table = ImageIO.read(new File("table2.png"));
        cueImg = ImageIO.read(new File("cue.png"));
        cueImgW = cueImg.getWidth();
        cueImgL = cueImg.getHeight();
        bgcolorImg = ImageIO.read(new File("bgcolor.png"));
        String [] imageNames = {"blue", "red", "black", "white", "blue1", "red1", "blue2", "red2", "grey1", "grey2",
                "black1", "black2", "1win", "2win", "callPocket", "-1", "-20", "-21", "-22", "-3"};
        for (int i = 0; i < imageNames.length; i++){
            images.put(imageNames[i], ImageIO.read(new File(imageNames[i] + ".png")));
        }
        sounds = new Sounds();
    }
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("8 Ball");
        Main panel = new Main();
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();
    }
    //Drawing stuff. Graphics parameter, no return
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        //Prevent the weird 125% display size issue
        g2d.scale((double) width / 1500, (double) height / 800);
        //Draw background and game state image
        g2d.drawImage(bgcolorImg,0,0,null);
        g2d.drawImage(images.get(Integer.toString(gameState)), 0,0, null);
        if (gameState == 0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //Power rectangle
            g2d.setColor(Color.GRAY);
            if (shotState == 1) g2d.fillRect(1400, 200, 50, (int) (300*cue.getPowerPercent()));
            //Call pocket image
            if (calling)
                g2d.drawImage(images.get("callPocket"), offsetX, offsetY, null);
            else
                g2d.drawImage(table, offsetX, offsetY, null);
            //Turn (number in the top right) drawing
            g2d.drawImage(images.get(turn), 1400, 50, null);
            //Draw cue and balls)
            drawBalls(g2d);
            drawCue(g2d);
        }
        if (gameState == 1){//Win screen drawing
            g2d.drawImage(images.get(winState), 0, 0, null);
        }
    }
    //This method initializes a new game. No parameters, no return.
    public void init(){
        //Reset variables
        turn = "grey1";
        firstHit = "none";
        winState = "none";
        breaking = true;
        calling = false;
        redHit = false;
        blueHit = false;
        blackHit = false;
        //ArrayList of IDs used to randomly place balls in the rack
        ArrayList<Integer> IDs = new ArrayList<>();
        for (int i = 2; i <= 14; i++){
            if (i == 8) continue;//8 ball position is set
            IDs.add(i);
        }
        breakShot = true;
        Collections.shuffle(IDs);//Shuffle IDs so the colours are randomized
        balls.clear();
        //Adding balls to the table.
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
        balls.add(new Ball(1, 260,415));
        balls.add(new Ball(15, 260,303));
        cueBall = new Ball (16, 942,359);
        balls.add(cueBall);
        cue = new Cue();
        //These lines represent the outline of the field. They are not drawn.
        lines.clear();
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
        gameState = 0;
    }
    //Draws the balls. Graphics2D object parameter, no return
    public void drawBalls(Graphics2D g2d){
        for (int i = 0; i < balls.size(); i++){//Loop through all balls and draw them
            //Anti aliasing to prevent ugly edges
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //Draw balls with correct colour. Use AffineTransform to prevent glitchy movement at slow speeds
            if (balls.get(i).getID() == 16) {
                AffineTransform tx = AffineTransform.getTranslateInstance(balls.get(i).getX() - 14 + offsetX, balls.get(i).getY() - 14 + offsetY);
                g2d.drawImage(images.get("white"), tx, null);
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
                AffineTransform tx = AffineTransform.getTranslateInstance(balls.get(i).getX() - 14 + offsetX, balls.get(i).getY() - 14 + offsetY);
                g2d.drawImage(images.get("black"), tx, null);
            }
        }
    }
    //Draws a line object. Graphics object, no return.
    public void drawLine(Graphics g, Line l){
        if (l == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        //Use antialiasing to prevent hideously ugly/staircase-like lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawLine((int) l.getX1() + offsetX, (int) l.getY1() + offsetY, (int) l.getX2() + offsetX, (int) l.getY2() + offsetY);
    }
    //Draws cue and paths. Graphics object parameter, no return.
    public void drawCue(Graphics g){
        if (!cue.getShow()) return;
        Graphics2D g2d = (Graphics2D) g.create();
        //Translate to the position of the ball (cue image is offset to one side, so the cue ball should be placed in the middle of the image)
        g2d.translate(cueBall.getX() + offsetX, cueBall.getY() + offsetY);
        //Rotate to the correct angle (rotate around cue ball, which is the centre of the cue image)
        g2d.rotate(cue.getAngle());
        //Translate by the pull back. Only need to change x since it is already rotated.
        g2d.translate(cue.getPullBack(), 0);
        if (shotState == 2){
            cue.striking();
        }
        g2d.drawImage(cueImg, -cueImgW / 2, -cueImgL / 2, null);
        //Draw lines
        drawLine(g, initPath);
        drawLine(g, cuePath);
        drawLine(g, objectPath);
        g.setColor(Color.WHITE);
        if (initPath != null) {//Draw circle at the end of the intial path
            g.drawOval((int) (initPath.getX2() - 14 + offsetX), (int) (initPath.getY2() - 14 + offsetY), 28, 28);
        }
    }
    //This method handles the changing of the number in the top right. No parameters, no return.
    public void nextTurn(){
        boolean foul = false;
        if (Collections.binarySearch(balls, cueBall) < 0){//If no white ball, foul
            balls.add(cueBall);
            foul = true;
        }
        else cue.setShow(true);
        if (!turn.contains("grey") && !turn.contains(firstHit)) foul = true;//If table isn't open and first hit was wrong, foul
        boolean redLeft = false;
        boolean blueLeft = false;
        for (int i = 0; i < balls.size(); i++){
            if (balls.get(i).getColour().equals("red")) redLeft = true;
            if (balls.get(i).getColour().equals("blue")) blueLeft = true;
        }
        System.out.println(redLeft);
        System.out.println(blueLeft);
        System.out.println(turn);
        if (blackHit){//Any situation where black was pocketed
            System.out.println(foul + " " + turn);
            //if pocketed black and foul, not supposed to hit black, or wrong pocket, the other player wins
            if (foul || !turn.contains("black") || hitPocket != calledPocket){
                if (turn.contains("1")) winState = "2win";
                else{
                    winState = "1win";
                    System.out.println("here");
                }
            }
            else{//If arrived here, then black was legally pocketed. Player wins.
                if (turn.contains("1")) winState = "1win";
                else winState = "2win";
            }
            gameState = 1;
        }
        else if (foul){//All other fouls
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
        else if (turn.contains("black")){//Didn't pocket black, didn't foul (so therefore must have correctly hit black)
            if (turn.contains("1") && redLeft) turn = "red2";
            else if (turn.contains("2") && redLeft) turn = "red1";
            else if (turn.contains("1") && blueLeft) turn = "blue2";
            else if (turn.contains("2") && blueLeft) turn = "blue1";
            else if (turn.contains("1")) turn = "black2";
            else turn = "black1";
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
            if (balls.size() < 16 && balls.contains(cueBall)) turn = "grey1";
            else turn = "grey2";
        }
        else if (turn.contains("red")){//Normal red
            if (!redHit && !blueLeft) {
                if (turn.contains("1")) turn = "black2";
                else turn = "black1";
            }
            else if (!redHit && turn.contains("1")) turn = "blue2";
            else if (!redHit && turn.contains("2")) turn = "blue1";
        }
        else if (turn.contains("blue")){//Normal blue
            if (!blueHit && !redLeft) {
                if (turn.contains("1")) turn = "black2";
                else turn = "black1";
            }
            else if (!blueHit && turn.contains("1")) turn = "red2";
            else if (!blueHit && turn.contains("2")) turn = "red1";
        }
        if (turn.contains("black")) calling = true;
        //Reset variables
        blueHit = false;
        redHit = false;
        firstHit = "none";
    }
    //Moves all the balls and checks if there are still any moving balls. Handles initial stage of next turn
    //No parameters, no return
    public void move(){
        boolean allStop = true;
        for (int i = 0; i < balls.size(); i++) {//loop through all balls and move them all. check if there are any still left moving
            balls.get(i).move();
            if (balls.get(i).getVelocity().getMagnitude() != 0) allStop = false;
        }
        if (allStop && shotState == 0){
            shotState = 1;
            cue.setPullBack(0);
            Collections.sort(balls);//Sort balls not only for binary search, but to determine the order of precedence
            // for projected collisions (only the closest one counts)
            nextTurn();
        }
    }
    //Checks ball collisions with each other. No parameters, no return
    public void checkBallCol(){
        //Loop through all balls and check every other ball for collision
        for (int i = 0; i < balls.size(); i++){
            for (int j = 0; j < balls.size(); j++){
                if(balls.get(i).checkCollision(balls.get(j)) && (balls.get(i).getID() == 16 || balls.get(j).getID() == 16)){
                    //Set firstHit if applicable
                    if (!firstHit.equals("none")) continue;//First hit can only be set once
                    if (balls.get(i).getColour().equals("red") || balls.get(j).getColour().equals("red")) firstHit = "red";
                    else if (balls.get(i).getColour().equals("blue") || balls.get(j).getColour().equals("blue")) firstHit = "blue";
                    else if (balls.get(i).getColour().equals("black") || balls.get(j).getColour().equals("black")) firstHit = "black";
                }
            }
        }
        //Loop through all collisions in the list and handle them
        Iterator<Collision> iter = Ball.collisions.iterator();
        for (int i = 0; i < Ball.collisions.size(); i++){
            Collision c = iter.next();
            if (c.getFrames() == 0) c.handle();//Only handle each collision once
            c.iterateFrame();//Keep collision in the hashset so slow moving balls don't get counted twice
            if (c.getFrames() == 4) iter.remove();
        }
    }
    //Checks ball collisions with all lines. No parameters, no return
    public void checkLineCol(){
        //Loop through all balls and lines and check collision.
        for (int i = 0; i < balls.size(); i++){
            for (int j = 0; j < lines.size(); j++){
                //If a collision occured, break, since only one collision should occur per frame per ball
                if (lines.get(j).collision(balls.get(i))) break;
            }
        }
    }
    //Checks if balls are scored. No parameters, no return
    public void checkOB(){
        //Loop through all balls in reverse order to allow for removal
        for (int i = balls.size()-1; i >= 0; i--){
            Ball b = balls.get(i);
            //Pocketed balls check
            if (b.getX() < 68 || b.getX() > 1236 || b.getY() < 68 || b.getY() > 654){
                //Set hit (pocketed) boolean variables
                if (b.getID() > 8 && (!turn.contains("grey") || (turn.contains("grey") && !blueHit))) redHit = true;
                else if (b.getID() < 8 && (!turn.contains("grey") || (turn.contains("grey") && !redHit))) blueHit = true;
                else if (b.getID() == 8){//For the 8-ball, the pocket that it was scored in must be documented as well
                    blackHit = true;
                    double x = b.getX();
                    double y = b.getY();
                    if (x < 150 && y < 150) hitPocket = 1;
                    else if (x < 150) hitPocket = 6;
                    else if (x > 1100 && y < 150) hitPocket = 3;
                    else if (x > 1100) hitPocket = 4;
                    else if (y < 150) hitPocket = 2;
                    else hitPocket = 5;
                }
                balls.remove(i);//Remove balls if scored
            }
        }
    }
    //Run physics, including ball movements, shot projection, and cue movement. No parameters, no return
    public void physics (){
        if (shotState == 3 && gameState == 0){//Striking
            //Max speed 2500 pixels/second. Cue angle is exactly reversed, so subtract pi
            cueBall.setVelocity(new Vectorio(2500*cue.getPowerPercent(), cue.getAngle() - Math.PI, true));
            shotState = 0;
            cue.setShow(false);
            sounds.playThump(cue.getPowerPercent());
            if (breaking) {
                Collections.shuffle(balls);//Shuffle the list of balls to ensure a random break every time (15! possibilities)
                breaking = false;
            }
        }
        //Projection
        if (shotState == 1 && gameState == 0) {
            boolean ballCol = false;
            //Ghost cue ball to do projection math/physics with without moving any drawn balls
            ghostCueBall = new Ball(17, cueBall.getX(), cueBall.getY());
            ghostCueBall.setVelocity(new Vectorio(1, cue.getAngle() - Math.PI, true));
            for (int i = 0; i < balls.size(); i++) {//Loop through all the balls
                if (balls.get(i).getID() == 16) continue;
                //If any of the balls are within one ball diameter from the aimed path of the cue, a collision should occur there
                if (cue.getLine().pointDistance(balls.get(i).getX(), balls.get(i).getY(), false) < 28) {
                    ballCol = true;
                    //Set the ghost object ball to the ball in question
                    ghostObjectBall = new Ball(18, balls.get(i).getX(), balls.get(i).getY());
                    int counter = 0;
                    //March ball forward until collides with ball.
                    //Counter to prevent infinite loop if, due to granularity of physics simulation, the balls didn't collide (only occurs in glancing collisions)
                    while (!ghostCueBall.checkCollision(balls.get(i)) && counter < 600) {
                        ghostCueBall.changeX(cue.getDirection().getX() * -4.5);
                        ghostCueBall.changeY(cue.getDirection().getY() * -4.5);
                        counter++;
                    }
                    //If exceeded counter, collision is glancing, so report no collision, since it would likely be skipped by the real physics anyway
                    if (counter > 595){
                        ballCol = false;
                        ghostCueBall = new Ball(17, cueBall.getX(), cueBall.getY());
                        ghostCueBall.setVelocity(new Vectorio(1, cue.getAngle() - Math.PI, true));
                        continue;
                    }
                    //If arrived at this point, ball has now been marched to contact point with the ball. Draw line to represent this
                    initPath = new Line((int) cueBall.getX(), (int) cueBall.getY(), (int) ghostCueBall.getX(), (int) ghostCueBall.getY());
                    Ball.collisions.clear();//checkCollision automatically adds collision to the hashset, but we don't want it there
                    Collision calculator = new Collision(ghostCueBall, ghostObjectBall);
                    calculator.handle();//handle collision
                    //Draw cue path and object path lines using the relative velocities after the collision as calculated by the handle method
                    cuePath = new Line((int) ghostCueBall.getX(), (int) ghostCueBall.getY(),
                            (int) (ghostCueBall.getX() + ghostCueBall.getVelocity().getX() * 100),
                            (int) (ghostCueBall.getY() + ghostCueBall.getVelocity().getY() * 100));
                    objectPath = new Line((int) ghostObjectBall.getX(), (int) ghostObjectBall.getY(),
                            (int) (ghostObjectBall.getX() + ghostObjectBall.getVelocity().getX() * 100),
                            (int) (ghostObjectBall.getY() + ghostObjectBall.getVelocity().getY() * 100));
                    break;
                }
            }
            //If no collision, draw a line of fixed lenght in the direction the cue would travel
            if (!ballCol) {
                cuePath = null;
                objectPath = null;
                initPath = new Line((int) cueBall.getX(), (int) cueBall.getY(),
                        (int) (cueBall.getX() + cue.getDirection().getX() * -500), (int) (cueBall.getY() + cue.getDirection().getY() * -500));
            }
        }
        if (shotState != 0) return;
        //If shotState is 0, run the physics as normal
        move();
        checkBallCol();
        checkLineCol();
        checkOB();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 's' && balls.size() >= 2){//Remove balls
            //Don't remove black ball or cue ball
            if (!balls.getFirst().getColour().equals("cue") && !balls.getFirst().getColour().equals("black"))
                balls.removeFirst();
            else//If cue or black, move the invalid ball to the back
                balls.add(balls.removeFirst());
        }
        if (gameState == 0 && calling){//Call pocket controls
            if (e.getKeyChar() == '1' || e.getKeyChar() == '2' || e.getKeyChar() == '3' || e.getKeyChar() == '4' ||
                    e.getKeyChar() == '5' || e.getKeyChar() == '6'){
                calledPocket = e.getKeyChar() - 48;//conversion from char value to int
                calling = false;
            }
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
    public boolean clickRectangle(MouseEvent e, int x1, int y1, int x2, int y2) {
        return (x1 <= e.getX() && e.getX() <= x2 && y1 <= e.getY() && e.getY() <= y2);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        //Controlling game states
        if (gameState == -1){
            if (clickRectangle(e, 1103, 287, 1418,412)){
                init();
            }
            if (clickRectangle(e, 1104,551, 1416, 627)){
                gameState = -20;
            }
            if (clickRectangle(e, 1104, 435, 1419, 517)){
                gameState = -3;
            }
        }
        else if (gameState == -20){
            if (clickRectangle(e, 35, 680, 352,763))
                gameState = -1;
            if (clickRectangle(e, 1159, 680, 1473,763))
                gameState = -21;
        }
        else if (gameState == -21){
            if (clickRectangle(e, 35, 680, 352,763))
                gameState = -20;
            if (clickRectangle(e, 1159, 680, 1473,763))
                gameState = -22;
        }
        else if (gameState == -22 && clickRectangle(e, 35, 680, 352,763))
            gameState = -21;
        else if (gameState == -3 && clickRectangle(e, 35, 680, 352,763))
            gameState = -1;
        else if (gameState == 1 && clickRectangle(e, 593, 657, 907,739))
            gameState = -1;
        //Calculating angle (aiming)
        if (gameState == 0 && e.getButton() == (MouseEvent.BUTTON1)){
            cue.calcAngle(cueBall, e.getX(), e.getY());
        }
        //Pulling downwards (right click pull). set start pull to current y coordinate of mouse
        if (gameState == 0 && SwingUtilities.isRightMouseButton(e) && shotState == 1){
            startPull = e.getY();
        }
        //Place ball, and make sure ball is not placed on top of another ball
        if (gameState == 0 && SwingUtilities.isLeftMouseButton(e) && shotState == 4 &&
                cueBall.squaredDistance(e.getX() - offsetX, e.getY() - offsetY) < 196){
            cueBall.setPosition(e.getX() - offsetX, e.getY() - offsetY);
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        //aiming
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 1)
            cue.calcAngle(cueBall, e.getX(), e.getY());
        //pulling back
        if (SwingUtilities.isRightMouseButton(e) && shotState == 1)
            cue.setPullBack(e.getY() - startPull);
        //moving ball around
        if (SwingUtilities.isLeftMouseButton(e) && shotState == 4)
            cueBall.setPosition(e.getX() - offsetX, e.getY() - offsetY);
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
            for (int i = 0; i < balls.size(); i++){
                if (cueBall.squaredDistance(balls.get(i).getX(), balls.get(i).getY()) < 784 && balls.get(i).getID() != 16)
                    return;
            }
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
                if (physicsRan){
                    repaint();
                    counter++;
                }
            }
            else {
                repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Thread.yield();//This entire process does not involve any waiting. Thread.sleep is inaccurate <15 ms, so this is the best I can do
            //Log
            if (System.currentTimeMillis() - timer > 1000) {
                timer = System.currentTimeMillis();
                System.out.println("One second! Graphics ran: " + counter + " times.");
                counter = 0;
            }
        }
    }
}