public class lolcats {
    public static void main(String[] argv) {
        if ( argv.length > 0 ) {
            for ( int i = 0; i < argv.length; i++ ) {
                System.out.println(argv[i]);
            }
        }

        if ( argv.length == 0 ) {
            System.out.println("BLAH");
        }
    }
}
