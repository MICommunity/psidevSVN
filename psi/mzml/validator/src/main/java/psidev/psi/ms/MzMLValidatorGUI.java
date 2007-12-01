/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 30-Aug-2007
 * Time: 11:39:30
 */
package psidev.psi.ms;

import psidev.psi.ms.swingworker.SwingWorker;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorMessage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
/*
 * CVS information:
 *
 * $Revision$
 * $Date$
 */

/**
 * This class provides a simple graphical user interface on top of the mzML validator.
 *
 * @author Lennart Martens
 * @version $Id$
 */
public class MzMLValidatorGUI extends JPanel {

    private JTextField txtInputFile = null;
    private JButton btnBrowse = null;

    private JRadioButton rbtInfo = null;
    private JRadioButton rbtWarn = null;
    private JRadioButton rbtError = null;

    private JButton btnValidate = null;

    private JProgressBar progress = null;

    private JTextArea txtOutput = null;

    private MzMLValidator validator = null;
    private SwingWorker sw = null;
    private Exception error = null;


    private static final String DEFAULT_PROGRESS_MESSAGE = "Select a file and press validate...";




    public MzMLValidatorGUI() {
        this.constructScreen();
    }

    private void constructScreen() {
        txtInputFile = new JTextField(30);
        txtInputFile.setMaximumSize(new Dimension(txtInputFile.getMaximumSize().width, txtInputFile.getPreferredSize().height));
        JLabel lblInputFile = new JLabel("mzML file to validate: ");
        btnBrowse = new JButton("Browse...");
        btnBrowse.setMnemonic(KeyEvent.VK_B);
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        btnBrowse.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectFile();
                }
            }
        });

        rbtInfo = new JRadioButton("Info");
        rbtWarn = new JRadioButton("Warn");
        rbtError = new JRadioButton("Error");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtInfo);
        bg.add(rbtWarn);
        bg.add(rbtError);
        rbtWarn.setSelected(true);

        progress = new JProgressBar();
        progress.setString(DEFAULT_PROGRESS_MESSAGE);
        progress.setStringPainted(true);

        txtOutput = new JTextArea(20,40);
        txtOutput.setEditable(false);

        // File input panel.
        JPanel jpanFile = new JPanel();
        jpanFile.setLayout(new BoxLayout(jpanFile, BoxLayout.LINE_AXIS));
        jpanFile.setBorder(BorderFactory.createTitledBorder("File selection"));
        jpanFile.add(Box.createHorizontalStrut(5));
        jpanFile.add(lblInputFile);
        jpanFile.add(Box.createHorizontalStrut(5));
        jpanFile.add(txtInputFile);
        jpanFile.add(Box.createHorizontalStrut(5));
        jpanFile.add(btnBrowse);
        jpanFile.add(Box.createHorizontalGlue());

        // Radio button panel for message levels.
        JPanel jpanLevels = new JPanel();
        jpanLevels.setLayout(new BoxLayout(jpanLevels, BoxLayout.LINE_AXIS));
        jpanLevels.setBorder(BorderFactory.createTitledBorder("Select minimal message level to display"));
        jpanLevels.add(Box.createHorizontalGlue());
        jpanLevels.add(rbtInfo);
        jpanLevels.add(Box.createHorizontalGlue());
        jpanLevels.add(rbtWarn);
        jpanLevels.add(Box.createHorizontalGlue());
        jpanLevels.add(rbtError);
        jpanLevels.add(Box.createHorizontalGlue());

        // Button panel.
        JPanel jpanButtons = this.getButtonPanel();

        // Progress bar panel.
        JPanel jpanProgress = new JPanel();
        jpanProgress.setLayout(new BoxLayout(jpanProgress, BoxLayout.LINE_AXIS));
        jpanProgress.setBorder(BorderFactory.createTitledBorder("Progress"));
        jpanProgress.add(Box.createHorizontalStrut(5));
        JPanel jpanIntermediateProgress = new JPanel();
        jpanIntermediateProgress.setLayout(new BoxLayout(jpanIntermediateProgress, BoxLayout.PAGE_AXIS));
        jpanIntermediateProgress.add(progress);
        jpanIntermediateProgress.add(Box.createVerticalStrut(5));
        jpanProgress.add(jpanIntermediateProgress);
        jpanProgress.add(Box.createHorizontalStrut(5));

        // Output panel.
        JPanel jpanOutput = new JPanel(new BorderLayout());
        jpanOutput.setBorder(BorderFactory.createTitledBorder("Messages"));
        JScrollPane scrollpane = new JScrollPane(txtOutput);
        jpanOutput.add(scrollpane, BorderLayout.CENTER);

        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.PAGE_AXIS));
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanFile);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanLevels);
        jpanMain.add(Box.createVerticalStrut(15));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(15));
        jpanMain.add(jpanProgress);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanOutput);
        jpanMain.add(Box.createVerticalStrut(5));

        this.setLayout(new BorderLayout());
        this.add(jpanMain);
    }

    private JPanel getButtonPanel() {

        btnValidate = new JButton("Validate!");
        btnValidate.setMnemonic(KeyEvent.VK_V);
        btnValidate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validatePressed();
            }
        });
        btnValidate.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validatePressed();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.LINE_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnValidate);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    private void selectFile() {
        File startHere = new File("/");
        // Open the filechooser on the root or the folder the user
        // already specified (if it exists).
        if(!txtInputFile.getText().trim().equals("")) {
            File f= new File(txtInputFile.getText().trim());
            if(f.exists()) {
                startHere = f;
            }
        }
        JFileChooser jfc = new JFileChooser(startHere);
        int returnVal = 0;
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setDialogTitle("Select mzML file to validate");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if(f.isDirectory() || f.getName().toLowerCase().endsWith(".xml")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "XML file";
            }
        });
        jfc.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if(f.isDirectory() || f.getName().toLowerCase().endsWith(".mzml")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "mzML file";
            }
        });
        returnVal = jfc.showOpenDialog(txtInputFile);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            txtInputFile.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
        }
    }

    public static void main(String[] args) {

        MzMLValidatorGUI jpanValidator = new MzMLValidatorGUI();

        if(args != null && args.length == 1) {
            jpanValidator.txtInputFile.setText(args[0]);
        }

        JFrame validatorFrame = new JFrame("mzML validator GUI (mzML version 0.99.1)");
        validatorFrame.getContentPane().add(jpanValidator, BorderLayout.CENTER);
        validatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validatorFrame.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
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


    private void validatePressed() {
        // Check input file.
        String input = txtInputFile.getText();
        if(input == null || input.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Please specify an mzML file to validate!", "No mzML file specified!", JOptionPane.WARNING_MESSAGE);
            txtInputFile.requestFocus();
            return;
        }
        // See if the file exists.
        final File inputFile = new File(input);
        if(!inputFile.exists()) {
            JOptionPane.showMessageDialog(this, "The mzML file you specified ('" + input + "') does not exist!", "Specified mzML file not found!", JOptionPane.WARNING_MESSAGE);
            txtInputFile.requestFocus();
            return;
        }
        // make sure the input is not a directory!
        if(inputFile.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The mzML file you specified ('" + input + "') is not a file but a folder!", "Folder specified instead of file!", JOptionPane.WARNING_MESSAGE);
            txtInputFile.requestFocus();
            return;
        }

        // Reset the error.
        error = null;

        // OK, we have a valid file to validate.
        // Proceed with the work.

        // Disable GUI
        txtInputFile.setEnabled(false);
        btnBrowse.setEnabled(false);
        btnValidate.setEnabled(false);
        rbtInfo.setEnabled(false);
        rbtWarn.setEnabled(false);
        rbtError.setEnabled(false);

        // Call the SwingWorker that will start the validation.
        sw = new SwingWorker() {
            public Object construct() {
                Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
                txtOutput.setText("");
                txtOutput.setCaretPosition(0);
                progress.setIndeterminate(true);
                progress.setString("Initializing validator...");
                try {
                    // Lazy cached validator.
                    if(validator == null) {
                        InputStream ontology = new FileInputStream("ontologies.xml");
                        InputStream cvMapping = new FileInputStream("ms-mapping.xml");
                        InputStream codedRules = new FileInputStream("ObjectRules.xml");
                        validator = new MzMLValidator(ontology, cvMapping, codedRules);
                        ontology.close();
                        cvMapping.close();
                        codedRules.close();
                    }
                    messages.addAll(validator.startValidation(inputFile, getLevel(), MzMLValidatorGUI.this));
                } catch(Exception e) {
                    MzMLValidatorGUI.this.notifyOfError(e);
                }
                return messages;
            }

            public void finished() {
                MzMLValidatorGUI.this.validationDone();
            }
        };
        sw.start();
    }

    private void validationDone() {
        progress.setValue(progress.getMaximum());
        if(error != null) {
            JOptionPane.showMessageDialog(this, new String[]{"A problem occurred when attempting to validate the mzML file!", error.getMessage()}, "Error occurred during validation!", JOptionPane.ERROR_MESSAGE);
        }
        Collection<ValidatorMessage> messages = (Collection<ValidatorMessage>)sw.get();

        if(messages != null && messages.size() > 0) {
            showMessages(messages);
        } else {
            txtOutput.setText("\n\nNo messages were returned by the validator.");
            if(error == null) {
                JOptionPane.showMessageDialog(MzMLValidatorGUI.this, "Your mzML file validated at the current message level.", "No messages produced.", JOptionPane.INFORMATION_MESSAGE);
            }
        }


        progress.setIndeterminate(false);
        progress.setValue(0);
        progress.setString(DEFAULT_PROGRESS_MESSAGE);
        // Re-enable GUI.
        txtInputFile.setEnabled(true);
        btnBrowse.setEnabled(true);
        btnValidate.setEnabled(true);
        rbtInfo.setEnabled(true);
        rbtWarn.setEnabled(true);
        rbtError.setEnabled(true);
    }

    private void showMessages(Collection<ValidatorMessage> aMessages) {
        StringBuffer sb = new StringBuffer("\n\nThe following messages were obtained during the validation of your XML file:\n\n\n");
        int count = 0;
        for (Iterator lIterator = aMessages.iterator(); lIterator.hasNext();) {
            count++;
            ValidatorMessage lMessage = (ValidatorMessage)lIterator.next();
            sb.append("\n\nMessage " + count + ":\n");
            sb.append("    Level: " + lMessage.getLevel() + "\n");
            sb.append("    Context: " + lMessage.getContext() + "\n");
            sb.append("     --> " + lMessage.getMessage() + "\n");
        }
        txtOutput.setText(sb.toString());
        txtOutput.setCaretPosition(0);
    }

    private MessageLevel getLevel() {
        MessageLevel result = null;
        if(rbtInfo.isSelected()) {
            result = MessageLevel.INFO;
        } else if(rbtWarn.isSelected()) {
            result = MessageLevel.WARN;
        } else if(rbtError.isSelected()) {
            result = MessageLevel.ERROR;
        }
        return result;
    }

    public void notifyOfError(Exception e) {
        error = e;
    }

    public void initProgress(int min, int max, int current) {
        progress.setIndeterminate(false);
        progress.setMinimum(min);
        progress.setMaximum(max);
        progress.setValue(current);
    }

    public void setProgress(int value, String message) {
        if(value > progress.getMaximum()) {
            throw new IllegalArgumentException("You tried to set the progress to value '" + value + "', but the maximum was '" + progress.getMaximum() + "'!");
        }
        progress.setValue(value);
        progress.setString(message);
    }
}
