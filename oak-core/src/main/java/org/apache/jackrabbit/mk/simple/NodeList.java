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
name|mk
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

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
name|mk
operator|.
name|json
operator|.
name|JsopWriter
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
name|simple
operator|.
name|NodeImpl
operator|.
name|ChildVisitor
import|;
end_import

begin_comment
comment|/**  * A list of child nodes.  */
end_comment

begin_interface
interface|interface
name|NodeList
block|{
comment|/**      * Get the number of (direct) child nodes.      *      * @return the number of child nodes      */
name|long
name|size
parameter_list|()
function_decl|;
comment|/**      * Check if the given child node already exists in this list.      *      * @param name the child node name      * @return true if it exists      */
name|boolean
name|containsKey
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Get the node id of the given child node. The child node must exist.      *      * @param name the child node name      * @return the node id      */
name|NodeId
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Add a new child node.      *      * @param name the node name      * @param x the node id      */
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeId
name|x
parameter_list|)
function_decl|;
comment|/**      * Replace an existing child node (keep the position).      *      * @param name the node name      * @param x the node id      */
name|void
name|replace
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeId
name|x
parameter_list|)
function_decl|;
comment|/**      * Get the child node name at this position. If the index is larger than the      * number of child nodes, then null is returned.      *      * @param pos the index      * @return the node name or null      */
name|String
name|getName
parameter_list|(
name|long
name|pos
parameter_list|)
function_decl|;
comment|/**      * Get an iterator over the child node names.      *      * @param offset the offset      * @param maxCount the maximum number of returned names      * @return the iterator      */
name|Iterator
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
name|long
name|offset
parameter_list|,
name|int
name|maxCount
parameter_list|)
function_decl|;
comment|/**      * Remove a child node. The node must exist.      *      * @param name the child node name      * @return the old node id      */
name|NodeId
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Clone the child node list.      *      * @param map the node map      * @param revId the revision      * @return a copy      */
name|NodeList
name|createClone
parameter_list|(
name|NodeMap
name|map
parameter_list|,
name|long
name|revId
parameter_list|)
function_decl|;
comment|/**      * Visit all child nodes.      *      * @param v the visitor      */
name|void
name|visit
parameter_list|(
name|ChildVisitor
name|v
parameter_list|)
function_decl|;
comment|/**      * Write the child node list into a jsop writer.      *      * @param json the jsop writer      * @param map the node map      */
name|void
name|append
parameter_list|(
name|JsopWriter
name|json
parameter_list|,
name|NodeMap
name|map
parameter_list|)
function_decl|;
comment|/**      * Estimate the memory used in bytes.      *      * @return the memory used      */
name|int
name|getMemory
parameter_list|()
function_decl|;
comment|/**      * Write the data into the output stream in order to calculate the content      * hash.      *      * @param map the node map      * @param out the output stream      */
name|void
name|updateHash
parameter_list|(
name|NodeMap
name|map
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

