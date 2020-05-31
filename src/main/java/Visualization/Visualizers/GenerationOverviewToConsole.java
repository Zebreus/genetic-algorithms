package Visualization.Visualizers;

import Evaluators.EvaluatorNESW;
import Interfaces.Visualizer;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.GeneticAlgorithm;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerationOverviewToConsole implements Visualizer {

  final int[] isHydrophobic;
  Config config;

  EvaluatorNESW bondsOverlapsEvaluator;

  public GenerationOverviewToConsole(int[] isHydrophobic, Config config) {
    this.isHydrophobic = isHydrophobic;
    this.config = config;
    bondsOverlapsEvaluator = new EvaluatorNESW(1,isHydrophobic);
  }

  @Override
  public void drawProtein(Candidate[] generation, GeneticAlgorithm geneticAlgorithm) {
    //TODO This should be done in the new Generation class
    Candidate bestCandidateOfGeneration = generation[0];
    for(Candidate candidate: generation){
      if(candidate.getFitness() > bestCandidateOfGeneration.getFitness()){
        bestCandidateOfGeneration = candidate;
      }
    }

    int bonds = bondsOverlapsEvaluator.evaluateBonds(bestCandidateOfGeneration);
    int overlaps = bondsOverlapsEvaluator.evaluateBonds(bestCandidateOfGeneration);

    System.out.println("Generation " + geneticAlgorithm.generation + ":");
    System.out.println("The fitness is: "
        + bestCandidateOfGeneration.getFitness()
        + " [hydrophobicBonds = " + bonds + " | overlaps = " + overlaps + "]");
  }
}
