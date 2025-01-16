package co.ooci.plugin.manager;

import cc.carm.lib.easyplugin.utils.ColorParser;
import co.ooci.pixelmon.core.enums.BaseColor;
import co.ooci.pixelmon.core.utils.PixelmonUtils;
import co.ooci.pixelmon.survive.OociSurvive;
import co.ooci.pixelmon.survive.entity.PokeDex;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j(topic = "OociSurvive")
public class PokeDexManager {

    @Getter
    public static PokeDexManager instance = new PokeDexManager();

    private Map<String, List<PokeDex>> cache = new LinkedHashMap<>();

    public void initialize() {
        loadingConfig();
    }

    public List<PokeDex> getPokeDex(String uuid){
        return cache.getOrDefault(uuid,new LinkedList<>());
    }

    public Integer getPokeDexCount(String uuid){
        return cache.getOrDefault(uuid,new LinkedList<>()).size();
    }

    public void addPokeDex(String uuid, PokeDex dex, Pokemon pokemon){
        List<PokeDex> dexs = cache.getOrDefault(uuid,new LinkedList<>());

        // 检查是否重复添加,如果存在就返回这个实体
        for (PokeDex data : dexs) {
            if (data.getPokemon().equals(dex.getPokemon())){
                data.setLast(new Date());
                data.setCount(data.getCount()+1);
                cache.put(uuid,dexs);
                return;
            }
        }

        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player!=null){
            player.sendMessage(ColorParser.parse(
                    BaseColor.SUCCESS.getIcon()+" &7宝可梦 "+ PixelmonUtils.getPokemonName(pokemon)+" &7的图鉴解锁了。"
            ));
        }

        dex.setCount(1);
        dexs.add(dex);
        cache.put(uuid,dexs);
    }

    public void saveConfig(String uuid){
        saveConfig(uuid,getPokeDex(uuid));
    }

    public void saveToFile(){
        for (String uuid : cache.keySet()) {
            saveConfig(uuid);
        }
    }

    private void loadingConfig(){
        File folder = new File(OociSurvive.getInstance().getDataFolder(), "/dex");
        if(!folder.exists()){
            folder.mkdirs();
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));

        if (files!=null){
            log.info("正在加载数据文件，已检测到 {} 个配置。",files.length);
            for (File file : files) {
                try {
                    String uuid = file.getName().substring(0, file.getName().lastIndexOf("."));;
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    List<PokeDex> dexs = new LinkedList<>();
                    for (String key : config.getKeys(false)) {
                        dexs.add(PokeDex.builder()
                                .uuid(uuid)
                                .pokemon(key)
                                .first(config.getLong(key+".first")==0?null:new Date(config.getLong(key+".first")))
                                .last(config.getLong(key+".last")==0?null:new Date(config.getLong(key+".last")))
                                .count(config.getInt(key+".count"))
                                .build());
                    }
                    cache.put(uuid,dexs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            log.info("未检测到数据文件。");
        }
    }

    public void saveConfig(String uuid, List<PokeDex> dexs) {
        File folder = new File(OociSurvive.getInstance().getDataFolder(), "/dex");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        for (PokeDex pokeDex : dexs) {
            String key = pokeDex.getPokemon(); // 获取 Pokémon 名称作为键
            if (pokeDex.getFirst() != null) {
                config.set(key + ".first", pokeDex.getFirst().getTime());
            } else {
                config.set(key + ".first", 0);
            }
            if (pokeDex.getLast() != null) {
                config.set(key + ".last", pokeDex.getLast().getTime());
            } else {
                config.set(key + ".last", 0);
            }
            config.set(key + ".count", pokeDex.getCount());
        }

        try {
            config.save(file);
            log.info("已成功保存数据到文件: {}", file.getName());
        } catch (IOException e) {
            log.error("保存数据到文件时出错: {}", e.getMessage());
        }
    }
}
