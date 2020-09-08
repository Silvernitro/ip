package com.siawsam.duke;

import java.util.Arrays;
import java.util.List;

/**
 * A parser for user inputs.
 */
public class Parser {
    /**
     * An array of valid parameterized Duke commands.
     */
    private static final List<String> PARAMETERIZED_COMMANDS = Arrays.asList("done", "delete", "find", "tag");
    private final TaskList userTaskList;
    private final TagList tagList = new TagList();
    private final Storage storage;
    
    /**
     * Constructs a Parser when no saved task list exists.
     *
     * @param storage A Storage instance to use when the parser needs to save to disk.
     */
    public Parser(Storage storage) {
        userTaskList = new TaskList();
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
     * Parses a string literal and executes the associated operation.
     *
     * @param literal The string literal to parse and execute.
     * @return The {@link Response response} of the executed operation.
     */
    public Response parseAndExecute(String literal) {
        String command = literal.split(" ")[0];
        boolean isValidCommand = PARAMETERIZED_COMMANDS.contains(command);
        boolean hasArgument = literal.split(" ").length > 1;
    
        if (isValidCommand && hasArgument) {
            // handle commands with >1 argument
            return parseParameterizedCommand(command, literal);
        } else {
            switch (literal) {
            case "bye":
                return Response.terminatingResponse(Ui.showGoodbyeMessage());
            case "list":
                return new Response(Ui.printList(userTaskList));
            case "tags":
                return new Response(Ui.printTags(tagList));
            case "save":
                return storage.save(userTaskList);
            default:
                return parseAddCommand(literal);
            }
        }
    }
    
    private Response parseParameterizedCommand(String command, String literal) {
        assert command.length() > 0 : "empty command string";
        
        switch (command) {
        case "done":
            return parseDoneCommand(literal);
        case "delete":
            return parseDeleteCommand(literal);
        case "find":
            return parseFindCommand(literal);
        case "tag":
            return parseTagCommand(literal);
        default:
            assert !PARAMETERIZED_COMMANDS.contains(command) : "invalid parameterized command";
            return Response.emptyResponse();
        }
    }
    
    private Response parseDoneCommand(String literal) {
        assert literal.length() > 0 : "empty command literal";
        
        try {
            return userTaskList.markAsDone(
                    Integer.parseInt(literal.split(" ")[1])
            );
        } catch (IllegalArgumentException ex) {
            return new Response(Ui.showErrorMessage(ex));
        }
    }
    
    private Response parseDeleteCommand(String literal) {
        assert literal.length() > 0 : "empty command literal";
        
        try {
            return userTaskList.removeItem(
                    Integer.parseInt(literal.split(" ")[1])
            );
        } catch (IllegalArgumentException ex) {
            return new Response(Ui.showErrorMessage(ex));
        }
    }
    
    private Response parseFindCommand(String literal) {
        assert literal.length() > 0 : "empty command literal";
        
        TaskSearcher searcher = new TaskSearcher(userTaskList);
        return searcher.searchAndDisplay(literal.split("find")[1].trim());
    }
    
    private Response parseAddCommand(String literal) {
        assert literal.length() > 0 : "empty command literal";
        
        try {
            Task addedTask = userTaskList.addItem(literal);
            return new Response(Ui.showSuccessfulAdd(addedTask));
        } catch (DukeException ex) {
            return new Response(Ui.showErrorMessage(ex));
        }
    }
    
    private Response parseTagCommand(String literal) {
        String parameters = literal.split("tag")[1].trim();
        int itemIndex = Integer.parseInt(parameters.split(" ")[0]);
        String tagName = parameters.split(Integer.toString(itemIndex))[1].trim();
        
        return userTaskList.tagItem(itemIndex, tagName, tagList);
    }
}
