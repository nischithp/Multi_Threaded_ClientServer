//Name: Nischith Javagal Panish
//UTA ID: 1001780908

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;

import javax.swing.JTextArea;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import net.miginfocom.swing.MigLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.awt.GridBagConstraints;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Color;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.print.attribute.standard.Severity;
import javax.swing.ButtonGroup;

public class ServerProg extends JFrame {

	static JPanel contentPane;
	static JFrame frame = new JFrame();
	static JLabel serverLabel = new JLabel("Server");
	static JButton exitButton = new JButton("Exit");
	static JLabel serverStart = new JLabel();
	static JTextArea infoLabel = new JTextArea();
	static JTextArea currentlyConnected = new JTextArea();
	private static Boolean fileCreationFlag = false;
	public static HashMap<String, String[]> usersToHomeDirectoryMap = new HashMap<String, String[]>();

	static Set<String> currentUsers = new HashSet<String>();
	private static Set<String> userNames = new HashSet<String>();
	static HashMap<String, String> serverFolderToUserName = new HashMap<String, String>();
	private static JTextField createTextField;
	private static JTextField moveSourceTextField;
	private static JTextField moveDestinationTextField;
	private static JTextField deleteTextField;
	private static JTextField renameOldTextField;
	private static JTextField renameNewTextField;

	private static JRadioButton createRadioButton;
	private static JRadioButton moveRadioButton;
	private static JRadioButton deleteRadioButton;
	private static JRadioButton renameRadioButton;
	private static JRadioButton listRadioButton;
	private static JButton performAction;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField listTextField;
	private JTextField undoTextField;

	public static void main(String[] args) throws IOException, BadLocationException {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerProg frame = new ServerProg();
					frame.setVisible(true);
					frame.setTitle("Server");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

//this method contains the code to start the server and make it listen at the
//specified port.
		startServer();
	}

//	Citation: https://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
//	Citation: https://stackabuse.com/java-list-files-in-a-directory/#:~:text=list(),an%20array%20of%20String%20s.&text=Using%20a%20simple%20for%2Deach,print%20out%20the%20String%20s.

//	Check for illegal characters entered in the username using the createDirectories() method 
//	which creates the directory if it does not exist, else raises an exception. Check if the entered username exists in the currentUsers HashSet. If it doesn't, proceed, else return false
	private static boolean checkUserNames(String username) {
		if (!currentUsers.contains(username)) {
			try {
				Files.createDirectories(Paths.get(".\\users\\" + username.trim()));
				return true;
			} catch (Exception e) {
				infoLabel.append("\n Cannot create folder. Error: " + e);
				System.out.println("Cannot create folder. Error: " + e);
			}
		}
		return false;
	}

//This function starts a server at port 6666 and listens for any connections that clients might attempt at that port. IT also has the username validation methods
//	which checks if the username is already in the HashSet, and if it is, it tells the user that he cannot use that name. If the client's username is valid, a folder is created
//	for the client and path is sent to client using DataOutputStream and they are added to currently connected clients list

