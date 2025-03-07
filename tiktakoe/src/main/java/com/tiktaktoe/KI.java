package com.tiktaktoe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.analysis.function.*;



public class KI extends User{

    private Map<String, double[]> loadedDB;
    private String DATABASE_FILE = "database_X.csv";
    private static final double BIAS = 0.01; // Bias value


    public KI(char symbol, int turn) {
        super(symbol, turn, "machine");
        loadedDB = new HashMap<>();
        if(symbol =='O') DATABASE_FILE = "database_O.csv";
        loadDatabase();
    } 



    // Load the database from CSV file
    private void loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String boardState = parts[0];
                double[] moves = new double[9];
                for (int i = 0; i < 9; i++) {
                    moves[i] = Double.parseDouble(parts[i + 1]);
                }
                loadedDB.put(boardState, moves);
            }
        } catch (IOException e) {
            System.out.println("No existing database found. Starting fresh.");
        }
    }




    // Save the database to CSV file
    private void saveDatabase() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATABASE_FILE))) {
            for (Map.Entry<String, double[]> entry : loadedDB.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey());
                for (double value : entry.getValue()) {
                    line.append(",").append(value);
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving database: " + e.getMessage());
        }
    }


    // Get board state as string
    private String getBoardState(Board board) {
        return String.copyValueOf(board.getMap());
    }



   // Make a move based on learned data
    public int makeMove(Board board) {
        String boardState = getBoardState(board);
        double[] moves = loadedDB.getOrDefault(boardState, new double[9]);
        
        // Find best move
        int bestMove = -1;
        double maxValue = -1;
       
        for (int i = 0; i < 9; i++) {
            double move_biased = (moves[i] + (new Random().nextDouble(1.1) * BIAS)); 
            if (board.getMap()[i] == '-' && move_biased> maxValue) {
                maxValue = move_biased;
                bestMove = i;
            }
        }
        
        // If no learned moves, make random move
        if (bestMove == -1) {
            List<Integer> availableMoves = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (board.getMap()[i] == '-') {
                    availableMoves.add(i);
                }
            }
            if (!availableMoves.isEmpty()) {
                bestMove = availableMoves.get(new Random().nextInt(availableMoves.size()));
            }
        }
        return bestMove;
    }



    // Learn from game outcome
    public void learnFromGame(List<String> gameStates, List<Integer> moves, boolean won, boolean draw) {
            double reward;
            if(won) reward = 0.1;
            else reward = -0.1;
            if(draw) reward = 0.05;

        for (int i = 0; i < gameStates.size(); i++) {
            String state = gameStates.get(i);
            int move = moves.get(i);
            
            double[] stateValues = loadedDB.getOrDefault(state, new double[9]);
            if(!won) stateValues[move] = -stateValues[move];
            stateValues[move] += reward;
            stateValues = normalizeWeights(stateValues, move);
            loadedDB.put(state, stateValues);
        }
        saveDatabase();
    }



    private double[] normalizeWeights(double[] weights, int move) {
        double[] normalizedWeights = new double[weights.length];
        
        // Find min and max values
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        for (double weight : weights) {
            if (weight < min) min = weight;
            if (weight > max) max = weight;
        }

        // Apply Min-Max normalization with bias
        for (int i = 0; i < weights.length; i++) {
            if (max == min | weights[i] <= 0.0000) {
                // Handle the case where all values are the same
                normalizedWeights[i] = 0.5 + (new Random().nextDouble(-1,1) * BIAS); // BIAS Implementation
            } 
            else if(i == move){                
                normalizedWeights[i] = new Sigmoid().value(weights[i]);
                
            } 
            else normalizedWeights[i] = weights[i];
        }
        return normalizedWeights;
    }
}
