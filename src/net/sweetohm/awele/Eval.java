/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sweetohm.awele;

/**
 * The evaluation function of the AI.
 */
public final class Eval {

    /**
     * Maximum possible note
     */
    public static final int MAX_NOTE = 200000;
    /**
     * Minimal possible note
     */
    public static final int MIN_NOTE = -200000;
    /**
     * The constant for the north
     */
    private static final int NORTH = 1;
    /**
     * The DNa for the evaluation function. The values of this DNA are
     * parameters resulting from evolution of the evaluation function
     * fighting against mutants (copies with different values in their
     * DNA. Each cell in this array are for:
     * <ul>
     * <li> 0: Absolute avance (the number of seeds one side has captured)
     * <li> 1: Potential (number of seeds in a given side)
     * <li> 2: Mobility (the number of playing possibilities)
     * <li> 3: Tactic mobility (the numbers of times one can play without dying)
     * <li> 4: Threats (number of adverse hole one can threat)
     * <li> 5: Loft (number of holes with more than 12 seeds)
     * <li> 6: Dam (number of continuous vulnerable holes)
     * </ul>
     */
    private static final int[] DNA = {100, 20, 40, 0, 80, 70, 60};

    /**
     * Compute the absolute advance with coefficients. Compute the difference of
     * captured seeds and multiply by the DNA value for absolute advance.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int absoluteAdvance(int board[],
                                       int side) {
        return ((board[12] - board[13]) * DNA[0] * (side == NORTH ? 1 : -1));
    }

    /**
     * Compute the potential with coefficient. Makes the difference of seeds
     * between north and south and return the value multiplied with the
     * DNA coefficient.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int potential(int[] board,
                                 int side) {
        int sum = 0;
        for (int i = 0; i < 6; i++) sum += board[i];
        for (int i = 6; i < 12; i++) sum -= board[i];
        return ((side == NORTH ? sum : -sum) * DNA[1]);
    }

    /**
     * Compute mobility with coefficient. Return the difference of empty holes
     * in each side and return this value multiplied with the DNA coeffecient.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int mobility(int[] board,
                                int side) {
        int mobility = 0;
        for (int i = 0; i < 6; i++) mobility += (board[i] != 0 ? 1 : 0);
        for (int i = 6; i < 12; i++) mobility -= (board[i] != 0 ? 1 : 0);
        return (mobility * DNA[2] * (side == NORTH ? 1 : -1));
    }

    /**
     * Compute the tactic mobility with coefficient. Return the difference of
     * number of move that don't feed the other side and multiply with the
     * DNA coefficient.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int tacticMobility(int[] board,
                                      int side) {
        int tacticMobility = 0;
        for (int i = 0; i < 5; i++) if (i + board[i] < 6) tacticMobility++;
        for (int i = 6; i < 11; i++) if (i + board[i] < 12) tacticMobility--;
        return (tacticMobility * DNA[3] * (side == NORTH ? 1 : -1));
    }

    /**
     * Compute the threats with coefficient. Return the difference of the number
     * of treatened holes multiplied by the DNA coefficient.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int threats(int[] board,
                               int side) {
        int[] northThreats = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] southThreats = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int north = 0;
        int south = 0;
        for (int i = 0; i < 6; i++) northThreats[(i + board[i] % 11) % 12]++;
        for (int i = 6; i < 12; i++) if (northThreats[i] > 0) north++;
        for (int i = 6; i < 12; i++) southThreats[(i + board[i] % 11) % 12]++;
        for (int i = 0; i < 6; i++) if (southThreats[i] > 0) south++;
        return ((north - south) * DNA[4] * (side == NORTH ? 1 : -1));
    }

    /**
     * Compute the number of lofts with coefficient. Return the difference of
     * the numbers of lofts in each side and multiply with the DNA factor.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int lofts(int[] board,
                             int side) {
        int northLofts = 0;
        int southLofts = 0;
        for (int i = 0; i < 6; i++)
            if (board[i] > 11 && board[i] < 34 && (i + board[i] % 11) % 12 > 5)
                northLofts++;
        for (int i = 6; i < 12; i++)
            if (board[i] > 11 && board[i] < 34 && (i + board[i] % 11) % 12 < 6)
                southLofts++;
        return ((northLofts - southLofts) * DNA[5] * (side == NORTH ? 1 : -1));
    }

    /**
     * Compute the number of dams with coefficient. Return the difference
     * between the number of dams (consecutive holes that ) and multiply with
     * the DNA factor.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    private static int dams(int[] board,
                            int side) {
        int holeSize = 0;
        int maxHoleSize = 0;
        int boardIndex = 0;
        int note = 0;
        while (boardIndex < 6) {
            if (board[boardIndex] < 3) holeSize++;
            else {
                if (holeSize > maxHoleSize) maxHoleSize = holeSize;
                holeSize = 0;
            }
            boardIndex++;
        }
        if (holeSize > maxHoleSize) maxHoleSize = holeSize;
        note -= maxHoleSize;
        maxHoleSize = 0;
        holeSize = 0;
        while (boardIndex < 12) {
            if (board[boardIndex] < 3) holeSize++;
            else {
                if (holeSize > maxHoleSize) maxHoleSize = holeSize;
                holeSize = 0;
            }
            boardIndex++;
        }
        if (holeSize > maxHoleSize) maxHoleSize = holeSize;
        note += maxHoleSize;
        return note * DNA[6] * (side == NORTH ? 1 : -1);
    }

    /**
     * Evaluate a position for a given side.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    public int evaluate(int[] board,
                        int side) {
        return (absoluteAdvance(board, side) +
                potential(board, side) +
                mobility(board, side) +
                tacticMobility(board, side) +
                threats(board, side) +
                lofts(board, side) +
                dams(board, side));
    }

    /**
     * Evaluate the position in the end of a branch.
     *
     * @param board The board as an int[]
     * @param side  The side as an int
     * @return The note
     */
    public int endEvaluation(int[] board,
                             int side) {
        int difference = (board[12] - board[13]) * (side == NORTH ? 1 : -1);
        if (difference > 0) return (MAX_NOTE - 100 + difference);
        else if (difference < 0) return (MIN_NOTE + 100 - difference);
        else return 0;
    }
}

