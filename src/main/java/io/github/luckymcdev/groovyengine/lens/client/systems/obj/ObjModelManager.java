package io.github.luckymcdev.groovyengine.lens.client.systems.obj;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class ObjModelManager {
    public static List<ObjModel> OBJ_MODELS = new ArrayList<>();


    /**
     * Registers an OBJ model instance to be loaded after the client setup event.
     * @param objModel The OBJ model instance to register.
     * @return The registered OBJ model instance.
     */
    public static ObjModel registerObjModel(ObjModel objModel) {
        OBJ_MODELS.add(objModel);
        return objModel;
    }

    /**
     * Loads all registered OBJ models.
     * This method is called automatically after the client setup event.
     */
    public static void loadModels() {
        OBJ_MODELS.forEach(ObjModel::loadModel);
    }

    /**
     * Loads all registered OBJ models on the client setup event.
     * This method is automatically called after the client setup event.
     *
     * @param event The client setup event.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientSetup(FMLClientSetupEvent event) {
        loadModels();
    }
}