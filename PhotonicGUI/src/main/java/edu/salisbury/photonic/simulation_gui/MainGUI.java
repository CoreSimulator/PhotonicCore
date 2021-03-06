package edu.salisbury.photonic.simulation_gui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;

import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;
import edu.salisbury.photonic.log_analyisis.NonDirectionalPairAnalyzer;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Menu;


public class MainGUI {
	//For latency calculation
	public static int totalRequestingTime = 0;
	public static int totalTasks = 0;
	
	//GUI Window
	protected Shell shellRONoC;
	protected Display display;
	
	//Simulation Variables (= default value)
	protected ProgressBar progressBar;
	protected TabFolder tabFolderMenu;
	private String simulatorTopology = "Ring";
	private int simulatorFlitPacketSize = 64; 
	private int simulatorTearDownTime = 1;
	//private int simulatorNumOfSwitches = 0;
	private Canvas canvasTopologyPreview;
	private Label lblConsoleOutput;
	
	//Data Analyzer Variables (= default value)
	private String fileName = "c:\\Users\\J\\PhotonicCoreSimulator\\Flow_Router\\flow_barnes.log";
	private int numOfSections = 1;
	protected Spinner spinnerStartingIndex;
	protected Spinner spinnerEndingIndex;
	private boolean userSpecifiesSection = false;
	public static StyledText styledTextConsoleOutput;
	private Spinner spinnerPopulationSize;
	private Spinner spinnerParents;
	private Spinner spinnerGenerations;
	private Spinner spinnerMutationsPerGen;
	private Spinner spinnerBestFittestKept;
	private Spinner[] nodeIDs = new Spinner[16];
	private int[] defaultNodeIDs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
	private int[] nodeArrangement = null;
	StyledText styledTextNSHOutput;
	


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
		
		Group grpDataAnalyzer = new Group(tabFolderMenu, SWT.NONE);
		tbtmDataAnalyzer.setControl(grpDataAnalyzer);
		
		//Dominant Flow group... consists of options to run the dominant flow analyzer
		createDominantFlowAnalyzer(grpDataAnalyzer);
		
		//Genetic algorithm group... options to modify and run genetic algorithm
		createGeneticAlgorithmAnalyzer(grpDataAnalyzer);
		
		//Console Output group... prints out the data to this console
		createConsoleOutputDA(grpDataAnalyzer);
		///////////////////////////////////////////////////////////
		
		//_______________________________________________________//
		
		//////////////Network Configuration TAB////////////////////
		TabItem tbtmNetworkConfigure = new TabItem(tabFolderMenu, SWT.NONE);
		tbtmNetworkConfigure.setText("Network Configuration");
		
		Group groupNetworkConfigure = new Group(tabFolderMenu, SWT.NONE);
		tbtmNetworkConfigure.setControl(groupNetworkConfigure);
		
		//Node Arranger group... gives the user the option to swap nodes
		createNodeArranger(groupNetworkConfigure);
		
		//MRR Switch Arranger group... gives the user the option to configure switches
		createMRRSWitchArranger(groupNetworkConfigure);
		
		//Network Single Hops group... console that displays the network single hops based upon MRR switches and node arrangement
		createNetworkSingleHops(groupNetworkConfigure);
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
				if (nodeArrangement == null) { drawRingTopology(defaultNodeIDs); }
				else { drawRingTopology(nodeArrangement); }
				
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
		tltm0Switches.setText("o No Switches            ");
		
