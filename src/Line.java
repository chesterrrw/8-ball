public class Line {

    private UnitVector direction;
    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public Line (int x1, int x2, int y1, int y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        direction = new UnitVector((x2 - x1), (y2 - y1));
    }
}
