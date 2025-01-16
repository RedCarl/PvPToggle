package co.ooci.plugin;

import co.ooci.plugin.command.BaseCommand;
import co.ooci.plugin.listener.PlayerListener;
import co.ooci.plugin.listener.PvPListener;
import co.ooci.plugin.manager.PlayerDataManager;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Slf4j(topic = "PvPToggle")
public final class PvPToggle extends JavaPlugin {

    @Getter
    private static PvPToggle instance;
    @Getter
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        log.info("注册监听器...");
        registerListener(
                new PlayerListener(),
                new PvPListener()
        );

        log.info("注册指令...");
        this.liteCommands = LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix(getName())
                        .nativePermissions(false)
                )
                .commands(
                        new BaseCommand()
                )
                .build();

        log.info("初始化玩家状态...");
        PlayerDataManager.getInstance().initialize();

        showAD();
    }

    @Override
    public void onDisable() {
        PlayerDataManager.getInstance().save();
        showAD();
    }

    /**
     * 作者信息
     */
    private void showAD() {
        Bukkit.getConsoleSender().sendMessage(
                "[PvPToggle] §7感谢您使用 §c§l"+getDescription().getName()+" v"+getDescription().getVersion(),
                "[PvPToggle] §7本插件由 §c§lOoci Studios §7提供长期支持与维护。"
        );
    }

    public void registerListener(Listener... listeners) {
        Arrays.stream(listeners).forEach((listener) -> Bukkit.getPluginManager().registerEvents(listener, this));
    }
}
