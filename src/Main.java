public class Main {
    public static void main(String[] args) {
        Parser parse = new Parser("79-((5^4)/(5+b)+8+a+4)");
        System.out.println(parse.compute());
    }
}