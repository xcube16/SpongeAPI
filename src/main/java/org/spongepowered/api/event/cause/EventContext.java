/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.event.cause;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.api.util.ResettableBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import javax.annotation.Nullable;

/**
 * Provides context for an event outside of the direct chain of causes present
 * in the event's {@link Cause}.
 */
public final class EventContext {

    private static final EventContext EMPTY_CONTEXT = new EventContext(ImmutableMap.of());

    public static EventContext empty() {
        return EMPTY_CONTEXT;
    }

    public static EventContext of(Map<String, Object> entries) {
        checkNotNull(entries, "Context entries cannot be null");
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            checkNotNull(entry.getValue(), "Entries cannot contain null values");
        }
        return new EventContext(entries);
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, Object> entries;

    EventContext(Map<String, Object> values) {
        this.entries = ImmutableMap.copyOf(values);
    }

    public Optional<?> get(String name) {
        checkNotNull(name, "Name cannot be null");
        return Optional.ofNullable(this.entries.get(name));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String name, Class<T> expectedType) {
        checkNotNull(name, "Name cannot be null");
        checkNotNull(expectedType, "Expected type cannot be null");
        Object val = this.entries.get(name);
        if (val == null || !expectedType.isInstance(val)) {
            return Optional.empty();
        }
        return Optional.of((T) val);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof EventContext)) {
            return false;
        }
        EventContext ctx = (EventContext) object;
        for (Map.Entry<String, Object> entry : this.entries.entrySet()) {
            Object other = ctx.entries.get(entry.getKey());
            if (other == null) {
                return false;
            }
            if (!entry.getValue().equals(other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.entries.hashCode();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Object> entry : this.entries.entrySet()) {
            joiner.add("{Name=" + entry.getKey() + ", Object={" + entry.getValue().toString() + "}}");
        }
        return "Context[" + joiner.toString() + "]";
    }

    public static final class Builder implements ResettableBuilder<EventContext, Builder> {

        private final Map<String, Object> entries = Maps.newHashMap();

        Builder() {

        }

        public Builder add(String name, Object context) {
            checkNotNull(context, "Context object cannot be null");
            checkArgument(!this.entries.containsKey(name), "Duplicate context value name");
            this.entries.put(name, context);
            return this;
        }

        @Override
        public Builder from(EventContext value) {
            this.entries.putAll(value.entries);
            return this;
        }

        @Override
        public Builder reset() {
            this.entries.clear();
            return this;
        }

        public EventContext build() {
            return new EventContext(this.entries);
        }

    }

}
