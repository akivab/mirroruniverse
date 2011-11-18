/* 
 * 	$Id: GUI.java,v 1.4 2007/11/14 22:02:59 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package mirroruniverse.sim.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import mirroruniverse.sim.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public final class MUGUI extends JFrame implements ActionListener, ChangeListener
{
	private MUControlPanel controlPanel;
	private MUBoardPanel boardPanelL;
	private MUBoardPanel boardPanelR;
	private JPanel mapPanel;
	private JPanel gamePanel;
	private MUDialPanel dialPanel;
	
	private volatile boolean fast;
	private boolean loading = false;
	private int[] aintReplay;
	private int intStepIndex;
	
	private JFileChooser chooser = new JFileChooser();
	
	private int intTotalScore;
	private int intDiffScore;
	
	public MUGUI() {
		setTitle("My Empty Frame");
		setPreferredSize(new Dimension(1200, 900));
		setMinimumSize(new Dimension(1200, 700));
		setLocation(10,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlPanel = new MUControlPanel();
		controlPanel.load.setText("Load");

		
		gamePanel = new JPanel(new BorderLayout());
		gamePanel.setName("Game Play");
		gamePanel.add(controlPanel, BorderLayout.NORTH);
		
		mapPanel = new JPanel( new GridLayout( 1, 2 ) );
		boardPanelL = new MUBoardPanel();
		boardPanelR = new MUBoardPanel();

		mapPanel.add( boardPanelL );
		mapPanel.add( boardPanelR );
		
		gamePanel.add(mapPanel, BorderLayout.CENTER);
		
		dialPanel = new MUDialPanel();
		gamePanel.add( dialPanel, BorderLayout.SOUTH );
		dialPanel.setVisible( false );
		
		getContentPane().add( gamePanel );
		this.pack();
		this.setVisible(true);

		setupListeners();
	}
	
	private void setupListeners() {
		controlPanel.addListener(this);
		dialPanel.addListener(this);
	}

	public static void main(String[] args) {
		JFrame f = new MUGUI();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component c = this;
		String command = arg0.getActionCommand();
		if (command.compareToIgnoreCase("Load") == 0) {

			chooser.setCurrentDirectory( new File( "replays" ) );
			int returnVal = chooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				loading = true;

				try {
					Scanner replayScanner = new Scanner( new FileInputStream( chooser.getSelectedFile()) );
					String strMapConfigL = replayScanner.nextLine();
					String strMapConfigR = replayScanner.nextLine();
					String strReplay = replayScanner.nextLine();
					String strScore = replayScanner.nextLine();
					
					boardPanelL.setBoard( strMapConfigL );
					boardPanelL.recalculateDimensions();
					boardPanelR.setBoard( strMapConfigR );
					boardPanelR.recalculateDimensions();
					this.repaint();
					
					String[] astrReplay = strReplay.split( ", " );
					aintReplay = new int[ astrReplay.length ];
					for ( int i = 0; i < astrReplay.length; i ++ )
					{
						aintReplay[ i ] = Integer.parseInt( astrReplay[ i ] );
					}
					
					if ( !controlPanel.manual.isSelected() )
					{
						String[] astrScore = strScore.split( ", " );
						intDiffScore = Integer.parseInt( astrScore[ 0 ] );
						intTotalScore = Integer.parseInt( astrScore[ 1 ] );
						controlPanel.setScore( intTotalScore, intDiffScore );
					}
					intStepIndex = 0;
				} catch (IOException e) {
					System.err.println("Error reading replay file:"
							+ e.getMessage());
					e.printStackTrace();
					System.exit(-1);
				}

//				JOptionPane.showMessageDialog((Frame) c,
//						"File loaded successfully.", "Success",
//						JOptionPane.INFORMATION_MESSAGE);
				loading = false;
				// }catch(IOException e){
				// // e.printStackTrace();
				// JOptionPane.showMessageDialog( (Frame)c, e, "Load Error",
				// JOptionPane.ERROR_MESSAGE);
				// loading = false;
				// }
				if ( !controlPanel.manual.isSelected() )
				{
					controlPanel.load.setEnabled(false);
					fast = false;
					if (true) {
						controlPanel.stop.setEnabled(true);
						controlPanel.play.setEnabled(true);
						controlPanel.pause.setEnabled(false);
						controlPanel.step.setEnabled(true);
					} else {
						// game set up failed. Turn the right buttons on/off.
						controlPanel.load.setEnabled(true);
						controlPanel.tournament.setEnabled(true);
					}
				}
			}
			// disable the begin button and the configPanel (freeze the config)
			
			this.repaint();
		} else if (command.compareToIgnoreCase("Step") == 0) {
			nextStep();
		} else if (command.compareToIgnoreCase("Play") == 0) {
			controlPanel.step.setEnabled(false);
			controlPanel.play.setEnabled(false);
			controlPanel.pause.setEnabled(true);
			controlPanel.load.setEnabled(false);
			controlPanel.stop.setEnabled(true);
			fast = true;
			GameRunner runner = new GameRunner(controlPanel.getSpeedSlider());
			runner.setName("Game Runner");
			runner.start();
		} else if (command.equalsIgnoreCase("Pause")) {
			fast = false;
			controlPanel.step.setEnabled(true);
			controlPanel.play.setEnabled(true);
			controlPanel.pause.setEnabled(false);
			controlPanel.load.setEnabled(false);
			controlPanel.stop.setEnabled(true);
		} else if (command.compareToIgnoreCase("Stop") == 0) {
			fast = false;
			controlPanel.stop.setEnabled(false);
			controlPanel.play.setEnabled(false);
			controlPanel.pause.setEnabled(false);
			controlPanel.step.setEnabled(false);
			controlPanel.load.setEnabled(true);
			intStepIndex = 0;
		} else if (command.compareToIgnoreCase("ManualSwitch") == 0) {
			fast = false;
			if ( controlPanel.manual.isSelected() )
			{
				dialPanel.setVisible( true );
				controlPanel.stop.setEnabled(false);
				controlPanel.play.setEnabled(false);
				controlPanel.pause.setEnabled(false);
				controlPanel.step.setEnabled(false);
				controlPanel.load.setEnabled(true);
				controlPanel.totalScore.setText( "--" );
				controlPanel.diffScore.setText( "0" );
				intDiffScore = 0;
			}
			else
			{
				dialPanel.setVisible( false );
				controlPanel.stop.setEnabled(false);
				controlPanel.play.setEnabled(false);
				controlPanel.pause.setEnabled(false);
				controlPanel.step.setEnabled(false);
				controlPanel.load.setEnabled(true);
				intStepIndex = 0;
			}			
		} else if (command.compareToIgnoreCase("Fog") == 0) {
			if ( controlPanel.fog.isSelected() )
			{
				boardPanelL.setFog( true );
				boardPanelR.setFog( true );
			}
			else
			{
				boardPanelL.setFog( false );
				boardPanelR.setFog( false );
			}
			this.repaint();
		} else if (command.compareToIgnoreCase("LU") == 0) {
			nextStep( 4 );
		} else if (command.compareToIgnoreCase("U") == 0) {
			nextStep( 3 );
		} else if (command.compareToIgnoreCase("RU") == 0) {
			nextStep( 2 );
		} else if (command.compareToIgnoreCase("L") == 0) {
			nextStep( 5 );
		} else if (command.compareToIgnoreCase("R") == 0) {
			nextStep( 1 );
		} else if (command.compareToIgnoreCase("LD") == 0) {
			nextStep( 6 );
		} else if (command.compareToIgnoreCase("D") == 0) {
			nextStep( 7 );
		} else if (command.compareToIgnoreCase("RD") == 0) {
			nextStep( 8 );
		} else {
			throw new RuntimeException("Unknow Action Command: " + command);
		}
	}
	
	private class GameRunner extends Thread implements ChangeListener {
		private JSlider slider;
		private int delay;

		public GameRunner(JSlider slider) {
			this.slider = slider;
		}

		public void run() {
			delay = slider.getValue();
			slider.addChangeListener(this);
			while (fast && intStepIndex < aintReplay.length ) {
				try {
					nextStep();
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// this should not happen!
					e.printStackTrace();
				}
			}
			slider.removeChangeListener(this);
		}

		public void stateChanged(ChangeEvent arg0) {
			if (arg0.getSource().equals(slider)) {
				delay = ((JSlider) arg0.getSource()).getValue();
			}
		}
	}
	
	private void nextStep( int intMoveTo) 
	{
		if ( intStepIndex < aintReplay.length )
		{
			boardPanelL.move( intMoveTo );
			boardPanelR.move( intMoveTo );
			intStepIndex ++ ;
		}
		if ( boardPanelL.getFinished() ^ boardPanelR.getFinished() )
		{
			intDiffScore ++;
			controlPanel.diffScore.setText( Integer.toString( intDiffScore ) );
		}
		controlPanel.setStepText( intStepIndex );
	}

	private void nextStep() 
	{
		if ( intStepIndex < aintReplay.length )
		{
			boardPanelL.move( aintReplay[ intStepIndex ] );
			boardPanelR.move( aintReplay[ intStepIndex ] );
			intStepIndex ++ ;
		}
		if ( intStepIndex == aintReplay.length )
		{
			controlPanel.step.setEnabled( false );
		}
		controlPanel.setStepText( intStepIndex );
	}

	@Override
	public void stateChanged(ChangeEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
}
