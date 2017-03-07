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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|List
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
name|Lists
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
name|document
operator|.
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|checkArgument
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
name|NodeDocument
operator|.
name|SplitDocType
operator|.
name|INTERMEDIATE
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
name|NodeDocument
operator|.
name|SplitDocType
operator|.
name|NONE
import|;
end_import

begin_comment
comment|/**  * Implements a split document cleanup.  */
end_comment

begin_class
specifier|public
class|class
name|SplitDocumentCleanUp
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SplitDocumentCleanUp
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// number of document IDs to collect before removing them in a single call
specifier|private
specifier|static
specifier|final
name|int
name|DELETE_BATCH_SIZE
init|=
literal|100
decl_stmt|;
specifier|protected
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|protected
specifier|final
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|splitDocGarbage
decl_stmt|;
specifier|protected
specifier|final
name|VersionGCStats
name|stats
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|idsToBeDeleted
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|deleteCount
decl_stmt|;
specifier|protected
name|SplitDocumentCleanUp
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|VersionGCStats
name|stats
parameter_list|,
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|splitDocGarbage
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|splitDocGarbage
operator|=
name|splitDocGarbage
expr_stmt|;
block|}
specifier|protected
name|SplitDocumentCleanUp
name|disconnect
parameter_list|()
block|{
for|for
control|(
name|NodeDocument
name|splitDoc
range|:
name|splitDocGarbage
control|)
block|{
name|disconnect
argument_list|(
name|splitDoc
argument_list|)
expr_stmt|;
name|collectIdToBeDeleted
argument_list|(
name|splitDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Collects document IDs for subsequent deletion.      *<p>      * Implementations that override      * {@link SplitDocumentCleanUp#deleteSplitDocuments()} should override this      * method as well.      */
specifier|protected
name|void
name|collectIdToBeDeleted
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|idsToBeDeleted
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|// proceed to delete early if we reach DELETE_BATCH_SIZE
if|if
condition|(
name|idsToBeDeleted
operator|.
name|size
argument_list|()
operator|>=
name|DELETE_BATCH_SIZE
condition|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|NODES
argument_list|,
name|idsToBeDeleted
argument_list|)
expr_stmt|;
name|deleteCount
operator|+=
name|idsToBeDeleted
operator|.
name|size
argument_list|()
expr_stmt|;
name|idsToBeDeleted
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|int
name|deleteSplitDocuments
parameter_list|()
block|{
name|store
operator|.
name|remove
argument_list|(
name|NODES
argument_list|,
name|idsToBeDeleted
argument_list|)
expr_stmt|;
return|return
name|idsToBeDeleted
operator|.
name|size
argument_list|()
operator|+
name|deleteCount
return|;
block|}
specifier|private
name|void
name|disconnect
parameter_list|(
name|NodeDocument
name|splitDoc
parameter_list|)
block|{
name|String
name|splitId
init|=
name|splitDoc
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|mainId
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|splitDoc
operator|.
name|getMainPath
argument_list|()
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|mainId
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Main document {} already removed. Split document is {}"
argument_list|,
name|mainId
argument_list|,
name|splitId
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|splitDocPath
init|=
name|splitDoc
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|slashIdx
init|=
name|splitDocPath
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|int
name|height
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|splitDocPath
operator|.
name|substring
argument_list|(
name|slashIdx
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Revision
name|rev
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|splitDocPath
operator|.
name|substring
argument_list|(
name|splitDocPath
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|,
name|slashIdx
operator|-
literal|1
argument_list|)
operator|+
literal|1
argument_list|,
name|slashIdx
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|=
name|doc
operator|.
name|findPrevReferencingDoc
argument_list|(
name|rev
argument_list|,
name|height
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Split document {} for path {} not referenced anymore. Main document is {}"
argument_list|,
name|splitId
argument_list|,
name|splitDocPath
argument_list|,
name|mainId
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// remove reference
if|if
condition|(
name|doc
operator|.
name|getSplitDocType
argument_list|()
operator|==
name|INTERMEDIATE
condition|)
block|{
name|disconnectFromIntermediate
argument_list|(
name|doc
argument_list|,
name|rev
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|markStaleOnMain
argument_list|(
name|doc
argument_list|,
name|rev
argument_list|,
name|height
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|disconnectFromIntermediate
parameter_list|(
name|NodeDocument
name|splitDoc
parameter_list|,
name|Revision
name|rev
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|splitDoc
operator|.
name|getSplitDocType
argument_list|()
operator|==
name|INTERMEDIATE
argument_list|,
literal|"Illegal type: %s"
argument_list|,
name|splitDoc
operator|.
name|getSplitDocType
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
name|splitDoc
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeDocument
operator|.
name|removePrevious
argument_list|(
name|update
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|NodeDocument
name|old
init|=
name|store
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|old
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|containsKey
argument_list|(
name|rev
argument_list|)
condition|)
block|{
comment|// this was the last reference on an intermediate split doc
name|disconnect
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|store
operator|.
name|remove
argument_list|(
name|NODES
argument_list|,
name|old
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|intermediateSplitDocGCCount
operator|++
expr_stmt|;
block|}
block|}
specifier|final
name|void
name|markStaleOnMain
parameter_list|(
name|NodeDocument
name|main
parameter_list|,
name|Revision
name|rev
parameter_list|,
name|int
name|height
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|main
operator|.
name|getSplitDocType
argument_list|()
operator|==
name|NONE
argument_list|,
literal|"Illegal type: %s"
argument_list|,
name|main
operator|.
name|getSplitDocType
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
name|main
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeDocument
operator|.
name|setStalePrevious
argument_list|(
name|update
argument_list|,
name|rev
argument_list|,
name|height
argument_list|)
expr_stmt|;
name|store
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|Utils
operator|.
name|closeIfCloseable
argument_list|(
name|splitDocGarbage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

