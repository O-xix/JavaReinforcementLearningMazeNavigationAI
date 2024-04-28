import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.imageio.*;
import javax.swing.*;

/**
 * Write a description of class Maze here.
 *
 * @author Jaxet Rasaid
 * @version 2.0.0   -   4/25/2024
 */
public class Maze {
    int number_of_attempts = 0;
    int size = 6;
    int delayTimer = 25;
    String[][] maze_layout;
    DrawingPanel maze = new DrawingPanel(768, 768);
    int currentXpos = 1;
    int currentYpos = 1;
    int squareSize;
    Run currentRun;
    boolean badGeneration = false;
    boolean outsideOfAI = false;
    ArrayList<Run> attempts = new ArrayList<Run>();
    ArrayList<Move> memory = new ArrayList<Move>();
    

    public Maze() {
        Random random = new Random();
        size = random.nextInt(10) + 5;
        maze_layout = new String[size][size];
        squareSize = maze.getHeight() / size;
        /*
        Scanner scanner = new Scanner(System.in);
        System.out.println("Is there a certain delay, in milliseconds, that you would like the program's instuctions to run at? Default value will be set at 25 milliseconds.");
        if (scanner.hasNextInt()) {
            delayTimer = scanner.nextInt(); 
            System.out.print("The delay is set to " + delayTimer + " milliseconds. ");
        } else {
            System.out.println("The input is not an integer.");
        }
        */

        String userInput = JOptionPane.showInputDialog("Is there a certain delay, in milliseconds, that you would like the program's instuctions to run at? Default value will be set at 25 milliseconds.");
        Scanner scanner = new Scanner(userInput);
        if (scanner.hasNextInt()) {
            delayTimer = scanner.nextInt();
            System.out.println("The delay is set to " + delayTimer + " milliseconds. ");
        } 
        else {
            JOptionPane.showMessageDialog(null, "The input is not an integer. The delay is set to 25 milliseconds.", "Exception occured.", JOptionPane.INFORMATION_MESSAGE);
        }

        setupRandomMaze(this.maze_layout);
        startNewRun();
    }

