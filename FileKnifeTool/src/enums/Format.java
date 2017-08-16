package enums;

public enum Format {
        STAT("stat"),
        BLOCK("block"),
        TABLE("table");

        private String label;

        Format(String label) {
            this.label = label;
        }

        public String toString() {
            return label;
        }

    
}
