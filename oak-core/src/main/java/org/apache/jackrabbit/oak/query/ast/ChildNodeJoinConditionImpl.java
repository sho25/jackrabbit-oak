begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|query
operator|.
name|ast
package|;
end_package

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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|query
operator|.
name|Filter
import|;
end_import

begin_comment
comment|/**  * The "ischildnode(...)" join condition.  */
end_comment

begin_class
specifier|public
class|class
name|ChildNodeJoinConditionImpl
extends|extends
name|JoinConditionImpl
block|{
specifier|private
specifier|final
name|String
name|childSelectorName
decl_stmt|;
specifier|private
specifier|final
name|String
name|parentSelectorName
decl_stmt|;
specifier|private
name|SelectorImpl
name|childSelector
decl_stmt|;
specifier|private
name|SelectorImpl
name|parentSelector
decl_stmt|;
specifier|public
name|ChildNodeJoinConditionImpl
parameter_list|(
name|String
name|childSelectorName
parameter_list|,
name|String
name|parentSelectorName
parameter_list|)
block|{
name|this
operator|.
name|childSelectorName
operator|=
name|childSelectorName
expr_stmt|;
name|this
operator|.
name|parentSelectorName
operator|=
name|parentSelectorName
expr_stmt|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ischildnode("
operator|+
name|quote
argument_list|(
name|childSelectorName
argument_list|)
operator|+
literal|", "
operator|+
name|quote
argument_list|(
name|parentSelectorName
argument_list|)
operator|+
literal|')'
return|;
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|parentSelector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|parentSelectorName
argument_list|)
expr_stmt|;
name|childSelector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|childSelectorName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
name|String
name|p
init|=
name|parentSelector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
name|String
name|c
init|=
name|childSelector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
comment|// the parent of the root is the root,
comment|// so we need to special case this
return|return
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|c
argument_list|)
operator|&&
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|c
argument_list|)
operator|.
name|equals
argument_list|(
name|p
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|parentSelector
condition|)
block|{
name|String
name|c
init|=
name|childSelector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
operator|&&
name|f
operator|.
name|isPreparing
argument_list|()
operator|&&
name|childSelector
operator|.
name|isPrepared
argument_list|()
condition|)
block|{
comment|// during the prepare phase, if the selector is already
comment|// prepared, then we would know the value
name|c
operator|=
name|KNOWN_PATH
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|f
operator|.
name|restrictPath
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|c
argument_list|)
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|childSelector
condition|)
block|{
name|String
name|p
init|=
name|parentSelector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
operator|&&
name|f
operator|.
name|isPreparing
argument_list|()
operator|&&
name|parentSelector
operator|.
name|isPrepared
argument_list|()
condition|)
block|{
comment|// during the prepare phase, if the selector is already
comment|// prepared, then we would know the value
name|p
operator|=
name|KNOWN_PATH
expr_stmt|;
block|}
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|f
operator|.
name|restrictPath
argument_list|(
name|p
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isParent
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
return|return
name|source
operator|==
name|parentSelector
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canEvaluate
parameter_list|(
name|Set
argument_list|<
name|SourceImpl
argument_list|>
name|available
parameter_list|)
block|{
return|return
name|available
operator|.
name|contains
argument_list|(
name|childSelector
argument_list|)
operator|&&
name|available
operator|.
name|contains
argument_list|(
name|parentSelector
argument_list|)
return|;
block|}
block|}
end_class

end_unit

