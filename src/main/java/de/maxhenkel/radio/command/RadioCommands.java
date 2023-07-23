package de.maxhenkel.radio.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.radio.Radio;
import de.maxhenkel.radio.radio.RadioData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class RadioCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, Commands.CommandSelection environment) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal("radio")
                .requires((commandSource) -> commandSource.hasPermission(Radio.SERVER_CONFIG.commandPermissionLevel.get()));

        literalBuilder.then(Commands.literal("create").then(Commands.argument("url", StringArgumentType.string()).then(Commands.argument("station_name", StringArgumentType.string()).executes(context -> {
            String url = StringArgumentType.getString(context, "url");
            String stationName = StringArgumentType.getString(context, "station_name");
            ServerPlayer player = context.getSource().getPlayerOrException();

            RadioData radioData = new RadioData(UUID.randomUUID(), url, stationName, false);
            player.getInventory().add(radioData.toItemWithNoId());
            return 1;
        }))));

        dispatcher.register(literalBuilder);
    }

}
