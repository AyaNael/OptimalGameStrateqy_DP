package application;

// Row class to represent each row in the table
public class DPRow {
    private final int index;
    private final String[] values;

    public DPRow(int index, int[][][] dp, int n) {
        this.index = index;
        this.values = new String[n];
        for (int j = 0; j < n; j++) {
            if (j < index) {
                values[j] = ""; // Empty for cells below the diagonal
            } else {
                values[j] = "(" + dp[index][j][0] + ", " + dp[index][j][1] + ")";
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public String getValueAt(int col) {
        if (col >= values.length) return "";
        return values[col];
    }
}