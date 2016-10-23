package com.theaigames.bot;

import java.util.Optional;

public class Board {
    private int[][] mBoard;
    private int mCols = 0, mRows = 0;
    private String mLastError = "";
    private int mLastColumn = 0;
    private int discCount = 0;

    private Board(Builder builder) {
        mBoard = new int[builder.getColumns()][builder.getRows()];
        mCols = builder.getColumns();
        mRows = builder.getRows();
        clearBoard();
    }

    /**
     * Clear the board
     */
    public void clearBoard() {
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                mBoard[x][y] = 0;
            }
        }
    }

    /**
     * Adds a disc to the board
     * @return : true if disc fits, otherwise false
     */
    public Boolean addDisc(int column, int disc) {
        mLastError = "";
        if (column < mCols) {
            for (int y = mRows-1; y >= 0; y--) { // From bottom column up
                if (mBoard[column][y] == 0) {
                    mBoard[column][y] = disc;
                    mLastColumn = column;
                    return true;
                }
            }
            mLastError = "Column is full.";
        } else {
            mLastError = "Move out of bounds.";
        }
        return false;
    }

    /**
     * Initialise field from comma separated String
     */
    public void parseFromString(String s) {
        s = s.replace(';', ',');
        String[] r = s.split(",");
        int counter = 0;
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                mBoard[x][y] = Integer.parseInt(r[counter]);
                counter++;
            }
        }
    }

    /**
     * Returns the current piece on a given column and row
     * @return : int
     */
    public int getDisc(int column, int row) {
        return mBoard[column][row];
    }

    /**
     * Returns whether a slot is open at given column
     * @return : Boolean
     */
    public Boolean isValidMove(int column) {
        return (mBoard[column][0] == 0);
    }

    /**
     * Returns reason why addDisc returns false
     * @return : reason why addDisc returns false
     */
    public String getLastError() {
        return mLastError;
    }

    /**
     * Creates comma separated String with every cell.
     * @return : String
     */
    @Override
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                if (counter > 0) {
                    r += ",";
                }
                r += mBoard[x][y];
                counter++;
            }
        }
        return r;
    }

    /**
     * Checks whether the field is full
     * @return : Returns true when field is full, otherwise returns false.
     */
    public boolean isFull() {
        for (int x = 0; x < mCols; x++)
          for (int y = 0; y < mRows; y++)
            if (mBoard[x][y] == 0)
              return false; // At least one cell is not filled
        // All cells are filled
        return true;
    }

    /**
     * Checks whether the given column is full
     * @return : Returns true when given column is full, otherwise returns false.
     */
    public boolean isColumnFull(int column) {
        return (mBoard[column][0] != 0);
    }

    /**
     * @return : Returns the number of columns in the field.
     */
    public int getNrColumns() {
        return mCols;
    }

    /**
     * @return : Returns the number of rows in the field.
     */
    public int getNrRows() {
        return mRows;
    }

    public static class Builder {
        private Optional<Integer> rows;
        private Optional<Integer> columns;

        public void setColumns(int columns) {
            this.columns = Optional.of(columns);
        }

        public void setRows(int rows) {
            this.rows = Optional.of(rows);
        }

        private int getRows() {
            return rows.get();
        }

        private int getColumns() {
            return columns.get();
        }

        public Board build() {
            if (!canBuild()) {
                throw new IllegalStateException("Cannot build incomplete object");
            }

            return new Board(this);
        }

        private boolean canBuild() {
            return rows.isPresent() && columns.isPresent();
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
