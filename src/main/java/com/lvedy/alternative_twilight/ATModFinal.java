package com.lvedy.alternative_twilight;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class ATModFinal {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Properties properties;
    private static final String CONFIG_FILENAME = "ATModConfig.properties";
    // 配置文件版本和覆盖选项
    public static String ConfigVersion;
    public static int ConfigOverrideEnabled;
    
    // 配置属性字段
    public static int NagaModify;
    public static int LichModify;
    public static int MinoshroomModify;
    public static int HydraModify;
    public static int KnightPhantomModify;
    public static int UrGhastModify;
    public static int AlphaYetiModify;
    public static int SnowQueenModify;
    public static int NagaArmor;
    public static int NagaArmorDamage;
    public static int NagaArmorDamageMin;
    public static int NagaPushDamage;
    public static int NagaPush;
    public static int NagaDrop;
    public static int NagaDaze;
    public static int NagaStop;
    public static int NagaReduceDamage1;
    public static int NagaReduceDamage2;
    public static int LichObsidianSwitch;
    public static int LichObsidian;
    public static int LichObsidianHealth;
    public static int MagicHealth;
    public static int MagicCD;
    public static int ExtraArmor;
    public static int ExtraTime;
    public static int MagicReduceDamage;
    public static int MinionSummon;
    public static int MinionDamageLich;
    public static int MinionHealth;
    public static int MinionArmor;
    public static int MinionArmorToughness;
    public static int MinionTreatLich;
    public static int LichMaxHealth;
    public static int LichArmor;
    public static int LichArmorToughness;
    public static int MinoshroomDuration;
    public static int MinoshroomAmplifier;
    public static int MinoshroomReduceDamage;
    public static int MinoshroomAddDamage;
    public static int MinoshroomHealthThreshold;
    public static int MinoshroomHealth;
    public static int MinoshroomArmor;
    public static int MinoshroomArmorToughness;
    public static int MinoshroomTrackingMaxTime;
    public static int MinoshroomAttackMultiplier;
    public static int MinoshroomSpeedMultiplier;
    public static int MinoshroomBuffLossPercent;
    public static int MinoshroomTargetSwitchCooldown;
    public static int MinoshroomAngry;
    public static int MinoshroomChargeTimeReduction;
    public static int MinoshroomWarcryBreakBlocks;
    public static int MinoshroomBreakBlocksRadius;
    public static int MinoshroomBreakBlocksHeight;
    public static int HydraBomb;
    public static int HydraAddDamage;
    public static int HydraHead;
    public static int KnightTreat;
    public static int KnightDamage;
    public static int KnightDuration;
    public static int KnightAmplifier;
    public static float KnightBox;
    public static int UrGhastAddDamage;
    public static int UrGhastDamageMin;
    public static int UrGhastBomb;
    public static int YetiSnowBox;
    public static int YetiIce;
    public static int SnowQueenTrapCD;
    public static double SnowQueenTrapCDCD;
    public static int SnowQueenTrapCount_1;
    public static int SnowQueenTrapCount_2;
    public static int SnowQueenHealthThreshold;
    public static int SnowQueenIceCD;
    public static int SnowQueenInvincible;
    public static int SnowQueenTreat;
    public static int SnowQueenIceDuration;
    public static int SnowQueenDamage;
    public static float SnowQueenExplosionDamage;
    public static int SnowQueenIceArmor;
    public static int SnowTrapScale;
    
    static {
        handleConfigFile();
    }
    
    /**
     * 从外部文件加载配置
     * @param configPath 配置文件路径
     * @return 是否成功加载
     */
    public static boolean loadConfigFromFile(Path configPath) {
        if (!Files.exists(configPath)) {
            return false;
        }
        
        properties = new Properties();
        try (InputStream fileStream = Files.newInputStream(configPath)) {
            properties.load(fileStream);
            parseProperties();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void handleConfigFile() {
        try {
            // 获取config目录路径
            Path configDir = FMLPaths.CONFIGDIR.get();
            Path configFile = configDir.resolve(CONFIG_FILENAME);

            // 获取jar包内的配置文件
            InputStream resourceStream = ATMod.class.getResourceAsStream("/" + CONFIG_FILENAME);
            if (resourceStream == null) {
                LOGGER.error("无法从资源中加载配置文件：{}！", CONFIG_FILENAME);
                return;
            }

            // 读取jar包内配置文件的版本
            Properties jarProperties = new Properties();
            jarProperties.load(resourceStream);
            String jarVersion = jarProperties.getProperty("ConfigVersion");

            // 重新获取资源流，因为上面的操作已经消耗了流
            resourceStream = ATMod.class.getResourceAsStream("/" + CONFIG_FILENAME);
            if (resourceStream == null) {
                LOGGER.error("无法从资源中加载配置文件：{}！", CONFIG_FILENAME);
                return;
            }

            if (Files.exists(configFile)) {
                // 如果外部配置文件存在
                LOGGER.info("存在外部配置文件");

                // 读取外部配置文件的版本和覆盖选项
                Properties externalProperties = new Properties();
                try (InputStream externalStream = Files.newInputStream(configFile)) {
                    externalProperties.load(externalStream);
                }
                String externalVersion = externalProperties.getProperty("ConfigVersion");
                String overrideEnabledStr = externalProperties.getProperty("ConfigOverrideEnabled");
                int overrideEnabled = Integer.parseInt(overrideEnabledStr);

                // 检查版本是否匹配
                if (!jarVersion.equals(externalVersion)) {
                    // 版本不匹配
                    if (overrideEnabled == 1) {
                        // 覆盖选项启用，使用jar包内配置文件覆盖外部配置文件
                        LOGGER.info("配置文件版本（{}）与模组版本（{}）不匹配，正在使用内部配置文件覆盖", externalVersion, jarVersion);
                        Files.copy(resourceStream, configFile, StandardCopyOption.REPLACE_EXISTING);

                        // 重新加载配置
                        if (ATModFinal.loadConfigFromFile(configFile))
                            LOGGER.info("外部配置文件已成功加载");
                    } else {
                        // 覆盖选项禁用，仅加载外部配置并记录警告
                        LOGGER.warn("配置文件版本（{}）与当前模组版本（{}）不匹配，且覆盖选项被禁用。如果你因为该模组报错而看到这条信息的话，请检查你的配置文件版本是否与模组版本对应", externalVersion, jarVersion);
                        if (ATModFinal.loadConfigFromFile(configFile)) LOGGER.info("虽然版本不匹配，但仍然成功加载配置文件");}
                } else {
                    // 版本匹配，直接加载外部配置
                    if (!ATModFinal.loadConfigFromFile(configFile)) LOGGER.error("加载配置文件失败,请检查配置文件是否有缺失或错误的格式");
                }
            } else {
                // 如果外部配置文件不存在，则从jar包内复制到config目录
                LOGGER.info("不存在外部配置文件");
                Files.createDirectories(configDir); // 确保config目录存在
                Files.copy(resourceStream, configFile, StandardCopyOption.REPLACE_EXISTING);

                // 加载新创建的配置文件
                if (ATModFinal.loadConfigFromFile(configFile)) {
                    LOGGER.info("已更新外部配置文件");
                } else {
                    LOGGER.error("外部配置文件更新失败");
                }
            }
        } catch (IOException e) {
            LOGGER.error("处理配置文件时发生错误:", e);
        }
    }
    
    /**
     * 解析属性文件中的值到静态字段
     */
    private static void parseProperties() {
        ConfigVersion = properties.getProperty("ConfigVersion");
        ConfigOverrideEnabled = Integer.parseInt(properties.getProperty("ConfigOverrideEnabled"));
        
        NagaModify = Integer.parseInt(properties.getProperty("NagaSwitch"));
        LichModify = Integer.parseInt(properties.getProperty("LichModify"));
        MinoshroomModify = Integer.parseInt(properties.getProperty("MinoshroomModify"));
        HydraModify = Integer.parseInt(properties.getProperty("HydraModify"));
        KnightPhantomModify = Integer.parseInt(properties.getProperty("KnightPhantomModify"));
        UrGhastModify = Integer.parseInt(properties.getProperty("UrGhastModify"));
        AlphaYetiModify = Integer.parseInt(properties.getProperty("AlphaYetiModify"));
        SnowQueenModify = Integer.parseInt(properties.getProperty("SnowQueenModify"));
        NagaArmor = Integer.parseInt(properties.getProperty("NagaArmor"));
        NagaArmorDamage = Integer.parseInt(properties.getProperty("NagaArmorDamage"));
        NagaArmorDamageMin = Integer.parseInt(properties.getProperty("NagaArmorDamageMin"));
        NagaPushDamage = Integer.parseInt(properties.getProperty("NagaPushDamage"));
        NagaPush = Integer.parseInt(properties.getProperty("NagaPush"));
        NagaDrop = Integer.parseInt(properties.getProperty("NagaDrop"));
        NagaDaze = Integer.parseInt(properties.getProperty("NagaDaze"));
        NagaStop = Integer.parseInt(properties.getProperty("NagaStop"));
        NagaReduceDamage1 = Integer.parseInt(properties.getProperty("NagaReduceDamage1"));
        NagaReduceDamage2 = Integer.parseInt(properties.getProperty("NagaReduceDamage2"));
        LichObsidianSwitch = Integer.parseInt(properties.getProperty("LichObsidianSwitch"));
        LichObsidian = Integer.parseInt(properties.getProperty("LichObsidian"));
        LichObsidianHealth = Integer.parseInt(properties.getProperty("LichObsidianHealth"));
        MagicHealth = Integer.parseInt(properties.getProperty("MagicHealth"));
        MagicCD = Integer.parseInt(properties.getProperty("MagicCD"));
        ExtraArmor = Integer.parseInt(properties.getProperty("ExtraArmor"));
        ExtraTime = Integer.parseInt(properties.getProperty("ExtraTime"));
        MagicReduceDamage = Integer.parseInt(properties.getProperty("MagicReduceDamage"));
        MinionSummon = Integer.parseInt(properties.getProperty("MinionSummon"));
        MinionDamageLich = Integer.parseInt(properties.getProperty("MinionDamageLich"));
        MinionHealth = Integer.parseInt(properties.getProperty("MinionHealth"));
        MinionArmor = Integer.parseInt(properties.getProperty("MinionArmor"));
        MinionArmorToughness = Integer.parseInt(properties.getProperty("MinionArmorToughness"));
        MinionTreatLich = Integer.parseInt(properties.getProperty("MinionTreatLich"));
        LichMaxHealth = Integer.parseInt(properties.getProperty("LichMaxHealth"));
        LichArmor = Integer.parseInt(properties.getProperty("LichArmor"));
        LichArmorToughness = Integer.parseInt(properties.getProperty("LichArmorToughness"));
        MinoshroomDuration = Integer.parseInt(properties.getProperty("MinoshroomDuration"));
        MinoshroomAmplifier = Integer.parseInt(properties.getProperty("MinoshroomAmplifier"));
        MinoshroomReduceDamage = Integer.parseInt(properties.getProperty("MinoshroomReduceDamage"));
        MinoshroomAddDamage = Integer.parseInt(properties.getProperty("MinoshroomAddDamage"));
        MinoshroomHealthThreshold = Integer.parseInt(properties.getProperty("MinoshroomHealthThreshold"));
        MinoshroomHealth = Integer.parseInt(properties.getProperty("MinoshroomHealth"));
        MinoshroomArmor = Integer.parseInt(properties.getProperty("MinoshroomArmor"));
        MinoshroomArmorToughness = Integer.parseInt(properties.getProperty("MinoshroomArmorToughness"));
        MinoshroomTrackingMaxTime = Integer.parseInt(properties.getProperty("MinoshroomTrackingMaxTime"));
        MinoshroomAttackMultiplier = Integer.parseInt(properties.getProperty("MinoshroomAttackMultiplier"));
        MinoshroomSpeedMultiplier = Integer.parseInt(properties.getProperty("MinoshroomSpeedMultiplier"));
        MinoshroomBuffLossPercent = Integer.parseInt(properties.getProperty("MinoshroomBuffLossPercent"));
        MinoshroomTargetSwitchCooldown = Integer.parseInt(properties.getProperty("MinoshroomTargetSwitchCooldown"));
        MinoshroomAngry = Integer.parseInt(properties.getProperty("MinoshroomAngry"));
        MinoshroomChargeTimeReduction = Integer.parseInt(properties.getProperty("MinoshroomChargeTimeReduction"));
        MinoshroomWarcryBreakBlocks = Integer.parseInt(properties.getProperty("MinoshroomWarcryBreakBlocks"));
        MinoshroomBreakBlocksRadius = Integer.parseInt(properties.getProperty("MinoshroomBreakBlocksRadius"));
        MinoshroomBreakBlocksHeight = Integer.parseInt(properties.getProperty("MinoshroomBreakBlocksHeight"));
        HydraBomb = Integer.parseInt(properties.getProperty("HydraBomb"));
        HydraAddDamage = Integer.parseInt(properties.getProperty("HydraAddDamage"));
        HydraHead = Integer.parseInt(properties.getProperty("HydraHead"));
        KnightTreat = Integer.parseInt(properties.getProperty("KnightTreat"));
        KnightDamage = Integer.parseInt(properties.getProperty("KnightDamage"));
        KnightDuration = Integer.parseInt(properties.getProperty("KnightDuration"));
        KnightAmplifier = Integer.parseInt(properties.getProperty("KnightAmplifier"));
        KnightBox = Float.parseFloat(properties.getProperty("KnightBox"));
        UrGhastAddDamage = Integer.parseInt(properties.getProperty("UrGhastAddDamage"));
        UrGhastDamageMin = Integer.parseInt(properties.getProperty("UrGhastDamageMin"));
        UrGhastBomb = Integer.parseInt(properties.getProperty("UrGhastBomb"));
        YetiSnowBox = Integer.parseInt(properties.getProperty("YetiSnowBox"));
        YetiIce = Integer.parseInt(properties.getProperty("YetiIce"));
        SnowQueenTrapCD = Integer.parseInt(properties.getProperty("SnowQueenTrapCD"));
        SnowQueenTrapCDCD = Double.parseDouble(properties.getProperty("SnowQueenTrapCDCD"));
        SnowQueenTrapCount_1 = Integer.parseInt(properties.getProperty("SnowQueenTrapCount_1"));
        SnowQueenTrapCount_2 = Integer.parseInt(properties.getProperty("SnowQueenTrapCount_2"));
        SnowQueenHealthThreshold = Integer.parseInt(properties.getProperty("SnowQueenHealthThreshold"));
        SnowQueenIceCD = Integer.parseInt(properties.getProperty("SnowQueenIceCD"));
        SnowQueenInvincible = Integer.parseInt(properties.getProperty("SnowQueenInvincible"));
        SnowQueenTreat = Integer.parseInt(properties.getProperty("SnowQueenTreat"));
        SnowQueenIceDuration = Integer.parseInt(properties.getProperty("SnowQueenIceDuration"));
        SnowQueenDamage = Integer.parseInt(properties.getProperty("SnowQueenDamage"));
        SnowQueenExplosionDamage = Float.parseFloat(properties.getProperty("SnowQueenExplosionDamage"));
        SnowQueenIceArmor = Integer.parseInt(properties.getProperty("SnowQueenIceArmor"));
        SnowTrapScale = Integer.parseInt(properties.getProperty("SnowTrapScale"));
    }
}
