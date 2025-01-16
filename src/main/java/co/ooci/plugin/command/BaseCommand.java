package co.ooci.plugin.command;

import co.ooci.plugin.manager.PlayerDataManager;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name="pvp")
public class BaseCommand {

    @Execute
    void pvp(@Context Player sender) {
        boolean pvp = PlayerDataManager.getInstance().toggle(sender.getUniqueId());
        if(pvp) {
            sender.sendMessage("§7您已 §c关闭 §7PVP。");
        } else {
            sender.sendMessage("§7您已 §a开启 §7PVP。");
        }
    }

}