	private static void startServer() throws IOException, BadLocationException {
		final int PORT = 6666;
		ServerSocket serverSocket = new ServerSocket(PORT);

		infoLabel.append("Server started...\n" + "Wating for clients...\n");
		System.out.println("Server started...");
		System.out.println("Wating for clients...");

		while (true) {
			Socket clientSocket = serverSocket.accept();
			try {
				DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
				DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
				String username = dataInputStream.readUTF();
				username = username.trim();

//Check if username already exists in the HashMap, else add it to the hashmap so that the next time it will be identified as already present.
//This can also be implemented by checking the file names present in the directory. 

				if ((checkUserNames(username))) {
					if (serverFolderToUserName.get(username) == null) {
						serverFolderToUserName.put(username, (serverFolderToUserName.size() % 3 == 0) ? "A"
								: (serverFolderToUserName.size() % 3 == 1) ? "B" : "C");
					}
					infoLabel.append("\nServer Directory given to :" + username + " is: "
							+ serverFolderToUserName.get(username));
					infoLabel.append("\n" + username + " has connected succesfully.\n");
					userNames.add(username);
					currentUsers.add(username);
					currentlyConnected.setText("");
					for (String user : currentUsers) {
						currentlyConnected.append("\n" + user);
					}
					dataOutputStream.writeUTF(".\\users\\" + username);

//Create a new thread for the newly connected client so that they can perform their file operations and call the constructor to initialize the values
					new SocketThread(clientSocket, dataInputStream, dataOutputStream, username).start();
				} else {
					clientSocket.close();
					infoLabel.append(
							"\n This username already exists or the filename is not valid. Please try again with a new name\n");
					System.out.println(
							"This username already exists or the filename is not valid. Please try again with a new name");
					JOptionPane.showMessageDialog(null,
							"This username already exists or the filename is not valid. Please try again with a new name");
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * Create the frame.
	 */
//	Creates the GUI. This is mostly autogenerated code using JFrames and WindowBuilder.
	public ServerProg() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1021, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.NORTH, currentlyConnected, 387, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, currentlyConnected, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, currentlyConnected, -763, SpringLayout.EAST, contentPane);

		infoLabel = new JTextArea();
		sl_contentPane.putConstraint(SpringLayout.EAST, infoLabel, 0, SpringLayout.EAST, exitButton);
		sl_contentPane.putConstraint(SpringLayout.NORTH, exitButton, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, exitButton, -10, SpringLayout.EAST, contentPane);
		contentPane.setLayout(sl_contentPane);
		infoLabel.setEnabled(true);
		infoLabel.setEditable(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, infoLabel, 112, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, infoLabel, 19, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, infoLabel, -15, SpringLayout.SOUTH, contentPane);

		JButton btnNewButton = new JButton("Exit");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnNewButton, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnNewButton, 0, SpringLayout.EAST, infoLabel);
		contentPane.add(exitButton);

		JLabel lblNewLabel = new JLabel("Server Logs");
		sl_contentPane.putConstraint(SpringLayout.WEST, currentlyConnected, 0, SpringLayout.WEST, lblNewLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, contentPane);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane(infoLabel);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 62, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblNewLabel, -16, SpringLayout.NORTH, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -548, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		contentPane.add(scrollPane);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Server");
		frame.pack();
		frame.setVisible(true);

		infoLabel.setEditable(false);

		JLabel lblNewLabel_1 = new JLabel("Currently Connected Clients");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -20, SpringLayout.NORTH, lblNewLabel_1);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel_1, 0, SpringLayout.WEST, lblNewLabel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblNewLabel_1, -6, SpringLayout.NORTH, currentlyConnected);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_1.setForeground(Color.RED);
		contentPane.add(lblNewLabel_1);
		currentlyConnected.setEditable(false);
		contentPane.add(currentlyConnected);

		createRadioButton = new JRadioButton("Create");
		buttonGroup.add(createRadioButton);
		sl_contentPane.putConstraint(SpringLayout.NORTH, createRadioButton, 2, SpringLayout.NORTH, scrollPane);
		contentPane.add(createRadioButton);

		moveRadioButton = new JRadioButton("Move");
		buttonGroup.add(moveRadioButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, moveRadioButton, 24, SpringLayout.EAST, scrollPane);
		contentPane.add(moveRadioButton);

		deleteRadioButton = new JRadioButton("Delete");
		buttonGroup.add(deleteRadioButton);
		sl_contentPane.putConstraint(SpringLayout.EAST, createRadioButton, 0, SpringLayout.EAST, deleteRadioButton);
		sl_contentPane.putConstraint(SpringLayout.NORTH, deleteRadioButton, 55, SpringLayout.SOUTH, moveRadioButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, deleteRadioButton, 24, SpringLayout.EAST, scrollPane);
		contentPane.add(deleteRadioButton);

		renameRadioButton = new JRadioButton("Rename");
		buttonGroup.add(renameRadioButton);
		sl_contentPane.putConstraint(SpringLayout.NORTH, renameRadioButton, 33, SpringLayout.SOUTH, deleteRadioButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, renameRadioButton, 24, SpringLayout.EAST, scrollPane);
		contentPane.add(renameRadioButton);

		listRadioButton = new JRadioButton("List Folders");
		buttonGroup.add(listRadioButton);
		sl_contentPane.putConstraint(SpringLayout.NORTH, listRadioButton, 347, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, listRadioButton, 0, SpringLayout.WEST, createRadioButton);
		contentPane.add(listRadioButton);

