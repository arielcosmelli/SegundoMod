package org.bladervg.utils;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.adapters.MoveTemplateAdapter;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.adapters.ElementalTypeAdapter;
import com.cobblemon.mod.common.util.adapters.NbtCompoundAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.mojang.authlib.GameProfile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.bladervg.SegundoMod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class Utils {
    public static final Random RANDOM = new Random();
    private static final Charset charset = StandardCharsets.UTF_8;
    private static Gson gsonPretty = null;
    private static Gson gsonnotPretty = null;


    public static Gson newGson() {
        if (gsonPretty == null) {
            gsonPretty = adapters()
                    .setPrettyPrinting()
                    .create();
        }
        return gsonPretty;
    }

    private static GsonBuilder adapters() {
        return addAdapters(new GsonBuilder()
                .disableHtmlEscaping());
    }

    public static Gson newWithoutSpacingGson() {
        if (gsonnotPretty == null) {
            gsonnotPretty = adapters()
                    .create();
        }
        return gsonnotPretty;
    }

    private static GsonBuilder addAdapters(GsonBuilder builder) {
        return builder
                .registerTypeAdapter(ElementalType.class, ElementalTypeAdapter.INSTANCE)
                .registerTypeAdapter(NbtCompound.class, NbtCompoundAdapter.INSTANCE)
                .registerTypeAdapter(Move.class, MoveTemplateAdapter.INSTANCE)
                .registerTypeAdapter(NbtCompoundAdapter.class, NbtCompoundAdapter.INSTANCE)
                .registerTypeAdapter(DateTypeAdapter.class, new DateTypeAdapter());
    }

    public static CompletableFuture<Boolean> writeFileAsync(String filePath, String filename, String data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
        File file = path.toFile();

        if (!Files.exists(path.getParent())) {
            file.getParentFile().mkdirs();
        }

        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(charset));

            fileChannel.write(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    attachment.clear();
                    try {
                        fileChannel.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    future.complete(true);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.complete(writeFileSync(file, data));
                }
            });
        } catch (IOException | SecurityException e) {
            SegundoMod.LOGGER.error("Unable to write file asynchronously, attempting sync write.");
            future.complete(future.complete(false));
            e.printStackTrace();
        }

        return future;
    }

    public static boolean writeFileSync(File file, String data) {
        try (FileWriter writer = new FileWriter(file, charset)) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CompletableFuture<Boolean> readFileAsync(String filePath, String filename, Consumer<String> callback) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
        File file = path.toFile();

        if (!file.exists()) {
            future.complete(false);
            executor.shutdown();
            return future;
        }

        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());

            Future<Integer> readResult = fileChannel.read(buffer, 0);
            readResult.get();
            buffer.flip();

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String fileContent = new String(bytes, charset);

            callback.accept(fileContent);

            fileChannel.close();
            executor.shutdown();
            future.complete(true);
        } catch (JsonSyntaxException e) {
            SegundoMod.LOGGER.error("Malformed JSON in file " + file.getAbsolutePath() + " - " + e.getMessage());
            future.complete(false);
            executor.shutdown();
            e.printStackTrace();
        } catch (Exception e) {
            future.complete(readFileSync(file, callback));
            executor.shutdown();
            e.printStackTrace();
        }

        return future;
    }

    public static boolean readFileSync(File file, Consumer<String> callback) {
        try (Scanner reader = new Scanner(file, charset)) {
            StringBuilder data = new StringBuilder();
            while (reader.hasNextLine()) {
                data.append(reader.nextLine());
            }
            callback.accept(data.toString());
            return true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readFileSync(File file) throws IOException {
        // Verifica si el archivo existe y es un archivo regular
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("El archivo no existe o no es un archivo regular: " + file.getPath());
        }
        return Files.readString(Path.of(file.getPath()), charset);
    }

    public static CompletableFuture<Void> writeFileAsync(File file, String content) {
        return CompletableFuture.runAsync(() -> {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al escribir el archivo: " + file.getPath(), e);
            }
        });
    }

    public static ItemStack parseItemId(String id) {
        return parseItemId(id, 1);
    }

    public static ItemStack parseItemId(String id, int amount) {
        ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(id)), amount);
        return itemStack;
    }

    public static File getAbsolutePath(String directoryPath) {
        return new File(Paths.get(new File("").getAbsolutePath()) + directoryPath);
    }

    public static void removeFiles(String directoryPath) {
        File directory = getAbsolutePath(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    } else if (file.isDirectory()) {
                        removeFiles(file.getAbsolutePath());
                    }
                }
            }
        } else {
            SegundoMod.LOGGER.info("Directory " + directoryPath + " does not exist or is not a directory.");
        }
    }

    public static void createDirectoryIfNeeded(String directoryPath) {
        File directory = getAbsolutePath(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                SegundoMod.LOGGER.info("Created directory: " + directoryPath);
            } else {
                SegundoMod.LOGGER.error("Failed to create directory: " + directoryPath);
            }
        }
    }

    public static ItemStack getHead(String replace, int amount) {
        ItemStack itemStack = Items.PLAYER_HEAD.getDefaultStack();
        var profile = new GameProfile(UUID.randomUUID(), replace);
        itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));
        itemStack.setCount(amount);
        return itemStack;
    }

    public static ItemStack parseItemId(String item, int amount, long customModelData) {
        ItemStack itemStack = parseItemId(item, amount);
        if (customModelData != 0)
            itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent((int) customModelData));
        return itemStack;
    }


}