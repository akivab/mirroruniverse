package mirroruniverse.mapcreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;

public class MapPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private final static Dimension GRID_DIMENSION = new Dimension(600, 600);
	private final static Dimension TEXT_DIMENSION = new Dimension(50, 30);
	
	int[][] map;
	int x_dimension;
	int y_dimension;
	
	JPanel controls;
	JPanel grid;
	
	JTextField xControl;
	JTextField yControl;
	JTextField mapName;
	
	JButton[][] buttons;
	
	public MapPanel(int x, int y)
	{
		x_dimension = x;
		y_dimension = y;
		map = new int[x_dimension][y_dimension];
		buttons = new JButton[x_dimension][y_dimension];

		grid = new JPanel();
		controls = new JPanel();
		fillMapPanel();
		fillControlPanel();
		
		this.setLayout(new BorderLayout());
		add(controls);
		add(grid, BorderLayout.SOUTH);
	}

	private void fillControlPanel()
	{
		JLabel label = new JLabel("X:");
		xControl = new JTextField();
		xControl.setText(String.valueOf(x_dimension));
		xControl.setPreferredSize(TEXT_DIMENSION);
		controls.add(label);
		controls.add(xControl);
		
		label = new JLabel("X:");
		yControl = new JTextField();
		yControl.setText(String.valueOf(x_dimension));
		yControl.setPreferredSize(TEXT_DIMENSION);
		controls.add(label);
		controls.add(yControl);
		
		JButton button = new JButton("Clear Map");
		button.setActionCommand("Command: clear");
		button.addActionListener(this);
		controls.add(button);

		button = new JButton("Fill with barriers");
		button.setActionCommand("Command: barriers");
		button.addActionListener(this);
		controls.add(button);

		button = new JButton("Load a Map");
		button.setActionCommand("Command: load");
		button.addActionListener(this);
		controls.add(button);

		label = new JLabel("Map Name:");
		mapName = new JTextField();
		mapName.setText("");
		mapName.setPreferredSize(TEXT_DIMENSION);
		controls.add(label);
		controls.add(mapName);
	}
	
	private void fillMapPanel()
	{
		grid.setLayout(new GridLayout(x_dimension, y_dimension));
		grid.setPreferredSize(GRID_DIMENSION);
		JButton button;
		for (int i = 0; i < x_dimension; ++i)
		{
			for (int j = 0; j < y_dimension; ++j)
			{
				button = new JButton();
				button.setOpaque(true);
				button.setActionCommand(i + "," + j);
				button.addActionListener(this);
				buttons[i][j] = button;
				colorButton(i, j);
				grid.add(button);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String actionCommand = arg0.getActionCommand();
		if (!actionCommand.contains("Command"))
		{
			changeButtonColor(arg0);
		}
		else if (actionCommand.contains("clear"))
		{
			replaceAll(-1, 0);
		}
		else if (actionCommand.contains("barriers"))
		{
			replaceAll(-1, 1);
		}
		else if (actionCommand.contains("load"))
		{
			loadFile();
		}
	}

	private void changeButtonColor(ActionEvent arg0)
	{
		JButton buttonClicked = (JButton)arg0.getSource();
		String[] sCoords = arg0.getActionCommand().split(",");
		int x = Integer.valueOf(sCoords[0]);
		int y = Integer.valueOf(sCoords[1]);

		if (MapCreator.setExit)
		{
			replaceAll(2, 0);
			map[x][y] = 2;
		}
		else if (MapCreator.setPlayerStart)
		{
			replaceAll(3, 0);
			map[x][y] = 3;
		}
		else if (buttonClicked.getBackground() == Color.LIGHT_GRAY)
		{
			map[x][y] = 1;
		}
		else 
		{
			map[x][y] = 0;
		}
		
		colorButton(x, y);
		
		MapCreator.setExit = false;
		MapCreator.setPlayerStart = false;
	}
	
	public void updateMapDimensions()
	{
		int newx = Integer.valueOf(xControl.getText());
		int newy = Integer.valueOf(yControl.getText());
		int[][] oldMap = map;
		x_dimension = newx;
		y_dimension = newy;
		map = new int[x_dimension][y_dimension];
		buttons = new JButton[x_dimension][y_dimension];
		
		fillMap(oldMap);
		grid.removeAll();
		fillMapPanel();
		grid.validate();
	}
	
	private void replaceAll(int num, int replaceWith)
	{
		boolean replaceAll = false;
		
		if(num < 0)
		{
			replaceAll = true;
		}
		
		for (int i = 0; i < map.length; ++i)
		{
			for (int j = 0; j < map[0].length; ++j)
			{
				if (map[i][j] == num || replaceAll)
				{
					map[i][j] = replaceWith;
					colorButton(i, j);
				}
			}
		}
	}
	
	private void colorButton(int x, int y)
	{
		switch(map[x][y])
		{
		case 0:
			buttons[x][y].setBackground(Color.LIGHT_GRAY);
			break;
		case 1:
			buttons[x][y].setBackground(Color.GRAY);
			break;
		case 2:
			buttons[x][y].setBackground(Color.GREEN);
			break;
		case 3:
			buttons[x][y].setBackground(Color.YELLOW);
			break;
		}

	}

	private void fillMap(int[][] otherMap)
	{
		int xDim = Math.min(otherMap.length, map.length);
		int yDim = Math.min(otherMap[0].length, map[0].length);
		for (int i = 0; i < xDim; ++i)
		{
			for (int j = 0; j < yDim; ++j)
			{
				map[i][j] = otherMap[i][j];
			}
		}
	}
	
	public String getMapName()
	{
		return mapName.getText();
	}
	
	private void loadFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory( new File( "maps" ) );
		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				mapName.setText(chooser.getSelectedFile().getName().replace(".txt", ""));
				
				Scanner mapScanner = new Scanner( new FileInputStream( chooser.getSelectedFile()) );
				Vector<int[]> lines = new Vector<int[]>();
				while (mapScanner.hasNext())
				{
					String[] lineNums = mapScanner.nextLine().split(" ");
					int[] nums = new int[lineNums.length];
					for(int i = 0; i < lineNums.length; ++i)
					{
						nums[i] = Integer.valueOf(lineNums[i]);
					}
					lines.add(nums);
				}
				
				x_dimension = lines.size();
				y_dimension = lines.get(0).length;
				map = new int[x_dimension][y_dimension];
				for (int i = 0; i < lines.size(); ++i)
				{
					map[i] = lines.get(i);
				}
				buttons = new JButton[x_dimension][y_dimension];

				grid.removeAll();
				fillMapPanel();
				grid.validate();

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
