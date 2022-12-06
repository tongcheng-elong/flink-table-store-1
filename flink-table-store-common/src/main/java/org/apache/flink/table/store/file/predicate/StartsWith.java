/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store.file.predicate;

import org.apache.flink.table.data.binary.BinaryStringData;
import org.apache.flink.table.store.format.FieldStats;
import org.apache.flink.table.types.logical.LogicalType;

import java.util.List;
import java.util.Optional;

/**
 * A {@link NullFalseLeafBinaryFunction} to evaluate {@code filter like 'abc%' or filter like
 * 'abc_'}.
 */
public class StartsWith extends NullFalseLeafBinaryFunction {

    public static final StartsWith INSTANCE = new StartsWith();

    private StartsWith() {}

    @Override
    public boolean test(LogicalType type, Object field, Object patternLiteral) {
        BinaryStringData fieldString = (BinaryStringData) field;
        return fieldString.startsWith((BinaryStringData) patternLiteral);
    }

    @Override
    public boolean test(
            LogicalType type, long rowCount, FieldStats fieldStats, Object patternLiteral) {
        BinaryStringData min = (BinaryStringData) fieldStats.minValue();
        BinaryStringData max = (BinaryStringData) fieldStats.maxValue();
        BinaryStringData pattern = (BinaryStringData) patternLiteral;
        return (min.startsWith(pattern) || min.compareTo(pattern) <= 0)
                && (max.startsWith(pattern) || max.compareTo(pattern) >= 0);
    }

    @Override
    public Optional<LeafFunction> negate() {
        return Optional.empty();
    }

    @Override
    public <T> T visit(FunctionVisitor<T> visitor, FieldRef fieldRef, List<Object> literals) {
        return visitor.visitStartsWith(fieldRef, literals.get(0));
    }
}
