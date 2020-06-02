package Visualizers;

import Evaluators.EvaluatorNESW;
import Interfaces.Visualizer;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.GeneticAlgorithm;

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
    int overlaps = bondsOverlapsEvaluator.evaluateOverlaps(bestCandidateOfGeneration);

    System.out.println("Generation " + geneticAlgorithm.generation + "/" + config.getTotalGenerations() + ":");
    System.out.println("Population size: " + generation.length);
    System.out.println("The fitness is: "
        + bestCandidateOfGeneration.getFitness()
        + " [hydrophobicBonds = " + bonds + " | overlaps = " + overlaps + "]");
  }
}
