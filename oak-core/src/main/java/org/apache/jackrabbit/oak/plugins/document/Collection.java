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
name|document
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * The collection types.  *  * @param<T> the document type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Collection
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
block|{
comment|/**      * The 'nodes' collection. It contains all the node data, with one document      * per node, and the path as the primary key. Each document possibly      * contains multiple revisions.      *<p>      * Key: the path, value: the node data (possibly multiple revisions)      *<p>      * Old revisions are removed after some time, either by the process that      * removed or updated the node, lazily when reading, or in a background      * process.      */
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|NodeDocument
argument_list|>
name|NODES
init|=
operator|new
name|Collection
argument_list|<
name|NodeDocument
argument_list|>
argument_list|(
literal|"nodes"
argument_list|)
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeDocument
name|newDocument
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|NodeDocument
argument_list|(
name|store
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * The 'clusterNodes' collection contains the list of currently running      * cluster nodes. The key is the clusterNodeId (0, 1, 2,...).      */
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|CLUSTER_NODES
init|=
operator|new
name|Collection
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
argument_list|(
literal|"clusterNodes"
argument_list|)
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|ClusterNodeInfoDocument
name|newDocument
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|ClusterNodeInfoDocument
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Collection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @param store the document store.      * @return a new document for this collection.      */
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|T
name|newDocument
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
function_decl|;
block|}
end_class

end_unit

