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
 * IA for the game. This IA implements Minimax and Alpha-Beta well known algorithmes.
 * Note that this class uses <code>Eval</code> one for position evaluation.
 */
class IA {

    /**
     * Evaluation function
     */
    private Eval eval = new Eval();
    /**
     * Board
     */
    public int[] board = new int[14];
    /**
     * Thinking depth in half plays
     */
    public int level = 8;
    /**
     * Side to be considered in search function
     */
    private int side;
    /**
     * Side for which we are looking for a move
     */
    private int searchSide;
    /**
     * Search depth in half plays
     */
    private int searchDepth;
    /**
     * Maximum search depth
     */
    public static final int MAX_SEARCH_DEPTH = 16;
    /**
     * Constant for north side
     */
    public static final int NORTH = 1;
    /**
     * Constant for south side
     */
    public static final int SOUTH = -1;
    /**
     * Constant for void move
     */
    public static final int NULL = 100;
    /**
     * Notes in minimax tree
     */
    private int[] Notes = new int[MAX_SEARCH_DEPTH];

    /**
     * Board initialization.
     */
    public void Init() {
        for (int i = 0; i < 12; i++)
            board[i] = 4;
        board[12] = 0;
        board[13] = 0;
    }

    /**
     * Tests if a given move is legal.
     *
     * @param move Move to consider as an <code>int</code>
     * @param side Side to consider as an <code>int</code>
     * @return A <code>boolean</code> telling if the move is legal
     */
    public boolean isLegal(int move, int side) {
        // copy the board for backup
        int[] P = new int[14];
        System.arraycopy(board, 0, P, 0, board.length);
        // play the move and check that opposite side has seeds
        if (play(move, side)) {
            int S = 0;
            for (int i = (side == NORTH ? 6 : 0);
                 i < (side == NORTH ? 12 : 6); i++)
                S += board[i];
            System.arraycopy(P, 0, board, 0, board.length);
            if (S != 0)
                return (true);
            else
                return (false);
        } else
            return (false);
    }

    /**
     * Play a given move.
     *
     * @param move The move to play as an <code>int</code>
     * @param side The side as an <code>int</code>
     * @return A <code>boolean</code> that tells if the move is legal
     */
    public boolean play(int move, int side) {
        // copy the board for backup
        int[] P = new int[14];
        System.arraycopy(board, 0, P, 0, board.length);
        int seedsCount;
        int jumpDistance;
        int seedsMoved;
        // if move is in the right side
        if (move >= (side == NORTH ? 0 : 6) &&
                move < (side == NORTH ? 6 : 12)) {
            // if a seed in the hole
            if (board[move] != 0) {
                seedsCount = board[move];
                jumpDistance = seedsCount % 11;
                seedsMoved = seedsCount / 11;
                // move the seeds on the board
                for (int i = 1; i < 12; i++) {
                    board[(move + i) % 12] += seedsMoved + (i > jumpDistance ? 0 : 1);
                }
                board[move] = 0;
                int hole = (move + jumpDistance) % 12;
                // while we are in the opposite side, we capture seeds if there are
                // more than one and less than four
                while (hole >= (side == NORTH ? 6 : 0)
                        && (hole < (side == NORTH ? 12 : 6)
                        && board[hole] > 1
                        && board[hole] < 4)) {
                    board[side == NORTH ? 12 : 13] +=
                            board[hole];
                    board[hole] = 0;
                    hole--;
                }
                int seedsCountOppositeSide = 0;
                for (int i = (side == NORTH ? 6 : 0);
                     i < (side == NORTH ? 12 : 6); i++)
                    seedsCountOppositeSide += board[i];
                if (seedsCountOppositeSide != 0)
                    return (true);
                else {
                    System.arraycopy(P, 0, board, 0, board.length);
                    return (false);
                }
            } else
                return (false);
        } else
            return (false);
    }

    /**
     * Recurrent function for search of moves.
     *
     * @return
     */
    private int search() {
        int[] boardCopy = new int[14];
        int noteMiniMax = ((searchDepth & 1) != 0 ?
                Eval.MAX_NOTE : Eval.MIN_NOTE);
        int CoupMM = NULL;
        boolean Jouable = false;
        if (searchDepth >= level) {
            noteMiniMax = eval.evaluate(board, searchSide);
        } else {
            System.arraycopy(board, 0, boardCopy, 0, board.length);
            for (int i = (side == NORTH ? 0 : 6);
                 i < (side == NORTH ? 6 : 12); i++) {
                if (play(i, side)) {
                    Jouable = true;
                    side *= -1;
                    searchDepth++;
                    int Note = search();
                    // suivante
                    searchDepth--;
                    side *= -1;
                    if (Note == noteMiniMax && Math.random() > 0.5) {
                        noteMiniMax = Note;
                        CoupMM = i;
                    } else if ((searchDepth & 1) != 0 ?
                            Note < noteMiniMax :
                            Note > noteMiniMax) {
                        noteMiniMax = Note;
                        CoupMM = i;
                    }
                    System.arraycopy(boardCopy, 0, board, 0, board.length);
                    if (((searchDepth & 1) != 0 &&
                            noteMiniMax < Notes[searchDepth])
                            || ((searchDepth & 1) == 0 &&
                            noteMiniMax > Notes[searchDepth])) {
                        return (noteMiniMax);
                    }
                }
            }
            if (!Jouable) {
                noteMiniMax = eval.endEvaluation(board, searchSide);
            }
            if ((searchDepth & 1) != 0 ?
                    noteMiniMax > Notes[searchDepth] :
                    noteMiniMax < Notes[searchDepth]) {
                Notes[searchDepth] = noteMiniMax;
            }
        }
        Notes[searchDepth + 1] = ((searchDepth & 1) != 0 ?
                Eval.MAX_NOTE : Eval.MIN_NOTE);
        return (searchDepth != 0 ? noteMiniMax : CoupMM);
    }

    public int getMove(int Cm) {
        int Cp;
        int unCp = NULL;
        int NbCp = 0;
        for (Cp = (Cm == NORTH ? 0 : 6);
             Cp < (Cm == NORTH ? 6 : 12); Cp++) {
            if (isLegal(Cp, Cm)) {
                unCp = Cp;
                NbCp++;
            }
        }
        if (NbCp == 0)
            return (NULL);
        if (NbCp == 1)
            return (unCp);
        side = Cm;
        searchSide = Cm;
        searchDepth = 0;
        for (int i = 0; i < MAX_SEARCH_DEPTH; i++) {
            Notes[i] = ((i & 1) != 0 ?
                    Eval.MIN_NOTE : Eval.MAX_NOTE);
        }
        Cp = search();
        return (Cp);
    }
}
