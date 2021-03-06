package crazypants.enderio.machine.attractor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.generator.combustion.TranslatedCubeRenderer;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.material.Material;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class AttractorRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private VertXForm xform = new VertXForm();
  private VertXForm2 xform2 = new VertXForm2();
  private ItemStack floatingStack;

  public AttractorRenderer() {
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return type != ItemRenderType.INVENTORY;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

    if(type == ItemRenderType.INVENTORY) {

      GL11.glPushMatrix();
      GL11.glTranslatef(-2, 3, 0);
      GL11.glScalef(10F, 10F, 10F);
      GL11.glTranslatef(1.0F, 0.5F, 1.0F);
      GL11.glScalef(1.0F, 1.0F, -1F);
      GL11.glRotatef(210F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);

      GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
      renderInventoryBlock(EnderIO.blockAttractor, item.getItemDamage(), 0, (RenderBlocks) data[0]);
      GL11.glPopMatrix();

      GL11.glPushMatrix();
      float scale = 0.4f;
      GL11.glScalef(scale, scale, scale);
      GL11.glTranslatef(12, 2f, 0);
      RenderItem ri = new RenderItem();
      ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
          getFloatingStack(null, 0, 0, 0, 0), 0, 0);
      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_LIGHTING);
    } else {
      GL11.glTranslatef(0, 0.25f, 0);
      GL11.glScalef(1.5f, 1.5f, 1.5f);
      renderInventoryBlock(EnderIO.blockAttractor, item.getItemDamage(), 0, (RenderBlocks) data[0]);
    }

  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

    World world = te.getWorldObj();
    float f = world.getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
    int l = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    Tessellator.instance.setColorOpaque_F(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    GL11.glPopMatrix();

    EntityItem ei = new EntityItem(world, x, y, z, getFloatingStack(te, x, y, z, tick));
    ei.age = (int) world.getTotalWorldTime();

    //Remove the bob
    float bob = ((float) ei.age + tick) / 10.0F;
    ei.hoverStart = -bob;

    RenderManager.instance.getEntityRenderObject(ei).doRender(ei, x + 0.5, y + 0.55, z + 0.5, 0, tick);

  }

  protected ItemStack getFloatingStack(TileEntity te, double x, double y, double z, float tick) {
    if(floatingStack == null) {
      floatingStack = new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal());
    }
    return floatingStack;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    GL11.glDisable(GL11.GL_LIGHTING);
    Tessellator.instance.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    Tessellator.instance.draw();
    GL11.glEnable(GL11.GL_LIGHTING);

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    BoundingBox bb = BoundingBox.UNIT_CUBE;
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, null, world != null);

    Tessellator.instance.addTranslation(x, y, z);

    IIcon icon = EnderIO.blockAttractor.getOnIcon();
    if(world != null) {
      icon = block.getIcon(world, x, y, z, 0);
    }

    float height = 0.475f;
    float width = 0.5f;
    bb = BoundingBox.UNIT_CUBE.scale(width, height, 1).translate(0, -0.5f + height / 2, 0);
    xform2.isX = false;
    CubeRenderer.render(bb, icon, xform2, true);

    bb = BoundingBox.UNIT_CUBE.scale(1, height, width).translate(0, -0.5f + height / 2, 0);
    xform2.isX = true;
    CubeRenderer.render(bb, icon, xform2, true);

    Tessellator.instance.addTranslation(-x, -y, -z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockAttractor.renderId;
  }

  private static class VertXForm implements VertexTransform {

    public VertXForm() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = 0.8;
      if(vec.y > 0.5) {
        pinch = 0.4;
      }
      vec.x -= 0.5;
      vec.x *= pinch;
      vec.x += 0.5;
      vec.z -= 0.5;
      vec.z *= pinch;
      vec.z += 0.5;

      double scale = 0.5;
      vec.y -= 0.5;
      vec.y *= scale;
      vec.y += (0.5 * scale);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

  private static class VertXForm2 implements VertexTransform {

    boolean isX = true;

    public VertXForm2() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = 0.9;
      if(vec.y > 0.2) {
        pinch = 0.5;
      }
      if(isX) {
        vec.x -= 0.5;
        vec.x *= pinch;
        vec.x += 0.5;
      } else {
        vec.z -= 0.5;
        vec.z *= pinch;
        vec.z += 0.5;
      }
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
