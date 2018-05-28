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
comment|/**  * The root node state of a persisted branch.  */
end_comment

begin_class
class|class
name|DocumentBranchRootNodeState
extends|extends
name|DocumentNodeState
block|{
specifier|private
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStoreBranch
name|branch
decl_stmt|;
name|DocumentBranchRootNodeState
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|DocumentNodeStoreBranch
name|branch
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|RevisionVector
name|rootRevision
parameter_list|,
annotation|@
name|Nullable
name|RevisionVector
name|lastRevision
parameter_list|,
annotation|@
name|Nonnull
name|BundlingContext
name|bundlingContext
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|,
name|path
argument_list|,
name|lastRevision
argument_list|,
name|rootRevision
argument_list|,
literal|false
argument_list|,
name|bundlingContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|branch
operator|=
name|checkNotNull
argument_list|(
name|branch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|DocumentRootBuilder
argument_list|(
name|store
operator|.
name|getRoot
argument_list|(
name|getRootRevision
argument_list|()
argument_list|)
argument_list|,
name|store
argument_list|,
name|branch
argument_list|)
return|;
block|}
block|}
end_class

end_unit
