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
name|plugins
operator|.
name|type
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|commons
operator|.
name|PathUtils
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
name|core
operator|.
name|ReadOnlyTree
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
name|Validator
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
name|ValidatorProvider
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
name|type
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
import|;
end_import

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|ValidatorProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TypeValidatorProvider
implements|implements
name|ValidatorProvider
block|{
annotation|@
name|Override
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|)
block|{
name|ReadOnlyNodeTypeManager
name|ntm
init|=
operator|new
name|ReadOnlyNodeTypeManager
argument_list|()
block|{
specifier|private
specifier|final
name|Tree
name|types
init|=
name|getTypes
argument_list|(
name|after
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|types
return|;
block|}
specifier|private
name|Tree
name|getTypes
parameter_list|(
name|NodeState
name|after
parameter_list|)
block|{
name|Tree
name|tree
init|=
operator|new
name|ReadOnlyTree
argument_list|(
name|after
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
control|)
block|{
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
break|break;
block|}
else|else
block|{
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tree
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TypeValidator
argument_list|(
name|ntm
argument_list|,
operator|new
name|ReadOnlyTree
argument_list|(
name|after
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

