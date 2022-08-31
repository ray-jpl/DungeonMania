package dungeonmania.Goals;

public abstract class Goals {

    /*
     * @returns true if the goal is complete and false if incomplete
     * @pre-condition - the goal condition will never be null
     */
    public abstract boolean isComplete();

    /*
     * @return String of goal(s) respective of whether they are a basic goal or a composite goal 
     * Empty string is returned if the goal is completed
     */
    public abstract String formatString();

}
