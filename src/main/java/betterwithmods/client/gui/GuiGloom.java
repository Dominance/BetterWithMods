package betterwithmods.client.gui;

import betterwithmods.util.player.EntityPlayerExt;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;

/**
 * Created by tyler on 5/13/17.
 */
public class GuiGloom {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void draw() {
        if(!EntityPlayerExt.isSurvival(mc.player))
            return;
        ScaledResolution scale = ((GuiIngameForge) mc.ingameGUI).getResolution();
        if (!this.mc.player.isInsideOfMaterial(Material.WATER)
                && this.mc.player.getAir() >= 300) {
            int left = scale.getScaledWidth() / 2 + 91;
            int top = scale.getScaledHeight() - GuiIngameForge.right_height;
                drawPenaltyText(left, top);
        }
    }

    private boolean drawPenaltyText(int left, int top) {
        if (this.mc.player.isDead) {
            return false;
        } else {
            FontRenderer fontRenderer = this.mc.fontRendererObj;
            String text = EntityPlayerExt.getGloomPenalty(mc.player).getDescription();
            if (!text.equals("")) {
                int width = fontRenderer.getStringWidth(text);
                fontRenderer.drawStringWithShadow(text, left - width, top,
                        16777215);
                GuiIngameForge.right_height += 10;
                return true;
            }
            return false;
        }
    }
}
