package com.tiomadre.foragersinsight.core.registry;

import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import com.tiomadre.foragersinsight.common.gui.HandbasketMenu;
import com.tiomadre.foragersinsight.core.ForagersInsight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;


public class FIMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ForagersInsight.MOD_ID);

    public static final RegistryObject<MenuType<HandbasketMenu>> HANDBASKET_MENU =
            MENUS.register("handbasket",
                    () -> IForgeMenuType.create(HandbasketMenu::new));
    public static final RegistryObject<MenuType<DiffuserMenu>> DIFFUSER_MENU =
            MENUS.register("diffuser",
                    () -> IForgeMenuType.create(DiffuserMenu::new));
}
