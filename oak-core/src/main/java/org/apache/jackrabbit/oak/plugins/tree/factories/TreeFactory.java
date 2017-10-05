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
name|tree
operator|.
name|factories
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
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|ImmutableTree
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
name|tree
operator|.
name|impl
operator|.
name|NodeBuilderTree
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

begin_comment
comment|/**  * Factory to obtain {@code Tree} objects from {@code NodeState}s  * and {@code NodeBuilder}s.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TreeFactory
block|{
specifier|private
name|TreeFactory
parameter_list|()
block|{}
specifier|public
specifier|static
name|Tree
name|createTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
operator|new
name|NodeBuilderTree
argument_list|(
literal|""
argument_list|,
name|builder
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Tree
name|createReadOnlyTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
operator|new
name|ImmutableTree
argument_list|(
name|rootState
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Tree
name|createReadOnlyTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|readOnlyParent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|childName
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|childState
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|readOnlyParent
operator|instanceof
name|ImmutableTree
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutableTree
argument_list|(
operator|(
name|ImmutableTree
operator|)
name|readOnlyParent
argument_list|,
name|childName
argument_list|,
name|childState
argument_list|)
return|;
block|}
block|}
end_class

end_unit
