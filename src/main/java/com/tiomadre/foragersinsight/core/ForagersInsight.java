package com.tiomadre.foragersinsight.core;

import com.tiomadre.foragersinsight.data.server.FIAdvancementData;
import com.tiomadre.foragersinsight.data.server.recipes.FIDiffusingRecipes;
import com.tiomadre.foragersinsight.core.registry.FIEnchantments;
import com.tiomadre.foragersinsight.core.other.FIClientCompat;
import com.tiomadre.foragersinsight.core.registry.FICompostableProvider;
import com.tiomadre.foragersinsight.core.registry.*;
import com.tiomadre.foragersinsight.data.client.FIBlockStates;
import com.tiomadre.foragersinsight.data.client.FIItemModels;
import com.tiomadre.foragersinsight.data.client.FIParticles;
import com.tiomadre.foragersinsight.core.registry.FILoot;
import com.tiomadre.foragersinsight.data.server.FIWorldgen;
import com.tiomadre.foragersinsight.data.server.recipes.FICraftingRecipes;
import com.tiomadre.foragersinsight.data.server.tags.FIBiomeTags;
import com.tiomadre.foragersinsight.data.server.tags.FIBlockTags;
import com.tiomadre.foragersinsight.data.server.tags.FIItemTags;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(ForagersInsight.MOD_ID)
public class ForagersInsight {
	public static final String MOD_ID = "foragersinsight";

	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

	public ForagersInsight(IEventBus modEventBus, ModContainer modContainer) {
		NeoForge.EVENT_BUS.register(this);

		REGISTRY_HELPER.register(modEventBus);
		FIBoatTypes.register();
		FIAdvancementCriteria.register();
		FIWoodTypes.register();
		FIEnchantments.register(modEventBus);
		FIBiomeFeatures.register(modEventBus);
		FIBlockEntityTypes.register(modEventBus);
		FIFoliagePlacerType.FOLIAGE_PLACER_TYPE.register(modEventBus);
		FILootModifiers.LOOT_MODIFIERS.register(modEventBus);
		FIMenuTypes.MENUS.register(modEventBus);
		FIMobEffects.MOB_EFFECTS.register(modEventBus);
		FIParticleTypes.register(modEventBus);
		FITabs.TABS.register(modEventBus);
		FITreeDecoratorTypes.TREE_DECORATOR_TYPES.register(modEventBus);
		FIRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		FITappables.bootstrap();


		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::dataSetup);

		modContainer.registerConfig(ModConfig.Type.COMMON, FIConfig.COMMON_SPEC);
	}

	public static ResourceLocation rl(String namespace) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, namespace);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {

			FIDiffusingRecipes.bootstrap();
		});
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(FIClientCompat::registerCompat);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();

		boolean server = event.includeServer();
		FIBlockTags blockTags = new FIBlockTags(event);
		gen.addProvider(server, blockTags);
		gen.addProvider(server, new FIBiomeTags(event));
		gen.addProvider(server, new FIItemTags(event, blockTags));
		gen.addProvider(server, new FILoot(event));
		gen.addProvider(server, new FICraftingRecipes(event.getGenerator().getPackOutput() ,event.getLookupProvider()));
		gen.addProvider(server, new FIWorldgen(event));
		gen.addProvider(server, new FIAdvancementData(event));
		gen.addProvider(server, new FICompostableProvider(gen.getPackOutput(), event.getLookupProvider()));


		boolean client = event.includeClient();
		gen.addProvider(client, new FIBlockStates(event));
		gen.addProvider(client, new FIItemModels(event));
		gen.addProvider(client, new FIParticles(event));
	}
}
