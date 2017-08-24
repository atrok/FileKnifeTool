package enums;

import cmdline.Command;
import cmdline.CommandDelete;
import cmdline.CommandParse;
import cmdline.CommandParseFileWithSeparators;
import cmdline.CommandPrint;

public enum Commands {
    delete("delete", new CommandDelete()),
    print("print", new CommandPrint()),
	genesys("genesys", new CommandParse()),
	lms("lms", new CommandParseFileWithSeparators());

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
