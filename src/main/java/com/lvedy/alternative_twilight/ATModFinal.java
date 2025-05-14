package com.lvedy.alternative_twilight;

import java.io.IOException;
import java.util.Properties;

public class ATModFinal {
    public static Properties properties;
    static {
        properties = new Properties();
        try {
            properties.load(ATMod.class.getResourceAsStream("/ATModConfig.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static final int NagaModify = Integer.parseInt(properties.getProperty("NagaSwitch"));
    public static final int LichModify = Integer.parseInt(properties.getProperty("LichModify"));
    public static final int MinoshroomModify = Integer.parseInt(properties.getProperty("MinoshroomModify"));
    public static final int HydraModify = Integer.parseInt(properties.getProperty("HydraModify"));
    public static final int KnightPhantomModify = Integer.parseInt(properties.getProperty("KnightPhantomModify"));
    public static final int UrGhastModify = Integer.parseInt(properties.getProperty("UrGhastModify"));
    public static final int AlphaYetiModify = Integer.parseInt(properties.getProperty("AlphaYetiModify"));
    public static final int SnowQueenModify = Integer.parseInt(properties.getProperty("SnowQueenModify"));
    public static final int NagaArmor = Integer.parseInt(properties.getProperty("NagaArmor"));
    public static final int NagaArmorDamage = Integer.parseInt(properties.getProperty("NagaArmorDamage"));
    public static final int NagaArmorDamageMin = Integer.parseInt(properties.getProperty("NagaArmorDamageMin"));
    public static final int NagaPushDamage = Integer.parseInt(properties.getProperty("NagaPushDamage"));
    public static final int NagaPush = Integer.parseInt(properties.getProperty("NagaPush"));
    public static final int NagaDrop = Integer.parseInt(properties.getProperty("NagaDrop"));
    public static final int NagaDaze = Integer.parseInt(properties.getProperty("NagaDaze"));
    public static final int NagaStop = Integer.parseInt(properties.getProperty("NagaStop"));
    public static final int NagaReduceDamage1 = Integer.parseInt(properties.getProperty("NagaReduceDamage1"));
    public static final int NagaReduceDamage2 = Integer.parseInt(properties.getProperty("NagaReduceDamage2"));
    public static final int LichObsidianSwitch = Integer.parseInt(properties.getProperty("LichObsidianSwitch"));
    public static final int LichObsidian = Integer.parseInt(properties.getProperty("LichObsidian"));
    public static final int LichObsidianHealth = Integer.parseInt(properties.getProperty("LichObsidianHealth"));
    public static final int MagicHealth = Integer.parseInt(properties.getProperty("MagicHealth"));
    public static final int MagicCD = Integer.parseInt(properties.getProperty("MagicCD"));
    public static final int ExtraArmor = Integer.parseInt(properties.getProperty("ExtraArmor"));
    public static final int ExtraTime = Integer.parseInt(properties.getProperty("ExtraTime"));
    public static final int MagicReduceDamage = Integer.parseInt(properties.getProperty("MagicReduceDamage"));
    public static final int MinionSummon = Integer.parseInt(properties.getProperty("MinionSummon"));
    public static final int MinionDamageLich = Integer.parseInt(properties.getProperty("MinionDamageLich"));
    public static final int MinionHealth = Integer.parseInt(properties.getProperty("MinionHealth"));
    public static final int MinionArmor = Integer.parseInt(properties.getProperty("MinionArmor"));
    public static final int MinionArmorToughness = Integer.parseInt(properties.getProperty("MinionArmorToughness"));
    public static final int MinionTreatLich = Integer.parseInt(properties.getProperty("MinionTreatLich"));
    public static final int LichMaxHealth = Integer.parseInt(properties.getProperty("LichMaxHealth"));
    public static final int LichArmor = Integer.parseInt(properties.getProperty("LichArmor"));
    public static final int LichArmorToughness = Integer.parseInt(properties.getProperty("LichArmorToughness"));
    public static final int MinoshroomDuration = Integer.parseInt(properties.getProperty("MinoshroomDuration"));
    public static final int MinoshroomAmplifier = Integer.parseInt(properties.getProperty("MinoshroomAmplifier"));
    public static final int MinoshroomReduceDamage = Integer.parseInt(properties.getProperty("MinoshroomReduceDamage"));
    public static final int MinoshroomAddDamage = Integer.parseInt(properties.getProperty("MinoshroomAddDamage"));
    public static final int MinoshroomHealthThreshold = Integer.parseInt(properties.getProperty("MinoshroomHealthThreshold"));
    public static final int MinoshroomHealth = Integer.parseInt(properties.getProperty("MinoshroomHealth"));
    public static final int MinoshroomArmor = Integer.parseInt(properties.getProperty("MinoshroomArmor"));
    public static final int MinoshroomArmorToughness = Integer.parseInt(properties.getProperty("MinoshroomArmorToughness"));
    public static final int HydraBomb = Integer.parseInt(properties.getProperty("HydraBomb"));
    public static final int KnightTreat = Integer.parseInt(properties.getProperty("KnightTreat"));
    public static final int KnightDamage = Integer.parseInt(properties.getProperty("KnightDamage"));
    public static final int KnightDuration = Integer.parseInt(properties.getProperty("KnightDuration"));
    public static final int KnightAmplifier = Integer.parseInt(properties.getProperty("KnightAmplifier"));
    public static final float KnightBox = Float.parseFloat(properties.getProperty("KnightBox"));
    public static final int UrGhastAddDamage = Integer.parseInt(properties.getProperty("UrGhastAddDamage"));
    public static final int UrGhastDamageMin = Integer.parseInt(properties.getProperty("UrGhastDamageMin"));
    public static final int UrGhastBomb = Integer.parseInt(properties.getProperty("UrGhastBomb"));
    public static final int YetiSnowBox = Integer.parseInt(properties.getProperty("YetiSnowBox"));
    public static final int YetiIce = Integer.parseInt(properties.getProperty("YetiIce"));
    public static final int SnowQueenTrapCD = Integer.parseInt(properties.getProperty("SnowQueenTrapCD"));
    public static final double SnowQueenTrapCDCD = Double.parseDouble(properties.getProperty("SnowQueenTrapCDCD"));
    public static final int SnowQueenTrapCount_1 = Integer.parseInt(properties.getProperty("SnowQueenTrapCount_1"));
    public static final int SnowQueenTrapCount_2 = Integer.parseInt(properties.getProperty("SnowQueenTrapCount_2"));
    public static final int SnowQueenHealthThreshold = Integer.parseInt(properties.getProperty("SnowQueenHealthThreshold"));
    public static final int SnowQueenIceCD = Integer.parseInt(properties.getProperty("SnowQueenIceCD"));
    public static final int SnowQueenInvincible = Integer.parseInt(properties.getProperty("SnowQueenInvincible"));
    public static final int SnowQueenTreat = Integer.parseInt(properties.getProperty("SnowQueenTreat"));
    public static final int SnowQueenIceDuration = Integer.parseInt(properties.getProperty("SnowQueenIceDuration"));
    public static final int SnowQueenDamage = Integer.parseInt(properties.getProperty("SnowQueenDamage"));
    public static final float SnowQueenExplosionDamage = Float.parseFloat(properties.getProperty("SnowQueenExplosionDamage"));
    public static final int SnowQueenIceArmor = Integer.parseInt(properties.getProperty("SnowQueenIceArmor"));
    public static final int SnowTrapScale = Integer.parseInt(properties.getProperty("SnowTrapScale"));
}
