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
name|cli
operator|.
name|node
package|;
end_package

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
name|Blob
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
name|spi
operator|.
name|commit
operator|.
name|CommitHook
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|TarNodeStore
implements|implements
name|NodeStore
block|{
specifier|private
specifier|final
name|NodeStore
name|ns
decl_stmt|;
specifier|private
specifier|final
name|SuperRootProvider
name|superRootProvider
decl_stmt|;
specifier|public
name|TarNodeStore
parameter_list|(
name|NodeStore
name|ns
parameter_list|,
name|SuperRootProvider
name|superRootProvider
parameter_list|)
block|{
name|this
operator|.
name|ns
operator|=
name|ns
expr_stmt|;
name|this
operator|.
name|superRootProvider
operator|=
name|superRootProvider
expr_stmt|;
block|}
specifier|public
name|void
name|setSuperRoot
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|superRootProvider
operator|.
name|setSuperRoot
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeState
name|getSuperRoot
parameter_list|()
block|{
return|return
name|superRootProvider
operator|.
name|getSuperRoot
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|ns
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|ns
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|commitHook
argument_list|,
name|info
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|ns
operator|.
name|rebase
argument_list|(
name|builder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|ns
operator|.
name|reset
argument_list|(
name|builder
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ns
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|getBlob
parameter_list|(
annotation|@
name|Nonnull
name|String
name|reference
parameter_list|)
block|{
return|return
name|ns
operator|.
name|getBlob
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
return|return
name|ns
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|,
name|properties
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
return|return
name|ns
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|checkpointInfo
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
return|return
name|ns
operator|.
name|checkpointInfo
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
return|return
name|ns
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|release
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
return|return
name|ns
operator|.
name|release
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
interface|interface
name|SuperRootProvider
block|{
name|void
name|setSuperRoot
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
function_decl|;
name|NodeState
name|getSuperRoot
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

