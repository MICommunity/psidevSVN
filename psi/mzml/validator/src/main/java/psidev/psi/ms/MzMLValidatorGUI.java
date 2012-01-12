/*
 * MzMLValidatorGUI.java Created on __DATE__, __TIME__
 */

package psidev.psi.ms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

import psidev.psi.ms.rulefilter.MaldiOrEsiCondition;
import psidev.psi.ms.rulefilter.RuleFilterAgent;
import psidev.psi.ms.rulefilter.RuleFilterManager;
import psidev.psi.ms.swingworker.SwingWorker;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.util.ValidatorReport;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 * 
 * @author __USER__
 */
public class MzMLValidatorGUI extends javax.swing.JPanel implements RuleFilterAgent {

	private Exception error;
	private SwingWorker sw;
	private long runStartTime = -11;
	private MzMLValidator validator = null;
	protected RuleFilterManager ruleFilterManager;
	private static final String DEFAULT_PROGRESS_MESSAGE = "Select a file and press validate...";

	/** Creates new form MzMLValidatorGUI */
	public MzMLValidatorGUI() {

		initComponents();

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		buttonGroup1 = new javax.swing.ButtonGroup();
		buttonGroup2 = new javax.swing.ButtonGroup();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jTextInputFile = new javax.swing.JTextField();
		jButtonBrowse = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		jRadioInfoLevel = new javax.swing.JRadioButton();
		jRadioWarnLevel = new javax.swing.JRadioButton();
		jRadioErrorLevel = new javax.swing.JRadioButton();
		jPanel3 = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		jRadioMALDI = new javax.swing.JRadioButton();
		jRadioESI = new javax.swing.JRadioButton();
		jRadioOTHER = new javax.swing.JRadioButton();
		jPanel5 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextAreaMessages = new javax.swing.JTextArea();
		jPanel6 = new javax.swing.JPanel();
		jProgressBar = new javax.swing.JProgressBar();
		jPanel8 = new javax.swing.JPanel();
		jComboValidationType = new javax.swing.JComboBox();
		jPanel7 = new javax.swing.JPanel();
		jButtonValidate = new javax.swing.JButton();

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("File selection"));

		jLabel1.setText("mzML file to validate:");

