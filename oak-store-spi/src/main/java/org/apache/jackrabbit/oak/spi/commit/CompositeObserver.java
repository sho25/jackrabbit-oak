begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|commit
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
name|checkState
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
name|collect
operator|.
name|Sets
operator|.
name|newIdentityHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Composite observer that delegates all content changes to the set of  * currently registered component observers.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeObserver
implements|implements
name|Observer
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|Observer
argument_list|>
name|observers
init|=
name|newIdentityHashSet
argument_list|()
decl_stmt|;
specifier|public
specifier|synchronized
name|void
name|addObserver
parameter_list|(
annotation|@
name|NotNull
name|Observer
name|observer
parameter_list|)
block|{
name|checkState
argument_list|(
name|observers
operator|.
name|add
argument_list|(
name|checkNotNull
argument_list|(
name|observer
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|removeObserver
parameter_list|(
annotation|@
name|NotNull
name|Observer
name|observer
parameter_list|)
block|{
name|checkState
argument_list|(
name|observers
operator|.
name|remove
argument_list|(
name|checkNotNull
argument_list|(
name|observer
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Observer>--
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|contentChanged
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
for|for
control|(
name|Observer
name|observer
range|:
name|observers
control|)
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
name|observers
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

