package modelgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.dom.svg.SVGGraphicsElement;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.svg.SVGStylableElement;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.AbstractJSVGComponent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import actions.ActionOpenFile;
import animations.Animation;
import animations.Colorize;
import animations.Move;
import animations.MovePath;
import animations.Opaque;
import animations.Rotate;
import animations.RotatePivot;
import animations.Text;
import controls.Button;
import controls.Control;
import controls.Textbox;
import exceptions.MappingException;

/**
 * @author Steffen Jacobs
 * 
 */
public class SVGApplication extends Observable implements Observer {

	/**
	 * 
	 */
	
	
	public final static String LOGGER_NAME = "modelgui.log";
	
	public static Logger log = null;

	// All mapping file constants. If you don't like some tag or attribute
	// names, change it here and in your mapping file and everything should be
	// fine.
	public static final String DISPLAY_TAG = "display";
	public static final String CONTROL_TAG = "control";
	public static final String MOVE_TAG = "move";
	public static final String MOVEPATH_TAG = "move-path";
	public static final String ROTATE_TAG = "rotate";
	public static final String COLOR_TAG = "colorize";
	public static final String OPACITY_TAG = "opaque";
	public static final String SCALE_TAG = "scale";
	public static final String TEXT_TAG = "textbox";
	public static final String BUTTON_TAG = "button";
	public static final String PATH_ATTR = "path";
	public static final String ID_ATTR = "id";
	public static final String PORT_ATTR = "port";
	public static final String INPUT_ATTR = "input";
	public static final String ANGLE_ATTR = "angle-range";
	public static final String X_ATTR = "x-range";
	public static final String Y_ATTR = "y-range";
	public static final String PIVOT_ATTR = "pivot";
	public static final String COLOR_ATTR = "color";
	public static final String COLORPROPERTY_ATTR = "color-property";
	public static final String OPACITY_ATTR = "opacity";
	public static final String ANCHORPOINT_ATTR = "anchor-point";
	public static final String AUTOORIENT_ATTR = "auto-orient";
	public static final String ORIENTANGLE_ATTR = "direction-angle";

	protected static final int MAX_DELAY = 3000;

	protected static final int MIN_DELAY = 20;

	private long updateTimeAvg = 0;
	private long countUpdates = 0;

	JFrame frame; // main frame
	JLabel statusLabel = new JLabel();
	JLabel labelXYpos = new JLabel(); // shows mouse position on canvas
	JSVGCanvas svgCanvas = new JSVGCanvas(null, true, false);
	URL svgFile; // currently loaded svg image
	URL mapFile; // currently loaded mapping file
	SVGOMDocument document;
	SVGApplication svgApp = this;
	ControlAnimationManager server;
	ModelguiToolBar toolBar;
	ModelguiMenuBar menu;
	static JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
	ActionOpenFile actionOpenToolBar; // opens file dialog on click on a
	// toolbar icon
	ActionOpenFile actionOpenMenu; // opens file dialog on click on a JMenuItem
	Vector<Animation> animationList = new Vector<Animation>(5); // all
	// registered
	// animations in
	// this list
	// will be
	// rendered
	Vector<Control> controlList = new Vector<Control>(5); // not used yet.
	// Maybe later?
	boolean isLogo = true; // only true if application is started. Then, the
	// canvas
	// is only showing a default image (modelgui-logo.svg), so nothing
	// should be animated
	long time = 0;
	boolean statusBarVisible = true;