		ButtonGroup selection = new ButtonGroup();
		selection.add(listRadioButton);
		selection.add(renameRadioButton);
		selection.add(moveRadioButton);
		selection.add(deleteRadioButton);
		selection.add(createRadioButton);

		JLabel lblNewLabel_2 = new JLabel("Enter Folder Name:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel_2, 4, SpringLayout.SOUTH, createRadioButton);
		sl_contentPane.putConstraint(SpringLayout.NORTH, moveRadioButton, 2, SpringLayout.SOUTH, lblNewLabel_2);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel_2, 22, SpringLayout.EAST, scrollPane);
		contentPane.add(lblNewLabel_2);

		JLabel lblEnterSourceFolder = new JLabel("Enter Source Folder Name:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterSourceFolder, 6, SpringLayout.SOUTH, moveRadioButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterSourceFolder, 24, SpringLayout.EAST, scrollPane);
		contentPane.add(lblEnterSourceFolder);

		JLabel lblEnterDestinationFolder = new JLabel("Enter Destination Folder Name:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterDestinationFolder, 24, SpringLayout.EAST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblEnterDestinationFolder, -6, SpringLayout.NORTH,
				deleteRadioButton);
		contentPane.add(lblEnterDestinationFolder);

		JLabel lblEnterFolderName = new JLabel("Enter Folder Name:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterFolderName, 24, SpringLayout.EAST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblEnterFolderName, -6, SpringLayout.NORTH, renameRadioButton);
		contentPane.add(lblEnterFolderName);

		JLabel lblEnterOldName = new JLabel("Enter Old Name Of Folder:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnterOldName, 16, SpringLayout.SOUTH, renameRadioButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterOldName, 0, SpringLayout.WEST, createRadioButton);
		contentPane.add(lblEnterOldName);

		JLabel lblEnterNewName = new JLabel("Enter New Name of Folder:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnterNewName, 0, SpringLayout.WEST, createRadioButton);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblEnterNewName, -17, SpringLayout.NORTH, listRadioButton);
		contentPane.add(lblEnterNewName);

		createTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, createTextField, 20, SpringLayout.EAST, lblNewLabel_2);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, createTextField, -26, SpringLayout.NORTH,
				lblEnterSourceFolder);
		sl_contentPane.putConstraint(SpringLayout.EAST, createTextField, 189, SpringLayout.EAST, lblNewLabel_2);
		contentPane.add(createTextField);
		createTextField.setColumns(10);

		moveSourceTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, moveSourceTextField, 20, SpringLayout.EAST,
				lblEnterSourceFolder);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, moveSourceTextField, 0, SpringLayout.SOUTH,
				lblEnterSourceFolder);
		sl_contentPane.putConstraint(SpringLayout.EAST, moveSourceTextField, 149, SpringLayout.EAST,
				lblEnterSourceFolder);
		moveSourceTextField.setColumns(10);
		contentPane.add(moveSourceTextField);

		moveDestinationTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, moveDestinationTextField, 0, SpringLayout.NORTH,
				lblEnterDestinationFolder);
		sl_contentPane.putConstraint(SpringLayout.WEST, moveDestinationTextField, 173, SpringLayout.EAST, scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, moveDestinationTextField, 0, SpringLayout.EAST,
				createTextField);
		moveDestinationTextField.setColumns(10);
		contentPane.add(moveDestinationTextField);

		deleteTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, deleteTextField, 0, SpringLayout.WEST, createTextField);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, deleteTextField, 0, SpringLayout.SOUTH, lblEnterFolderName);
		sl_contentPane.putConstraint(SpringLayout.EAST, deleteTextField, 0, SpringLayout.EAST, createTextField);
		deleteTextField.setColumns(10);
		contentPane.add(deleteTextField);

		renameOldTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, renameOldTextField, 0, SpringLayout.NORTH, lblEnterOldName);
		sl_contentPane.putConstraint(SpringLayout.WEST, renameOldTextField, 24, SpringLayout.EAST, lblEnterOldName);
		sl_contentPane.putConstraint(SpringLayout.EAST, renameOldTextField, 0, SpringLayout.EAST, createTextField);
		renameOldTextField.setColumns(10);
		contentPane.add(renameOldTextField);

		renameNewTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, renameNewTextField, 0, SpringLayout.NORTH, lblEnterNewName);
		sl_contentPane.putConstraint(SpringLayout.WEST, renameNewTextField, 21, SpringLayout.EAST, lblEnterNewName);
		sl_contentPane.putConstraint(SpringLayout.EAST, renameNewTextField, 0, SpringLayout.EAST, createTextField);
		renameNewTextField.setColumns(10);
		contentPane.add(renameNewTextField);

		performAction = new JButton("Perform Action");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, performAction, -60, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, performAction, -329, SpringLayout.EAST, contentPane);
		contentPane.add(performAction);

		listTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, listTextField, 0, SpringLayout.NORTH, listRadioButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, listTextField, 0, SpringLayout.WEST, createTextField);
		sl_contentPane.putConstraint(SpringLayout.EAST, listTextField, 0, SpringLayout.EAST, createTextField);
		listTextField.setColumns(10);
		contentPane.add(listTextField);

		JButton undoButton = new JButton("Undo");
		sl_contentPane.putConstraint(SpringLayout.WEST, undoButton, 45, SpringLayout.EAST, currentlyConnected);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, undoButton, 0, SpringLayout.SOUTH, currentlyConnected);
		contentPane.add(undoButton);

		undoTextField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, undoTextField, -25, SpringLayout.NORTH, undoButton);
		sl_contentPane.putConstraint(SpringLayout.WEST, undoTextField, 0, SpringLayout.WEST, undoButton);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, undoTextField, -6, SpringLayout.NORTH, undoButton);
		sl_contentPane.putConstraint(SpringLayout.EAST, undoTextField, -5, SpringLayout.EAST, moveRadioButton);
		contentPane.add(undoTextField);
		undoTextField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Undo Operations:");
		lblNewLabel_3.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 20));
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel_3, 0, SpringLayout.WEST, undoButton);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblNewLabel_3, 0, SpringLayout.SOUTH, lblNewLabel_1);
		contentPane.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Copy and paste the log of the \r\n");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel_4, 6, SpringLayout.NORTH, currentlyConnected);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel_4, 0, SpringLayout.WEST, undoButton);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNewLabel_4, 231, SpringLayout.EAST, currentlyConnected);
		contentPane.add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("operation that you wish to undo:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel_5, 5, SpringLayout.SOUTH, lblNewLabel_4);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel_5, 0, SpringLayout.WEST, undoButton);
		contentPane.add(lblNewLabel_5);

//		Adding an action listener for the undo button on the server side. 
		undoButton.addActionListener(new ActionListener() {
			
//			This function gets information from the undoTextField and dissects the pasted log to obtain information necessary to perform the Undo operation. 
//			It uses a switch case to call different functions depending on the contents of the log.
//			Each of the below functions also perform the operation of deleting the log from the server after they have been succesfully rolled back.
			@Override
			public void actionPerformed(ActionEvent e) {
				String pastedLog = undoTextField.getText();
				if (pastedLog != "") {
					String[] log = pastedLog.split(" ");
					switch (log[0]) {
					case "created":
						undoCreate(log);
						break;
					case "renamed":
						undoRename(log);
						break;
					case "moved":
						undoMove(log);
						break;
					default:
						JOptionPane.showMessageDialog(null,
								"Invalid operation selected for Undo. Please select a valid operation.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please copy paste the log which you wish to undo");
				}
			}
			
//			This function is called when a move operation has to be rolled back. It accepts a log array which contains details about the source and dest
//			This information is swapped around to roll back the performed operation. 
			private void undoMove(String[] log) {
				String source = log[3];
				String dest = log[1];
				System.out.println("\nSource:"+".\\ServerDirectory\\" + source +"\\"+dest);
				System.out.println("\n Destination:"+".\\ServerDirectory\\" + dest + "\\");
				try {
					FileUtils.moveDirectoryToDirectory(new File(".\\ServerDirectory\\" + source +"\\"+dest),
							new File(".\\ServerDirectory\\" + dest + "\\"), true);
					infoLabel.append("\n moved " + source + " to " + dest);
					infoLabel.append("Selected Move operation undone");
					for (String userName : userNames) {
						deSyncDirectories(userName);
					}
					for (String userName : userNames) {
						copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
					}
					String labelText = infoLabel.getText();
					String labelText1 = labelText.replace(log[0], "");
					labelText = labelText1.replace(log[1], "");
					labelText1 = labelText.replace(log[2], "");
					labelText = labelText1.replace(log[3], "");
					infoLabel.setText(null);
					infoLabel.append(labelText);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "File name error. Please check file names and try again.",
							"Error", JOptionPane.ERROR_MESSAGE);
					infoLabel.append("\nMove not completed from:" + source + " to " + dest);
				}
			}

//			This function is called when a rename operation has to be rolled back. 
//			It uses the previous name and new name and swaps them around to perform the rollback. 
			private void undoRename(String[] log) {		
				String source = log[3];
				String dest = log[1];
				// createsource File Object using the old name
				File oldName = new File(".\\ServerDirectory\\" + source);
				// create destination File object using the new name sent
				File newName = new File(".\\ServerDirectory\\" + dest);
				boolean isFileRenamed = oldName.renameTo(newName);
				if (isFileRenamed) {
					infoLabel.append("Rename Undo operation succesful");
					for (String userName : userNames) {
						deSyncDirectories(userName);
					}
					for (String userName : userNames) {
						copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
					}
					String labelText = infoLabel.getText();
					String labelText1 = labelText.replace(log[0], "");
					labelText = labelText1.replace(log[1], "");
					labelText1 = labelText.replace(log[2], "");
					labelText = labelText1.replace(log[3], "");
					infoLabel.setText(null);
					infoLabel.append(labelText);
				} else {
					JOptionPane.showMessageDialog(null, "File not renamed. Please check file names and try again.",
							"Error", JOptionPane.ERROR_MESSAGE);
					infoLabel.append("\nThere has been an error renaming the file");
				}
			}

//			This function is called when a create operation has to be rolled back. 
//			The name is used from the server log to delete the particular file from the file system.
			private void undoCreate(String[] log) {
				String path = log[1];
				String filePath = ".\\ServerDirectory\\" + path;
				File file = new File(filePath);
				try {
					FileUtils.deleteDirectory(file);
					infoLabel.append("\nSelected operation has been undone");
					for (String userName : userNames) {
						deSyncDirectories(userName);
					}
					for (String userName : userNames) {
						copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
					}
					String labelText = infoLabel.getText();
					String labelText1 = labelText.replace(log[0], "");
					labelText = labelText1.replace(log[1], "");
					infoLabel.setText(null);
					infoLabel.append(labelText);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});


//		This function is called on click of the Perform Action button on the server side.
//		Based on the radio button which is selected, the respective textfield is accessed to get the relavant folder information
//		and a function is called to perform the specific action. 
		performAction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (createRadioButton.isSelected()) {
					if (createTextField.getText() != "") {
						String name = createTextField.getText();
						try {
							Files.createDirectories(Paths.get(".\\ServerDirectory\\" + name.trim()));
							infoLabel.append("\n created " + name);
							for (String userName : userNames) {
								deSyncDirectories(userName);
							}
							for (String userName : userNames) {
								copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
							}
						} catch (Exception e1) {
							infoLabel.append("\n Server Directory: " + name
									+ " could not be created. File Name error. Try again.");
							JOptionPane.showMessageDialog(null, "File could not be created. Please try again.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Please enter a file name", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (moveRadioButton.isSelected()) {
					String source = moveSourceTextField.getText();
					String dest = moveDestinationTextField.getText();
					try {
						FileUtils.moveDirectoryToDirectory(new File(".\\ServerDirectory\\" + source),
								new File(".\\ServerDirectory\\" + dest + "\\"), true);
						infoLabel.append("\n moved " + source + " to " + dest);
						for (String userName : userNames) {
							deSyncDirectories(userName);
						}
						for (String userName : userNames) {
							copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "File name error. Please check file names and try again.",
								"Error", JOptionPane.ERROR_MESSAGE);
						infoLabel.append("\nMove not completed from:" + source + " to " + dest);
					}
				} else if (deleteRadioButton.isSelected()) {
					String path = deleteTextField.getText();
					String filePath = ".\\ServerDirectory\\" + path;
					File file = new File(filePath);
					try {
						FileUtils.deleteDirectory(file);
						infoLabel.append("\nSERVER DIRECTORY: " + path + " has been deleted.");
						for (String userName : userNames) {
							deSyncDirectories(userName);
						}
						for (String userName : userNames) {
							copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
						}
					} catch (Exception e1) {
						infoLabel.append("\nSERVER DIRECTORY: " + path + " has not been deleted.");
						JOptionPane.showMessageDialog(null, "File not deleted. Please check file name and try again.",
								"Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}

				} else if (renameRadioButton.isSelected()) {
					String source = renameOldTextField.getText();
					String dest = renameNewTextField.getText();
					// createsource File Object using the old name
					File oldName = new File(".\\ServerDirectory\\" + source);
					// create destination File object using the new name sent
					File newName = new File(".\\ServerDirectory\\" + dest);
					boolean isFileRenamed = oldName.renameTo(newName);
					if (isFileRenamed) {
						infoLabel.append("\n renamed " + source + " to " + dest);
						for (String userName : userNames) {
							deSyncDirectories(userName);
						}
						for (String userName : userNames) {
							copyDirectoriesToClient(userName, usersToHomeDirectoryMap.get(userName));
						}
					} else {
						JOptionPane.showMessageDialog(null, "File not renamed. Please check file names and try again.",
								"Error", JOptionPane.ERROR_MESSAGE);
						infoLabel.append("\nThere has been an error renaming the file");
					}
				} else if (listRadioButton.isSelected()) {

					String directoryName = listTextField.getText();
					File f = new File(".\\ServerDirectory\\" + directoryName.trim());
					try {
						message = "";
						showDir(1, f);
						JOptionPane.showMessageDialog(null, message);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				performAction.setEnabled(true);
			}
		});

//		This action listener is used to close the Server by clicking the Exit button
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

//				Citation: https://stackoverflow.com/questions/7764574/close-jframe-from-a-jbutton-process-remain-alive
//				Closing the frame on click of the exit button
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // do nothing
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				frame.setVisible(false);
				System.exit(0);
			}

		});
	}

//	This method is used to de-sync the directories of the user whose name is passed as an arguement
//	The server directories in that user's folder is deleted on click of the De-Sync button
	public static void deSyncDirectories(String username) {
		String filePath = ".\\users\\" + username + "\\home_directory_";
		for (int i = 1; i < 4; i++) {
			File file = new File(filePath + i);
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	This method is used to copy over the directories which the client has selected into the user's specific folder 
//	It calls a helper method to do the copy operation 
	public void copyDirectoriesToClient(String username, String[] data) {
		for (int i = 1; i < data.length; i++) {
			switch (data[i]) {
			case "hd1":
				copyDirectoryToClientHelper(username, "home_directory_1");
				break;
			case "hd2":
				copyDirectoryToClientHelper(username, "home_directory_2");
				break;
			case "hd3":
				copyDirectoryToClientHelper(username, "home_directory_3");
				break;
			}
		}
	}

//	This method is the helper method for copyDirectoriesToClient(). This function takes the arguements as the username and the name of the home directory to be copied over. 
	private void copyDirectoryToClientHelper(String username, String homeDirectoryName) {
		File origin = new File(".\\ServerDirectory\\" + homeDirectoryName);
		File destination = new File(".\\users\\" + username.trim());
		try {
			FileUtils.copyDirectoryToDirectory(origin, destination);
			ServerProg.infoLabel.append("\n Directory changes updated");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	This method is used to recursively show the contents of the folder down to its lowest leaf. 
//	Citation: http://www.java2s.com/Code/Java/File-Input-Output/ReadingandPrintingaDirectoryHierarchy.htm
	
	public static String message = "";
	static void showDir(int indent, File file) throws IOException {
		for (int i = 0; i < indent; i++)
		{
			message+=("-");
//			infoLabel.append("-");
		}
		message+=(file.getName() + "\n");
//		infoLabel.append(file.getName() + "\n");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++)
				showDir(indent + 4, files[i]);
		}
	}
}
