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

import java.awt.*;

import java.applet.Applet;

/**
 * This is the entry point of the Awele program.
 */
public class Main extends Applet implements Runnable {

    /**
     * Tell if we are running in a standalone program
     */
    private static boolean isProgram = false;
    /**
     * Thread to search next move
     */
    private Thread thread;
    /**
     * Board
     */
    private int[] board;
    /**
     * Screen picture
     */
    private Image ecran;
    /**
     * mark picture
     */
    private Image marque;
    /**
     * light bulb picture
     */
    private Image ampoule;
    /**
     * double buffering RAM picture
     */
    private Image image;
    /**
     * font for number of seeds printing
     */
    private Font fonte;
    /**
     * FontMetrics
     */
    private FontMetrics fm;
    /**
     * AI
     */
    private IA ia = new IA();
    /**
     * tells if machine should play
     */
    private boolean traitMachine = false;
    /**
     * tells if machine is computing
     */
    private boolean pensif = false;
    /**
     * constants for north, south and null
     */
    private static final int NORD = 1;
    private static final int SUD = -1;
    private static final int NUL = 100;
    /**
     * user interface screen coordinates
     */
    private final int[][] Coord = {
            {310, 110, 339, 133},
            {260, 110, 292, 132},
            {212, 110, 242, 132},
            {157, 110, 187, 133},
            {107, 110, 137, 131},
            {60, 110, 90, 132},
            {58, 151, 88, 181},
            {110, 151, 136, 181},
            {153, 152, 183, 182},
            {209, 152, 239, 182},
            {257, 152, 287, 182},
            {309, 154, 339, 184},
            {19, 128, 50, 158},
            {350, 129, 380, 159},
            {27, 222, 84, 279},
            {319, 224, 375, 280},
            {113, 251, 135, 273},
            {141, 251, 164, 274},
            {169, 251, 194, 274},
            {206, 251, 227, 274},
            {233, 251, 257, 274},
            {262, 251, 292, 274}
    };

    /**
     * Main for program.
     */
    public static void main(String[] args) {
        isProgram = true;
        CloseableFrame frame = new CloseableFrame(Constants.COPYRIGHT);
        Main applet = new Main();
        frame.add("Center", applet);
        frame.setResizable(false);
        frame.pack();
        applet.init();
        applet.start();
        frame.show();
    }

    /**
     * Applet init.
     */
    public void init() {
        chargerImages();
        fonte = new Font("Courrier", Font.PLAIN, 20);
        fm = getFontMetrics(fonte);
        ia.Init();
        board = new int[14];
        System.arraycopy(ia.board, 0, board, 0, board.length);
        resize(400, 300);
        if (!isProgram)
            getAppletContext().showStatus(Constants.COPYRIGHT);
    }

    /**
     * Pictures loading.
     */
    private void chargerImages() {
        if (!isProgram)
            getAppletContext().showStatus("Chargement des images...");
        MediaTracker tracker = new MediaTracker(this);
        ecran = Toolkit.getDefaultToolkit().getImage(
                this.getClass().getClassLoader().getResource(
                        "net/sweetohm/awele/screen.gif"));
        marque = Toolkit.getDefaultToolkit().getImage(
                this.getClass().getClassLoader().getResource(
                        "net/sweetohm/awele/marque.gif"));
        ampoule = Toolkit.getDefaultToolkit().getImage(
                this.getClass().getClassLoader().getResource(
                        "net/sweetohm/awele/ampoule.gif"));
        tracker.addImage(ecran, 0);
        tracker.addImage(marque, 0);
        tracker.addImage(ampoule, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Minimum size for applet.
     */
    public Dimension getMinimumSize() {
        return new Dimension(400, 300);
    }

    /**
     * Preferred size for applet.
     */
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    /**
     * Draw the board.
     */
    private void dessinerEcran() {
        Graphics g = image.getGraphics();
        g.drawImage(ecran, 0, 0, this);
        for (int i = 0; i < 14; i++)
            if (board[i] != 0)
                dessinerCase(i, g);
        for (int i = 0; i < ia.level / 2; i++)
            imageCentree(Coord[i + 16], marque, g);
        if (pensif)
            g.drawImage(ampoule, 180, 205, this);
    }

    /**
     * Print a board hole.
     */
    private void dessinerCase(int C, Graphics g) {
        int X, Y;
        g.setColor(Color.yellow);
        g.setFont(fonte);
        String S = String.valueOf(board[C]);
        X = (Coord[C][0] + Coord[C][2] - fm.stringWidth(S)) / 2;
        Y = (Coord[C][1] + Coord[C][3] + fm.getHeight() / 2) / 2;
        g.drawString(S, X, Y);
    }

    /**
     * Print a centered picture.
     */
    private void imageCentree(int[] C, Image I, Graphics g) {
        g.drawImage(I, (C[0] + C[2] - I.getWidth(this)) / 2, (C[1] + C[3] - I
                .getHeight(this)) / 2, this);
    }

    /**
     * Draw the applet.
     */
    public void paint(Graphics g) {
        if (image == null) {
            image = createImage(400, 300);
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dessinerEcran();
        g.drawImage(image, 0, 0, this);
    }

    /**
     * Draw the applet without the background.
     */
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Manage mouse clicks.
     */
    public boolean mouseDown(Event evt, int x, int y) {
        if (pensif)
            return true;
        int clic = NUL;
        for (int i = 0; i < 22; i++)
            if (x > Coord[i][0] && x < Coord[i][2] && y > Coord[i][1]
                    && y < Coord[i][3]) {
                clic = i;
                break;
            }
        if (clic > 5 && clic < 12) {
            if (ia.isLegal(clic, SUD)) {
                ia.play(clic, SUD);
                System.arraycopy(ia.board, 0, board, 0, board.length);
                repaint();
                traitMachine = true;
            }
        } else if (clic == 14) {
            int temp;
            for (int i = 0; i < 6; i++) {
                temp = ia.board[i];
                ia.board[i] = ia.board[i + 6];
                ia.board[i + 6] = temp;
            }
            temp = ia.board[12];
            ia.board[12] = ia.board[13];
            ia.board[13] = temp;
            System.arraycopy(ia.board, 0, board, 0, board.length);
            pensif = false;
            repaint();
            traitMachine = true;
        } else if (clic == 15) {
            ia.Init();
            System.arraycopy(ia.board, 0, board, 0, board.length);
            pensif = false;
            repaint();
        } else if (clic > 15 && clic < 22) {
            ia.level = (clic - 15) * 2;
            pensif = false;
            repaint();
        }
        return (true);
    }

    /**
     * Mouse button is released.
     */
    public boolean mouseUp(Event ev, int x, int y) {
        if (traitMachine) {
            traitMachine = false;
            pensif = true;
            repaint();
            thread = new Thread(this);
            thread.start();
        }
        return (true);
    }

    /**
     * Thread for computing.
     */
    public void run() {
        ia.play(ia.getMove(NORD), NORD);
        System.arraycopy(ia.board, 0, board, 0, board.length);
        pensif = false;
        repaint();
    }
}

