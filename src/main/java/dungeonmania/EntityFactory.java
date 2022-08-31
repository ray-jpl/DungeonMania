package dungeonmania;

import com.google.gson.JsonObject;

import dungeonmania.LogicalEntities.LightBulb;
import dungeonmania.LogicalEntities.Logic;
import dungeonmania.LogicalEntities.SwitchDoor;
import dungeonmania.LogicalEntities.Wire;
import dungeonmania.LogicalEntities.Operators.AND;
import dungeonmania.LogicalEntities.Operators.CO_AND;
import dungeonmania.LogicalEntities.Operators.OR;
import dungeonmania.LogicalEntities.Operators.XOR;
import dungeonmania.MovingEntities.Assassin;
import dungeonmania.MovingEntities.Hydra;
import dungeonmania.MovingEntities.Mercenary;
import dungeonmania.MovingEntities.Player;
import dungeonmania.MovingEntities.Spider;
import dungeonmania.MovingEntities.ZombieToast;
import dungeonmania.buildableEntities.Bow;
import dungeonmania.buildableEntities.Shield;
import dungeonmania.collectableEntities.Arrows;
import dungeonmania.collectableEntities.Key;
import dungeonmania.collectableEntities.Sword;
import dungeonmania.collectableEntities.TimeTurner;
import dungeonmania.collectableEntities.Treasure;
import dungeonmania.collectableEntities.Wood;
import dungeonmania.collectableEntities.Bomb.Bomb;
import dungeonmania.collectableEntities.Bomb.BombActive;
import dungeonmania.collectableEntities.Potions.InvincibilityPotion;
import dungeonmania.collectableEntities.Potions.InvisiblityPotion;
import dungeonmania.StaticEntities.Boulder;
import dungeonmania.StaticEntities.Door;
import dungeonmania.StaticEntities.Exit;
import dungeonmania.StaticEntities.Portal;
import dungeonmania.StaticEntities.Switch;
import dungeonmania.StaticEntities.TimeTravellingPortal;
import dungeonmania.StaticEntities.Wall;
import dungeonmania.StaticEntities.ZombieSpawner;
import dungeonmania.util.Position;

public class EntityFactory {

    public EntityInterface initialiseEntity(String id, String type, Position pos, JsonObject config) {
        // TODO: need to find a better way to do the factory
        EntityInterface entity = null;
        entity = initialiseCollectableEntity(id, type, pos, config);
        if (entity == null) {
            entity = initialiseMovingEntity(id, type, pos, config);
        }

        if (entity == null) {
            entity = initialiseStaticEntity(id, type, pos, config);
        }

        if (entity == null) {
            entity = new Entity(id, type, pos);
        }

        return entity;
    }

    public EntityInterface initialisePortal(String id, String type, Position pos, String colour) {
        EntityInterface entity = new Portal(id, type, pos, colour);
        return entity;
    }

    public EntityInterface initialiseBuildableEntity(String id, String type, Position pos, JsonObject config) {
        EntityInterface entity = null;
        if (type.equals("bow")) {
            entity = new Bow(id, type, pos, config.get("bow_durability").getAsInt());
        } else if (type.equals("shield")) {
            entity = new Shield(id, type, pos, config.get("shield_defence").getAsInt(),
                    config.get("shield_durability").getAsInt());
        }

        return entity;
    }

    public EntityInterface initialiseCollectableEntity(String id, String type, Position pos, JsonObject config) {
        EntityInterface entity = null;
        if (type.equals("treasure")) {
            entity = new Treasure(id, type, pos);
        } else if (type.equals("invincibility_potion")) {
            entity = new InvincibilityPotion(id, type, pos, config.get("invincibility_potion_duration").getAsInt());
        } else if (type.equals("invisibility_potion")) {
            entity = new InvisiblityPotion(id, type, pos, config.get("invisibility_potion_duration").getAsInt());
        } else if (type.equals("wood")) {
            entity = new Wood(id, type, pos);
        } else if (type.equals("arrow")) {
            entity = new Arrows(id, type, pos);
        } else if (type.equals("bomb")) {
            entity = new Bomb(id, type, pos, config);
        } else if (type.equals("sword")) {
            entity = new Sword(id, type, pos, config.get("sword_attack").getAsInt(),
                    config.get("sword_durability").getAsInt());
        } else if (type.equals("time_turner")) {
            entity = new TimeTurner(id, type, pos);
        }

        return entity;
    }

