/*
 * This source file was generated by the Gradle 'init' task
 */
package tiktakto;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        int decision = Input.inputInt("Choose between a pvp (1) or mvp (0):");

        if ( decision == 1){
            User player1 = new User('X', 0);
            User player2 = new User('O', 1);
            pvp(player1, player2);
        } 
        else if(decision == 0){

        }
        else{
            
        }
            
    }
            
    public static void pvp(User player1, User player2){
        Board map = new Board();
        System.out.println("\nLet's begin: \n");
        boolean won = false;

        while(!won){
            if(player1.getTurn() == 1){
                printBoard(map);
                map.setMap(Input.inputInt("Player 1 makes a move (0-8): "), player1.getSymbol());
                player1.setTurn(0);
                player2.setTurn(1);
            }
            else if(player2.getTurn() == 1){
                printBoard(map);
                map.setMap(Input.inputInt("Player 2 makes a move (0-8):"), player2.getSymbol());
                player2.setTurn(0);
                player1.setTurn(1);
            }
            printBoard(map);
            if(Input.inputInt("Continue(0), end (1): ") == 1){
                won = true;
            }
        }

    }

    public static void printBoard(Board board){
        for (int i = 0; i < 9; i++) {
            System.out.print(board.getMap()[i]); 
            System.out.print(" ");
            if (i == 2 | i == 5 | i == 8) System.out.println(); 
        }

    }
}
