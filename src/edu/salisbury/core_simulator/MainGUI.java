package edu.salisbury.core_simulator;
import java.util.HashMap;

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

import edu.salisbury.cyclical_core_simulator.CyclicalSimOverseer;
import org.eclipse.swt.widgets.ProgressBar;


public class MainGUI {

	protected Shell shellRONoC;
	private String simulatorTopology = "Ring";
	private int simulatorNumOfBodyCores = 64; 
	private int simulatorTearDownTime = 1;
	//private int simulatorNumOfSwitches = 0;
	private Canvas canvasTopologyPreview;
	private static Label lblConsoleOutput;


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
		Display display = Display.getDefault();
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
		
		//Architecture Options... choose number of nodes, topology...
		createArchitectureOptions();
		
		//Architecture preview group... displays what the generic topology
		createArchitecturePreview();
		
		//Console output group... maybe print out the latency at the end, or the current subtask
		createConsoleOutput();
		
		//Simulator button... start button
		createSimulatorButton();

	}//end create contents function

	private void createArchitectureOptions() {
		Group grpArchitectureOptions = new Group(shellRONoC, SWT.NONE);
		grpArchitectureOptions.setText("Architecture Options");
		grpArchitectureOptions.setBounds(10, 10, 884, 251);
		
		ToolBar toolBarTopology = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.VERTICAL);
		toolBarTopology.setBounds(11, 41, 280, 179);
		
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
		
		ToolBar toolBarNumOfCore = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.FLAT | SWT.VERTICAL);
		toolBarNumOfCore.setBounds(302, 41, 280, 179);
		
		ToolItem tltm16BC = new ToolItem(toolBarNumOfCore, SWT.RADIO);
		tltm16BC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorNumOfBodyCores = 16;
			}
		});
		tltm16BC.setText("o 16");
		
		ToolItem tltm32BC = new ToolItem(toolBarNumOfCore, SWT.RADIO);
		tltm32BC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorNumOfBodyCores = 32;
			}
		});
		tltm32BC.setText("o 32");
		
		ToolItem tltm64BC = new ToolItem(toolBarNumOfCore, SWT.RADIO);
		tltm64BC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulatorNumOfBodyCores = 64;
			}
		});
		tltm64BC.setWidth(40);
		tltm64BC.setSelection(true);
		tltm64BC.setText("o 64");
		
		Label lblNumOfBodyCores = new Label(grpArchitectureOptions, SWT.NONE);
		lblNumOfBodyCores.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNumOfBodyCores.setBounds(377, 20, 131, 15);
		lblNumOfBodyCores.setText("Number of Body Cores");
		
		Label lblTopology = new Label(grpArchitectureOptions, SWT.NONE);
		lblTopology.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblTopology.setBounds(124, 20, 55, 15);
		lblTopology.setText("Topology");
		
		Label lblTearDownTime = new Label(grpArchitectureOptions, SWT.NONE);
		lblTearDownTime.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblTearDownTime.setBounds(688, 20, 92, 15);
		lblTearDownTime.setText("Tear Down Time");
		
		ToolBar toolBarTearDownTime = new ToolBar(grpArchitectureOptions, SWT.BORDER | SWT.FLAT | SWT.VERTICAL);
		toolBarTearDownTime.setBounds(594, 41, 280, 73);
		
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
		toolBarMRRSwitches.setBounds(594, 147, 280, 73);
		
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
	
	private void createArchitecturePreview() {
		Group grpArchitecturePreview = new Group(shellRONoC, SWT.NONE);
		grpArchitecturePreview.setText("Architecture Preview");
		grpArchitecturePreview.setBounds(10, 267, 884, 251);
		
		canvasTopologyPreview = new Canvas(grpArchitecturePreview, SWT.BORDER | SWT.NO_REDRAW_RESIZE);
		canvasTopologyPreview.setBounds(10, 23, 864, 224);
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
				//draw left and right vertical link
				e.gc.drawLine(xInit + diameter/2, y1 + diameter/2, xInit + diameter/2, y1 + diameter/2 + yOffset);
				e.gc.drawLine(xInit + (diameter + xOffset)*7 + diameter/2, y1 + diameter/2, xInit + (diameter + xOffset)*7 + diameter/2, y1 + diameter/2 + yOffset);
				//draw body cores and horizontal links
				for (int i = 0; i < 8; i++) {
					e.gc.drawOval(xInit + xOffset*i, yInit, diameter, diameter);
					e.gc.drawOval(xInit + xOffset*i, yInit + diameter + yOffset, diameter, diameter);
					xInit += diameter;
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
	
	private void createConsoleOutput() {
		Group grpConsoleOutput = new Group(shellRONoC, SWT.NONE);
		grpConsoleOutput.setText("Console Output");
		grpConsoleOutput.setBounds(10, 524, 884, 32);
		
		lblConsoleOutput = new Label(grpConsoleOutput, SWT.NONE);
		lblConsoleOutput.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.BOLD));
		lblConsoleOutput.setBounds(102, 10, 772, 15);
		lblConsoleOutput.setText("Simulator Cycle");
		//end console group output setup
		
	}
	
	public static void printToConsole(String message) {
		lblConsoleOutput.setText(message);
	}
	
	private void createSimulatorButton() {
		Button btnSimulate = new Button(shellRONoC, SWT.NONE);
		btnSimulate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				printToConsole("Simulating...");
				runSimulator();
			}
		});
		btnSimulate.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		btnSimulate.setBounds(819, 562, 75, 25);
		btnSimulate.setText("Simulate");
		
		ProgressBar progressBar = new ProgressBar(shellRONoC, SWT.NONE);
		progressBar.setBounds(10, 566, 803, 17);
		//end simulator button
		
	}

	private void runSimulator() {
		CoreLog basicLog = LogReader.readLogIgnoreRepeaters("flow_barnes.log");
		
		HashMap<Coordinate, Integer> switchingMap =  new HashMap<Coordinate, Integer>();
		for(int i = 0; i < 8; i++)
		{
			switchingMap.put(new Coordinate(1,i), i);
		}
		for(int i = 7; i >= 0; i--)
		{
			switchingMap.put(new Coordinate(2,7-i), 8+i);
		}
		
		//Select the topology to simulate
		switch(simulatorTopology) {
			case "Ring":
				CyclicalSimOverseer test = new CyclicalSimOverseer(simulatorNumOfBodyCores, simulatorTearDownTime, switchingMap);
				test.simulateWithLog(basicLog);
				break;
			default:
				CyclicalSimOverseer test1 = new CyclicalSimOverseer(simulatorNumOfBodyCores, simulatorTearDownTime, switchingMap);
				test1.simulateWithLog(basicLog);
				break;
		}//end switch
		
	}
}//end class