	/**
	 * Constructor. Sets title string, opens the default SVG file, and
	 * initializes components
	 * 
	 * @param f
	 */
	public SVGApplication(JFrame f) {
		log = Logger.getLogger(LOGGER_NAME);
		log.setUseParentHandlers(false);
		log.setLevel(Level.ALL);
		try {
			FileHandler simpleFileHandler = new FileHandler("LOG");
			simpleFileHandler.setFormatter(new SimpleFormatter());
			log.addHandler(simpleFileHandler);
		}
		catch (SecurityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		/*catch (IOException e2) {
			log.addHandler(new ConsoleHandler());
			log.throwing("SVGApplication", "Constructor", e2);
		}*/
		frame = f;
		frame.setTitle(frame.getTitle() + " " + Version.getVersionString());
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					svgApp.open(getClass().getResource(
							"/resources/modelgui-logo.svg").toURI());
				}
				catch (Exception e1) {
					new ModelguiErrorDialog(e1, "Cannot load SVG file");
				}
			}
		});
		f.setSize(800, 600);
		this.createComponents();
		// menu is instantiated now, so add functionality to exit item
		menu.getExitItem().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (svgCanvas != null && svgCanvas.getUpdateManager() != null)
					svgCanvas.getUpdateManager().interrupt();
				if (menu != null && menu.getPreferencesDialog() != null)
					;
				menu.getPreferencesDialog().savePreferences();
				server.close();
			}
		});
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.svgCanvas.setDocumentState(AbstractJSVGComponent.ALWAYS_DYNAMIC);
		this.svgCanvas.setDoubleBufferedRendering(false);
		svgCanvas.setDisableInteractions(true);
		// svgCanvas.enableInputMethods(false);
		svgCanvas.setAnimationLimitingCPU(0.5f);
		// svgCanvas.setDoubleBuffered(true);
		// svgCanvas.setProgressivePaint(true);

		this.centerToScreen();
		f.setVisible(true);
		
		// loading a default SVG file, if it was set in the preferences
		String defaultSvg = ((JTextField)menu.getPreferencesDialog().getPreference(PreferencesDialog.DEFAULT_PREF_NAME)).getText();
		try {
			if(! defaultSvg.matches("\\w*")){ // does not match any whitespace
				URL url = new URL(defaultSvg);
				actionOpenMenu.setFile(new File(url.getFile()));
				this.open(url);
			}
		} catch (MalformedURLException e1) {
			new ModelguiErrorDialog(e1, "The default SVG filename set in the preferences is a malformed URL.");
		}
	}

	public void centerToScreen() {
		Dimension screensize = frame.getToolkit().getScreenSize();
		Dimension framesize = frame.getSize();
		int x = (int) ((screensize.getWidth() - framesize.getWidth()) / 2);
		int y = (int) ((screensize.getHeight() - framesize.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	/**
	 * @return
	 */
	public void createComponents() {
		frame.getContentPane().setLayout(new BorderLayout());
		JPanel bottompanel = new JPanel(new BorderLayout());
		menu = new ModelguiMenuBar(new PreferencesDialog(this));
		if (server != null)
			server.close();
		if (svgApp.isClientMode()) {
			// label.setText("Finished rendering SVG. Create client...");
			server = new SCADEControlAnimationClient(svgCanvas, menu
					.getPreferencesDialog());
			
		}
		else {
			// label.setText("Finished rendering SVG. Create server...");
			server = new ControlAnimationServer(svgCanvas, menu
					.getPreferencesDialog());
		}
		toolBar = new ModelguiToolBar(frame);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.getPlayButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean paused = toolBar.isPaused();
				if (paused) {
					server.setDelay(toolBar.getDelay());
				}
				else {
					server.doPause();
				}
				svgApp.setChanged();
				svgApp.notifyObservers(Boolean.valueOf(!paused));
				//toolBar.setPaused(!paused);
			}
		});
		toolBar.getStepForwardButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server.doStep();
			}

		});
		toolBar.getStopButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server.close();
				//toolBar.setPaused(true);
			}

		});
		toolBar.getDelayTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						|| /* for remote */e.getKeyCode() == 36) {
					String input = toolBar.getDelayTextFieldText().trim();
					if (input.equals("")) {
						toolBar.setDelay(toolBar.getDelay());
					}
					else {
						int delay = Integer.parseInt(input);
						if (delay > MAX_DELAY) {
							delay = MAX_DELAY;
						}
						else if (delay < MIN_DELAY) {
							delay = MIN_DELAY;
						}
						toolBar.setDelay(delay);
						//server.setDelay(delay);
					}
					toolBar.getDelayTextField().transferFocus();
					toolBar.icon.setVisible(false);
				}
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
						|| e.getKeyCode() == KeyEvent.VK_DELETE
						|| /* for remote */e.getKeyCode() == 22) {
					toolBar.icon.setVisible(true);
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					toolBar.setDelay(toolBar.getDelay());
					toolBar.icon.setVisible(false);
					toolBar.getDelayTextField().transferFocus();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// only numbers are allowed
				if (e.getKeyChar() < '0' || e.getKeyChar() > '9') {
					e.consume();
				}
				else {
					toolBar.icon.setVisible(true);
				}
			}
		});
		toolBar.getReloadButton().addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				svgCanvas.flushImageCache();
				svgApp.open(svgFile);
				//svgCanvas.loadSVGDocument(svgFile.toString());
			}
			
		});
		frame.setJMenuBar(menu);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		progressBar.setPreferredSize(new Dimension(250, 0));
		frame.getContentPane().add(svgCanvas, BorderLayout.CENTER);
		JPanel bottom_left_panel = new JPanel(new BorderLayout());
		// bottom_left_panel.add(label);
		bottom_left_panel.add("West", progressBar);
		bottom_left_panel.add("Center", statusLabel);

		/*
		 * Please don't change this line. Otherwise, it can lead to a
		 * NullPointerException at JSVGCanvas
		 */
		bottompanel.setPreferredSize(new Dimension(0, 20));

		bottompanel.add("West", bottom_left_panel);
		bottompanel.add("East", labelXYpos);
		frame.getContentPane().add("South", bottompanel);
		progressBar.setStringPainted(true);
		// panel.setBorder(BorderFactory.createEtchedBorder());
		bottompanel.setBorder(BorderFactory.createEtchedBorder());

		// Set the open file action.
		actionOpenMenu = new ActionOpenFile(null, toolBar.getOpenFileButton()
				.getIcon(), frame.getContentPane());
		toolBar.getOpenFileButton().setAction(actionOpenMenu);

		// Set the JSVGCanvas listeners.
		svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
			@Override
			public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
				progressBar.setString("Load document...");
				progressBar.setValue(0);
				statusLabel.setVisible(false);
				progressBar.setVisible(true);
				svgApp.setChanged();
				svgApp.notifyObservers("loading started");
			}

			@Override
			public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
				progressBar.setString("Document loaded.");
				progressBar.setValue(33);
			}
		});

		svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
			@Override
			public void gvtBuildStarted(GVTTreeBuilderEvent e) {
				progressBar.setString("Creating data structure...");
			}

			@Override
			public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
				progressBar.setString("Data structure finished.");
				progressBar.setValue(66);
			}
		});

		svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
			@Override
			public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
				progressBar.setString("Starting rendering...");

				time = System.currentTimeMillis();
			}

			@Override
			public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
				progressBar.setString("Rendering completed!");
				progressBar.setValue(100);		

				document = (SVGOMDocument) svgCanvas.getSVGDocument();
				svgApp.testViewBoxAttributeAndInsert();
				// docCopy = (SVGOMDocument)svgCanvas.getSVGDocument();
				svgApp.pack();

				svgCanvas.getUpdateManager().addUpdateManagerListener(
						new UpdateManagerListener() {

							public void managerResumed(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub

							}

							public void managerStarted(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub
								updateTimeAvg = 0;
								countUpdates = 0;
							}

							public void managerStopped(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub
								if (countUpdates != 0) {
									updateTimeAvg = updateTimeAvg
											/ countUpdates;
									log.log(Level.INFO, "Average update time during animations: "
													+ updateTimeAvg + " ms");
								}
							}

							public void managerSuspended(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub

							}

							public void updateCompleted(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub
								updateTimeAvg = updateTimeAvg + Tools.tac();
								countUpdates++;
							}

							public void updateFailed(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub
								// log.println("update failed");
							}

							public void updateStarted(UpdateManagerEvent arg0) {
								// TODO Auto-generated method stub
								Tools.tic();
							}

						});

				svgCanvas.addMouseMotionListener(new MouseMotionListener() {

					public void mouseDragged(MouseEvent e) {
						/* not used */
					}

					public void mouseMoved(MouseEvent e) {
						try {
							int xpos = e.getX();
							int ypos = e.getY();
							double scalex = svgCanvas.getCanvasGraphicsNode()
									.getViewingTransform().getScaleX();
							double scaley = svgCanvas.getCanvasGraphicsNode()
									.getViewingTransform().getScaleY();
							double translatex = svgCanvas
									.getCanvasGraphicsNode()
									.getViewingTransform().getTranslateX();
							double translatey = svgCanvas
									.getCanvasGraphicsNode()
									.getViewingTransform().getTranslateY();
							// log.println("Translate: " +translatex + ",
							// " + translatey);
							// log.println("Scale: " + scalex + ", " +
							// scaley);
							labelXYpos.setText("x="
									+ Math.round(xpos / scalex
											- (translatex / scalex))
									+ ", y="
									+ Math.round(ypos / scaley
											- (translatey / scaley)));
						}
						catch (Exception e1) {
							/* just ignore exceptions */
						}
					}

				});

				// check if we see the start screen image (the modelgui logo).
				// only after a custom image is loaded, we parse mapping file,
				// start server action, etc.
				if (actionOpenMenu.getFile() != null) {
					// very, VERY dirty work. Sorry to everyone, who must go
					// through this...
					try {
						// log.println(actionOpenMenu.getFile());
						svgFile = actionOpenMenu.getURI().toURL();
						String temp = svgFile.toString();
						// TODO: evtl. problematische String-Ersetzung.
						// z.B. wird test.svg.svg zu test.map.map!
						temp = temp.replace(".svg", ".map");
						mapFile = new URL(temp);

						NodeList displayNodes = null;
						NodeList controlNodes = null;
						// server.getDataToSend().getData().setSize(0);

						// clean up SVG animations and controls before we set
						// them up for the new image
						animationList.removeAllElements();
						controlList.removeAllElements();
						// try to validate mapping file (only if activated in
						// preferences dialog)
						boolean xmlValidationStatus = ((JRadioButton) menu
								.getPreferencesDialog().getPreference(
										"xml-validation")).isSelected();
						if (xmlValidationStatus == true) {
							Tools.isValidXml(mapFile, getClass().getResource(
									"/resources/config/mapping.xsd"));
						}
						MapParser parser = new MapParser(mapFile);
						displayNodes = parser.getDisplayNodes();
						controlNodes = parser.getControlNodes();
						// maybe we did not find display tags in mapping file,
						// so we don't have to
						// animate anything and skip this part
						if (displayNodes != null) {
							for (int i = 0; i < displayNodes.getLength(); i++) {
								Node current = displayNodes.item(i);
								int port = parser.getPort(current);
								// get the list of display actions like move,
								// rotate, color, opacity, scale, ...
								NodeList actions = current.getChildNodes();
								for (int j = 0; j < actions.getLength(); j++) {
									Node currentAction = actions.item(j);
									String tagname = currentAction
											.getNodeName();
									// we do not want to parse commentaries or
									// text (marked with a '#' prefix),
									// but only attributes and their values
									if (!tagname.startsWith("#")) {
										// get id value of SVG element, which
										// has to be manipulated by current
										// action
										// e.g. if current action is 'move' and
										// id='railway' it means that SVG
										// element 'railway'
										// will be moved
										String id = currentAction
												.getAttributes().getNamedItem(
														ID_ATTR).getNodeValue();

										// get the (hopefully unique) graphical
										// SVG element whose ID equals
										// <code>id</code>
										// The uniqueness of an element's ID
										// must be guaranteed at SVG document
										// creation.
										// Here, ambiguous id allocation will
										// lead to errors

										// TODO: put this in every if clause
										// separately, because text-fields do
										// not extend SVGGraphicsElement
										// so this line will lead to a
										// ClassCastException if text shall be
										// displayed
										SVGStylableElement el = (SVGStylableElement) document
												.getRootElement()
												.getElementById(id);
										// error handling if mapping object does
										// not exist -- haf
										if (el == null)
											throw new MappingException(id
													+ "not found!");

										// the following if clauses check which
										// display action we want to perform on
										// current
										// element, based on data in the mapping
										// file.
										if (tagname.equals(MOVEPATH_TAG)) {
											try {
												Interval inputInterval = new Interval(
														currentAction
																.getAttributes()
																.getNamedItem(
																		INPUT_ATTR)
																.getNodeValue());
												Point anchorPoint = null;
												// per default, auto orientation is enabled
												boolean autoOrient = true;
												// per default, object is heading upwards
												double orientAngle = 0;
												try {
													String anchorPointStr = currentAction
															.getAttributes()
															.getNamedItem(
																	ANCHORPOINT_ATTR)
															.getNodeValue();
													StringTokenizer tok = new StringTokenizer(
															anchorPointStr, ",");
													anchorPoint = new Point(
															Integer
																	.parseInt(tok
																			.nextToken()),
															Integer
																	.parseInt(tok
																			.nextToken()));													
												}
												catch (Exception e1) {
													/*
													 * if no anchor point is
													 * available, just ignore,
													 * since it is an optional
													 * attribute
													 */
												}
												
												try {
													String autoOrientStr = currentAction.getAttributes().getNamedItem(AUTOORIENT_ATTR).getNodeValue();
													if (autoOrientStr != null && !autoOrientStr.equals("")){
														// if we found some explicit entry in mapping, first set autoOrient to false...
														autoOrient = false;
													}
													// ... then try to parse the String. So, in case of typing errors, auto orientation is disabled
													if(autoOrientStr.equals("1") || autoOrientStr.equals("true") || autoOrientStr.equals("yes")){
														autoOrient = true;
													}													
												} catch (Exception e1){
													/* just ignore */
												}
												
												try {
													String orientAngleStr = currentAction.getAttributes().getNamedItem(ORIENTANGLE_ATTR).getNodeValue();
													orientAngle = Integer.parseInt(orientAngleStr);
												} catch (Exception e1){
													/* just ignore */
												}
												
												String pathID = currentAction
														.getAttributes()
														.getNamedItem(PATH_ATTR)
														.getNodeValue();
												SVGOMElement path = (SVGOMElement) document
														.getRootElement()
														.getElementById(pathID);
												// error handling if mapping
												// object does not exist -- haf
												if (path == null)
													throw new MappingException(
															pathID
																	+ "not found!");
												//System.out.println("auto orient:" + autoOrient);
												//System.out.println("direction Angle:" + orientAngle);
												MovePath mp = new MovePath(
														port,
														(SVGGraphicsElement) el,
														inputInterval,
														anchorPoint,
														autoOrient,
														orientAngle, path,
														svgCanvas);
												addAnimation(mp);

											}
											catch (NumberFormatException e1) {
												new ModelguiErrorDialog(
														"Could not parse mapping attributes. Unkown number format!");
											}
											catch (Exception e1) {
												new ModelguiErrorDialog(e1,
														"Unknown error occured while parsing mapping attributes!");
											}
										}
										if (tagname.equals(MOVE_TAG)) {
											// log.println("Move element:
											// " + id);
											try {
												Interval inputInterval = new Interval(
														currentAction
																.getAttributes()
																.getNamedItem(
																		INPUT_ATTR)
																.getNodeValue());
												Interval xRangeInterval = new Interval(
														currentAction
																.getAttributes()
																.getNamedItem(
																		X_ATTR)
																.getNodeValue());
												Interval yRangeInterval = new Interval(
														currentAction
																.getAttributes()
																.getNamedItem(
																		Y_ATTR)
																.getNodeValue());

												Move m = new Move(
														port,
														(SVGGraphicsElement) el,
														inputInterval,
														xRangeInterval,
														yRangeInterval,
														svgCanvas);
												addAnimation(m);
											}
											catch (NumberFormatException e1) {
												new ModelguiErrorDialog(
														"Could not parse mapping attributes of element '"
																+ id + "'!");
											}

										}
										if (tagname.equals(ROTATE_TAG)) {
											// log.println("Rotate
											// element: " + id);
											Interval inputInterval = new Interval(
													currentAction
															.getAttributes()
															.getNamedItem(
																	INPUT_ATTR)
															.getNodeValue());
											Interval angleInterval = new Interval(
													currentAction
															.getAttributes()
															.getNamedItem(
																	ANGLE_ATTR)
															.getNodeValue());
											Node pivotNode = currentAction
													.getAttributes()
													.getNamedItem(PIVOT_ATTR);
											if (pivotNode != null) {
												String pivotStr = pivotNode
														.getNodeValue();
												if (pivotStr.equals(""))
													// if no 'pivot' attribute
													// was used just do a
													// regular rotation (around
													// the center of the
													// element)
													addAnimation(new Rotate(
															port,
															(SVGGraphicsElement) el,
															inputInterval,
															angleInterval,
															svgCanvas));
												else {
													// parse the pivot attribute
													// and rotate around given
													// x,y coordinate
													StringTokenizer tok = new StringTokenizer(
															pivotStr, ",");
													try {
														int pivotx = Math
																.round(Float
																		.parseFloat(tok
																				.nextToken()));
														int pivoty = Math
																.round(Float
																		.parseFloat(tok
																				.nextToken()));
														Point pivot = new Point(
																pivotx, pivoty);
														// log.println("pivot:
														// " + pivot);
														addAnimation(new RotatePivot(
																port,
																(SVGGraphicsElement) el,
																angleInterval,
																inputInterval,
																pivot,
																svgCanvas));
													}
													catch (Exception e1) {
														new ModelguiErrorDialog(
																e1,
																"Error! Possibly the 'pivot' attribute of element '"
																		+ id
																		+ "' could not be parsed!");
													}
												}
											}
											else {
												// pivot node is null, so just
												// rotate around center of the
												// bounding box
												addAnimation(new Rotate(
														port,
														(SVGGraphicsElement) el,
														inputInterval,
														angleInterval,
														svgCanvas));
											}
										}
										if (tagname.equals(COLOR_TAG)) {
											// the value of the input attribute
											// for a 'colorize' element is a
											// comma separated list of numeric
											// or boolean values
											Node inputAttr = currentAction
													.getAttributes()
													.getNamedItem(INPUT_ATTR);
											// color values are comma separated
											// hex strings representing a
											// specific color, e.g. #ffffff for
											// white
											Node colors = currentAction
													.getAttributes()
													.getNamedItem(COLOR_ATTR);
											// colorProperty defines whether to
											// colorize the border ('stroke') or
											// the fill region ('fill')
											Node colorProperty = currentAction
													.getAttributes()
													.getNamedItem(
															COLORPROPERTY_ATTR);

											Colorize c = new Colorize(port, el,
													inputAttr.getNodeValue(),
													colors.getNodeValue(),
													colorProperty
															.getNodeValue(),
													svgCanvas);
											addAnimation(c);
										}
										if (tagname.equals(SCALE_TAG)) {
											// TODO: scaling not supported yet

											@SuppressWarnings("unused")
											Node inputAttr = currentAction
													.getAttributes()
													.getNamedItem(INPUT_ATTR);
										}
										if (tagname.equals(OPACITY_TAG)) {
											// change the opacity of an element
											Node inputAttr = currentAction
													.getAttributes()
													.getNamedItem(INPUT_ATTR);
											Node opaques = currentAction
													.getAttributes()
													.getNamedItem(OPACITY_ATTR);
											addAnimation(new Opaque(port, el,
													inputAttr.getNodeValue(),
													opaques.getNodeValue(),
													svgCanvas));
										}
										if (tagname.equals(TEXT_TAG)) {
											// displays any incoming String data
											// in a specific textbox
											addAnimation(new Text(port, el,
													svgCanvas));

										}
									}
								}
							}
						}
						if (controlNodes != null) {
							for (int i = 0; i < controlNodes.getLength(); i++) {
								Node current = controlNodes.item(i);
								int port = parser.getPort(current);
								NodeList actions = current.getChildNodes();
								for (int j = 0; j < actions.getLength(); j++) {
									Node currentAction = actions.item(j);
									String tagname = currentAction
											.getNodeName();
									if (tagname.equals(BUTTON_TAG)) {
										NamedNodeMap attributes = currentAction
												.getAttributes();
										Node idAttr = attributes
												.getNamedItem(ID_ATTR);
										String id = idAttr.getNodeValue();
										// log.println("button " + id + "
										// found!");
										SVGOMElement el = (SVGOMElement) document
												.getRootElement()
												.getElementById(id);
										if (el != null) {
											// log.println("Control '" +
											// id + "' gefunden!");
											JavaStringData dataToSend = server
													.getDataToSend();
											dataToSend
													.getData()
													.setSize(
															dataToSend
																	.getData()
																	.size() + 1);
											// log.println("dataToSend
											// size: " +
											// dataToSend.getData().size());
											server.toString();
											addControl(new Button(port, el, dataToSend, false));
										}
									}
									if (tagname.equals(TEXT_TAG)) {
										NamedNodeMap attributes = currentAction
												.getAttributes();
										Node idAttr = attributes
												.getNamedItem(ID_ATTR);
										String id = idAttr.getNodeValue();
										SVGOMElement el = (SVGOMElement) document
												.getRootElement()
												.getElementById(id);
										JavaStringData dataToSend = server
												.getDataToSend();
										dataToSend.getData()
												.setSize(
														dataToSend.getData()
																.size() + 1);
										addControl(new Textbox(port, el,
												dataToSend, svgApp.document,
												"", svgCanvas));
									}
								}
							}
						}

						if (!(displayNodes == null && controlNodes == null)) {

							server.addObserver(svgApp);
							svgApp.addObserver(toolBar);
							server.addObserver(toolBar);
							svgApp.addObserver(server);
							// notify server that rendering process is completed
							// after receiving this notification, server starts
							// listening

							svgApp.setChanged();
							svgApp.notifyObservers("rendering done");

							// svgApp.addElementInformations();
							// start in pause mode
							// toolBar.setPaused(true);
						}
						else {
							// if neither display tags nor control tags were
							// found, the user may have
							// a great time watching an animation free
							// non-interactive but beautiful image
							statusLabel
									.setText("Finished rendering SVG, but no display or control action found. Nothing to do...");
							statusLabel.setVisible(true);
						}
					}
					catch (MappingException me) {
						// something failed during parsing of the mapping, e.g.
						// a mapped object does
						// not exist in the svg file
						new ModelguiErrorDialog(me,
								"Error while processing the animation mapping file: ");
					}
					catch (MalformedURLException e2) {
						new ModelguiErrorDialog(e2, "URL is not well formed!");
					}
					catch (IOException e2) {
						new ModelguiErrorDialog(e2, "An error occured during some read/write operations!");
					}
					catch (SAXException e2) {
						new ModelguiErrorDialog(e2, "Mapping file is not valid! See details for more infos!");
					}
					catch (ParserConfigurationException e2) {
						new ModelguiErrorDialog(e2, "Could not parse file!");
					}
					catch (URISyntaxException e2) {
						new ModelguiErrorDialog(e2, "URI Syntax error!");
					}
				}
				else {
					// in this case we just rendered the modelgui logo.
					statusLabel.setText("Open new SVG file to start.");
					statusLabel.setVisible(true);
					if (AbstractGateway.getInstance() !=null)
						AbstractGateway.getInstance().addSimulationListener(toolBar);
					// svgApp.addObserver(toolBar);
					// svgApp.setChanged();
					// svgApp.notifyObservers("rendering done");
				}
				SplashWindow.disposeSplash();
				new Thread(new Runnable(){

					public void run() {
						try {
							Thread.sleep(500);
						}
						catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						progressBar.setVisible(false);					
					}
					
				}).start();
				
			}
		});
	}

	public void restart() {
		this.open(this.svgFile);
	}

	public void addControl(Control control) {
		this.controlList.add(control);
		svgApp.addObserver(control);
	}

	public void addAnimation(Animation animation) {
		this.animationList.add(animation);
	}

	/*
	 * To get scalable and resizable SVG documents, we need to add a 'viewport'
	 * attribute to the <svg> tag. if no viewbox attribute is found, this method
	 * prints a warning message but additionally it does nothing spectacular. It
	 * could be used to insert a new 'viewport' attribute by calculating the
	 * needed values, but at the moment that leads to a NullPointerException, so
	 * the 'important' lines are commented out and tagged as 'todo'.
	 */
	private void testViewBoxAttributeAndInsert() {
		final Element svgElement = document.getRootElement();
		// get svg tag's attributes
		if (!svgElement.hasAttribute("viewBox")) {
			// this SVG does not have a viewbox attribute, so we must add it
			// manually
			log.warning("Warning: SVG document has no viewBox element. Automatic scaling to canvas size will not work!");
			// first, get the width of the document
			String widthString = svgElement.getAttribute("width");
			float width = 0;
			if (widthString.endsWith("pt")) {
				widthString = widthString
						.substring(0, widthString.length() - 2).trim();
				// warn.println("widthString ohne pt: " + widthString);
				// convert pt to px:
				width = Float.parseFloat(widthString) * 4.0f / 3.0f;
			}
			else if (widthString.endsWith("px")) {
				// get substring of widthString without 'px'
				widthString = widthString
						.substring(0, widthString.length() - 2).trim();
			}
			else {
				try {
					width = Float.parseFloat(widthString);
				}
				catch (NumberFormatException e) {
					new ModelguiErrorDialog(e,
							"Could not parse width attribute: " + widthString);
				}
			}
			// same for height:
			String heightString = svgElement.getAttribute("height");
			float height = 0;
			if (heightString.endsWith("pt")) {
				heightString = heightString.substring(0,
						heightString.length() - 2).trim();
				// warn.println("heightString ohne pt: " + heightString);
				// convert pt to px:
				height = Float.parseFloat(heightString) * 4.0f / 3.0f;
			}
			else if (heightString.endsWith("px")) {
				heightString = heightString.substring(0,
						heightString.length() - 2).trim();
			}
			else {
				try {
					height = Float.parseFloat(heightString);
				}
				catch (NumberFormatException e) {
					new ModelguiErrorDialog(e,
							"Could not parse height attribute: " + heightString);
				}
			}

			@SuppressWarnings("unused")
			final float finalHeight = height;
			@SuppressWarnings("unused")
			final float finalWidth = width;
			// TODO: the following lines throw a NullPointerException
			/*
			 * svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(new
			 * Runnable(){
			 * 
			 * public void run() { // TODO Auto-generated method stub
			 * svgElement.setAttributeNS(null, "viewBox", "0 0 " +
			 * ((int)finalWidth) + " " + ((int)finalHeight)); }
			 * 
			 * });
			 */
			//
			// log.println("viewBox: " +
			// svgElement.getAttribute("viewBox"));
		}
		else {
			// if we are here, the SVG document DOES have a viewbox attribute,
			// so scalability
			// is guaranteed
			// String value = svgElement.getAttribute("viewBox");
			// log.println(value);
		}
		// svgElement.removeAttribute("width");
		// svgElement.removeAttribute("height");
	}

	/**
	 * 
	 */
	public void pack() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxwidthInt = (int) Math.floor(screensize.getWidth());
		int maxheightInt = (int) Math.floor(screensize.getHeight());
		this.frame.pack();
		if (this.frame.getWidth() > maxwidthInt)
			this.frame.setSize(maxwidthInt, this.frame.getHeight());
		if (this.frame.getHeight() > maxheightInt)
			this.frame.setSize(this.frame.getWidth(), maxheightInt);
	}

	/**
	 * @param uri
	 */
	public void open(URI file) {
		svgCanvas.setURI(file.toString());
		try {
			this.svgFile = file.toURL();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void open(URL svgFile2) {
		svgCanvas.setURI(svgFile2.toString());
		svgFile = svgFile2;
	}

	public void setStatusString(String status) {
		this.progressBar.setString(status);
	}

	public void setStatusString(String status, int value) {
		this.progressBar.setString(status);
		this.progressBar.setValue(value);
	}

	public void setStatus(int value) {
		this.progressBar.setValue(value);
	}

	public void setStatusBarVisible(boolean flag) {
		this.statusBarVisible = flag;
	}
		
	/**
	 * Just a convenience method to find out if we are in client mode (SCADE) or
	 * in server mode (Simulink)
	 * 
	 * @return
	 */
	private boolean isClientMode() {
		return ((JRadioButton) this.menu.getPreferencesDialog().getPreference(
				PreferencesDialog.SCADE_PREF_NAME)).isSelected();
	}

	public void update(Observable o, Object arg) {
		if (o == server) {
			statusLabel.setText(server.getStatus());
			statusLabel.setVisible(true);
			if (arg instanceof String) {
				String msg = (String) arg;
				if (msg.equals(" connected")) {
					toolBar.setEnableControls(true);
					// don't send delay at first but send initial dataToSend
					// value to simulink
					// server.send("delay pause");
				}
				if (msg.equals("server restarted")) {
					// log.println("server resetted");
					//toolBar.setPaused(true);
					toolBar.setEnableControls(false);
				}
			}
			if (server.isConnected() == false) {
				// animationList = null;
				return;
			}

			// do animations based on mappingFile
			if (svgCanvas != null && arg instanceof JavaStringData) {
				final JavaStringData data = (JavaStringData) arg;
				// using invokeAndWait() can lead to deadlock!			
				try {
					svgCanvas.getUpdateManager().getUpdateRunnableQueue()
							.invokeLater(new Runnable() {
								public void run() {
									for (int i = 0; i < animationList.size(); i++) {
										animationList.get(i).doAnimation(data);
									}
								}
							});
					//svgCanvas.getUpdateManager().resume();
				} catch(IllegalStateException e){
					/* do nothing */
				}

			}
		}
	}
	
	public static JProgressBar getProgressBar(){
		if (SVGApplication.progressBar == null)
			progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		return progressBar; 
	}
}
