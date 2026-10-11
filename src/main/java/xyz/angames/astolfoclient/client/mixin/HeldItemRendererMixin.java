package xyz.angames.astolfoclient.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.angames.astolfoclient.client.module.ModuleManager;
import xyz.angames.astolfoclient.client.module.modules.render.HandPositionModule;
import xyz.angames.astolfoclient.client.module.modules.render.SwingAnimationModule;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
   @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
   private void onSubmitHandsHead(float frameInterp, PoseStack poseStack, SubmitNodeCollector collector, LocalPlayer player, int light, CallbackInfo ci) {
      SwingAnimationModule swingAnim = (SwingAnimationModule)ModuleManager.getModule(SwingAnimationModule.class);
      if (swingAnim != null) {
         swingAnim.updatePhysics(player, frameInterp);
      }
   }

   @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
   private void onSubmitArmWithItem(
      AbstractClientPlayer player,
      float frameInterp,
      float xRot,
      InteractionHand hand,
      float attack,
      ItemStack item,
      float inverseArmHeight,
      PoseStack poseStack,
      SubmitNodeCollector collector,
      int light,
      CallbackInfo ci
   ) {
      SwingAnimationModule swingMod = (SwingAnimationModule)ModuleManager.getModule(SwingAnimationModule.class);
      HandPositionModule handMod = (HandPositionModule)ModuleManager.getModule(HandPositionModule.class);
      boolean swingEnabled = swingMod != null && swingMod.isEnabled();
      boolean handEnabled = handMod != null && handMod.isEnabled();
      if ((swingEnabled || handEnabled) && !item.isEmpty() && !item.has(DataComponents.MAP_ID)) {
         ci.cancel();
         if (swingMod != null) {
            // inverseArmHeight is 1 - equip progress.
            swingMod.handleRenderItem(player, frameInterp, xRot, hand, attack, item, 1.0F - inverseArmHeight, poseStack, collector, light);
         }
      }
   }

   @Inject(method = "renderPlayerArm", at = @At("HEAD"))
   private void onRenderPlayerArmHead(
      PoseStack poseStack, SubmitNodeCollector collector, int light, float inverseArmHeight, float attack, HumanoidArm arm, CallbackInfo ci
   ) {
      if (!SwingAnimationModule.renderingCustomItem) {
         HandPositionModule handMod = (HandPositionModule)ModuleManager.getModule(HandPositionModule.class);
         if (handMod != null && handMod.isEnabled()) {
            Minecraft mc = Minecraft.getInstance();
            boolean isMainHand = mc.player != null && arm == mc.player.getMainArm();
            float[] pos = isMainHand ? handMod.getMainHandPos() : handMod.getOffHandPos();
            poseStack.translate(pos[0], pos[1], pos[2]);
         }
      }

      SwingAnimationModule mod = (SwingAnimationModule)ModuleManager.getModule(SwingAnimationModule.class);
      if (mod != null && mod.isHoldMyItemsEnabled()) {
         if (arm == HumanoidArm.RIGHT) {
            poseStack.translate(mod.getRightX(), mod.getRightZ(), mod.getRightY());
         } else {
            poseStack.translate(mod.getLeftX(), mod.getLeftZ(), mod.getLeftY());
         }
      }
   }
}
