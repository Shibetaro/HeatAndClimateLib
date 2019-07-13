package defeatedcrow.hac.core.client;

import java.util.Set;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.fluid.FluidDic;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedTooltipEvent {

	@SubscribeEvent
	public void advancedTooltip(ItemTooltipEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack target = event.getItemStack();
		boolean flag = false;

		if (player != null && player instanceof EntityPlayerSP && !DCUtil.isEmpty(target)) {
			Item tI = target.getItem();

			if (CoreConfigDC.showAltTips) {

				if (event.getFlags().isAdvanced()) {
					// まず耐久値
					if (tI.isDamageable() && target.getMetadata() == 0) {
						int max = target.getMaxDamage();
						String ret = I18n.format("dcs_climate.tip.durability") + ": " + max;
						event.getToolTip().add(ret);
					}

					// tool tier
					if (tI instanceof ItemTool) {
						Set<String> classes = tI.getToolClasses(target);
						if (!classes.isEmpty()) {
							String className = classes.iterator().next();
							int tier = ((ItemTool) tI).getHarvestLevel(target, className, player, null);
							String ret = I18n.format("dcs_climate.tip.harvestlevel") + ": " + tier;
							event.getToolTip().add(ret);
						}
					}

					// climate reg
					float regH = DamageAPI.itemRegister.getHeatPreventAmount(target);
					float regC = DamageAPI.itemRegister.getColdPreventAmount(target);

					if (regH == 0 && regC == 0 && tI instanceof ItemArmor) {
						ArmorMaterial mat = ((ItemArmor) tI).getArmorMaterial();
						regH = DamageAPI.armorRegister.getHeatPreventAmount(mat);
						regC = DamageAPI.armorRegister.getColdPreventAmount(mat);
					}
					if (regH != 0 || regC != 0) {
						String ret = I18n.format("dcs_climate.tip.resistance") + ": Heat " + regH + "/ Cold " + regC;
						event.getToolTip().add(ret);
					}

					// universal bucket
					if (tI instanceof UniversalBucket) {
						UniversalBucket bucket = (UniversalBucket) tI;
						FluidStack f = bucket.getFluid(target);
						if (f != null && f.getFluid() != null) {
							String fName = f.getFluid().getName();
							FluidDic dic = FluidDictionaryDC.getDic(f.getFluid());
							if (dic != null) {
								fName += " (" + dic.dicName + ")";
							}
							int temp = f.getFluid().getTemperature();
							DCHeatTier tier = DCHeatTier.getTypeByTemperature(temp);
							event.getToolTip().add(fName);
							event.getToolTip().add("Temp: " + temp + " / " + tier);
						}
					}

				}

			}
		}
	}

}
