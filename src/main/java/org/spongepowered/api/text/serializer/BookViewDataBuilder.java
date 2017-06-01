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
package org.spongepowered.api.text.serializer;

import static org.spongepowered.api.data.Queries.TEXT_AUTHOR;
import static org.spongepowered.api.data.Queries.TEXT_PAGE_LIST;
import static org.spongepowered.api.data.Queries.TEXT_TITLE;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.data.DataMap;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * An implementation of {@link AbstractDataBuilder} and {@link TypeSerializer} for {@link BookView}.
 */
public class BookViewDataBuilder extends AbstractDataBuilder<BookView> implements TypeSerializer<BookView> {

    private static final String NODE_AUTHOR = "author";
    private static final String NODE_TITLE = "title";
    private static final String NODE_PAGES = "pages";

    private static final TypeToken<Text> TOKEN_TEXT = TypeToken.of(Text.class);
    private static final TypeToken<List<Text>> TOKEN_TEXT_LIST = new TypeToken<List<Text>>() {
        private static final long serialVersionUID = 1;
    };

    /**
     * Constructs a new {@link BookViewDataBuilder} to build
     * {@link BookView}s.
     */
    public BookViewDataBuilder() {
        super(BookView.class, 1);
    }

    @Override
    protected Optional<BookView> buildContent(DataMap container) throws InvalidDataException {
        BookView.Builder builder = BookView.builder();

        container.getObject(TEXT_TITLE, Text.class).ifPresent(builder::title);
        container.getObject(TEXT_AUTHOR, Text.class).ifPresent(builder::author);
        container.getList(TEXT_PAGE_LIST).ifPresent(pageList -> {
            // TODO: better way to interate over DataList
            for (int i = 0; i < pageList.size(); i++) {
                pageList.getObject(i, Text.class).ifPresent(builder::addPage);
            }
        });

        return Optional.of(builder.build());
    }

    @Override
    public BookView deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        // TODO: Replace with generic DataMap <-> ConfigurationNode translator to void duplicated code
        BookView.Builder builder = BookView.builder();
        builder.author(value.getNode(NODE_AUTHOR).getValue(TOKEN_TEXT));
        builder.title(value.getNode(NODE_TITLE).getValue(TOKEN_TEXT));
        builder.addPages(value.getNode(NODE_PAGES).getValue(TOKEN_TEXT_LIST));
        return builder.build();
    }

    @Override
    public void serialize(TypeToken<?> type, BookView bookView, ConfigurationNode value) throws ObjectMappingException {
        // TODO: Replace with generic DataMap <-> ConfigurationNode translator to void duplicated code
        value.getNode(NODE_AUTHOR).setValue(TOKEN_TEXT, bookView.getAuthor());
        value.getNode(NODE_TITLE).setValue(TOKEN_TEXT, bookView.getTitle());
        value.getNode(NODE_PAGES).setValue(TOKEN_TEXT_LIST, bookView.getPages());
    }
}
