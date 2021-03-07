package defeatedcrow.hac.core.climate.recipe;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.recipe.IMillRecipe;
import defeatedcrow.hac.api.recipe.IMillRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MillRecipeRegister implements IMillRecipeRegister {

	public MillRecipeRegister() {
		this.list = new ArrayList<IMillRecipe>();
	}

	public IMillRecipeRegister instance() {
		return RecipeAPI.registerMills;
	}

	private static List<IMillRecipe> list;

	@Override
	public List<IMillRecipe> getRecipeList() {
		return this.list;
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, Object input) {
		if (secondary == null) {
			secondary = ItemStack.EMPTY;
		}
		if (input != null && !DCUtil.isEmpty(output)) {
			if (input instanceof String && OreDictionary.getOres((String) input).isEmpty()) {
				DCLogger.infoLog("MillRecipe Accepted empty input: " + input);
				return;
			}
			if (input instanceof List && ((List) input).isEmpty()) {
				DCLogger.infoLog("MillRecipe Accepted empty input list");
				return;
			}
			list.add(new MillRecipe(output, secondary, secondaryChance, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, Object input) {
		addRecipe(output, secondary, 1.0F, input);
	}

	@Override
	public void addRecipe(ItemStack output, Object input) {
		addRecipe(output, ItemStack.EMPTY, 0.0F, input);
	}

	@Override
	public void addRecipe(IMillRecipe recipe) {
		Class clazz = recipe.getClass();
		if (!Modifier.isAbstract(clazz.getModifiers()))
			list.add(recipe);
	}

	@Override
	public IMillRecipe getRecipe(ItemStack item) {
		IMillRecipe ret = null;
		if (!list.isEmpty()) {
			for (IMillRecipe recipe : list) {
				if (recipe.matchInput(item)) {
					// DCLogger.debugLog("get recipe");
					ret = recipe;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean removeRecipe(ItemStack input) {
		IMillRecipe recipe = getRecipe(input);
		if (recipe != null && list.remove(recipe)) {
			return true;
		}
		return false;
	}

}