    public void startNewRun() {
        if (attempts.size() == (size * size)) {
            concludeMaze();
        }

        this.currentRun = new Run();
        this.currentXpos = 1;
        this.currentYpos = 1;

        this.visualizeMaze(this.maze_layout, this.maze);
        this.visualizePlayerSpace(this.maze);


        Random navi_random = new Random();
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(delayTimer);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            double navi_input = navi_random.nextDouble();
            this.visualizeMaze(this.maze_layout, this.maze);
            this.changePlayerPosition(navi_input, this.maze_layout);
            this.visualizePlayerSpace(this.maze);
        }        
    }

    /**
     * 
     * @param maze_layout
     */
    public void setupRandomMaze(String[][] maze_layout) {
        //make it empty at first
        for (int i = 0; i < maze_layout.length; i++) {
            for (int j = 0; j < maze_layout[i].length; j++) {
                maze_layout[i][j] = "empty";
            }
        }
        //set up bordering walls
        for (int i = 0; i < maze_layout.length; i++) {
            for (int j = 0; j < maze_layout[i].length; j++) {
                if (i == 0 || i == maze_layout.length - 1) {
                    maze_layout[i][j] = "wall";
                }
                else if (j == 0 || j == maze_layout[0].length - 1) {
                    maze_layout[i][j] = "wall";
                }
                else {
                    continue;
                }
            }
        }
        //set up starting and ending spaces
        maze_layout[1][1] = "start";
        maze_layout[maze_layout.length - 2][maze_layout[0].length - 2] = "end";
        //Necessary variables.
        String current = maze_layout[1][1];
        int direction = 0;
        Random random = new Random();
        //reassign last space as finish
        maze_layout[maze_layout.length - 2][maze_layout[0].length - 2] = "end";
        //figure out the remaining spaces
        for (int i = 0; i < maze_layout.length; i++) {
            for (int j = 0; j < maze_layout[i].length; j++) {
                current = maze_layout[i][j];
                if (current.equals("empty")) {
                    direction = random.nextInt(5);
                    if (direction == 0) {
                        maze_layout[i][j] = "wall";
                    }
                    else {
                        maze_layout[i][j] = "walkable";
                    }
                }
            }
        }
        //print the maze layout
        /*
        for (int i = 0; i < maze_layout.length; i++) {
            for (int j = 0; j < maze_layout[i].length; j++) {
                System.out.print(maze_layout[i][j] + ", ");
            }
            System.out.println();
        }
        */
    }

    /**
     * 
     * @param maze_layout
     * @param panel
     */
    public void visualizeMaze(String[][] maze_layout, DrawingPanel panel) {
        Graphics maze = panel.getGraphics();
        for (int i = 0; i < maze_layout.length; i++) {
            for (int j = 0; j < maze_layout[i].length; j++) {
                if (maze_layout[i][j].equals("wall")) {
                    maze.setColor(new Color(0, 0, 0));
                    maze.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
                }
                else if (maze_layout[i][j].equals("start")) {
                    maze.setColor(new Color(0, 0, 255));
                    maze.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
                }
                else if (maze_layout[i][j].equals("end")) {
                    maze.setColor(new Color(0, 255, 0));
                    maze.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
                }
                else if (maze_layout[i][j].equals("walkable")) {
                    maze.setColor(new Color(255, 255, 255));
                    maze.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
                }
                else {
                    continue;
                }
            }
        }
    }

    /**
     * 
     * @param direction
     * @param maze_layout
     */
    public void changePlayerPosition(double direction, String[][] maze_layout) {

        //Finish run and print error in case maze cannot be navigated.
        //If the maze was not finished before:
        if (memory.size() == 0 && currentRun.number_of_moves == ((4 * size * size) - (2 * (size + size)))) {
            currentRun.finishRun(memory, attempts);
            JOptionPane.showMessageDialog(null, "It seems like this Maze cannot be navigated, or the AI is stuck.", "Exception Occured: Outside of AI Model's capabilities.", JOptionPane.INFORMATION_MESSAGE);
            badGeneration = true;
            concludeMaze();
            //JOptionPane.showMessageDialog(null, "It seems like this Maze cannot be navigated, or the AI is stuck.", "Exception Occured: Outside of AI Model's capabilities.", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("It seems like this Maze cannot be navigated, or the AI is stuck.");
            System.exit(1);
        }
        //If it was completed before:
        else if (memory.size() != 0 && currentRun.number_of_moves == (((4 * size * size) - (2 * (size + size))) * ((4 * size * size) - (2 * (size + size))))) {
            concludeMaze();
            //System.out.println("It seems like the last route was the most optimal route.");
            System.exit(1);
        }
        /*
        else if (currentRun.number_of_moves != 0 && currentRun.number_of_moves > 2 * size) {
            shortTermMemory();

        }
        */
        



        //Determining chances for the change of pathing.
        double[] raw_chances = new double[4];
        for (int i = 0; i < 4; i++) {
            raw_chances[i] = 69.0;
        }
        for (Move move : memory) {
            //Values that are greater than 1.0, but less than 2.
            if (currentXpos == move.getStart_x_position() && currentYpos == move.getStart_y_position()) {
                if (move.getEnd_x_position() == (currentXpos + 1)) {
                    raw_chances[3] = move.getChance();
                }
                else if (move.getEnd_x_position() == (currentXpos - 1)) {
                    raw_chances[2] = move.getChance();
                }
                else if (move.getEnd_y_position() == (currentYpos + 1)) {
                    raw_chances[1] = move.getChance();
                }
                else if (move.getEnd_y_position() == (currentYpos - 1)) {
                    raw_chances[0] = move.getChance();
                }
            }
        }
        if (currentRun.number_of_moves != 0) {
            //Values that are greater than 1.0, but less than 2.
            for (Move move : currentRun.moves) {
                if (currentXpos == move.getStart_x_position() && currentYpos == move.getStart_y_position()) {
                    if (move.getEnd_x_position() == (currentXpos + 1)) {
                        raw_chances[3] = move.getChance();
                    }
                    else if (move.getEnd_x_position() == (currentXpos - 1)) {
                        raw_chances[2] = move.getChance();
                    }
                    else if (move.getEnd_y_position() == (currentYpos + 1)) {
                        raw_chances[1] = move.getChance();
                    }
                    else if (move.getEnd_y_position() == (currentYpos - 1)) {
                        raw_chances[0] = move.getChance();
                    }
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            //Default chances in decision-making have a weight of 1.
            if (raw_chances[i] == 69.0) {
                raw_chances[i] = 1.0;
            }
        }
        double sumOfChances = 0.0;
        for (int i = 0; i < 4; i++) {
            sumOfChances += raw_chances[i];
        }
        double[] chances = new double[4];
        for (int i = 0; i < 4; i++) {
            //Chances should now be comprised of values less than 1.
            chances[i] = raw_chances[i] / sumOfChances;
        }
        double[] summativeChances = new double[4];
        summativeChances[0] = chances[0];
        for (int i = 1; i < summativeChances.length; i++) {
            summativeChances[i] = summativeChances[i - 1] + chances[i];
        }
        

        //Check for sticky situations:
        

        /*
        int repetitiveCount = 0; 
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (summativeChances[i] == summativeChances[j]) {
                    repetitiveCount++;
                }
                if (repetitiveCount == 4) {
                    System.out.println("The AI is stuck in a bad place.");
                    System.exit(1);
                }
            }
            repetitiveCount = 0;
        }
        */

        //Printing out chances for each move from the starting position:
        System.out.println("Starting position: (" + currentXpos + ", " + currentYpos + "); Chances:");
        System.out.println("Up: " + (Math.round(summativeChances[0] * 1000000000) / 1000000000.0));
        System.out.println("Down: " + (Math.round(summativeChances[1] * 1000000000) / 1000000000.0));
        System.out.println("Left: " + (Math.round(summativeChances[2] * 1000000000) / 1000000000.0));
        System.out.println("Right: " + (Math.round(summativeChances[3] * 1000000000) / 1000000000.0));
        

        if (summativeChances[0] == 0.0 && summativeChances[0] == summativeChances[1] && summativeChances[1] == summativeChances[2] && summativeChances[2] == summativeChances[3] && memory.size() == 0) {
            //JOptionPane.showMessageDialog(null, "The AI is stuck in a bad place.", "Exception Occured: Unnavigatable Generation", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("The AI is stuck in a bad place.");
            badGeneration = true;
            concludeMaze();
        }


        //Decision-making using the chances.
        Move move = new Move(currentXpos, currentYpos);
        //Moving up.
        if (direction <= summativeChances[0]) {
            System.out.println("    UP");
            currentYpos--;
            move.setEnd_x_position(currentXpos);
            move.setEnd_y_position(currentYpos);
            if (maze_layout[currentXpos][currentYpos].equals("wall")) {
                currentYpos++;
                move.setHits_wall(true);
            }
            else if (maze_layout[currentXpos][currentYpos].equals("end")) {
                move.setChance(1.0);
                System.out.println("YOU WIN!");
                number_of_attempts++;
                currentRun.finishRun(memory, attempts);
                startNewRun();
                System.exit(0);
            }
            else {
                move.setChance(1.0);
            }
        }
        //Moving down.
        else if (direction <= summativeChances[1]) {
            System.out.println("    DOWN");
            currentYpos++;
            move.setEnd_x_position(currentXpos);
            move.setEnd_y_position(currentYpos);
            if (maze_layout[currentXpos][currentYpos].equals("wall")) {
                currentYpos--;
                move.setHits_wall(true);
            }
            else if (maze_layout[currentXpos][currentYpos].equals("end")) {
                move.setChance(1.0);
                System.out.println("YOU WIN!");
                number_of_attempts++;
                currentRun.finishRun(memory, attempts);
                startNewRun();
                System.exit(0);
            }
            else {
                move.setChance(1.0);
            }
        }
        //Moving left.
        else if (direction <= summativeChances[2]) {
            System.out.println("    LEFT");
            currentXpos--;
            move.setEnd_x_position(currentXpos);
            move.setEnd_y_position(currentYpos);
            if (maze_layout[currentXpos][currentYpos].equals("wall")) {
                currentXpos++;
                move.setHits_wall(true);
            }
            else if (maze_layout[currentXpos][currentYpos].equals("end")) {
                move.setChance(1.0);
                System.out.println("YOU WIN!");
                number_of_attempts++;
                currentRun.finishRun(memory, attempts);
                startNewRun();
                System.exit(0);
            }
            else {
                move.setChance(1.0);
            }
        }
        //Moving right.
        else if (direction <= summativeChances[3]) {
            System.out.println("    RIGHT");
            currentXpos++;
            move.setEnd_x_position(currentXpos);
            move.setEnd_y_position(currentYpos);
            if (maze_layout[currentXpos][currentYpos].equals("wall")) {
                currentXpos--;
                move.setHits_wall(true);
            }
            else if (maze_layout[currentXpos][currentYpos].equals("end")) {
                move.setChance(1.0);
                System.out.println("YOU WIN!");
                number_of_attempts++;
                currentRun.finishRun(memory, attempts);
                startNewRun();
                System.exit(0);
            }
            else {
                move.setChance(1.0);
            }
        }
        else {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }
        currentRun.add(move);
        return;
    }

    /**
     * 
     * @param panel
     */
    public void visualizePlayerSpace(DrawingPanel panel) {
        Graphics player = panel.getGraphics();
        player.setColor(new Color(255, 0, 0));
        player.fillRect(currentXpos * squareSize, currentYpos * squareSize, squareSize, squareSize);
    }


    /*
    public void shortTermMemory() {
        //Implementation of short-term memory.

        Move lastMove = currentRun.moves.get(currentRun.number_of_moves - 1);
        Move secondLastMove = currentRun.moves.get(currentRun.number_of_moves - 2);
        
        int consecutiveOccurrences = 0;
        for (int i = currentRun.moves.size() - 2; i >= 0; i--) {
            if (currentRun.moves.get(i).equals(lastMove) && currentRun.moves.get(i).equals(secondLastMove)) {
                consecutiveOccurrences++;
                if (consecutiveOccurrences == size) {
                    System.out.println("It seems like this Maze cannot be navigated, or the AI is stuck.");
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.exit(1);
                }
            } else {
                consecutiveOccurrences = 0;
            }
        }
    }
    */

    private void concludeMaze() {
        /*
        System.out.println("The most optimal route has been found.");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to see it? (Yes/No)");
        String answer = scanner.nextLine();
        
        if (answer.toLowerCase().substring(0, 3).equals("yes")) {
            int lowest_number_of_moves = (((4 * size * size) - (2 * (size + size))) * ((4 * size * size) - (2 * (size + size))));
            int optimalRouteIndex = 0;
            for (int i = 0; i < attempts.size(); i++) {
                if (attempts.get(i).number_of_moves < lowest_number_of_moves) {
                    lowest_number_of_moves = attempts.get(i).number_of_moves;
                    optimalRouteIndex = i;
                }
            }
            replayRun(attempts.get(optimalRouteIndex));
        }
        else {
            System.exit(1);
        }
        */
        if (badGeneration) {
            JOptionPane.showMessageDialog(null, "The AI is stuck in a bad place.", "Exception Occured: Unnavigatable Generation", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            int choice = JOptionPane.showConfirmDialog(null, "The most optimal route has been found. Would you like to see it?", "User Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                int lowest_number_of_moves = (((4 * size * size) - (2 * (size + size))) * ((4 * size * size) - (2 * (size + size))));
                int optimalRouteIndex = 0;
                for (int i = 0; i < attempts.size(); i++) {
                    if (attempts.get(i).number_of_moves < lowest_number_of_moves) {
                        lowest_number_of_moves = attempts.get(i).number_of_moves;
                        optimalRouteIndex = i;
                    }
                }
                replayRun(attempts.get(optimalRouteIndex));
            } 
        }
        
        int choice = JOptionPane.showConfirmDialog(null, "Would you like to make a new maze?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            maze.setVisible(false);
            Maze newMaze = new Maze();
        } else {
            JOptionPane.showMessageDialog(null, "Thank you!", "Salutations!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(1);
        }
    }

    private void replayRun(Run run) {
        this.currentXpos = 1;
        this.currentYpos = 1;

        this.visualizeMaze(this.maze_layout, this.maze);
        this.visualizePlayerSpace(this.maze);

        Run optimizedRun = run.optimizeRunMoves(run);

        for (Move move : optimizedRun.moves) {
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            currentXpos = move.getEnd_x_position();
            currentYpos = move.getEnd_y_position();

            this.visualizeMaze(this.maze_layout, this.maze);
            this.visualizePlayerSpace(this.maze);
        }

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        JOptionPane.showMessageDialog(null, "Finished!", "Maze Navigation Completed", JOptionPane.INFORMATION_MESSAGE);;

        concludeMaze();
    }

    /*
    public void handleJOptionPaneClosing(JDialog dialog) {
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(null, "Error Occured. Unexpected closing.", "System exiting...", JOptionPane.INFORMATION_MESSAGE);;
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }
    */
}