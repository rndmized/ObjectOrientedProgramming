## Object Oriented Programming Project 2016

This repository contains code and information for a third-year undergraduate project for the module **Object Oriented Programming**.
The module is taught to undergraduate students at [GMIT](http://www.gmit.ie) in the Department of Computer Science and Applied Physics.
The lecturer is John Healy.

### Project Overview
I have created a client + multi-threaded file server that allow multiple users connect to the server, retrieve a list of files to download, and download a file from such list. The server stores all requests in a log file.

The project was guided by the following excerpt from the project instructions:
>You are required to implement a multi-threaded file server and logging application that allows a  client  application  to  download  files using a  set  of  options  presented  in  a  terminal  user interface

### How to run the application
The client application should present a command-line user interface whenstarted from a terminal window as follows

```bash
$ java -cp .:./oop.jar ie.gmit.sw.ClientRunner
```

The server application should be started with the following command and be packaged in the same JAR achive (oop.jar) as the client:

```bash
$ java -cp .:./oop.jar ie.gmit.sw.Server 7777 /path/to/myfiles
```


### How to use the application
#### Client Side
The following options will be offered to a user:

1. Connect to Server
2. Print File Listing
3. Download File
4. Quit
Type Option [1-4]>
Typing 1: The client will establish a connection with the server, causing the server to spawn a thread for it (for every client).
Typing 2: A list of available files to download will be sent from server and printed to screen.
typing 3: A name file will prompt. The typed filename will be retreived from server (if such file exists) and downloaded in the download directory.
Typing 4: The client will close the connection with the server if exists and will terminate the program.

