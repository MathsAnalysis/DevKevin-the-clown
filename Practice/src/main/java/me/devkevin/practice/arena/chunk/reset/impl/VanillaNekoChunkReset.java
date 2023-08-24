package me.devkevin.practice.arena.chunk.reset.impl;

import lombok.SneakyThrows;
import me.devkevin.practice.arena.chunk.reset.INekoChunkReset;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class VanillaNekoChunkReset implements INekoChunkReset {
    @SneakyThrows
    public void setSections(Chunk nmsChunk, ChunkSection[] sections) {
        setField("sections", nmsChunk, sections);

        nmsChunk.getWorld().getWorld().refreshChunk(nmsChunk.locX, nmsChunk.locZ);
    }

    public ChunkSection[] cloneSections(ChunkSection[] sections) {
        ChunkSection[] newSections = new ChunkSection[sections.length];

        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] != null) {
                newSections[i] = cloneSection(sections[i]);
            }
        }

        return newSections;
    }

    @SneakyThrows
    public ChunkSection cloneSection(ChunkSection chunkSection) {
        ChunkSection section = new ChunkSection(chunkSection.getYPosition(), chunkSection.getSkyLightArray() != null);

        setField("nonEmptyBlockCount", section, getFromField("nonEmptyBlockCount", chunkSection));
        setField("tickingBlockCount", section, getFromField("tickingBlockCount", chunkSection));
        setField("blockIds", section, chunkSection.getIdArray().clone());
        if (chunkSection.getEmittedLightArray() != null) {
            section.a(cloneNibbleArray(chunkSection.getEmittedLightArray()));
        }

        if (chunkSection.getSkyLightArray() != null) {
            section.b(cloneNibbleArray(chunkSection.getSkyLightArray()));
        }

        return section;
    }

    public NibbleArray cloneNibbleArray(NibbleArray nibbleArray) {
        return new NibbleArray(nibbleArray.a().clone());
    }

    @SneakyThrows
    public void setField(String fieldName, Object clazz, Object value) {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(clazz, value);
    }

    public Object getFromField(String fieldName, Object clazz) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field.get(clazz);
    }
}
