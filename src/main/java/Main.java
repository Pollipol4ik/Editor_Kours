import gui.MainJFrame;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 1) {
            MainJFrame.getFrame(args[0]);
        } else {
            MainJFrame.getFrame();
        }
    }
}
