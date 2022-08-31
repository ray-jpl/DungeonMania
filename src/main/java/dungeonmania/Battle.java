package dungeonmania;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import dungeonmania.MovingEntities.Ally;
import dungeonmania.MovingEntities.Hostile;
import dungeonmania.MovingEntities.Hydra;
import dungeonmania.MovingEntities.MovingEntity;
import dungeonmania.MovingEntities.Player;
import dungeonmania.buildableEntities.Bow;
import dungeonmania.buildableEntities.Shield;
import dungeonmania.collectableEntities.Sword;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Position;

public class Battle {
    public BattleResponse battleStart(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Player player = getPlayer(map);
        Position playerPos = map.getPlayerPosition();
        ArrayList<Entity> playerPosEntities = dungeonMap.get(playerPos);

        BattleResponse battle = null;

        List<ItemResponse> weapons = new ArrayList<>();
        boolean isInvincible = false;

        if (player.getCurrEffect() != null) {
            if (player.getCurrEffect().equals("invincibility_potion")) {
                // Player loses no health and enemy dies
                weapons.add(new ItemResponse(player.getCurrPotion().getId(), player.getCurrPotion().getType()));
                isInvincible = true;
            }
        }

        // Get Ally properties
        double allyDefence = 0;
        double allyAttack = 0;
        for (Position adjPos : playerPos.getAdjacentPositions()) {
            if (dungeonMap.containsKey(adjPos)) {
                for (Entity entity : dungeonMap.get(adjPos)) {
                    if (entity instanceof Ally) {
                        Ally ally = (Ally) entity;
                        allyDefence = ally.getHealth();
                        allyAttack = ally.getAttack();
                    }
                }
            }
        }

        // Get Weapon Bonuses
        int swordAttack = 0;
        int bowMultiplier = 1;
        int shieldDef = 0;

        Sword sword = (Sword) player.getItemByType("sword");
        if (sword != null) {
            weapons.add(new ItemResponse(sword.getId(), sword.getType()));
            swordAttack = sword.getSwordAttack();
            sword.useSword();
            if (sword.getDurability() == 0) {
                player.removeItem(map, sword.getId());
            }
        }

        Bow bow = (Bow) player.getItemByType("bow");
        if (bow != null) {
            weapons.add(new ItemResponse(bow.getId(), bow.getType()));
            bowMultiplier = bow.getDmgMultiplier();
            bow.useBow();
            if (bow.getDurability() == 0) {
                player.removeItem(map, bow.getId());
            }
        }

        Shield shield = (Shield) player.getItemByType("shield");
        if (shield != null) {
            weapons.add(new ItemResponse(shield.getId(), shield.getType()));
            shieldDef = shield.getDefence();
            shield.useShield();
            if (shield.getDurability() == 0) {
                player.removeItem(map, shield.getId());
            }
        }

        // Have to use C style for loop or ConcurrentModificationException occurs
        // sometimes
        // TODO: Fix properly later by moving removes out of the loop.
        for (int i = 0; i < playerPosEntities.size(); i++) {
            Entity entity = playerPosEntities.get(i);
            if (entity instanceof Hostile && entity instanceof MovingEntity) {
                MovingEntity enemy = (MovingEntity) entity;
                List<RoundResponse> rounds = new ArrayList<>();
                battle = new BattleResponse(enemy.getType(), rounds, player.getHealth(), enemy.getHealth());
                if (isInvincible) {
                    rounds.add(new RoundResponse(0, -enemy.getHealth(), weapons));
                    enemy.setHealth(0);
                    dungeonMap.get(playerPos).remove((Entity) enemy);
                    map.setDungeonMap(dungeonMap);
                    break;
                }
                ////////////////////////////////
                // Rounds are calculated here //
                ////////////////////////////////
                while (player.getHealth() > 0 && enemy.getHealth() > 0) {
                    double player_attack = player.getAttack();
                    double enemyAttack = enemy.getAttack();

                    double deltaPlayerHealth = -(enemyAttack - allyDefence - shieldDef) / 10;
                    double deltaEnemyHealth = -(bowMultiplier * (player_attack + swordAttack + allyAttack) / 5);
                    if (enemy instanceof Hydra) {
                        Hydra hydra = (Hydra) enemy;
                        Random rand = new Random();
                        if (rand.nextDouble() < hydra.health_increase_rate()) {
                            deltaEnemyHealth = hydra.health_increase_amount();
                        }
                    }

                    rounds.add(battleRound(player, enemy, deltaPlayerHealth, deltaEnemyHealth, weapons));
                }
                if (player.getHealth() <= 0) {
                    dungeonMap.get(playerPos).remove((Entity) player);
                    map.setDungeonMap(dungeonMap);
                }
                if (enemy.getHealth() <= 0) {
                    dungeonMap.get(playerPos).remove((Entity) enemy);
                    player.incrementEnemiesKilled();
                    map.setDungeonMap(dungeonMap);
                }
            }
        }

        return battle;
    }

    public RoundResponse battleRound(Player player, MovingEntity enemy, double deltaPlayerHealth,
            double deltaEnemyHealth, List<ItemResponse> weapons) {
        double player_health = player.getHealth();

        double enemyHealth = enemy.getHealth();

        double newPlayerHealth = player_health + deltaPlayerHealth;
        double newEnemyHealth = enemyHealth + deltaEnemyHealth;

        player.setHealth(newPlayerHealth);
        enemy.setHealth(newEnemyHealth);

        return new RoundResponse(deltaPlayerHealth, deltaEnemyHealth, weapons);
    }

    public Player getPlayer(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        ArrayList<Entity> playerPosition = dungeonMap.get(map.getPlayerPosition());
        Player player = null;

        for (Entity entity : playerPosition) {
            if (entity instanceof Player) {
                player = (Player) entity;
                return player;
            }
        }
        return player;
    }

}
