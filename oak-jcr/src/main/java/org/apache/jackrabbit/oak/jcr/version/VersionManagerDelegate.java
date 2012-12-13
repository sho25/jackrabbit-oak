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
name|version
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
name|UnsupportedRepositoryOperationException
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
name|TreeLocation
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
name|jcr
operator|.
name|NodeDelegate
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
name|jcr
operator|.
name|SessionDelegate
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

begin_comment
comment|/**  *<code>VersionManagerDelegate</code>...  */
end_comment

begin_class
specifier|public
class|class
name|VersionManagerDelegate
block|{
comment|/**      * TODO: this assumes the version store is in the same workspace.      */
specifier|private
specifier|static
specifier|final
name|String
name|VERSION_STORAGE_PATH
init|=
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_VERSIONSTORAGE
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteVersionManager
name|versionManager
decl_stmt|;
specifier|static
name|VersionManagerDelegate
name|create
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
name|TreeLocation
name|location
init|=
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocation
argument_list|(
name|VERSION_STORAGE_PATH
argument_list|)
decl_stmt|;
return|return
operator|new
name|VersionManagerDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|location
argument_list|)
return|;
block|}
specifier|private
name|VersionManagerDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|TreeLocation
name|versionStorageLocation
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|versionManager
operator|=
operator|new
name|ReadWriteVersionManager
argument_list|(
name|versionStorageLocation
argument_list|,
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|VersionManagerDelegate
operator|.
name|this
operator|.
name|sessionDelegate
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
block|{
return|return
name|sessionDelegate
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|checkin
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|versionManager
operator|.
name|checkin
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|checkout
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|versionManager
operator|.
name|checkout
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCheckedOut
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|versionManager
operator|.
name|isCheckedOut
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionHistoryDelegate
name|getVersionHistory
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|vh
init|=
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|vh
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Node does not"
operator|+
literal|" have a version history: "
operator|+
name|nodeDelegate
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|VersionHistoryDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|vh
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|getBaseVersion
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|v
init|=
name|versionManager
operator|.
name|getBaseVersion
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Node does not"
operator|+
literal|" have a base version: "
operator|+
name|nodeDelegate
operator|.
name|getPath
argument_list|()
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
name|v
argument_list|)
return|;
block|}
comment|//----------------------------< internal>----------------------------------
comment|/**      * Returns the underlying tree.      *      * @param nodeDelegate the node delegate.      * @return the underlying tree.      * @throws InvalidItemStateException if the location points to a stale item.      */
annotation|@
name|Nonnull
specifier|private
specifier|static
name|Tree
name|getTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|t
init|=
name|checkNotNull
argument_list|(
name|nodeDelegate
argument_list|)
operator|.
name|getLocation
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Node does not exist: "
operator|+
name|nodeDelegate
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

