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
name|Calendar
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
name|Node
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
name|Value
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
name|VersionHistory
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
name|NodeImpl
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
name|PropertyDelegate
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

begin_class
class|class
name|VersionImpl
extends|extends
name|NodeImpl
argument_list|<
name|VersionDelegate
argument_list|>
implements|implements
name|Version
block|{
specifier|public
name|VersionImpl
parameter_list|(
name|VersionDelegate
name|dlg
parameter_list|)
block|{
name|super
argument_list|(
name|dlg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|VersionHistory
name|getContainingHistory
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|VersionHistoryImpl
argument_list|(
name|getVersionManagerDelegate
argument_list|()
operator|.
name|getVersionHistory
argument_list|(
name|dlg
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Calendar
name|getCreated
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getPropertyOrThrow
argument_list|(
name|JcrConstants
operator|.
name|JCR_CREATED
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|getDate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|getLinearPredecessor
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
name|Version
name|getLinearSuccessor
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
name|Version
index|[]
name|getPredecessors
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PropertyDelegate
name|p
init|=
name|getPropertyOrThrow
argument_list|(
name|VersionConstants
operator|.
name|JCR_PREDECESSORS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|predecessors
init|=
operator|new
name|ArrayList
argument_list|<
name|Version
argument_list|>
argument_list|()
decl_stmt|;
name|VersionManagerDelegate
name|vMgr
init|=
name|getVersionManagerDelegate
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|v
range|:
name|p
operator|.
name|getValues
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|v
operator|.
name|getString
argument_list|()
decl_stmt|;
name|predecessors
operator|.
name|add
argument_list|(
operator|new
name|VersionImpl
argument_list|(
name|vMgr
operator|.
name|getVersionByIdentifier
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|predecessors
operator|.
name|toArray
argument_list|(
operator|new
name|Version
index|[
name|predecessors
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Version
index|[]
name|getSuccessors
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PropertyDelegate
name|p
init|=
name|getPropertyOrThrow
argument_list|(
name|VersionConstants
operator|.
name|JCR_SUCCESSORS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|successors
init|=
operator|new
name|ArrayList
argument_list|<
name|Version
argument_list|>
argument_list|()
decl_stmt|;
name|VersionManagerDelegate
name|vMgr
init|=
name|getVersionManagerDelegate
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|v
range|:
name|p
operator|.
name|getValues
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|v
operator|.
name|getString
argument_list|()
decl_stmt|;
name|successors
operator|.
name|add
argument_list|(
operator|new
name|VersionImpl
argument_list|(
name|vMgr
operator|.
name|getVersionByIdentifier
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|successors
operator|.
name|toArray
argument_list|(
operator|new
name|Version
index|[
name|successors
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getFrozenNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|NodeImpl
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|dlg
operator|.
name|getChild
argument_list|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|)
argument_list|)
return|;
block|}
comment|//------------------------------< internal>--------------------------------
annotation|@
name|Nonnull
specifier|private
name|VersionManagerDelegate
name|getVersionManagerDelegate
parameter_list|()
block|{
return|return
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|dlg
operator|.
name|getSessionDelegate
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PropertyDelegate
name|getPropertyOrThrow
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyDelegate
name|p
init|=
name|dlg
operator|.
name|getProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
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
name|RepositoryException
argument_list|(
literal|"Inconsistent version storage. "
operator|+
literal|"Version does not have a "
operator|+
name|name
operator|+
literal|" property."
argument_list|)
throw|;
block|}
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

