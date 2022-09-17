import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Sudoku {

	private static int boardSize = 0;
	private static int partitionSize = 0;
	public int[][] vals;
	public int[][] track;
	public int[][] sol;
	public String filename;
	
	private static final String inputFilename = "src/sudoku9Easy.txt";
	
	
	public void readFile(String filename) throws IOException {
		this.filename = filename;
		File inputFile = new File(filename);
		Scanner input = null;
		this.vals = null;

		int temp = 0;
    	int count = 0;
    	
	    try {
			input = new Scanner(inputFile);
			temp = input.nextInt();
			boardSize = temp;
			partitionSize = (int) Math.sqrt(boardSize);
			System.out.println("Boardsize: " + temp + "x" + temp);
			this.vals = new int[boardSize][boardSize];
			this.track = new int[boardSize][boardSize];
			
			
			System.out.println("Input:");
	    	int i = 0;
	    	int j = 0;
	    	while (input.hasNext()){
	    		temp = input.nextInt();
	    		count++;
	    		System.out.printf("%3d", temp);
	    		this.vals[i][j] = temp;
				if (temp == 0) {
					this.track[i][j] = -1;
				}
				j++;
				if (j == boardSize) {
					j = 0;
					i++;
					System.out.println();
				}
				if (j == boardSize) {
					break;
				}
	    	}
	    	input.close();
	    } catch (FileNotFoundException exception) {
	    	System.out.println("Input file not found: " + filename);
	    }
	    if (count != boardSize*boardSize) throw new RuntimeException("Incorrect number of inputs.");
	    
	    boolean solved;
	    if(this.track[0][0] != -1) {
	    	int[] initSpace = this.findNext(0, 0, this.track);
	    	solved = solve(initSpace[0], initSpace[1], this.vals);
	    }else {
	    	solved = solve(0, 0, this.vals);
	    }
	    
	    // Output
	    if (!solved) {
	    	System.out.println("No solution found.");
	    	this.writeAnswer(filename, false);
	    	return;
	    }
	    System.out.println("\nOutput\n");
	    for (int i = 0; i < boardSize; i++) {
	    	for (int j = 0; j < boardSize; j++) {
	    		System.out.printf("%3d", this.sol[i][j]);
	    	}
	    	System.out.println();
	    }		
	    this.writeAnswer(filename, true);
	}
	
	public void writeAnswer(String filename, boolean solved) throws FileNotFoundException, UnsupportedEncodingException {
		String newFileName = filename.substring(0, filename.length() - 4) + "Solution.txt";
//		System.out.println(newFileName);
		System.out.println("Answer write to: " + newFileName);
		PrintWriter writer = new PrintWriter(newFileName, "UTF-8");
		
		if(solved) {
			this.checkAnswer();
			
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					writer.printf("%3d", this.sol[i][j]);
				}
				writer.println();
			}	
			
		}else {
			writer.println(-1);
		}

		writer.close();
	}
	
	public boolean checkValue(int value, int i, int j, int[][] board) {
		//row
//		System.out.println("row: " + Arrays.toString(board[i]));
		
		for(int n : board[i]) {
			if(n == value) {
				return false;
			}
		}
		
		//col
//		System.out.println("col: ");
		
		for(int k = 0; k < 9; k++) {
//			System.out.println(board[k][j]);
			if(board[k][j] == value) {
				return false;
			}
		}
		
		//square
		int rowStart = i / 3 * 3;
		int colStart = j / 3 * 3;
		
//		System.out.println("square: ");
		
		for(int m = rowStart; m < rowStart + 3; m++) {
//			System.out.println();
			for(int n = colStart; n < colStart + 3; n++) {
//				System.out.print(" " + board[m][n]);
				if(board[m][n] == value) {
					return false;
				}
			}
		}
		
		
		return true;
		
	}
	
	public int[] findNext(int i, int j, int[][] board) {
		int[] output = new int[2];
		
		
		for(int m = i; m < this.boardSize; m++){
			for(int n = 0; n < this.boardSize; n++){
				if(this.track[m][n] == -1) {
					if(m == i && n <= j) {
						
					}else {
						
						output[0] = m;
						output[1] = n;
						
						return output;
					}
				}
			}
		}
		return null;
		
	}
	
	public int[][] copy2dArray(int[][] input){
		int [][] output = new int[input.length][];
		for(int i = 0; i < input.length; i++)
		    output[i] = input[i].clone();
		return output;
	}
	
	public boolean solve(int i, int j, int[][] board){
//		System.out.println(Arrays.toString(this.vals[0]));
		int[][] newBoard = this.copy2dArray(board);

		for(int k = 1; k < this.boardSize + 1; k++) {
			if(this.checkValue(k, i, j, board)) {
//				this.track[i][j] = k;
				newBoard[i][j] = k;
				this.sol = this.copy2dArray(newBoard);
//				this.printBoard(sol);
//				System.out.println();
//				break;
				int[] nextSpace = this.findNext(i, j, this.track);
				if(nextSpace == null) {
					return true;
				}
				if(this.solve(nextSpace[0], nextSpace[1], newBoard)){//solvable
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void printBoard(int[][] board) {
		for(int m = 0; m < this.boardSize; m++){
			System.out.println();
			for(int n = 0; n < this.boardSize; n++){
				System.out.print(board[m][n] + " ");
			}
		}
	}
		
	public void checkAnswer() {
		ArrayList<Integer> checkList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
//		System.out.println(checkList.toString());
		
		//rows
		for(int i = 0; i < 9; i++) {
			ArrayList<Integer> temp =  new ArrayList<>();
			for(int j = 0; j < 9; j++) {
				temp.add(this.sol[i][j]);
			}
//			System.out.println(temp.toString());
			Collections.sort(temp);   
//			System.out.println(temp.toString());
//			System.out.println(temp.equals(checkList));
			if(!(temp.equals(checkList))) {
				System.out.println("error at row " + i);
				return;
			}
		}
		
		//cols
		for(int i = 0; i < 9; i++) {
			ArrayList<Integer> temp =  new ArrayList<>();
			for(int j = 0; j < 9; j++) {
				temp.add(this.sol[j][i]);
			}
//			System.out.println(temp.toString());
			Collections.sort(temp);   
//			System.out.println(temp.toString());
//			System.out.println(temp.equals(checkList));
			if(!(temp.equals(checkList))) {
				System.out.println("error at col " + i);
				return;
			}
		}
		
		//square
		
		for(int i = 0; i < 9; i+=3) {
			for(int j = 0; j < 9; j+=3) {
//				System.out.println("( " + i + " " + j + " )");
				ArrayList<Integer> temp =  new ArrayList<>();
				for(int m = 0; m < 3; m++) {
					for(int n = 0; n < 3; n++) {
						
						temp.add(this.sol[i + m][j + n]);
					}
				}
//				System.out.println(temp.toString());
				Collections.sort(temp);   
//				System.out.println(temp.toString());
//				System.out.println(temp.equals(checkList));
				if(!(temp.equals(checkList))) {
					System.out.println("error at square " + i + " " + j);
					return;
				}
			}
		}
		
//		System.out.println("Answer is correct !!!");
		
	}
	
	public static void main(String[] args) throws IOException{
		
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter the file full path (or copy to file into the src folder and enter src/filename): ");
		String filename = input.nextLine();
		
		Sudoku game = new Sudoku();
		game.readFile(filename);	
	}
}