package edu.willamette.cs1.wordle;
import java.io.PrintWriter;

import java.io.FileOutputStream;

import java.io.FileNotFoundException;

public class WriteToFile {

   public static void main(String[] args){

    for (String arg : args) {
        updateFile(arg);

    }

    }

 

/** Appends text to a file named output.txt

        If the file doesn't exist, it creates one for you */

 

public static void updateFile(String text)

{ 

   PrintWriter outputStream;

       try {

              outputStream = new PrintWriter(new FileOutputStream("output.txt", true));

              outputStream.println(text);

              outputStream.close();

         } catch (FileNotFoundException e) {

                 System.out.println("File not found");

                 System.exit(0); }

          }

}
