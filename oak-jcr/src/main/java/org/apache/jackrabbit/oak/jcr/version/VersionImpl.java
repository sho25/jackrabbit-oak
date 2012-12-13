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
name|Calendar
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
name|util
operator|.
name|TODO
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
name|Calendar
name|getCreated
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
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
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
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
operator|new
name|Version
index|[
literal|0
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
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
operator|new
name|Version
index|[
literal|0
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
block|}
end_class

end_unit

