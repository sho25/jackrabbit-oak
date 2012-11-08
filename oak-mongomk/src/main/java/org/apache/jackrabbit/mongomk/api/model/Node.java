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
name|mongomk
operator|.
name|api
operator|.
name|model
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|mk
operator|.
name|model
operator|.
name|NodeDiffHandler
import|;
end_import

begin_comment
comment|/**  * A higher level object representing a node.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Node
block|{
comment|/**      * Returns the descendant node entry (descendant)      *      * @param name Name of the descendant.      * @return Descendant node.      */
name|Node
name|getChildNodeEntry
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      *      * Returns the total number of children of this node.      *      *<p>      *<strong>This is not necessarily equal to the number of children returned by      * {@link #getChildren()} since this {@code Node} might be created with only      * a subset of children.</strong>      *</p>      *      * @return The total number of children.      */
name|int
name|getChildNodeCount
parameter_list|()
function_decl|;
comment|/**      * Returns the children iterator for the supplied offset and count.      *      * @param offset The offset to return the children from.      * @param count The number of children to return.      * @return Iterator with child entries.      */
name|Iterator
argument_list|<
name|Node
argument_list|>
name|getChildNodeEntries
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * Returns the properties this {@code Node} was created with.      *      * @return The properties.      */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Diffs this node with the other node and calls the passed in diff handler.      *      * @param otherNode Other node.      * @param nodeDiffHandler Diff handler.      */
name|void
name|diff
parameter_list|(
name|Node
name|otherNode
parameter_list|,
name|NodeDiffHandler
name|nodeDiffHandler
parameter_list|)
function_decl|;
comment|/**      * Returns the path of this {@code Node}.      *      * @return The path.      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * Returns the revision id of this node if known already, else this will return {@code null}.      * The revision id will be determined only after the commit has been successfully      * performed or the node has been read as part of an existing revision.      *      * @return The revision id of this commit or {@code null}.      */
name|Long
name|getRevisionId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

