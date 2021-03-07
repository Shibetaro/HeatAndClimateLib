package defeatedcrow.hac.api.recipe;

import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Climateを条件に進む、1:1交換のレシピ。<br>
 * 材料は辞書対応。<br>
 */
public interface IClimateSmelting {

	/**
	 * Input登録内容
	 */
	Object getInput();

	ItemStack getOutput();

	@Deprecated
	ItemStack getSecondary();

	@Deprecated
	float getSecondaryChance();

	/**
	 * Inputのコンテナアイテムのリスト
	 */
	ItemStack getContainerItem(ItemStack item);

	/**
	 * macth条件判定用、鉱石辞書変換後のInputリスト
	 */
	List<ItemStack> getProcessedInput();

	/**
	 * Input条件判定
	 */
	boolean matcheInput(ItemStack item);

	boolean matchClimate(int code);

	/**
	 * Climate条件判定
	 */
	boolean matchClimate(IClimate climate);

	/**
	 * 追加条件
	 * trueで条件クリア
	 */
	boolean additionalRequire(World world, BlockPos pos);

	/**
	 * 冷却が必要か<br>
	 * 冷却レシピの場合、エリア内の最も低いTierを冷媒としてカウントし、<br>
	 * 冷媒がNORMAL以下であればレシピが進行する。
	 */
	boolean isNeedCooling();

	/**
	 * BlockでもEntityでもないアイテムの場合、<br>
	 * trueのレシピのみドロップアイテム状態でレシピが進行する
	 */
	boolean canProceedAsDropItem();

	/**
	 * 0: 設置不可, 1: Block, 2: Entity, 3:DropItem
	 */
	int hasPlaceableOutput();

	/**
	 * 0: 高頻度, 1: BlockのRandomTick, 2: 監視イベントでの更新(低頻度)
	 */
	int recipeFrequency();

	/**
	 * おもにレシピ条件表示機能用
	 * HeatTierのみ、Tier+1まで対応範囲になる
	 */
	List<DCHeatTier> requiredHeat();

	List<DCHumidity> requiredHum();

	List<DCAirflow> requiredAir();
}
