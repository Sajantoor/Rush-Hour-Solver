package rushhourtest;

import java.io.File;
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner;

import rushhour.Solver;

public class TestRushHour {

	public static int dirChar2Int(char d) {
		switch (d) {
		case 'U': {
			return RushHour.UP;
		}
		case 'D': {
			return RushHour.DOWN;
		}
		case 'L': {
			return RushHour.LEFT;
		}
		case 'R': {
			return RushHour.RIGHT;
		}
		default:
			throw new IllegalArgumentException("Unexpected direction: " + d);
		}
	}

	public static void testSolution(String puzzleName, String solName) {
		try {

			RushHour puzzle = new RushHour(puzzleName);

			Scanner scannerSolution = new Scanner(new File(solName));
			while (scannerSolution.hasNextLine()) {
				String line = scannerSolution.nextLine();
				if (line.length() != 3)
					throw new IllegalMoveException(line);
				puzzle.makeMove(line.charAt(0), dirChar2Int(line.charAt(1)), line.charAt(2) - '0');
			}

			if (puzzle.isSolved()) {
				System.out.println("Solved");
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static void main(String[] args) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		/* changes to file here*/
		String folderName = "../testFiles/";
		String solutionFolder = "../solutions/";

		File dir = new File(folderName);
        File[] directoryListing = dir.listFiles();

		if (directoryListing != null) {
			for (File file : directoryListing) {
				String name = file.getName();
				String puzzleName = folderName + name;
				String solName = solutionFolder + name + ".sol";

				/* Changes end here */
				// This is obviously outside of this loop.
				try {
					Solver.solveFromFile(puzzleName, solName);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				testSolution(puzzleName, solName);
			}
		} 
	}
}