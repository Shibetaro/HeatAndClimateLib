package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.core.recipe.ShapelessNBTRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;

public class ShapelessNBTHandler implements IRecipeHandler<ShapelessNBTRecipe> {
	private final IJeiHelpers jeiHelpers;

	public ShapelessNBTHandler(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public Class<ShapelessNBTRecipe> getRecipeClass() {
		return ShapelessNBTRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public String getRecipeCategoryUid(ShapelessNBTRecipe recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ShapelessNBTRecipe recipe) {
		return new ShapelessNBTWrapper(jeiHelpers, recipe);
	}

	@Override
	public boolean isRecipeValid(ShapelessNBTRecipe recipe) {
		if (recipe.getRecipeOutput() == null) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no output. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.getInput()) {
			if (input instanceof List) {
				if (((List) input).isEmpty()) {
					// missing items for an oreDict name. This is normal behavior, but the recipe is invalid.
					return false;
				}
			}
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount > 9) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has too many inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount == 0) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no inputs. {}", recipeInfo);
			return false;
		}
		return true;
	}
}
