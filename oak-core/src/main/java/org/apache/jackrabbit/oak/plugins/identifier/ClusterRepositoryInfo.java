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
name|identifier
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|Type
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
name|CommitInfo
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
name|EmptyHook
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Utility class to manage a unique cluster/repository id for the cluster.  */
end_comment

begin_class
specifier|public
class|class
name|ClusterRepositoryInfo
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ClusterRepositoryInfo
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_CLUSTERID_REPOSITORY_DESCRIPTOR_KEY
init|=
literal|"oak.clusterid"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_CONFIG_NODE
init|=
literal|":clusterConfig"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ID_PROP
init|=
literal|":clusterId"
decl_stmt|;
comment|/**      * Gets the {# CLUSTER_ID_PROP} if available, if it doesn't it       * creates it and returns the newly created one (or if that      * happened concurrently and another cluster instance won,      * return that one)      *<p>      * Note that this method doesn't require synchronization as      * concurrent execution within the VM would be covered      * within NodeStore's merge and result in a conflict for      * one of the two threads - in which case the looser would      * re-read and find the clusterId set.      *       * @param store the NodeStore instance      * @return the persistent clusterId      */
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|getOrCreateId
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
comment|// first try to read an existing clusterId
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|node
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|CLUSTER_CONFIG_NODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|exists
argument_list|()
operator|&&
name|node
operator|.
name|hasProperty
argument_list|(
name|CLUSTER_ID_PROP
argument_list|)
condition|)
block|{
comment|// clusterId is set - this is the normal case
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|CLUSTER_ID_PROP
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
comment|// otherwise either the config node or the property doesn't exist.
comment|// then try to create it - but since this could be executed concurrently
comment|// in a cluster, it might result in a conflict. in that case, re-read
comment|// the node
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// choose a new random clusterId
name|String
name|newRandomClusterId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|CLUSTER_CONFIG_NODE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|CLUSTER_ID_PROP
argument_list|,
name|newRandomClusterId
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// great, we were able to create it, all good.
name|log
operator|.
name|info
argument_list|(
literal|"getOrCreateId: created a new clusterId="
operator|+
name|newRandomClusterId
argument_list|)
expr_stmt|;
return|return
name|newRandomClusterId
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// if we can't commit, then another instance in the cluster
comment|// set the clusterId concurrently. in which case we should now
comment|// see that one
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|node
operator|=
name|root
operator|.
name|getChildNode
argument_list|(
name|CLUSTER_CONFIG_NODE
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|exists
argument_list|()
operator|&&
name|node
operator|.
name|hasProperty
argument_list|(
name|CLUSTER_ID_PROP
argument_list|)
condition|)
block|{
comment|// clusterId is set - this is the normal case
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|CLUSTER_ID_PROP
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
comment|// this should not happen
name|String
name|path
init|=
literal|"/"
operator|+
name|CLUSTER_CONFIG_NODE
operator|+
literal|"/"
operator|+
name|CLUSTER_ID_PROP
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"getOrCreateId: both setting and then reading of "
operator|+
name|path
operator|+
literal|"failed"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Both setting and then reading of "
operator|+
name|path
operator|+
literal|" failed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

