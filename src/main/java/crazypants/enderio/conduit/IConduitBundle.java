package crazypants.enderio.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import mekanism.api.gas.IGasHandler;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import appeng.api.networking.IGridHost;
import cofh.api.transport.IItemDuct;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.util.BlockCoord;

@Interface(iface = "appeng.api.networking.IGridHost", modid = "appliedenergistics2")
public interface IConduitBundle extends IInternalPowerReceptor, IFluidHandler, IItemDuct, IGasHandler, IGridHost {

  TileEntity getEntity();

  BlockCoord getLocation();

  // conduits

  boolean hasType(Class<? extends IConduit> type);

  <T extends IConduit> T getConduit(Class<T> type);

  void addConduit(IConduit conduit);

  void removeConduit(IConduit conduit);

  Collection<IConduit> getConduits();

  Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir);

  // connections

  Set<ForgeDirection> getConnections(Class<? extends IConduit> type);

  boolean containsConnection(Class<? extends IConduit> type, ForgeDirection west);

  Set<ForgeDirection> getAllConnections();

  boolean containsConnection(ForgeDirection dir);

  //geometry

  List<CollidableComponent> getCollidableComponents();

  List<CollidableComponent> getConnectors();

  // events

  void onNeighborBlockChange(Block blockId);
  
  void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ);

  void onBlockRemoved();

  void dirty();

  // Facade

  enum FacadeRenderState {
    NONE,
    FULL,
    WIRE_FRAME
  }

  @SideOnly(Side.CLIENT)
  FacadeRenderState getFacadeRenderedAs();

  @SideOnly(Side.CLIENT)
  void setFacadeRenderAs(FacadeRenderState state);

  int getLightOpacity();

  void setLightOpacity(int opacity);

  boolean hasFacade();

  void setFacadeId(Block block);

  void setFacadeId(Block block, boolean triggerUpdate);

  Block getFacadeId();

  void setFacadeMetadata(int meta);

  int getFacadeMetadata();
  
  World getWorld();

  void setGridNode(Object node);
}
