package org.TNTStudios.fakenameportfabric.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.TNTStudios.fakenameportfabric.FakeName;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakeNameStorage {
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<ConcurrentHashMap<UUID, String>>() {}.getType();
    private static final String FILE_NAME = "fakename_data.json";
    private static File dataFile;
    private static ConcurrentHashMap<UUID, String> fakeNames = new ConcurrentHashMap<>();

    /**
     * Inicializa el almacenamiento estableciendo la ubicación del archivo y cargando datos existentes.
     *
     * @param server El servidor de Minecraft.
     */
    public static void initialize(MinecraftServer server) {
        File serverDirectory = server.getRunDirectory();
        dataFile = new File(serverDirectory, FILE_NAME);
        load();
    }

    /**
     * Carga los nombres falsos desde el archivo JSON.
     */
    private static void load() {
        if (!dataFile.exists()) {
            return; // No hay datos para cargar
        }

        try (FileReader reader = new FileReader(dataFile)) {
            fakeNames = GSON.fromJson(reader, MAP_TYPE);
            if (fakeNames == null) {
                fakeNames = new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda los nombres falsos en el archivo JSON.
     */
    public static void save() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            GSON.toJson(fakeNames, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el nombre falso de un jugador y guarda los cambios.
     *
     * @param player    El jugador cuyo nombre falso se está estableciendo.
     * @param fakeName  El nuevo nombre falso.
     */
    public static void setFakeName(ServerPlayerEntity player, String fakeName) {
        fakeNames.put(player.getUuid(), fakeName);
        FakeName.setFakeName(player, fakeName);
        save();
    }

    /**
     * Obtiene el nombre falso de un jugador.
     *
     * @param player El jugador cuyo nombre falso se está obteniendo.
     * @return El nombre falso o el nombre real si no hay uno establecido.
     */
    public static String getFakeName(ServerPlayerEntity player) {
        return fakeNames.getOrDefault(player.getUuid(), player.getEntityName());
    }

    /**
     * Elimina el nombre falso de un jugador y guarda los cambios.
     *
     * @param player El jugador cuyo nombre falso se está eliminando.
     */
    public static void clearFakeName(ServerPlayerEntity player) {
        fakeNames.remove(player.getUuid());
        FakeName.setFakeName(player, player.getEntityName());
        save();
    }
}
