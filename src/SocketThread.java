//Name: Nischith Javagal Panish
//UTA ID: 1001780908

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

//import com.sun.security.ntlm.Client;

public class SocketThread extends Thread {

	static String[] userDirectoriesInfo= new String[200];
	private Socket clientSocket;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	JOptionPane jOptionPane;
	public  String username;

	public SocketThread(Socket clientSocket, DataInputStream dataInputStream, DataOutputStream dataOutputStream,
			String username) {
		this.clientSocket = clientSocket;
		this.dataOutputStream = dataOutputStream;
		this.dataInputStream = dataInputStream;
		this.username = username;
	}

	@Override
	public void run() {
// After creating the new thread for each client, I send data from the client using the dataoutPutStream.writeUTF(). This data is the form of <Operation> <Data1> <Data2>
//		After getting the data here in the server, I split the string that I received into an array using String.split() method.
//		This array is then used in a switch case to send the data to the respective function depending on the <Operation> that I have sent.

		System.out.println("New Thread created for client");
		try {
			while (true) {
				String input = dataInputStream.readUTF();
				String[] data = input.split(" ");

				switch (data[0]) {
				case "create":
					createFile(data[1]); // DONE
					break;
				case "delete":
					deleteFile(data[1]); // DONE
					break;
				case "rename":
					renameFolder(data[1], data[2]); // DONE
					break;
				case "move":
					moveFolder(data[1], data[2]); // DONE
					break;
				case "list":
					listFolderContents(); // DONE
					break;
				case "server": // DONE
					copyDirectoriesToClient(data);
					break;
				case "desync":  //DONE
					deSyncDirectories(username);
					JOptionPane.showMessageDialog(ClientProg.contentPane, "Desync succesful", "Success", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
		} catch (IOException e) {
			ServerProg.currentUsers.remove(username);
			ServerProg.currentlyConnected.setText("");
			for (String user : ServerProg.currentUsers) {
				ServerProg.currentlyConnected.append("\n" + user);
			}
			ServerProg.infoLabel.append("\n" + username + " has diconnected");
			e.printStackTrace();
		}
	}

//	This method is used to de-sync the directories of the user whose name is passed as an arguement
//	The server directories in that user's folder is deleted on click of the De-Sync button
	public  static void deSyncDirectories(String username) {
		String filePath = ".\\users\\" + username + "\\home_directory_";
		for (int i = 1; i < 4; i++) {
			File file = new File(filePath+i);
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	
//	This method is used to copy over the directories which the client has selected into the user's specific folder 
//	It calls a helper method to do the copy operation 
	public void copyDirectoriesToClient(String[] data) {
		ServerProg.usersToHomeDirectoryMap.put(username, data);
		for (int i = 0; i < data.length; i++) {
			switch(data[i]) {
			case "hd1": 
				copyDirectoryToClientHelper("home_directory_1");
				break;
			case "hd2":
				copyDirectoryToClientHelper("home_directory_2");
				break;
			case "hd3":
				copyDirectoryToClientHelper("home_directory_3");
				break;
			}
		}
	}
//	This method is the helper method for copyDirectoriesToClient(). This function takes the arguements as the username
//	and the name of the home directory to be copied over. 
	private void copyDirectoryToClientHelper(String homeDirectoryName) {
		File origin = new File(".\\ServerDirectory\\"+homeDirectoryName);
		File destination = new File(".\\users\\" + username.trim());
		try {
			FileUtils.copyDirectoryToDirectory(origin,destination);
			ServerProg.infoLabel.append("\n Directory:"+homeDirectoryName+" copied to "+ username);
			ClientProg.clientInfoLabel.append("\n Directory:"+homeDirectoryName+" copied to "+ username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	Display file and folder names inside the current working directory.  Makes a call to the recursive function showDir() 
//	which recursively traverses each directory and appends the folder names to a string which is later displayed to the user using a JOptionPane Message Dialog Box
	private void listFolderContents() {
		File f = new File(".\\users\\" + username.trim());
		String[] pathnames = f.list();
		ServerProg.infoLabel.append("\n The contents of the folder are:\n ");
		ClientProg.clientInfoLabel.append("\n The contents of the folder are: ");
		message = "";
		try {
			showDir(0, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, message);
	}
	
//	Citation: http://www.java2s.com/Code/Java/File-Input-Output/ReadingandPrintingaDirectoryHierarchy.htm
//	Recursively traverses the given File object and appens the result to the message variable which
//	is later shown as a JOptionPane to the client in the parent method. 
	static String message = "Contents of the folder are:";

	static void showDir(int indent, File file) throws IOException {
		for (int i = 0; i < indent; i++) {
			ServerProg.infoLabel.append("-");
			message+="-";
		}
		ServerProg.infoLabel.append(file.getName()+"\n");
		message+=(file.getName()+"\n");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++)
				showDir(indent + 4, files[i]);
		}
	}

	// Citation: https://commons.apache.org/proper/commons-io/
	// Uses the Apache Commons's moveDirectoryToDirectory() to move the source
	// directory into the destination directory
	private void moveFolder(String source, String dest) throws IOException {
		try {
			FileUtils.moveDirectoryToDirectory(new File(".\\users\\" + username + "\\" + source),
					new File(".\\users\\" + username + "\\" + dest + "\\"), true);
			ServerProg.infoLabel.append("\nMove completed from:" + source + " to " + dest);
		} catch (Exception e) {
			ServerProg.infoLabel.append("\nMove not completed from:" + source + " to " + dest);
			e.printStackTrace();
		}

	}

	private void renameFolder(String original, String newName) {
		// create source File object using the original file path sent
		File oldName = new File(".\\users\\" + username + "\\" + original);

		// create destination File object using the new name sent
		File newName1 = new File(".\\users\\" + username + "\\" + newName);
		boolean isFileRenamed = oldName.renameTo(newName1);

		if (isFileRenamed) {
			ServerProg.infoLabel.append("\nFile has been renamed to: " + newName1);

			System.out.println("Successfully renamed");
		} else
			ServerProg.infoLabel.append("\nThere has been an error renaming the file");

	}

//	 Citation: https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/FileUtils.html#deleteDirectory(java.io.File)
//	Here I try to get the file path and then delete the directory using FileUtils's deleteDirectory() method
	private void deleteFile(String name) {
		String filePath = ".\\users\\" + username + "\\" + name;
		File file = new File(filePath);
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServerProg.infoLabel.append("\n" + name + " has been deleted.");
	}

//In this method, I create a new File and using the createDirectories() method, I can create the new folder at the specified location :"name"
	private void createFile(String name) {
		try {
			if(!name.contains("home_directory")) {
				Files.createDirectories(Paths.get(".\\users\\" + username.trim() + "\\" + name));
				ServerProg.infoLabel.append("\n File: " + name + " has been created.");
				dataOutputStream.writeUTF("created");;
			}
			else {
				dataOutputStream.writeUTF("invalid directory");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
