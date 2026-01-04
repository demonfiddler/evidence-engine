/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

package io.github.demonfiddler.ee.common.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * An unmodifiable composite list that wraps and is dynamically backed by multiple unique sub-lists. The implementation
 * is not thread-safe and requires external synchronization control if it is to be modified by multiple threads.
 * @param <E> The list element type.
 */
public class CompositeList<E> extends AbstractList<E> {

    /** The lists from which this wrapper is composed. */
    protected final List<List<? extends E>> delegates;

    /**
     * Constructs a new CompositeList.
     */
    public CompositeList() {
        delegates = new ArrayList<List<? extends E>>();
    }

    /**
     * Constructs a new CompositeList.
     * @param initialCapacity The initial capacity.
     */
    public CompositeList(int initialCapacity) {
        delegates = new ArrayList<List<? extends E>>(initialCapacity);
    }

    /**
     * Constructs a new CompositeList.
     * @param lists The sub-lists to wrap.
     */
    public CompositeList(List<? extends List<? extends E>> lists) {
        this.delegates = new ArrayList<List<? extends E>>(lists);
    }

    /**
     * Adds a new sub-list.
     * @param sublist The sub-list to add.
     * @return <code>true</code> if <code>sublist</code> was not already a member.
     * @throws IllegalArgumentException if <code>sublist</code> is already included in this composite list.
     */
    public boolean addSubList(List<? extends E> sublist) {
        return delegates.add(sublist);
    }

    /** {@inheritDoc} Overridden to enforce equality by identity. */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    /**
     * {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E get(int index) {
        for (int i = 0, offset = 0; i < delegates.size(); offset += delegates.get(i).size(), i++) {
            if (index >= offset && index < offset + delegates.get(i).size())
                return delegates.get(i).get(index - offset);
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    /** {@inheritDoc} Overridden to use the system identity hash code. */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public int relativeIndex(int index) {
        for (int i = 0, offset = 0; i < delegates.size(); offset += delegates.get(i).size(), i++) {
            if (index >= offset && index < offset + delegates.get(i).size())
                return index - offset;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    /**
     * Removes an existing sub-list.
     * @param sublist The sub-list to remove.
     * @return <code>true</code> if <code>sublist</code> was removed, <code>false</code> if it was not an existing
     * member.
     */
    public boolean removeSubList(List<? extends E> sublist) {
        return delegates.remove(sublist);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < delegates.size(); i++)
            size += delegates.get(i).size();
        return size;
    }

    /**
     * Returns the sub-list for the specified index.
     * @param index The index.
     * @return The sub-list containing the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size()).
     */
    protected List<? extends E> subListFor(int index) {
        for (int i = 0, offset = 0; i < delegates.size(); offset += delegates.get(i).size(), i++) {
            if (index >= offset && index < offset + delegates.get(i).size())
                return delegates.get(i);
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    /**
     * Returns the sub-list for the specified object.
     * @param object The putative list element.
     * @return The first sub-list containing the specified object or <code>null</code> if <code>object</code> is not
     * contained by any of the sub-lists.
     */
    protected List<? extends E> subListFor(Object object) {
        for (List<? extends E> subList : delegates) {
            if (subList.contains(object))
                return subList;
        }
        return null;
    }

}
