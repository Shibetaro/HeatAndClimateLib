package defeatedcrow.hac.core.climate.recipe;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IFluidRecipe;
import defeatedcrow.hac.api.recipe.IFluidRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class FluidCraftRegister implements IFluidRecipeRegister {

	public FluidCraftRegister() {}

	public IFluidRecipeRegister instance() {
		return RecipeAPI.registerFluidRecipes;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<IFluidRecipe> list = new ArrayList<>();

	@Override
	public List<IFluidRecipe> getRecipeList() {
		return list;
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid,
			DCHeatTier heat, DCHumidity hum, DCAirflow air, boolean needCooling, FluidStack inFluid, Object... input) {
		if (secondary == null) {
			secondary = ItemStack.EMPTY;
		}
		boolean b1 = input == null && inFluid == null;
		boolean b2 = DCUtil.isEmpty(output) && outFluid == null;
		boolean b3 = hasEmptyInput(input);
		if (!b1 && !b2 && !b3) {
			list.add(new FluidCraftRecipe(output, secondary, outFluid, heat, hum, air, secondaryChance, needCooling,
					inFluid, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid,
			List<DCHeatTier> heat, DCHumidity hum, DCAirflow air, boolean needCooling, FluidStack inFluid,
			Object... input) {
		if (secondary == null) {
			secondary = ItemStack.EMPTY;
		}
		boolean b1 = input == null && inFluid == null;
		boolean b2 = DCUtil.isEmpty(output) && outFluid == null;
		boolean b3 = hasEmptyInput(input);
		if (!b1 && !b2 && !b3) {
			FluidCraftRecipe recipe = new FluidCraftRecipe(output, secondary, outFluid, null, hum, air, secondaryChance,
					needCooling, inFluid, input);
			for (DCHeatTier h : heat) {
				recipe.requiredHeat().add(h);
			}
			list.add(recipe);
		}
	}

	@Override
	public void addRecipe(IFluidRecipe recipe, DCHeatTier heat) {
		Class clazz = recipe.getClass();
		if (!Modifier.isAbstract(clazz.getModifiers()))
			this.addRecipe(recipe);
	}

	private boolean hasEmptyInput(Object... inputs) {
		if (inputs != null && inputs.length > 0) {
			for (Object in : inputs) {
				if (in instanceof String) {
					boolean ret = true;
					if (OreDictionary.doesOreNameExist((String) in)) {
						List l = OreDictionary.getOres((String) in);
						if (!l.isEmpty() && l.size() > 0) {
							ret = false;
						}
					}

					if (ret) {
						return true;
					}
				} else if (in == null) {
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public IFluidRecipe getRecipe(IClimate clm, List<ItemStack> items, FluidStack fluid) {
		IFluidRecipe ret = null;
		int c = 0;
		for (IFluidRecipe recipe : list) {
			if (recipe.matches(items, fluid) && recipe.matchClimate(clm)) {
				if (recipe.recipeCoincidence() >= c) {
					ret = recipe;
					c = recipe.recipeCoincidence();
					// DCLogger.debugInfoLog("4: coincidence " + c);
				}
			}
		}

		if (ret != null) {
			// DCLogger.debugInfoLog("5: TRUE");
			return ret;
		}
		return null;
	}

	@Override
	public IFluidRecipe getRecipe(int code, List<ItemStack> items, FluidStack fluid) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return getRecipe(clm, items, fluid);
	}

	@Override
	public void addRecipe(IFluidRecipe recipe) {
		if (!hasEmptyInput(recipe.getInput()))
			list.add(recipe);
	}

	@Override
	public boolean removeRecipe(IClimate clm, List<ItemStack> inputs, FluidStack inFluid) {
		IFluidRecipe recipe = getRecipe(clm, inputs, inFluid);
		if (recipe != null && list.remove(recipe)) {
			return true;
		}
		return false;
	}

}