		jButtonBrowse.setText("Browse...");
		jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonBrowseActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jLabel1)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(jTextInputFile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372,
								Short.MAX_VALUE)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(jButtonBrowse).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout
						.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
						.add(jLabel1)
						.add(jButtonBrowse)
						.add(jTextInputFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

		jPanel2.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Select minimal message level to display"));

		buttonGroup1.add(jRadioInfoLevel);
		jRadioInfoLevel.setText("Info");

		buttonGroup1.add(jRadioWarnLevel);
		jRadioWarnLevel.setSelected(true);
		jRadioWarnLevel.setText("Warn");

		buttonGroup1.add(jRadioErrorLevel);
		jRadioErrorLevel.setText("Error");

		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel2Layout
						.createSequentialGroup()
						.add(27, 27, 27)
						.add(jRadioInfoLevel)
						.add(50, 50, 50)
						.add(jRadioWarnLevel)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 45,
								Short.MAX_VALUE).add(jRadioErrorLevel).add(42, 42, 42)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel2Layout
						.createSequentialGroup()
						.add(jPanel2Layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
								.add(jRadioWarnLevel).add(jRadioInfoLevel).add(jRadioErrorLevel))
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Select one option"));

		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Ionization source"));

		buttonGroup2.add(jRadioMALDI);
		jRadioMALDI.setText("MALDI source");

		buttonGroup2.add(jRadioESI);
		jRadioESI.setSelected(true);
		jRadioESI.setText("ESI source");

		buttonGroup2.add(jRadioOTHER);
		jRadioOTHER.setText("Other source");

		org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout
				.setHorizontalGroup(jPanel4Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel4Layout
								.createSequentialGroup()
								.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).add(jRadioMALDI)
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jRadioESI)
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jRadioOTHER)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel4Layout
						.createSequentialGroup()
						.add(jPanel4Layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
								.add(jRadioMALDI).add(jRadioESI).add(jRadioOTHER))
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel3Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel3Layout
						.createSequentialGroup()
						.add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));

		jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

		jTextAreaMessages.setColumns(20);
		jTextAreaMessages.setLineWrap(true);
		jTextAreaMessages.setRows(5);
		jTextAreaMessages.setWrapStyleWord(true);
		jScrollPane1.setViewportView(jTextAreaMessages);

		org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel5Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576,
								Short.MAX_VALUE).addContainerGap()));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel5Layout
						.createSequentialGroup()
						.add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 276,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));

		jProgressBar.setString("Select a file and press validate...");
		jProgressBar.setStringPainted(true);

		org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel6Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576,
								Short.MAX_VALUE).addContainerGap()));
		jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel6Layout
						.createSequentialGroup()
						.add(jProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Type of validation"));

		jComboValidationType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"MIAPE-compliant validation", "Semantic validation" }));
		jComboValidationType.setToolTipText("Select the type of validation");
		jComboValidationType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboValidationTypeActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel8Layout
						.createSequentialGroup()
						.add(51, 51, 51)
						.add(jComboValidationType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(39, Short.MAX_VALUE)));
		jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel8Layout
						.createSequentialGroup()
						.add(jComboValidationType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Action"));

		jButtonValidate.setText("Validate!");
		jButtonValidate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonValidateActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				jPanel7Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jButtonValidate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 242,
								Short.MAX_VALUE).addContainerGap()));
		jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel7Layout.createSequentialGroup().addContainerGap().add(jButtonValidate)
						.addContainerGap(38, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.add(layout
										.createSequentialGroup()
										.add(layout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING,
														false)
												.add(jPanel2,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.add(jPanel3,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(layout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING)
												.add(jPanel8,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.add(jPanel7,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)))).addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.add(layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(layout
										.createSequentialGroup()
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
								.add(org.jdesktop.layout.GroupLayout.TRAILING,
										layout.createSequentialGroup()
												.add(8, 8, 8)
												.add(jPanel2,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
								.add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
	}// </editor-fold>
		// GEN-END:initComponents

	private void jComboValidationTypeActionPerformed(java.awt.event.ActionEvent evt) {
		if (isMIAPEValidationSelected())
			enableRadioButtons(true);
		else if (isSemanticValidationSelected())
			enableRadioButtons(false);
	}

	private void enableRadioButtons(boolean b) {
		this.jRadioESI.setEnabled(b);
		this.jRadioMALDI.setEnabled(b);
		this.jRadioOTHER.setEnabled(b);
	}

	private void jButtonBrowseActionPerformed(ActionEvent evt) {
		selectFile();

	}

	private void jButtonValidateActionPerformed(ActionEvent evt) {
		// Check input file.
		String input = jTextInputFile.getText();
		if (input == null || input.trim().equals("")) {
			JOptionPane.showMessageDialog(this, "Please specify an mzML file to validate!",
					"No mzML file specified!", JOptionPane.WARNING_MESSAGE);
			jTextInputFile.requestFocus();
			return;
		}
		// See if the file exists.
		final File inputFile = new File(input);
		if (!inputFile.exists()) {
			JOptionPane.showMessageDialog(this, "The mzML file you specified ('" + input
					+ "') does not exist!", "Specified mzML file not found!",
					JOptionPane.WARNING_MESSAGE);
			jTextInputFile.requestFocus();
			return;
		}
		// make sure the input is not a directory!
		if (inputFile.isDirectory()) {
			JOptionPane.showMessageDialog(this, "The mzML file you specified ('" + input
					+ "') is not a file but a folder!", "Folder specified instead of file!",
					JOptionPane.WARNING_MESSAGE);
			jTextInputFile.requestFocus();
			return;
		}

		// Reset the error.
		error = null;

		// OK, we have a valid file to validate.
		// Proceed with the work.

		// Disable GUI
		jTextInputFile.setEnabled(false);
		jButtonBrowse.setEnabled(false);
		jButtonValidate.setEnabled(false);
		jRadioInfoLevel.setEnabled(false);
		jRadioWarnLevel.setEnabled(false);
		jRadioErrorLevel.setEnabled(false);
		jComboValidationType.setEditable(false);
		enableRadioButtons(false);

		if (isMIAPEValidationSelected()) {
			try {
				this.ruleFilterManager = new RuleFilterManager(new File(
						getProperty("miape.filter.rule.file")));
				// filter rules by the options of the user
			} catch (JAXBException e1) {
				// no filter rules
			} catch (IllegalArgumentException e2) {
				// no filter rules
			}
		}
		// Call the SwingWorker that will start the validation.
		sw = new SwingWorker() {
			public Object construct() {
				Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
				jTextAreaMessages.setText("");
				jTextAreaMessages.setCaretPosition(0);
				jProgressBar.setIndeterminate(true);
				jProgressBar.setString("Initializing validator...");
				runStartTime = System.currentTimeMillis();
				try {
					// Lazy cached validator.
					if (validator == null) {
						jProgressBar.setString("Loading configuration files...");

						InputStream ontology = new FileInputStream(getProperty("ontologies.file"));

						validator = new MzMLValidator(ontology, getMappingRuleFile(),
								getObjectRuleFile(), MzMLValidatorGUI.this);
						ontology.close();
					} else {

						// reset all validator fields except the ontologies
						validator.reset(getMappingRuleFile(), getObjectRuleFile());
					}
					// this will add to the validator the rules to be skipped
					if (isMIAPEValidationSelected() && ruleFilterManager != null)
						ruleFilterManager.filterRulesByUserOptions(getSelectedOptions());

					// common settings to set each time the validation button is
					// pressed
					validator.setValidatorGUI(MzMLValidatorGUI.this);
					validator.setMessageReportLevel(getLevel());
					validator.setSchemaUri(getSchemaUri());
					validator.setSkipValidation(skipValidation());

					// set the rule filter manager
					validator.setRuleFilterManager(ruleFilterManager);

					jProgressBar.setString("Indexing mzML file...");
					final Collection<ValidatorMessage> validationResult = validator
							.startValidation(inputFile);
					messages.addAll(validationResult);

				} catch (Exception e) {
					e.printStackTrace();
					notifyOfError(e);
				}
				return messages;
			}

			public void finished() {
				validationDone();
			}
		};
		sw.start();
	}

	public String getMappingRuleFile() {
		// by default:
		String mappingRuleFile = "ms-mapping.xml";
		if (this.jComboValidationType.getSelectedItem().equals(
				ValidationType.MIAPE_VALIDATION.getName()))
			mappingRuleFile = getProperty("mapping.rule.file.miape.validation");
		else if (this.jComboValidationType.getSelectedItem().equals(
				ValidationType.SEMANTIC_VALIDATION.getName()))
			mappingRuleFile = getProperty("mapping.rule.file.semantic.validation");

		return mappingRuleFile;
	}

	public String getObjectRuleFile() {
		// by default:
		String objectRuleFile = "ObjectRules.xml";
		if (isMIAPEValidationSelected())
			objectRuleFile = getProperty("object.rule.file.miape.validation");
		else if (isSemanticValidationSelected())
			objectRuleFile = getProperty("object.rule.file.semantic.validation");

		return objectRuleFile;
	}

	boolean isSemanticValidationSelected() {
		return this.jComboValidationType.getSelectedItem().equals(
				ValidationType.SEMANTIC_VALIDATION.getName());
	}

	boolean isMIAPEValidationSelected() {
		return this.jComboValidationType.getSelectedItem().equals(
				ValidationType.MIAPE_VALIDATION.getName());
	}

	private String getProperty(String propertyName) {
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is;

		is = cl.getResourceAsStream("validation.properties");
		if (is == null)
			throw new IllegalArgumentException("validation.properties" + " file not found");

		Properties prop = new Properties();
		try {
			prop.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		return prop.getProperty(propertyName);
	}

	private void validationDone() {
		jProgressBar.setValue(jProgressBar.getMaximum());
		if (error != null) {
			JOptionPane.showMessageDialog(this,
					new String[] { "A problem occurred when attempting to validate the mzML file!",
							error.getMessage() }, "Error occurred during validation!",
					JOptionPane.ERROR_MESSAGE);
		}
		Collection<ValidatorMessage> messages = (Collection<ValidatorMessage>) sw.get();

		showMessages(messages);

		jProgressBar.setIndeterminate(false);
		jProgressBar.setValue(0);
		// Calculate last run time.
		long delta = System.currentTimeMillis() - runStartTime;
		// Reset run start time and moment.
		runStartTime = -1;
		jProgressBar.setString(DEFAULT_PROGRESS_MESSAGE + " (last run took " + (delta / 1000)
				+ " seconds)");
		// Re-enable GUI.
		jTextInputFile.setEnabled(true);
		jButtonBrowse.setEnabled(true);
		jButtonValidate.setEnabled(true);
		jRadioInfoLevel.setEnabled(true);
		jRadioWarnLevel.setEnabled(true);
		jRadioErrorLevel.setEnabled(true);
		jComboValidationType.setEnabled(true);
		jComboValidationTypeActionPerformed(null);

		showExtendedReportDialog(messages.size());
		// showReportDialog();

	}

	private void showReportDialog() {
		ValidatorReport report = validator.getReport();
		StringBuilder sb = new StringBuilder("<html><body><table cellpadding='5'>");
		sb.append("<tr align='left'><td>CvMappingRule total count: </td><td>")
				.append(validator.getCvRuleManager().getCvRules().size()).append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules not run: </td><td>")
				.append(report.getCvRulesNotChecked().size()).append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules with invalid Xpath: </td><td>")
				.append(report.getCvRulesInvalidXpath().size()).append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules with valid Xpath, but no hit: </td><td>")
				.append(report.getCvRulesValidXpath().size()).append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules run & valid: </td><td>")
				.append(report.getCvRulesValid().size()).append("</td></tr>");
		sb.append("</table></body></html>");

		JOptionPane.showMessageDialog(this, new String[] { "", sb.toString() },
				"CvMappingRule Report", JOptionPane.INFORMATION_MESSAGE);

	}

	private void showExtendedReportDialog(int messageNumber) {
		ExtendedValidatorReport report = validator.getExtendedReport();
		StringBuilder sb = new StringBuilder("<html><body><table cellpadding='5'>");

		sb.append("<tr align='left'><td>CvMappingRule total count: </td><td>")
				.append(report.getTotalCvRules()).append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules not run: </td><td>")
				.append(report.getCvRulesNotChecked().size()).append("</td></tr>");
		sb.append("<tr align='left'><td>");
		// red in case of more than 0 invalid rules
		if (report.getCvRulesInvalidXpath().size() > 0)
			sb.append("<font color='red'>");
		sb.append("CvMappingRules with invalid Xpath: ");
		if (report.getCvRulesInvalidXpath().size() > 0)
			sb.append("</font>");
		sb.append("</td><td>");
		if (report.getCvRulesInvalidXpath().size() > 0)
			sb.append("<font color='red'>");
		sb.append(report.getCvRulesInvalidXpath().size());
		if (report.getCvRulesInvalidXpath().size() > 0)
			sb.append("</font>");
		sb.append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules with valid Xpath, but no hit: </td><td>")
				.append(report.getCvRulesValidXpath().size()).append("</td></tr>");
		sb.append("<tr align='left'><td>CvMappingRules run & valid: </td><td>")
				.append(report.getCvRulesValid().size()).append("</td></tr>");
		sb.append("<tr><td></td></tr>");
		sb.append("<tr align='left'><td>ObjectRules total count:</td><td>"
				+ report.getTotalObjectRules() + "</td></tr>");
		sb.append("<tr align='left'><td>ObjectRules not run:</td><td>"
				+ report.getObjectRulesNotChecked().size() + "</td></tr>");
		sb.append("<tr align='left'><td>");
		// red in case of more than 0 invalid rules
		if (report.getObjectRulesInvalid().size() > 0)
			sb.append("<font color='red'>");
		sb.append("ObjectRules run & invalid:");
		if (report.getObjectRulesInvalid().size() > 0)
			sb.append("</font>");
		sb.append("</td><td>");
		if (report.getObjectRulesInvalid().size() > 0)
			sb.append("<font color='red'>");
		sb.append(report.getObjectRulesInvalid().size());
		if (report.getObjectRulesInvalid().size() > 0)
			sb.append("</font>");
		sb.append("</td></tr>");
		sb.append("<tr align='left'><td>ObjectRules run & valid:</td><td>"
				+ report.getObjectRulesValid().size() + "</td></tr>");
		sb.append("<tr><td></td></tr>");

		sb.append("<tr align='left'><td>");
		if (messageNumber > 0)
			sb.append("<font color='red'>");
		sb.append("Messages received: ");
		if (messageNumber > 0)
			sb.append("</font>");
		sb.append("</td><td>");
		if (messageNumber > 0)
			sb.append("<font color='red'>");
		sb.append(messageNumber);
		if (messageNumber > 0)
			sb.append("</font>");
		sb.append("</td></tr>");

		sb.append("</table></body></html>");

		JOptionPane.showMessageDialog(this, new String[] { "", sb.toString() },
				"Rule Execution Report", JOptionPane.INFORMATION_MESSAGE);

	}

	private void showMessages(Collection<ValidatorMessage> aMessages) {
		Collection<ValidatorMessage> messages = (Collection<ValidatorMessage>) sw.get();
		if (messages != null && messages.size() > 0) {
			this.jTextAreaMessages.setText(printValidatorMessages(messages));
			this.jTextAreaMessages.setCaretPosition(0);
		} else {
			this.jTextAreaMessages.setText("\n\nNo messages were returned by the validator.");
			if (error == null) {
				JOptionPane.showMessageDialog(MzMLValidatorGUI.this,
						"Your mzIdentML file validated at the current message level.",
						"No messages produced.", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		this.jTextAreaMessages.append(validator.printValidatorReport());
		// this.jTextAreaMessages.append(validator.printCvContextReport());
	}

	private String printValidatorMessages(Collection<ValidatorMessage> messages) {
		StringBuffer sb = new StringBuffer(
				"\n\nThe following messages were obtained during the validation of your XML file:\n\n\n");
		int count = 0;
		for (ValidatorMessage message : messages) {
			// only show messages that have the same (or higher) message level
			// as selected
			if (message.getLevel().isSame(getLevel()) || message.getLevel().isHigher(getLevel())) {
				count++;
				sb.append("\n\nMessage ").append(count).append(":\n");
				sb.append("    Level: ").append(message.getLevel()).append("\n");
				sb.append("    ").append(message.getContext()).append("\n");
				sb.append("    --> ").append(message.getMessage()).append("\n");
				if (message.getRule() != null && message.getRule().getHowToFixTips() != null) {
					for (String howToFixTip : message.getRule().getHowToFixTips()) {
						sb.append("    Tip: " + howToFixTip).append("\n");
					}
				}
			}
		}

		return sb.toString();
	}

	private void selectFile() {
		File startHere = new File("/");
		// Open the filechooser on the root or the folder the user
		// already specified (if it exists).
		if (!jTextInputFile.getText().trim().equals("")) {
			File f = new File(jTextInputFile.getText().trim());
			if (f.exists()) {
				startHere = f;
			}
		}
		JFileChooser jfc = new JFileChooser(startHere);
		int returnVal = 0;
		jfc.setDialogType(JFileChooser.OPEN_DIALOG);
		jfc.setDialogTitle("Select mzML file to validate");
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				boolean result = false;
				if (f.isDirectory() || f.getName().toLowerCase().endsWith(".mzml")
						|| f.getName().toLowerCase().endsWith(".xml")) {
					result = true;
				}
				return result;
			}

			public String getDescription() {
				return "mzML files";
			}
		});
		returnVal = jfc.showOpenDialog(jTextInputFile);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			jTextInputFile.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
		}

	}

	private MessageLevel getLevel() {
		MessageLevel result = null;
		if (jRadioInfoLevel.isSelected()) {
			result = MessageLevel.INFO;
		} else if (jRadioWarnLevel.isSelected()) {
			result = MessageLevel.WARN;
		} else if (jRadioErrorLevel.isSelected()) {
			result = MessageLevel.ERROR;
		}
		return result;
	}

	private URI getSchemaUri() {
		// ToDo: implement
		return validator.getSchemaUri();
	}

	private boolean skipValidation() {
		// ToDo: implement
		return false;
	}

	public void notifyOfError(Exception e) {
		error = e;
	}

	public void initProgress(int min, int max, int current) {
		jProgressBar.setIndeterminate(false);
		jProgressBar.setMinimum(min);
		jProgressBar.setMaximum(max);
		jProgressBar.setValue(current);
	}

	public void setProgress(int value, String message) {
		if (value > jProgressBar.getMaximum()) {
			// throw new
			// IllegalArgumentException("You tried to set the progress to value '"
			// + value
			// + "', but the maximum was '" + jProgressBar.getMaximum() + "'!");
			value = jProgressBar.getMaximum();
		}
		jProgressBar.setValue(value);
		jProgressBar.setString(message);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("No Windows lookAndFeel found");
		}
		MzMLValidatorGUI jpanValidator = new MzMLValidatorGUI();

		if (args != null && args.length == 1) {
			jpanValidator.jTextInputFile.setText(args[0]);
		}

		JFrame validatorFrame = new JFrame("mzML validator GUI (mzML version 1.0)");
		validatorFrame.getContentPane().add(jpanValidator, BorderLayout.CENTER);
		validatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		validatorFrame.setResizable(false);
		validatorFrame.addWindowListener(new WindowAdapter() {
			/**
			 * Invoked when a window is in the process of being closed. The
			 * close operation can be overridden at this point.
			 */
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		validatorFrame.setLocation(100, 100);
		validatorFrame.pack();
		validatorFrame.setVisible(true);
	}

	public HashMap<String, String> getSelectedOptions() {
		HashMap<String, String> conditionSet = new HashMap<String, String>();
		if (this.jRadioESI.isSelected()) {
			conditionSet.put(MaldiOrEsiCondition.getID(), MaldiOrEsiCondition.ESI.getOption());
		} else if (this.jRadioMALDI.isSelected()) {
			conditionSet.put(MaldiOrEsiCondition.getID(), MaldiOrEsiCondition.MALDI.getOption());
		} else if (this.jRadioOTHER.isSelected()) {
			conditionSet.put(MaldiOrEsiCondition.getID(), MaldiOrEsiCondition.OTHER.getOption());
		}
		return conditionSet;
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.ButtonGroup buttonGroup2;
	private javax.swing.JButton jButtonBrowse;
	private javax.swing.JButton jButtonValidate;
	private javax.swing.JComboBox jComboValidationType;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JProgressBar jProgressBar;
	private javax.swing.JRadioButton jRadioESI;
	private javax.swing.JRadioButton jRadioErrorLevel;
	private javax.swing.JRadioButton jRadioInfoLevel;
	private javax.swing.JRadioButton jRadioMALDI;
	private javax.swing.JRadioButton jRadioOTHER;
	private javax.swing.JRadioButton jRadioWarnLevel;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextAreaMessages;
	private javax.swing.JTextField jTextInputFile;
	// End of variables declaration//GEN-END:variables

}