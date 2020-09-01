package com.siawsam.duke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * A parser for command-line user inputs.
 */
public class Parser {
    /**
     * An array of valid Duke commands to edit tasks.
     */
    private static final List<String> COMMANDS = Arrays.asList("done", "delete", "find");
    private final TaskList userTaskList;
    private final Storage storage;
    
    /**
     * Constructs a Parser when no saved task list exists.
     *
     * @param storage A Storage instance to use when the parser needs to save to disk.
     */
    public Parser(Storage storage) {
        this.userTaskList = new TaskList();
        this.storage = storage;
    }
    
    /**
     * Constructs a Parser when a saved task list exists.
     *
     * @param storage      A Storage instance to use when the parser needs to save to disk.
     * @param userTaskList A TaskList instance that represents the existing save.
     */
    Parser(Storage storage, TaskList userTaskList) {
        this.userTaskList = userTaskList;
        this.storage = storage;
    }
    
    /**
     * Parses user input strings from the command line.
     *
     * @throws IOException if an IO exception occurs when trying to read standard input.
     */
    public void scan() throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader((System.in)));
        boolean willContinue = true;
        
        while (willContinue) {
            String line = reader.readLine();
            willContinue = parse(line);
        }
    }
    
    public boolean parse(String literal) {
        String command = literal.split(" ")[0];
    
        if (COMMANDS.contains(command) && literal.split(" ").length > 1) {
            // handle commands with >1 argument
            switch (command) {
            case "done":
                try {
                    userTaskList.markAsDone(
                            Integer.parseInt(literal.split(" ")[1])
                    );
                } catch (IllegalArgumentException ex) {
                    Ui.showErrorMessage(ex);
                }
                break;
            case "delete":
                try {
                    userTaskList.removeItem(
                            Integer.parseInt(literal.split(" ")[1])
                    );
                } catch (IllegalArgumentException ex) {
                    Ui.showErrorMessage(ex);
                }
                break;
            case "find":
                TaskSearcher searcher = new TaskSearcher(userTaskList);
                searcher.searchAndDisplay(literal.split("find")[1].trim());
                break;
            default:
                break;
            }
        } else {
            switch (literal) {
            case "bye":
                Ui.showGoodbyeMessage();
                return false;
            case "list":
                userTaskList.printList();
                break;
            case "save":
                storage.save(userTaskList);
                break;
            default:
                try {
                    Task addedTask = userTaskList.addItem(literal);
                    Ui.showSuccessfulAdd(addedTask);
                } catch (DukeException ex) {
                    Ui.showErrorMessage(ex);
                }
                break;
            }
        }
        return true;
    }
}
