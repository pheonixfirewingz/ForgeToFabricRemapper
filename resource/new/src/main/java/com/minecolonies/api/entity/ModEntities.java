package com.minecolonies.api.entity;


@ObjectHolder(Constants.MOD_ID)
{
    @ObjectHolder("citizen")
    public static EntityType<? extends AbstractEntityCitizen> CITIZEN;

    @ObjectHolder("visitor")
    public static EntityType<? extends AbstractEntityCitizen> VISITOR;

    @ObjectHolder("fishhook")
    public static EntityType<? extends Entity> FISHHOOK;

    @ObjectHolder("barbarian")
    public static EntityType<? extends AbstractEntityBarbarian> BARBARIAN;

    @ObjectHolder("mercenary")
    public static EntityType<? extends CreatureEntity> MERCENARY;

    @ObjectHolder("archerbarbarian")
    public static EntityType<? extends AbstractEntityBarbarian> ARCHERBARBARIAN;

    @ObjectHolder("chiefbarbarian")
    public static EntityType<? extends AbstractEntityBarbarian> CHIEFBARBARIAN;

    @ObjectHolder("pirate")
    public static EntityType<? extends AbstractEntityPirate> PIRATE;

    @ObjectHolder("chiefpirate")
    public static EntityType<? extends AbstractEntityPirate> CHIEFPIRATE;

    @ObjectHolder("archerpirate")
    public static EntityType<? extends AbstractEntityPirate> ARCHERPIRATE;

    @ObjectHolder("sittingentity")
    public static EntityType<? extends Entity> SITTINGENTITY;

    @ObjectHolder("mummy")
    public static EntityType<? extends AbstractEntityEgyptian> MUMMY;

    @ObjectHolder("pharao")
    public static EntityType<? extends AbstractEntityEgyptian> PHARAO;

    @ObjectHolder("archermummy")
    public static EntityType<? extends AbstractEntityEgyptian> ARCHERMUMMY;

    @ObjectHolder("norsemenarcher")
    public static EntityType<? extends AbstractEntityNorsemen> NORSEMEN_ARCHER;

    @ObjectHolder("shieldmaiden")
    public static EntityType<? extends AbstractEntityNorsemen> SHIELDMAIDEN;

    @ObjectHolder("norsemenchief")
    public static EntityType<? extends AbstractEntityNorsemen> NORSEMEN_CHIEF;

    @ObjectHolder("amazon")
    public static EntityType<? extends AbstractEntityAmazon> AMAZON;

    @ObjectHolder("amazonchief")
    public static EntityType<? extends AbstractEntityAmazon> AMAZONCHIEF;

    @ObjectHolder("minecart")
    public static EntityType<MinecoloniesMinecart> MINECART;

    @ObjectHolder("firearrow")
    public static EntityType<? extends AbstractArrowEntity> FIREARROW;

    public static EntityType<? extends ArrowEntity> MC_NORMAL_ARROW;
}
