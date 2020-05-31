package Visualization.Visualizers;

import Evaluators.EvaluatorNESW;
import Interfaces.Visualizer;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.GeneticAlgorithm;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GenerationProgressToLog implements Visualizer {
  Path logfilePath;
  final int[] isHydrophobic;
  Config config;

  //TODO Make sure this is initialized or prepare for bugs
  Candidate overallBestCandidate;
  EvaluatorNESW bondsOverlapsEvaluator;

  public GenerationProgressToLog(int[] isHydrophobic, Config config) {
    this.isHydrophobic = isHydrophobic;
    this.config = config;
    String filename = config.getLogfileDirectory() + "/" + config.getJobName() + ".txt";
    logfilePath = Paths.get(filename);
    bondsOverlapsEvaluator = new EvaluatorNESW(1,isHydrophobic);

    //Initialize the logfile
    initializeLogfile();
  }

  @Override
  public void drawProtein(Candidate[] generation, GeneticAlgorithm geneticAlgorithm) {
    //TODO This should be done in the new Generation class
    double averageFitness = 0.0;
    Candidate bestCandidateOfGeneration = generation[0];
    for(Candidate candidate: generation){
      averageFitness += candidate.getFitness();
      if(candidate.getFitness() > bestCandidateOfGeneration.getFitness()){
        bestCandidateOfGeneration = candidate;
      }
    }
    averageFitness /= generation.length;

    //Find overall best Candidate
    if(geneticAlgorithm.generation == 0) {
      overallBestCandidate = bestCandidateOfGeneration;
    }else if(bestCandidateOfGeneration.getFitness() > overallBestCandidate.getFitness()){
      overallBestCandidate = bestCandidateOfGeneration;
    }

    int bonds = bondsOverlapsEvaluator.evaluateBonds(bestCandidateOfGeneration);
    int overlaps = bondsOverlapsEvaluator.evaluateBonds(bestCandidateOfGeneration);

    String log = String.format("%d\t%.4f\t%.4f\t%.4f\t %d\t%d\n",
        geneticAlgorithm.generation,
        averageFitness,
        bestCandidateOfGeneration.getFitness(),
        overallBestCandidate.getFitness(),
        bonds,
        overlaps);

    try {
      String logfilePath = config.getLogfileDirectory() + "/" + config.getJobName() + ".txt";
      Files.write(Paths.get(logfilePath), log.getBytes(), StandardOpenOption.APPEND);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initializeLogfile() {
    String content = "Generation\tAverage Fitness\tBest Fitness\tOverall Best Fitness\tBonds\tOverlaps\n";
    try {
      Files.createDirectories(Paths.get(config.getLogfileDirectory()));
      Files.write(logfilePath, content.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}