    public static EntityInterface initialiseDoorAndKey(String id, String type, Position pos, int keyId) {
        EntityInterface entity = null;
        if (type.equals("key")) {
            entity = new Key(id, type, pos, keyId);
        } else if (type.equals("door")) {
            entity = new Door(id, type, pos, keyId);
        } else if (type.equals("door_open")) {
            entity = new Door(id, type, pos, keyId);
        }

        if (entity == null) {
            entity = new Entity(id, type, pos);
        }

        return entity;
    }

    public static EntityInterface initialiseActiveBomb(String id, String type, Position pos, int bombRadius) {
        return new BombActive(id, type, pos, bombRadius);
    }

    public EntityInterface initialiseMovingEntity(String id, String type, Position pos, JsonObject config) {
        EntityInterface entity = null;
        if (type.equals("player")) {
            entity = new Player(id, type, pos, config.get("player_health").getAsInt(),
                    config.get("player_attack").getAsInt());
        } else if (type.equals("zombie_toast")) {
            entity = new ZombieToast(id, type, pos, config.get("zombie_health").getAsInt(),
                    config.get("zombie_attack").getAsInt());
        } else if (type.equals("mercenary")) {
            entity = new Mercenary(id, type, pos, config.get("mercenary_health").getAsInt(),
                    config.get("mercenary_attack").getAsInt(), config.get("bribe_radius").getAsInt(),
                    config.get("bribe_amount").getAsInt());
        } else if (type.equals("spider")) {
            entity = new Spider(id, type, pos, config.get("spider_health").getAsInt(),
                    config.get("spider_attack").getAsInt());
        } else if (type.equals("assassin")) {
            entity = new Assassin(id, type, pos, config.get("assassin_health").getAsInt(),
                    config.get("assassin_attack").getAsInt(), config);
        } else if (type.equals("hydra")) {
            entity = new Hydra(id, type, pos, config.get("hydra_health").getAsInt(),
                    config.get("hydra_attack").getAsInt(), config);
        }

        return entity;
    }

    public EntityInterface initialiseStaticEntity(String id, String type, Position pos, JsonObject config) {
        EntityInterface entity = null;
        if (type.equals("boulder")) {
            entity = new Boulder(id, type, pos);
        } else if (type.equals("exit")) {
            entity = new Exit(id, type, pos);
        } else if (type.equals("switch")) {
            entity = new Switch(id, type, pos);
        } else if (type.equals("wall")) {
            entity = new Wall(id, type, pos);
        } else if (type.equals("zombie_toast_spawner")) {
            entity = new ZombieSpawner(id, type, pos, config.get("zombie_spawn_rate").getAsInt());
        } else if (type.equals("time_travelling_portal")) {
            entity = new TimeTravellingPortal(id, type, pos);
        } else if (type.equals("wire")) {
            entity = new Wire(id, type, pos);
        }

        return entity;
    }

    public static Logic intialiseLogic(String logic) {
        Logic operation = null;
        if (logic.equals("AND")) {
            operation = new AND();
        } else if (logic.equals("OR")) {
            operation = new OR();
        } else if (logic.equals("XOR")) {
            operation = new XOR();
        } else if (logic.equals("CO_AND")) {
            operation = new CO_AND();
        }
        return operation;
    }

    public static EntityInterface initialiseLogicalEntity(String id, String type, Position pos, String logic) {
        EntityInterface entity = null;
        Logic logicOperation = intialiseLogic(logic);
        if (type.equals("light_bulb_off")) {
            entity = new LightBulb(id, type, pos, logicOperation);
        }

        return entity;
    }

    public static EntityInterface initialiseLogicalEntity(String id, String type, Position pos, int keyId,
            String logic) {
        EntityInterface entity = null;
        Logic logicOperation = intialiseLogic(logic);
        if (type.equals("switch_door")) {
            entity = new SwitchDoor(id, type, pos, keyId, logicOperation);
        }
        return entity;
    }
}
