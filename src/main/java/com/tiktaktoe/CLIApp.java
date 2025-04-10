package com.tiktaktoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CLIApp {

    public static void main(String[] args) {
        int decision = Input.inputInt("Choose between a pvp (1) or mvp (0) or training (2):");

        if ( decision == 1){
            User player1 = new User('X', 0, "player1");
            User player2 = new User('O', 1, "player2");
            game(player1, player2);
        } 
        else if(decision == 0){
            int random = new Random().nextInt(2);
            char machine_char = 'O';
            char human_char = 'X';
            int turn1 = 0;
            int turn2 = 1;

            if(random == 1){
                machine_char = 'X';
                human_char = 'O';
            }
            random = new Random().nextInt(2);
            if(random == 1){
                turn1 = 1;
                turn2 = 0;
            } 
            
            KI machine = new KI(machine_char, turn1);
            User player = new User(human_char, turn2, "human player");
            game(machine, player);

        }
        else if(decision == 2){
            int training_cycles = Input.inputInt("Choose how many training cycles: ");
            ProgressBar pb = new ProgressBar(); 
            pb.setMax(training_cycles);
            for (int i = 0; i < training_cycles; i++) {
                int turnX = 1;
                int turnO = 0;
                int random = new Random().nextInt(2);
                if(random == 1){
                    turnO = 1;
                    turnX = 0;
                }
                KI machine1 = new KI('X', turnX);
                KI machine2 = new KI('O', turnO); 
                machineGame(machine1, machine2);
                pb.tickOne();
            }
        }
    }


    // Starting game Machine vs Machine
    public static void machineGame(KI X, KI O){
 
        List<String> boardStates_O  = new ArrayList<>();
        List<String> boardStates_X = new ArrayList<>();
        List<Integer> moves_O = new ArrayList<>();
        List<Integer> moves_X = new ArrayList<>();

        Board map = new Board();
        Boolean gamestop = false;
        while(!gamestop){
            if(X.getTurn() == 1){
                int move = X.makeMove(map);
                boardStates_X.add(getBoardState(map));
                moves_X.add(move);

                map.setMap(move, 'X');
                if(checkWin(map, 'X')) break;
                X.setTurn(0);
                O.setTurn(1);


            }
            else if(O.getTurn() == 1){
                int move = O.makeMove(map);
                boardStates_O.add(getBoardState(map));
                moves_O.add(move);
                map.setMap(move, 'O');
                if(checkWin(map, 'O')) break;
                O.setTurn(0);
                X.setTurn(1);

            }
            gamestop = getBoardState(map).matches("[XO]{9}");
        }
        X.learnFromGame(boardStates_X, moves_X, checkWin(map, 'X'), gamestop);
        O.learnFromGame(boardStates_O, moves_O, checkWin(map, 'O'), gamestop);
    }



    // Starting game Player / Machine vs Player            
    public static void game(User player1, User player2){

        List<String> boardStates_X = new ArrayList<>();
        List<Integer> moves_X = new ArrayList<>();
        List<String> boardStates_O = new ArrayList<>();
        List<Integer> moves_O = new ArrayList<>();

        Board map = new Board();
        System.out.println("\nLet's begin: \n");
        boolean gameStop = false;

        while(!gameStop){
            if(player1.getName().equals("machine") && player1.getTurn() == 1){
                // AI making a move
                int machinemove = machineMoves(((KI) player1), player2, map);
                // data for the AI
                if(player1.getSymbol() == 'X'){
                    moves_X.add(machinemove);
                    boardStates_X.add(getBoardState(map));
                }
                else if(player1.getSymbol() == 'O'){
                    moves_O.add(machinemove);
                    boardStates_O.add(getBoardState(map));
                }
                //updating state of map
                map.setMap(machinemove, player1.getSymbol());

                //check if machine wins
                if (checkWin(map, player1.getSymbol())) {
                    printBoard(map);
                    System.out.println("Machine wins!!");
                    break;
                }
            }
            else if(player1.getTurn() == 1 && player1.getName().equals("player1")){
                //Player1 making a move
                int playerMove = playerMakesMove(player1, player2, map);
                // data for the AI
                if(player1.getSymbol() == 'X'){
                    moves_X.add(playerMove);
                    boardStates_X.add(getBoardState(map));
                }
                else if(player1.getSymbol() == 'O'){
                    moves_O.add(playerMove);
                    boardStates_O.add(getBoardState(map));
                }

                // updating state of map
                map.setMap(playerMove, player1.getSymbol());
                // check if player1 wins

                if (checkWin(map, player1.getSymbol())) {
                    printBoard(map);
                    System.out.println("Player 1 wins!!");
                    break;
                }
            }
            else if(player2.getTurn() == 1){

                int playerMove = playerMakesMove(player2, player1, map);
                
                // data for the AI
                if(player2.getSymbol() == 'X'){
                    moves_X.add(playerMove);
                    boardStates_X.add(getBoardState(map));
                }
                else if(player2.getSymbol() == 'O'){
                    moves_O.add(playerMove);
                    boardStates_O.add(getBoardState(map));
                }

                // updating state of map
                map.setMap(playerMove, player2.getSymbol());
                
                /// checks if player2 wins
                if(checkWin(map, player2.getSymbol())){
                    printBoard(map);
                    System.out.println("Player 2 wins!!");
                    break;
                }
            }
            //checks for a draw
            gameStop = getBoardState(map).matches("[XO]{9}");
        }

        if(gameStop) System.out.println("It's a draw");

        int learning = Input.inputInt("Would you like the AI to learn from this match: (1 =yes) (0 = no):") ;
         
        if(learning== 1 && player1.getName().equals("player1")){
            KI machine1 = new KI(player1.getSymbol(), 0);
            if(player1.getSymbol() == 'X') machine1.learnFromGame(boardStates_X, moves_X, checkWin(map, 'X'), gameStop);
            else machine1.learnFromGame(boardStates_O, moves_O, checkWin(map, 'O'), gameStop);

            KI machine2 = new KI(player2.getSymbol(), 0);
            if(player2.getSymbol() == 'X') machine2.learnFromGame(boardStates_X, moves_X, checkWin(map, 'X'), gameStop);
            else machine2.learnFromGame(boardStates_O, moves_O, checkWin(map, 'O'), gameStop);



        }
        else if(learning == 1 && player1.getName().equals("machine")){
            if(player1.getSymbol() == 'X') ((KI) player1).learnFromGame(boardStates_X, moves_X, checkWin(map, 'X'), gameStop);
            else ((KI) player1).learnFromGame(boardStates_O, moves_O, checkWin(map, 'O'), gameStop);

            KI machine2 = new KI(player2.getSymbol(), 0);
            if(player2.getSymbol() == 'X') machine2.learnFromGame(boardStates_X, moves_X, checkWin(map, 'X'), gameStop);
            else machine2.learnFromGame(boardStates_O, moves_O, checkWin(map, 'O'), gameStop);
        }
    }


    // Function that returns the player's Input
    public static int playerMakesMove(User firstPlayer, User secondPlayer, Board map) {
        printBoard(map);
        secondPlayer.setTurn(1);
        firstPlayer.setTurn(0);
        return Input.validMove(String.format("%s (%c) makes a move (0-8): ", firstPlayer.getName(), firstPlayer.getSymbol()), map);
        
    }


    // Function that return the Machine's Input
    public static int machineMoves(KI firstPlayer, User secondPlayer, Board map) {
        int move = firstPlayer.makeMove(map);
        secondPlayer.setTurn(1);
        firstPlayer.setTurn(0);
        return move;
        
    }


    // Print the Board in the correct format for terminal use
    public static void printBoard(Board board){
        for (int i = 0; i < 9; i++) {
            System.out.print(board.getMap()[i]); 
            System.out.print(" ");
            if (i == 2 | i == 5 | i == 8) System.out.println(); 
        }
        System.out.println("");
    }


    // Check for winning states
    public static boolean checkWin(Board board, char symbol) {
        String boardString = getBoardState(board);
        boolean horizontalWin = boardString.matches(String.format("(.{0}|.{3}|.{6})(%c%c%c).*",symbol,symbol,symbol));
        boolean diagonalWin = boardString.matches(String.format(".*(%c...%c...%c).*",symbol,symbol,symbol));
        boolean diagonalrWin = boardString.matches(String.format(".{2}(%c.%c.%c).{2}",symbol,symbol,symbol));
        boolean verticalWin = boardString.matches(String.format(".*(%c..%c..%c).*",symbol,symbol,symbol));
        if (horizontalWin | diagonalWin | verticalWin | diagonalrWin) return true;
        else return false;
    }
        


    // Return the board state as a String
    private static String getBoardState(Board board) {
        return String.copyValueOf(board.getMap());
    }

    
}

