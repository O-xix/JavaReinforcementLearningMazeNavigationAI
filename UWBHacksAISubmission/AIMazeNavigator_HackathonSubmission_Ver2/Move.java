public class Move {
    private int start_x_position;
    private int start_y_position;
    private int end_x_position;
    private int end_y_position;
    private boolean hits_wall;
    private double chance;
    
    public Move() {
        this.start_x_position = 0;
        this.start_y_position = 0;
        this.end_x_position = 0;
        this.end_y_position = 0;
        this.hits_wall = false;
        this.chance = 0.0;
    }

    public Move(int start_x_position, int start_y_position) {
        this.start_x_position = start_x_position;
        this.start_y_position = start_y_position;
        this.end_x_position = 0;
        this.end_y_position = 0;
        this.hits_wall = false;
        this.chance = 0.0;
    }

    public Move(int start_x_position, int start_y_position, int end_x_position, int end_y_position, boolean hits_wall) {
        this.start_x_position = start_x_position;
        this.start_y_position = start_y_position;
        this.end_x_position = end_x_position;
        this.end_y_position = end_y_position;
        this.hits_wall = hits_wall;
        this.chance = 0.0;
    }

    public Move(int start_x_position, int start_y_position, int end_x_position, int end_y_position, boolean hits_wall, double chance) {
        this.start_x_position = start_x_position;
        this.start_y_position = start_y_position;
        this.end_x_position = end_x_position;
        this.end_y_position = end_y_position;
        this.hits_wall = hits_wall;
        this.chance = chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
    public void setEnd_x_position(int end_x_position) {
        this.end_x_position = end_x_position;
    }
    public void setEnd_y_position(int end_y_position) {
        this.end_y_position = end_y_position;
    }
    public void setHits_wall(boolean hits_wall) {
        this.hits_wall = hits_wall;
    }
    public void setStart_x_position(int start_x_position) {
        this.start_x_position = start_x_position;
    }
    public void setStart_y_position(int start_y_position) {
        this.start_y_position = start_y_position;
    }


    public double getChance() {
        return chance;
    }
    public int getEnd_x_position() {
        return end_x_position;
    }
    public int getEnd_y_position() {
        return end_y_position;
    }
    public int getStart_x_position() {
        return start_x_position;
    }
    public int getStart_y_position() {
        return start_y_position;
    }
    public boolean getHits_wall() {
        return hits_wall;
    }

    public boolean equals(Move that) {
        return (this.start_x_position == that.start_x_position && this.end_x_position == that.end_x_position  && this.start_y_position == that.start_y_position);
    }
}
