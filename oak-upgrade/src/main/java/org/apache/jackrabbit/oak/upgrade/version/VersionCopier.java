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
name|upgrade
operator|.
name|version
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CREATED
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
name|JcrConstants
operator|.
name|JCR_SYSTEM
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
name|JcrConstants
operator|.
name|JCR_VERSIONSTORAGE
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
name|JcrConstants
operator|.
name|NT_VERSION
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|oak
operator|.
name|api
operator|.
name|PropertyState
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
name|plugins
operator|.
name|index
operator|.
name|IndexUtils
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
name|nodetype
operator|.
name|TypePredicate
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
name|ChildNodeEntry
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
name|upgrade
operator|.
name|DescendantsIterator
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
name|upgrade
operator|.
name|nodestate
operator|.
name|NodeStateCopier
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
name|util
operator|.
name|ISO8601
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
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
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
name|upgrade
operator|.
name|version
operator|.
name|VersionHistoryUtil
operator|.
name|getVersionHistoryPath
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
name|upgrade
operator|.
name|version
operator|.
name|VersionHistoryUtil
operator|.
name|getVersionHistoryNodeState
import|;
end_import

begin_comment
comment|/**  * This class allows to copy the version history, optionally filtering it with a  * given date.  */
end_comment

begin_class
specifier|public
class|class
name|VersionCopier
block|{
specifier|private
specifier|final
name|TypePredicate
name|isVersion
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|sourceRoot
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|targetRoot
decl_stmt|;
specifier|public
name|VersionCopier
parameter_list|(
name|NodeState
name|sourceRoot
parameter_list|,
name|NodeBuilder
name|targetRoot
parameter_list|)
block|{
name|this
operator|.
name|isVersion
operator|=
operator|new
name|TypePredicate
argument_list|(
name|targetRoot
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NT_VERSION
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceRoot
operator|=
name|sourceRoot
expr_stmt|;
name|this
operator|.
name|targetRoot
operator|=
name|targetRoot
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|copyVersionStorage
parameter_list|(
name|NodeState
name|sourceRoot
parameter_list|,
name|NodeBuilder
name|targetRoot
parameter_list|,
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
specifier|final
name|NodeState
name|versionStorage
init|=
name|sourceRoot
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|NodeState
argument_list|>
name|versionStorageIterator
init|=
operator|new
name|DescendantsIterator
argument_list|(
name|versionStorage
argument_list|,
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|VersionCopier
name|versionCopier
init|=
operator|new
name|VersionCopier
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|)
decl_stmt|;
name|boolean
name|versionsCopied
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|versionStorageIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|NodeState
name|versionHistoryBucket
init|=
name|versionStorageIterator
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|versionHistory
range|:
name|versionHistoryBucket
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|versionsCopied
operator||=
name|versionCopier
operator|.
name|doCopyVersionHistory
argument_list|(
name|versionHistory
argument_list|,
name|config
operator|.
name|getOrphanedMinDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|versionsCopied
condition|)
block|{
name|versionCopier
operator|.
name|markUuidToReindex
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Copy history filtering versions using passed date and returns {@code      * true} if the history has been copied.      *       * @param versionableUuid      *            Name of the version history node      * @param minDate      *            Only versions older than this date will be copied      * @return {@code true} if at least one version has been copied      */
specifier|public
name|boolean
name|copyVersionHistory
parameter_list|(
name|String
name|versionableUuid
parameter_list|,
name|Calendar
name|minDate
parameter_list|)
block|{
name|boolean
name|copied
init|=
name|doCopyVersionHistory
argument_list|(
name|versionableUuid
argument_list|,
name|minDate
argument_list|)
decl_stmt|;
if|if
condition|(
name|copied
condition|)
block|{
name|markUuidToReindex
argument_list|()
expr_stmt|;
block|}
return|return
name|copied
return|;
block|}
specifier|private
name|boolean
name|doCopyVersionHistory
parameter_list|(
name|String
name|versionableUuid
parameter_list|,
name|Calendar
name|minDate
parameter_list|)
block|{
specifier|final
name|String
name|versionHistoryPath
init|=
name|getVersionHistoryPath
argument_list|(
name|versionableUuid
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|sourceVersionHistory
init|=
name|getVersionHistoryNodeState
argument_list|(
name|sourceRoot
argument_list|,
name|versionableUuid
argument_list|)
decl_stmt|;
specifier|final
name|Calendar
name|lastModified
init|=
name|getVersionHistoryLastModified
argument_list|(
name|sourceVersionHistory
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceVersionHistory
operator|.
name|exists
argument_list|()
operator|&&
operator|(
name|lastModified
operator|.
name|after
argument_list|(
name|minDate
argument_list|)
operator|||
name|minDate
operator|.
name|getTimeInMillis
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|NodeStateCopier
operator|.
name|builder
argument_list|()
operator|.
name|include
argument_list|(
name|versionHistoryPath
argument_list|)
operator|.
name|merge
argument_list|(
name|VERSION_STORE_PATH
argument_list|)
operator|.
name|copy
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|Calendar
name|getVersionHistoryLastModified
parameter_list|(
specifier|final
name|NodeState
name|versionHistory
parameter_list|)
block|{
name|Calendar
name|youngest
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|youngest
operator|.
name|setTimeInMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ChildNodeEntry
name|entry
range|:
name|versionHistory
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
specifier|final
name|NodeState
name|version
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isVersion
operator|.
name|apply
argument_list|(
name|version
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|version
operator|.
name|hasProperty
argument_list|(
name|JCR_CREATED
argument_list|)
condition|)
block|{
specifier|final
name|Calendar
name|created
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|version
operator|.
name|getProperty
argument_list|(
name|JCR_CREATED
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|created
operator|.
name|after
argument_list|(
name|youngest
argument_list|)
condition|)
block|{
name|youngest
operator|=
name|created
expr_stmt|;
block|}
block|}
block|}
return|return
name|youngest
return|;
block|}
specifier|private
name|void
name|markUuidToReindex
parameter_list|()
block|{
specifier|final
name|NodeBuilder
name|uuidIndexDefinition
init|=
name|IndexUtils
operator|.
name|getOrCreateOakIndex
argument_list|(
name|targetRoot
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"uuid"
argument_list|)
decl_stmt|;
specifier|final
name|PropertyState
name|reindex
init|=
name|uuidIndexDefinition
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|reindex
operator|==
literal|null
operator|||
operator|!
name|reindex
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
condition|)
block|{
name|uuidIndexDefinition
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

