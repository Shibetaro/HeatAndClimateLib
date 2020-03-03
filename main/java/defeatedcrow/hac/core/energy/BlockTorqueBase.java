package defeatedcrow.hac.core.energy;

import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.blockstate.EnumSide;
import defeatedcrow.hac.api.energy.IWrenchDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.base.BlockContainerDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/*
 * トルク系装置のBlockクラス。
 * メタデータは基本的に持たない。
 */
public abstract class BlockTorqueBase extends BlockContainerDC {

	public BlockTorqueBase(Material m, String s, int max) {
		super(m, s);
		this.setHardness(1.5F);
		this.setResistance(15.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.SIDE, EnumSide.DOWN)
				.withProperty(DCState.POWERED, false));
		this.fullBlock = false;
		this.lightOpacity = 0;
	}

	@Override
	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (!DCUtil.isEmpty(heldItem) && heldItem.getItem() instanceof IWrenchDC) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileTorqueBase) {
				((TileTorqueBase) tile).rotateFace();
			}
			return true;
		}
		if (ClimateCore.isDebug && state
				.getBlock() instanceof BlockTorqueBase && world.isRemote && hand == EnumHand.MAIN_HAND) {
			DCLogger.debugLog("current side: " + DCState.getSide(state, DCState.SIDE));
		}
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	public static void changeState(World world, BlockPos pos, boolean b) {
		IBlockState state = world.getBlockState(pos);
		boolean m = DCState.getBool(state, DCState.POWERED);
		if (m != b) {
			world.setBlockState(pos, state.withProperty(DCState.POWERED, b), 3);
			world.notifyNeighborsOfStateChange(pos, state.getBlock(), true);
		}
	}

	// 設置・破壊処理
	@Override
	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		state = state.withProperty(DCState.SIDE, EnumSide.fromFacing(facing.getOpposite()));
		return state;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int m = meta & 7;
		IBlockState state = this.getDefaultState().withProperty(DCState.SIDE, EnumSide.fromIndex(m))
				.withProperty(DCState.POWERED, Boolean.valueOf((meta & 8) > 0));
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int f = 0;
		int i = 0;

		f = state.getValue(DCState.SIDE).index;
		i = state.getValue(DCState.POWERED) ? 8 : 0;
		return i + f;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCState.SIDE,
				DCState.POWERED
		});

	}
}
