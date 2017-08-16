package enums;

import cmdline.Command;
import cmdline.CommandDelete;
import cmdline.CommandParse;
import cmdline.CommandParseFileWithSeparators;
import cmdline.CommandPrint;

public enum Commands {
    DELETE("delete", new CommandDelete()),
    PRINT("print", new CommandPrint()),
	GENESYS("genesys", new CommandParse()),
	LMS("lms", new CommandParseFileWithSeparators());

    private String label;
    private Command cmd;
    
    Commands(String label, Command cmd) {
        this.label = label;
        this.cmd=cmd;
        
    }

    public String toString() {
        return label;
    }
    
    public Command getCmd() {
        return cmd;
    }
}
