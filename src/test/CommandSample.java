package test;

public class CommandSample{

//Interface
interface Command { String execute(String s); }

//Command to reuse - must be static
static class commandUpper implements Command {
   public String execute(String s) {
      return  s.toUpperCase();
}  }

//Function using this command
static void myPrinter( Command c,String s ) {
   String os = c.execute(s);
   System.out.println( os );
}

public static void main( String[] args ) {
	myPrinter( new commandUpper(),"this");
}}