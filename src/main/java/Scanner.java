import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Scanner {
    private final static List<String> COMMANDS = Arrays.asList("done");

    static void scan() throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader((System.in)));
        TaskList userTaskList = new TaskList();

        scanLoop:
        while(true) {
            String line = reader.readLine();
            String command = line.split(" ")[0];

            if (COMMANDS.contains(command) && line.split(" ").length > 1) {
                switch (command) {
                    case "done":
                        try {
                            userTaskList.markAsDone(
                                    Integer.parseInt(line.split( " ")[1])
                            );
                        } catch (IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                        }
                        break;
                    default:
                        break;
                }
            } else {
                switch(line) {
                    case "bye":
                        System.out.println("Bye. Hope to see you again");
                        break scanLoop;
                    case "list":
                        userTaskList.printList();
                        break;
                    default:
                        userTaskList.addItem(line);
                        System.out.println("added: " + line);
                        break;
                }
            }
        }
    }
}
