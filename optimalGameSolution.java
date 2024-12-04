package application;
import java.util.*;

public class optimalGameSolution {

	public static int[][][] maximumAmountOfCoins(int[] arr, int[] chosenCoins, int[] secondPlayerCoins) {
	    int n = arr.length;
	    int[][][] dp = new int[n][n][2]; // Stores [firstPlayerScore, secondPlayerScore]

	    // Base case: for each single coin, the maximum amount is the coin itself.
	    for (int i = 0; i < n; i++) {
	        dp[i][i][0] = arr[i]; // First player gets the coin
	        dp[i][i][1] = 0;      // Opponent gets nothing
	    }

	    // Fill the dp table for subarrays of increasing lengths
	    for (int length = 2; length <= n; length++) {
	        for (int i = 0; i <= n - length; i++) {
	            int j = i + length - 1; // The last element in the array

	            // Case 1: First player picks the left coin
	            int pickLeftFirst = arr[i] + (i + 1 <= j ? dp[i + 1][j][1] : 0); // First player picks left coin
	            int pickLeftSecond = (i + 1 <= j ? dp[i + 1][j][0] : 0); // Remaining score for second player

	            // Case 2: First player picks the right coin
	            int pickRightFirst = arr[j] + (i <= j - 1 ? dp[i][j - 1][1] : 0); // First player picks right coin
	            int pickRightSecond = (i <= j - 1 ? dp[i][j - 1][0] : 0); // Remaining score for second player

	            // Choose the better option for the first player
	            if (pickLeftFirst > pickRightFirst) {
	                dp[i][j][0] = pickLeftFirst; // Update first player's score
	                dp[i][j][1] = pickLeftSecond; // Update second player's score
	            } else {
	                dp[i][j][0] = pickRightFirst; // Update first player's score
	                dp[i][j][1] = pickRightSecond; // Update second player's score
	            }
	        }
	    }

	

        

        // Backtrace to find the chosen coins for both players
        int start = 0, end = n - 1;
        int firstIndex = 0, secondIndex = 0;
        boolean firstPlayerTurn = true; // Track whose turn it is

        while (start <= end) {
            // Determine which coin was chosen based on the DP values
            if (dp[start][end][0] - arr[start] == dp[start + 1][end][1]) {
                // Left coin was chosen
                if (firstPlayerTurn) {
                    chosenCoins[firstIndex++] = arr[start]; // Add to first player's coins
                } else {
                    secondPlayerCoins[secondIndex++] = arr[start]; // Add to second player's coins
                }
                start++; // Move the start index forward
            } else {
                // Right coin was chosen
                if (firstPlayerTurn) {
                    chosenCoins[firstIndex++] = arr[end]; // Add to first player's coins
                } else {
                    secondPlayerCoins[secondIndex++] = arr[end]; // Add to second player's coins
                }
                end--; // Move the end index backward
            }

            // Switch turns
            firstPlayerTurn = !firstPlayerTurn;
        }

        // Resize the arrays to remove unused slots
        chosenCoins = Arrays.copyOf(chosenCoins, firstIndex); // Resize first player's coins array
        secondPlayerCoins = Arrays.copyOf(secondPlayerCoins, secondIndex); // Resize second player's coins array

        return dp;
    }

    public static void main(String[] args) {
        int[] arr = {4, 15, 7, 3, 8, 9};
        int[] chosenCoins = new int[arr.length]; // Allocate sufficient size for chosen coins
        int[] secondPlayerCoins = new int[arr.length];
        int[][][] dp = maximumAmountOfCoins(arr, chosenCoins, secondPlayerCoins);

        // Print the DP table with pairs of scores
        System.out.println("\nDP Table (First and Second Player Scores):");
        System.out.print("\t");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(i + "\t");
        }
        System.out.println();
        for (int i = 0; i < arr.length; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < arr.length; j++) {
                if (j >= i) {
                    System.out.print("(" + dp[i][j][0] + "," + dp[i][j][1] + ")\t");
                } else {
                    System.out.print("\t");
                }
            }
            System.out.println();
        }

        System.out.println("\nOptimal result for first player: " + dp[0][arr.length - 1][0]);
        System.out.print("Coins chosen by first player: ");
        for (int coin : chosenCoins) {
            if (coin != 0) { // Avoid printing unused slots in the array
                System.out.print(coin + " ");
            }
        }
        System.out.print("\nCoins chosen by second player: ");
        for (int coin : secondPlayerCoins) {
            if (coin != 0) { // Avoid printing unused slots in the array
                System.out.print(coin + " ");
            }
        }
    }
}
