import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Run {
    ArrayList<Move> moves;
    int number_of_moves;
    boolean formatted;

    public Run() {
        moves = new ArrayList<Move>();
        number_of_moves = 0;
        formatted = false;
    }

    public void add(Move move) {
        moves.add(move);
        number_of_moves++;
    }

    public void finishRun(ArrayList<Move> memory, ArrayList<Run> attempts) {
        if (formatted == false) {
            formatRunMoves();
        }
        /*
         * If moves are in memory, add to their chance by 1 / number of moves;
         * If moves are not in memory, set their chance to 1
         */
        boolean inMemory = false;
        //
        for (Move move : moves) {
            for (Move memoryMove : memory) {
                //If it's a move that doesn't hit a wall that is in memory
                if (move.getStart_x_position() == memoryMove.getStart_x_position() && move.getEnd_x_position() == memoryMove.getEnd_x_position() && move.getEnd_x_position() == memoryMove.getEnd_x_position() && move.getEnd_y_position() == memoryMove.getEnd_y_position()) {
                    inMemory = true;
                    if (!move.getHits_wall()) {
                        int indexOfMove = memory.indexOf(memoryMove);
                        double initialChance = memory.get(indexOfMove).getChance();
                        double adjustedChance = initialChance + (1.0 / number_of_moves);
                        move.setChance(adjustedChance);
                        memory.set(indexOfMove, move);
                    }
                }
            }
            //If it's not in memory.
            if (inMemory == false) {
                if (!move.getHits_wall()) {
                    move.setChance(1);
                }
                memory.add(move);
            }
            inMemory = false;
        }

        attempts.add(this);

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return;
    }

    public void formatRunMoves() {
        ArrayList<Move> uniqueMoves = new ArrayList<Move>();
        
        for (Move move : moves) {
            if (!uniqueMoves.contains(move)) {
                uniqueMoves.add(move);
            }
        }
        
        moves.clear();
        moves.addAll(uniqueMoves);
        formatted = true;
    }

    public Run optimizeRunMoves(Run run) {
        for (int i = 0; i < run.moves.size(); i++) {
            Move current = run.moves.get(i);
            for (int j = i + 1; j < run.moves.size(); j++) {
                if (run.moves.get(j).equals(current)) {
                    for (int k = i + 1; k < j; k++) {
                        run.moves.remove(i + 1);
                    }
                    run.moves.remove(i);
                    // After removing elements, adjust indices and break out of the inner loop
                    i--;
                    break;
                }
            }
        }

        return run;
    }
}
