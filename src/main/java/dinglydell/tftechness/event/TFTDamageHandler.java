package dinglydell.tftechness.event;

import java.util.HashMap;
import java.util.Map;

import mods.railcraft.common.util.misc.RailcraftDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import blusunrize.immersiveengineering.common.util.Lib;

import com.bioxx.tfc.Handlers.EntityDamageHandler;
import com.bioxx.tfc.api.Enums.EnumDamageType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Class that allows TFC damage types to be exist for non-TFC damage sources */
public class TFTDamageHandler extends EntityDamageHandler {

	private Map<String, EnumDamageType> types = new HashMap<String, EnumDamageType>();

	public static TFTDamageHandler INSTANCE;

	public TFTDamageHandler() {
		INSTANCE = this;
	}

	public void registerDamageSource(DamageSource source, EnumDamageType type) {
		registerDamageSource(source.getDamageType(), type);
	}

	/**
	 * Associate a damage source with a damage type. TFT will make sure this
	 * type is applied whenever an entity is hurt with this source. Warning:
	 * Anything registered in this way will automatically have its damage value
	 * multiplied by 10 to adjust for the TFC scale
	 */
	public void registerDamageSource(String sourceName, EnumDamageType type) {
		types.put(sourceName, type);
	}

	//HACK ALERT - don't read this
	@Override
	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event) {
		if (types.containsKey(event.source.getDamageType())) {

			event.ammount = applyArmorCalculations(event.entityLiving,
					new EntityDamageSource(event.source.getDamageType(),
							new DamageTypeSource(event.entity.worldObj, types
									.get(event.source.getDamageType()))),
					event.ammount);
		}
	}

	@Override
	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event) {

	}

	/** Registers all the damage sources added by IE & RC */
	public void registerDamageSources() {
		registerDamageSource(Lib.DMG_Crusher, EnumDamageType.CRUSHING);

		registerDamageSource(Lib.DMG_RevolverCasull, EnumDamageType.PIERCING);

		registerDamageSource(Lib.DMG_RevolverBuck, EnumDamageType.PIERCING);
		registerDamageSource(Lib.DMG_RevolverDragon, EnumDamageType.PIERCING);
		registerDamageSource(Lib.DMG_RevolverHoming, EnumDamageType.PIERCING);
		registerDamageSource(Lib.DMG_RevolverSilver, EnumDamageType.PIERCING);
		registerDamageSource(Lib.DMG_RevolverWolfpack, EnumDamageType.PIERCING);
		registerDamageSource(Lib.DMG_RevolverAP, EnumDamageType.PIERCING);
		registerDamageSource(Lib.DMG_Railgun, EnumDamageType.PIERCING);

		registerDamageSource(RailcraftDamageSource.CRUSHER,
				EnumDamageType.CRUSHING);
		registerDamageSource(RailcraftDamageSource.TRAIN,
				EnumDamageType.CRUSHING);
		registerDamageSource(RailcraftDamageSource.BORE,
				EnumDamageType.CRUSHING);
	}

}
