package MainClasses;

public class Main {

    public static void main(String[] args) {

        String propertyPath = "./src/main/resources/genetic.properties";
        Config config = new Config(propertyPath);

        int[] protein = Examples.convertStringToIntArray(Examples.SEQ50);
        GeneticAlgorithm ga = new GeneticAlgorithm(protein, config);
        ga.simulateGenerations();

    }
}
