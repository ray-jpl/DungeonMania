package dungeonmania.buildableEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildableHelper {
    public static List<HashMap<String,Integer>> buildableBlueprints(String buildable) {
        List<HashMap<String,Integer>> blueprints = null;
        if (buildable.equals("bow")) {
            Bow testBow = new Bow("-1",buildable, null, 0);
            blueprints = testBow.getBlueprints(); 
        } else if (buildable.equals("shield")) {
            Shield testShield = new Shield("-1", buildable, null, 0, 0);
            blueprints = testShield.getBlueprints();
        }

        return blueprints;
    }

    public static List<String> buildableObjects() {
        List<String> buildablesList = new ArrayList<>();
        buildablesList.add("bow");
        buildablesList.add("shield");
        return buildablesList;
    }
}
