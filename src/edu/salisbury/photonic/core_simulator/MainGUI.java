package edu.salisbury.photonic.core_simulator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


public class MainGUI {
	public static int totalRequestingTime = 0;
	public static int totalTasks = 0;
	
	protected Shell shellRONoC;
	protected Display display;
	protected ProgressBar progressBar;
	protected TabFolder tabFolderMenu;
	private String simulatorTopology = "Ring";
	private int simulatorFlitPacketSize = 64; 
	private int simulatorTearDownTime = 1;
	//private int simulatorNumOfSwitches = 0;
	private Canvas canvasTopologyPreview;;
	public static Label lblConsoleOutput;


	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainGUI window = new MainGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shellRONoC.open();
		shellRONoC.layout();
		while (!shellRONoC.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shellRONoC = new Shell();
		shellRONoC.setSize(920, 635);
		shellRONoC.setText("REU_15 : Reconfigurable ONoC Simulator");
		shellRONoC.setLayout(null);
		
		tabFolderMenu = new TabFolder(shellRONoC, SWT.NONE);
		tabFolderMenu.setLocation(0, 0);
		tabFolderMenu.setSize(903, 597);
		
		/////////////SIMULATOR TAB/////////////////////////
		
		TabItem tbtmSimulator = new TabItem(tabFolderMenu, SWT.NONE);
		tbtmSimulator.setText("Simulator");
		
		Group grpSimulator = new Group(tabFolderMenu, SWT.NONE);
		tbtmSimulator.setControl(grpSimulator);
		
		//Architecture Options... choose number of nodes, topology...
		createArchitectureOptions(grpSimulator);
		
		//Architecture preview group... displays what the generic topology
		createArchitecturePreview(grpSimulator);
		
		//Console output group... maybe print out the latency at the end, or the current subtask
		createConsoleOutput(grpSimulator);
		
		//Simulator button... start button
		createSimulatorButton(grpSimulator);
		///////////////////////////////////////////////////
		
		//_______________________________________________//
		
		//////////////DATA ANALYZER TAB////////////////////
		TabItem tbtmDataAnalyzer = new TabItem(tabFolderMenu, SWT.NONE);
		tbtmDataAnalyzer.setText("Data Analyzer");
		
		
		///////////////////////////////////////////////////
		

	}//end create contents function

	private void createArchitectureOptions(Group group) {
				
		Group grpArchitectureOptions = new Group(group, SWT.NONE);
		grpArchitectureOptions.setBounds(10, 10, 875, 223);
		grpArchitectureOptions.setText("Architecture Options");
		
		ToolBar toolBarTopology = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.VERTICAL);
		toolBarTopology.setBounds(11, 41, 280, 172);
		