		ToolItem tltmOConfigure = new ToolItem(toolBarMRRSwitches, SWT.RADIO);
		tltmOConfigure.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tltmOConfigure.setText("o Configure Switches");
		
	}//end architecture options group
	
	private void createArchitecturePreview(Group group) {
		Group grpArchitecturePreview = new Group(group, SWT.NONE);
		grpArchitecturePreview.setLocation(10, 239);
		grpArchitecturePreview.setSize(875, 244);
		grpArchitecturePreview.setText("Architecture Preview");
		
		canvasTopologyPreview = new Canvas(grpArchitecturePreview, SWT.BORDER | SWT.NO_REDRAW_RESIZE);
		canvasTopologyPreview.setBounds(10, 23, 855, 211);
		drawRingTopology(defaultNodeIDs); //default preview
		
	}//end architecture preview group
	
	protected void drawRingTopology(final int[] nodes) {	
		canvasTopologyPreview.setFocus();
		canvasTopologyPreview.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				//dimensional variables
				int diameter = 50; //diameter of circles (nodes)
				int xInit = 10 + 48; //initial starting X (nodes)
				int yInit = 23 + 15; //initial starting y (nodes)
				int xOffset = 46; //offset from node to node (X)
				int yOffset = 41; //offset from node to node (Y)
				int x1 = xInit + diameter; //x1 for the links
				int y1 = yInit + diameter/2; //y1 for the links
				int x2; //x2 for the links
				int y2 = yInit + diameter + yOffset + diameter/2; //y2 for the links
				int j = 15; //negative counter for cyclical wrapping of node IDS
				
				//creates a background (acts as a 'clear canvas' when redrawing 
				e.gc.fillRectangle(0, 0, 855, 211);
				
				//draw left and right vertical link
				e.gc.drawLine(xInit + diameter/2, y1 + diameter/2, xInit + diameter/2, y1 + diameter/2 + yOffset);
				e.gc.drawLine(xInit + (diameter + xOffset)*7 + diameter/2, y1 + diameter/2, xInit + (diameter + xOffset)*7 + diameter/2, y1 + diameter/2 + yOffset);
				
				//draw body cores and horizontal links
				for (int i = 0; i < 8; i++) {
					//draw circles (nodes)
					e.gc.drawOval(xInit + xOffset*i, yInit, diameter, diameter);
					e.gc.drawOval(xInit + xOffset*i, yInit + diameter + yOffset, diameter, diameter);
					//draw numbers (nodeIDs)
					e.gc.drawText("" + nodes[i], xInit + diameter/2 + i*xOffset - 3, y1 - 6);
					e.gc.drawText("" + nodes[j], xInit + diameter/2 + i*xOffset - 3, y2 - 6);
					
					xInit += diameter;
					j --;
					
					if (i < 7) {
						//draw horizontal links
						x2 = x1 + xOffset;
						e.gc.drawLine(x1, y1, x2, y1);
						e.gc.drawLine(x1, y2, x2, y2);
						x1 += xOffset + diameter;
					}
				}				
			}
		});
	}//end drawRingTopology
	
	private void createConsoleOutput(Group group) {
		Group grpConsoleOutput = new Group(group, SWT.NONE);
		grpConsoleOutput.setLocation(10, 489);
		grpConsoleOutput.setSize(875, 32);
		grpConsoleOutput.setText("Console Output");
		lblConsoleOutput = new Label(grpConsoleOutput, SWT.NONE);
		lblConsoleOutput.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.BOLD));
		lblConsoleOutput.setBounds(102, 10, 763, 15);
		lblConsoleOutput.setText("Simulator Cycle");
		
	}//end console group output setup
	
	public void printToConsole(String message) {
		lblConsoleOutput.setText(message);
	}
	
	private void createSimulatorButton(Group group) {
		progressBar = new ProgressBar(group, SWT.SMOOTH);
		progressBar.setLocation(10, 537);
		progressBar.setSize(794, 17);
		
		Button btnSimulate = new Button(group, SWT.NONE);
		btnSimulate.setLocation(810, 534);
		btnSimulate.setSize(75, 25);
		btnSimulate.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (progressBar.getSelection() != 0) {
					return;
				}
				printToConsole("Simulating...");
				CoreLog basicLog = LogReader.readLogIgnoreRepeaters("c:\\Users\\J\\PhotonicCoreSimulator\\Flow_Router\\flow_barnes.log");
				SimulatorThread simulator = new SimulatorThread(simulatorTopology, simulatorFlitPacketSize, simulatorTearDownTime, basicLog, nodeArrangement);
				Thread simulatorThread = new Thread(simulator);
				simulatorThread.start();
				progressBar.setMaximum(basicLog.logSize());
				new Thread() {
					
					boolean finished = false;
					public void run() {
						while (!finished)//totalTasks <= progressBar.getMaximum() - 1) 
						{
							try {Thread.sleep(100);} 
							catch (Throwable th) {}
							
							
							Display.getDefault().asyncExec(new Runnable(){
									public void run(){
										progressBar.setSelection(totalTasks);
										if(finished == true) return;
										else if (totalTasks >= progressBar.getMaximum()-1) {
											finished = true;
											progressBar.setSelection(0);
											printToConsole("Simulation Completed... Total requesting time = " + totalRequestingTime + " | Total tasks = " + totalTasks + 
													" | Total latency = " + (totalRequestingTime - (2*totalTasks)));
											totalRequestingTime = 0;
											totalTasks = 0;
										}
									}
								});
							
						}
						System.out.println("Finished");
					}
				}.start();
				
				
			}
		});
		btnSimulate.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD | SWT.ITALIC));
		btnSimulate.setText("Simulate");
		
	}//end simulator button
	
	//----------------------------------------------------------------------//
	/////////////////DATA ANALYZER TAB BELOW THIS LINE////////////////////////
	//----------------------------------------------------------------------//
	
	private void createDominantFlowAnalyzer(Group grpDataAnalyzer) {
		Group grpDominantFlow = new Group(grpDataAnalyzer, SWT.NONE);
		grpDominantFlow.setText("Dominant Flow");
		grpDominantFlow.setBounds(11, 9, 430, 273);
		
		ToolBar toolBarInputLog = new ToolBar(grpDominantFlow, SWT.BORDER | SWT.FLAT | SWT.WRAP | SWT.RIGHT | SWT.VERTICAL);
		toolBarInputLog.setBounds(12, 41, 197, 191);
		
		ToolItem tltmBarnesLog = new ToolItem(toolBarInputLog, SWT.RADIO);
		tltmBarnesLog.setSelection(true);
		tltmBarnesLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileName = "/PhotonicGUI/Flow_Router";
			}
		});
		tltmBarnesLog.setText("o Barnes Log");
		
		Label lblInputLog = new Label(grpDominantFlow, SWT.NONE);
		lblInputLog.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblInputLog.setBounds(83, 20, 55, 15);
		lblInputLog.setText("Input Log");
		
		Label lblSectionsOfLog = new Label(grpDominantFlow, SWT.NONE);
		lblSectionsOfLog.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSectionsOfLog.setBounds(277, 20, 85, 15);
		lblSectionsOfLog.setText("Sections of Log");
		
		ToolBar toolBarSectionsOfLog = new ToolBar(grpDominantFlow, SWT.BORDER | SWT.FLAT | SWT.WRAP | SWT.RIGHT | SWT.VERTICAL);
		toolBarSectionsOfLog.setBounds(221, 41, 197, 97);
		
		ToolItem tltmFullSection = new ToolItem(toolBarSectionsOfLog, SWT.RADIO);
		tltmFullSection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numOfSections = 1;
				userSpecifiesSection = false;
				spinnerStartingIndex.setEnabled(false);
				spinnerEndingIndex.setEnabled(false);
			}
		});
		tltmFullSection.setSelection(true);
		tltmFullSection.setWidth(36);
		tltmFullSection.setText("o Full Section       ");
		
		ToolItem tltmHalfSections = new ToolItem(toolBarSectionsOfLog, SWT.RADIO);
		tltmHalfSections.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numOfSections = 2;
				userSpecifiesSection = false;
				spinnerStartingIndex.setEnabled(false);
				spinnerEndingIndex.setEnabled(false);
			}
		});
		tltmHalfSections.setText("o Half Sections    ");
		
		ToolItem tltmFourthSections = new ToolItem(toolBarSectionsOfLog, SWT.RADIO);
		tltmFourthSections.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numOfSections = 4;
				userSpecifiesSection = false;
				spinnerStartingIndex.setEnabled(false);
				spinnerEndingIndex.setEnabled(false);
			}
		});
		tltmFourthSections.setText("o Fourth Sections");
		
		spinnerStartingIndex = new Spinner(grpDominantFlow, SWT.BORDER);
		spinnerStartingIndex.setEnabled(false);
		spinnerStartingIndex.setMinimum(0);
		CoreLog coreLog = LogReader.readLogIgnoreRepeaters(fileName);
		spinnerStartingIndex.setMaximum(coreLog.logSize() - 2);
		spinnerStartingIndex.setBounds(224, 192, 68, 22);
		spinnerStartingIndex.setToolTipText("[0, " + (coreLog.logSize() - 2) + "]");
		
		spinnerEndingIndex = new Spinner(grpDominantFlow, SWT.BORDER);
		spinnerEndingIndex.setEnabled(false);
		spinnerEndingIndex.setMinimum(1);
		spinnerEndingIndex.setMaximum(coreLog.logSize() - 1);
		spinnerEndingIndex.setBounds(350, 192, 68, 22);
		spinnerEndingIndex.setToolTipText("[1, " + (coreLog.logSize() - 1) + "]");
		
		ToolItem tltmSpecificSection = new ToolItem(toolBarSectionsOfLog, SWT.RADIO);
		tltmSpecificSection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				userSpecifiesSection = true;
				spinnerStartingIndex.setEnabled(true);
				spinnerEndingIndex.setEnabled(true);
			}
		});
		tltmSpecificSection.setText("o Specific Section");
		
		Button btnDominantFlow = new Button(grpDominantFlow, SWT.NONE);
		btnDominantFlow.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD | SWT.ITALIC));
		btnDominantFlow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (userSpecifiesSection) {
					int[] index = validateIndex(spinnerStartingIndex.getSelection(), spinnerEndingIndex.getSelection());
					if (index[0] >= index[1]) { printToConsoleDA("The starting index must be smaller than the ending index."); } 
					else { DataAnalyzer.analyzeDominantFlow(index[0], index[1], fileName); }
				} else {
					DataAnalyzer.analyzeDominantFlow(numOfSections, fileName);
				}	
			}
		});
		btnDominantFlow.setBounds(345, 238, 75, 25);
		btnDominantFlow.setText("Analyze!");
		
		Label lblSpecifySection = new Label(grpDominantFlow, SWT.NONE);
		lblSpecifySection.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblSpecifySection.setBounds(275, 144, 89, 15);
		lblSpecifySection.setText("Specify Section");
		
		Label lblStartingIndex = new Label(grpDominantFlow, SWT.NONE);
		lblStartingIndex.setBounds(221, 171, 75, 15);
		lblStartingIndex.setText("Starting Index");
		
		Label lblEndingIndex = new Label(grpDominantFlow, SWT.NONE);
		lblEndingIndex.setBounds(350, 171, 68, 15);
		lblEndingIndex.setText("Ending Index");
		
		
	}//end dominant flow analyzer group
	
	private int[] validateIndex(int start, int end) {
		if (start < spinnerStartingIndex.getMinimum()) { start = spinnerStartingIndex.getMinimum(); } 
		else if (start > spinnerStartingIndex.getMaximum()) { start = spinnerStartingIndex.getMaximum(); }
		spinnerStartingIndex.setSelection(start);
		
		if (end < spinnerEndingIndex.getMinimum()) { end = spinnerEndingIndex.getMinimum(); } 
		else if (end > spinnerEndingIndex.getMaximum()) { end = spinnerEndingIndex.getMaximum(); }
		spinnerEndingIndex.setSelection(end);
		
		int[] index = {start, end};
		return index;
	}
	
	private void createGeneticAlgorithmAnalyzer(Group grpDataAnalyzer) {
		Group grpGeneticAlgorithm = new Group(grpDataAnalyzer, SWT.NONE);
		grpGeneticAlgorithm.setText("Genetic Algorithm");
		grpGeneticAlgorithm.setBounds(11, 288, 430, 270);
		
		Button btnGeneticAlgorithm = new Button(grpGeneticAlgorithm, SWT.NONE);
		btnGeneticAlgorithm.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD | SWT.ITALIC));
		btnGeneticAlgorithm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (userSpecifiesSection) {
					int[] index = validateIndex(spinnerStartingIndex.getSelection(), spinnerEndingIndex.getSelection());
					if (index[0] >= index[1]) { 
						printToConsoleDA("The starting index must be smaller than the ending index."); 
					} else { 
						GeneticAlgorithm genetic = new GeneticAlgorithm(fileName, index[0], index[1], nodeArrangement, simulatorFlitPacketSize, simulatorTearDownTime, 
								spinnerPopulationSize.getSelection(), spinnerMutationsPerGen.getSelection());
						genetic.setNumberOfAllTimeFittestKept(spinnerBestFittestKept.getSelection());
						genetic.setNumberOfGenerations(spinnerGenerations.getSelection());
						genetic.setNumberOfParents(spinnerParents.getSelection());
						Thread geneticThread = new Thread(genetic);
						geneticThread.start();
					}
				} else {
					printToConsoleDA("You must specify the section of the log to use Genetic Algorithm.");
				}	
			}
		});
		btnGeneticAlgorithm.setBounds(345, 235, 75, 25);
		btnGeneticAlgorithm.setText("Algorithize!");
		
		Label lblPopulationSize = new Label(grpGeneticAlgorithm, SWT.NONE);
		lblPopulationSize.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblPopulationSize.setBounds(10, 32, 85, 15);
		lblPopulationSize.setText("Population Size");
		
		spinnerPopulationSize = new Spinner(grpGeneticAlgorithm, SWT.BORDER);
		spinnerPopulationSize.setMaximum(1000);
		spinnerPopulationSize.setMinimum(3);
		spinnerPopulationSize.setSelection(30);
		spinnerPopulationSize.setBounds(274, 29, 58, 22);
		
		Label lblNumOfParents = new Label(grpGeneticAlgorithm, SWT.NONE);
		lblNumOfParents.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNumOfParents.setBounds(10, 79, 114, 15);
		lblNumOfParents.setText("Number of Parents");
		
		spinnerParents = new Spinner(grpGeneticAlgorithm, SWT.BORDER);
		spinnerParents.setMinimum(2);
		spinnerParents.setSelection(3);
		spinnerParents.setMaximum(spinnerPopulationSize.getSelection() - 1);
		spinnerParents.setBounds(274, 76, 58, 22);

		Label lblNumOfGenertations = new Label(grpGeneticAlgorithm, SWT.NONE);
		lblNumOfGenertations.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNumOfGenertations.setBounds(10, 126, 150, 15);
		lblNumOfGenertations.setText("Number of Generations");
		
		spinnerGenerations = new Spinner(grpGeneticAlgorithm, SWT.BORDER);
		spinnerGenerations.setMaximum(10000);
		spinnerGenerations.setSelection(1000);
		spinnerGenerations.setBounds(274, 119, 58, 22);
		
		Label lblNumOfMutationsPerGen = new Label(grpGeneticAlgorithm, SWT.NONE);
		lblNumOfMutationsPerGen.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNumOfMutationsPerGen.setBounds(10, 173, 218, 15);
		lblNumOfMutationsPerGen.setText("Number of Mutations per Generation");
		
		spinnerMutationsPerGen = new Spinner(grpGeneticAlgorithm, SWT.BORDER);
		spinnerMutationsPerGen.setMaximum(1000);
		spinnerMutationsPerGen.setSelection(2);
		spinnerMutationsPerGen.setBounds(274, 166, 58, 22);
		
		Label lblNumOfBestFittestKept = new Label(grpGeneticAlgorithm, SWT.NONE);
		lblNumOfBestFittestKept.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNumOfBestFittestKept.setBounds(10, 220, 186, 15);
		lblNumOfBestFittestKept.setText("Number of All Time Fittest Kept");
		
		spinnerBestFittestKept = new Spinner(grpGeneticAlgorithm, SWT.BORDER);
		spinnerBestFittestKept.setMaximum(spinnerParents.getSelection() - 1);
		spinnerBestFittestKept.setSelection(1);
		spinnerBestFittestKept.setBounds(274, 213, 58, 22);
		
	}//end genetic algorithm group
	
	private void createConsoleOutputDA(Group grpDataAnalyzer) {
		Group grpConsoleOutput_DA = new Group(grpDataAnalyzer, SWT.NONE);
		grpConsoleOutput_DA.setToolTipText("");
		grpConsoleOutput_DA.setText("Console Output");
		grpConsoleOutput_DA.setBounds(452, 10, 430, 549);
		
		styledTextConsoleOutput = new StyledText(grpConsoleOutput_DA, SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		styledTextConsoleOutput.setToolTipText("");
		styledTextConsoleOutput.setBounds(10, 20, 410, 519);
		styledTextConsoleOutput.setAlwaysShowScrollBars(true);
		
	}//end console output group
	
	public static void printToConsoleDA(String message) {
		styledTextConsoleOutput.setText(message);
	}
	
	//----------------------------------------------------------------------//
	///////////////Network Configuration TAB BELOW THIS LINE//////////////////
	//----------------------------------------------------------------------//
	
	private void createNodeArranger(Group groupNetworkConfigure) {
		Group grpNodeArranger = new Group(groupNetworkConfigure, SWT.NONE);
		grpNodeArranger.setText("Node Arranger");
		grpNodeArranger.setBounds(10, 14, 875, 170);
		
		Button btnResetNodeArranger = new Button(grpNodeArranger, SWT.NONE);
		btnResetNodeArranger.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//restore the default nodes to their corresponding position
				for (int position = 0; position < nodeIDs.length; position ++) {
					nodeIDs[position].setSelection(defaultNodeIDs[position]);
				}
			}
		});
		btnResetNodeArranger.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnResetNodeArranger.setToolTipText("Resets back to original arrangement");
		btnResetNodeArranger.setBounds(790, 23, 75, 25);
		btnResetNodeArranger.setText("Reset");
		
		Label lblNodeArrangerHelp = new Label(grpNodeArranger, SWT.WRAP);
		lblNodeArrangerHelp.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.ITALIC));
		lblNodeArrangerHelp.setBounds(10, 21, 169, 139);
		lblNodeArrangerHelp.setText("o Click on the 'slider' under the position for which you would like to change the node.\r\no Change the position of all the desired nodes and click 'Submit' when finished.\r\no Each node can only appear once.\r\no Click 'reset' to set the nodes back to their original positions.");
		
		//TODO loop this into the designated arrays or something.....
		Spinner spinnerNodePosition0 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition0.setMaximum(15);
		spinnerNodePosition0.setBounds(227, 54, 47, 22);
		nodeIDs[0] = spinnerNodePosition0;
		
		Spinner spinnerNodePosition1 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition1.setMaximum(15);
		spinnerNodePosition1.setSelection(1);
		spinnerNodePosition1.setBounds(293, 54, 47, 22);
		nodeIDs[1] = spinnerNodePosition1;
		
		Spinner spinnerNodePosition2 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition2.setMaximum(15);
		spinnerNodePosition2.setSelection(2);
		spinnerNodePosition2.setBounds(360, 54, 47, 22);
		nodeIDs[2] = spinnerNodePosition2;
		
		Spinner spinnerNodePosition3 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition3.setMaximum(15);
		spinnerNodePosition3.setSelection(3);
		spinnerNodePosition3.setBounds(424, 54, 47, 22);
		nodeIDs[3] = spinnerNodePosition3;
		
		Spinner spinnerNodePosition4 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition4.setMaximum(15);
		spinnerNodePosition4.setSelection(4);
		spinnerNodePosition4.setBounds(494, 54, 47, 22);
		nodeIDs[4] = spinnerNodePosition4;
		
		Spinner spinnerNodePosition5 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition5.setMaximum(15);
		spinnerNodePosition5.setSelection(5);
		spinnerNodePosition5.setBounds(557, 54, 47, 22);
		nodeIDs[5] = spinnerNodePosition5;
		
		Spinner spinnerNodePosition6 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition6.setMaximum(15);
		spinnerNodePosition6.setSelection(6);
		spinnerNodePosition6.setBounds(624, 54, 47, 22);
		nodeIDs[6] = spinnerNodePosition6;
		
		Spinner spinnerNodePosition7 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition7.setMaximum(15);
		spinnerNodePosition7.setSelection(7);
		spinnerNodePosition7.setBounds(688, 54, 47, 22);
		nodeIDs[7] = spinnerNodePosition7;
		
		Spinner spinnerNodePosition8 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition8.setMaximum(15);
		spinnerNodePosition8.setSelection(8);
		spinnerNodePosition8.setBounds(688, 91, 47, 22);
		nodeIDs[8] = spinnerNodePosition8;
		
		Spinner spinnerNodePosition9 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition9.setMaximum(15);
		spinnerNodePosition9.setSelection(9);
		spinnerNodePosition9.setBounds(624, 91, 47, 22);
		nodeIDs[9] = spinnerNodePosition9;
		
		Spinner spinnerNodePosition10 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition10.setMaximum(15);
		spinnerNodePosition10.setSelection(10);
		spinnerNodePosition10.setBounds(557, 91, 47, 22);
		nodeIDs[10] = spinnerNodePosition10;
		
		Spinner spinnerNodePosition11 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition11.setMaximum(15);
		spinnerNodePosition11.setSelection(11);
		spinnerNodePosition11.setBounds(494, 91, 47, 22);
		nodeIDs[11] = spinnerNodePosition11;
		
		Spinner spinnerNodePosition12 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition12.setMaximum(15);
		spinnerNodePosition12.setSelection(12);
		spinnerNodePosition12.setBounds(424, 91, 47, 22);
		nodeIDs[12] = spinnerNodePosition12;
		
		Spinner spinnerNodePosition13 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition13.setMaximum(15);
		spinnerNodePosition13.setSelection(13);
		spinnerNodePosition13.setBounds(360, 91, 47, 22);
		nodeIDs[13] = spinnerNodePosition13;
		
		Spinner spinnerNodePosition14 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition14.setMaximum(15);
		spinnerNodePosition14.setSelection(14);
		spinnerNodePosition14.setBounds(293, 91, 47, 22);
		nodeIDs[14] = spinnerNodePosition14;
		
		Spinner spinnerNodePosition15 = new Spinner(grpNodeArranger, SWT.BORDER);
		spinnerNodePosition15.setMaximum(15);
		spinnerNodePosition15.setSelection(15);
		spinnerNodePosition15.setBounds(227, 91, 47, 22);
		nodeIDs[15] = spinnerNodePosition15;
		
		Label lblPosition0 = new Label(grpNodeArranger, SWT.WRAP | SWT.CENTER);
		lblPosition0.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition0.setBounds(219, 20, 55, 31);
		lblPosition0.setText("Position \r\n0");
		
		Label lblPosition1 = new Label(grpNodeArranger, SWT.WRAP | SWT.CENTER);
		lblPosition1.setText("Position\r\n1");
		lblPosition1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition1.setBounds(285, 20, 55, 31);
		
		Label lblPosition2 = new Label(grpNodeArranger, SWT.WRAP | SWT.CENTER);
		lblPosition2.setText("Position\r\n2");
		lblPosition2.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition2.setBounds(352, 20, 55, 28);
		
		Label lblPosition3 = new Label(grpNodeArranger, SWT.WRAP | SWT.CENTER);
		lblPosition3.setText("Position\r\n3");
		lblPosition3.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition3.setBounds(416, 20, 55, 28);
		
		Label lblPosition4 = new Label(grpNodeArranger, SWT.WRAP);
		lblPosition4.setAlignment(SWT.CENTER);
		lblPosition4.setText("Position\r\n4");
		lblPosition4.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition4.setBounds(486, 20, 55, 28);
		
		Label lblPosition5 = new Label(grpNodeArranger, SWT.WRAP);
		lblPosition5.setAlignment(SWT.CENTER);
		lblPosition5.setText("Position\r\n5");
		lblPosition5.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition5.setBounds(549, 20, 55, 28);
		
		Label lblPosition6 = new Label(grpNodeArranger, SWT.WRAP);
		lblPosition6.setAlignment(SWT.CENTER);
		lblPosition6.setText("Position\r\n6");
		lblPosition6.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition6.setBounds(616, 20, 55, 28);
		
		Label lblPosition7 = new Label(grpNodeArranger, SWT.WRAP);
		lblPosition7.setAlignment(SWT.CENTER);
		lblPosition7.setText("Position\r\n7");
		lblPosition7.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition7.setBounds(680, 20, 55, 28);
		
		Label lblPosition8 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition8.setAlignment(SWT.CENTER);
		lblPosition8.setText("Position\r\n8");
		lblPosition8.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition8.setBounds(688, 119, 55, 28);
		
		Label lblPosition9 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition9.setAlignment(SWT.CENTER);
		lblPosition9.setText("Position\r\n9");
		lblPosition9.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition9.setBounds(624, 119, 55, 28);
		
		Label lblPosition10 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition10.setAlignment(SWT.CENTER);
		lblPosition10.setText("Position\r\n10");
		lblPosition10.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition10.setBounds(550, 119, 61, 28);
		
		Label lblPosition11 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition11.setAlignment(SWT.CENTER);
		lblPosition11.setText("Position\r\n11");
		lblPosition11.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition11.setBounds(487, 119, 61, 28);
		
		Label lblPosition12 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition12.setAlignment(SWT.CENTER);
		lblPosition12.setText("Position\r\n12");
		lblPosition12.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition12.setBounds(416, 119, 65, 28);
		
		Label lblPosition13 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition13.setAlignment(SWT.CENTER);
		lblPosition13.setText("Position\r\n13");
		lblPosition13.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition13.setBounds(353, 119, 61, 28);
		
		Label lblPosition14 = new Label(grpNodeArranger, SWT.NONE);
		lblPosition14.setAlignment(SWT.CENTER);
		lblPosition14.setText("Position\r\n14");
		lblPosition14.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition14.setBounds(286, 119, 61, 28);
		
		Label lblPosition15 = new Label(grpNodeArranger, SWT.WRAP);
		lblPosition15.setAlignment(SWT.CENTER);
		lblPosition15.setText("Position \r\n15");
		lblPosition15.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblPosition15.setBounds(220, 119, 60, 31);
		
		Button btnSubmit = new Button(grpNodeArranger, SWT.NONE);
		btnSubmit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//the index in nodeArrangement is the position, the value is the nodeID
				if (nodeArrangementIsValid()) {
					nodeArrangement = new int[16];
					int i = 0;
					for (Spinner position: nodeIDs) {
						nodeArrangement[i] = position.getSelection();
						i ++;
					}
					drawRingTopology(nodeArrangement);
					printNetworkSingleHops();
				} else {
					JOptionPane.showMessageDialog(null, "Make sure each number (node ID) is only used once.");
				}
			}
		});
		btnSubmit.setToolTipText("Click when finshed (will update the architecture).");
		btnSubmit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD | SWT.ITALIC));
		btnSubmit.setBounds(790, 119, 75, 25);
		btnSubmit.setText("Submit");
		
	}//end Node Arranger group
	
	private boolean nodeArrangementIsValid() {
		int freq = 0;
		for (int id: defaultNodeIDs) {
			for (Spinner position: nodeIDs) {
				if (id == position.getSelection()) {
					freq ++;
				}
			}
			if (freq > 1) {
				return false;
			}
			freq = 0;
		}
		return true;
	}
	
	private void createMRRSWitchArranger(Group groupNetworkConfigure) {
		//TODO finish this once the switches are integrated
		Group grpMrrSwitchArranger = new Group(groupNetworkConfigure, SWT.NONE);
		grpMrrSwitchArranger.setText("MRR Switch Arranger");
		grpMrrSwitchArranger.setBounds(10, 198, 875, 170);
		
	}//end MRR Switch Arranger group
	
	private void createNetworkSingleHops(Group groupNetworkConfigure) {
		Group grpNetworkSingleHops = new Group(groupNetworkConfigure, SWT.NONE);
		grpNetworkSingleHops.setText("Network Single Hops");
		grpNetworkSingleHops.setBounds(10, 382, 875, 170);
		
		styledTextNSHOutput = new StyledText(grpNetworkSingleHops, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		styledTextNSHOutput.setDoubleClickEnabled(false);
		styledTextNSHOutput.setEnabled(false);
		styledTextNSHOutput.setEditable(false);
		styledTextNSHOutput.setBounds(10, 20, 855, 140);
		
	}//end Network Single Hops group
	
	private void printNetworkSingleHops() {
		//TODO add mrrSwitch arrangement considerations when they are complete
		//topology should be a parameter
		String message = "";
		int nodeID;
		int prevNodeID;
		int nextNodeID;
		int columns = 0;
		boolean twoTwoDigits = false;
		for (int i = 0; i < nodeIDs.length; i ++) {
			nodeID = nodeIDs[i].getSelection();
			//get previous connected node
			if (i == 0) { prevNodeID = nodeIDs[nodeIDs.length - 1].getSelection(); }
			else { prevNodeID = nodeIDs[i - 1].getSelection(); }
			//get next connected node
			if (i == 15) { nextNodeID = nodeIDs[0].getSelection(); }
			else { nextNodeID = nodeIDs[i + 1].getSelection(); }
			//add info to message, to be printed to console
			twoTwoDigits = setsOfTwoTwoDigits(nodeID, prevNodeID, nextNodeID);
			if (columns >= 4) {//next line
				message += ("" + nodeID + " --> " + prevNodeID + " , " + nextNodeID + "\t\t\n\n");
				columns = 0;
			} else {//same line
				message += ("" + nodeID + " --> " + prevNodeID + " , " + nextNodeID + "\t\t");
				if (!twoTwoDigits) {
					message += ("\t");
				}
				columns ++;
			}
		}
		styledTextNSHOutput.setText(message);
	}

	private boolean setsOfTwoTwoDigits(int nodeID, int prevNodeID,
			int nextNodeID) {
		if (nodeID/10.0 < 1) {
			if (prevNodeID/10.0 >= 1 && nextNodeID/10.0 >= 1) { return true; }
			else {return false;}
		} else {
			if (prevNodeID/10.0 >= 1 || nextNodeID/10.0 >= 1) { return true; }
			else { return false; }
		}
	}
}//end class
