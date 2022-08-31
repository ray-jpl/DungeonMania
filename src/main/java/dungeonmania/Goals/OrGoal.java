package dungeonmania.Goals;

import java.util.List;

import com.google.gson.JsonObject;

import dungeonmania.GameMap;

import java.util.ArrayList;

/*
 * Defines the class which processes subgoals in which only one has to be complete 
 * to mark the entire composite goal as complete
 */
public class OrGoal extends Goals {

    private List<Goals> subgoals;
    private GameMap map;

    public OrGoal(GameMap map, JsonObject one, JsonObject two) {
        subgoals = new ArrayList<>();
        this.map = map;
        addGoal(one);
        addGoal(two);
    }

    /*
     * Function calls the respective goal function based on input given in the map
     */
    public void addGoal(JsonObject goal) {
        Goals goalObject;
        if (goal.get("goal").getAsString().equals("AND")) {
            goalObject = new AndGoal(this.map, goal.getAsJsonArray("subgoals").get(0).getAsJsonObject(),
                    goal.getAsJsonArray("subgoals").get(1).getAsJsonObject());
        } else if (goal.get("goal").getAsString().equals("OR")) {
            goalObject = new OrGoal(this.map, goal.getAsJsonArray("subgoals").get(0).getAsJsonObject(),
                    goal.getAsJsonArray("subgoals").get(1).getAsJsonObject());
        } else if (goal.get("goal").getAsString().equals("enemies")) {
            goalObject = new EnemyGoal(this.map);
        } else if (goal.get("goal").getAsString().equals("boulders")) {
            goalObject = new BouldersGoal(this.map);
        } else if (goal.get("goal").getAsString().equals("treasure")) {
            goalObject = new TreasureGoal(this.map);
        } else if (goal.get("goal").getAsString().equals("exit")) {
            goalObject = new ExitGoal(this.map);
        } else {
            return;
        }

        subgoals.add(goalObject);
    }
    @Override
    /*
     * @return true if one of the goal is complete and false if none are.
     */
    public boolean isComplete() {
        for (Goals goal : subgoals) {
            if (goal.isComplete()) {
                return true;
            }
        }
        return false;
    }

    @Override
    /*
     * formats string to (:goal OR :goal) if not complete
     */
    public String formatString() {

        // Loop through and conjoin all the goals together
        String goalCondition = "(";
        if (subgoals.get(0).isComplete() || subgoals.get(1).isComplete()) {
            return "";
        } else {
            goalCondition += subgoals.get(0).formatString();
            goalCondition += " OR ";
            goalCondition += subgoals.get(1).formatString();
        }

        goalCondition += ")";
        return goalCondition;
    }
}
