/*
 * DPP - Serious Distributed Pair Programming
 * (c) Freie Universitšt Berlin - Fachbereich Mathematik und Informatik - 2006
 * (c) Riad Djemili - 2006
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package de.fu_berlin.inf.dpp;

import de.fu_berlin.inf.dpp.net.JID;

public class User {
    private JID     jid;

    public User(JID jid) {
        this.jid = jid;
    }

    public JID getJid() {
        return jid;
    }
    
    @Override
    public String toString() {
        return jid.getName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User other = (User)obj;
            return jid.equals(other.jid);
        }
        
        return false;
    }
}
