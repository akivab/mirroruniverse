package mirroruniverse.mapcreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

public class MapCreator implements ActionListener {
	private final static int DEFAULT_MAP_DIMENSION = 10;
	private final static Dimension GRID_DIMENSION = new Dimension(600, 600);
	private final static int MAP1 = 0;
	private final static int MAP2 = 1;
	private final static int X_DIM = 0;
	private final static int Y_DIM = 1;

	public static boolean setPlayerStart = false;
	public static boolean setExit = false;

	JFrame frame;
	MapPanel map1Panel;
	MapPanel map2Panel;
	JTextField x1Control;
	JTextField y1Control;
	JTextField x2Control;
	JTextField y2Control;
	
	int[][] mapDimensions;
	
	public MapCreator()
	{
		new MapCreator(DEFAULT_MAP_DIMENSION, DEFAULT_MAP_DIMENSION);
	}
	
	public MapCreator(int xDimension, int yDimension)
	{
		mapDimensions = new int[2][2];
		mapDimensions[MAP1][X_DIM] = xDimension;
		mapDimensions[MAP1][Y_DIM] = yDimension;
		mapDimensions[MAP2][X_DIM] = xDimension;
		mapDimensions[MAP2][Y_DIM] = yDimension;
		
		createAndRun();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MapCreator();
	}
	
	private void createAndRun()
	{
		frame = new JFrame();
		JPanel controlPanel = createAndFillControl();
		map1Panel = new MapPanel(mapDimensions[MAP1][X_DIM], mapDimensions[MAP1][Y_DIM]);
		map2Panel = new MapPanel(mapDimensions[MAP2][X_DIM], mapDimensions[MAP2][Y_DIM]);
		JPanel mapsPanel = new JPanel();
		mapsPanel.setLayout(new FlowLayout());
		mapsPanel.add(map1Panel);
		mapsPanel.add(map2Panel);
		frame.setLayout(new BorderLayout());
		frame.add(controlPanel, BorderLayout.NORTH);
		frame.add(mapsPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JPanel createAndFillControl()
	{
		JPanel cp = new JPanel();
		cp.setLayout(new FlowLayout());

		/* create player start control */
		JButton button = new JButton("Place Player Start");
		button.setActionCommand("CONTROL: playerStart");
		button.addActionListener(this);
		cp.add(button);
		
		/* create exit control */
		button = new JButton("Place Exit Square");
		button.setActionCommand("CONTROL: exit");
		button.addActionListener(this);
		cp.add(button);
		
		/* create update dimensions control */
		button = new JButton("Update Dimensions");
		button.setActionCommand("CONTROL: update");
		button.addActionListener(this);
		cp.add(button);

		/* create save control */
		button = new JButton("Save Map");
		button.setActionCommand("CONTROL: save");
		button.addActionListener(this);
		cp.add(button);

		/* return created panel */
		return cp;
	}	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String actionCommand = arg0.getActionCommand();
		handleCommandAction(actionCommand);
	}
	
	
	private void handleCommandAction(String actionCommand)
	{
		if (actionCommand.contains("update"))
		{
			updateMapDimensions();
		}
		else if (actionCommand.contains("playerStart"))
		{
			setPlayerStart = true;
		}
		else if (actionCommand.contains("exit"))
		{
			setExit = true;
		}
		else if (actionCommand.contains("save"))
		{
			printMapText();
		}
	}
	
	private void updateMapDimensions()
	{
		map1Panel.updateMapDimensions();
		map2Panel.updateMapDimensions();
		
//		frame.repaint();
	}
	
	private void printMapText()
	{
		String map1Name = map1Panel.getMapName();
		String map2Name = map2Panel.getMapName();
		FileWriter fw;
		BufferedWriter bw;
		
		if (map1Name.isEmpty())
		{
			map1Name = "map1";
		}
		if (map2Name.isEmpty())
		{
			map2Name = "map1";
		}
		
		try {
			fw = new FileWriter("maps/" + map1Name + ".txt");
			bw = new BufferedWriter(fw);
			printToBW(map1Panel.map, bw);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			fw = new FileWriter("maps/" + map2Name + ".txt");
			bw = new BufferedWriter(fw);
			printToBW(map2Panel.map, bw);
			bw.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printToBW(int[][] map, BufferedWriter bw) throws IOException
	{
		for (int i = 0; i < map.length; ++i)
		{
			for (int j = 0; j < map[0].length; ++j)
			{
				bw.write(map[i][j] + " ");
			}
			bw.write("\n");
		}
	}

}
