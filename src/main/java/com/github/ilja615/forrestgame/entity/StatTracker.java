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

package com.github.ilja615.forrestgame.entity;

import com.github.ilja615.forrestgame.Game;
import com.github.ilja615.forrestgame.entity.StatTracker.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tracks the {@link Stat}s of a {@link Entity}.
 */
public class StatTracker extends AbstractMap<Stat, Integer>
{
    private final Game game;
    private final Map<Stat, Integer> delegate;

    public StatTracker(final Game game, final Map<Stat, Integer> stats)
    {
        this.game = game;
        this.delegate = new HashMap<>(stats);
    }

    @Override
    public Integer get(final Object key)
    {
        return delegate.get(key);
    }

    @Override
    public Integer replace(final Stat key, final Integer value)
    {
        if (value <= 0)
        {
            switch (key)
            {
                case HEALTH -> game.end(Game.EndReason.DIED);
                case HUNGER -> game.end(Game.EndReason.NO_ENERGY);
            }

            return delegate.get(key);
        } else if (value > key.getLimit())
        {
            return delegate.get(key);
        } else
        {
            return delegate.replace(key, value);
        }
    }

    public void decrement(final Stat key)
    {
        this.replace(key, this.get(key) - 1);
    }

    public void increment(final Stat key)
    {
        this.replace(key, this.get(key) + 1);
    }

    @NotNull
    @Override
    public Set<Entry<Stat, Integer>> entrySet()
    {
        return delegate.entrySet();
    }

    public enum Stat
    {
        HEALTH(10),
        HUNGER(10);

        private final int limit;

        Stat(final int limit)
        {
            this.limit = limit;
        }

        public int getLimit()
        {
            return limit;
        }
    }
}
