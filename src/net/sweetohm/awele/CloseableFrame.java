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

import java.awt.Event;
import java.awt.Frame;

/**
 * This is a Frame that close when clicking on ... the close button!
 *
 * @author Michel Casabianca
 */
public class CloseableFrame
        extends Frame {

    public CloseableFrame() {
    }

    public CloseableFrame(String title) {
        super(title);
    }

    /**
     * @deprecated As of JDK version 1.1
     * replaced by processEvent(AWTEvent).
     */
    public boolean handleEvent(Event evt) {
        if (evt.id == Event.WINDOW_DESTROY) {
            System.exit(0);
            return true;
        } else return super.handleEvent(evt);
    }
}
