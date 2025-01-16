package co.ooci.plugin.manager;

import co.ooci.plugin.PvPToggle;
import co.ooci.plugin.entity.PlayerData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j(topic = "PvPToggle")
public class PlayerDataManager {

    @Getter
    public static PlayerDataManager instance = new PlayerDataManager();

    private final Map<String, PlayerData> cache = new LinkedHashMap<>();

    public void initialize(){
        File folder = new File(PvPToggle.getInstance().getDataFolder(), "/data");
        if(!folder.exists()){
            folder.mkdirs();
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));

        if (files!=null){
            for (File file : files) {
                try {
                    String uuid = file.getName().substring(0, file.getName().lastIndexOf("."));;
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    cache.put(uuid,PlayerData.builder()
                            .uuid(uuid)
                            .pvp(config.getBoolean("pvp"))
                            .build());
                } catch (Exception e) {
                    log.error("加载玩家数据时出错：{}", e.getMessage());
                }
            }
        }
    }

    public boolean isPvP(UUID uuid) {
        PlayerData data = cache.get(uuid.toString());
        if (data == null) {
            data = PlayerData.builder()
                    .uuid(uuid.toString())
                    .pvp(false)
                    .build();
            cache.put(uuid.toString(), data);
        }
        return data.isPvp();
    }

    public boolean toggle(UUID uuid) {
        PlayerData data = cache.get(uuid.toString());
        if (data == null) {
            data = PlayerData.builder()
                    .uuid(uuid.toString())
                    .pvp(false)
                    .build();
            cache.put(uuid.toString(), data);
            return data.isPvp();
        } else {
            data.setPvp(!data.isPvp());
            return data.isPvp();
        }
    }

    public void save(){
        for (Map.Entry<String, PlayerData> entry : cache.entrySet()) {
            saveConfig(entry.getKey(), entry.getValue());
        }
    }

    private void saveConfig(String uuid, PlayerData data) {
        File folder = new File(PvPToggle.getInstance().getDataFolder(), "/data");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("pvp", data.isPvp());

        try {
            config.save(file);
        } catch (IOException e) {
            log.error("保存数据到文件时出错: {}", e.getMessage());
        }
    }

}
