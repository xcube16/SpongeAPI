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
package org.spongepowered.api.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class InheritableNode implements CommentedConfigurationNode {
    private final CommentedConfigurationNode world;
    private final CommentedConfigurationNode dimension;
    private final CommentedConfigurationNode global; // These would have to be cached somehow so that all world configs share a dimension and all share a global -- config reference

    public InheritableNode(CommentedConfigurationNode world, CommentedConfigurationNode dimension,
        CommentedConfigurationNode global) {
        this.world = world;
        this.dimension = dimension;
        this.global = global;
    }

    @Override
    public Object getValue(Object def) {
        Object ret = this.world.getValue();
        if (ret == null) {
            ret = this.dimension.getValue();
        }
        if (ret == null) {
            ret = this.global.getValue(def);
        }
        return ret;
    }

    @Override
    public Object getValue(Supplier<Object> defSupplier) {
        Object value = this.world.getValue(defSupplier);
        if (value == null) {
            value = this.dimension.getValue(defSupplier);
        }
        if (value == null) {
            value = this.global.getValue(defSupplier);
        }
        return value;
    }

    @Override
    public <T> T getValue(Function<Object, T> transformer, T def) {
        T value = this.world.getValue(transformer);
        if (value == null) {
            value = this.dimension.getValue(transformer);
        }
        if (value == null) {
            value = this.global.getValue(transformer, def);
        }
        return value;
    }

    @Override
    public <T> T getValue(Function<Object, T> transformer, Supplier<T> defSupplier) {
        T value = this.world.getValue(transformer);
        if (value == null) {
            value = this.dimension.getValue(transformer);
        }
        if (value == null) {
            value = this.global.getValue(transformer, defSupplier);
        }
        return value;
    }

    @Override
    public <T> List<T> getList(Function<Object, T> transformer) {
        List<T> value = this.world.getList(transformer);
        if (value == null) {
            value = this.dimension.getList(transformer);
        }
        if (value == null) {
            value = this.global.getList(transformer);
        }
        return value;
    }

    @Override
    public <T> List<T> getList(Function<Object, T> transformer, List<T> def) {
        List<T> value = this.world.getList(transformer);
        if (value == null) {
            value = this.dimension.getList(transformer);
        }
        if (value == null) {
            value = this.global.getList(transformer, def);
        }
        return value;
    }

    @Override
    public <T> List<T> getList(Function<Object, T> transformer, Supplier<List<T>> defSupplier) {
        List<T> value = this.world.getList(transformer);
        if (value == null) {
            value = this.dimension.getList(transformer);
        }
        if (value == null) {
            value = this.global.getList(transformer, defSupplier);
        }
        return value;
    }

    @Override
    public <T> List<T> getList(TypeToken<T> type, List<T> def) throws ObjectMappingException {
        List<T> value = this.world.getList(type);
        if (value == null) {
            value = this.dimension.getList(type);
        }
        if (value == null) {
            value = this.global.getList(type, def);
        }
        return value;
    }

    @Override
    public <T> List<T> getList(TypeToken<T> type, Supplier<List<T>> defSupplier) throws ObjectMappingException {
        List<T> value = this.world.getList(type);
        if (value == null) {
            value = this.dimension.getList(type);
        }
        if (value == null) {
            value = this.global.getList(type, defSupplier);
        }
        return value;
    }

    @Override
    public Optional<String> getComment() {
        return Optional.ofNullable(this.world.getComment().orElse(this.dimension.getComment().orElse(this.global.getComment().orElse(null))));
    }

    @Override
    public CommentedConfigurationNode setComment(String comment) {
        return this.global.setComment(comment);
    }

    @Override
    public Object getKey() {
        return this.world.getKey();
    }

    @Override
    public Object[] getPath() {
        return this.world.getPath();
    }

    @Override
    public CommentedConfigurationNode getParent() {
        return this.world.getParent();
    }

    @Override
    public ConfigurationOptions getOptions() {
        return this.global.getOptions();
    }

    @Override
    public List<? extends CommentedConfigurationNode> getChildrenList() {
        return this.global.getChildrenList();
    }

    @Override
    public Map<Object, ? extends CommentedConfigurationNode> getChildrenMap() {
        return this.global.getChildrenMap();
    }

    @Override
    public boolean removeChild(Object key) {
        return this.global.removeChild(key) && this.dimension.removeChild(key) && this.world.removeChild(key);
    }

    @Override
    public CommentedConfigurationNode setValue(Object val) {
        if (val == null) {
            this.world.setValue(null);
            this.dimension.setValue(null);
            this.global.setValue(null);
        } else {
            if (!this.world.isVirtual()) {
                this.world.setValue(val);
            } else if (!this.dimension.isVirtual()) {
                this.dimension.setValue(val);
            } else {
                this.global.setValue(val);
            }
        }
        return this;
    }

    @Override
    public <T> T getValue(TypeToken<T> type, T def) throws ObjectMappingException {
        T value = this.world.getValue(type);
        if (value == null) {
            value = this.dimension.getValue(type);
        }
        if (value == null) {
            value = this.global.getValue(type, def);
        }
        return value;
    }

    @Override
    public <T> T getValue(TypeToken<T> type, Supplier<T> defSupplier) throws ObjectMappingException {
        T value = this.world.getValue(type);
        if (value == null) {
            value = this.dimension.getValue(type);
        }
        if (value == null) {
            value = this.global.getValue(type, defSupplier);
        }
        return value;
    }

    // Mirrored from Configurate (remove once default version is released)
    @Override
    @SuppressWarnings("rawtypes")
    public <T> ConfigurationNode setValue(TypeToken<T> type, T value) throws ObjectMappingException {
        if (value == null) {
            setValue(null);
            return this;
        }
        TypeSerializer serial = getOptions().getSerializers().get(type);
        if (serial != null) {
            serial.serialize(type, value, this);
        } else if (getOptions().acceptsType(value.getClass())) {
            setValue(value); // Just write if no applicable serializer exists?
        } else {
            throw new ObjectMappingException("No serializer available for type " + type);
        }
        return this;
    }

    @Override
    public CommentedConfigurationNode mergeValuesFrom(ConfigurationNode other) {
        if (!this.world.isVirtual()) {
            this.world.mergeValuesFrom(other);
        } else if (!this.dimension.isVirtual()) {
            this.dimension.mergeValuesFrom(other);
        } else {
            this.global.mergeValuesFrom(other);
        }
        return this;
    }

    @Override
    public boolean hasListChildren() {
        return this.global.hasListChildren() || this.dimension.hasListChildren() || this.world.hasListChildren();
    }

    @Override
    public boolean hasMapChildren() {
        return this.global.hasMapChildren() || this.dimension.hasMapChildren() || this.world.hasMapChildren();
    }

    @Override
    public CommentedConfigurationNode getAppendedNode() {
        if (!this.world.isVirtual()) {
            return getNode(this.world.getAppendedNode().getKey());
        } else if (!this.dimension.isVirtual()) {
            return getNode(this.dimension.getAppendedNode().getKey());
        } else {
            return getNode(this.global.getAppendedNode().getKey());
        }
    }

    public CommentedConfigurationNode world() {
        return this.world;
    }

    public CommentedConfigurationNode dimension() {
        return this.dimension;
    }

    public CommentedConfigurationNode global() {
        return this.global;
    }

    @Override
    public InheritableNode getNode(Object... path) {
        return new InheritableNode(this.world.getNode(path), this.dimension.getNode(path), this.global.getNode(path));
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

}
