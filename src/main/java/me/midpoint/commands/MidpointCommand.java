package me.midpoint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.midpoint.MidpointClient;
import me.midpoint.modules.BaseModule;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MidpointCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("midpoint")
            .then(ClientCommandManager.argument("module", StringArgumentType.word())
                .then(ClientCommandManager.argument("action", StringArgumentType.word())
                    .executes(context -> {
                        String moduleName = StringArgumentType.getString(context, "module");
                        String action = StringArgumentType.getString(context, "action");
                        
                        for (BaseModule m : MidpointClient.modules) {
                            if (m.name.equalsIgnoreCase(moduleName)) {
                                if (action.equalsIgnoreCase("on")) {
                                    m.enabled = true;
                                    context.getSource().sendFeedback(Text.literal("§a✅ " + m.name + " ВКЛЮЧЁН"));
                                } else if (action.equalsIgnoreCase("off")) {
                                    m.enabled = false;
                                    context.getSource().sendFeedback(Text.literal("§c❌ " + m.name + " ВЫКЛЮЧЁН"));
                                } else {
                                    context.getSource().sendFeedback(Text.literal("§eИспользуй on или off"));
                                }
                                return 1;
                            }
                        }
                        context.getSource().sendFeedback(Text.literal("§c❌ Модуль не найден!"));
                        return 0;
                    })
                )
            )
        );
    }
}
