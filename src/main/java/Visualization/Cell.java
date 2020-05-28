package Visualization;

import Enums.State;

import java.util.ArrayList;

public class Cell {
    public ArrayList<State> states = new ArrayList<>();
    public ArrayList<Integer> aminoIndexes = new ArrayList<>();

    public Cell() {
        states.add(State.Empty);
    }

    public void addState (State state) {
        if (states.get(0).equals(State.Empty)) {
            states.clear();
        }
        states.add(state);
    }

    public void addAminoIndex (int aminoIndex) {
        aminoIndexes.add(aminoIndex);
    }

    public State getRelevantDrawState () {
        State rc = states.get(0);
        if (states.size() > 1) {
            if (rc.equals(State.Hydrophobic)) {
                for (State s : states) {
                    if (s.equals(State.Hydrophilic)) {
                        return State.Mixed;
                    }
                }
                return State.HydrophobicMulti;

            } else if (rc.equals(State.Hydrophilic)) {
                for (State s : states) {
                    if (s.equals(State.Hydrophobic)) {
                        return State.Mixed;
                    }
                }
                return State.HydrophilicMulti;
            }
        }
        return rc;
    }
}
