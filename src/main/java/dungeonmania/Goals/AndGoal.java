package dungeonmania.Goals;

import java.util.List;

import com.google.gson.JsonObject;

import dungeonmania.GameMap;

import java.util.ArrayList;

/*
 * Defines the class which processes subgoals where both must be completed to changed status of goal condition to complete
 */
public class AndGoal extends Goals {

    private List<Goals> subgoals;
    private GameMap map;

    public AndGoal(GameMap map, JsonObject one, JsonObject two) {
        super();
        subgoals = new ArrayList<>();
        this.map = map;
        addGoal(one);
        addGoal(two);

        // Allow (exit)goal to know state of other goal
        if (subgoals.get(0) instanceof ExitGoal) {
            ((ExitGoal)subgoals.get(0)).setSiblingGoal(subgoals.get(1));
        }
        if (subgoals.get(1) instanceof ExitGoal) {
            ((ExitGoal)subgoals.get(1)).setSiblingGoal(subgoals.get(0));
        }
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

    /*
     * @return - true all the goals are complete, else return false
     */
    @Override
    public boolean isComplete() {
        for (Goals goal : subgoals) {
            if (!goal.isComplete()) {
                return false;
            }
        }
        return true;
    }

    /*
     * Formats string to (:goal AND :goal) if not complete
     */
    @Override
    public String formatString() {

        // Loop through an conjoin all the goals together
        String goalCondition = "(";
        if (subgoals.get(0).isComplete() && subgoals.get(1).isComplete()) {
            return "";
        } else {
            goalCondition += subgoals.get(0).formatString();
            goalCondition += " AND ";
            goalCondition += subgoals.get(1).formatString();
        }
        goalCondition += ")";
        return goalCondition;

    }
}
