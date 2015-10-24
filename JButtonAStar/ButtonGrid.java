package JButtonAStar;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*The class for the layout*/
// Y is length
// X is width
public class ButtonGrid
{
	private static final int BUTTON_SIZE = 60; // dimensions of each button
		// movement cost of moving diagonally
		// i.e. (the square root of 2) * 10
	private Node destination;
	private Node start;
	/*length and width to make sure nodes are actually on the grid*/
	private final int width;
	private final int length;
	JFrame frame = new JFrame(); // reference to the frame
	Node[][] grid; // 2d array that is the grid of JButtons
	// the constructor
	List<Node> openSet = new ArrayList<Node>();
		// we need these sorted
		// the set of nodes we haven't evaluated
	Set<Node> closedSet = new HashSet<Node>();
		// we don't need these sorted
		// the set of nodes we have already evaluated
	public ButtonGrid(int width, int length)
	{
		this.width = width; // set this grid's width
		this.length = length; // set this grid's length
		frame.setLayout(new GridLayout(width, length));
			// delegate GridLayout to take care of the frame's layout
		/*Now populate the JButtons*/
		grid= new Node[width][length];
		for (int y = 0; y < length; y++)
		{
			for (int x = 0; x < width; x++)
			{
				grid[x][y] = new Node(x,y);
				// instantiate the JButton object for each node in the grid
				// gives the JButton a name, that is the tuple (x,y)
				grid[x][y].setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
					// set the size to 10 by 10 for that button
				grid[x][y].setBackground(Color.white); // make the button white
				grid[x][y].setFont(new Font("Sans Serif", Font.PLAIN, 8));
				frame.add(grid[x][y]); // add that button to the grids
			}
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("A* Pathfinding Algorithm");
			// the frame will close when you press X
		frame.pack(); // makes the frame the proper size so everything fits
		frame.setVisible(true); // make the frame visible
	}
	/*Returns true if the path between parent and child Nodes clips a corner
	 * of a blocked tile*/
	private boolean cornerBlocked(Node parent, Node child)
	{
		int xDiff = parent.x - child.x;
		int yDiff = parent.y - child.y;

		if (xDiff == 0 || yDiff == 0)
		{
			return false;
			// if the movement is simply straight horizontal or
			// straight vertical then return false
		}
		// if movement is diagonal, these are the perpendicular nodes
		Node nodeA = grid[child.x][child.y + yDiff];
		Node nodeB = grid[child.x + xDiff][child.y];

		return nodeA.blocked || nodeB.blocked;
	}
	/*set point A*/
	private void setPointA(int x, int y)
	{
		this.grid[x][y].setText("A");
		this.grid[x][y].setBackground(Color.CYAN);
			// label point A and set it to some color
		this.start = grid[x][y];
		// set the reference start to be the node
		// we just set to be the start
	}
	/*set point B*/
	private void setPointB(int x, int y)
	{
		this.grid[x][y].setText("B");
		this.grid[x][y].setBackground(Color.MAGENTA);
			// label point B and set it to some color
		this.destination = grid[x][y];
		// set the reference destination to be the node
		// we just set to be the destination
	}
	/*set the heuristic of the node*/
	// that is the estimated movement cost from that node to the
	// destination node (point B)
	private void updateH(Node node)
	{
		int xDiff = Math.abs(node.x - destination.x);
		int yDiff = Math.abs(node.y - destination.y);
		// the difference in the coordinates between the specified node
		// and the destination node
		// that is, the Heuristic or the estimated distance from that node
		// and the destination
		node.hCost = (xDiff + yDiff) * 10;
	}
	/*Returns list of neighbors for the specified node*/
	private List<Node> getNeighbors(Node node)
	{
		List<Node> neighbors = new ArrayList<Node>();
		int nodeX = node.x; // the specified node's x coordinate
		int nodeY = node.y; // the specified node's y coordinate

		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				boolean sameNode = x == 0 && y == 0;
					// the node will not be the same as the
					// specified node
				boolean withinGrid = nodeX + x >= 0 && nodeX + x < width
							&& nodeY + y >= 0 && nodeY + y < length;
					// the node will be within the grid

				if (!sameNode && withinGrid)
				{
					neighbors.add(grid[nodeX + x][nodeY + y]);
					// add a node to the list with those coordinates
				}
			}
		}
		return neighbors;
	}
	/*Calculate the surroundings*/
	private void calcSurroundings(Node node)
	{
		List<Node> neighbors = getNeighbors(node);
			// get the list of neighbors
		for (int i = 0; i < neighbors.size(); i++)
			// iterate through all the neighbors
		{
			Node neighbor = neighbors.get(i);
			//neighbor.setBackground(Color.GRAY);
			if (!neighbor.blocked && !closedSet.contains(neighbor)
					&& !cornerBlocked(node, neighbor))
				// if the neighbor is traversable and it is not in the
				// closed set
			{
				int distanceToNeighbor = node.gCost + getDistance(neighbor, node);
					// another possible path to the neighbor, using this specified
					// node as a parent
				boolean openContainsNeighbor = openSet.contains(neighbor);
					// doeSet<Node> openSet = new TreeSet<Node>();s the openSet contain the neighbor?
				if (distanceToNeighbor < neighbor.gCost || !openContainsNeighbor)
					// if the new path is better, then make the specified node
					// the neighbor's parent
				{
					neighbor.gCost = distanceToNeighbor;
					updateH(neighbor);
					neighbor.setText(neighbor.fCost() + "");
					neighbor.parent = node;
					if (!openContainsNeighbor)
					{
						openSet.add(neighbor);
						// add the neighbor to the openSet
					}
				}
			}
		}
	}
	/*Calculate the gScore*/
	// I got this function from Sebastian Lague
	private int getDistance(Node start, Node destination)
	{
		int xDiff = Math.abs(destination.x - start.x);
			// the horizontal difference
		int yDiff = Math.abs(destination.y - start.y);
			// the vertical difference
		if (xDiff > yDiff)
		{
			return 14*yDiff + 10*(xDiff - yDiff);
		}
		return 14*xDiff + 10*(yDiff - xDiff);
	}
	/*the path-finding algorithm*/
	private void aStar()
	{
		boolean pathFound = false;
		Node root = start;
		root.gCost = 0;
		updateH(root);
		openSet.add(root);

		while (!pathFound)
		{
			Collections.sort(openSet);
				// sort the neighbors by their fCost
				// (lowest to highest)
			Node currentNode = openSet.get(0);
				// get the first element from the ArrayList
			if (currentNode == destination)
				// if we found a path
			{
				pathFound = true;
				System.out.println("done.");
				while (currentNode != start)
				{
					currentNode.setBackground(Color.RED);
					currentNode = currentNode.parent;
				}
			}
			else
			{
				openSet.remove(currentNode);
				closedSet.add(currentNode);
				calcSurroundings(currentNode);
			}
		}
	}
	public static void main(String[] args)
	{
		/*ADD START POINT AND END POINT*/
		int gridSize = 10;
		final ButtonGrid myGrid = new ButtonGrid(gridSize,gridSize);
		myGrid.setPointA(0, 0);
		myGrid.setPointB(9, 9);
		// start the a* algorithm when the user clicks the A tile
		myGrid.start.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myGrid.aStar();
				myGrid.start.setBackground(Color.RED);
			}
		});
	}
}
