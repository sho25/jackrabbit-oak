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
name|segment
operator|.
name|file
operator|.
name|tooling
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|commons
operator|.
name|PathUtils
operator|.
name|elements
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
name|json
operator|.
name|JsonSerializer
operator|.
name|DEFAULT_FILTER_EXPRESSION
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|util
operator|.
name|Iterator
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|json
operator|.
name|BlobSerializer
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
name|json
operator|.
name|JsonSerializer
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
name|segment
operator|.
name|SegmentNodeState
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|segment
operator|.
name|file
operator|.
name|JournalReader
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

begin_comment
comment|/**  * Utility for tracing a node back through the revision history.  */
end_comment

begin_class
specifier|public
class|class
name|RevisionHistory
block|{
specifier|private
specifier|final
name|ReadOnlyFileStore
name|store
decl_stmt|;
comment|/**      * Create a new instance for a {@link ReadOnlyFileStore} in the given {@code directory}.      *      * @param directory      * @throws IOException      */
specifier|public
name|RevisionHistory
parameter_list|(
annotation|@
name|Nonnull
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|this
operator|.
name|store
operator|=
name|fileStoreBuilder
argument_list|(
name|checkNotNull
argument_list|(
name|directory
argument_list|)
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeState
name|getNode
parameter_list|(
name|SegmentNodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
comment|/**      * Return the history of the node at the given {@code path} according to the passed      * {@code journal}.      *      * @param journal      * @param path      * @return      * @throws IOException      */
specifier|public
name|Iterator
argument_list|<
name|HistoryElement
argument_list|>
name|getHistory
parameter_list|(
annotation|@
name|Nonnull
name|File
name|journal
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
operator|new
name|JournalReader
argument_list|(
name|checkNotNull
argument_list|(
name|journal
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|HistoryElement
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|HistoryElement
name|apply
parameter_list|(
name|String
name|revision
parameter_list|)
block|{
name|store
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|NodeState
name|node
init|=
name|getNode
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|HistoryElement
argument_list|(
name|revision
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Representation of a point in time for a given node.      */
specifier|public
specifier|static
specifier|final
class|class
name|HistoryElement
block|{
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|node
decl_stmt|;
name|HistoryElement
parameter_list|(
name|String
name|revision
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
comment|/**          * Revision of the node          * @return          */
annotation|@
name|Nonnull
specifier|public
name|String
name|getRevision
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
comment|/**          * Node at given revision          * @return          */
annotation|@
name|CheckForNull
specifier|public
name|NodeState
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
comment|/**          * Serialise this element to JSON up to the given {@code depth}.          * @param depth          * @return          */
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|JsonSerializer
name|json
init|=
operator|new
name|JsonSerializer
argument_list|(
name|depth
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|DEFAULT_FILTER_EXPRESSION
argument_list|,
operator|new
name|BlobSerializer
argument_list|()
argument_list|)
decl_stmt|;
name|json
operator|.
name|serialize
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|revision
operator|+
literal|"="
operator|+
name|json
return|;
block|}
comment|/**          * @return  {@code toString(0)}          */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|HistoryElement
name|that
init|=
operator|(
name|HistoryElement
operator|)
name|other
decl_stmt|;
return|return
name|revision
operator|.
name|equals
argument_list|(
name|that
operator|.
name|revision
argument_list|)
operator|&&
operator|(
name|node
operator|==
literal|null
condition|?
name|that
operator|.
name|node
operator|==
literal|null
else|:
name|node
operator|.
name|equals
argument_list|(
name|that
operator|.
name|node
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|revision
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|node
operator|!=
literal|null
condition|?
name|node
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

