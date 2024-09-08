package com.lemg.masi.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MapPersistence {
    // 序列化到文件
    public static void savePacksToFile(ConcurrentHashMap<String,List<Integer>> uuidPos, Path filePath) throws IOException {
        OutputStream fileOut = Files.newOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(uuidPos);
        out.close();
        fileOut.close();
    }

    // 从文件反序列化
    public static ConcurrentHashMap<String,List<Integer>> loadPacksFromFile(Path filePath) throws IOException, ClassNotFoundException {
        InputStream in = Files.newInputStream(filePath);
        ObjectInputStream objectIn = new ObjectInputStream(in);
        ConcurrentHashMap<String,List<Integer>> map = (ConcurrentHashMap<String,List<Integer>>) objectIn.readObject();
        in.close();
        objectIn.close();
        return map;
    }
}
