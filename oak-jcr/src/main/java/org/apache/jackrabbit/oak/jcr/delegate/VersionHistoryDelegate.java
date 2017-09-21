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
name|jcr
operator|.
name|delegate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|java
operator|.
name|util
operator|.
name|List
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
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|LabelExistsVersionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionException
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
name|JcrConstants
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
name|Tree
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
name|version
operator|.
name|VersionConstants
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
name|JcrConstants
operator|.
name|JCR_BASEVERSION
import|;
end_import

begin_comment
comment|/**  * {@code VersionHistoryDelegate}...  */
end_comment

begin_class
specifier|public
class|class
name|VersionHistoryDelegate
extends|extends
name|NodeDelegate
block|{
name|VersionHistoryDelegate
parameter_list|(
annotation|@
name|Nonnull
name|SessionDelegate
name|sessionDelegate
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|vhTree
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|,
name|checkNotNull
argument_list|(
name|vhTree
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getVersionableIdentifier
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_VERSIONABLEUUID
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
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|getRootVersion
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Tree
name|rootVersion
init|=
name|getTree
argument_list|()
operator|.
name|getChild
argument_list|(
name|VersionConstants
operator|.
name|JCR_ROOTVERSION
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rootVersion
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Inconsistent version storage. "
operator|+
literal|"VersionHistory does not have a root version"
argument_list|)
throw|;
block|}
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|rootVersion
argument_list|)
return|;
block|}
comment|/**      * Gets the version with the given name.      *      * @param versionName a version name.      * @return the version delegate.      * @throws VersionException if there is no version with the given name.      * @throws RepositoryException if another error occurs.      */
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|getVersion
parameter_list|(
annotation|@
name|Nonnull
name|String
name|versionName
parameter_list|)
throws|throws
name|VersionException
throws|,
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|versionName
argument_list|)
expr_stmt|;
name|Tree
name|version
init|=
name|getTree
argument_list|()
operator|.
name|getChild
argument_list|(
name|versionName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|version
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"No such Version: "
operator|+
name|versionName
argument_list|)
throw|;
block|}
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|version
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|getVersionByLabel
parameter_list|(
annotation|@
name|Nonnull
name|String
name|label
parameter_list|)
throws|throws
name|VersionException
throws|,
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|Tree
name|versionLabels
init|=
name|getVersionLabelsTree
argument_list|()
decl_stmt|;
name|PropertyState
name|p
init|=
name|versionLabels
operator|.
name|getProperty
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Unknown label: "
operator|+
name|label
argument_list|)
throw|;
block|}
name|String
name|id
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|REFERENCE
argument_list|)
decl_stmt|;
name|Tree
name|version
init|=
name|sessionDelegate
operator|.
name|getIdManager
argument_list|()
operator|.
name|getTree
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
operator|||
operator|!
name|version
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Invalid label: "
operator|+
name|label
operator|+
literal|'('
operator|+
name|id
operator|+
literal|')'
argument_list|)
throw|;
block|}
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|version
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getVersionLabels
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Tree
name|versionLabels
init|=
name|getVersionLabelsTree
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|labels
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|versionLabels
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|REFERENCE
condition|)
block|{
name|labels
operator|.
name|add
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|labels
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getVersionLabels
parameter_list|(
annotation|@
name|Nonnull
name|String
name|identifier
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|Tree
name|versionLabels
init|=
name|getVersionLabelsTree
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|labels
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|versionLabels
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|REFERENCE
operator|&&
name|identifier
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
condition|)
block|{
name|labels
operator|.
name|add
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|labels
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Iterator
argument_list|<
name|VersionDelegate
argument_list|>
name|getAllVersions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|NodeDelegate
argument_list|>
name|versions
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|NodeDelegate
argument_list|>
name|it
init|=
name|getChildren
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeDelegate
name|n
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|primaryType
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|primaryType
operator|.
name|equals
argument_list|(
name|VersionConstants
operator|.
name|NT_VERSION
argument_list|)
condition|)
block|{
name|versions
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
comment|// best-effort sort by created time stamp, see JCR 2.0, 15.1.1.2
name|Collections
operator|.
name|sort
argument_list|(
name|versions
argument_list|,
operator|new
name|Comparator
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NodeDelegate
name|n1
parameter_list|,
name|NodeDelegate
name|n2
parameter_list|)
block|{
try|try
block|{
name|PropertyDelegate
name|c1
init|=
name|n1
operator|.
name|getPropertyOrNull
argument_list|(
name|JcrConstants
operator|.
name|JCR_CREATED
argument_list|)
decl_stmt|;
name|PropertyDelegate
name|c2
init|=
name|n2
operator|.
name|getPropertyOrNull
argument_list|(
name|JcrConstants
operator|.
name|JCR_CREATED
argument_list|)
decl_stmt|;
if|if
condition|(
name|c1
operator|!=
literal|null
operator|&&
name|c2
operator|!=
literal|null
condition|)
block|{
return|return
name|c1
operator|.
name|getDate
argument_list|()
operator|.
name|compareTo
argument_list|(
name|c2
operator|.
name|getDate
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|c1
operator|!=
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|c2
operator|!=
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|ex
parameter_list|)
block|{
comment|// best effort
return|return
literal|0
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Tree
name|thisTree
init|=
name|getTree
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|versions
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeDelegate
argument_list|,
name|VersionDelegate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|VersionDelegate
name|apply
parameter_list|(
name|NodeDelegate
name|nd
parameter_list|)
block|{
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|thisTree
operator|.
name|getChild
argument_list|(
name|nd
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Iterator
argument_list|<
name|VersionDelegate
argument_list|>
name|getAllLinearVersions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|id
init|=
name|getVersionableIdentifier
argument_list|()
decl_stmt|;
name|NodeDelegate
name|versionable
init|=
name|sessionDelegate
operator|.
name|getNodeByIdentifier
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|versionable
operator|==
literal|null
operator|||
name|versionable
operator|.
name|getPropertyOrNull
argument_list|(
name|JCR_BASEVERSION
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
name|Iterators
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
name|Deque
argument_list|<
name|VersionDelegate
argument_list|>
name|linearVersions
init|=
operator|new
name|ArrayDeque
argument_list|<
name|VersionDelegate
argument_list|>
argument_list|()
decl_stmt|;
name|VersionManagerDelegate
name|vMgr
init|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|)
decl_stmt|;
name|VersionDelegate
name|version
init|=
name|vMgr
operator|.
name|getVersionByIdentifier
argument_list|(
name|versionable
operator|.
name|getProperty
argument_list|(
name|JCR_BASEVERSION
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|linearVersions
operator|.
name|add
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|version
operator|=
name|version
operator|.
name|getLinearPredecessor
argument_list|()
expr_stmt|;
block|}
return|return
name|linearVersions
operator|.
name|descendingIterator
argument_list|()
return|;
block|}
specifier|public
name|void
name|addVersionLabel
parameter_list|(
annotation|@
name|Nonnull
name|VersionDelegate
name|version
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakVersionLabel
parameter_list|,
name|boolean
name|moveLabel
parameter_list|)
throws|throws
name|LabelExistsVersionException
throws|,
name|VersionException
throws|,
name|RepositoryException
block|{
name|VersionManagerDelegate
name|vMgr
init|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|)
decl_stmt|;
name|vMgr
operator|.
name|addVersionLabel
argument_list|(
name|this
argument_list|,
name|version
argument_list|,
name|oakVersionLabel
argument_list|,
name|moveLabel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeVersionLabel
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakVersionLabel
parameter_list|)
throws|throws
name|VersionException
throws|,
name|RepositoryException
block|{
name|VersionManagerDelegate
name|vMgr
init|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|)
decl_stmt|;
name|vMgr
operator|.
name|removeVersionLabel
argument_list|(
name|this
argument_list|,
name|oakVersionLabel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeVersion
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakVersionName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|VersionManagerDelegate
name|vMgr
init|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|)
decl_stmt|;
name|vMgr
operator|.
name|removeVersion
argument_list|(
name|this
argument_list|,
name|oakVersionName
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------< internal>---------------------------------
comment|/**      * @return the jcr:versionLabels tree or throws a {@code RepositoryException}      *         if it doesn't exist.      * @throws RepositoryException if the jcr:versionLabels child does not      *                             exist.      */
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getVersionLabelsTree
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Tree
name|versionLabels
init|=
name|getTree
argument_list|()
operator|.
name|getChild
argument_list|(
name|VersionConstants
operator|.
name|JCR_VERSIONLABELS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|versionLabels
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Inconsistent version storage. "
operator|+
literal|"VersionHistory does not have jcr:versionLabels child node"
argument_list|)
throw|;
block|}
return|return
name|versionLabels
return|;
block|}
block|}
end_class

end_unit

