begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
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

begin_import
import|import static
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
operator|.
name|Collection
operator|.
name|NODES
import|;
end_import

begin_comment
comment|/**  * Checkpoints provide details around which revision are to be kept. Currently these  * are stored in NODES collection itself.  */
end_comment

begin_class
class|class
name|Checkpoints
block|{
comment|/**      * Id of checkpoint document. It differs from normal convention of ID used for NodeDocument      * which back JCR Nodes as it is internal to DocumentNodeStore      */
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"/checkpoint"
decl_stmt|;
comment|/**      * Property name to store all checkpoint data. The data is stored as Revision => expiryTime      */
specifier|private
specifier|static
specifier|final
name|String
name|PROP_CHECKPOINT
init|=
literal|"checkpoint"
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|Checkpoints
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
operator|.
name|getDocumentStore
argument_list|()
expr_stmt|;
name|createIfNotExist
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Revision
name|create
parameter_list|(
name|long
name|lifetimeInMillis
parameter_list|)
block|{
name|Revision
name|r
init|=
name|nodeStore
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|long
name|endTime
init|=
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
name|lifetimeInMillis
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|PROP_CHECKPOINT
argument_list|,
name|r
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|endTime
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/**      * Returns the oldest valid checkpoint registered.      *      * @return oldest valid checkpoint registered. Might return null if no valid      * checkpoint found      */
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|getOldestRevisionToKeep
parameter_list|()
block|{
comment|//Get uncached doc
name|NodeDocument
name|cdoc
init|=
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|ID
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|checkpoints
init|=
name|cdoc
operator|.
name|getLocalMap
argument_list|(
name|PROP_CHECKPOINT
argument_list|)
decl_stmt|;
specifier|final
name|long
name|currentTime
init|=
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Revision
name|lastAliveRevision
init|=
literal|null
decl_stmt|;
name|long
name|oldestExpiryTime
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|e
range|:
name|checkpoints
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|long
name|expiryTime
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentTime
operator|>
name|expiryTime
condition|)
block|{
name|op
operator|.
name|removeMapEntry
argument_list|(
name|PROP_CHECKPOINT
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expiryTime
operator|>
name|oldestExpiryTime
condition|)
block|{
name|oldestExpiryTime
operator|=
name|expiryTime
expr_stmt|;
name|lastAliveRevision
operator|=
name|e
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|op
operator|.
name|hasChanges
argument_list|()
condition|)
block|{
name|store
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Purged {} expired checkpoints"
argument_list|,
name|op
operator|.
name|getChanges
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|lastAliveRevision
return|;
block|}
specifier|private
name|void
name|createIfNotExist
parameter_list|()
block|{
if|if
condition|(
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|ID
argument_list|)
operator|==
literal|null
condition|)
block|{
name|UpdateOp
name|updateOp
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|updateOp
operator|.
name|set
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
name|ID
argument_list|)
expr_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|NODES
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

