# Multi_Threaded_ClientServer
1. Open Eclipse and go to File->Open Projects from File System. Navigate to the directory where my project is present and click on the root folder.  
2. You will have to run ServerProg.java which is located inside the src folder 
3. Now you can run the ClientProg.java which is also inside the src folder.  
4. ClientProg.java has a textfield at the top of the newly opened window that will have a label “Enter Name”. Enter a valid username in the textfield and click on the “Submit Name” button.  
5. There will be a message displayed on the server’s “Server Log” which says that the client has either connected or not connected after verifying the client’s username. If you did not enter a valid username, you can use the “Enable button” that is next to the Submit Name button to enable the Submit button to try again with a valid name  
6. After the Client connects successfully, a message is displayed on both client and server’s logs showing the client has connected. The Currently connected client list is also updated.   
7. After this, the client can enter a name in the Folder name textfield and click on “Perform Action” to create a new Folder. The server logs will give the information about this action being successful or not. 
8. Similarly, the client can enter a folder name in the Path name textfield and the destination Path textfield and click on “Perform Action” to move the folder. The server logs will give the information about this action being successful or not. 
9. After this, the client can enter a folder name in the Folder name textfield and click on “Perform Action” to delete the Folder. The user can also enter a file path is the folder that is to be deleted is inside another folder. The server logs will give the information about this action being successful or not. 
10. After this, the client can enter a Folder name in the folder current name textfield and “Enter the name of the new folder textfield” and and click on “Perform Action” to rename the old folder into the new folder. The server logs will give the information about this action being successful or not.
11. If the client selects the List Contents and clicks on the List Contents of the directory, a list of items is shown. 
12. The Client can click on the “Exit” button at any time when he wants to close the program. This also indicates to the server that the client has left, and disconnection message is displayed in the logs. 
13. The same operations can be performed on the server-side as well namely, create, delete, move, rename and list contents. For each of the operation, either the file path or the folder name has to provided in the textbox beside it. 
14. The files which are updated on the server are always reflected on the client’s copied directories as well.
15. The Sync button has to be pressed after checking the checkboxes for the required directories. When this button is pressed, the folders from the server are copied over to the user’s home folder. 
16. The De-Sync button deletes all the folders which were synced from the server’s folder and de-sync’s the user’s folder with the server’s folders. To Sync again, the user can again click the Sync button to Sync folders with the server. The results of the Sync and Desync can be viewed by checking the contents of the folders by selecting the List Radio button and clicking on Perform Action button after which a window will pop up showing the contents of the Folders inside that user’s local directory.
17. The server program allows for rolling back of performed operations by the use of the Server’s log and the undo button provided. The user has to copy the specific log file from the server’s log which they intend to undo, and paste the same into the provided text field without any spaces before or after the log data and click on the Undo button. 
18. The server then, rolls back the operation and deletes the specific log message from the server’s log. I have also implemented some error catching mechanisms to prevent the crashing of the application in-case of any exceptions during any of the rollback operations, which will display error messages on the console as well to notify the user.  

NOTE: If there are any compilation errors, please include the recommended files in the build path. The files are present in the zip folder. Lab 3 NOTE: Please copy and paste the log files into the given textfield without any spaces before or after the log. 
