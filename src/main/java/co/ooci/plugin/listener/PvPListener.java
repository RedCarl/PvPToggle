
package co.ooci.plugin.listener;

import co.ooci.plugin.PvPToggle;
import co.ooci.plugin.manager.PlayerDataManager;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collection;
import java.util.Iterator;

@Slf4j(topic = "PvPToggle")
public class PvPListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player attacked) {
			boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
			boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
			if (damagerState) {
				e.setCancelled(true);
			} else if (attackedState) {
				e.setCancelled(true);
			} else {
				// Todo 冷却系统，暂时没做
			}
		} else if (e.getDamager() instanceof Projectile arrow) {
            if(arrow.getShooter() instanceof Player) {
				if(e.getEntity() instanceof Player attacked) {
					Player damager = (Player) arrow.getShooter();
					boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
					boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
					if(damager == attacked) {
						return;
					}
					if(damagerState) {
						e.setCancelled(true);
					} else if(attackedState) {
						e.setCancelled(true);
					} else {
						// Todo 冷却系统，暂时没做
					}
				}
			}
		} else if(e.getDamager() instanceof ThrownPotion potion) {
            if (potion.getShooter() instanceof Player damager && e.getEntity() instanceof Player attacked) {
                boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
				boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
				if(damager == attacked) {
					return;
				}
				if (damagerState) {
					e.setCancelled(true);
				} else if (attackedState) {
					e.setCancelled(true);
				} else {
					// Todo 冷却系统，暂时没做
				}
			}
		} else if (e.getDamager() instanceof LightningStrike && !e.getDamager().getMetadata("TRIDENT").isEmpty() && e.getEntity() instanceof Player attacked) {
			boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
			if (attackedState) {
				e.setCancelled(true);
			}
		} else if (e.getDamager() instanceof Firework && e.getEntity() instanceof Player attacked) {
			boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
			if (attackedState) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onFlameArrow(EntityCombustByEntityEvent e) {
		if(e.getCombuster() instanceof Arrow arrow) {
            if(arrow.getShooter() instanceof Player damager && e.getEntity() instanceof Player attacked) {
				boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
				boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
				if (damagerState) {
					e.setCancelled(true);
				} else if (attackedState) {
					e.setCancelled(true);
				} else {
					// Todo 冷却系统，暂时没做
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent e) {
		if(e.getPotion().getShooter() instanceof Player) {
			for(LivingEntity entity : e.getAffectedEntities()) {
				if(entity instanceof Player attacked) {
					Player damager = (Player) e.getPotion().getShooter();
					boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
					boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());
					if(damager != attacked) {
						if(damagerState) {
							Collection<LivingEntity> affected = e.getAffectedEntities();
							for(LivingEntity ent : affected){
								if(ent instanceof Player && ent != damager){
									e.setIntensity(ent, 0);
								}
							}
						} else if(attackedState) {
							Collection<LivingEntity> affected = e.getAffectedEntities();
							for(LivingEntity ent : affected){
								if(ent instanceof Player && ent != damager){
									e.setIntensity(ent, 0);
								}
							}
						} else {
							// Todo 冷却系统，暂时没做
						}
					}
				}
			}
		}
	}

    @EventHandler(ignoreCancelled = true)
    public void onCloudEffects(AreaEffectCloudApplyEvent e) {
		if(e.getEntity().getSource() instanceof Player) {
			Iterator<LivingEntity> it = e.getAffectedEntities().iterator();
			while(it.hasNext()) {
				LivingEntity entity = it.next();
				if(entity instanceof Player attacked) {
					Player damager = (Player) e.getEntity().getSource();
					boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
                    boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());

					if(attackedState) {
						it.remove();
					} else if(damagerState) {
						it.remove();
					} else {
						// Todo 冷却系统，暂时没做
					}
				}
			}
		}
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerFishing (PlayerFishEvent e) {
		if (e.getCaught() instanceof Player attacked) {
			Player damager = e.getPlayer();
			boolean damagerState = PlayerDataManager.getInstance().isPvP(damager.getUniqueId());
            boolean attackedState = PlayerDataManager.getInstance().isPvP(attacked.getUniqueId());

			if (damager.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD || damager.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
				if (damagerState) {
					e.setCancelled(true);
				} else if (attackedState) {
					e.setCancelled(true);
				}else {
					// Todo 冷却系统，暂时没做
				}
			}
		}
    }

	@EventHandler(ignoreCancelled = true)
	public void onLightningStrike(LightningStrikeEvent e){
		if(e.getCause() == LightningStrikeEvent.Cause.TRIDENT){
			e.getLightning().setMetadata(
					"TRIDENT",
					new FixedMetadataValue(
							PvPToggle.getInstance(),
							e.getLightning().getLocation()
					)
			);
		}
	}
    
}