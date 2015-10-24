package JButtonAStar;

import java.awt.event.InputEvent;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Node extends JButton implements Comparable<Node>
{
    /*Super Constructors*/
    // delegate to the superclass default constructor
    Node(int x, int y) {
        super();
        blocked = false; // by default you can traverse the node
        this.x = x;
        this.y = y;
        this.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
             if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
                    // Shift is down...
                 if (blocked)
                     // if the tile was already blocked
                 {
                     // unblock it upon click
                     blocked = false;
                     setBackground(Color.white);
                 }
                 else
                 {
                     // if it was not blocked, make it blocked upon click
                     blocked = true;
                     setBackground(Color.BLACK);
                 }
                }
            }
        });
    }
    // delegate to the superclass String constructor
    Node(int x, int y, String s) {
        super(s); // this node can be traversed by default
        blocked = false;
        this.x = x;
        this.y = y;
    }
    /*Fields for A-Star*/
    public final int x;
    public final int y;
    public boolean blocked; // is this node blocked from being traversed?
    public int gCost; // cost from start node (A) to this node
    public int hCost; // (heuristic) cost from node to destination (B)
    public Node parent; // the parent node
    @Override
    public String toString()
    {
        return new String(String.format("(%d,%d)", x,y));
    }
    /*simply return the fCost*/
    public int fCost()
    {
        return gCost + hCost;
    }
    @Override
    public int compareTo(Node o) {
        return this.fCost() - o.fCost();
    }
}
