package fr.arthurbambou.fblink.mixin;

import fr.arthurbambou.fblink.FBLink;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.ServerCommandOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandOutput.class)
public class MixinServerCommandOutput {

    @Shadow @Final private MinecraftServer server;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onStartup(MinecraftServer server, CallbackInfo ci) {
        FBLink.onStartup(server);
    }

}
