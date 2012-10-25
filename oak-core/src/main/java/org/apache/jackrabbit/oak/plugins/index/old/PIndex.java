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
name|plugins
operator|.
name|index
operator|.
name|old
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|plugins
operator|.
name|index
operator|.
name|old
operator|.
name|mk
operator|.
name|simple
operator|.
name|NodeImpl
import|;
end_import

begin_comment
comment|/**  * An index is a lookup mechanism. It typically uses a tree to store data. It  * updates the tree whenever a node was changed. The index is updated  * automatically.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PIndex
block|{
comment|/**      * Get the unique index name. This is also the name of the index node.      *      * @return the index name      */
name|String
name|getIndexNodeName
parameter_list|()
function_decl|;
comment|/**      * The given node was added or removed.      *      * @param node the node including (old or new) data      * @param add true if added, false if removed      */
name|void
name|addOrRemoveNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|boolean
name|add
parameter_list|)
function_decl|;
comment|/**      * The given property was added or removed.      *      * @param nodePath the path of the node      * @param propertyName the property name      * @param value the old (when deleting) or new (when adding) value      * @param add true if added, false if removed      */
name|void
name|addOrRemoveProperty
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|add
parameter_list|)
function_decl|;
comment|/**      * Get an iterator over the paths for the given value. For unique      * indexes, the iterator will contain at most one element.      *      * @param value the value, or null to return all indexed rows      * @param revision the revision      * @return an iterator of the paths (an empty iterator if not found)      */
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|revision
parameter_list|)
function_decl|;
comment|/**      * Whether each value may only appear once in the index.      *      * @return true if unique      */
name|boolean
name|isUnique
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

