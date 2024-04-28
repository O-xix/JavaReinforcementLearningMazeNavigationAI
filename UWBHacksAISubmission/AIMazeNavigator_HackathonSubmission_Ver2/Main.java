import java.awt.*;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import javax.imageio.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int choice = JOptionPane.showConfirmDialog(null, "Would you like to make a new Maze?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("");
            Maze maze = new Maze();
        } else {
            JOptionPane.showMessageDialog(null, "Thank you!", "Exiting program...", JOptionPane.INFORMATION_MESSAGE);
        } 
    }
}