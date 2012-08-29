begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|CommitHook
import|;
end_import

begin_comment
comment|/**  * An index is a lookup mechanism. It typically uses a tree to store data. It  * updates the tree whenever a node was changed. The index is updated  * automatically.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Index
extends|extends
name|CommitHook
extends|,
name|Closeable
block|{
comment|/**      * Get the the index definition. This contains the name, type, uniqueness      * and other properties.      *       * @return the index definition      */
annotation|@
name|Nonnull
name|IndexDefinition
name|getDefinition
parameter_list|()
function_decl|;
comment|//    /**
comment|//     * The given node was added or removed.
comment|//     *
comment|//     * @param node the node including (old or new) data
comment|//     * @param add true if added, false if removed
comment|//     */
comment|//    void addOrRemoveNode(NodeImpl node, boolean add);
comment|//
comment|//    /**
comment|//     * The given property was added or removed.
comment|//     *
comment|//     * @param nodePath the path of the node
comment|//     * @param propertyName the property name
comment|//     * @param value the old (when deleting) or new (when adding) value
comment|//     * @param add true if added, false if removed
comment|//     */
comment|//    void addOrRemoveProperty(String nodePath, String propertyName,
comment|//            String value, boolean add);
comment|//
comment|//    /**
comment|//     * Get an iterator over the paths for the given value. For unique
comment|//     * indexes, the iterator will contain at most one element.
comment|//     *
comment|//     * @param value the value, or null to return all indexed rows
comment|//     * @param revision the revision
comment|//     * @return an iterator of the paths (an empty iterator if not found)
comment|//     */
comment|//    Iterator<String> getPaths(String value, String revision);
block|}
end_interface

end_unit

