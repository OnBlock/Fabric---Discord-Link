package fr.arthurbambou.fblink.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import fr.arthurbambou.fblink.FBLink;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    /**
     * This method handles message from the death of tamed entity, team chat, various commands and everything
     * broadcastChatMessage will processes
     * @param text_1
     * @param ci
     */
    @Inject(at = @At("RETURN"), method = "sendMessage")
    public void sendMessage(Text text_1, CallbackInfo ci) {
        FBLink.getDiscordBot().sendMessage(text_1);
    }

    @Inject(at = @At("HEAD"), method = "shutdown")
    private void onShutdown(CallbackInfo ci) {
        FBLink.getDiscordBot().onServerShutdown();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        FBLink.getDiscordBot().onTick();
    }
}
