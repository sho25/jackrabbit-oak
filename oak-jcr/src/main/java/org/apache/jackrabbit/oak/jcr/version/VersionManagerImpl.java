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
name|ItemExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
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
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockException
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
name|Version
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
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionHistory
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
name|VersionManager
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
name|commons
operator|.
name|iterator
operator|.
name|NodeIteratorAdapter
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
name|SessionOperation
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
name|util
operator|.
name|TODO
import|;
end_import

begin_class
specifier|public
class|class
name|VersionManagerImpl
implements|implements
name|VersionManager
block|{
specifier|private
specifier|final
name|VersionManagerDelegate
name|versionManagerDelegate
decl_stmt|;
specifier|public
name|VersionManagerImpl
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
name|this
operator|.
name|versionManagerDelegate
operator|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|setActivity
parameter_list|(
name|Node
name|activity
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restoreByLabel
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|versionLabel
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Version
name|version
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|versionName
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|Version
name|version
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|Version
index|[]
name|versions
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|ItemExistsException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
name|VersionException
throws|,
name|LockException
throws|,
name|InvalidItemStateException
throws|,
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeActivity
parameter_list|(
name|Node
name|activityNode
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|merge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|srcWorkspace
parameter_list|,
name|boolean
name|bestEffort
parameter_list|,
name|boolean
name|isShallow
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
name|NodeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|merge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|srcWorkspace
parameter_list|,
name|boolean
name|bestEffort
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
name|NodeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|merge
parameter_list|(
name|Node
name|activityNode
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
name|NodeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCheckedOut
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|versionManagerDelegate
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
return|return
name|versionManagerDelegate
operator|.
name|isCheckedOut
argument_list|(
name|nodeDelegate
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|VersionHistory
name|getVersionHistory
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|versionManagerDelegate
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|VersionHistory
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|VersionHistory
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
return|return
operator|new
name|VersionHistoryImpl
argument_list|(
name|versionManagerDelegate
operator|.
name|getVersionHistory
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|getBaseVersion
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|versionManagerDelegate
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Version
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Version
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
return|return
operator|new
name|VersionImpl
argument_list|(
name|versionManagerDelegate
operator|.
name|getBaseVersion
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getActivity
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneMerge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|createConfiguration
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|createActivity
parameter_list|(
name|String
name|title
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|checkpoint
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkout
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|versionManagerDelegate
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
if|if
condition|(
name|sessionDelegate
operator|.
name|getLockManager
argument_list|()
operator|.
name|isLocked
argument_list|(
name|absPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Node at "
operator|+
name|absPath
operator|+
literal|" is locked"
argument_list|)
throw|;
block|}
name|versionManagerDelegate
operator|.
name|checkout
argument_list|(
name|nodeDelegate
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|checkin
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|versionManagerDelegate
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Version
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Version
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
if|if
condition|(
name|sessionDelegate
operator|.
name|getLockManager
argument_list|()
operator|.
name|isLocked
argument_list|(
name|absPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Node at "
operator|+
name|absPath
operator|+
literal|" is locked"
argument_list|)
throw|;
block|}
return|return
operator|new
name|VersionImpl
argument_list|(
name|versionManagerDelegate
operator|.
name|checkin
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cancelMerge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

