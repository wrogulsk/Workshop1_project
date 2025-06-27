package pl.coderslab;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(ConsoleColors.BLUE + "Please select an option");
            System.out.print(ConsoleColors.RESET);
            System.out.println("add");
            System.out.println("remove");
            System.out.println("list");
            System.out.println("exit");

            String choice = scanner.nextLine();
            switch (choice) {
                case "add":
                    addTask();
                    break;
                case "remove":
                    removeTask();
                    break;
                case "list":
                    listTask();
                    break;
                case "exit":
                    exitTask();
                    return;
                default:
                    System.out.println("Please select a correct option");
                    System.out.println();
                    break;
            }
        }
    }

    public static void addTask() {

        String[][] tasksArray = fileToArray("tasks.csv");
        String[][] updatedTasksArray = new String[tasksArray.length + 1][4];
        int index = tasksArray.length;
        Scanner scanner = new Scanner(System.in);

        String task = "";
        do {
            System.out.println("Please add task description:");
            task = scanner.nextLine().trim();
            if (task.isEmpty()) {
                System.out.println("Task description cannot be empty. Please try again.");
            }
        } while (task.isEmpty());

        String input;
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (date == null) {
            System.out.println("Please add task due date in format yyyy-MM-dd");
            input = scanner.nextLine().trim();

            if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("Invalid date format. Please use format yyyy-MM-dd.");
                continue;
            }

            try {
                date = LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use format yyyy-MM-dd.");
            }
        }

        boolean valid = false;
        String important = "";
        while (!valid) {
            System.out.println("Is your task important: true / false");
            important = scanner.nextLine().trim().toLowerCase();
            if (important.equalsIgnoreCase("true") || important.equalsIgnoreCase("false")) {
                valid = true;
            } else {
                System.out.println("Invalid value. Please enter 'true' or 'false'.");
            }
        }

        for (int i = 0; i < tasksArray.length; i++) {
            for (int j = 0; j < tasksArray[i].length; j++) {
                updatedTasksArray[i][j] = tasksArray[i][j];
            }
        }
        updatedTasksArray[tasksArray.length][0] = String.valueOf(index);
        updatedTasksArray[tasksArray.length][1] = task;
        updatedTasksArray[tasksArray.length][2] = date.toString();
        updatedTasksArray[tasksArray.length][3] = important;

        updatingFile(updatedTasksArray);
        System.out.println("Task successfully added!");
        System.out.println();
    }

    public static void removeTask() {
        String[][] tasksArray = fileToArray("tasks.csv");

        if (ArrayUtils.isEmpty(tasksArray)) {
            System.out.println("No tasks to remove.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please select number to remove:");
        int index = -1;
        while (true) {
            if (scanner.hasNextLine()) {
                index = scanner.nextInt();
                scanner.nextLine();
                if (index >= 0 && index < tasksArray.length) {
                    break;
                } else {
                    System.out.println("Invalid index. Please enter a number between 0 and " + (tasksArray.length - 1));
                }
            } else {
                System.out.println("Invalid index. Please try again.");
                scanner.nextLine();
            }
        }

        String[][] updatedTasksArray = ArrayUtils.remove(tasksArray, index);

        updatingFile(updatedTasksArray);
        System.out.println("Task was successfully removed.");
        System.out.println();
    }

    public static void listTask() {

        String[][] tasksArray = fileToArray("tasks.csv");
        System.out.println("List");
        for (String[] task : tasksArray) {
            System.out.println(task[0] + ": " + task[1] + " " + task[2] + " " + task[3]);
        }
        System.out.println();
    }

    public static void exitTask() {
        System.out.println(ConsoleColors.RED + "Bye, bye");
        System.out.print(ConsoleColors.RESET);
    }

    public static String[][] fileToArray(String fileName) {

        String[][] tasksArray = new String[0][4];
        int index = 0;
        try (Scanner scanner = new Scanner(new File(fileName));) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s*,\\s*");
                String[][] newTasksArray = new String[tasksArray.length + 1][4];

                for (int i = 0; i < tasksArray.length; i++) {
                    newTasksArray[i] = Arrays.copyOf(tasksArray[i], tasksArray[i].length);
                }

                newTasksArray[tasksArray.length][0] = String.valueOf(index);
                newTasksArray[tasksArray.length][1] = parts[0];
                newTasksArray[tasksArray.length][2] = parts[1];
                newTasksArray[tasksArray.length][3] = parts[2];

                tasksArray = newTasksArray;
                index++;
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tasksArray;
    }

    public static void updatingFile(String[][] array) {
        try (PrintWriter printWriter = new PrintWriter("tasks.csv")) {
            for (String[] el : array) {
                printWriter.println(el[1] + ", " + el[2] + ", " + el[3]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}