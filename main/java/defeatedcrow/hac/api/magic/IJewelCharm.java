package defeatedcrow.hac.api.magic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

/**
 * チャーム<br>
 * プレイヤーのインベントリ上部9スロットにこのインターフェイスを実装したItemが入っていた場合、<br>
 * チャームであると判定され効果が発動します。
 */
public interface IJewelCharm extends IJewel {

	/**
	 * DIFFENCE<br>
	 * プレイヤーがダメージを受けたときに呼ばれ、軽減量を返す
	 */
	float reduceDamage(DamageSource source, ItemStack charm);

	/**
	 * DIFFENCE<br>
	 * プレイヤーがダメージを受けたときに呼ばれ、被弾時のアクションを起こす。<br>
	 */
	boolean onDiffence(DamageSource source, EntityLivingBase owner, float damage, ItemStack charm);

	// Attack charm
	/**
	 * ATTACK<br>
	 * プレイヤーがダメージを与えたときに呼ばれ、ダメージ増加倍率を返す。
	 */
	float increaceDamage(EntityLivingBase target, DamageSource source, ItemStack charm);

	/**
	 * ATTACK<br>
	 * ダメージを与えたときに呼ばれ、アクションを起こす。<br>
	 */
	boolean onAttacking(EntityLivingBase owner, EntityLivingBase target, DamageSource source, float damage,
			ItemStack charm);

	/**
	 * PLAYER_ATTACK<br>
	 * プレイヤーがダメージを与えたときに呼ばれ、アクションを起こす。<br>
	 */
	boolean onPlayerAttacking(EntityPlayer owner, EntityLivingBase target, DamageSource source, float damage,
			ItemStack charm);

	// Tool charm
	/**
	 * TOOL<br>
	 * プレイヤーがブロックを破壊した時に呼ばれる。<br>
	 * trueの場合、BreakEventをキャンセルする
	 */
	boolean onToolUsing(EntityLivingBase owner, BlockPos pos, IBlockState state, ItemStack charm);

	// Constant charm
	/**
	 * CONSTANT<br>
	 * プレイヤーのTick更新ごとに呼ばれる常時効果<br>
	 */
	void constantEffect(EntityLivingBase owner, ItemStack charm);

	// X key using
	/**
	 * KEY<br>
	 * プレイヤーがコンフィグで設定したUseキーを押すと呼ばれ、アクションを起こす。<br>
	 *
	 * @return
	 */
	boolean onUsing(EntityPlayer owner, ItemStack charm);

	// active check
	/**
	 * チャームが使用可能かどうか。<br>
	 */
	boolean isActive(ItemStack charm);

	/**
	 * チャームが使用可能かを切り替える。<br>
	 */
	void setActive(ItemStack charm, boolean flag);

	// damage or consume
	/**
	 * 効果使用後のダメージor消費処理<br>
	 */
	@Deprecated
	ItemStack consumeCharmItem(ItemStack stack);

}