		ToolItem tltmRingTopology = new ToolItem(toolBarTopology, SWT.RADIO);
		tltmRingTopology.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorTopology = "Ring";
				drawRingTopology();
			}
		});
		tltmRingTopology.setSelection(true);
		tltmRingTopology.setText("o Ring Topology");
		
		ToolBar toolBarFlitPacketSize = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.FLAT | SWT.VERTICAL);
		toolBarFlitPacketSize.setBounds(302, 41, 280, 172);
		
		ToolItem tltm16FlitPacket = new ToolItem(toolBarFlitPacketSize, SWT.RADIO);
		tltm16FlitPacket.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorFlitPacketSize = 16;
			}
		});
		tltm16FlitPacket.setText("o 16 bits");
		
		ToolItem tltm32FlitPacket = new ToolItem(toolBarFlitPacketSize, SWT.RADIO);
		tltm32FlitPacket.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorFlitPacketSize = 32;
			}
		});
		tltm32FlitPacket.setText("o 32 bits");
		
		ToolItem tltm64FlitPacket = new ToolItem(toolBarFlitPacketSize, SWT.RADIO);
		tltm64FlitPacket.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorFlitPacketSize = 64;
			}
		});
		tltm64FlitPacket.setWidth(40);
		tltm64FlitPacket.setSelection(true);
		tltm64FlitPacket.setText("o 64 bits");
		
		Label lblFlitPacketSize = new Label(grpArchitectureOptions, SWT.NONE);
		lblFlitPacketSize.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblFlitPacketSize.setBounds(377, 20, 131, 15);
		lblFlitPacketSize.setText("Size of Flit Packet");
		
		Label lblTopology = new Label(grpArchitectureOptions, SWT.NONE);
		lblTopology.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblTopology.setBounds(124, 20, 55, 15);
		lblTopology.setText("Topology");
		
		Label lblTearDownTime = new Label(grpArchitectureOptions, SWT.NONE);
		lblTearDownTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblTearDownTime.setBounds(688, 20, 92, 15);
		lblTearDownTime.setText("Tear Down Time");
		
		ToolBar toolBarTearDownTime = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.FLAT | SWT.VERTICAL);
		toolBarTearDownTime.setBounds(594, 41, 271, 66);
		
		ToolItem tltm1CC = new ToolItem(toolBarTearDownTime, SWT.RADIO);
		tltm1CC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorTearDownTime = 1;
			}
		});
		tltm1CC.setSelection(true);
		tltm1CC.setText("o 1 Clock Cycle");
		
		Label lblMrrSwitches = new Label(grpArchitectureOptions, SWT.NONE);
		lblMrrSwitches.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblMrrSwitches.setBounds(690, 126, 88, 15);
		lblMrrSwitches.setText("MRR Switches");
		
		ToolBar toolBarMRRSwitches = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.FLAT | SWT.VERTICAL);
		toolBarMRRSwitches.setBounds(594, 147, 271, 66);
		
		ToolItem tltm0Switches = new ToolItem(toolBarMRRSwitches, SWT.RADIO);
		tltm0Switches.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//simulatorNumOfSwitches = 0;
			}
		});
		tltm0Switches.setSelection(true);
		tltm0Switches.setText("o 0 Switches");
		//End architecture option group setup
		
	}
	
	private void createArchitecturePreview(Group group) {
		Group grpArchitecturePreview = new Group(group, SWT.NONE);
		grpArchitecturePreview.setLocation(10, 239);
		grpArchitecturePreview.setSize(875, 244);
		grpArchitecturePreview.setText("Architecture Preview");
		
		canvasTopologyPreview = new Canvas(grpArchitecturePreview, SWT.BORDER | SWT.NO_REDRAW_RESIZE);
		canvasTopologyPreview.setBounds(10, 23, 855, 211);
		drawRingTopology(); //default preview
		//end architecture preview group setup
		
	}
	
	protected void drawRingTopology() {	
		canvasTopologyPreview.setFocus();
		canvasTopologyPreview.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				int diameter = 50;
				int xInit = 10 + 48;
				int yInit = 23 + 15;
				int xOffset = 46;
				int yOffset = 41;
				int x1 = xInit + diameter;
				int y1 = yInit + diameter/2;
				int x2;
				int y2 = yInit + diameter + yOffset + diameter/2;
				int coreID = 1;
				int coreIDRow2 = 16;
				//draw left and right vertical link
				e.gc.drawLine(xInit + diameter/2, y1 + diameter/2, xInit + diameter/2, y1 + diameter/2 + yOffset);
				e.gc.drawLine(xInit + (diameter + xOffset)*7 + diameter/2, y1 + diameter/2, xInit + (diameter + xOffset)*7 + diameter/2, y1 + diameter/2 + yOffset);
				//draw body cores and horizontal links
				for (int i = 0; i < 8; i++) {
					e.gc.drawOval(xInit + xOffset*i, yInit, diameter, diameter);
					e.gc.drawText("" + coreID, xInit + diameter/2 + i*xOffset - 1, y1 - 6);
					e.gc.drawOval(xInit + xOffset*i, yInit + diameter + yOffset, diameter, diameter);
					if (coreIDRow2 == 9) {
						e.gc.drawText("" + coreIDRow2, xInit + diameter/2 + i*xOffset - 1, y2 - 6);
					} else {
						e.gc.drawText("" + coreIDRow2, xInit + diameter/2 + i*xOffset - 5, y2 - 6);
					}
					xInit += diameter;
					coreID ++;
					coreIDRow2 --;
					if (i < 7) {
						x2 = x1 + xOffset;
						e.gc.drawLine(x1, y1, x2, y1);
						e.gc.drawLine(x1, y2, x2, y2);
						x1 += xOffset + diameter;
					}
				}				
			}
		});
	}
	
	private void createConsoleOutput(Group group) {
		Group grpConsoleOutput = new Group(group, SWT.NONE);
		grpConsoleOutput.setLocation(10, 489);
		grpConsoleOutput.setSize(875, 32);
		grpConsoleOutput.setText("Console Output");
		lblConsoleOutput = new Label(grpConsoleOutput, SWT.NONE);
		lblConsoleOutput.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.BOLD));
		lblConsoleOutput.setBounds(102, 10, 763, 15);
		lblConsoleOutput.setText("Simulator Cycle");
		//end console group output setup
		
	}
	
	public static void printToConsole(String message) {
		lblConsoleOutput.setText(message);
	}
	
	private void createSimulatorButton(Group group) {
		progressBar = new ProgressBar(group, SWT.SMOOTH);
		progressBar.setLocation(10, 537);
		progressBar.setSize(794, 17);
		
		Button btnSimulate = new Button(group, SWT.NONE);
		btnSimulate.setLocation(810, 534);
		btnSimulate.setSize(75, 25);
		btnSimulate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				printToConsole("Simulating...");
				CoreLog basicLog = LogReader.readLogIgnoreRepeaters("flow_barnes.log");
				SimulatorThread simulator = new SimulatorThread(simulatorTopology, simulatorFlitPacketSize, simulatorTearDownTime, basicLog);
				Thread simulatorThread = new Thread(simulator);
				simulatorThread.start();
				progressBar.setMaximum(basicLog.logSize());
				while (totalTasks <= progressBar.getMaximum() - 1) {
					try {Thread.sleep(100); } 
					catch (Throwable th) {}
					progressBar.setSelection(totalTasks);
				}
				printToConsole("Simulation Completed... Total requesting time = " + totalRequestingTime + " | Total tasks = " + totalTasks + 
						" | Total latency = " + (totalRequestingTime - (2*totalTasks)));
				totalRequestingTime = 0;
				totalTasks = 0;
			}
		});
		btnSimulate.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		btnSimulate.setText("Simulate");
		//end simulator button
	}
}//end class
