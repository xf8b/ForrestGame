/*
 * Copyright (c) 2021 ilja615.
 *
 * This file is part of Forrest Game.
 *
 * Forrest Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Forrest Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Forrest Game.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.ilja615.forrestgame.gui.texture;

import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class PngTexture implements Texture
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PngTexture.class);
    private final int id;
    private final boolean isTall;

    public PngTexture(final String fileName)
    {
        this(fileName, false);
    }

    public PngTexture(final String fileName, final boolean isTallTexture)
    {
        final IntBuffer width = BufferUtils.createIntBuffer(1);
        final IntBuffer height = BufferUtils.createIntBuffer(1);
        final IntBuffer comp = BufferUtils.createIntBuffer(1);

        this.isTall = isTallTexture;
        try
        {
            final Path textureFile = Paths.get(System.getProperty("java.io.tmpdir"), "forrestgame").resolve(fileName + ".png");

            if (Files.notExists(textureFile))
            {
                final Path forrestGameFolder = textureFile.getParent();

                if (Files.notExists(forrestGameFolder))
                {
                    LOGGER.debug("Creating cached textures folder ({})", forrestGameFolder.toString());
                }
                Files.createDirectories(forrestGameFolder);

                try (final InputStream inputStream = Objects.requireNonNull(Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(fileName + ".png")))
                {
                    Files.write(textureFile, inputStream.readAllBytes(), CREATE_NEW);
                    LOGGER.debug("Creating cached texture file ({})", textureFile.toString());
                }
            }

            final ByteBuffer data = stbi_load(
                    textureFile.toString(),
                    width,
                    height,
                    comp,
                    4
            );

            LOGGER.debug("Loaded texture file {}", textureFile.toString());

            this.id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            stbi_image_free(Objects.requireNonNull(data));
        } catch (final IOException exception)
        {
            LOGGER.error("Could not read textures", exception);
            System.exit(1);
            throw new IllegalStateException("how");
        }
    }

    @Override
    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public boolean getIsTall()
    {
        return this.isTall;
    }
}
